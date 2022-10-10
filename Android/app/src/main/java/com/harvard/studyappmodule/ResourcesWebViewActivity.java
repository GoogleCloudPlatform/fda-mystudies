/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
 *
 */

package com.harvard.studyappmodule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.PdfViewerView;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import io.realm.Realm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.CipherInputStream;

public class ResourcesWebViewActivity extends AppCompatActivity
    implements NetworkChangeReceiver.NetworkChangeCallback {
  private AppCompatTextView titleTv;
  private RelativeLayout backBtn;
  private WebView webView;
  private RelativeLayout shareBtn;
  private String CreateFilePath;
  private String fileName;
  private static final int PERMISSION_REQUEST_CODE = 1000;
  private String intentTitle;
  private String intentType;
  private String intentContent;
  private File finalMSharingFile;
  private ConnectionDetector connectionDetector =
      new ConnectionDetector(ResourcesWebViewActivity.this);
  private Realm realm;
  private DbServiceSubscriber dbServiceSubscriber;
  String resourceId;
  Resource resource;
  PdfViewerView pdfViewer;
  private CustomFirebaseAnalytics analyticsInstance;
  private NetworkChangeReceiver networkChangeReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_resources_web_view);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    networkChangeReceiver = new NetworkChangeReceiver(this);

    CreateFilePath = "/data/data/" + getPackageName() + "/files/";
    initializeXmlId();
    intentTitle = getIntent().getStringExtra("title");
    intentType = getIntent().getStringExtra("type");
    resourceId = getIntent().getStringExtra("resourceId");
    String studyId = getIntent().getStringExtra("studyId");

    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(ResourcesWebViewActivity.this);
    resource = dbServiceSubscriber.getResource(resourceId, realm);

    String title;
    // removing space b/w the string : name of the pdf
    try {
      title = intentTitle.replaceAll("\\s+", "");
    } catch (Exception e) {
      title = intentTitle;
      Logger.log(e);
    }
    title = title.replace("/", "\u2215");
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
            new CreateFileFromBase64(resource.getContent(), CreateFilePath, fileName).execute();
          } else {
            // offline functionality
            offLineFunctionality();
          }
        }
      } else {
        if (connectionDetector.isConnectingToInternet()) {
          // starting new Async Task for downlaoding pdf file
          new CreateFileFromBase64(resource.getContent(), CreateFilePath, fileName).execute();
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
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.resources_webview_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });
    shareBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.resources_webview_share));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            try {

              Intent shareIntent = new Intent(Intent.ACTION_SEND);
              shareIntent.setData(Uri.parse("mailto:"));
              shareIntent.setType("application/pdf");
              shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
              shareIntent.putExtra(Intent.EXTRA_SUBJECT, intentTitle);
              // if pdf then attach and send else content send
              if (intentType.equalsIgnoreCase("pdf")) {
                File file = new File(CreateFilePath + fileName + ".pdf");
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
                shareIntent.setType("text/html");
                shareIntent.putExtra(
                    Intent.EXTRA_TEXT, Html.fromHtml(resource.getContent()).toString());
                shareIntent.putExtra(
                    Intent.EXTRA_HTML_TEXT, Html.fromHtml(resource.getContent()).toString());
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
          new CreateFileFromBase64(resource.getContent(), CreateFilePath, fileName).execute();
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
    pdfViewer = (PdfViewerView) findViewById(R.id.pdfViewer);
  }

  private void setTextForView() {
    String title = intentTitle;
    titleTv.setText(title);
    webView.getSettings().setLoadsImagesAutomatically(true);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDefaultTextEncodingName("utf-8");
    webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
    String webData = resource.getContent();
    if (Build.VERSION.SDK_INT >= 24) {
      webView.loadDataWithBaseURL(
          null,
          Html.fromHtml((webData), Html.FROM_HTML_MODE_LEGACY).toString(),
          "text/html",
          "UTF-8",
          null);
    } else {
      webView.loadDataWithBaseURL(
          null, Html.fromHtml((webData)).toString(), "text/html", "UTF-8", null);
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
    pdfViewer.destroyPdfRender();
    try {
      File file = new File(CreateFilePath + fileName + ".pdf");
      if (file.exists()) {
        file.delete();
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    try {
      if (finalMSharingFile != null && finalMSharingFile.exists()) {
        finalMSharingFile.delete();
      }

    } catch (Exception e) {
      Logger.log(e);
    }
    dbServiceSubscriber.closeRealmObj(realm);
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      shareBtn.setClickable(false);
      shareBtn.setAlpha(0.3F);
    } else {
      shareBtn.setClickable(true);
      shareBtn.setAlpha(1F);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }

  class CreateFileFromBase64 extends AsyncTask<String, String, String> {

    /** Before starting background thread Show Progress Bar Dialog. */
    String downloadUrl = "";

    String filePath = "";
    String fileName = "";

    CreateFileFromBase64(String downloadUrl, String filePath, String fileName) {
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
        FileOutputStream fos = new FileOutputStream(filePath + fileName + ".pdf");
        fos.write(Base64.decode(downloadUrl.split(",")[1], Base64.NO_WRAP));
        fos.close();

      } catch (Exception e) {
        // while downloading time, net got disconnected so delete the file
        try {
          new File(filePath + fileName + ".pdf").delete();
        } catch (Exception e1) {
          Logger.log(e1);
        }
      }
      AppController.generateEncryptedConsentPdf(filePath, fileName);
      return null;
    }

    /** After completing background task Dismiss the progress dialog. */
    @Override
    protected void onPostExecute(String url) {
      try {
        // downlaod success mean file exist else check offline file
        File file = new File(filePath + fileName + ".pdf");
        if (file.exists()) {
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
      File file = new File(CreateFilePath + fileName + ".txt");
      if (file.exists()) {
        // decrypt the file
        File decryptFile = getEncryptedFilePath(CreateFilePath + fileName + ".txt");
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
      File file = new File(CreateFilePath + fileName + ".pdf");
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
    pdfViewer.setVisibility(View.VISIBLE);
    pdfViewer.setPdf(new File(filePath));
  }
}
