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

package com.harvard.passcodemodule;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.SplashActivity;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.model.Apps;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionChecker;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;

public class PasscodeSetupActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private AppCompatTextView setup;
  private AppCompatTextView hrLine1;
  private RelativeLayout cancelBtn;
  private PasscodeView passcodeView;
  private TextView forgot;
  private TextView passcodeTitle;
  private TextView passcodeDesc;
  private DbServiceSubscriber dbServiceSubscriber;
  private static final int APPS_RESPONSE = 101;
  private static final int RESULT_CODE_UPGRADE = 102;
  private String newVersion;
  private boolean force = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passcode_setup);
    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    dbServiceSubscriber = new DbServiceSubscriber();
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvent();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    setup = (AppCompatTextView) findViewById(R.id.setup_info);
    hrLine1 = (AppCompatTextView) findViewById(R.id.hrLine1);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    passcodeView = (PasscodeView) findViewById(R.id.passcode_view);
    passcodeTitle = (TextView) findViewById(R.id.passcodetitle);

    forgot = (TextView) findViewById(R.id.forgot);
    passcodeDesc = (TextView) findViewById(R.id.passcodedesc);
  }

  private void setTextForView() {
    cancelBtn.setVisibility(View.GONE);
    hrLine1.setVisibility(View.GONE);
    passcodeDesc.setVisibility(View.INVISIBLE);
    title.setText("");
    setup.setText("");
    passcodeTitle.setText(getString(R.string.enter_your_passcode));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "medium"));
      passcodeTitle.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvent() {
    passcodeView.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            passcodeView.requestToShowKeyboard();
          }
        },
        400);

    backBtn.setVisibility(View.GONE);
    forgot.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            getAppsInfo();
          }
        });

    passcodeView.setPasscodeEntryListener(
        new PasscodeView.PasscodeEntryListener() {
          @Override
          public void onPasscodeEntered(String passcode) {
            // chk with keystore passcode
            if (passcode.equalsIgnoreCase(AppController.refreshKeys("passcode"))) {
              AppController.getHelperSharedPreference()
                  .writePreference(getApplicationContext(), "passcodeAnswered", "yes");
              AppController.getHelperHideKeyboard(PasscodeSetupActivity.this);
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);
              finish();
            } else {
              Toast.makeText(PasscodeSetupActivity.this, R.string.invalidcode, Toast.LENGTH_SHORT)
                  .show();
              passcodeView.clearText();
            }
          }
        });
  }

  private void forgotSignin() {
    // display sign out confirmation.
    AlertDialog.Builder adb = new AlertDialog.Builder(PasscodeSetupActivity.this);
    adb.setTitle(getResources().getString(R.string.app_name));
    adb.setIcon(android.R.drawable.ic_dialog_alert);
    adb.setMessage(R.string.forgotpasscodemsg);
    adb.setPositiveButton(
        getResources().getString(R.string.ok),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            AppController.forceSignout(PasscodeSetupActivity.this);
          }
        });

    adb.setNegativeButton(
        getResources().getString(R.string.cancel),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    adb.show();
  }

  @Override
  protected void onStop() {
    super.onStop();
    finish();
  }

  @Override
  public void onBackPressed() {
    if (getIntent().getStringExtra("from") != null
        && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
      super.onBackPressed();
    }
  }

  private void getAppsInfo() {
    AppController.getHelperProgressDialog().showProgress(PasscodeSetupActivity.this, "", "", false);
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
      Apps apps = (Apps) response;
      if (apps != null && apps.getVersion().getAndroid().getLatestVersion() != null) {
        DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
        apps.setAppId(AppConfig.APP_ID_VALUE);
        dbServiceSubscriber.saveApps(PasscodeSetupActivity.this, apps);
        Version currVer = new Version(AppController.currentVersion());
        Version newVer = new Version(apps.getVersion().getAndroid().getLatestVersion());
        newVersion = apps.getVersion().getAndroid().getLatestVersion();
        force = Boolean.parseBoolean(apps.getVersion().getAndroid().getForceUpdate());
        if (currVer.equals(newVer) || currVer.compareTo(newVer) > 0) {
          forgotSignin();
        } else {
          if (!force) {
            forgotSignin();
          } else {
            isUpgrade(true, newVersion, force);
          }
        }
      } else {
        retryAlert();
      }
    }
  }

  private void retryAlert() {
    AlertDialog.Builder alertDialogBuilder =
        new AlertDialog.Builder(PasscodeSetupActivity.this, R.style.MyAlertDialogStyle);
    alertDialogBuilder
        .setMessage("Error, can't continue")
        .setCancelable(false)
        .setPositiveButton(
            getResources().getString(R.string.retry),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                getAppsInfo();
              }
            });
    alertDialogBuilder.show();
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
          new AlertDialog.Builder(PasscodeSetupActivity.this, R.style.MyAlertDialogStyle);
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
                  if (force) {
                    Toast.makeText(
                        PasscodeSetupActivity.this,
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
                    dialog.dismiss();
                    forgotSignin();
                  }
                }
              });
      AlertDialog alertDialog = alertDialogBuilder.create();
      alertDialog.show();
    } else {
      forgotSignin();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == APPS_RESPONSE) {
      retryAlert();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RESULT_CODE_UPGRADE) {
      Version currVer = new Version(AppController.currentVersion());
      Version newVer = new Version(newVersion);
      if (currVer.equals(newVer) || currVer.compareTo(newVer) > 0) {
        forgotSignin();
      } else {
        if (force) {
          Toast.makeText(
              PasscodeSetupActivity.this,
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
              new AlertDialog.Builder(PasscodeSetupActivity.this, R.style.MyAlertDialogStyle);
          alertDialogBuilder.setTitle("Upgrade");
          alertDialogBuilder
              .setMessage("Please consider updating app next time")
              .setCancelable(false)
              .setPositiveButton(
                  "ok",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      forgotSignin();
                    }
                  }).show();
        }
      }
    }
  }
}
