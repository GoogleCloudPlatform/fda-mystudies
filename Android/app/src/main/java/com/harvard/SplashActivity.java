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

package com.harvard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.offlinemodule.auth.SyncAdapterManager;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.usermodule.NewPasscodeSetupActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionChecker;
import io.fabric.sdk.android.services.common.CommonUtils;

public class SplashActivity extends AppCompatActivity implements VersionChecker.Upgrade {

  private static final int PASSCODE_RESPONSE = 101;
  private VersionChecker versionChecker;
  private String newVersion = "";
  private boolean force = false;
  private static final int RESULT_CODE_UPGRADE = 102;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    if (CommonUtils.isRooted(SplashActivity.this)) {
      Toast.makeText(
              SplashActivity.this,
              getResources().getString(R.string.rooted_device),
              Toast.LENGTH_LONG)
          .show();
      new Handler()
          .postDelayed(
              new Runnable() {
                @Override
                public void run() {
                  System.exit(0);
                }
              },
              1000);
    } else {
      // sync registration
      SyncAdapterManager.init(this);
      AppController.keystoreInitilize(SplashActivity.this);
      versionChecker = new VersionChecker(SplashActivity.this);
      versionChecker.execute();
    }
    AppController.getHelperSharedPreference()
        .writePreference(SplashActivity.this, getString(R.string.json_object_filter), "");
  }

  public void loadsplash() {
    new LongOperation().execute();
  }

  private void startmain() {
    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                if (!AppController.getHelperSharedPreference()
                        .readPreference(
                            SplashActivity.this, getResources().getString(R.string.userid), "")
                        .equalsIgnoreCase("")
                    && AppController.getHelperSharedPreference()
                        .readPreference(
                            SplashActivity.this, getResources().getString(R.string.verified), "")
                        .equalsIgnoreCase("true")) {
                  if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(SplashActivity.this, StudyActivity.class);
                    startActivity(intent);
                  } else {
                    Intent intent = new Intent(SplashActivity.this, StandaloneActivity.class);
                    startActivity(intent);
                  }
                } else {
                  SharedPreferences settings =
                      SharedPreferenceHelper.getPreferences(SplashActivity.this);
                  settings.edit().clear().apply();
                  // delete passcode from keystore
                  String pass = AppController.refreshKeys("passcode");
                  if (pass != null) AppController.deleteKey("passcode_" + pass);
                  if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(SplashActivity.this, GatewayActivity.class);
                    startActivity(intent);
                  } else {
                    Intent intent = new Intent(SplashActivity.this, StandaloneActivity.class);
                    startActivity(intent);
                  }
                }
                finish();
              }
            },
            1000);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESULT_CODE_UPGRADE) {
      Version curr_ver = new Version(versionChecker.currentVersion());
      Version new_ver = new Version(newVersion);
      if (curr_ver.equals(new_ver) || curr_ver.compareTo(new_ver) > 0) {
        proceedToApp();
      } else {
        if (force) {
          Toast.makeText(
                  SplashActivity.this,
                  "Please update the app to continue using",
                  Toast.LENGTH_SHORT)
              .show();
          finish();
        } else {
          Toast.makeText(
                  SplashActivity.this, "Please consider updating app next time", Toast.LENGTH_SHORT)
              .show();
          proceedToApp();
        }
      }
    } else if (requestCode == PASSCODE_RESPONSE) {
      startmain();
    }
  }

  private class LongOperation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      if (AppController.getHelperSharedPreference()
          .readPreference(
              getApplicationContext(), getResources().getString(R.string.usepasscode), "")
          .equalsIgnoreCase("yes")) {
        while (AppController.getHelperSharedPreference()
            .readPreference(getApplicationContext(), "passcodeAnswered", "no")
            .equalsIgnoreCase("no")) {
          if (AppController.getHelperSharedPreference()
              .readPreference(getApplicationContext(), "passcodeAnswered", "no")
              .equalsIgnoreCase("yes")) {
            break;
          }
        }
      }
      return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
      startmain();
    }

    @Override
    protected void onPreExecute() {}
  }

  @Override
  public void isUpgrade(boolean b, String newVersion, final boolean force) {
    this.newVersion = newVersion;
    this.force = force;
    if (b) {
      AlertDialog.Builder alertDialogBuilder =
          new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
      alertDialogBuilder.setTitle("Upgrade");
      alertDialogBuilder
          .setMessage("Please upgrade the app to continue.")
          .setCancelable(false)
          .setPositiveButton(
              "Upgrade",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  startActivityForResult(
                      new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)),
                      RESULT_CODE_UPGRADE);
                }
              })
          .setNegativeButton(
              "Cancel",
              new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  if (force) {
                    Toast.makeText(
                            SplashActivity.this,
                            "Please update the app to continue using",
                            Toast.LENGTH_SHORT)
                        .show();
                    finish();
                  } else {
                    proceedToApp();
                  }
                }
              });
      AlertDialog alertDialog = alertDialogBuilder.create();
      alertDialog.show();
    } else {
      proceedToApp();
    }
  }

  private void proceedToApp() {
    if (!AppController.getHelperSharedPreference()
            .readPreference(SplashActivity.this, getResources().getString(R.string.userid), "")
            .equalsIgnoreCase("")
        && AppController.getHelperSharedPreference()
            .readPreference(SplashActivity.this, getString(R.string.initialpasscodeset), "yes")
            .equalsIgnoreCase("no")) {
      Intent intent = new Intent(SplashActivity.this, NewPasscodeSetupActivity.class);
      intent.putExtra("from", "signin");
      startActivityForResult(intent, PASSCODE_RESPONSE);
    } else {
      loadsplash();
    }
  }
}
