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

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.offlinemodule.auth.SyncAdapterManager;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.usermodule.NewPasscodeSetupActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.model.Apps;
import com.harvard.usermodule.webservicemodel.RegistrationData;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.utils.realm.RealmEncryptionHelper;
import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionChecker;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;

public class SplashActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

  private static final int PASSCODE_RESPONSE = 101;
  private static final int APPS_RESPONSE = 103;
  private String newVersion;
  private boolean force = false;
  private static final int RESULT_CODE_UPGRADE = 102;
  private Apps apps;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.getInstance();
    byte[] key = realmEncryptionHelper.getEncryptKey();
    String s = bytesToHex(key);
    Log.e("realm key", "" + s);

    // sync registration
    SyncAdapterManager.init(this);
    AppController.keystoreInitilize(SplashActivity.this);
    getAppsInfo();

    AppController.getHelperSharedPreference()
        .writePreference(SplashActivity.this, getString(R.string.json_object_filter), "");
  }


  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  public static String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  private void getAppsInfo() {
    AppController.getHelperProgressDialog().showProgress(SplashActivity.this, "", "", false);
    ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
        new ParticipantDatastoreConfigEvent(
            "get",
            Urls.APPS + "?appId=" + AppConfig.APP_ID_VALUE,
            APPS_RESPONSE,
            this,
            Apps.class,
            new HashMap<String, String>(),
            null,
            null,
            false,
            this);
    RegisterUserEvent registerUserEvent = new RegisterUserEvent();
    registerUserEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performRegistration(registerUserEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == APPS_RESPONSE) {
      apps = (Apps) response;
      if (apps != null && apps.getVersion().getAndroid().getLatestVersion() != null) {
        DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
        apps.setAppId(AppConfig.APP_ID_VALUE);
        dbServiceSubscriber.saveApps(SplashActivity.this, apps);
        Version currVer = new Version(AppController.currentVersion());
        Version newVer = new Version(apps.getVersion().getAndroid().getLatestVersion());
        newVersion = apps.getVersion().getAndroid().getLatestVersion();
        force = Boolean.parseBoolean(apps.getVersion().getAndroid().getForceUpdate());
        if (currVer.equals(newVer) || currVer.compareTo(newVer) > 0) {
          isUpgrade(false, newVersion, force);
        } else {
          isUpgrade(true, newVersion, force);
        }
      } else {
        retryAlert();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == APPS_RESPONSE) {
      retryAlert();
    }
  }

  private void retryAlert() {
    AlertDialog.Builder alertDialogBuilder =
        new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
    alertDialogBuilder
        .setMessage("Error, can't continue")
        .setCancelable(false)
        .setPositiveButton(
            getResources().getString(R.string.retry),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                getAppsInfo();
              }
            })
        .setNegativeButton(
            getResources().getString(R.string.cancel),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                finish();
              }
            });
    alertDialogBuilder.show();
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
                  if (pass != null) {
                    AppController.deleteKey("passcode_" + pass);
                  }
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
      Version currVer = new Version(AppController.currentVersion());
      Version newVer = new Version(newVersion);
      if (currVer.equals(newVer) || currVer.compareTo(newVer) > 0) {
        proceedToApp();
      } else {
        if (force) {
          Toast.makeText(
              SplashActivity.this,
              "Please update the app to continue using",
              Toast.LENGTH_SHORT)
              .show();
          moveTaskToBack(true);
          if (Build.VERSION.SDK_INT < 21) {
            finishAffinity();
          } else {
            finishAndRemoveTask();
          }
        } else {
          AlertDialog.Builder alertDialogBuilder =
              new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
          alertDialogBuilder.setTitle("Upgrade");
          alertDialogBuilder
              .setMessage("Please consider updating app next time")
              .setCancelable(false)
              .setPositiveButton(
                  "ok",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      proceedToApp();
                    }
                  }).show();

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
    protected void onPreExecute() {
    }
  }

  public void isUpgrade(boolean b, String newVersion, final boolean force) {
    this.newVersion = newVersion;
    this.force = force;
    String msg;
    String positiveButton;
    String negativeButton;
    if (b) {
      if (force) {
        msg = "Please upgrade the app to continue.";
        positiveButton = "Ok";
        negativeButton = "Cancel";
      } else {
        msg = "A new version of this app is available. Do you want to update it now?";
        positiveButton = "Yes";
        negativeButton = "Skip";
      }
      AlertDialog.Builder alertDialogBuilder =
          new AlertDialog.Builder(SplashActivity.this, R.style.MyAlertDialogStyle);
      alertDialogBuilder.setTitle("Upgrade");
      alertDialogBuilder
          .setMessage(msg)
          .setCancelable(false)
          .setPositiveButton(
              positiveButton,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  startActivityForResult(
                      new Intent(Intent.ACTION_VIEW, Uri.parse(VersionChecker.PLAY_STORE_URL)),
                      RESULT_CODE_UPGRADE);
                }
              })
          .setNegativeButton(
              negativeButton,
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
                    moveTaskToBack(true);
                    if (Build.VERSION.SDK_INT < 21) {
                      finishAffinity();
                    } else {
                      finishAndRemoveTask();
                    }
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
