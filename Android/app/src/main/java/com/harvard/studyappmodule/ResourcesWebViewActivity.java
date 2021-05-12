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
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.crypto.CipherInputStream;

public class ResourcesWebViewActivity extends AppCompatActivity {
  private AppCompatTextView titleTv;
  private RelativeLayout backBtn;
  private WebView webView;
  private RelativeLayout shareBtn;
  private PDFView pdfView;
  private String downloadedFilePath;
  private String fileName;
  private String downloadingFileUrl = "";
  private static final int PERMISSION_REQUEST_CODE = 1000;
  private String intentTitle;
  private String intentType;
  private String intentContent;
  private File finalMSharingFile;
  private ConnectionDetector connectionDetector =
      new ConnectionDetector(ResourcesWebViewActivity.this);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resources_web_view);

    downloadedFilePath = "/data/data/" + getPackageName() + "/files/";
    initializeXmlId();
    downloadingFileUrl = getIntent().getStringExtra("content");
    intentTitle = getIntent().getStringExtra("title");
    intentType = getIntent().getStringExtra("type");
    intentContent = getIntent().getStringExtra("content");
    String studyId = getIntent().getStringExtra("studyId");

    String title;
    // removing space b/w the string : name of the pdf
    try {
      title = intentTitle.replaceAll("\\s+", "");
    } catch (Exception e) {
      title = intentTitle;
      Logger.log(e);
    }
    fileName = title + studyId;

    if (intentType.equalsIgnoreCase("pdf")) {
      webView.setVisibility(View.GONE);
      titleTv.setText(intentTitle);
      // checking the permissions
      if ((ActivityCompat.checkSelfPermission(
                  ResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED)
          || (ActivityCompat.checkSelfPermission(
                  ResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED)) {
        String[] permission =
            new String[] {
              Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        if (!hasPermissions(permission)) {
          ActivityCompat.requestPermissions(
              (Activity) ResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
        } else {
          if (connectionDetector.isConnectingToInternet()) {
            // starting new Async Task for downlaoding pdf file
            new DownloadFileFromUrl(downloadingFileUrl, downloadedFilePath, fileName).execute();
          } else {
            // offline functionality
            offLineFunctionality();
          }
        }
      } else {
        if (connectionDetector.isConnectingToInternet()) {
          // starting new Async Task for downlaoding pdf file
          new DownloadFileFromUrl(downloadingFileUrl, downloadedFilePath, fileName).execute();
        } else {
          // offline functionality
          offLineFunctionality();
        }
      }
    } else {
      setTextForView();
    }

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
              // if pdf then attach and send else content send
              if (intentType.equalsIgnoreCase("pdf")) {
                File file = new File(downloadedFilePath + fileName + ".pdf");
                if (file.exists()) {
                  finalMSharingFile = copy(file);
                  Uri fileUri =
                      FileProvider.getUriForFile(
                          ResourcesWebViewActivity.this,
                          getString(R.string.FileProvider_authorities),
                          finalMSharingFile);
                  shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                }
              } else {
                shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(intentContent));
              }

              startActivity(shareIntent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
        Toast.makeText(
                ResourcesWebViewActivity.this,
                getResources().getString(R.string.permission_deniedDate),
                Toast.LENGTH_SHORT)
            .show();
        finish();
      } else {
        if (connectionDetector.isConnectingToInternet()) {
          new DownloadFileFromUrl(downloadingFileUrl, downloadedFilePath, fileName).execute();
        } else {
          Toast.makeText(
                  ResourcesWebViewActivity.this,
                  getResources().getString(R.string.check_internet),
                  Toast.LENGTH_LONG)
              .show();
        }
      }
    }
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    titleTv = (AppCompatTextView) findViewById(R.id.title);
    webView = (WebView) findViewById(R.id.webView);
    shareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
    pdfView = (PDFView) findViewById(R.id.pdfView);
  }

  private void setTextForView() {
    String title = intentTitle;
    titleTv.setText(title);
    webView.getSettings().setLoadsImagesAutomatically(true);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");
    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    String webData = intentContent;
    if (Build.VERSION.SDK_INT >= 24) {
      webView.loadData(
              Html.fromHtml((webData), Html.FROM_HTML_MODE_LEGACY).toString(), "text/html", "UTF-8");
    } else {
      webView.loadData(Html.fromHtml((webData)).toString(), "text/html", "UTF-8");
    }
  }

  private void setFont() {
    titleTv.setTypeface(AppController.getTypeface(this, "bold"));
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(ResourcesWebViewActivity.this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  public File copy(File src) throws IOException {
    String filePath;
    if (Build.VERSION.SDK_INT < VERSION_CODES.Q) {
      filePath =
          Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + intentTitle + ".pdf";
    } else {
      filePath = getExternalFilesDir(getString(R.string.app_name)) + "/" + intentTitle + ".pdf";
    }
    File file = new File(filePath);
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
      File file = new File(downloadedFilePath + fileName + ".pdf");
      if (file.exists()) {
        file.delete();
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    try {
      if (finalMSharingFile.exists()) {
        finalMSharingFile.delete();
      }

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  class DownloadFileFromUrl extends AsyncTask<String, String, String> {

    /** Before starting background thread Show Progress Bar Dialog. */
    String downloadUrl = "";

    String filePath = "";
    String fileName = "";

    DownloadFileFromUrl(String downloadUrl, String filePath, String fileName) {
      this.downloadUrl = downloadUrl;
      this.filePath = filePath;
      this.fileName = fileName;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      AppController.getHelperProgressDialog()
          .showProgress(ResourcesWebViewActivity.this, "", "", false);
    }

    /** Downloading file in background thread. */
    @Override
    protected String doInBackground(String... url1) {
      int count;
      try {
        URL url = new URL(downloadUrl);
        URLConnection conection = url.openConnection();
        conection.connect();
        // getting file length
        int lenghtOfFile = conection.getContentLength();

        // input stream to read file - with 8k buffer
        InputStream input = new BufferedInputStream(url.openStream(), 8192);

        // Output stream to write file
        OutputStream output = new FileOutputStream(filePath + fileName + ".pdf");

        byte[] data = new byte[1024];

        long total = 0;

        while ((count = input.read(data)) != -1) {
          total += count;
          // publishing the progress....
          // After this onProgressUpdate will be called
          publishProgress("" + (int) ((total * 100) / lenghtOfFile));

          // writing data to file
          output.write(data, 0, count);
        }

        // flushing output
        output.flush();

        // closing streams
        output.close();
        input.close();

      } catch (Exception e) {
        // while downloading time, net got disconnected so delete the file
        try {
          new File(filePath + fileName + ".pdf").delete();
        } catch (Exception e1) {
          Logger.log(e1);
        }
      }
      return null;
    }

    /** After completing background task Dismiss the progress dialog. */
    @Override
    protected void onPostExecute(String url) {
      try {
        // downlaod success mean file exist else check offline file
        File file = new File(filePath + fileName + ".pdf");
        if (file.exists()) {
          AppController.generateEncryptedConsentPdf(filePath, fileName);
          displayPdfView(filePath + fileName + ".pdf");
        } else {
          // offline functionality
          offLineFunctionality();
        }
      } catch (Exception e) {
        Logger.log(e);
      }
      // dismiss the dialog after the file was downloaded
      AppController.getHelperProgressDialog().dismissDialog();
    }
  }

  private void offLineFunctionality() {
    try {
      // checking encrypted file is there or not?
      File file = new File(downloadedFilePath + fileName + ".txt");
      if (file.exists()) {
        // decrypt the file
        File decryptFile = getEncryptedFilePath(downloadedFilePath + fileName + ".txt");
        displayPdfView(decryptFile.getAbsolutePath());
      } else {
        Toast.makeText(
                ResourcesWebViewActivity.this,
                getResources().getString(R.string.check_internet),
                Toast.LENGTH_LONG)
            .show();
      }
    } catch (Resources.NotFoundException e) {
      Toast.makeText(
              ResourcesWebViewActivity.this,
              getResources().getString(R.string.check_internet),
              Toast.LENGTH_LONG)
          .show();
      Logger.log(e);
    }
  }

  private File getEncryptedFilePath(String filePath) {
    try {
      CipherInputStream cis = AppController.generateDecryptedConsentPdf(filePath);
      byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
      File file = new File(downloadedFilePath + fileName + ".pdf");
      if (!file.exists() && file == null) {
        file.createNewFile();
      }
      OutputStream output = new FileOutputStream(file);
      output.write(byteArray);
      output.close();
      return file;
    } catch (IOException e) {
      Logger.log(e);
    }
    return null;
  }

  private void displayPdfView(String filePath) {
    pdfView.setVisibility(View.VISIBLE);
    try {
      pdfView
          .fromFile(new File(filePath))
          .defaultPage(0)
          .enableAnnotationRendering(true)
          .scrollHandle(new DefaultScrollHandle(ResourcesWebViewActivity.this))
          .load();
    } catch (Exception e) {
      Logger.log(e);
    }
  }
}
