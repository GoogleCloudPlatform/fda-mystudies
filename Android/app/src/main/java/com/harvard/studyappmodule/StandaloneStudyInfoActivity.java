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

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetPreferenceEvent;
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
import com.harvard.webservicemodule.events.RegistrationServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.WcpConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import org.researchstack.backbone.task.OrderedTask;

public class StandaloneStudyInfoActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private static final int JOIN_ACTION_SIGIN = 100;
  private static final int SPECIFIC_STUDY = 103;
  private static final int STUDY_INFO = 104;
  private static final int GET_CONSENT_DOC = 102;
  private static final int GET_PREFERENCES = 101;

  private RelativeLayout backBtn;
  private AppCompatImageView bookmarkimage;
  private AppCompatTextView visitWebsiteButton;
  private AppCompatTextView learnMoreButton;
  private AppCompatTextView consentLayButton;
  private AppCompatTextView joinButton;
  private LinearLayout bottombar;
  private LinearLayout bottombar1;
  private RelativeLayout consentLay;
  private ConsentDocumentData consentDocumentData;
  private Study study;
  private StudyHome studyHome;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private RealmList<Studies> userPreferenceStudies;
  private EligibilityConsent eligibilityConsent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_standalone_study_info);

    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    initializeXmlId();
    setFont();
    bindEvents();

    AppController.getHelperProgressDialog()
        .showProgress(StandaloneStudyInfoActivity.this, "", "", false);
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    HashMap<String, String> params = new HashMap();
    params.put("studyId", AppConfig.StudyId);
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get",
            Urls.SPECIFIC_STUDY + "?studyId=" + AppConfig.StudyId,
            SPECIFIC_STUDY,
            StandaloneStudyInfoActivity.this,
            Study.class,
            params,
            header,
            null,
            false,
            this);

    getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);

    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
      bookmarkimage.setVisibility(View.GONE);
      backBtn.setVisibility(View.GONE);
    }
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    bookmarkimage = (AppCompatImageView) findViewById(R.id.imageViewRight);
    joinButton = (AppCompatTextView) findViewById(R.id.joinButton);
    visitWebsiteButton = (AppCompatTextView) findViewById(R.id.mVisitWebsiteButton);
    learnMoreButton = (AppCompatTextView) findViewById(R.id.mLernMoreButton);
    consentLayButton = (AppCompatTextView) findViewById(R.id.consentLayButton);
    bottombar = (LinearLayout) findViewById(R.id.bottom_bar);
    bottombar1 = (LinearLayout) findViewById(R.id.bottom_bar1);
    consentLay = (RelativeLayout) findViewById(R.id.consentLay);
  }

  private void setFont() {
    joinButton.setTypeface(AppController.getTypeface(this, "regular"));
    visitWebsiteButton.setTypeface(
        AppController.getTypeface(StandaloneStudyInfoActivity.this, "regular"));
    learnMoreButton.setTypeface(
        AppController.getTypeface(StandaloneStudyInfoActivity.this, "regular"));
    consentLayButton.setTypeface(
        AppController.getTypeface(StandaloneStudyInfoActivity.this, "regular"));
  }

  private void bindEvents() {

    joinButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (SharedPreferenceHelper.readPreference(
                    StandaloneStudyInfoActivity.this, getString(R.string.userid), "")
                .equalsIgnoreCase("")) {
              Toast.makeText(
                      StandaloneStudyInfoActivity.this,
                      "SignIn to Join the Study",
                      Toast.LENGTH_SHORT)
                  .show();
              SharedPreferenceHelper.writePreference(
                  StandaloneStudyInfoActivity.this,
                  getString(R.string.loginflow),
                  "StandaloneStudyInfo");
              SharedPreferenceHelper.writePreference(
                  StandaloneStudyInfoActivity.this, getString(R.string.logintype), "signIn");
              CustomTabsIntent customTabsIntent =
                  new CustomTabsIntent.Builder()
                      .setToolbarColor(getResources().getColor(R.color.colorAccent))
                      .setShowTitle(true)
                      .setCloseButtonIcon(
                          BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
                      .setStartAnimations(
                          StandaloneStudyInfoActivity.this,
                          R.anim.slide_in_right,
                          R.anim.slide_out_left)
                      .setExitAnimations(
                          StandaloneStudyInfoActivity.this,
                          R.anim.slide_in_left,
                          R.anim.slide_out_right)
                      .build();
              customTabsIntent.intent.setData(Uri.parse(Urls.LOGIN_URL));
              startActivity(customTabsIntent.intent);
            } else {
              loginCallback();
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
              Intent intent = new Intent(StandaloneStudyInfoActivity.this, WebViewActivity.class);
              intent.putExtra("consent", consentDocumentData.getConsent().getContent());
              startActivity(intent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  private void callGetStudyInfoWebservice() {
    AppController.getHelperProgressDialog()
        .showProgress(StandaloneStudyInfoActivity.this, "", "", false);
    HashMap<String, String> header = new HashMap<>();
    String url = Urls.STUDY_INFO + "?studyId=" + AppConfig.StudyId;
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get",
            url,
            STUDY_INFO,
            StandaloneStudyInfoActivity.this,
            StudyHome.class,
            null,
            header,
            null,
            false,
            StandaloneStudyInfoActivity.this);

    getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == SPECIFIC_STUDY) {
      if (response != null) {
        study = (Study) response;
        AppController.getHelperProgressDialog().dismissDialog();
        if (!study.getStudies().isEmpty()) {
          dbServiceSubscriber.saveStudyListToDB(this, study);
          if (study.getStudies().get(0).getStatus().equalsIgnoreCase("active")) {
            callGetStudyInfoWebservice();
            if (study.getStudies().get(0).getStatus().equalsIgnoreCase(getString(R.string.upcoming))
                || study
                    .getStudies()
                    .get(0)
                    .getStatus()
                    .equalsIgnoreCase(getString(R.string.closed))) {
              joinButton.setVisibility(View.GONE);
            }
            if (study
                .getStudies()
                .get(0)
                .getStatus()
                .equalsIgnoreCase(getString(R.string.closed))) {
              bookmarkimage.setVisibility(View.GONE);
            }
          } else {
            Toast.makeText(
                    this,
                    "This study is " + study.getStudies().get(0).getStatus(),
                    Toast.LENGTH_SHORT)
                .show();
            finish();
          }
        } else {
          Toast.makeText(this, "Study not found", Toast.LENGTH_SHORT).show();
          finish();
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(
                StandaloneStudyInfoActivity.this, R.string.error_retriving_data, Toast.LENGTH_SHORT)
            .show();
        finish();
      }
    } else if (responseCode == STUDY_INFO) {
      studyHome = (StudyHome) response;
      if (studyHome != null) {

        HashMap<String, String> header = new HashMap<>();
        String url =
            Urls.GET_CONSENT_DOC
                + "?studyId="
                + AppConfig.StudyId
                + "&consentVersion=&activityId=&activityVersion=";
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WcpConfigEvent wcpConfigEvent =
            new WcpConfigEvent(
                "get",
                url,
                GET_CONSENT_DOC,
                StandaloneStudyInfoActivity.this,
                ConsentDocumentData.class,
                null,
                header,
                null,
                false,
                StandaloneStudyInfoActivity.this);

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);

        setViewPagerView(studyHome);
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == GET_CONSENT_DOC) {
      AppController.getHelperProgressDialog().dismissDialog();
      consentDocumentData = (ConsentDocumentData) response;
      getStudyWebsiteNull();
      studyHome.setStudyId(AppConfig.StudyId);
      if (studyHome != null) {
        dbServiceSubscriber.saveStudyInfoToDB(this, studyHome);
      }
      if (consentDocumentData != null) {
        consentDocumentData.setStudyId(AppConfig.StudyId);
        dbServiceSubscriber.saveConsentDocumentToDB(this, consentDocumentData);
      }
      setViewPagerView(studyHome);
    } else if (responseCode == GET_PREFERENCES) {

      AppController.getHelperProgressDialog().dismissDialog();
      StudyData studies = (StudyData) response;
      if (studies != null) {
        studies.setUserId(
            AppController.getHelperSharedPreference()
                .readPreference(StandaloneStudyInfoActivity.this, getString(R.string.userid), ""));

        StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
        if (studyData == null) {
          int size = studies.getStudies().size();
          for (int i = 0; i < size; i++) {
            if (!studies.getStudies().get(i).getStudyId().equalsIgnoreCase(AppConfig.StudyId)) {
              studies.getStudies().remove(i);
              size = size - 1;
              i--;
            }
          }
          dbServiceSubscriber.saveStudyPreferencesToDB(this, studies);
        } else {
          studies = studyData;
        }

        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.title),
                "" + study.getStudies().get(0).getTitle());
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.bookmark),
                "" + study.getStudies().get(0).isBookmarked());
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.status),
                "" + study.getStudies().get(0).getStatus());
        if (!studies.getStudies().isEmpty()) {
          AppController.getHelperSharedPreference()
              .writePreference(
                  StandaloneStudyInfoActivity.this,
                  getString(R.string.studyStatus),
                  "" + studies.getStudies().get(0).getStatus());
        } else {
          AppController.getHelperSharedPreference()
              .writePreference(
                  StandaloneStudyInfoActivity.this, getString(R.string.studyStatus), "yetToJoin");
        }
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this, getString(R.string.position), "" + 0);
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.enroll),
                "" + study.getStudies().get(0).getSetting().isEnrolling());
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.rejoin),
                "" + study.getStudies().get(0).getSetting().getRejoin());
        AppController.getHelperSharedPreference()
            .writePreference(
                StandaloneStudyInfoActivity.this,
                getString(R.string.studyVersion),
                "" + study.getStudies().get(0).getStudyVersion());

        userPreferenceStudies = studies.getStudies();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(AppConfig.StudyId, realm);
        if (studyList != null) {
          if (studyList.getStatus().equalsIgnoreCase(StudyFragment.UPCOMING)) {
            Toast.makeText(getApplication(), R.string.upcoming_study, Toast.LENGTH_SHORT).show();
          } else if (!studyList.getSetting().isEnrolling()) {
            Toast.makeText(getApplication(), R.string.study_no_enroll, Toast.LENGTH_SHORT).show();
          } else if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
            Toast.makeText(getApplication(), R.string.study_paused, Toast.LENGTH_SHORT).show();
          } else if (!studyList.getSetting().getRejoin()
              && studyList.getStudyStatus().equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
            Toast.makeText(getApplication(), R.string.cannot_rejoin_study, Toast.LENGTH_SHORT)
                .show();
          } else {
            new CallConsentMetaData(false).execute();
          }
        } else {
          Toast.makeText(this, "No study present", Toast.LENGTH_SHORT).show();
        }
      } else {
        Toast.makeText(
                StandaloneStudyInfoActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT)
            .show();
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
      ConnectionDetector connectionDetector =
          new ConnectionDetector(StandaloneStudyInfoActivity.this);

      String url =
          Urls.BASE_URL_WCP_SERVER + Urls.CONSENT_METADATA + "?studyId=" + AppConfig.StudyId;
      if (connectionDetector.isConnectingToInternet()) {
        responseModel = HttpRequest.getRequest(url, new HashMap<String, String>(), "WCP");
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
          AppController.getHelperSessionExpired(
              StandaloneStudyInfoActivity.this, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  StandaloneStudyInfoActivity.this,
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
            eligibilityConsent.setStudyId(AppConfig.StudyId);
            saveConsentToDB(eligibilityConsent);

            if (join) {
              joinStudy();
            } else {
              if (userPreferenceStudies != null) {
                if (userPreferenceStudies.size() != 0) {
                  boolean studyIdPresent = false;
                  for (int i = 0; i < userPreferenceStudies.size(); i++) {
                    if (userPreferenceStudies
                        .get(i)
                        .getStudyId()
                        .equalsIgnoreCase(AppConfig.StudyId)) {
                      studyIdPresent = true;
                      if (userPreferenceStudies
                          .get(i)
                          .getStatus()
                          .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                        Intent intent =
                            new Intent(StandaloneStudyInfoActivity.this, SurveyActivity.class);
                        intent.putExtra("studyId", AppConfig.StudyId);
                        startActivity(intent);
                        finish();
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
                        StandaloneStudyInfoActivity.this,
                        R.string.error_retriving_data,
                        Toast.LENGTH_SHORT)
                    .show();
              }
            }
          } else {
            Toast.makeText(
                    StandaloneStudyInfoActivity.this,
                    R.string.error_retriving_data,
                    Toast.LENGTH_SHORT)
                .show();
          }
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  StandaloneStudyInfoActivity.this,
                  getResources().getString(R.string.unable_to_retrieve_data),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(
                StandaloneStudyInfoActivity.this,
                getString(R.string.unknown_error),
                Toast.LENGTH_SHORT)
            .show();
      }
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog()
          .showProgress(StandaloneStudyInfoActivity.this, "", "", false);
    }
  }

  private void joinStudy() {
    if (study.getStudies().get(0).getStatus().equalsIgnoreCase(StudyFragment.UPCOMING)) {
      Toast.makeText(getApplication(), R.string.upcoming_study, Toast.LENGTH_SHORT).show();
    } else if (!study.getStudies().get(0).getSetting().isEnrolling()) {
      Toast.makeText(getApplication(), R.string.study_no_enroll, Toast.LENGTH_SHORT).show();
    } else if (study.getStudies().get(0).getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
      Toast.makeText(getApplication(), R.string.study_paused, Toast.LENGTH_SHORT).show();
    } else if (!study.getStudies().get(0).getSetting().getRejoin()
        && study.getStudies().get(0).getStudyStatus().equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
      Toast.makeText(getApplication(), R.string.cannot_rejoin_study, Toast.LENGTH_SHORT).show();
    } else {
      if (eligibilityConsent.getEligibility().getType().equalsIgnoreCase("token")) {
        Intent intent =
            new Intent(StandaloneStudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", study.getStudies().get(0).getTitle());
        intent.putExtra("studyId", AppConfig.StudyId);
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
                AppConfig.StudyId,
                eligibilityConsent.getEligibility(),
                study.getStudies().get(0).getTitle(),
                "",
                "test",
                "join");
        startActivity(intent);

      } else {
        Intent intent =
            new Intent(StandaloneStudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", study.getStudies().get(0).getTitle());
        intent.putExtra("studyId", AppConfig.StudyId);
        intent.putExtra("eligibility", "combined");
        intent.putExtra("type", "join");
        startActivityForResult(intent, 12345);
      }
    }
  }

  private void saveConsentToDB(EligibilityConsent eligibilityConsent) {
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(eligibilityConsent);
    realm.commitTransaction();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                  AppConfig.StudyId,
                  eligibilityConsent.getEligibility(),
                  study.getStudies().get(0).getTitle(),
                  data.getStringExtra("enrollId"),
                  "combined",
                  "join");
          startActivity(intent);
        }
      }
    }
  }

  private void getStudyWebsiteNull() {
    joinButton.setVisibility(View.VISIBLE);
    boolean aboutThisStudy = false;
    if ((aboutThisStudy) && studyHome.getStudyWebsite().equalsIgnoreCase("")) {
      bottombar.setVisibility(View.INVISIBLE);
      bottombar1.setVisibility(View.GONE);
      joinButton.setVisibility(View.INVISIBLE);
      visitWebsiteButton.setClickable(false);
      learnMoreButton.setClickable(false);
    } else if (aboutThisStudy) {
      bottombar.setVisibility(View.INVISIBLE);
      bottombar1.setVisibility(View.VISIBLE);
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
      bottombar.setVisibility(View.INVISIBLE);
      bottombar1.setVisibility(View.VISIBLE);
      visitWebsiteButton.setClickable(false);
      learnMoreButton.setClickable(false);
      consentLay.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                Intent intent = new Intent(StandaloneStudyInfoActivity.this, WebViewActivity.class);
                intent.putExtra("consent", consentDocumentData.getConsent().getContent());
                startActivity(intent);
              } catch (Exception e) {
                Logger.log(e);
              }
            }
          });
    } else {
      bottombar.setVisibility(View.VISIBLE);
      bottombar1.setVisibility(View.GONE);
      visitWebsiteButton.setClickable(true);
      learnMoreButton.setClickable(true);
    }

    if (study.getStudies().get(0).getStatus().equalsIgnoreCase(getString(R.string.upcoming))
        || study.getStudies().get(0).getStatus().equalsIgnoreCase(getString(R.string.closed))) {
      joinButton.setVisibility(View.GONE);
    }

    if (study.getStudies().get(0).getStatus().equalsIgnoreCase(getString(R.string.closed))) {
      bookmarkimage.setVisibility(View.GONE);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {}

  private void setViewPagerView(final StudyHome studyHome) {

    ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
    CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
    viewpager.setAdapter(
        new StudyInfoPagerAdapter(
            StandaloneStudyInfoActivity.this, studyHome.getInfo(), AppConfig.StudyId));
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
  }

  private void loginCallback() {
    AppController.getHelperProgressDialog()
        .showProgress(StandaloneStudyInfoActivity.this, "", "", false);

    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(
                    StandaloneStudyInfoActivity.this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(
                StandaloneStudyInfoActivity.this, getResources().getString(R.string.userid), ""));
    RegistrationServerEnrollmentConfigEvent registrationServerConfigEvent =
        new RegistrationServerEnrollmentConfigEvent(
            "get",
            Urls.STUDY_STATE,
            GET_PREFERENCES,
            StandaloneStudyInfoActivity.this,
            StudyData.class,
            null,
            header,
            null,
            false,
            this);
    GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
    getPreferenceEvent.setRegistrationServerEnrollmentConfigEvent(registrationServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performGetUserPreference(getPreferenceEvent);
  }
}
