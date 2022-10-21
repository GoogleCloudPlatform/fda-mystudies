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

package com.harvard.gatewaymodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.viewpager.widget.ViewPager;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.gatewaymodule.events.GetStartedEvent;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.usermodule.SignupActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.model.Apps;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionChecker;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import io.realm.Realm;
import java.util.HashMap;


public class GatewayActivity extends AppCompatActivity
    implements NetworkChangeReceiver.NetworkChangeCallback, ApiCall.OnAsyncRequestComplete {
  private static final int UPGRADE = 100;
  private static final int RESULT_CODE_UPGRADE = 101;
  private AppCompatTextView getStarted;
  private RelativeLayout newUserLayout;
  private AppCompatTextView newUserButton;
  private RelativeLayout signInButtonLayout;
  private AppCompatTextView signInButton;
  private static final String COMMING_FROM = "Gateway";
  private static final String FROM = "from";
  private static final String TYPEFACE_REGULAR = "regular";
  private static AlertDialog alertDialog;
  VersionReceiver versionReceiver;
  private String latestVersion;
  private TextView offlineIndicatior;
  private boolean force = false;
  AlertDialog.Builder alertDialogBuilder;
  private CustomFirebaseAnalytics analyticsInstance;
  private NetworkChangeReceiver networkChangeReceiver;
  private static final int APPS_RESPONSE = 103;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gateway);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    initializeXmlId();
    setFont();
    bindEvents();
    setViewPagerView();

    if (getIntent().getStringExtra("action") != null
        && getIntent().getStringExtra("action").equalsIgnoreCase(AppController.loginCallback)) {
      loadLogin();
    }
    if (!AppController.isNetworkAvailable(this)) {
      offlineIndicatior.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      offlineIndicatior.setVisibility(View.VISIBLE);
    } else {
      offlineIndicatior.setVisibility(View.GONE);
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == APPS_RESPONSE) {
      Apps apps = (Apps) response;
      CustomTabsIntent.Builder builder =
          new CustomTabsIntent.Builder()
              .setToolbarColor(getResources().getColor(R.color.colorAccent))
              .setShowTitle(true)
              .setCloseButtonIcon(
                  BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
              .setStartAnimations(GatewayActivity.this, R.anim.slide_in_right,
                  R.anim.slide_out_left)
              .setExitAnimations(GatewayActivity.this, R.anim.slide_in_left,
                  R.anim.slide_out_right);
      CustomTabsIntent customTabsIntent = builder.build();
      customTabsIntent.intent.setData(Uri.parse(Urls.LOGIN_URL
          .replace("$FromEmail", apps.getFromEmail())
          .replace("$SupportEmail", apps.getSupportEmail())
          .replace("$AppName", apps.getAppName())
          .replace("$ContactEmail", apps.getContactUsEmail())));
      startActivity(customTabsIntent.intent);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
  }

  public class VersionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getStringExtra("api").equalsIgnoreCase("success")) {
        Version currVer = new Version(AppController.currentVersion());
        Version latestVer = new Version(intent.getStringExtra("latestVersion"));

        latestVersion = intent.getStringExtra("latestVersion");
        force = Boolean.parseBoolean(intent.getStringExtra("force"));

        if (currVer.equals(latestVer) || currVer.compareTo(latestVer) > 0) {
          isUpgrade(false, latestVersion, force);
        } else {
          AppController.getHelperSharedPreference()
              .writePreference(GatewayActivity.this, "versionalert", "done");
          isUpgrade(true, latestVersion, force);
        }
      } else {
        // commented because if impleting the offline indicator

        //        Toast.makeText(GatewayActivity.this, "Error detected", Toast.LENGTH_SHORT).show();
        //        if (Build.VERSION.SDK_INT < 21) {
        //          finishAffinity();
        //        } else {
        //          finishAndRemoveTask();
        //        }
      }
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    AppController.getHelperHideKeyboard(GatewayActivity.this);
    AppController.getHelperSharedPreference()
        .writePreference(GatewayActivity.this, getString(R.string.join), "false");

    IntentFilter filter = new IntentFilter();
    filter.addAction(BuildConfig.APPLICATION_ID);
    versionReceiver = new VersionReceiver();
    registerReceiver(versionReceiver, filter);

    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();

    try {
      unregisterReceiver(versionReceiver);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (alertDialog != null) {
        alertDialog.dismiss();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }

  private void initializeXmlId() {
    getStarted = (AppCompatTextView) findViewById(R.id.mGetStarted);
    newUserLayout = (RelativeLayout) findViewById(R.id.mNewUserLayout);
    newUserButton = (AppCompatTextView) findViewById(R.id.mNewUserButton);
    signInButtonLayout = (RelativeLayout) findViewById(R.id.mSignInButtonLayout);
    signInButton = (AppCompatTextView) findViewById(R.id.mSignInButton);
    offlineIndicatior = findViewById(R.id.offlineIndicatior);
  }

  private void setFont() {
    try {
      getStarted.setTypeface(AppController.getTypeface(this, TYPEFACE_REGULAR));
      newUserButton.setTypeface(AppController.getTypeface(GatewayActivity.this, TYPEFACE_REGULAR));
      signInButton.setTypeface(AppController.getTypeface(GatewayActivity.this, TYPEFACE_REGULAR));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    newUserLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!AppController.isNetworkAvailable(GatewayActivity.this)) {
              androidx.appcompat.app.AlertDialog.Builder alertDialog =
                  new androidx.appcompat.app.AlertDialog.Builder(
                      GatewayActivity.this, R.style.Style_Dialog_Rounded_Corner);
              alertDialog.setTitle("              You are offline");
              alertDialog.setMessage("You are offline. Kindly check the internet connection.");
              alertDialog.setCancelable(false);
              alertDialog.setPositiveButton(
                  "OK",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      Bundle eventProperties = new Bundle();
                      //          eventProperties.putString(
                      //              CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                      //              getString(R.string.app_update_next_time_ok));
                      //          analyticsInstance.logEvent(
                      //              CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK,
                      // eventProperties);
                      dialogInterface.dismiss();
                    }
                  });
              final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
              dialog.show();
            } else {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON, getString(R.string.new_user));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);

              Intent intent = new Intent(GatewayActivity.this, SignupActivity.class);
              startActivity(intent);
            }
          }
        });

    signInButtonLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON, getString(R.string.sign_in_btn));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (!AppController.isNetworkAvailable(GatewayActivity.this)) {
              androidx.appcompat.app.AlertDialog.Builder alertDialog =
                  new androidx.appcompat.app.AlertDialog.Builder(
                      GatewayActivity.this, R.style.Style_Dialog_Rounded_Corner);
              alertDialog.setTitle("              You are offline");
              alertDialog.setMessage("You are offline. Kindly check the internet connection.");
              alertDialog.setCancelable(false);
              alertDialog.setPositiveButton(
                  "OK",
                  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                      Bundle eventProperties = new Bundle();
                      //          eventProperties.putString(
                      //              CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                      //              getString(R.string.app_update_next_time_ok));
                      //          analyticsInstance.logEvent(
                      //              CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK,
                      // eventProperties);
                      dialogInterface.dismiss();
                    }
                  });
              final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
              dialog.show();
            } else {
              loadLogin();
            }
          }
        });

    getStarted.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON, getString(R.string.get_started));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);

            GetStartedEvent getStartedEvent = new GetStartedEvent();
            getStartedEvent.setCommingFrom(COMMING_FROM);
            onEvent(getStartedEvent);
          }
        });
  }

  private void setViewPagerView() {
    ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
    CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
    viewpager.setAdapter(new GatewayPagerAdapter());
    indicator.setViewPager(viewpager);
    viewpager.setCurrentItem(0);
    indicator.setOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          public void onPageScrollStateChanged(int state) {}

          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          public void onPageSelected(int position) {
            // Check if this is the page you want.
            if (position == 0) {
              getStarted.setBackground(getResources().getDrawable(R.drawable.rectangle_blue_white));
              getStarted.setTextColor(getResources().getColor(R.color.white));
            } else {
              getStarted.setBackground(
                  getResources().getDrawable(R.drawable.rectangle_black_white));
              getStarted.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  public void onEvent(GetStartedEvent event) {
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      Intent intent = new Intent(GatewayActivity.this, StudyActivity.class);
      intent.putExtra(FROM, event.getCommingFrom());
      startActivity(intent);
      finish();
    } else {
      Intent intent = new Intent(GatewayActivity.this, StandaloneActivity.class);
      intent.putExtra(FROM, event.getCommingFrom());
      startActivity(intent);
      finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == UPGRADE) {
      alertDialog.dismiss();
    } else if (requestCode == RESULT_CODE_UPGRADE) {
      Version currVer = new Version(AppController.currentVersion());
      Version latestVer = new Version(latestVersion);
      if (currVer.equals(latestVer) || currVer.compareTo(latestVer) > 0) {
        Logger.info(BuildConfig.APPLICATION_ID, "App Updated");
      } else {
        if (force) {
          Toast.makeText(
                  GatewayActivity.this,
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
              new AlertDialog.Builder(GatewayActivity.this, R.style.MyAlertDialogStyle);
          alertDialogBuilder.setTitle("Upgrade");
          alertDialogBuilder
              .setMessage("Please consider updating app next time")
              .setCancelable(false)
              .setPositiveButton(
                  "ok",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                      Bundle eventProperties = new Bundle();
                      eventProperties.putString(
                          CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                          getString(R.string.app_update_next_time_ok));
                      analyticsInstance.logEvent(
                          CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                      dialog.dismiss();
                    }
                  })
              .show();
        }
      }
    }
  }

  private void loadLogin() {
    SharedPreferenceHelper.writePreference(
        GatewayActivity.this, getString(R.string.loginflow), "Gateway");
    SharedPreferenceHelper.writePreference(
        GatewayActivity.this, getString(R.string.logintype), "signIn");
    CustomTabsIntent.Builder builder =
        new CustomTabsIntent.Builder()
            .setToolbarColor(getResources().getColor(R.color.colorAccent))
            .setShowTitle(true)
            .setCloseButtonIcon(
                BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
            .setStartAnimations(GatewayActivity.this, R.anim.slide_in_right, R.anim.slide_out_left)
            .setExitAnimations(GatewayActivity.this, R.anim.slide_in_left, R.anim.slide_out_right);

    CustomTabsIntent customTabsIntent = builder.build();
    DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
    Realm realm = AppController.getRealmobj(GatewayActivity.this);
    Apps apps = dbServiceSubscriber.getApps(realm);
    if (apps != null) {
      customTabsIntent.intent.setData(
          Uri.parse(
              Urls.LOGIN_URL
                  .replace("$FromEmail", apps.getFromEmail())
                  .replace("$SupportEmail", apps.getSupportEmail())
                  .replace("$AppName", apps.getAppName())
                  .replace("$ContactEmail", apps.getContactUsEmail())));
      dbServiceSubscriber.closeRealmObj(realm);
      startActivity(customTabsIntent.intent);
    } else {
      getAppsInfo();
    }
  }

  public void isUpgrade(boolean b, String latestVersion, final boolean force) {
    this.latestVersion = latestVersion;
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
      alertDialogBuilder =
          new AlertDialog.Builder(GatewayActivity.this, R.style.MyAlertDialogStyle);
      alertDialogBuilder.setTitle("Upgrade");
      alertDialogBuilder
          .setMessage(msg)
          .setCancelable(false)
          .setPositiveButton(
              positiveButton,
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  Bundle eventProperties = new Bundle();
                  eventProperties.putString(
                      CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                      getString(R.string.app_upgrade_ok));
                  analyticsInstance.logEvent(
                      CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
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
                  Bundle eventProperties = new Bundle();
                  eventProperties.putString(
                      CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                      getString(R.string.app_upgrade_cancel));
                  analyticsInstance.logEvent(
                      CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                  dialog.dismiss();
                  if (force) {
                    Toast.makeText(
                            GatewayActivity.this,
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
                  }
                }
              });
      alertDialog = alertDialogBuilder.create();
      alertDialog.show();
    }
  }

  private void getAppsInfo() {
    AppController.getHelperProgressDialog().showProgress(GatewayActivity.this, "", "", false);
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
}
