/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
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

package com.harvard.studyappmodule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GatewayResourcesWebViewActivity extends AppCompatActivity {
  private AppCompatTextView title;
  private RelativeLayout backBtn;
  private WebView webView;
  private RelativeLayout shareBtn;
  private PDFView pdfView;
  private static final int PERMISSION_REQUEST_CODE = 1000;
  private String intentTitle;
  private String intentType;
  private File finalSharingFile;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resources_web_view);

    initializeXmlId();
    intentTitle = getIntent().getStringExtra("title");
    intentType = getIntent().getStringExtra("type");

    defaultPdfShow();

    setFont();

    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
          }
        });
    shareBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            try {

              Intent shareIntent = new Intent(Intent.ACTION_SEND);
              shareIntent.setData(Uri.parse("mailto:"));
              shareIntent.setType("application/pdf");
              shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
              shareIntent.putExtra(Intent.EXTRA_SUBJECT, intentTitle);

              ///////// default pdf show
              if (finalSharingFile.exists()) {
                Uri fileUri =
                    FileProvider.getUriForFile(
                        GatewayResourcesWebViewActivity.this,
                        getString(R.string.FileProvider_authorities),
                        finalSharingFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(shareIntent);
              }
              ////////
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  private void defaultPdfShow() {
    if (intentType.equalsIgnoreCase("pdf")) {
      webView.setVisibility(View.GONE);
      title.setText(intentTitle);
      // checking the permissions
      if ((ActivityCompat.checkSelfPermission(
                  GatewayResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED)
          || (ActivityCompat.checkSelfPermission(
                  GatewayResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED)) {
        String[] permission =
            new String[] {
              Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        if (!hasPermissions(permission)) {
          ActivityCompat.requestPermissions(
              (Activity) GatewayResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
        } else {
          finalSharingFile = getAssetsPdfPath();
          displayPdfView(finalSharingFile.getAbsolutePath());
        }
      } else {
        finalSharingFile = getAssetsPdfPath();
        displayPdfView(finalSharingFile.getAbsolutePath());
      }
    }
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
          Toast.makeText(
                  GatewayResourcesWebViewActivity.this,
                  getResources().getString(R.string.permission_deniedDate),
                  Toast.LENGTH_SHORT)
              .show();
          finish();
        } else {

          ///////// default pdf show
          finalSharingFile = getAssetsPdfPath();
          displayPdfView(finalSharingFile.getAbsolutePath());
          /////////
        }
        break;
    }
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    webView = (WebView) findViewById(R.id.webView);
    shareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
    pdfView = (PDFView) findViewById(R.id.pdfView);
  }

  private void setFont() {
    title.setTypeface(AppController.getTypeface(this, "bold"));
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  public File copy(File src) throws IOException {
    String primaryStoragePath =
        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + intentTitle + ".pdf";
    File file = new File(primaryStoragePath);
    if (!file.exists()) {
      file.createNewFile();
    }

    InputStream in = new FileInputStream(src);
    OutputStream out = new FileOutputStream(file);
    // Transfer bytes from in to out
    byte[] buf = new byte[1024];
    int len;
    while ((len = in.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    in.close();
    out.close();

    return file;
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      if (finalSharingFile.exists()) {
        finalSharingFile.delete();
      }

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void displayPdfView(String filePath) {
    pdfView.setVisibility(View.VISIBLE);
    try {
      pdfView
          .fromFile(new File(filePath))
          .defaultPage(0)
          .enableAnnotationRendering(true)
          .scrollHandle(new DefaultScrollHandle(GatewayResourcesWebViewActivity.this))
          .load();
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  public File getAssetsPdfPath() {
    String filePath =
        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + intentTitle + ".pdf";

    File destinationFile = new File(filePath);

    try {
      FileOutputStream outputStream = new FileOutputStream(destinationFile);
      InputStream inputStream = getAssets().open("pdf/appglossary.pdf");
      byte[] buffer = new byte[1024];
      int length = 0;
      while ((length = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, length);
      }
      outputStream.close();
      inputStream.close();
    } catch (IOException e) {
      Logger.log(e);
    }

    return destinationFile;
  }
}
