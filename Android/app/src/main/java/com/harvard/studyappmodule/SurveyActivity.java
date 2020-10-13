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

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.LogoutEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.HashMap;

public class SurveyActivity extends AppCompatActivity
    implements View.OnClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        ApiCall.OnAsyncRequestComplete {
  private RelativeLayout dashboardButtonLayout;
  private AppCompatImageView dashboardButton;
  private AppCompatTextView dashboardButtonLabel;
  private RelativeLayout activitiesButtonLayout;
  private AppCompatImageView activitiesButton;
  private AppCompatTextView activitiesButtonLabel;
  private RelativeLayout resourcesButtonLayout;
  private AppCompatImageView resourcesButton;
  private AppCompatTextView resourcesButtonLabel;
  private String studyId;
  public String from;
  public String to;
  private SurveyDashboardFragment surveyDashboardFragment;
  private SurveyActivitiesFragment surveyActivitiesFragment;
  private SurveyResourcesFragment surveyResourcesFragment;
  private static final int LOGOUT_REPSONSECODE = 100;
  private String title;
  private boolean bookmark;
  private String status;
  private String studyStatus;
  private String position;
  private String enroll;
  private String rejoin;
  public String activityId = "";
  public String localNotification = "";
  private LinearLayout menulayout;
  private DrawerLayout drawer;
  private LinearLayout reachoutLayout;
  private AppCompatImageView signinImg;
  private AppCompatTextView signinLabel;
  private AppCompatImageView newUsrReachoutImg;
  private AppCompatTextView signUpLabel;
  private RelativeLayout signOutLayout;
  private AppCompatTextView newUsrReachoutLabel;
  private int previousValue = 0;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private Toolbar toolbar;
  private boolean isExit = false;
  private TextView menutitle;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);
    initializeXmlId();
    bindEvents();
    // default settings

    isExit = false;
    surveyDashboardFragment = new SurveyDashboardFragment();
    surveyActivitiesFragment = new SurveyActivitiesFragment();
    surveyResourcesFragment = new SurveyResourcesFragment();

    studyId = getIntent().getStringExtra("studyId");
    activityId = "";
    localNotification = "";
    from = "";
    to = "";

    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    checkSignOrSignOutScenario();

    if (getIntent().getStringExtra("from") != null
        && getIntent().getStringExtra("from").equalsIgnoreCase("NotificationActivity")) {
      from = "NotificationActivity";
      if (getIntent().getStringExtra("activityId") != null) {
        activityId = getIntent().getStringExtra("activityId");
      }
      if (getIntent().getStringExtra("localNotification") != null) {
        localNotification = getIntent().getStringExtra("localNotification");
      }
      if (getIntent().getStringExtra("to") != null
          && getIntent().getStringExtra("to").equalsIgnoreCase("Activity")) {
        defaultFragementSettings();
      } else if (getIntent().getStringExtra("to") != null
          && getIntent().getStringExtra("to").equalsIgnoreCase("Resource")) {
        openResources();
      } else {
        defaultFragementSettings();
      }
    } else {
      defaultFragementSettings();
    }

    try {
      title =
          AppController.getHelperSharedPreference()
              .readPreference(SurveyActivity.this, getResources().getString(R.string.title), "");
      if (AppController.getHelperSharedPreference()
          .readPreference(SurveyActivity.this, getResources().getString(R.string.bookmark), "")
          .equalsIgnoreCase("true")) {
        bookmark = true;
      } else {
        bookmark = false;
      }
      status =
          AppController.getHelperSharedPreference()
              .readPreference(SurveyActivity.this, getResources().getString(R.string.status), "");
      studyStatus =
          AppController.getHelperSharedPreference()
              .readPreference(
                  SurveyActivity.this, getResources().getString(R.string.studyStatus), "");
      position =
          AppController.getHelperSharedPreference()
              .readPreference(SurveyActivity.this, getResources().getString(R.string.position), "");
      enroll =
          AppController.getHelperSharedPreference()
              .readPreference(SurveyActivity.this, getResources().getString(R.string.enroll), "");
      rejoin =
          AppController.getHelperSharedPreference()
              .readPreference(SurveyActivity.this, getResources().getString(R.string.rejoin), "");
    } catch (Exception e) {
      Logger.log(e);
    }

    drawer = findViewById(R.id.survey_menu);
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    } else {
      drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    drawer.addDrawerListener(
        new DrawerLayout.DrawerListener() {
          @Override
          public void onDrawerSlide(View drawerView, float slideOffset) {}

          @Override
          public void onDrawerOpened(View drawerView) {
            checkSignOrSignOutScenario();
          }

          @Override
          public void onDrawerClosed(View drawerView) {}

          @Override
          public void onDrawerStateChanged(int newState) {}
        });
  }

  private void checkSignOrSignOutScenario() {
    // signIn
    if (AppController.getHelperSharedPreference()
        .readPreference(SurveyActivity.this, getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      signinImg.setBackground(getResources().getDrawable(R.drawable.signin_menu1));
      signinLabel.setText(getResources().getString(R.string.sign_in_btn));
      signOutLayout.setVisibility(View.GONE);
      reachoutLayout.setVisibility(View.VISIBLE);
      // set Reach out details to new user,
      newUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.newuser_menu1));
      newUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_new_user));
      signUpLabel.setVisibility(View.VISIBLE);
    } else {
      // Sign out
      signinImg.setBackground(getResources().getDrawable(R.drawable.profile_menu1));
      signinLabel.setText(getResources().getString(R.string.profile_small));
      signOutLayout.setVisibility(View.VISIBLE);
      reachoutLayout.setVisibility(View.GONE);
      // set Reach out details to new user,
      newUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.reachout_menu1));
      newUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_reach_out));
      signUpLabel.setVisibility(View.GONE);
    }
  }

  private void initializeXmlId() {
    dashboardButtonLayout = (RelativeLayout) findViewById(R.id.myDashboardButtonLayout);
    dashboardButton = (AppCompatImageView) findViewById(R.id.myDashboardButton);
    dashboardButtonLabel = (AppCompatTextView) findViewById(R.id.myDashboardButtonLabel);
    activitiesButtonLayout = (RelativeLayout) findViewById(R.id.mActivitiesButtonLayout);
    activitiesButton = (AppCompatImageView) findViewById(R.id.mActivitiesButton);
    activitiesButtonLabel = (AppCompatTextView) findViewById(R.id.mActivitiesButtonLabel);
    resourcesButtonLayout = (RelativeLayout) findViewById(R.id.mResourcesButtonLayout);
    resourcesButton = (AppCompatImageView) findViewById(R.id.mResourcesButton);
    resourcesButtonLabel = (AppCompatTextView) findViewById(R.id.mResourcesButtonLabel);

    menulayout = (LinearLayout) findViewById(R.id.menulayout);

    reachoutLayout = (LinearLayout) findViewById(R.id.mReachoutLayout);

    signinImg = (AppCompatImageView) findViewById(R.id.signinImg);
    signinLabel = (AppCompatTextView) findViewById(R.id.mSigninLabel);
    newUsrReachoutImg = (AppCompatImageView) findViewById(R.id.mNewUsrReachoutImg);
    newUsrReachoutLabel = (AppCompatTextView) findViewById(R.id.mNewUsrReachoutLabel);
    signUpLabel = (AppCompatTextView) findViewById(R.id.mSignUpLabel);
    signOutLayout = (RelativeLayout) findViewById(R.id.mSignOutLayout);
    menutitle = findViewById(R.id.menutitle);

    AppCompatTextView homeLabel = (AppCompatTextView) findViewById(R.id.mHomeLabel);
    homeLabel.setTypeface(AppController.getTypeface(this, "medium"));
    AppCompatTextView resourceLabel = (AppCompatTextView) findViewById(R.id.mResourceLabel);
    resourceLabel.setTypeface(AppController.getTypeface(this, "medium"));
    AppCompatTextView reachoutLabel = (AppCompatTextView) findViewById(R.id.mReachoutLabel);
    reachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
    AppCompatTextView signOutLabel = (AppCompatTextView) findViewById(R.id.mSignOutLabel);
    signOutLabel.setTypeface(AppController.getTypeface(this, "medium"));
    signinLabel.setTypeface(AppController.getTypeface(this, "medium"));
    newUsrReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
    signUpLabel.setTypeface(AppController.getTypeface(this, "medium"));

    RelativeLayout menu = findViewById(R.id.menu);
    menu.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            openDrawer();
          }
        });

    toolbar = findViewById(R.id.toolbar);
    toolbar.setVisibility(View.GONE);
    LinearLayout homeLayout = (LinearLayout) findViewById(R.id.mHomeLayout);
    homeLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            menulayout.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            closeDrawer();
            if (previousValue != R.id.mHomeLayout) {
              previousValue = R.id.mHomeLayout;
              defaultFragementSettings();
            }
          }
        });
    LinearLayout resourcesLayout = (LinearLayout) findViewById(R.id.mResourcesLayout);
    resourcesLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            menulayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            menutitle.setText(R.string.resources);
            closeDrawer();
            if (previousValue != R.id.mResourcesLayout) {
              previousValue = R.id.mResourcesLayout;
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment")
                  .commit();
            }
          }
        });
    LinearLayout signInProfileLayout = (LinearLayout) findViewById(R.id.mSignInProfileLayout);
    signInProfileLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            menulayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);

            closeDrawer();
            if (previousValue != R.id.mSignInProfileLayout) {
              previousValue = R.id.mSignInProfileLayout;
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.frameLayoutContainer, new ProfileFragment(), "fragment")
                  .commit();
              menutitle.setText(R.string.profile);
            }
          }
        });
    LinearLayout newUsrReachoutLayout = (LinearLayout) findViewById(R.id.mNewUsrReachoutLayout);
    newUsrReachoutLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            menulayout.setVisibility(View.GONE);
            toolbar.setVisibility(View.VISIBLE);
            closeDrawer();
            if (previousValue != R.id.mNewUsrReachoutLayout) {
              previousValue = R.id.mNewUsrReachoutLayout;
              if (AppController.getHelperSharedPreference()
                  .readPreference(SurveyActivity.this, getString(R.string.userid), "")
                  .equalsIgnoreCase("")) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment")
                    .commit();
                menutitle.setText(R.string.sign_up);
              } else {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutContainer, new ReachoutFragment(), "fragment")
                    .commit();
                menutitle.setText(R.string.reachout);
              }
            }
          }
        });
    signOutLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            closeDrawer();
            if (previousValue != R.id.mSignOutLayout) {
              previousValue = R.id.mSignOutLayout;
              logout();
            }
          }
        });

    TextView version = (TextView) findViewById(R.id.version);
    setVersion(version);
  }

  private void logout() {
    AlertDialog.Builder alertDialogBuilder =
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

    alertDialogBuilder.setTitle(getResources().getString(R.string.sign_out));
    String message;
    if (checkOfflineDataEmpty()) {
      message = getResources().getString(R.string.sign_out_message);
    } else {
      message = getResources().getString(R.string.sign_out_message_data_lost);
    }

    alertDialogBuilder
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            getResources().getString(R.string.sign_out),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {

                AppController.getHelperProgressDialog()
                    .showProgress(SurveyActivity.this, "", "", false);

                HashMap<String, String> params = new HashMap<>();

                HashMap<String, String> header = new HashMap<String, String>();
                header.put(
                    "Authorization",
                    "Bearer "
                        + SharedPreferenceHelper.readPreference(
                            SurveyActivity.this, getString(R.string.auth), ""));
                header.put("correlationId", "" + FdaApplication.getRandomString());
                header.put("appId", "" + BuildConfig.APP_ID);
                header.put("mobilePlatform", "ANDROID");

                AuthServerConfigEvent authServerConfigEvent =
                    new AuthServerConfigEvent(
                        "post",
                        Urls.AUTH_SERVICE
                            + "/"
                            + SharedPreferenceHelper.readPreference(
                                SurveyActivity.this, getString(R.string.userid), "")
                            + Urls.LOGOUT,
                        LOGOUT_REPSONSECODE,
                        SurveyActivity.this,
                        LoginData.class,
                        params,
                        header,
                        null,
                        false,
                        SurveyActivity.this);
                LogoutEvent logoutEvent = new LogoutEvent();
                logoutEvent.setAuthServerConfigEvent(authServerConfigEvent);
                UserModulePresenter userModulePresenter = new UserModulePresenter();
                userModulePresenter.performLogout(logoutEvent);
              }
            });

    alertDialogBuilder.setNegativeButton(
        getResources().getString(R.string.cancel),
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }

  private boolean checkOfflineDataEmpty() {
    RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(realm);
    if (results == null || results.size() == 0) {
      return true;
    } else {
      return false;
    }
  }

  public void openDrawer() {
    drawer.openDrawer(GravityCompat.START);
  }

  private void closeDrawer() {
    drawer.closeDrawer(GravityCompat.START);
  }

  public void setVersion(TextView version) {
    try {
      PackageInfo info =
          getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
      version.append("" + info.versionName);
    } catch (PackageManager.NameNotFoundException e) {
      Logger.log(e);
      version.setText("");
    }
  }

  private void bindEvents() {
    dashboardButtonLayout.setOnClickListener(this);
    activitiesButtonLayout.setOnClickListener(this);
    resourcesButtonLayout.setOnClickListener(this);
  }

  private void defaultFragementSettings() {
    menulayout.setVisibility(View.VISIBLE);
    toolbar.setVisibility(View.GONE);

    dashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
    activitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
    resourcesButton.setBackgroundResource(R.drawable.resources_grey);
    dashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
    activitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
    resourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.frameLayoutContainer, surveyActivitiesFragment, "fragment")
        .commit();
  }

  private void openResources() {
    dashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
    activitiesButton.setBackgroundResource(R.drawable.activities_grey);
    resourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
    dashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
    activitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
    resourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.frameLayoutContainer, surveyResourcesFragment, "fragment")
        .commit();
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.myDashboardButtonLayout:
        dashboardButton.setBackgroundResource(R.drawable.dashboard_blue_active);
        activitiesButton.setBackgroundResource(R.drawable.activities_grey);
        resourcesButton.setBackgroundResource(R.drawable.resources_grey);
        dashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        activitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        resourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, surveyDashboardFragment, "fragment")
            .commit();
        break;

      case R.id.mActivitiesButtonLayout:
        dashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        activitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
        resourcesButton.setBackgroundResource(R.drawable.resources_grey);
        dashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        activitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        resourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, surveyActivitiesFragment, "fragment")
            .commit();
        break;

      case R.id.mResourcesButtonLayout:
        dashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        activitiesButton.setBackgroundResource(R.drawable.activities_grey);
        resourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
        dashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        activitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        resourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, surveyResourcesFragment, "fragment")
            .commit();
        break;
    }
  }

  @Override
  public void onBackPressed() {
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      Intent intent = new Intent(SurveyActivity.this, StudyActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      startActivity(mainIntent);
      finish();
    } else if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else if (previousValue != R.id.mHomeLayout) {
      previousValue = R.id.mHomeLayout;
      defaultFragementSettings();
    } else {
      if (isExit) {
        finish();
      } else {
        Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
        isExit = true;
        new Handler()
            .postDelayed(
                new Runnable() {
                  @Override
                  public void run() {
                    isExit = false;
                  }
                },
                3000);
      }
    }
  }

  public String getStudyId() {
    return studyId;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 1000) {
      if (surveyActivitiesFragment != null) {
        surveyActivitiesFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    } else if (requestCode == 2000) {
      if (surveyDashboardFragment != null) {
        surveyDashboardFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
      }
    }
  }

  public String getTitle1() {
    return title;
  }

  public boolean getBookmark() {
    return bookmark;
  }

  public String getStatus() {
    return status;
  }

  public String getStudyStatus() {
    return studyStatus;
  }

  public String getPosition() {
    return position;
  }

  public String getEnroll() {
    return enroll;
  }

  public String getRejoin() {
    return rejoin;
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == LOGOUT_REPSONSECODE) {
      Toast.makeText(this, getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT)
          .show();
      SharedPreferences settings = SharedPreferenceHelper.getPreferences(SurveyActivity.this);
      settings.edit().clear().apply();
      // delete passcode from keystore
      String pass = AppController.refreshKeys("passcode");
      if (pass != null) {
        AppController.deleteKey("passcode_" + pass);
      }

      try {
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancleActivityLocalNotification(SurveyActivity.this);
        notificationModuleSubscriber.cancleResourcesLocalNotification(SurveyActivity.this);
      } catch (Exception e) {
        Logger.log(e);
      }
      // Call AsyncTask
      new ClearNotification().execute();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {}

  private class ClearNotification extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      try {
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancleActivityLocalNotification(SurveyActivity.this);
        notificationModuleSubscriber.cancleResourcesLocalNotification(SurveyActivity.this);
      } catch (Exception e) {
        Logger.log(e);
      }
      dbServiceSubscriber.deleteDb(SurveyActivity.this);

      return "";
    }

    @Override
    protected void onPostExecute(String result) {
      AppController.getHelperProgressDialog().dismissDialog();
      // clear notifications from notification tray
      NotificationManagerCompat notificationManager =
          NotificationManagerCompat.from(SurveyActivity.this);
      notificationManager.cancelAll();
      Toast.makeText(SurveyActivity.this, R.string.signed_out, Toast.LENGTH_SHORT).show();
      signout();
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(SurveyActivity.this, "", "", false);
    }
  }

  public void signout() {
    Intent intent = new Intent(SurveyActivity.this, StandaloneStudyInfoActivity.class);
    ComponentName cn = intent.getComponent();
    Intent mainIntent = Intent.makeRestartActivityTask(cn);
    startActivity(mainIntent);
    finish();
  }
}
