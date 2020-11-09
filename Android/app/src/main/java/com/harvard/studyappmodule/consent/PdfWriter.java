/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.consent;

import android.content.Context;
import android.content.res.AssetManager;
import com.harvard.utils.Logger;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType0Font;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfWriter {
  private String pdfOutputDirectory = "";
  private String pdfFileName = "";
  private PDDocument doc = null;
  private PDFont font = PDType1Font.HELVETICA;

  PdfWriter(String pdfOutputDirectory, String pdfFileName) {
    this.pdfOutputDirectory = pdfOutputDirectory;
    if (!this.pdfOutputDirectory.endsWith("/")) {
      this.pdfOutputDirectory += "/";
    }
    if (!pdfFileName.endsWith(".pdf")) {
      pdfFileName = pdfFileName + ".pdf";
    }
    this.pdfFileName = pdfFileName;
  }

  void createPdfFile(Context context) {
    PDFBoxResourceLoader.init(context);
    AssetManager assetManager = context.getAssets();
    doc = new PDDocument();
    try {
      font =
          PDType0Font.load(
              doc,
              assetManager.open("com/tom_roush/pdfbox/resources/ttf/LiberationSans-Regular.ttf"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  boolean addPage(String pageHeader, StringBuffer pageText, String path) {
    boolean ok = false;
    // Create and add the page to the document
    PDPage page = new PDPage(PDRectangle.A4);
    doc.addPage(page);
    PDPageContentStream contents = null;

    float fontSize = 14;
    float leading = 1.5f * fontSize;
    PDRectangle mediabox = page.getMediaBox();
    float margin = 50;
    float width = mediabox.getWidth() - 2 * margin;
    float startX = mediabox.getLowerLeftX() + margin;
    float startY = mediabox.getUpperRightY() - margin;
    float offsetY = startY;

    try {
      contents = new PDPageContentStream(doc, page);
      contents.beginText();
      contents.setFont(font, fontSize);
      contents.newLineAtOffset(startX, startY);
      offsetY -= leading;
      contents.showText(pageHeader);
      contents.newLineAtOffset(0, -leading);
      offsetY -= leading;

      List<String> lines = new ArrayList<>();
      parseIndividualLines(pageText, lines, fontSize, font, width);

      for (String line : lines) {
        contents.showText(line);
        contents.newLineAtOffset(0, -leading);
        offsetY -= leading;

        if (offsetY <= 0) {
          contents.endText();
          try {
            if (contents != null) {
              contents.close();
            }
          } catch (IOException e) {
            ok = false;
            Logger.log(e);
          }
          page = new PDPage();
          doc.addPage(page);
          contents = new PDPageContentStream(doc, page);
          contents.beginText();
          contents.setFont(font, fontSize);
          offsetY = startY;
          contents.newLineAtOffset(startX, startY);
        }
      }
      contents.endText();

      float scale = 1f;
      PDImageXObject pdImage = PDImageXObject.createFromFile(path, doc);
      pdImage.setWidth(200);
      pdImage.setHeight(100);
      scale = 1;
      offsetY -= (pdImage.getHeight() * scale);
      if (offsetY <= 0) {
        try {
          if (contents != null) {
            contents.close();
          }
        } catch (IOException e) {
          ok = false;
          Logger.log(e);
        }
        page = new PDPage();
        doc.addPage(page);
        contents = new PDPageContentStream(doc, page);
        offsetY = startY - (pdImage.getHeight() * scale);
      }
      contents.drawImage(pdImage, startX, offsetY);
      ok = true;
    } catch (IOException e) {
      Logger.log(e);
      ok = false;
    } finally {
      try {
        if (contents != null) {
          contents.close();
        }
      } catch (IOException e) {
        ok = false;
        Logger.log(e);
      }
    }
    return ok;
  }

  void saveAndClose() {
    try {
      doc.save(pdfOutputDirectory + pdfFileName);
    } catch (IOException e) {
      Logger.log(e);
    } finally {
      try {
        doc.close();
      } catch (IOException e) {
        Logger.log(e);
      }
    }
  }

  private void parseIndividualLines(
      StringBuffer wholeLetter, List<String> lines, float fontSize, PDFont pdfFont, float width) {
    String[] paragraphs = wholeLetter.toString().split(System.getProperty("line.separator"));
    for (int i = 0; i < paragraphs.length; i++) {
      int lastSpace = -1;
      lines.add(" ");
      if (paragraphs[i] != null) {
        while (paragraphs[i].length() > 0) {
          paragraphs[i] = sanitizeCharacter(paragraphs[i]).toString();
          int spaceIndex = paragraphs[i].indexOf(' ', lastSpace + 1);
          if (spaceIndex < 0) {
            spaceIndex = paragraphs[i].length();
          }
          String subString = paragraphs[i].substring(0, spaceIndex);
          float size = 10;
          try {
            size = fontSize * pdfFont.getStringWidth(subString) / 1000;
          } catch (Exception e) {
            Logger.log(e);
          }
          if (size > width) {
            if (lastSpace < 0) {
              lastSpace = spaceIndex;
            }
            subString = paragraphs[i].substring(0, lastSpace);
            lines.add(subString);
            paragraphs[i] = paragraphs[i].substring(lastSpace).trim();
            lastSpace = -1;
          } else if (spaceIndex == paragraphs[i].length()) {
            lines.add(paragraphs[i]);
            paragraphs[i] = "";
          } else {
            lastSpace = spaceIndex;
          }
        }
      }
    }
  }

  private StringBuffer sanitizeCharacter(String text) {
    StringBuffer nonSymbolBuffer = new StringBuffer();
    for (char character : text.toCharArray()) {
      if (isCharacterEncodeable(character)) {
        nonSymbolBuffer.append(character);
      } else {
        nonSymbolBuffer.append("□");
      }
    }
    return nonSymbolBuffer;
  }

  private boolean isCharacterEncodeable(char character) {
    try {
      font.encode(Character.toString(character));
      return true;
    } catch (Exception e) {
      Logger.log(e);
      return false;
    }
  }
}
