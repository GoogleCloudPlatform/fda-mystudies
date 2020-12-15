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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.AppFirebaseMessagingService;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.notificationmodule.AlarmReceiver;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.LogoutEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.util.HashMap;

public class StudyActivity extends AppCompatActivity
    implements View.OnClickListener, ApiCall.OnAsyncRequestComplete {

  private static final int NOTIFICATION_RESULT = 112;
  private RelativeLayout notificationBtn;
  private RelativeLayout infoIcon;
  private RelativeLayout filter;
  private RelativeLayout searchBtn;
  private RelativeLayout editBtnLayout;
  private DrawerLayout drawer;
  private AppCompatTextView titleFdaListens;
  private AppCompatTextView title;
  private AppCompatTextView sidebarTitle;
  private LinearLayout homeLayout;
  private AppCompatTextView homeLabel;
  private LinearLayout resourcesLayout;
  private AppCompatTextView resourceLabel;
  private LinearLayout reachoutLayout;
  private AppCompatTextView reachoutLabel;
  private LinearLayout signInProfileLayout;
  private AppCompatImageView signinImg;
  private AppCompatTextView signinLabel;
  private LinearLayout newUsrReachoutLayout;
  private AppCompatImageView newUsrReachoutImg;
  private AppCompatImageView notificationIcon;
  private AppCompatImageView notificatioStatus;
  private AppCompatTextView newUsrReachoutLabel;
  private AppCompatTextView signUpLabel;
  private RelativeLayout signOutLayout;
  private AppCompatTextView signOutLabel;
  private int previousValue = 0; // 0 means signup 1 means signout
  private static final int LOGOUT_REPSONSE_CODE = 100;
  private AppCompatTextView editTxt;
  private ProfileFragment profileFragment;
  private boolean isExit = false;
  private StudyFragment studyFragment;
  public static String FROM = "from";
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private RelativeLayout searchToolBarLayout;
  private RelativeLayout toolBarLayout;
  private AppCompatTextView cancel;
  private AppCompatEditText searchEditText;
  private RelativeLayout clearLayout;
  private String intentFrom = "";
  private BroadcastReceiver receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      isExit = false;
      setContentView(R.layout.activity_study);
      if (getIntent().getStringExtra(FROM) != null) {
        intentFrom = getIntent().getStringExtra(FROM);
      } else {
        intentFrom = "";
      }
      dbServiceSubscriber = new DbServiceSubscriber();
      realm = AppController.getRealmobj(this);
      initializeXmlId();
      bindEvents();
      setFont();
      // default settings
      loadstudylist();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
      Intent intent = new Intent(StudyActivity.this, StandaloneActivity.class);
      ComponentName cn = intent.getComponent();
      Intent mainIntent = Intent.makeRestartActivityTask(cn);
      startActivity(mainIntent);
      finish();
    } else {
      try {
        if (AppController.getHelperSharedPreference()
            .readPreference(this, getString(R.string.notification), "")
            .equalsIgnoreCase("true")) {
          notificationIcon.setImageResource(R.drawable.notification_white_active);
          notificatioStatus.setVisibility(View.VISIBLE);
        } else {
          notificationIcon.setImageResource(R.drawable.notification_white_active);
          notificatioStatus.setVisibility(View.GONE);
        }

        IntentFilter intentFilter = new IntentFilter("com.fda.notificationReceived");
        receiver =
            new BroadcastReceiver() {
              @Override
              public void onReceive(Context context, Intent intent) {
                notificationIcon.setImageResource(R.drawable.notification_white_active);
                notificatioStatus.setVisibility(View.VISIBLE);
              }
            };
        // registering our receiver
        this.registerReceiver(receiver, intentFilter);

      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      this.unregisterReceiver(this.receiver);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  protected void onNewIntent(Intent intent1) {
    super.onNewIntent(intent1);
    setIntent(intent1);
    if (intent1.getStringExtra(FROM) != null) {
      intentFrom = intent1.getStringExtra(FROM);
    } else {
      intentFrom = "";
    }
  }

  public void checkForNotification(Intent intent1) {
    if (!intentFrom.equalsIgnoreCase("")) {
      intentFrom = "";
      String type = intent1.getStringExtra(AppFirebaseMessagingService.TYPE);
      String subType = intent1.getStringExtra(AppFirebaseMessagingService.SUBTYPE);
      String studyId = intent1.getStringExtra(AppFirebaseMessagingService.STUDYID);

      String localNotification = "";
      if (intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION) != null) {
        localNotification = intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION);
      }
      String activityIdNotification = "";
      if (intent1.getStringExtra(AlarmReceiver.ACTIVITYID) != null) {
        activityIdNotification = intent1.getStringExtra(AlarmReceiver.ACTIVITYID);
      }

      if (!AppController.getHelperSharedPreference()
          .readPreference(StudyActivity.this, getResources().getString(R.string.userid), "")
          .equalsIgnoreCase("")) {
        if (type != null) {
          if (type.equalsIgnoreCase("Gateway")) {
            if (subType.equalsIgnoreCase("Study")) {
              Study study = dbServiceSubscriber.getStudyListFromDB(realm);
              if (study != null) {
                RealmList<StudyList> studyListArrayList = study.getStudies();
                studyListArrayList =
                    dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                boolean isStudyAvailable = false;
                for (int i = 0; i < studyListArrayList.size(); i++) {
                  if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                    try {
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.title),
                              "" + studyListArrayList.get(i).getTitle());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.bookmark),
                              "" + studyListArrayList.get(i).isBookmarked());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.status),
                              "" + studyListArrayList.get(i).getStatus());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.studyStatus),
                              "" + studyListArrayList.get(i).getStudyStatus());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this, getString(R.string.position), "" + i);
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.enroll),
                              "" + studyListArrayList.get(i).getSetting().isEnrolling());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.rejoin),
                              "" + studyListArrayList.get(i).getSetting().getRejoin());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.studyVersion),
                              "" + studyListArrayList.get(i).getStudyVersion());
                    } catch (Exception e) {
                      Logger.log(e);
                    }
                    if (studyListArrayList
                            .get(i)
                            .getStatus()
                            .equalsIgnoreCase(getString(R.string.active))
                        && studyListArrayList
                            .get(i)
                            .getStudyStatus()
                            .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                      Intent intent = new Intent(StudyActivity.this, SurveyActivity.class);
                      intent.putExtra("studyId", studyId);
                      startActivity(intent);
                    } else if (studyListArrayList
                        .get(i)
                        .getStatus()
                        .equalsIgnoreCase(getString(R.string.paused))) {
                      Toast.makeText(StudyActivity.this, R.string.study_paused, Toast.LENGTH_SHORT)
                          .show();
                    } else if (studyListArrayList
                        .get(i)
                        .getStatus()
                        .equalsIgnoreCase(getString(R.string.closed))) {
                      Toast.makeText(StudyActivity.this, R.string.study_resume, Toast.LENGTH_SHORT)
                          .show();
                    } else {
                      Intent intent = new Intent(getApplicationContext(), StudyInfoActivity.class);
                      intent.putExtra("studyId", studyListArrayList.get(i).getStudyId());
                      intent.putExtra("title", studyListArrayList.get(i).getTitle());
                      intent.putExtra("bookmark", studyListArrayList.get(i).isBookmarked());
                      intent.putExtra("status", studyListArrayList.get(i).getStatus());
                      intent.putExtra("studyStatus", studyListArrayList.get(i).getStudyStatus());
                      intent.putExtra("position", "" + i);
                      intent.putExtra(
                          "enroll", "" + studyListArrayList.get(i).getSetting().isEnrolling());
                      intent.putExtra(
                          "rejoin", "" + studyListArrayList.get(i).getSetting().getRejoin());
                      startActivity(intent);
                    }
                    isStudyAvailable = true;
                    break;
                  }
                }
                if (!isStudyAvailable) {
                  Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                      .show();
                }
              } else {
                Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                    .show();
              }
            } else if (subType.equalsIgnoreCase("Resource")) {
              previousValue = R.id.mResourcesLayout;
              titleFdaListens.setText("");
              title.setText(getResources().getString(R.string.resources));
              editBtnLayout.setVisibility(View.GONE);
              notificationBtn.setVisibility(View.GONE);
              filter.setVisibility(View.GONE);
              searchBtn.setVisibility(View.GONE);
              closeDrawer();
              getSupportFragmentManager()
                  .beginTransaction()
                  .replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment")
                  .commit();
            }
          } else if (type.equalsIgnoreCase("Study")) {
            if (subType.equalsIgnoreCase("Activity") || subType.equalsIgnoreCase("Resource")) {
              Study study = dbServiceSubscriber.getStudyListFromDB(realm);
              if (study != null) {
                RealmList<StudyList> studyListArrayList = study.getStudies();
                studyListArrayList =
                    dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                boolean isStudyAvailable = false;
                boolean isStudyJoined = false;
                for (int i = 0; i < studyListArrayList.size(); i++) {
                  if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                    isStudyAvailable = true;
                    try {
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.title),
                              "" + studyListArrayList.get(i).getTitle());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.bookmark),
                              "" + studyListArrayList.get(i).isBookmarked());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.status),
                              "" + studyListArrayList.get(i).getStatus());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.studyStatus),
                              "" + studyListArrayList.get(i).getStudyStatus());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this, getString(R.string.position), "" + i);
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.enroll),
                              "" + studyListArrayList.get(i).getSetting().isEnrolling());
                      AppController.getHelperSharedPreference()
                          .writePreference(
                              StudyActivity.this,
                              getString(R.string.rejoin),
                              "" + studyListArrayList.get(i).getSetting().getRejoin());
                    } catch (Exception e) {
                      Logger.log(e);
                    }
                    if (studyListArrayList
                            .get(i)
                            .getStatus()
                            .equalsIgnoreCase(getString(R.string.active))
                        && studyListArrayList
                            .get(i)
                            .getStudyStatus()
                            .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                      if (subType.equalsIgnoreCase("Resource")) {
                        studyFragment.getStudyUpdate(
                            studyListArrayList.get(i).getStudyId(),
                            studyListArrayList.get(i).getStudyVersion(),
                            studyListArrayList.get(i).getTitle(),
                            "Resource",
                            "NotificationActivity",
                            activityIdNotification,
                            localNotification);
                      } else {
                        studyFragment.getStudyUpdate(
                            studyListArrayList.get(i).getStudyId(),
                            studyListArrayList.get(i).getStudyVersion(),
                            studyListArrayList.get(i).getTitle(),
                            "",
                            "NotificationActivity",
                            activityIdNotification,
                            localNotification);
                      }
                      isStudyJoined = true;
                      break;
                    } else {
                      isStudyJoined = false;
                      break;
                    }
                  }
                }
                if (!isStudyAvailable) {
                  Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                      .show();
                } else if (!isStudyJoined) {
                  Toast.makeText(StudyActivity.this, R.string.studyNotJoined, Toast.LENGTH_SHORT)
                      .show();
                }
              } else {
                Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                    .show();
              }
            }
          }
        }
      } else {
        Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void initializeXmlId() {
    infoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
    editTxt = (AppCompatTextView) findViewById(R.id.editBtnLabel);
    editTxt.setVisibility(View.GONE);

    titleFdaListens = (AppCompatTextView) findViewById(R.id.mTitleFDAListens);
    title = (AppCompatTextView) findViewById(R.id.mTitle);
    sidebarTitle = (AppCompatTextView) findViewById(R.id.mSidebarTitle);
    notificationBtn = (RelativeLayout) findViewById(R.id.mNotificationBtn);
    editBtnLayout = (RelativeLayout) findViewById(R.id.editBtnLayout);
    drawer = (DrawerLayout) findViewById(R.id.activity_study);
    homeLayout = (LinearLayout) findViewById(R.id.mHomeLayout);
    homeLabel = (AppCompatTextView) findViewById(R.id.mHomeLabel);
    resourcesLayout = (LinearLayout) findViewById(R.id.mResourcesLayout);
    resourceLabel = (AppCompatTextView) findViewById(R.id.mResourceLabel);
    reachoutLayout = (LinearLayout) findViewById(R.id.mReachoutLayout);
    reachoutLabel = (AppCompatTextView) findViewById(R.id.mReachoutLabel);
    signInProfileLayout = (LinearLayout) findViewById(R.id.mSignInProfileLayout);
    signinImg = (AppCompatImageView) findViewById(R.id.signinImg);
    signinLabel = (AppCompatTextView) findViewById(R.id.mSigninLabel);
    newUsrReachoutLayout = (LinearLayout) findViewById(R.id.mNewUsrReachoutLayout);
    newUsrReachoutImg = (AppCompatImageView) findViewById(R.id.mNewUsrReachoutImg);
    notificationIcon = (AppCompatImageView) findViewById(R.id.mNotificationIcon);
    notificatioStatus = (AppCompatImageView) findViewById(R.id.notificatioStatus);
    newUsrReachoutLabel = (AppCompatTextView) findViewById(R.id.mNewUsrReachoutLabel);
    signUpLabel = (AppCompatTextView) findViewById(R.id.mSignUpLabel);
    signOutLayout = (RelativeLayout) findViewById(R.id.mSignOutLayout);
    signOutLabel = (AppCompatTextView) findViewById(R.id.mSignOutLabel);
    filter = (RelativeLayout) findViewById(R.id.mFilter);
    searchBtn = (RelativeLayout) findViewById(R.id.mSearchBtn);
    searchToolBarLayout = (RelativeLayout) findViewById(R.id.mSearchToolBarLayout);
    toolBarLayout = (RelativeLayout) findViewById(R.id.mToolBarLayout);
    cancel = (AppCompatTextView) findViewById(R.id.mCancel);
    clearLayout = (RelativeLayout) findViewById(R.id.mClearLayout);
    searchEditText = (AppCompatEditText) findViewById(R.id.mSearchEditText);

    TextView version = (TextView) findViewById(R.id.version);
    setVersion(version);
    RelativeLayout backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            checkSignOrSignOutScenario();
            openDrawer();
            try {
              AppController.getHelperHideKeyboard(StudyActivity.this);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
    filter.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(StudyActivity.this, FilterActivity.class);
            startActivityForResult(intent, 999);
          }
        });
    searchBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            toolBarLayout.setVisibility(View.GONE);
            searchToolBarLayout.setVisibility(View.VISIBLE);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            searchEditText.setText("");
            // forcecfully set focus
            searchEditText.post(
                new Runnable() {
                  @Override
                  public void run() {
                    searchEditText.requestFocus();
                    try {
                      InputMethodManager imm =
                          (InputMethodManager)
                              getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                      imm.toggleSoftInput(
                          InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } catch (Exception e) {
                      Logger.log(e);
                    }
                  }
                });
          }
        });

    searchEditText.addTextChangedListener(
        new TextWatcher() {

          public void afterTextChanged(Editable s) {}

          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
              clearLayout.setVisibility(View.VISIBLE);
            } else {
              clearLayout.setVisibility(View.INVISIBLE);
              studyFragment.setStudyFilteredStudyList();
            }
          }
        });

    searchEditText.setOnEditorActionListener(
        new TextView.OnEditorActionListener() {
          @Override
          public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
              if (searchEditText.getText().length() > 0) {
                studyFragment.searchFromFilteredStudyList(
                    searchEditText.getText().toString().trim());
                hideKeyboard();
              } else {
                Toast.makeText(
                        StudyActivity.this,
                        getResources().getString(R.string.please_enter_key),
                        Toast.LENGTH_LONG)
                    .show();
              }
              return true;
            }
            return false;
          }
        });

    clearLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            searchEditText.setText("");
            clearLayout.setVisibility(View.INVISIBLE);
            studyFragment.setStudyFilteredStudyList();
          }
        });

    cancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            searchEditText.setText("");
            setToolBarEnable();
            hideKeyboard();
            studyFragment.setStudyFilteredStudyList();
          }
        });

    notificationBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(StudyActivity.this, NotificationActivity.class);
            startActivityForResult(intent, NOTIFICATION_RESULT);
          }
        });
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

    infoIcon.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SetDialogHelper.setNeutralDialog(
                StudyActivity.this,
                getResources().getString(R.string.registration_message),
                false,
                getResources().getString(R.string.ok),
                getResources().getString(R.string.why_register));
          }
        });
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

  private void hideKeyboard() {
    try {
      InputMethodManager inputMethodManager =
          (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
      inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void setToolBarEnable() {
    searchToolBarLayout.setVisibility(View.GONE);
    toolBarLayout.setVisibility(View.VISIBLE);
    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
  }

  private void setFont() {
    try {
      editTxt.setTypeface(AppController.getTypeface(this, "medium"));
      titleFdaListens.setTypeface(AppController.getTypeface(this, "bold"));
      title.setTypeface(AppController.getTypeface(this, "bold"));
      sidebarTitle.setTypeface(AppController.getTypeface(this, "medium"));
      homeLabel.setTypeface(AppController.getTypeface(this, "medium"));
      resourceLabel.setTypeface(AppController.getTypeface(this, "medium"));
      reachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
      signinLabel.setTypeface(AppController.getTypeface(this, "medium"));
      newUsrReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
      signUpLabel.setTypeface(AppController.getTypeface(this, "medium"));
      signOutLabel.setTypeface(AppController.getTypeface(this, "medium"));
      searchEditText.setTypeface(AppController.getTypeface(this, "regular"));
      cancel.setTypeface(AppController.getTypeface(this, "medium"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    homeLayout.setOnClickListener(this);
    resourcesLayout.setOnClickListener(this);
    reachoutLayout.setOnClickListener(this);
    signInProfileLayout.setOnClickListener(this);
    newUsrReachoutLayout.setOnClickListener(this);
    signOutLayout.setOnClickListener(this);
  }

  private void checkSignOrSignOutScenario() {
    // signIn
    if (AppController.getHelperSharedPreference()
        .readPreference(StudyActivity.this, getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      signinImg.setBackground(getResources().getDrawable(R.drawable.signin_menu1));
      signinLabel.setText(getResources().getString(R.string.sign_in_btn));
      signOutLayout.setVisibility(View.GONE);
      reachoutLayout.setVisibility(View.VISIBLE);
      // set Reach out details to new user,
      newUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.newuser_menu1));
      newUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_new_user));
      signUpLabel.setVisibility(View.VISIBLE);
      notificationIcon.setImageResource(R.drawable.notification_white_active);
      notificatioStatus.setVisibility(View.GONE);
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

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.mHomeLayout:
        previousValue = R.id.mHomeLayout;
        titleFdaListens.setText(getResources().getString(R.string.app_name));
        title.setText("");
        editBtnLayout.setVisibility(View.GONE);
        notificationBtn.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        searchBtn.setVisibility(View.VISIBLE);
        infoIcon.setVisibility(View.GONE);

        try {
          if (AppController.getHelperSharedPreference()
              .readPreference(this, getString(R.string.notification), "")
              .equalsIgnoreCase("true")) {
            notificationIcon.setImageResource(R.drawable.notification_white_active);
            notificatioStatus.setVisibility(View.VISIBLE);
          } else {
            notificationIcon.setImageResource(R.drawable.notification_white_active);
            notificatioStatus.setVisibility(View.GONE);
          }

        } catch (Exception e) {
          Logger.log(e);
        }

        closeDrawer();
        studyFragment = new StudyFragment();
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, studyFragment, "fragment")
            .commit();
        break;

      case R.id.mResourcesLayout:
        if (previousValue == R.id.mResourcesLayout) {
          closeDrawer();
        } else {
          previousValue = R.id.mResourcesLayout;
          titleFdaListens.setText("");
          title.setText(getResources().getString(R.string.resources));
          editBtnLayout.setVisibility(View.GONE);
          notificationBtn.setVisibility(View.GONE);
          filter.setVisibility(View.GONE);
          searchBtn.setVisibility(View.GONE);
          closeDrawer();
          getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment")
              .commit();
        }
        break;

      case R.id.mReachoutLayout:
        reachoutMenuClicked();
        break;

      case R.id.mSignInProfileLayout:
        if (AppController.getHelperSharedPreference()
            .readPreference(StudyActivity.this, getString(R.string.userid), "")
            .equalsIgnoreCase("")) {
          closeDrawer();
          SharedPreferenceHelper.writePreference(
              StudyActivity.this, getString(R.string.loginflow), "SideMenu");
          SharedPreferenceHelper.writePreference(
              StudyActivity.this, getString(R.string.logintype), "signIn");
          CustomTabsIntent customTabsIntent =
              new CustomTabsIntent.Builder()
                  .setToolbarColor(getResources().getColor(R.color.colorAccent))
                  .setShowTitle(true)
                  .setCloseButtonIcon(
                      BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
                  .setStartAnimations(
                      StudyActivity.this, R.anim.slide_in_right, R.anim.slide_out_left)
                  .setExitAnimations(
                      StudyActivity.this, R.anim.slide_in_left, R.anim.slide_out_right)
                  .build();
          customTabsIntent.intent.setData(Uri.parse(Urls.LOGIN_URL));
          startActivity(customTabsIntent.intent);
        } else {
          if (previousValue == R.id.mSignInProfileLayout) {
            closeDrawer();
          } else {
            previousValue = R.id.mSignInProfileLayout;
            titleFdaListens.setText("");
            title.setText(getResources().getString(R.string.profile));
            editBtnLayout.setVisibility(View.VISIBLE);
            notificationBtn.setVisibility(View.GONE);
            filter.setVisibility(View.GONE);
            searchBtn.setVisibility(View.GONE);
            infoIcon.setVisibility(View.GONE);
            profileFragment = new ProfileFragment();

            editBtnLayout.setOnClickListener(
                new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                    if (editTxt
                        .getText()
                        .toString()
                        .equalsIgnoreCase(getResources().getString(R.string.edit))) {
                      enableEditText();
                    } else if (editTxt
                        .getText()
                        .toString()
                        .equalsIgnoreCase(getResources().getString(R.string.cancel))) {
                      disableEditText();
                    }
                  }
                });
            closeDrawer();
            profileFragment = new ProfileFragment();
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayoutContainer, profileFragment, "fragment")
                .commit();
          }
        }
        break;

      case R.id.mNewUsrReachoutLayout:
        previousValue = R.id.mNewUsrReachoutLayout;
        if (AppController.getHelperSharedPreference()
            .readPreference(StudyActivity.this, getString(R.string.userid), "")
            .equalsIgnoreCase("")) {
          titleFdaListens.setText("");
          title.setText(getResources().getString(R.string.signup));
          editBtnLayout.setVisibility(View.GONE);
          notificationBtn.setVisibility(View.GONE);
          filter.setVisibility(View.GONE);
          searchBtn.setVisibility(View.GONE);
          infoIcon.setVisibility(View.GONE);
          closeDrawer();
          getSupportFragmentManager()
              .beginTransaction()
              .replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment")
              .commit();
        } else {
          // SignOut Reach out menu click
          reachoutMenuClicked();
        }
        break;
      case R.id.mSignOutLayout:
        closeDrawer();
        logout();
        break;
    }
  }

  private boolean checkOfflineDataEmpty() {
    RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(realm);
    if (results == null || results.size() == 0) {
      return true;
    } else {
      return false;
    }
  }

  private void reachoutMenuClicked() {
    if (previousValue == R.id.mReachoutLayout) {
      closeDrawer();
    } else {
      previousValue = R.id.mReachoutLayout;
      titleFdaListens.setText("");
      title.setText(getResources().getString(R.string.reachout));
      editBtnLayout.setVisibility(View.GONE);
      notificationBtn.setVisibility(View.GONE);
      filter.setVisibility(View.GONE);
      searchBtn.setVisibility(View.GONE);
      closeDrawer();
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.frameLayoutContainer, new ReachoutFragment(), "fragment")
          .commit();
    }
  }

  private void enableEditText() {
    editTxt.setText(getResources().getString(R.string.cancel));
    profileFragment.enableEditText();
  }

  private void disableEditText() {
    editTxt.setText(getResources().getString(R.string.edit));
    profileFragment.disableEditText();
  }

  public void disableEditTextFromFragment() {
    editTxt.setText(getResources().getString(R.string.edit));
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
                    .showProgress(StudyActivity.this, "", "", false);
                HashMap<String, String> params = new HashMap<>();

                HashMap<String, String> header = new HashMap<String, String>();
                header.put(
                    "Authorization",
                    "Bearer "
                        + SharedPreferenceHelper.readPreference(
                            StudyActivity.this, getString(R.string.auth), ""));
                header.put("correlationId", "" + FdaApplication.getRandomString());
                header.put("appId", "" + BuildConfig.APP_ID);
                header.put("mobilePlatform", "ANDROID");

                AuthServerConfigEvent authServerConfigEvent =
                    new AuthServerConfigEvent(
                        "post",
                        Urls.AUTH_SERVICE
                            + "/"
                            + SharedPreferenceHelper.readPreference(
                                StudyActivity.this, getString(R.string.userid), "")
                            + Urls.LOGOUT,
                        LOGOUT_REPSONSE_CODE,
                        StudyActivity.this,
                        LoginData.class,
                        params,
                        header,
                        null,
                        false,
                        StudyActivity.this);
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

  private void openDrawer() {
    drawer.openDrawer(GravityCompat.START);
  }

  private void closeDrawer() {
    drawer.closeDrawer(GravityCompat.START);
  }

  @Override
  public void onBackPressed() {
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
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

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == LOGOUT_REPSONSE_CODE) {
      Toast.makeText(this, getResources().getString(R.string.signed_out), Toast.LENGTH_SHORT)
          .show();
      SharedPreferences settings = SharedPreferenceHelper.getPreferences(StudyActivity.this);
      settings.edit().clear().apply();
      // delete passcode from keystore
      String pass = AppController.refreshKeys("passcode");
      if (pass != null) {
        AppController.deleteKey("passcode_" + pass);
      }

      try {
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancleActivityLocalNotification(StudyActivity.this);
        notificationModuleSubscriber.cancleResourcesLocalNotification(StudyActivity.this);
      } catch (Exception e) {
        Logger.log(e);
      }
      // Call AsyncTask
      new ClearNotification().execute();

    } else {
      AppController.getHelperProgressDialog().dismissDialog();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(StudyActivity.this, errormsg);
    } else {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  public void loadstudylist() {
    checkSignOrSignOutScenario();
    previousValue = R.id.mHomeLayout;
    titleFdaListens.setText(getResources().getString(R.string.app_name));
    title.setText("");
    editBtnLayout.setVisibility(View.GONE);
    notificationBtn.setVisibility(View.VISIBLE);
    filter.setVisibility(View.VISIBLE);
    searchBtn.setVisibility(View.VISIBLE);
    infoIcon.setVisibility(View.GONE);
    closeDrawer();
    studyFragment = new StudyFragment();
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.frameLayoutContainer, studyFragment, "fragment")
        .commit();
  }

  public void loadsignup() {
    checkSignOrSignOutScenario();
    previousValue = R.id.mNewUsrReachoutLayout;
    titleFdaListens.setText("");
    title.setText(getResources().getString(R.string.signup));
    editBtnLayout.setVisibility(View.GONE);
    notificationBtn.setVisibility(View.GONE);
    filter.setVisibility(View.GONE);
    searchBtn.setVisibility(View.GONE);
    closeDrawer();
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment")
        .commit();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 100) {
      if (resultCode == RESULT_OK) {
        if (data != null && data.getStringExtra("action").equalsIgnoreCase("signin")) {
          new LongOperation().execute();
        } else {
          loadstudylist();
        }
      }
    } else if (requestCode == NOTIFICATION_RESULT) {
      if (resultCode == RESULT_OK) {
        previousValue = R.id.mResourcesLayout;
        titleFdaListens.setText("");
        title.setText(getResources().getString(R.string.resources));
        editBtnLayout.setVisibility(View.GONE);
        notificationBtn.setVisibility(View.GONE);
        filter.setVisibility(View.GONE);
        searchBtn.setVisibility(View.GONE);
        closeDrawer();
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment")
            .commit();
      }
    }
  }

  private class LongOperation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        Thread.interrupted();
      }
      return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
      AppController.getHelperProgressDialog().dismissDialog();
      previousValue = R.id.mSignInProfileLayout;
      titleFdaListens.setText("");
      title.setText(getResources().getString(R.string.sign_in));
      editBtnLayout.setVisibility(View.GONE);
      notificationBtn.setVisibility(View.GONE);
      filter.setVisibility(View.GONE);
      searchBtn.setVisibility(View.GONE);
      infoIcon.setVisibility(View.GONE);
      closeDrawer();
      SharedPreferenceHelper.writePreference(
          StudyActivity.this, getString(R.string.loginflow), "SideMenu");
      SharedPreferenceHelper.writePreference(
          StudyActivity.this, getString(R.string.logintype), "signIn");
      CustomTabsIntent customTabsIntent =
          new CustomTabsIntent.Builder()
              .setToolbarColor(getResources().getColor(R.color.colorAccent))
              .setShowTitle(true)
              .setCloseButtonIcon(
                  BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
              .setStartAnimations(StudyActivity.this, R.anim.slide_in_right, R.anim.slide_out_left)
              .setExitAnimations(StudyActivity.this, R.anim.slide_in_left, R.anim.slide_out_right)
              .build();
      customTabsIntent.intent.setData(Uri.parse(Urls.LOGIN_URL));
      startActivity(customTabsIntent.intent);
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(StudyActivity.this, "", "", false);
    }
  }

  private class ClearNotification extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
      try {
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancleActivityLocalNotification(StudyActivity.this);
        notificationModuleSubscriber.cancleResourcesLocalNotification(StudyActivity.this);
      } catch (Exception e) {
        Logger.log(e);
      }
      dbServiceSubscriber.deleteDb(StudyActivity.this);

      return "";
    }

    @Override
    protected void onPostExecute(String result) {
      AppController.getHelperProgressDialog().dismissDialog();
      // clear notifications from notification tray
      NotificationManagerCompat notificationManager =
          NotificationManagerCompat.from(StudyActivity.this);
      notificationManager.cancelAll();
      Toast.makeText(StudyActivity.this, R.string.signed_out, Toast.LENGTH_SHORT).show();
      loadstudylist();
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(StudyActivity.this, "", "", false);
    }
  }

  @Override
  protected void onDestroy() {
    if (dbServiceSubscriber != null && realm != null) {
      dbServiceSubscriber.closeRealmObj(realm);
    }
    super.onDestroy();
  }

  public String getSearchKey() {
    String key = null;
    try {
      if (searchEditText != null && searchEditText.getText().length() > 0) {
        key = searchEditText.getText().toString();
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    return key;
  }
}
