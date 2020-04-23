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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.harvard.eligibilitymodule.CustomViewTaskActivity;
import com.harvard.eligibilitymodule.StepsBuilder;
import com.harvard.R;
import com.harvard.WebViewActivity;
import com.harvard.gatewaymodule.CircleIndicator;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DBServiceSubscriber;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.Steps;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.SignInActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.URLs;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.RegistrationServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.WCPConfigEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.task.OrderedTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class StudyInfoActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
  private static final int STUDY_INFO = 10;
  private static final int UPDATE_PREFERENCES = 11;
  private static final int GET_CONSENT_DOC = 12;
  private static final int JOIN_ACTION_SIGIN = 100;
  private static final int GET_PREFERENCES = 101;
  private RelativeLayout mBackBtn;
  private RelativeLayout mRightBtn;
  private AppCompatImageView mBookmarkimage;
  private boolean mBookmarked = false;
  private String mStudyId = "";
  private String mStatus = "";
  private String mStudyStatus = "";
  private String mPosition = "";
  private String mTitle = "";
  private String mEnroll = "";
  private String mRejoin = "";
  private AppCompatTextView mJoinButton;
  private StudyHome mStudyHome;
  private ConsentDocumentData mConsentDocumentData;

  private AppCompatTextView mVisitWebsiteButton;
  private AppCompatTextView mLernMoreButton;
  private AppCompatTextView mConsentLayButton;
  private LinearLayout mBottombar;
  private LinearLayout mBottombar1;
  private RelativeLayout mConsentLay;
  private boolean mAboutThisStudy;
  private int mDeleteIndexNumberDB;
  private DBServiceSubscriber dbServiceSubscriber;
  private Realm mRealm;
  private EligibilityConsent eligibilityConsent;
  private RealmList<Studies> userPreferenceStudies;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_study_info);
    dbServiceSubscriber = new DBServiceSubscriber();
    mRealm = AppController.getRealmobj(this);

    initializeXMLId();
    setFont();
    bindEvents();

    try {
      mStudyId = getIntent().getStringExtra("studyId");
      mStatus = getIntent().getStringExtra("status");
      mStudyStatus = getIntent().getStringExtra("studyStatus");
      mPosition = getIntent().getStringExtra("position");
      mTitle = getIntent().getStringExtra("title");
      mBookmarked = getIntent().getBooleanExtra("bookmark", false);
      mEnroll = getIntent().getStringExtra("enroll");
      mRejoin = getIntent().getStringExtra("rejoin");
      mAboutThisStudy = getIntent().getBooleanExtra("about_this_study", false);
    } catch (Exception e) {
      Logger.log(e);
    }

    if (AppController.getHelperSharedPreference()
        .readPreference(this, getResources().getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      mBookmarkimage.setVisibility(View.GONE);
      mRightBtn.setClickable(false);
    }
    if (mBookmarked) {
      mBookmarkimage.setImageResource(R.drawable.star_yellow_big);
    } else {
      mBookmarkimage.setImageResource(R.drawable.star_grey_big);
    }
    AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
    callGetStudyInfoWebservice();

    if (mStatus.equalsIgnoreCase(getString(R.string.upcoming))
        || mStatus.equalsIgnoreCase(getString(R.string.closed))) {
      mJoinButton.setVisibility(View.GONE);
    }

    if (mStatus.equalsIgnoreCase(getString(R.string.closed))) {
      mBookmarkimage.setVisibility(View.GONE);
    }

    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
      mBookmarkimage.setVisibility(View.GONE);
      mBackBtn.setVisibility(View.GONE);
    }
  }

  private void initializeXMLId() {
    mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
    mRightBtn = (RelativeLayout) findViewById(R.id.rightBtn);
    mBookmarkimage = (AppCompatImageView) findViewById(R.id.imageViewRight);
    mJoinButton = (AppCompatTextView) findViewById(R.id.joinButton);
    mVisitWebsiteButton = (AppCompatTextView) findViewById(R.id.mVisitWebsiteButton);
    mLernMoreButton = (AppCompatTextView) findViewById(R.id.mLernMoreButton);
    mConsentLayButton = (AppCompatTextView) findViewById(R.id.consentLayButton);
    mBottombar = (LinearLayout) findViewById(R.id.bottom_bar);
    mBottombar1 = (LinearLayout) findViewById(R.id.bottom_bar1);
    mConsentLay = (RelativeLayout) findViewById(R.id.consentLay);
  }

  private void setFont() {
    mJoinButton.setTypeface(AppController.getTypeface(this, "regular"));
    mVisitWebsiteButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
    mLernMoreButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
    mConsentLayButton.setTypeface(AppController.getTypeface(StudyInfoActivity.this, "regular"));
  }

  private void bindEvents() {
    mBackBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent();
            intent.putExtra("position", mPosition);
            intent.putExtra("bookmark", mBookmarked);
            intent.putExtra("action", "refresh");
            setResult(Activity.RESULT_OK, intent);
            finish();
          }
        });

    mRightBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (mBookmarked) {
              updatebookmark(false);
            } else {
              updatebookmark(true);
            }
          }
        });

    mJoinButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            if (AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.userid), "")
                .equalsIgnoreCase("")) {
              Intent intent = new Intent(StudyInfoActivity.this, SignInActivity.class);
              intent.putExtra("from", "StudyInfo");
              startActivityForResult(intent, JOIN_ACTION_SIGIN);
            } else {
              new callConsentMetaData(true).execute();
            }
          }
        });

    mVisitWebsiteButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              Intent browserIntent =
                  new Intent(Intent.ACTION_VIEW, Uri.parse(mStudyHome.getStudyWebsite()));
              startActivity(browserIntent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
    mLernMoreButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              Intent intent = new Intent(StudyInfoActivity.this, WebViewActivity.class);
              intent.putExtra("consent", mConsentDocumentData.getConsent().getContent());
              startActivity(intent);
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
  }

  private void joinStudy() {
    if (mStatus.equalsIgnoreCase(StudyFragment.UPCOMING)) {
      Toast.makeText(getApplication(), R.string.upcoming_study, Toast.LENGTH_SHORT).show();
    } else if (mEnroll.equalsIgnoreCase("false")) {
      Toast.makeText(getApplication(), R.string.study_no_enroll, Toast.LENGTH_SHORT).show();
    } else if (mStatus.equalsIgnoreCase(StudyFragment.PAUSED)) {
      Toast.makeText(getApplication(), R.string.study_paused, Toast.LENGTH_SHORT).show();
    } else if (mRejoin.equalsIgnoreCase("false")
        && mStudyStatus.equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
      Toast.makeText(getApplication(), R.string.cannot_rejoin_study, Toast.LENGTH_SHORT).show();
    } else {
      if (eligibilityConsent.getEligibility().getType().equalsIgnoreCase("token")) {
        Intent intent = new Intent(StudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", mTitle);
        intent.putExtra("studyId", mStudyId);
        intent.putExtra("eligibility", "token");
        intent.putExtra("type", "join");
        startActivity(intent);
      } else if (eligibilityConsent.getEligibility().getType().equalsIgnoreCase("test")) {

        RealmList<Steps> stepsRealmList = eligibilityConsent.getEligibility().getTest();
        StepsBuilder stepsBuilder = new StepsBuilder(this, stepsRealmList, false);
        OrderedTask mTask = new OrderedTask("Test", stepsBuilder.getsteps());

        Intent intent =
            CustomViewTaskActivity.newIntent(
                this,
                mTask,
                "",
                mStudyId,
                eligibilityConsent.getEligibility(),
                mTitle,
                "",
                "test",
                "join");
        startActivity(intent);

      } else {
        Intent intent = new Intent(StudyInfoActivity.this, EligibilityEnrollmentActivity.class);
        intent.putExtra("enrollmentDesc", eligibilityConsent.getEligibility().getTokenTitle());
        intent.putExtra("title", mTitle);
        intent.putExtra("studyId", mStudyId);
        intent.putExtra("eligibility", "combined");
        intent.putExtra("type", "join");
        startActivityForResult(intent, 12345);
      }
    }
  }

  private class callConsentMetaData extends AsyncTask<String, Void, String> {
    String response = null;
    String responseCode = null;
    Responsemodel mResponseModel;
    boolean join;

    public callConsentMetaData(boolean join) {
      this.join = join;
    }

    @Override
    protected String doInBackground(String... params) {
      ConnectionDetector connectionDetector = new ConnectionDetector(StudyInfoActivity.this);

      String url = URLs.BASE_URL_WCP_SERVER + URLs.CONSENT_METADATA + "?studyId=" + mStudyId;
      if (connectionDetector.isConnectingToInternet()) {
        mResponseModel = HttpRequest.getRequest(url, new HashMap<String, String>(), "WCP");
        responseCode = mResponseModel.getResponseCode();
        response = mResponseModel.getResponseData();
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
            eligibilityConsent.setStudyId(mStudyId);
            saveConsentToDB(eligibilityConsent);

            if (join) joinStudy();
            else {
              if (userPreferenceStudies != null) {
                if (userPreferenceStudies.size() != 0) {
                  boolean studyIdPresent = false;
                  for (int i = 0; i < userPreferenceStudies.size(); i++) {
                    if (userPreferenceStudies.get(i).getStudyId().equalsIgnoreCase(mStudyId)) {
                      studyIdPresent = true;
                      if (userPreferenceStudies
                          .get(i)
                          .getStatus()
                          .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                        Intent intent = new Intent(StudyInfoActivity.this, SurveyActivity.class);
                        intent.putExtra("studyId", mStudyId);
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
    mRealm.beginTransaction();
    mRealm.copyToRealmOrUpdate(eligibilityConsent);
    mRealm.commitTransaction();
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent();
    intent.putExtra("position", mPosition);
    intent.putExtra("bookmark", mBookmarked);
    intent.putExtra("status", mStatus);
    intent.putExtra("studyId", mStudyId);
    intent.putExtra("action", "refresh");
    setResult(Activity.RESULT_OK, intent);
    finish();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == JOIN_ACTION_SIGIN) {
      if (resultCode == RESULT_OK) {
        AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
        GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
        HashMap<String, String> header = new HashMap();
        header.put(
            "accessToken",
            AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.auth), ""));
        header.put(
            "userId",
            AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.userid), ""));
        header.put(
            "clientToken",
            AppController.getHelperSharedPreference()
                .readPreference(
                    StudyInfoActivity.this, getResources().getString(R.string.clientToken), ""));
        RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent =
            new RegistrationServerEnrollmentConfigEvent(
                "get",
                URLs.STUDY_STATE,
                GET_PREFERENCES,
                StudyInfoActivity.this,
                StudyData.class,
                null,
                header,
                null,
                false,
                this);

        getPreferenceEvent.setRegistrationServerEnrollmentConfigEvent(
            registrationServerEnrollmentConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserPreference(getPreferenceEvent);
      }
    } else if (requestCode == 12345) {
      if (resultCode == RESULT_OK) {
        if (eligibilityConsent != null) {
          RealmList<Steps> stepsRealmList = eligibilityConsent.getEligibility().getTest();
          StepsBuilder stepsBuilder = new StepsBuilder(this, stepsRealmList, false);
          OrderedTask mTask = new OrderedTask("Test", stepsBuilder.getsteps());

          Intent intent =
              CustomViewTaskActivity.newIntent(
                  this,
                  mTask,
                  "",
                  mStudyId,
                  eligibilityConsent.getEligibility(),
                  mTitle,
                  data.getStringExtra("enrollId"),
                  "combined",
                  "join");
          startActivity(intent);
        }
      }
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == STUDY_INFO) {
      mStudyHome = (StudyHome) response;
      if (mStudyHome != null) {
        HashMap<String, String> header = new HashMap<>();
        String url =
            URLs.GET_CONSENT_DOC
                + "?studyId="
                + mStudyId
                + "&consentVersion=&activityId=&activityVersion=";
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WCPConfigEvent wcpConfigEvent =
            new WCPConfigEvent(
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

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
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
        if (mBookmarked) {
          mBookmarkimage.setImageResource(R.drawable.star_grey_big);
          mBookmarked = false;
        } else {
          mBookmarkimage.setImageResource(R.drawable.star_yellow_big);
          mBookmarked = true;
        }

        dbServiceSubscriber.updateStudyPreferenceToDb(this, mStudyId, mBookmarked, mStudyStatus);
        /// delete offline row
        dbServiceSubscriber.deleteOfflineDataRow(this, mDeleteIndexNumberDB);
      }
    } else if (responseCode == GET_CONSENT_DOC) {
      AppController.getHelperProgressDialog().dismissDialog();
      mConsentDocumentData = (ConsentDocumentData) response;
      getStudyWebsiteNull();
      mStudyHome.setmStudyId(mStudyId);
      if (mStudyHome != null) {
        dbServiceSubscriber.saveStudyInfoToDB(this, mStudyHome);
      }
      if (mConsentDocumentData != null) {
        mConsentDocumentData.setmStudyId(mStudyId);
        dbServiceSubscriber.saveConsentDocumentToDB(this, mConsentDocumentData);
      }
      setViewPagerView(mStudyHome);
    } else if (responseCode == GET_PREFERENCES) {

      AppController.getHelperProgressDialog().dismissDialog();
      StudyData studies = (StudyData) response;
      if (studies != null) {
        studies.setUserId(
            AppController.getHelperSharedPreference()
                .readPreference(StudyInfoActivity.this, getString(R.string.userid), ""));
        dbServiceSubscriber.saveStudyPreferencesToDB(this, studies);

        userPreferenceStudies = studies.getStudies();
        StudyList studyList = dbServiceSubscriber.getStudiesDetails(mStudyId, mRealm);
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
          new callConsentMetaData(false).execute();
        }
      } else {
        Toast.makeText(StudyInfoActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void getStudyWebsiteNull() {
    mJoinButton.setVisibility(View.VISIBLE);
    if ((mAboutThisStudy) && mStudyHome.getStudyWebsite().equalsIgnoreCase("")) {
      mBottombar.setVisibility(View.INVISIBLE);
      mBottombar1.setVisibility(View.GONE);
      mJoinButton.setVisibility(View.INVISIBLE);
      mVisitWebsiteButton.setClickable(false);
      mLernMoreButton.setClickable(false);
    } else if (mAboutThisStudy) {
      mBottombar.setVisibility(View.INVISIBLE);
      mBottombar1.setVisibility(View.VISIBLE);
      mJoinButton.setVisibility(View.INVISIBLE);
      mVisitWebsiteButton.setClickable(false);
      mLernMoreButton.setClickable(false);
      if (mStudyHome.getStudyWebsite() != null
          && !mStudyHome.getStudyWebsite().equalsIgnoreCase("")) {
        mConsentLayButton.setText(getResources().getString(R.string.visit_website));
        mConsentLay.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(mStudyHome.getStudyWebsite()));
                startActivity(browserIntent);
              }
            });
      } else {
        mConsentLay.setVisibility(View.GONE);
      }
    } else if (mStudyHome.getStudyWebsite().equalsIgnoreCase("")) {
      mBottombar.setVisibility(View.INVISIBLE);
      mBottombar1.setVisibility(View.VISIBLE);
      mVisitWebsiteButton.setClickable(false);
      mLernMoreButton.setClickable(false);
      mConsentLay.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                Intent intent = new Intent(StudyInfoActivity.this, WebViewActivity.class);
                intent.putExtra("consent", mConsentDocumentData.getConsent().getContent());
                startActivity(intent);
              } catch (Exception e) {
                Logger.log(e);
              }
            }
          });
    } else {
      mBottombar.setVisibility(View.VISIBLE);
      mBottombar1.setVisibility(View.GONE);
      mVisitWebsiteButton.setClickable(true);
      mLernMoreButton.setClickable(true);
    }

    if (mStatus.equalsIgnoreCase(getString(R.string.upcoming))
        || mStatus.equalsIgnoreCase(getString(R.string.closed))) {
      mJoinButton.setVisibility(View.GONE);
    }

    if (mStatus.equalsIgnoreCase(getString(R.string.closed))) {
      mBookmarkimage.setVisibility(View.GONE);
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
        if (mBookmarked) {
          mBookmarkimage.setImageResource(R.drawable.star_grey_big);
          mBookmarked = false;
        } else {
          mBookmarkimage.setImageResource(R.drawable.star_yellow_big);
          mBookmarked = true;
        }
        dbServiceSubscriber.updateStudyPreferenceToDb(this, mStudyId, mBookmarked, mStudyStatus);
      }
      mStudyHome = dbServiceSubscriber.getStudyInfoListFromDB(mStudyId, mRealm);
      if (mStudyHome != null) {
        mConsentDocumentData = dbServiceSubscriber.getConsentDocumentFromDB(mStudyId, mRealm);
        getStudyWebsiteNull();
        setViewPagerView(mStudyHome);
      } else {
        Toast.makeText(StudyInfoActivity.this, errormsg, Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }

  private void callGetStudyInfoWebservice() {
    AppController.getHelperProgressDialog().showProgress(StudyInfoActivity.this, "", "", false);
    HashMap<String, String> header = new HashMap<>();
    String url = URLs.STUDY_INFO + "?studyId=" + mStudyId;
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    WCPConfigEvent wcpConfigEvent =
        new WCPConfigEvent(
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

    getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  public void updatebookmark(boolean b) {
    AppController.getHelperProgressDialog().showProgress(this, "", "", false);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

    HashMap<String, String> header = new HashMap();
    header.put(
        "accessToken",
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.userid), ""));
    header.put(
        "clientToken",
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.clientToken), ""));

    JSONObject jsonObject = new JSONObject();

    JSONArray studieslist = new JSONArray();
    JSONObject studies = new JSONObject();
    try {
      studies.put("studyId", mStudyId);
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
      int number = dbServiceSubscriber.getUniqueID(mRealm);
      if (number == 0) {
        number = 1;
      } else {
        number += 1;
      }
      String studyId = mStudyId;
      OfflineData offlineData = dbServiceSubscriber.getStudyIdOfflineData(studyId, mRealm);
      if (offlineData != null) {
        number = offlineData.getNumber();
      }
      mDeleteIndexNumberDB = number;
      AppController.pendingService(
          this,
          number,
          "post_object",
          URLs.UPDATE_STUDY_PREFERENCE,
          "",
          jsonObject.toString(),
          "RegistrationServerEnrollment",
          "",
          studyId,
          "");
    } catch (Exception e) {
      Logger.log(e);
    }
    //////////
    RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent =
        new RegistrationServerEnrollmentConfigEvent(
            "post_object",
            URLs.UPDATE_STUDY_PREFERENCE,
            UPDATE_PREFERENCES,
            this,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);

    updatePreferenceEvent.setRegistrationServerEnrollmentConfigEvent(
        registrationServerEnrollmentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  private void setViewPagerView(final StudyHome studyHome) {

    ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
    CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
    viewpager.setAdapter(
        new StudyInfoPagerAdapter(StudyInfoActivity.this, studyHome.getInfo(), mStudyId));
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
              mJoinButton.setBackground(
                  getResources().getDrawable(R.drawable.rectangle_blue_white));
              mJoinButton.setTextColor(getResources().getColor(R.color.white));
            } else {
              mJoinButton.setBackground(
                  getResources().getDrawable(R.drawable.rectangle_black_white));
              mJoinButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
          }
        });
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(mRealm);
    super.onDestroy();
  }
}
