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
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.WebViewActivity;
import com.harvard.eligibilitymodule.CustomViewTaskActivity;
import com.harvard.eligibilitymodule.StepsBuilder;
import com.harvard.gatewaymodule.CircleIndicator;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.ParticipantDatastoreEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.task.OrderedTask;

public class StudyInfoActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
  private static final int STUDY_INFO = 10;
  private static final int UPDATE_PREFERENCES = 11;
  private static final int GET_CONSENT_DOC = 12;
  private static final int JOIN_ACTION_SIGIN = 100;
  private static final int GET_PREFERENCES = 101;
  private RelativeLayout backBtn;
  private RelativeLayout rightBtn;
  private AppCompatImageView bookmarkimage;
  private boolean bookmarked = false;
  private String studyId = "";
  private String status = "";
  private String studyStatus = "";
  private String position = "";
  private String title = "";
  private String enroll = "";
  private String rejoin = "";
  private AppCompatTextView joinButton;
  private StudyHome studyHome;
  private ConsentDocumentData consentDocumentData;
  private AppCompatTextView visitWebsiteButton;
  private AppCompatTextView learnMoreButton;
  private AppCompatTextView consentLayButton;
  private LinearLayout bottomBar;
  private LinearLayout bottomBar1;
  private RelativeLayout consentLay;
  private boolean aboutThisStudy;
  private int deleteIndexNumberDb;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private EligibilityConsent eligibilityConsent;
  private RealmList<Studies> userPreferenceStudies;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_study_info);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);

    initializeXmlId();
    setFont();
    bindEvents();

    try {
      studyId = getIntent().getStringExtra("studyId");
      status = getIntent().getStringExtra("status");
      studyStatus = getIntent().getStringExtra("studyStatus");
      position = getIntent().getStringExtra("position");
      title = getIntent().getStringExtra("title");
      bookmarked = getIntent().getBooleanExtra("bookmark", false);
      enroll = getIntent().getStringExtra("enroll");
      rejoin = getIntent().getStringExtra("rejoin");
      aboutThisStudy = getIntent().getBooleanExtra("about_this_study", false);
    } catch (Exception e) {
      Logger.log(e);
    }

    if (AppController.getHelperSharedPreference()
        .readPreference(this, getResources().getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      bookmarkimage.setVisibility(View.GONE);
      rightBtn.setClickable(false);
    }
    if (bookmarked) {
      bookmarkimage.setImageResource(R.drawable.star_yellow_big);
    } else {
      bookmarkimage.setImageResource(R.drawable.star_grey_big);
    }
    AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
    callGetStudyInfoWebservice();

    if (status.equalsIgnoreCase(getString(R.string.upcoming))
        || status.equalsIgnoreCase(getString(R.string.closed))) {
      joinButton.setVisibility(View.GONE);
    }

    if (status.equalsIgnoreCase(getString(R.string.closed))) {
      bookmarkimage.setVisibility(View.GONE);
    }

    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
      bookmarkimage.setVisibility(View.GONE);
      backBtn.setVisibility(View.VISIBLE);
    }
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    rightBtn = (RelativeLayout) findViewById(R.id.rightBtn);
    bookmarkimage = (AppCompatImageView) findViewById(R.id.imageViewRight);
    joinButton = (AppCompatTextView) findViewById(R.id.joinButton);
    visitWebsiteButton = (AppCompatTextView) findViewById(R.id.mVisitWebsiteButton);
    learnMoreButton = (AppCompatTextView) findViewById(R.id.mLernMoreButton);
    consentLayButton = (AppCompatTextView) findViewById(R.id.consentLayButton);
    bottomBar = (LinearLayout) findViewById(R.id.bottom_bar);
    bottomBar1 = (LinearLayout) findViewById(R.id.bottom_bar1);
    consentLay = (RelativeLayout) findViewById(R.id.consentLay);
  }

  private void setFont() {
    joinButton.setTypeface(AppController.getTypeface(this, "regular"));
    visitWebsiteButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
    learnMoreButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
    consentLayButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            backClicked();
          }
        });

    rightBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (bookmarked) {
              updatebookmark(false);
            } else {
              updatebookmark(true);
            }
          }
        });

    joinButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            if (AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.userid), "")
                .equalsIgnoreCase("")) {
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this, getString(R.string.loginflow), "StudyInfo");
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this, getString(R.string.logintype), "signIn");

              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_studyId",
                  getIntent().getStringExtra("studyId"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_status",
                  getIntent().getStringExtra("status"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_studyStatus",
                  getIntent().getStringExtra("studyStatus"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_position",
                  getIntent().getStringExtra("position"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_title",
                  getIntent().getStringExtra("title"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_bookmark",
                  "" + getIntent().getBooleanExtra("bookmark", false));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_enroll",
                  getIntent().getStringExtra("enroll"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_rejoin",
                  getIntent().getStringExtra("rejoin"));
              SharedPreferenceHelper.writePreference(
                  StudyInfoActivity.this,
                  "login_studyinfo_about_this_study",
                  "" + getIntent().getBooleanExtra("about_this_study", false));

              CustomTabsIntent customTabsIntent =
                  new CustomTabsIntent.Builder()
                      .setToolbarColor(getResources().getColor(R.color.colorAccent))
                      .setShowTitle(true)
                      .setCloseButtonIcon(
                          BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
                      .setStartAnimations(
                          StudyInfoActivity.this, R.anim.slide_in_right, R.anim.slide_out_left)
                      .setExitAnimations(
                          StudyInfoActivity.this, R.anim.slide_in_left, R.anim.slide_out_right)
                      .build();
              customTabsIntent.intent.setData(Uri.parse(Urls.LOGIN_URL));
              startActivity(customTabsIntent.intent);
            } else {
              new CallConsentMetaData(true).execute();
            }
          }
        });

    visitWebsiteButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              Intent browserIntent =
                  new Intent(Intent.ACTION_VIEW, Uri.parse(studyHome.getStudyWebsite()));
              startActivity(browserIntent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
    learnMoreButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              Intent intent = new Intent(StudyInfoActivity.this, WebViewActivity.class);
              intent.putExtra("consent", consentDocumentData.getConsent().getContent());
              startActivity(intent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  private void backClicked() {
    if (getIntent().getStringExtra("flow") != null
        && getIntent().getStringExtra("flow").equalsIgnoreCase("login_callback")) {
      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        Intent intent = new Intent(StudyInfoActivity.this, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
      } else {
        Intent intent = new Intent(StudyInfoActivity.this, StandaloneActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
      }
    } else {
      Intent intent = new Intent();
      intent.putExtra("position", position);
      intent.putExtra("bookmark", bookmarked);
      intent.putExtra("status", status);
      intent.putExtra("studyId", studyId);
      intent.putExtra("action", "refresh");
      setResult(Activity.RESULT_OK, intent);
      finish();
    }
  }

  private void joinStudy() {
    if (status.equalsIgnoreCase(StudyFragment.UPCOMING)) {
      Toast.makeText(getApplication(), R.string.upcoming_study, Toast.LENGTH_SHORT).show();
    } else if (enroll.equalsIgnoreCase("false")) {
      Toast.makeText(getApplication(), R.string.study_no_enroll, Toast.LENGTH_SHORT).show();
    } else if (status.equalsIgnoreCase(StudyFragment.PAUSED)) {
      Toast.makeText(getApplication(), R.string.study_paused, Toast.LENGTH_SHORT).show();
    } else if (rejoin.equalsIgnoreCase("false")
        && studyStatus.equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
      Toast.makeText(getApplication(), R.string.cannot_rejoin_study, Toast.LENGTH_SHORT).show();
    } else {
      if (eligibilityConsent.getEligibility().getType().equalsIgnoreCase("token")) {
        Intent intent = new Intent(StudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", title);
        intent.putExtra("studyId", studyId);
        intent.putExtra("eligibility", "token");
        intent.putExtra("type", "join");
        startActivity(intent);
      } else if (eligibilityConsent.getEligibility().getType().equalsIgnoreCase("test")) {

        RealmList<Steps> stepsRealmList = eligibilityConsent.getEligibility().getTest();
        StepsBuilder stepsBuilder = new StepsBuilder(this, stepsRealmList, false);
        OrderedTask task = new OrderedTask("Test", stepsBuilder.getsteps());

        Intent intent =
            CustomViewTaskActivity.newIntent(
                this,
                task,
                "",
                studyId,
                eligibilityConsent.getEligibility(),
                title,
                "",
                "test",
                "join");
        startActivity(intent);

      } else {
        Intent intent = new Intent(StudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", title);
        intent.putExtra("studyId", studyId);
        intent.putExtra("eligibility", "combined");
        intent.putExtra("type", "join");
        startActivityForResult(intent, 12345);
      }
    }
  }

  private class CallConsentMetaData extends AsyncTask<String, Void, String> {
    String response = null;
    String responseCode = null;
    Responsemodel responseModel;
    boolean join;

    public CallConsentMetaData(boolean join) {
      this.join = join;
    }

    @Override
    protected String doInBackground(String... params) {
      ConnectionDetector connectionDetector = new ConnectionDetector(StudyInfoActivity.this);

      String url = Urls.BASE_URL_STUDY_DATASTORE_SERVER + Urls.CONSENT_METADATA + "?studyId=" + studyId;
      if (connectionDetector.isConnectingToInternet()) {
        responseModel = HttpRequest.getRequest(url, new HashMap<String, String>(), "STUDY_DATASTORE");
        responseCode = responseModel.getResponseCode();
        response = responseModel.getResponseData();
        if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
          response = "timeout";
        } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
          response = "error";
        } else if (Integer.parseInt(responseCode) >= 201
            && Integer.parseInt(responseCode) < 300
            && response.equalsIgnoreCase("")) {
          response = "No data";
        } else if (Integer.parseInt(responseCode) >= 400
            && Integer.parseInt(responseCode) < 500
            && response.equalsIgnoreCase("http_not_ok")) {
          response = "client error";
        } else if (Integer.parseInt(responseCode) >= 500
            && Integer.parseInt(responseCode) < 600
            && response.equalsIgnoreCase("http_not_ok")) {
          response = "server error";
        } else if (response.equalsIgnoreCase("http_not_ok")) {
          response = "Unknown error";
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
          response = "session expired";
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK
            && !response.equalsIgnoreCase("")) {
          response = response;
        } else {
          response = getString(R.string.unknown_error);
        }
      }
      return response;
    }

    @Override
    protected void onPostExecute(String result) {
      AppController.getHelperProgressDialog().dismissDialog();

      if (response != null) {
        if (response.equalsIgnoreCase("session expired")) {
          AppController.getHelperProgressDialog().dismissDialog();
          AppController.getHelperSessionExpired(StudyInfoActivity.this, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  StudyInfoActivity.this,
                  getResources().getString(R.string.connection_timeout),
                  Toast.LENGTH_SHORT)
              .show();
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {

          Gson gson =
              new GsonBuilder()
                  .setExclusionStrategies(
                      new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                          return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                          return false;
                        }
                      })
                  .registerTypeAdapter(
                      new TypeToken<RealmList<CorrectAnswerString>>() {}.getType(),
                      new TypeAdapter<RealmList<CorrectAnswerString>>() {

                        @Override
                        public void write(JsonWriter out, RealmList<CorrectAnswerString> value)
                            throws IOException {
                          // Ignore
                        }

                        @Override
                        public RealmList<CorrectAnswerString> read(JsonReader in)
                            throws IOException {
                          RealmList<CorrectAnswerString> list =
                              new RealmList<CorrectAnswerString>();
                          in.beginArray();
                          while (in.hasNext()) {
                            CorrectAnswerString surveyObjectString = new CorrectAnswerString();
                            surveyObjectString.setAnswer(in.nextString());
                            list.add(surveyObjectString);
                          }
                          in.endArray();
                          return list;
                        }
                      })
                  .create();
          eligibilityConsent = gson.fromJson(response, EligibilityConsent.class);
          if (eligibilityConsent != null) {
            eligibilityConsent.setStudyId(studyId);
            saveConsentToDB(eligibilityConsent);

            if (join) {
              joinStudy();
            } else {
              if (userPreferenceStudies != null) {
                if (userPreferenceStudies.size() != 0) {
                  boolean studyIdPresent = false;
                  for (int i = 0; i < userPreferenceStudies.size(); i++) {
                    if (userPreferenceStudies.get(i).getStudyId().equalsIgnoreCase(studyId)) {
                      studyIdPresent = true;
                      if (userPreferenceStudies
                          .get(i)
                          .getStatus()
                          .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                        Intent intent = new Intent(StudyInfoActivity.this, SurveyActivity.class);
                        intent.putExtra("studyId", studyId);
                        startActivity(intent);
                      } else {
                        joinStudy();
                      }
                    }
                  }
                  if (!studyIdPresent) {
                    joinStudy();
                  }
                } else {
                  joinStudy();
                }
              } else {
                Toast.makeText(
                        StudyInfoActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT)
                    .show();
              }
            }
          } else {
            Toast.makeText(
                    StudyInfoActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  StudyInfoActivity.this,
                  getResources().getString(R.string.unable_to_retrieve_data),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(
                StudyInfoActivity.this, getString(R.string.unknown_error), Toast.LENGTH_SHORT)
            .show();
      }
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
    }
  }

  private void saveConsentToDB(EligibilityConsent eligibilityConsent) {
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(eligibilityConsent);
    realm.commitTransaction();
  }

  @Override
  public void onBackPressed() {
    backClicked();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == JOIN_ACTION_SIGIN) {
      if (resultCode == RESULT_OK) {
        loginCallback();
      }
    } else if (requestCode == 12345) {
      if (resultCode == RESULT_OK) {
        if (eligibilityConsent != null) {
          RealmList<Steps> stepsRealmList = eligibilityConsent.getEligibility().getTest();
          StepsBuilder stepsBuilder = new StepsBuilder(this, stepsRealmList, false);
          OrderedTask task = new OrderedTask("Test", stepsBuilder.getsteps());

          Intent intent =
              CustomViewTaskActivity.newIntent(
                  this,
                  task,
                  "",
                  studyId,
                  eligibilityConsent.getEligibility(),
                  title,
                  data.getStringExtra("enrollId"),
                  "combined",
                  "join");
          startActivity(intent);
        }
      }
    }
  }

  private void loginCallback() {
    AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);

    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(StudyInfoActivity.this, getResources().getString(R.string.userid), ""));
    ParticipantDatastoreEnrollmentConfigEvent participantDatastoreEnrollmentConfigEvent =
        new ParticipantDatastoreEnrollmentConfigEvent(
            "get",
            Urls.STUDY_STATE,
            GET_PREFERENCES,
            StudyInfoActivity.this,
            StudyData.class,
            null,
            header,
            null,
            false,
            this);
    GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
    getPreferenceEvent.setParticipantDatastoreEnrollmentConfigEvent(
        participantDatastoreEnrollmentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performGetUserPreference(getPreferenceEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == STUDY_INFO) {
      studyHome = (StudyHome) response;
      if (studyHome != null) {
        HashMap<String, String> header = new HashMap<>();
        String url =
            Urls.GET_CONSENT_DOC
                + "?studyId="
                + studyId
                + "&consentVersion=&activityId=&activityVersion=";
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        StudyDatastoreConfigEvent studyDatastoreConfigEvent =
            new StudyDatastoreConfigEvent(
                "get",
                url,
                StudyInfoActivity.GET_CONSENT_DOC,
                StudyInfoActivity.this,
                ConsentDocumentData.class,
                null,
                header,
                null,
                false,
                StudyInfoActivity.this);

        getUserStudyInfoEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == UPDATE_PREFERENCES) {
      LoginData loginData = (LoginData) response;
      AppController.getHelperProgressDialog().dismissDialog();
      if (loginData != null) {
        if (bookmarked) {
          bookmarkimage.setImageResource(R.drawable.star_grey_big);
          bookmarked = false;
        } else {
          bookmarkimage.setImageResource(R.drawable.star_yellow_big);
          bookmarked = true;
        }

        dbServiceSubscriber.updateStudyPreferenceToDb(this, studyId, bookmarked, studyStatus);
        /// delete offline row
        dbServiceSubscriber.deleteOfflineDataRow(this, deleteIndexNumberDb);
      }
    } else if (responseCode == GET_CONSENT_DOC) {
      AppController.getHelperProgressDialog().dismissDialog();
      consentDocumentData = (ConsentDocumentData) response;
      getStudyWebsiteNull();
      studyHome.setStudyId(studyId);
      if (studyHome != null) {
        dbServiceSubscriber.saveStudyInfoToDB(this, studyHome);
      }
      if (consentDocumentData != null) {
        consentDocumentData.setStudyId(studyId);
        dbServiceSubscriber.saveConsentDocumentToDB(this, consentDocumentData);
      }
      setViewPagerView(studyHome);
    } else if (responseCode == GET_PREFERENCES) {

      AppController.getHelperProgressDialog().dismissDialog();
      StudyData studies = (StudyData) response;
      if (studies != null) {
        studies.setUserId(
            AppController.getHelperSharedPreference()
                .readPreference(StudyInfoActivity.this, getString(R.string.userid), ""));
        dbServiceSubscriber.saveStudyPreferencesToDB(this, studies);

        userPreferenceStudies = studies.getStudies();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(studyId, realm);
        if (studyList.getStatus().equalsIgnoreCase(StudyFragment.UPCOMING)) {
          Toast.makeText(getApplication(), R.string.upcoming_study, Toast.LENGTH_SHORT).show();
        } else if (!studyList.getSetting().isEnrolling()) {
          Toast.makeText(getApplication(), R.string.study_no_enroll, Toast.LENGTH_SHORT).show();
        } else if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
          Toast.makeText(getApplication(), R.string.study_paused, Toast.LENGTH_SHORT).show();
        } else if (!studyList.getSetting().getRejoin()
            && studyList.getStudyStatus().equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
          Toast.makeText(getApplication(), R.string.cannot_rejoin_study, Toast.LENGTH_SHORT).show();
        } else {
          new CallConsentMetaData(false).execute();
        }
      } else {
        Toast.makeText(StudyInfoActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void getStudyWebsiteNull() {
    joinButton.setVisibility(View.VISIBLE);
    if ((aboutThisStudy) && studyHome.getStudyWebsite().equalsIgnoreCase("")) {
      bottomBar.setVisibility(View.INVISIBLE);
      bottomBar1.setVisibility(View.GONE);
      joinButton.setVisibility(View.INVISIBLE);
      visitWebsiteButton.setClickable(false);
      learnMoreButton.setClickable(false);
    } else if (aboutThisStudy) {
      bottomBar.setVisibility(View.INVISIBLE);
      bottomBar1.setVisibility(View.VISIBLE);
      joinButton.setVisibility(View.INVISIBLE);
      visitWebsiteButton.setClickable(false);
      learnMoreButton.setClickable(false);
      if (studyHome.getStudyWebsite() != null
          && !studyHome.getStudyWebsite().equalsIgnoreCase("")) {
        consentLayButton.setText(getResources().getString(R.string.visit_website));
        consentLay.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(studyHome.getStudyWebsite()));
                startActivity(browserIntent);
              }
            });
      } else {
        consentLay.setVisibility(View.GONE);
      }
    } else if (studyHome.getStudyWebsite().equalsIgnoreCase("")) {
      bottomBar.setVisibility(View.INVISIBLE);
      bottomBar1.setVisibility(View.VISIBLE);
      visitWebsiteButton.setClickable(false);
      learnMoreButton.setClickable(false);
      consentLay.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                Intent intent = new Intent(StudyInfoActivity.this, WebViewActivity.class);
                intent.putExtra("consent", consentDocumentData.getConsent().getContent());
                startActivity(intent);
              } catch (Exception e) {
                Logger.log(e);
              }
            }
          });
    } else {
      bottomBar.setVisibility(View.VISIBLE);
      bottomBar1.setVisibility(View.GONE);
      visitWebsiteButton.setClickable(true);
      learnMoreButton.setClickable(true);
    }

    if (status.equalsIgnoreCase(getString(R.string.upcoming))
        || status.equalsIgnoreCase(getString(R.string.closed))) {
      joinButton.setVisibility(View.GONE);
    }

    if (status.equalsIgnoreCase(getString(R.string.closed))) {
      bookmarkimage.setVisibility(View.GONE);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      AppController.getHelperSessionExpired(this, errormsg);
    } else {
      // offline update
      if (responseCode == UPDATE_PREFERENCES) {
        if (bookmarked) {
          bookmarkimage.setImageResource(R.drawable.star_grey_big);
          bookmarked = false;
        } else {
          bookmarkimage.setImageResource(R.drawable.star_yellow_big);
          bookmarked = true;
        }
        dbServiceSubscriber.updateStudyPreferenceToDb(this, studyId, bookmarked, studyStatus);
      }
      studyHome = dbServiceSubscriber.getStudyInfoListFromDB(studyId, realm);
      if (studyHome != null) {
        consentDocumentData = dbServiceSubscriber.getConsentDocumentFromDB(studyId, realm);
        getStudyWebsiteNull();
        setViewPagerView(studyHome);
      } else {
        Toast.makeText(StudyInfoActivity.this, errormsg, Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  private void callGetStudyInfoWebservice() {
    AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
    HashMap<String, String> header = new HashMap<>();
    String url = Urls.STUDY_INFO + "?studyId=" + studyId;
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            STUDY_INFO,
            StudyInfoActivity.this,
            StudyHome.class,
            null,
            header,
            null,
            false,
            StudyInfoActivity.this);

    getUserStudyInfoEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  public void updatebookmark(boolean b) {
    AppController.getHelperProgressDialog().showProgress(this, "", "", false);

    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.userid), ""));

    JSONObject jsonObject = new JSONObject();

    JSONArray studieslist = new JSONArray();
    JSONObject studies = new JSONObject();
    try {
      studies.put("studyId", studyId);
      studies.put("bookmarked", b);
    } catch (JSONException e) {
      Logger.log(e);
    }
    studieslist.put(studies);
    try {
      jsonObject.put("studies", studieslist);
    } catch (JSONException e) {
      Logger.log(e);
    }

    /////////// offline data storing
    try {
      int number = dbServiceSubscriber.getUniqueID(realm);
      if (number == 0) {
        number = 1;
      } else {
        number += 1;
      }
      OfflineData offlineData = dbServiceSubscriber.getStudyIdOfflineData(studyId, realm);
      if (offlineData != null) {
        number = offlineData.getNumber();
      }
      deleteIndexNumberDb = number;
      AppController.pendingService(
          this,
          number,
          "post_object",
          Urls.UPDATE_STUDY_PREFERENCE,
          "",
          jsonObject.toString(),
          "ParticipantDatastoreEnrollment",
          "",
          studyId,
          "");
    } catch (Exception e) {
      Logger.log(e);
    }
    //////////
    ParticipantDatastoreEnrollmentConfigEvent participantDatastoreEnrollmentConfigEvent =
        new ParticipantDatastoreEnrollmentConfigEvent(
            "post_object",
            Urls.UPDATE_STUDY_PREFERENCE,
            UPDATE_PREFERENCES,
            this,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    updatePreferenceEvent.setParticipantDatastoreEnrollmentConfigEvent(
        participantDatastoreEnrollmentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  private void setViewPagerView(final StudyHome studyHome) {

    ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
    CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
    viewpager.setAdapter(
        new StudyInfoPagerAdapter(StudyInfoActivity.this, studyHome.getInfo(), studyId));
    indicator.setViewPager(viewpager);
    if (studyHome.getInfo().size() < 2) {
      indicator.setVisibility(View.GONE);
    }
    viewpager.setCurrentItem(0);
    indicator.setOnPageChangeListener(
        new ViewPager.OnPageChangeListener() {
          public void onPageScrollStateChanged(int state) {}

          public void onPageScrolled(
              int position, float positionOffset, int positionOffsetPixels) {}

          public void onPageSelected(int position) {
            // Check if this is the page you want.
            if (studyHome.getInfo().get(position).getType().equalsIgnoreCase("video")) {
              joinButton.setBackground(getResources().getDrawable(R.drawable.rectangle_blue_white));
              joinButton.setTextColor(getResources().getColor(R.color.white));
            } else {
              joinButton.setBackground(
                  getResources().getDrawable(R.drawable.rectangle_black_white));
              joinButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
          }
        });

    if (getIntent().getStringExtra("flow") != null
        && getIntent().getStringExtra("flow").equalsIgnoreCase("login_callback")) {
      loginCallback();
    }
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }
}
