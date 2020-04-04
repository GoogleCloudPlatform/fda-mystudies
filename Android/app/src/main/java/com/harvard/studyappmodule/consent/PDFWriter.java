/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.studyappmodule.consent;

import android.content.Context;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFWriter {
  private String pdfOutputDirectory = "";
  private String pdfFileName = "";
  private PDDocument doc = null;
  private PDFont font = null;

  PDFWriter(String pdfOutputDirectory, String pdfFileName) {
    this.pdfOutputDirectory = pdfOutputDirectory;
    if (!this.pdfOutputDirectory.endsWith("/")) this.pdfOutputDirectory += "/";
    if (!pdfFileName.endsWith(".pdf")) {
      pdfFileName = pdfFileName + ".pdf";
    }
    this.pdfFileName = pdfFileName;
  }

  void createPdfFile(Context context) {
    PDFBoxResourceLoader.init(context);
    doc = new PDDocument();
    font = PDType1Font.HELVETICA;
  }

  boolean addPage(String pageHeader, StringBuffer pageText, String path) {
    boolean ok = false;
    // Create and add the page to the document
    PDPage page = new PDPage();
    doc.addPage(page);
    PDPageContentStream contents = null;

    float fontSize = 12;
    float leading = 1.5f * fontSize;
    PDRectangle mediabox = page.getMediaBox();
    float margin = 25;
    float width = mediabox.getWidth() - 2 * margin;
    float startX = mediabox.getLowerLeftX() + margin;
    float startY = mediabox.getUpperRightY() - margin;
    float yOffset = startY;

    try {
      contents = new PDPageContentStream(doc, page);
      contents.beginText();
      contents.setFont(font, 14);
      contents.newLineAtOffset(startX, startY);
      yOffset -= leading;
      contents.showText(pageHeader);
      contents.newLineAtOffset(0, -leading);
      yOffset -= leading;

      List<String> lines = new ArrayList<>();
      parseIndividualLines(pageText, lines, fontSize, font, width);

      contents.setFont(font, fontSize);
      for (String line : lines) {
        contents.showText(line);
        contents.newLineAtOffset(0, -leading);
        yOffset -= leading;

        if (yOffset <= 0) {
          contents.endText();
          try {
            if (contents != null) contents.close();
          } catch (IOException e) {
            ok = false;
            e.printStackTrace();
          }
          page = new PDPage();
          doc.addPage(page);
          contents = new PDPageContentStream(doc, page);
          contents.beginText();
          contents.setFont(font, fontSize);
          yOffset = startY;
          contents.newLineAtOffset(startX, startY);
        }
      }
      contents.endText();

      float scale = 1f;
      PDImageXObject pdImage = PDImageXObject.createFromFile(path, doc);
      pdImage.setWidth(200);
      pdImage.setHeight(100);
      scale = 1;
      yOffset -= (pdImage.getHeight() * scale);
      if (yOffset <= 0) {
        try {
          if (contents != null) contents.close();
        } catch (IOException e) {
          ok = false;
          e.printStackTrace();
        }
        page = new PDPage();
        doc.addPage(page);
        contents = new PDPageContentStream(doc, page);
        yOffset = startY - (pdImage.getHeight() * scale);
      }
      contents.drawImage(pdImage, startX, yOffset);
      ok = true;
    } catch (IOException e) {
      e.printStackTrace();
      ok = false;
    } finally {
      try {
        if (contents != null) contents.close();
      } catch (IOException e) {
        ok = false;
        e.printStackTrace();
      }
    }

    return ok;
  }

  void saveAndClose() {
    try {
      doc.save(pdfOutputDirectory + pdfFileName);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        doc.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void parseIndividualLines(
      StringBuffer wholeLetter, List<String> lines, float fontSize, PDFont pdfFont, float width)
      throws IOException {
    String[] paragraphs = wholeLetter.toString().split(System.getProperty("line.separator"));
    for (int i = 0; i < paragraphs.length; i++) {
      int lastSpace = -1;
      lines.add(" ");
      while (paragraphs[i].length() > 0) {
        int spaceIndex = paragraphs[i].indexOf(' ', lastSpace + 1);
        if (spaceIndex < 0) {
          spaceIndex = paragraphs[i].length();
        }
        String subString = paragraphs[i].substring(0, spaceIndex);
        float size = fontSize * pdfFont.getStringWidth(subString) / 1000;
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
