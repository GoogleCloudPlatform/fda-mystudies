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
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.CipherInputStream;

public class ConsentCompletedActivity extends AppCompatActivity {

  private static final int PERMISSION_REQUEST_CODE = 1000;
  private TextView consentCompleteTxt;
  private TextView textSecRow;
  private TextView viewPdf;
  private TextView next;

  public static String FROM = "from";
  private boolean click = true;
  private File sharingFile = null;
  private String comingFrom = "";
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_consent_completed);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);

    initializeXmlId();
    try {
      if (getIntent().getStringExtra(FROM) != null
          && getIntent()
              .getStringExtra(FROM)
              .equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
        comingFrom = SurveyActivitiesFragment.FROM_SURVAY;
      }
    } catch (Exception e) {
      comingFrom = "";
      Logger.log(e);
    }
    setFont();
    viewPdf.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if ((ActivityCompat.checkSelfPermission(
                        ConsentCompletedActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(
                        ConsentCompletedActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)) {
              String[] permission =
                  new String[] {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                  };
              if (!hasPermissions(permission)) {
                ActivityCompat.requestPermissions(
                    (Activity) ConsentCompletedActivity.this, permission, PERMISSION_REQUEST_CODE);
              } else {
                displayPdf();
              }
            } else {
              displayPdf();
            }
          }
        });
    next.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (click) {
              click = false;
              new Handler()
                  .postDelayed(
                      new Runnable() {
                        @Override
                        public void run() {
                          click = true;
                        }
                      },
                      3000);
              if (!comingFrom.equalsIgnoreCase(SurveyActivitiesFragment.FROM_SURVAY)) {
                Intent intent = new Intent(ConsentCompletedActivity.this, SurveyActivity.class);
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                startActivity(intent);
              } else {
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
              }
              finish();
            }
          }
        });
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(ConsentCompletedActivity.this, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
        Toast.makeText(
                ConsentCompletedActivity.this, R.string.permission_deniedDate, Toast.LENGTH_SHORT)
            .show();
      } else {
        displayPdf();
      }
    }
  }

  private void initializeXmlId() {
    consentCompleteTxt = (TextView) findViewById(R.id.consentcomplete);
    textSecRow = (TextView) findViewById(R.id.mTextSecRow);
    viewPdf = (TextView) findViewById(R.id.viewpdf);
    next = (TextView) findViewById(R.id.next);
  }

  @Override
  public void onBackPressed() {}

  private void setFont() {
    try {
      consentCompleteTxt.setTypeface(
          AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
      textSecRow.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "light"));
      viewPdf.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
      next.setTypeface(AppController.getTypeface(ConsentCompletedActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void displayPdf() {

    File file = getEncryptedFilePath(getIntent().getStringExtra("PdfPath"));

    try {
      sharingFile = copy(file);
    } catch (IOException e) {
      Logger.log(e);
    }

    if (sharingFile != null && sharingFile.exists()) {
      LayoutInflater li = LayoutInflater.from(ConsentCompletedActivity.this);
      View promptsView = li.inflate(R.layout.pdfdisplayview, null);
      PDFView pdfView = (PDFView) promptsView.findViewById(R.id.pdf);
      TextView share = (TextView) promptsView.findViewById(R.id.share);
      AlertDialog.Builder db = new AlertDialog.Builder(ConsentCompletedActivity.this);
      db.setView(promptsView);
      final File finalMSharingFile = sharingFile;
      share.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent shareIntent = new Intent(Intent.ACTION_SEND);
              shareIntent.setData(Uri.parse("mailto:"));
              shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.signed_consent));
              shareIntent.setType("application/pdf");
              Uri fileUri =
                  FileProvider.getUriForFile(
                      ConsentCompletedActivity.this,
                      getString(R.string.FileProvider_authorities),
                      finalMSharingFile);
              shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
              shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
              startActivity(shareIntent);
            }
          });
      pdfView.documentFitsView();
      pdfView.useBestQuality(true);
      pdfView
          .fromFile(file)
          .enableAnnotationRendering(true)
          .scrollHandle(new DefaultScrollHandle(this))
          .load();
      db.show();
    } else {
      Toast.makeText(this, R.string.consentPdfNotAvailable, Toast.LENGTH_SHORT).show();
    }
  }

  private File getEncryptedFilePath(String filePath) {
    try {
      CipherInputStream cis = AppController.generateDecryptedConsentPdf(filePath);
      byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
      File file = new File("/data/data/" + getPackageName() + "/files/" + "temp" + ".pdf");
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

  public File copy(File src) throws IOException {
    String root;
    if (Build.VERSION.SDK_INT < VERSION_CODES.Q) {
      root = Environment.getExternalStorageDirectory().getAbsolutePath();
    } else {
      root = getExternalFilesDir(getString(R.string.app_name)).getAbsolutePath();
    }
    String primaryStoragePath =
        root
            + "/"
            + AppController.getHelperSharedPreference()
                .readPreference(ConsentCompletedActivity.this, getString(R.string.title), "")
            + "_"
            + getString(R.string.signed_consent)
            + ".pdf";
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
    dbServiceSubscriber.closeRealmObj(realm);
    if (sharingFile != null && sharingFile.exists()) {
      sharingFile.delete();
    }
    super.onDestroy();
  }
}
