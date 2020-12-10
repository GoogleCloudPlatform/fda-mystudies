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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.activitybuilder.ActivityBuilder;
import com.harvard.studyappmodule.activitybuilder.CustomSurveyViewTaskActivity;
import com.harvard.studyappmodule.activitybuilder.StepsBuilder;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityInfoData;
import com.harvard.studyappmodule.activitybuilder.model.servicemodel.ActivityObj;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.activitylistmodel.AnchorDateSchedulingDetails;
import com.harvard.studyappmodule.activitylistmodel.Frequency;
import com.harvard.studyappmodule.activitylistmodel.FrequencyRuns;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDate;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDateEnd;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDateStart;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.studyappmodule.events.GetActivityInfoEvent;
import com.harvard.studyappmodule.events.GetActivityListEvent;
import com.harvard.studyappmodule.events.GetResourceListEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.studymodel.MotivationalNotification;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.studymodel.StudyResource;
import com.harvard.studyappmodule.studymodel.StudyUpdate;
import com.harvard.studyappmodule.studymodel.StudyUpdateListdata;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.studyappmodule.surveyscheduler.model.ActivityStatus;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.ActivityStateEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.Activities;
import com.harvard.usermodule.webservicemodel.ActivityData;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ResponseDatastoreConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

public class SurveyActivitiesFragment extends Fragment
    implements ApiCall.OnAsyncRequestComplete,
        ActivityCompat.OnRequestPermissionsResultCallback,
        CustomActivitiesDailyDialogClass.DialogClick {
  private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
  private static final int PERMISSION_REQUEST_CODE = 1000;
  private static final int GET_PREFERENCES = 112;
  private static final int STUDY_UPDATES = 113;
  private static final int CONSENT_METADATA = 114;
  private static final int CONSENT_RESPONSECODE = 115;
  private static final int CONSENT_COMPLETE = 116;
  private static final int UPDATE_STUDY_PREFERENCE = 119;
  private static final int STUDY_INFO = 10;
  private static final int RESOURCE_REQUEST_CODE = 213;
  private RelativeLayout backBtn;
  private AppCompatTextView titleTv;
  private RelativeLayout filterBtn;
  private RecyclerView surveyActivitiesRecyclerView;
  private SwipeRefreshLayout swipeRefreshLayout;
  private Context context;
  private int currentRunId; // runid for webservice on click of activity
  private String activityStatusStr; // activityStatusStr for webservice on click of activity
  private String activityId; // activityId for webservice on click of activity
  private boolean branching; // branching for webservice on click of activity
  private String activityVersion; // activityVersion for webservice on click of activity

  private static final int ACTIVTTYLIST_RESPONSECODE = 100;
  private static final int ACTIVTTYINFO_RESPONSECODE = 101;
  public static final String YET_To_START = "yetToJoin";
  public static final String IN_PROGRESS = "inProgress";
  public static final String COMPLETED = "completed";
  public static final String INCOMPLETE = "abandoned";

  public static final String STATUS_CURRENT = "Current";
  public static final String STATUS_UPCOMING = "Upcoming";
  public static final String STATUS_COMPLETED = "Completed";
  public static final String FROM_SURVAY = "survey";

  private ActivityListData activityListData;

  private String eligibilityType = "";

  private OrderedTask task;
  private ActivityObj activityObj;
  private ActivityStatus activityStatusData;
  private boolean locationPermission = false;
  private int deleteIndexNumberDb;
  private EligibilityConsent eligibilityConsent;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private boolean activityUpdated = false;
  public static String DELETE = "deleted";
  private static String ACTIVE = "active";
  private SurveyActivitiesListAdapter studyVideoAdapter;
  private int filterPos = 0;
  private ArrayList<String> status = new ArrayList<>();
  private ArrayList<ActivitiesWS> activitiesArrayList1 = new ArrayList<>();
  private ArrayList<ActivityStatus> currentRunStatusForActivities = new ArrayList<>();
  private StudyResource studyResource;
  private StepsBuilder stepsBuilder;
  private ArrayList<AnchorDateSchedulingDetails> arrayList;
  private ActivityData activityDataDB;
  String title = "";

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_survey_activities, container, false);
    initializeXmlId(view);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(context);
    try {
      AppController.getHelperHideKeyboard((Activity) context);
    } catch (Exception e) {
      Logger.log(e);
    }
    setTextForView();
    setFont();
    bindEvents();
    getStudyUpdateFomWS(false);

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    try {
      AppController.getHelperHideKeyboard(getActivity());
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void initializeXmlId(View view) {
    backBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
    titleTv = (AppCompatTextView) view.findViewById(R.id.title);
    filterBtn = (RelativeLayout) view.findViewById(R.id.filterBtn);
    surveyActivitiesRecyclerView =
        (RecyclerView) view.findViewById(R.id.mSurveyActivitiesRecyclerView);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);

    AppCompatImageView backBtnimg = view.findViewById(R.id.backBtnimg);
    AppCompatImageView menubtnimg = view.findViewById(R.id.menubtnimg);

    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      backBtnimg.setVisibility(View.VISIBLE);
      menubtnimg.setVisibility(View.GONE);
    } else {
      backBtnimg.setVisibility(View.GONE);
      menubtnimg.setVisibility(View.VISIBLE);
    }
  }

  private void setTextForView() {
    titleTv.setText(context.getResources().getString(R.string.study_activities));
  }

  private void setFont() {
    try {
      titleTv.setTypeface(AppController.getTypeface(getActivity(), "bold"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
              Intent intent = new Intent(context, StudyActivity.class);
              ComponentName cn = intent.getComponent();
              Intent mainIntent = Intent.makeRestartActivityTask(cn);
              context.startActivity(mainIntent);
              ((Activity) context).finish();
            } else {
              ((SurveyActivity) context).openDrawer();
            }
          }
        });
    filterBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            final ArrayList<String> mScheduledTime = new ArrayList<>();
            mScheduledTime.add(context.getResources().getString(R.string.all));
            mScheduledTime.add(context.getResources().getString(R.string.surveys1));
            mScheduledTime.add(context.getResources().getString(R.string.tasks1));
            CustomActivitiesDailyDialogClass c =
                new CustomActivitiesDailyDialogClass(
                    context, mScheduledTime, filterPos, true, SurveyActivitiesFragment.this);
            c.show();
          }
        });
    swipeRefreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            getStudyUpdateFomWS(true);
          }
        });
  }

  void onItemsLoadComplete() {
    // Update the adapter and notify data set changed
    // Stop refresh animation
    swipeRefreshLayout.setRefreshing(false);
  }

  private void getStudyUpdateFomWS(boolean isSwipeToRefresh) {
    if (isSwipeToRefresh) {
      AppController.getHelperProgressDialog()
          .showSwipeListCustomProgress(getActivity(), R.drawable.transparent, false);
    } else {
      AppController.getHelperProgressDialog()
          .showProgressWithText(getActivity(), "", getString(R.string.activity_loading_msg), false);
    }

    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    StudyList studyList =
        dbServiceSubscriber.getStudiesDetails(((SurveyActivity) context).getStudyId(), realm);
    String url =
        Urls.STUDY_UPDATES
            + "?studyId="
            + ((SurveyActivity) context).getStudyId()
            + "&studyVersion="
            + studyList.getStudyVersion();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get", url, STUDY_UPDATES, context, StudyUpdate.class, null, header, null, false, this);

    getUserStudyListEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
  }

  private void setRecyclerView() {
    surveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    surveyActivitiesRecyclerView.setNestedScrollingEnabled(false);

    AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    GetActivityListEvent getActivityListEvent = new GetActivityListEvent();
    HashMap<String, String> header = new HashMap();
    String url = Urls.ACTIVITY_LIST + "?studyId=" + ((SurveyActivity) context).getStudyId();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            ACTIVTTYLIST_RESPONSECODE,
            context,
            ActivityListData.class,
            null,
            header,
            null,
            false,
            this);

    getActivityListEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetActivityList(getActivityListEvent);
  }

  private void callConsentMetaDataWebservice() {

    new CallConsentMetaData().execute();
  }

  private class CallConsentMetaData extends AsyncTask<String, Void, String> {
    String response = null;
    String responseCode = null;
    Responsemodel responseModel;

    @Override
    protected String doInBackground(String... params) {
      ConnectionDetector connectionDetector = new ConnectionDetector(context);

      String url =
          Urls.BASE_URL_STUDY_DATASTORE
              + Urls.CONSENT_METADATA
              + "?studyId="
              + ((SurveyActivity) context).getStudyId();
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
      onItemsLoadComplete();
      if (response != null) {
        if (response.equalsIgnoreCase("session expired")) {
          AppController.getHelperProgressDialog().dismissDialog();
          AppController.getHelperSessionExpired(context, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  context,
                  context.getResources().getString(R.string.connection_timeout),
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
            eligibilityConsent.setStudyId(((SurveyActivity) context).getStudyId());
            saveConsentToDB(context, eligibilityConsent);
            startConsent(
                eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
          } else {
            Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
          }
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  context,
                  context.getResources().getString(R.string.unable_to_retrieve_data),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    }
  }

  private void saveConsentToDB(Context context, EligibilityConsent eligibilityConsent) {
    DatabaseEvent databaseEvent = new DatabaseEvent();
    databaseEvent.setE(eligibilityConsent);
    databaseEvent.setType(DbServiceSubscriber.TYPE_COPY_UPDATE);
    databaseEvent.setaClass(EligibilityConsent.class);
    databaseEvent.setOperation(DbServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
    dbServiceSubscriber.insert(context, databaseEvent);
  }

  private void startConsent(Consent consent, String type) {
    eligibilityType = type;
    Toast.makeText(
            context,
            context.getResources().getString(R.string.please_review_the_updated_consent),
            Toast.LENGTH_SHORT)
        .show();
    StudyList studyList =
        dbServiceSubscriber.getStudiesDetails(((SurveyActivity) context).getStudyId(), realm);
    title = studyList.getTitle();
    ConsentBuilder consentBuilder = new ConsentBuilder();
    List<Step> consentStep =
        consentBuilder.createsurveyquestion(context, consent, studyList.getTitle());
    Task consentTask = new OrderedTask(StudyFragment.CONSENT, consentStep);
    Intent intent =
        CustomConsentViewTaskActivity.newIntent(
            context,
            consentTask,
            ((SurveyActivity) context).getStudyId(),
            "",
            title,
            eligibilityType,
            "update");
    startActivityForResult(intent, CONSENT_RESPONSECODE);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 123) {
      setRecyclerView();
    } else if (requestCode == CONSENT_RESPONSECODE) {
      if (resultCode == getActivity().RESULT_OK) {
        Intent intent = new Intent(getActivity(), ConsentCompletedActivity.class);
        intent.putExtra(ConsentCompletedActivity.FROM, FROM_SURVAY);
        intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
        intent.putExtra("title", title);
        intent.putExtra("eligibility", eligibilityType);
        intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
        // get the encrypted file path
        intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
        startActivityForResult(intent, CONSENT_COMPLETE);

      } else {
        Toast.makeText(context, R.string.consent_complete, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
        ((Activity) context).finish();
      }
    } else if (requestCode == CONSENT_COMPLETE) {
      if (resultCode == getActivity().RESULT_OK) {
        setRecyclerView();
      } else {
        Toast.makeText(context, R.string.consent_complete, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
        ((Activity) context).finish();
      }
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {

    if (responseCode == STUDY_UPDATES) {
      StudyUpdate studyUpdate = (StudyUpdate) response;
      studyUpdate.setStudyId(((SurveyActivity) context).getStudyId());
      StudyUpdateListdata studyUpdateListdata = new StudyUpdateListdata();

      RealmList<StudyUpdate> studyUpdates = new RealmList<>();
      studyUpdates.add(studyUpdate);
      studyUpdateListdata.setStudyUpdates(studyUpdates);
      dbServiceSubscriber.saveStudyUpdateListdataToDB(context, studyUpdateListdata);

      if (studyUpdate
          .getStudyUpdateData()
          .getStatus()
          .equalsIgnoreCase(getString(R.string.paused))) {
        AppController.getHelperProgressDialog().dismissDialog();
        onItemsLoadComplete();
        Toast.makeText(context, R.string.studyPaused, Toast.LENGTH_SHORT).show();
        ((Activity) context).finish();
      } else if (studyUpdate
          .getStudyUpdateData()
          .getStatus()
          .equalsIgnoreCase(getString(R.string.closed))) {
        AppController.getHelperProgressDialog().dismissDialog();
        onItemsLoadComplete();
        Toast.makeText(context, R.string.studyClosed, Toast.LENGTH_SHORT).show();
        ((Activity) context).finish();
      } else {

        if (studyUpdate.getStudyUpdateData().isResources()) {
          dbServiceSubscriber.deleteResourcesFromDb(
              context, ((SurveyActivity) context).getStudyId());
        }
        if (studyUpdate.getStudyUpdateData().isInfo()) {
          dbServiceSubscriber.deleteStudyInfoFromDb(
              context, ((SurveyActivity) context).getStudyId());
        }
        if (studyUpdate.getStudyUpdateData().isConsent()) {
          callConsentMetaDataWebservice();
        } else {
          StudyList studyList =
              dbServiceSubscriber.getStudyTitle(((SurveyActivity) context).getStudyId(), realm);
          dbServiceSubscriber.updateStudyPreferenceVersionDB(
              context, ((SurveyActivity) context).getStudyId(), studyList.getStudyVersion());
          setRecyclerView();
        }
      }

    } else if (responseCode == CONSENT_METADATA) {
      eligibilityConsent = (EligibilityConsent) response;
      if (eligibilityConsent != null) {
        eligibilityConsent.setStudyId(((SurveyActivity) context).getStudyId());
        saveConsentToDB(context, eligibilityConsent);
        startConsent(
            eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == ACTIVTTYLIST_RESPONSECODE) {
      activityListData = (ActivityListData) response;
      activityListData.setStudyId(((SurveyActivity) context).getStudyId());

      HashMap<String, String> header = new HashMap();
      header.put(
          "Authorization",
          "Bearer "
              + AppController.getHelperSharedPreference()
                  .readPreference(context, context.getResources().getString(R.string.auth), ""));
      header.put(
          "userId",
          AppController.getHelperSharedPreference()
              .readPreference(context, context.getResources().getString(R.string.userid), ""));
      Realm realm = AppController.getRealmobj(context);
      Studies studies =
          dbServiceSubscriber.getStudies(((SurveyActivity) context).getStudyId(), realm);

      String url =
          Urls.ACTIVITY_STATE
              + "?studyId="
              + ((SurveyActivity) context).getStudyId()
              + "&participantId="
              + studies.getParticipantId();
      ResponseDatastoreConfigEvent responseDatastoreConfigEvent =
          new ResponseDatastoreConfigEvent(
              "get",
              url,
              GET_PREFERENCES,
              context,
              ActivityData.class,
              null,
              header,
              null,
              false,
              this);
      ActivityStateEvent activityStateEvent = new ActivityStateEvent();
      activityStateEvent.setResponseDatastoreConfigEvent(responseDatastoreConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performActivityState(activityStateEvent);

    } else if (responseCode == GET_PREFERENCES) {
      ActivityData activityData1 = (ActivityData) response;
      activityData1.setStudyId(((SurveyActivity) context).getStudyId());
      ActivityData activityData = new ActivityData();
      RealmList<Activities> activities = new RealmList<>();
      activityData.setMessage(activityData1.getMessage());
      activityData.setActivities(activities);
      activityData.setStudyId(((SurveyActivity) context).getStudyId());
      activityDataDB =
          dbServiceSubscriber.getActivityPreference(((SurveyActivity) context).getStudyId(), realm);
      if (activityDataDB == null) {
        for (int i = 0; i < activityData1.getActivities().size(); i++) {
          activityData1.getActivities().get(i).setStudyId(((SurveyActivity) context).getStudyId());
          if (activityData1.getActivities().get(i).getActivityVersion() != null) {
            activityData.getActivities().add(activityData1.getActivities().get(i));
          }
        }
        dbServiceSubscriber.updateActivityState(context, activityData);
        activityDataDB =
            dbServiceSubscriber.getActivityPreference(
                ((SurveyActivity) context).getStudyId(), realm);
      }

      calculateStartAnsEndDateForActivities();

    } else if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
      AppController.getHelperProgressDialog().dismissDialog();
      onItemsLoadComplete();
      ActivityInfoData activityInfoData = (ActivityInfoData) response;
      if (activityInfoData != null) {
        launchSurvey(activityInfoData.getActivity());
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
      AppController.getHelperProgressDialog().dismissDialog();
      onItemsLoadComplete();
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        updateActivityInfo(activityId);
        // activityVersion
        dbServiceSubscriber.deleteOfflineDataRow(context, deleteIndexNumberDb);
        dbServiceSubscriber.updateActivityPreferenceDB(
            context,
            activityId,
            ((SurveyActivity) context).getStudyId(),
            currentRunId,
            SurveyActivitiesFragment.IN_PROGRESS,
            activityStatusData.getTotalRun(),
            activityStatusData.getCompletedRun(),
            activityStatusData.getMissedRun(),
            activityVersion);
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }

    } else if (responseCode == UPDATE_STUDY_PREFERENCE) {
      // check for notification
      AppController.getHelperProgressDialog().dismissDialog();
      getResourceListWebservice();
      onItemsLoadComplete();
      checkForNotification();
    } else if (responseCode == RESOURCE_REQUEST_CODE) {
      // call study info
      callGetStudyInfoWebservice();
      if (response != null) {
        studyResource = (StudyResource) response;
      }
    } else if (responseCode == STUDY_INFO) {
      if (response != null) {
        StudyHome studyHome = (StudyHome) response;
        ((SurveyActivity) context).getStudyId();
        String studyId = ((SurveyActivity) context).getStudyId();
        dbServiceSubscriber.saveStudyInfoToDB(context, studyHome);

        if (studyResource != null) {
          // primary key studyId
          studyResource.setStudyId(studyId);
          // remove duplicate and
          dbServiceSubscriber.deleteStudyResourceDuplicateRow(context, studyId);
          dbServiceSubscriber.saveResourceList(context, studyResource);
        }
      }
    } else {
      AppController.getHelperProgressDialog().dismissDialog();
      onItemsLoadComplete();
    }
  }

  private void calculateStartAnsEndDateForActivities() {
    // call to resp server to get anchorDate
    arrayList = new ArrayList<>();
    AnchorDateSchedulingDetails anchorDateSchedulingDetails;
    if (activityListData == null) {
      ActivityListData activityListDataTemp =
          dbServiceSubscriber.getActivities(((SurveyActivity) context).getStudyId(), realm);

      if (activityListDataTemp != null) {
        activityListData = realm.copyFromRealm(activityListDataTemp);
      }
    }
    if (activityDataDB == null) {
      ActivityData activityDataTemp =
          dbServiceSubscriber.getActivityPreference(((SurveyActivity) context).getStudyId(), realm);

      if (activityDataTemp != null) {
        activityDataDB = realm.copyFromRealm(activityDataTemp);
      }
    }
    if (activityListData != null
        && activityListData.getActivities() != null
        && activityDataDB != null) {
      for (int i = 0; i < activityListData.getActivities().size(); i++) {
        if (activityListData.getActivities().get(i).getSchedulingType() != null) {
          if (activityListData
              .getActivities()
              .get(i)
              .getSchedulingType()
              .equalsIgnoreCase("AnchorDate")) {
            Studies studies =
                dbServiceSubscriber.getStudies(((SurveyActivity) context).getStudyId(), realm);
            if (activityListData
                .getActivities()
                .get(i)
                .getAnchorDate()
                .getSourceType()
                .equalsIgnoreCase("ActivityResponse")) {
              anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
              anchorDateSchedulingDetails.setSourceActivityId(
                  activityListData.getActivities().get(i).getAnchorDate().getSourceActivityId());
              anchorDateSchedulingDetails.setSourceKey(
                  activityListData.getActivities().get(i).getAnchorDate().getSourceKey());
              anchorDateSchedulingDetails.setSourceFormKey(
                  activityListData.getActivities().get(i).getAnchorDate().getSourceFormKey());

              anchorDateSchedulingDetails.setSchedulingType(
                  activityListData.getActivities().get(i).getSchedulingType());
              anchorDateSchedulingDetails.setSourceType(
                  activityListData.getActivities().get(i).getAnchorDate().getSourceType());
              anchorDateSchedulingDetails.setStudyId(((SurveyActivity) context).getStudyId());
              anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
              anchorDateSchedulingDetails.setActivityVersion(
                  activityListData.getActivities().get(i).getActivityVersion());
              anchorDateSchedulingDetails.setTargetActivityId(
                  activityListData.getActivities().get(i).getActivityId());

              for (int j = 0; j < activityDataDB.getActivities().size(); j++) {
                if (activityDataDB
                    .getActivities()
                    .get(j)
                    .getActivityId()
                    .equalsIgnoreCase(anchorDateSchedulingDetails.getSourceActivityId())) {
                  anchorDateSchedulingDetails.setActivityState(
                      activityDataDB.getActivities().get(j).getStatus());
                  arrayList.add(anchorDateSchedulingDetails);
                  break;
                }
              }
            } else {
              // For enrollmentDate
              anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
              anchorDateSchedulingDetails.setSchedulingType(
                  activityListData.getActivities().get(i).getSchedulingType());
              anchorDateSchedulingDetails.setSourceType(
                  activityListData.getActivities().get(i).getAnchorDate().getSourceType());
              anchorDateSchedulingDetails.setStudyId(((SurveyActivity) context).getStudyId());
              anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
              anchorDateSchedulingDetails.setTargetActivityId(
                  activityListData.getActivities().get(i).getActivityId());
              anchorDateSchedulingDetails.setActivityVersion(
                  activityListData.getActivities().get(i).getActivityVersion());
              anchorDateSchedulingDetails.setAnchorDate(studies.getEnrolledDate());
              arrayList.add(anchorDateSchedulingDetails);
            }
          }
        }
      }
    }

    if (!arrayList.isEmpty()) {
      callLabkeyService(0);
    } else {
      metadataProcess();
    }
  }

  private void metadataProcess() {

    for (int i = 0; i < arrayList.size(); i++) {}

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    SimpleDateFormat dateSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timezoneSimpleDateFormat = new SimpleDateFormat("Z");
    Date date;

    ArrayList<String> activityIds = new ArrayList<>();
    ArrayList<String> runIds = new ArrayList<>();

    if (activityListData != null) {
      for (int i = 0; i < activityListData.getActivities().size(); i++) {
        if (activityListData.getActivities().get(i).getSchedulingType() != null) {
          if (activityListData
              .getActivities()
              .get(i)
              .getSchedulingType()
              .equalsIgnoreCase("AnchorDate")) {
            for (int j = 0; j < arrayList.size(); j++) {
              if (activityListData
                  .getActivities()
                  .get(i)
                  .getActivityId()
                  .equalsIgnoreCase(arrayList.get(j).getTargetActivityId())) {
                if (arrayList.get(j).getAnchorDate() != null
                    && !arrayList.get(j).getAnchorDate().equalsIgnoreCase("")) {
                  String startTime = "";
                  String endTime = "";
                  if (activityListData.getActivities().get(i).getAnchorDate() != null
                      && activityListData.getActivities().get(i).getAnchorDate().getStart()
                          != null) {
                    if (!activityListData
                        .getActivities()
                        .get(i)
                        .getAnchorDate()
                        .getStart()
                        .getTime()
                        .equalsIgnoreCase("")) {
                      startTime =
                          activityListData
                              .getActivities()
                              .get(i)
                              .getAnchorDate()
                              .getStart()
                              .getTime();
                    } else {
                      startTime = "00:00:00";
                    }
                  }
                  if (activityListData.getActivities().get(i).getAnchorDate() != null
                      && activityListData.getActivities().get(i).getAnchorDate().getEnd() != null) {
                    if (!activityListData
                        .getActivities()
                        .get(i)
                        .getAnchorDate()
                        .getEnd()
                        .getTime()
                        .equalsIgnoreCase("")) {
                      endTime =
                          activityListData
                              .getActivities()
                              .get(i)
                              .getAnchorDate()
                              .getEnd()
                              .getTime();
                    } else {
                      endTime = "23:59:59";
                    }
                  }

                  // to do run calculation and expecting source question has answered
                  RealmResults<ActivityRun> runs =
                      dbServiceSubscriber.getAllActivityRunFromDB(
                          activityListData.getStudyId(),
                          activityListData.getActivities().get(i).getActivityId(),
                          realm);
                  if (runs == null || runs.size() == 0) {
                    activityUpdated = true;
                    activityIds.add(activityListData.getActivities().get(i).getActivityId());
                    runIds.add("-1");
                  }

                  if (activityListData
                      .getActivities()
                      .get(i)
                      .getFrequency()
                      .getType()
                      .equalsIgnoreCase("One Time")) {
                    Calendar calendar;
                    if (activityListData.getActivities().get(i).getAnchorDate() != null
                        && activityListData.getActivities().get(i).getAnchorDate().getStart()
                            != null) {
                      calendar = Calendar.getInstance();
                      try {
                        date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                        calendar.setTime(date);
                        calendar.add(
                            Calendar.DATE,
                            activityListData
                                .getActivities()
                                .get(i)
                                .getAnchorDate()
                                .getStart()
                                .getAnchorDays());
                      } catch (ParseException e) {
                        Logger.log(e);
                      }

                      activityListData
                          .getActivities()
                          .get(i)
                          .setStartTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + startTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                    if (activityListData.getActivities().get(i).getAnchorDate() != null
                        && activityListData.getActivities().get(i).getAnchorDate().getEnd()
                            != null) {
                      calendar = Calendar.getInstance();
                      try {
                        date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                        calendar.setTime(date);
                        calendar.add(
                            Calendar.DATE,
                            activityListData
                                .getActivities()
                                .get(i)
                                .getAnchorDate()
                                .getEnd()
                                .getAnchorDays());
                      } catch (ParseException e) {
                        Logger.log(e);
                      }

                      activityListData
                          .getActivities()
                          .get(i)
                          .setEndTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + endTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                  } else if (activityListData
                      .getActivities()
                      .get(i)
                      .getFrequency()
                      .getType()
                      .equalsIgnoreCase("Daily")) {
                    if (activityListData
                        .getActivities()
                        .get(i)
                        .getStartTime()
                        .equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                        calendar.setTime(date);
                        calendar.add(
                            Calendar.DATE,
                            activityListData
                                .getActivities()
                                .get(i)
                                .getAnchorDate()
                                .getStart()
                                .getAnchorDays());
                      } catch (ParseException e) {
                        Logger.log(e);
                      }
                      activityListData
                          .getActivities()
                          .get(i)
                          .setStartTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + startTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                    if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        calendar.setTime(
                            simpleDateFormat.parse(
                                activityListData.getActivities().get(i).getStartTime()));
                      } catch (ParseException e) {
                        Logger.log(e);
                      }
                      calendar.add(
                          Calendar.DATE,
                          activityListData
                              .getActivities()
                              .get(i)
                              .getAnchorDate()
                              .getEnd()
                              .getRepeatInterval());
                      activityListData
                          .getActivities()
                          .get(i)
                          .setEndTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + endTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                  } else if (activityListData
                      .getActivities()
                      .get(i)
                      .getFrequency()
                      .getType()
                      .equalsIgnoreCase("Weekly")) {
                    if (activityListData
                        .getActivities()
                        .get(i)
                        .getStartTime()
                        .equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                        calendar.setTime(date);
                        calendar.add(
                            Calendar.DATE,
                            activityListData
                                .getActivities()
                                .get(i)
                                .getAnchorDate()
                                .getStart()
                                .getAnchorDays());
                      } catch (ParseException e) {
                        Logger.log(e);
                      }

                      activityListData
                          .getActivities()
                          .get(i)
                          .setStartTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + startTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                    if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        calendar.setTime(
                            simpleDateFormat.parse(
                                activityListData.getActivities().get(i).getStartTime()));
                      } catch (ParseException e) {
                        Logger.log(e);
                      }
                      calendar.add(
                          Calendar.WEEK_OF_YEAR,
                          activityListData
                              .getActivities()
                              .get(i)
                              .getAnchorDate()
                              .getEnd()
                              .getRepeatInterval());
                      activityListData
                          .getActivities()
                          .get(i)
                          .setEndTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + endTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                  } else if (activityListData
                      .getActivities()
                      .get(i)
                      .getFrequency()
                      .getType()
                      .equalsIgnoreCase("Monthly")) {
                    if (activityListData
                        .getActivities()
                        .get(i)
                        .getStartTime()
                        .equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                        calendar.setTime(date);
                        calendar.add(
                            Calendar.DATE,
                            activityListData
                                .getActivities()
                                .get(i)
                                .getAnchorDate()
                                .getStart()
                                .getAnchorDays());
                      } catch (ParseException e) {
                        Logger.log(e);
                      }
                      activityListData
                          .getActivities()
                          .get(i)
                          .setStartTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + startTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                    if (activityListData.getActivities().get(i).getEndTime().equalsIgnoreCase("")) {
                      Calendar calendar = Calendar.getInstance();
                      try {
                        calendar.setTime(
                            simpleDateFormat.parse(
                                activityListData.getActivities().get(i).getStartTime()));
                      } catch (ParseException e) {
                        Logger.log(e);
                      }
                      calendar.add(
                          Calendar.MONTH,
                          activityListData
                              .getActivities()
                              .get(i)
                              .getAnchorDate()
                              .getEnd()
                              .getRepeatInterval());
                      activityListData
                          .getActivities()
                          .get(i)
                          .setEndTime(
                              dateSimpleDateFormat.format(calendar.getTime())
                                  + "T"
                                  + endTime
                                  + ".000"
                                  + timezoneSimpleDateFormat.format(calendar.getTime()));
                    }
                  } else {
                    // custom runs
                    if (activityListData.getActivities().get(i).getStartTime().equalsIgnoreCase("")
                        && activityListData
                            .getActivities()
                            .get(i)
                            .getEndTime()
                            .equalsIgnoreCase("")) {
                      Calendar startCalendar;
                      Calendar endCalendar;
                      for (int k = 0;
                          k
                              < activityListData
                                  .getActivities()
                                  .get(i)
                                  .getFrequency()
                                  .getAnchorRuns()
                                  .size();
                          k++) {
                        startCalendar = Calendar.getInstance();
                        endCalendar = Calendar.getInstance();

                        // start runs
                        try {
                          date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                          startCalendar.setTime(date);
                          startCalendar.add(
                              Calendar.DATE,
                              activityListData
                                  .getActivities()
                                  .get(i)
                                  .getFrequency()
                                  .getAnchorRuns()
                                  .get(k)
                                  .getStartDays());
                        } catch (ParseException e) {
                          Logger.log(e);
                        }
                        activityListData
                            .getActivities()
                            .get(i)
                            .getFrequency()
                            .getRuns()
                            .get(k)
                            .setStartTime(
                                dateSimpleDateFormat.format(startCalendar.getTime())
                                    + "T"
                                    + activityListData
                                        .getActivities()
                                        .get(i)
                                        .getFrequency()
                                        .getAnchorRuns()
                                        .get(k)
                                        .getTime()
                                    + ".000"
                                    + timezoneSimpleDateFormat.format(startCalendar.getTime()));

                        // end runs
                        try {
                          date = simpleDateFormat.parse(arrayList.get(j).getAnchorDate());
                          endCalendar.setTime(date);
                          endCalendar.add(
                              Calendar.DATE,
                              activityListData
                                  .getActivities()
                                  .get(i)
                                  .getFrequency()
                                  .getAnchorRuns()
                                  .get(k)
                                  .getEndDays());
                        } catch (ParseException e) {
                          Logger.log(e);
                        }
                        activityListData
                            .getActivities()
                            .get(i)
                            .getFrequency()
                            .getRuns()
                            .get(k)
                            .setEndTime(
                                dateSimpleDateFormat.format(endCalendar.getTime())
                                    + "T"
                                    + activityListData
                                        .getActivities()
                                        .get(i)
                                        .getFrequency()
                                        .getAnchorRuns()
                                        .get(k)
                                        .getTime()
                                    + ".000"
                                    + timezoneSimpleDateFormat.format(endCalendar.getTime()));

                        activityListData
                            .getActivities()
                            .get(i)
                            .setStartTime(
                                activityListData
                                    .getActivities()
                                    .get(i)
                                    .getFrequency()
                                    .getRuns()
                                    .get(0)
                                    .getStartTime());
                        activityListData
                            .getActivities()
                            .get(i)
                            .setEndTime(
                                activityListData
                                    .getActivities()
                                    .get(i)
                                    .getFrequency()
                                    .getRuns()
                                    .get(k)
                                    .getEndTime());
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    // If any activities available in Db we take from Db otherwise from Webservice

    // find any updates on available activity
    if (activityDataDB != null && activityListData != null) {
      for (int j = 0; j < activityListData.getActivities().size(); j++) {
        boolean activityAvailable = false;
        for (int i = 0; i < activityDataDB.getActivities().size(); i++) {

          if (activityDataDB
              .getActivities()
              .get(i)
              .getActivityId()
              .equalsIgnoreCase(activityListData.getActivities().get(j).getActivityId())) {
            activityAvailable = true;
            if (!activityDataDB
                .getActivities()
                .get(i)
                .getActivityVersion()
                .equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
              activityUpdated = true;
              // update ActivityWS DB with new version
              dbServiceSubscriber.updateActivitiesWsVersion(
                  activityListData.getActivities().get(j).getActivityId(),
                  activityListData.getStudyId(),
                  realm,
                  activityListData.getActivities().get(j).getActivityVersion());
              dbServiceSubscriber.updateActivityPreferenceVersion(
                  context,
                  activityListData.getActivities().get(j).getActivityVersion(),
                  activityDataDB.getActivities().get(i));
              if (activityIds.contains(activityDataDB.getActivities().get(i).getActivityId())) {
                // change on 15/10/2019

                runIds.set(
                    activityIds.indexOf(activityDataDB.getActivities().get(i).getActivityId()),
                    activityDataDB.getActivities().get(i).getActivityRunId());
              } else {
                // change on 15/10/2019
                activityIds.add(activityDataDB.getActivities().get(i).getActivityId());

                runIds.add(activityDataDB.getActivities().get(i).getActivityRunId());
              }
            }
            break;
          }
        }
        // change on 16/10/2019
        if (!activityAvailable) {
          ActivitiesWS activitiesWS =
              dbServiceSubscriber.getActivityObj(
                  activityListData.getActivities().get(j).getActivityId(),
                  activityListData.getStudyId(),
                  realm);
          if (activitiesWS != null
              && !activitiesWS
                  .getActivityVersion()
                  .equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
            activityUpdated = true;
            // update ActivityWS DB with new version
            dbServiceSubscriber.updateActivitiesWsVersion(
                activityListData.getActivities().get(j).getActivityId(),
                activityListData.getStudyId(),
                realm,
                activityListData.getActivities().get(j).getActivityVersion());
            if (!activityIds.contains(activityListData.getActivities().get(j).getActivityId())) {

              activityIds.add(activityListData.getActivities().get(j).getActivityId());

              runIds.add("-1");
            }
          }
        }
      }
    } else if (activityDataDB == null && activityListData != null) {
      for (int j = 0; j < activityListData.getActivities().size(); j++) {
        ActivitiesWS activitiesWS =
            dbServiceSubscriber.getActivityObj(
                activityListData.getActivities().get(j).getActivityId(),
                activityListData.getStudyId(),
                realm);
        if (activitiesWS != null
            && !activitiesWS
                .getActivityVersion()
                .equalsIgnoreCase(activityListData.getActivities().get(j).getActivityVersion())) {
          activityUpdated = true;
          // update ActivityWS DB with new version
          dbServiceSubscriber.updateActivitiesWsVersion(
              activityListData.getActivities().get(j).getActivityId(),
              activityListData.getStudyId(),
              realm,
              activityListData.getActivities().get(j).getActivityVersion());
          if (!activityIds.contains(activityListData.getActivities().get(j).getActivityId())) {

            activityIds.add(activityListData.getActivities().get(j).getActivityId());

            runIds.add("-1");
          }
        }
      }
    }

    displayData(activityListData, activityIds, runIds, null);
  }

  private void getResourceListWebservice() {

    HashMap<String, String> header = new HashMap<>();
    String studyId = ((SurveyActivity) context).getStudyId();
    header.put("studyId", studyId);
    String url = Urls.RESOURCE_LIST + "?studyId=" + studyId;
    GetResourceListEvent getResourceListEvent = new GetResourceListEvent();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            RESOURCE_REQUEST_CODE,
            getActivity(),
            StudyResource.class,
            null,
            header,
            null,
            false,
            this);

    getResourceListEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetResourceListEvent(getResourceListEvent);
  }

  private void callGetStudyInfoWebservice() {
    String studyId = ((SurveyActivity) context).getStudyId();
    HashMap<String, String> header = new HashMap<>();
    String url = Urls.STUDY_INFO + "?studyId=" + studyId;
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            STUDY_INFO,
            getActivity(),
            StudyHome.class,
            null,
            header,
            null,
            false,
            this);

    getUserStudyInfoEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  private void checkForNotification() {
    if (((SurveyActivity) context).from.equalsIgnoreCase("NotificationActivity")
        && ((SurveyActivity) context).localNotification.equalsIgnoreCase("true")
        && !((SurveyActivity) context).to.equalsIgnoreCase("Resource")) {
      ((SurveyActivity) context).from = "";
      ((SurveyActivity) context).localNotification = "";
      ((SurveyActivity) context).to = "";
      int position = 0;
      for (int i = 0; i < studyVideoAdapter.items.size(); i++) {
        if (studyVideoAdapter.items.get(i).getActivityId() != null
            && studyVideoAdapter
                .items
                .get(i)
                .getActivityId()
                .equalsIgnoreCase(((SurveyActivity) context).activityId)) {
          position = i;
          break;
        }
      }
      StudyList studyList =
          dbServiceSubscriber.getStudiesDetails(((SurveyActivity) context).getStudyId(), realm);
      boolean paused;
      if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
        paused = true;
      } else {
        paused = false;
      }
      if (paused) {
        Toast.makeText(context, R.string.study_Joined_paused, Toast.LENGTH_SHORT).show();
      } else {
        if (studyVideoAdapter
                .status
                .get(position)
                .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)
            && (studyVideoAdapter
                    .currentRunStatusForActivities
                    .get(position)
                    .getStatus()
                    .equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)
                || studyVideoAdapter
                    .currentRunStatusForActivities
                    .get(position)
                    .getStatus()
                    .equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START))) {
          if (studyVideoAdapter.currentRunStatusForActivities.get(position).isRunIdAvailable()) {
            getActivityInfo(
                studyVideoAdapter.items.get(position).getActivityId(),
                studyVideoAdapter.currentRunStatusForActivities.get(position).getCurrentRunId(),
                studyVideoAdapter.currentRunStatusForActivities.get(position).getStatus(),
                studyVideoAdapter.items.get(position).getBranching(),
                studyVideoAdapter.items.get(position).getActivityVersion(),
                studyVideoAdapter.currentRunStatusForActivities.get(position),
                studyVideoAdapter.items.get(position));
          } else {
            Toast.makeText(
                    context,
                    context.getResources().getString(R.string.survey_message),
                    Toast.LENGTH_SHORT)
                .show();
          }
        } else if (studyVideoAdapter
            .status
            .get(position)
            .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
          Toast.makeText(context, R.string.upcoming_event, Toast.LENGTH_SHORT).show();
        } else if (studyVideoAdapter
            .currentRunStatusForActivities
            .get(position)
            .getStatus()
            .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
          Toast.makeText(context, R.string.incomple_event, Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(context, R.string.completed_event, Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  private void displayData(
      ActivityListData activityListData,
      ArrayList<String> activityIds,
      ArrayList<String> runIds,
      String errormsg) {
    new CalculateRuns(activityListData, activityIds, runIds, errormsg).execute();
  }

  @Override
  public void clicked(int positon) {
    StudyList studyList =
        dbServiceSubscriber.getStudiesDetails(((SurveyActivity) context).getStudyId(), realm);
    boolean paused;
    if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
      paused = true;
    } else {
      paused = false;
    }
    SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
    StudyData studyPreferences = dbServiceSubscriber.getStudyPreference(realm);
    Date joiningDate =
        survayScheduler.getJoiningDateOfStudy(
            studyPreferences, ((SurveyActivity) context).getStudyId());
    filterPos = positon;
    Filter filter = getFilterList();
    studyVideoAdapter =
        new SurveyActivitiesListAdapter(
            context,
            filter.getActivitiesArrayList1(),
            filter.getStatus(),
            filter.getCurrentRunStatusForActivities(),
            SurveyActivitiesFragment.this,
            paused,
            joiningDate);
    surveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    surveyActivitiesRecyclerView.setAdapter(studyVideoAdapter);
  }

  private class CalculateRuns
      extends AsyncTask<ArrayList<ActivitiesWS>, Void, ArrayList<ActivitiesWS>> {

    ArrayList<ActivitiesWS> currentactivityList = new ArrayList<>();
    ArrayList<String> currentStatus = new ArrayList<>();
    ArrayList<ActivityStatus> currentActivityStatus = new ArrayList<>();
    ArrayList<ActivitiesWS> upcomingactivityList = new ArrayList<>();
    ArrayList<String> upcomingStatus = new ArrayList<>();
    ArrayList<ActivityStatus> upcomingActivityStatus = new ArrayList<>();
    ArrayList<ActivitiesWS> completedactivityList = new ArrayList<>();
    ArrayList<String> completedStatus = new ArrayList<>();
    ArrayList<ActivityStatus> completedActivityStatus = new ArrayList<>();
    private boolean updateRun = true;
    private ActivityListData activityListData;
    private ActivityListData activityListData2 = new ActivityListData();
    private ArrayList<String> activityIds;
    private ArrayList<String> runIds;
    int completed = 0;
    int missed = 0;
    int total = 0;
    RealmList<ActivitiesWS> activitiesArrayList = new RealmList<>();

    Realm realm;
    String errormsg;

    CalculateRuns(
        ActivityListData activityListData,
        ArrayList<String> activityIds,
        ArrayList<String> runIds,
        String errormsg) {
      this.activityListData = activityListData;
      this.activityIds = activityIds;
      this.runIds = runIds;
      this.errormsg = errormsg;
    }

    @Override
    protected ArrayList<ActivitiesWS> doInBackground(ArrayList<ActivitiesWS>... params) {
      realm = AppController.getRealmobj(context);

      try {
        currentactivityList.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        upcomingactivityList.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        completedactivityList.clear();
      } catch (Exception e) {
        Logger.log(e);
      }

      // find new activity and deleted activity

      RealmList<ActivitiesWS> activitiesWSesDeleted = new RealmList<>();
      RealmList<ActivitiesWS> newlyAdded = new RealmList<>();
      ActivityListData activityListDataDB =
          dbServiceSubscriber.getActivities(((SurveyActivity) context).getStudyId(), realm);
      if (activityListDataDB != null
          && activityListDataDB.getActivities() != null
          && activityListDataDB.getActivities().size() > 0) {
        ActivityListData activityListData1 = null;
        activityUpdated = false;
        activityListData1 = new ActivityListData();
        activityListData1.setStudyId(activityListDataDB.getStudyId());
        activityListData1.setMessage(activityListDataDB.getMessage());
        activityListData1.setAnchorDate(activityListDataDB.getAnchorDate());
        activityListData1.setWithdrawalConfig(activityListDataDB.getWithdrawalConfig());

        if (activityListData == null) {
          activityListData1.getActivities().addAll(activityListDataDB.getActivities());
        } else {
          activityListData1.getActivities().addAll(activityListData.getActivities());

          for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
            boolean activityAvailable = false;
            for (int j = 0; j < activityListData.getActivities().size(); j++) {
              if (activityListData
                  .getActivities()
                  .get(j)
                  .getActivityId()
                  .equalsIgnoreCase(activityListDataDB.getActivities().get(i).getActivityId())) {
                activityAvailable = true;
                if (activityListDataDB.getActivities().get(i).getStartTime().equalsIgnoreCase("")
                    && !activityListData
                        .getActivities()
                        .get(j)
                        .getStartTime()
                        .equalsIgnoreCase("")) {
                  dbServiceSubscriber.saveActivityStartTime(
                      activityListDataDB.getActivities().get(i),
                      realm,
                      activityListData.getActivities().get(j).getStartTime());
                }
                if (activityListDataDB.getActivities().get(i).getEndTime().equalsIgnoreCase("")
                    && !activityListData.getActivities().get(j).getEndTime().equalsIgnoreCase("")) {
                  dbServiceSubscriber.saveActivityEndTime(
                      activityListDataDB.getActivities().get(i),
                      realm,
                      activityListData.getActivities().get(j).getEndTime());
                }
              }
            }
            if (!activityAvailable) {
              activityUpdated = true;
              activitiesWSesDeleted.add(activityListDataDB.getActivities().get(i));
              activityListData1.getActivities().add(activityListDataDB.getActivities().get(i));
            }
          }

          for (int j = 0; j < activityListData.getActivities().size(); j++) {
            boolean activityAvailable = false;
            for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
              if (activityListData
                  .getActivities()
                  .get(j)
                  .getActivityId()
                  .equalsIgnoreCase(activityListDataDB.getActivities().get(i).getActivityId())) {
                activityAvailable = true;
                if (activityListData.getActivities().get(j).getState().equalsIgnoreCase(DELETE)
                    && activityListDataDB
                        .getActivities()
                        .get(i)
                        .getState()
                        .equalsIgnoreCase(ACTIVE)) {
                  RealmResults<ActivityRun> activityRuns =
                      dbServiceSubscriber.getAllActivityRunFromDB(
                          ((SurveyActivity) context).getStudyId(),
                          activityListData.getActivities().get(j).getActivityId(),
                          realm);
                  try {
                    dbServiceSubscriber.deleteAllRun(context, activityRuns);
                  } catch (Exception e) {
                    Logger.log(e);
                  }
                  dbServiceSubscriber.saveActivityState(
                      activityListDataDB.getActivities().get(i), realm);
                }
              }
            }
            if (!activityAvailable) {
              newlyAdded.add(activityListData.getActivities().get(j));
            }
          }
        }

        updateRun = false;

        activityListData2 = activityListData1;

      } else {
        activityUpdated = false;
        if (activityListData != null) {
          insertAndUpdateToDB(context, activityListData);
          activityListData2 = activityListData;
        }
      }

      if (activityListData2 != null) {
        activitiesArrayList.addAll(activityListData2.getActivities());
        SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
        StudyData studyPreferences = dbServiceSubscriber.getStudyPreference(realm);
        ActivityData activityData =
            dbServiceSubscriber.getActivityPreference(
                ((SurveyActivity) context).getStudyId(), realm);
        Date joiningDate =
            survayScheduler.getJoiningDateOfStudy(
                studyPreferences, ((SurveyActivity) context).getStudyId());

        Date currentDate = new Date();

        if (activityUpdated) {
          dbServiceSubscriber.deleteMotivationalNotification(
              context, ((SurveyActivity) context).getStudyId());
        }

        if (newlyAdded.size() > 0) {
          // insert to activitylist db
          // activityListDataDB
          for (int k = 0; k < newlyAdded.size(); k++) {
            dbServiceSubscriber.addActivityWsList(context, activityListDataDB, newlyAdded.get(k));
          }
        }

        for (int i = 0; i < activitiesArrayList.size(); i++) {
          SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
          Date starttime = null;
          Date endtime = null;
          if (activitiesArrayList.get(i) != null
              && activitiesArrayList.get(i).getSchedulingType() != null
              && activitiesArrayList.get(i).getSchedulingType().equalsIgnoreCase("AnchorDate")) {

            if (!activitiesArrayList.get(i).getStartTime().equalsIgnoreCase("")) {
              if ((activitiesArrayList.get(i).getEndTime().equalsIgnoreCase("")
                      && activitiesArrayList.get(i).getAnchorDate() != null
                      && activitiesArrayList.get(i).getAnchorDate().getEnd() != null)
                  || !activitiesArrayList.get(i).getEndTime().equalsIgnoreCase("")) {
                try {
                  starttime =
                      simpleDateFormat.parse(
                          activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
                } catch (ParseException e) {
                  Logger.log(e);
                }
                try {
                  endtime =
                      simpleDateFormat.parse(
                          activitiesArrayList.get(i).getEndTime().split("\\.")[0]);
                } catch (ParseException e) {
                  Logger.log(e);
                } catch (Exception e1) {
                  Logger.log(e1);
                }
              } else {
                if (activitiesArrayList.get(i).getFrequency().getType().equalsIgnoreCase("One Time")
                    && activitiesArrayList.get(i).getAnchorDate() != null
                    && activitiesArrayList.get(i).getAnchorDate().getEnd() == null) {
                  try {
                    starttime =
                        simpleDateFormat.parse(
                            activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
                  } catch (ParseException e) {
                    Logger.log(e);
                  }
                }
              }
            }
          } else {
            try {
              if (activitiesArrayList.get(i).getStartTime().equalsIgnoreCase("")) {
                starttime = new Date();
              } else {
                starttime =
                    simpleDateFormat.parse(
                        activitiesArrayList.get(i).getStartTime().split("\\.")[0]);
              }
            } catch (ParseException e) {
              Logger.log(e);
            }
            try {
              endtime =
                  simpleDateFormat.parse(activitiesArrayList.get(i).getEndTime().split("\\.")[0]);
            } catch (ParseException e) {
              Logger.log(e);
            } catch (Exception e1) {
              Logger.log(e1);
            }
          }

          RealmResults<ActivityRun> activityRuns =
              dbServiceSubscriber.getAllActivityRunFromDB(
                  ((SurveyActivity) context).getStudyId(),
                  activitiesArrayList.get(i).getActivityId(),
                  realm);

          boolean deleted = false;
          for (int j = 0; j < activitiesWSesDeleted.size(); j++) {
            if (activitiesWSesDeleted
                .get(j)
                .getActivityId()
                .equalsIgnoreCase(activitiesArrayList.get(i).getActivityId())) {
              deleted = true;
              try {
                dbServiceSubscriber.deleteAllRun(context, activityRuns);
              } catch (Exception e) {
                Logger.log(e);
              }
            }
          }

          if (updateRun || activityRuns == null || activityRuns.size() == 0) {
            if (!deleted) {
              survayScheduler.setRuns(
                  activitiesArrayList.get(i),
                  ((SurveyActivity) context).getStudyId(),
                  starttime,
                  endtime,
                  joiningDate,
                  context);
            }
          } else if (activityIds.size() > 0) {
            // remove runs for these Ids and set runs once again
            if (activityIds.contains(activitiesArrayList.get(i).getActivityId())) {
              dbServiceSubscriber.deleteActivityRunsFromDb(
                  context,
                  activitiesArrayList.get(i).getActivityId(),
                  ((SurveyActivity) context).getStudyId());
              if (!deleted) {
                survayScheduler.setRuns(
                    activitiesArrayList.get(i),
                    ((SurveyActivity) context).getStudyId(),
                    starttime,
                    endtime,
                    joiningDate,
                    context);
              }
              // delete activity object that used for survey
              dbServiceSubscriber.deleteActivityObjectFromDb(
                  context,
                  activitiesArrayList.get(i).getActivityId(),
                  ((SurveyActivity) context).getStudyId());
              for (int j = 0; j < activityData.getActivities().size(); j++) {
                if (activitiesArrayList
                    .get(i)
                    .getActivityId()
                    .equalsIgnoreCase(activityData.getActivities().get(j).getActivityId())) {
                  if (!activityData
                      .getActivities()
                      .get(j)
                      .getStatus()
                      .equalsIgnoreCase(YET_To_START)) {
                    // Delete response data
                    if (!runIds
                        .get(activityIds.indexOf(activitiesArrayList.get(i).getActivityId()))
                        .equalsIgnoreCase("-1")) {
                      dbServiceSubscriber.deleteResponseDataFromDb(
                          context,
                          ((SurveyActivity) context).getStudyId()
                              + "_STUDYID_"
                              + activitiesArrayList.get(i).getActivityId()
                              + "_"
                              + runIds.get(
                                  activityIds.indexOf(activitiesArrayList.get(i).getActivityId())));
                    }
                  }
                }
              }
            }
          }

          String currentDateString = AppController.getDateFormatForApi().format(currentDate);
          try {
            currentDate = AppController.getDateFormatForApi().parse(currentDateString);
          } catch (ParseException e) {
            Logger.log(e);
          }
          Calendar calendarCurrentTime = Calendar.getInstance();
          calendarCurrentTime.setTime(currentDate);
          calendarCurrentTime.setTimeInMillis(
              calendarCurrentTime.getTimeInMillis() - survayScheduler.getOffset(context));
          if (!deleted) {
            ActivityStatus activityStatus =
                survayScheduler.getActivityStatus(
                    activityData,
                    ((SurveyActivity) context).getStudyId(),
                    activitiesArrayList.get(i).getActivityId(),
                    calendarCurrentTime.getTime(),
                    activitiesArrayList.get(i));
            if (activityStatus != null) {
              if (activityStatus.getCompletedRun() >= 0) {
                completed = completed + activityStatus.getCompletedRun();
              }
              if (activityStatus.getMissedRun() >= 0) {
                missed = missed + activityStatus.getMissedRun();
              }
              if (activityStatus.getTotalRun() >= 0) {
                total = total + activityStatus.getTotalRun();
              }
            }
            if (!activitiesArrayList.get(i).getState().equalsIgnoreCase("deleted")) {
              if (starttime != null) {
                if (AppController.isWithinRange(starttime, endtime)) {
                  currentactivityList.add(activitiesArrayList.get(i));
                  currentActivityStatus.add(activityStatus);
                  currentStatus.add(STATUS_CURRENT);
                } else if (AppController.checkafter(starttime)) {
                  upcomingactivityList.add(activitiesArrayList.get(i));
                  upcomingActivityStatus.add(activityStatus);
                  upcomingStatus.add(STATUS_UPCOMING);
                } else {
                  completedactivityList.add(activitiesArrayList.get(i));
                  completedActivityStatus.add(activityStatus);
                  completedStatus.add(STATUS_COMPLETED);
                }
              }
            } else {
              NotificationModuleSubscriber notificationModuleSubscriber =
                  new NotificationModuleSubscriber(dbServiceSubscriber, realm);
              try {
                notificationModuleSubscriber.cancleActivityLocalNotificationByIds(
                    context,
                    activitiesArrayList.get(i).getActivityId(),
                    ((SurveyActivity) context).getStudyId());
              } catch (Exception e) {
                Logger.log(e);
              }
              try {
                notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(
                    context,
                    activitiesArrayList.get(i).getActivityId(),
                    ((SurveyActivity) context).getStudyId());
              } catch (Exception e) {
                Logger.log(e);
              }
            }
          } else {
            NotificationModuleSubscriber notificationModuleSubscriber =
                new NotificationModuleSubscriber(dbServiceSubscriber, realm);
            try {
              notificationModuleSubscriber.cancleActivityLocalNotificationByIds(
                  context,
                  activitiesArrayList.get(i).getActivityId(),
                  ((SurveyActivity) context).getStudyId());
            } catch (Exception e) {
              Logger.log(e);
            }
            try {
              notificationModuleSubscriber.cancleResourcesLocalNotificationByIds(
                  context,
                  activitiesArrayList.get(i).getActivityId(),
                  ((SurveyActivity) context).getStudyId());
            } catch (Exception e) {
              Logger.log(e);
            }

            // delete from activity list db
            dbServiceSubscriber.deleteActivityWsList(
                context, activityListDataDB, activitiesArrayList.get(i).getActivityId());
          }
        }

        activitiesArrayList.clear();
      } else {
        if (errormsg != null) {
          Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
        }
      }

      // sort
      for (int i = 0; i < currentactivityList.size(); i++) {
        for (int j = i; j < currentactivityList.size(); j++) {
          try {
            if (AppController.getDateFormatForApi()
                .parse(currentactivityList.get(i).getStartTime())
                .after(
                    AppController.getDateFormatForApi()
                        .parse(currentactivityList.get(j).getStartTime()))) {
              ActivitiesWS activitiesWS = currentactivityList.get(i);
              currentactivityList.set(i, currentactivityList.get(j));
              currentactivityList.set(j, activitiesWS);

              ActivityStatus activityStatus = currentActivityStatus.get(i);
              currentActivityStatus.set(i, currentActivityStatus.get(j));
              currentActivityStatus.set(j, activityStatus);

              String status = currentStatus.get(i);
              currentStatus.set(i, currentStatus.get(j));
              currentStatus.set(j, status);
            }
          } catch (ParseException e) {
            Logger.log(e);
          }
        }
      }

      ArrayList<ActivitiesWS> yetToStartOrResumeList = new ArrayList<>();
      ArrayList<ActivitiesWS> otherList = new ArrayList<>();
      ArrayList<ActivityStatus> yetToStartOrResumeActivityStatusList = new ArrayList<>();
      ArrayList<ActivityStatus> otherActivityStatusList = new ArrayList<>();
      ArrayList<String> yetToStartOrResumeStatusList = new ArrayList<>();
      ArrayList<String> otherStatusList = new ArrayList<>();
      for (int i = 0; i < currentactivityList.size(); i++) {
        if (currentActivityStatus
                .get(i)
                .getStatus()
                .equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START)
            || currentActivityStatus
                .get(i)
                .getStatus()
                .equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)) {
          yetToStartOrResumeList.add(currentactivityList.get(i));
          yetToStartOrResumeActivityStatusList.add(currentActivityStatus.get(i));
          yetToStartOrResumeStatusList.add(currentStatus.get(i));
        } else {
          otherList.add(currentactivityList.get(i));
          otherActivityStatusList.add(currentActivityStatus.get(i));
          otherStatusList.add(currentStatus.get(i));
        }
      }
      try {
        currentactivityList.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        currentActivityStatus.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        currentStatus.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      currentactivityList.addAll(yetToStartOrResumeList);
      currentactivityList.addAll(otherList);

      currentActivityStatus.addAll(yetToStartOrResumeActivityStatusList);
      currentActivityStatus.addAll(otherActivityStatusList);

      currentStatus.addAll(yetToStartOrResumeStatusList);
      currentStatus.addAll(otherStatusList);

      for (int i = 0; i < upcomingactivityList.size(); i++) {
        for (int j = i; j < upcomingactivityList.size(); j++) {
          try {
            if (AppController.getDateFormatForApi()
                .parse(upcomingactivityList.get(i).getStartTime())
                .after(
                    AppController.getDateFormatForApi()
                        .parse(upcomingactivityList.get(j).getStartTime()))) {
              ActivitiesWS activitiesWS = upcomingactivityList.get(i);
              upcomingactivityList.set(i, upcomingactivityList.get(j));
              upcomingactivityList.set(j, activitiesWS);

              ActivityStatus activityStatus = upcomingActivityStatus.get(i);
              upcomingActivityStatus.set(i, upcomingActivityStatus.get(j));
              upcomingActivityStatus.set(j, activityStatus);

              String status = upcomingStatus.get(i);
              upcomingStatus.set(i, upcomingStatus.get(j));
              upcomingStatus.set(j, status);
            }
          } catch (ParseException e) {
            Logger.log(e);
          }
        }
      }

      for (int i = 0; i < completedactivityList.size(); i++) {
        for (int j = i; j < completedactivityList.size(); j++) {
          try {
            if (AppController.getDateFormatForApi()
                .parse(completedactivityList.get(i).getStartTime())
                .after(
                    AppController.getDateFormatForApi()
                        .parse(completedactivityList.get(j).getStartTime()))) {
              ActivitiesWS activitiesWS = completedactivityList.get(i);
              completedactivityList.set(i, completedactivityList.get(j));
              completedactivityList.set(j, activitiesWS);

              ActivityStatus activityStatus = completedActivityStatus.get(i);
              completedActivityStatus.set(i, completedActivityStatus.get(j));
              completedActivityStatus.set(j, activityStatus);

              String status = completedStatus.get(i);
              completedStatus.set(i, completedStatus.get(j));
              completedStatus.set(j, status);
            }
          } catch (ParseException e) {
            Logger.log(e);
          }
        }
      }

      // Checking the Empty values
      if (currentactivityList.isEmpty()) {
        ActivitiesWS w = new ActivitiesWS();
        w.setActivityId("");
        currentactivityList.add(w);
      }
      if (upcomingactivityList.isEmpty()) {
        ActivitiesWS w = new ActivitiesWS();
        w.setActivityId("");
        upcomingactivityList.add(w);
      }
      if (completedactivityList.isEmpty()) {
        ActivitiesWS w = new ActivitiesWS();
        w.setActivityId("");
        completedactivityList.add(w);
      }

      activitiesArrayList.addAll(currentactivityList);
      activitiesArrayList.addAll(upcomingactivityList);
      activitiesArrayList.addAll(completedactivityList);

      activitiesArrayList1.clear();
      for (int k = 0; k < activitiesArrayList.size(); k++) {
        if (!activitiesArrayList.get(k).getActivityId().equalsIgnoreCase("")) {
          Frequency frequency = new Frequency();
          RealmList<FrequencyRuns> frequencyRunses = new RealmList<>();
          for (int j = 0; j < activitiesArrayList.get(k).getFrequency().getRuns().size(); j++) {
            FrequencyRuns frequencyRuns = new FrequencyRuns();
            frequencyRuns.setEndTime(
                activitiesArrayList.get(k).getFrequency().getRuns().get(j).getEndTime());
            frequencyRuns.setStartTime(
                activitiesArrayList.get(k).getFrequency().getRuns().get(j).getStartTime());
            frequencyRunses.add(frequencyRuns);
          }
          frequency.setRuns(frequencyRunses);
          frequency.setType(activitiesArrayList.get(k).getFrequency().getType());
          ActivitiesWS activitiesWS = new ActivitiesWS();
          activitiesWS.setFrequency(frequency);
          activitiesWS.setStartTime(activitiesArrayList.get(k).getStartTime());
          activitiesWS.setEndTime(activitiesArrayList.get(k).getEndTime());
          activitiesWS.setType(activitiesArrayList.get(k).getType());
          activitiesWS.setActivityVersion(activitiesArrayList.get(k).getActivityVersion());
          activitiesWS.setActivityId(activitiesArrayList.get(k).getActivityId());
          activitiesWS.setBranching(activitiesArrayList.get(k).getBranching());
          activitiesWS.setStatus(activitiesArrayList.get(k).getStatus());
          activitiesWS.setTitle(activitiesArrayList.get(k).getTitle());
          activitiesWS.setSchedulingType(activitiesArrayList.get(k).getSchedulingType());
          if (activitiesArrayList
                  .get(k)
                  .getFrequency()
                  .getType()
                  .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)
              && activitiesArrayList.get(k).getAnchorDate() != null) {
            SchedulingAnchorDate schedulingAnchorDate = new SchedulingAnchorDate();
            schedulingAnchorDate.setSourceType(
                activitiesArrayList.get(k).getAnchorDate().getSourceType());
            if (activitiesArrayList.get(k).getAnchorDate().getStart() != null) {
              SchedulingAnchorDateStart schedulingAnchorDateStart = new SchedulingAnchorDateStart();
              schedulingAnchorDateStart.setAnchorDays(
                  activitiesArrayList.get(k).getAnchorDate().getStart().getAnchorDays());
              schedulingAnchorDateStart.setDateOfMonth(
                  activitiesArrayList.get(k).getAnchorDate().getStart().getDateOfMonth());
              schedulingAnchorDateStart.setDayOfWeek(
                  activitiesArrayList.get(k).getAnchorDate().getStart().getDayOfWeek());
              schedulingAnchorDateStart.setTime(
                  activitiesArrayList.get(k).getAnchorDate().getStart().getTime());
              schedulingAnchorDate.setStart(schedulingAnchorDateStart);
            }

            if (activitiesArrayList.get(k).getAnchorDate().getEnd() != null) {
              SchedulingAnchorDateEnd schedulingAnchorDateEnd = new SchedulingAnchorDateEnd();
              schedulingAnchorDateEnd.setAnchorDays(
                  activitiesArrayList.get(k).getAnchorDate().getEnd().getAnchorDays());
              schedulingAnchorDateEnd.setRepeatInterval(
                  activitiesArrayList.get(k).getAnchorDate().getEnd().getRepeatInterval());
              schedulingAnchorDateEnd.setTime(
                  activitiesArrayList.get(k).getAnchorDate().getEnd().getTime());
              schedulingAnchorDate.setEnd(schedulingAnchorDateEnd);
            }
            activitiesWS.setAnchorDate(schedulingAnchorDate);
          }

          activitiesArrayList1.add(activitiesWS);
        } else {
          activitiesArrayList1.add(activitiesArrayList.get(k));
        }
      }

      status.clear();
      // Checking the size is zero
      if (currentStatus.size() == 0) {
        currentStatus.add(STATUS_CURRENT);
      }
      if (upcomingStatus.size() == 0) {
        upcomingStatus.add(STATUS_UPCOMING);
      }
      if (completedStatus.size() == 0) {
        completedStatus.add(STATUS_COMPLETED);
      }
      status.addAll(currentStatus);
      status.addAll(upcomingStatus);
      status.addAll(completedStatus);

      currentRunStatusForActivities.clear();

      // Checking the Empty values
      if (currentActivityStatus.isEmpty()) {
        currentActivityStatus.add(new ActivityStatus());
      }
      if (upcomingActivityStatus.isEmpty()) {
        upcomingActivityStatus.add(new ActivityStatus());
      }
      if (completedActivityStatus.isEmpty()) {
        completedActivityStatus.add(new ActivityStatus());
      }
      currentRunStatusForActivities.addAll(currentActivityStatus);
      currentRunStatusForActivities.addAll(upcomingActivityStatus);
      currentRunStatusForActivities.addAll(completedActivityStatus);

      StudyList studyList =
          dbServiceSubscriber.getStudiesDetails(((SurveyActivity) context).getStudyId(), realm);
      boolean paused;
      if (studyList.getStatus().equalsIgnoreCase(StudyFragment.PAUSED)) {
        paused = true;
      } else {
        paused = false;
      }
      SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
      StudyData studyPreferences = dbServiceSubscriber.getStudyPreference(realm);
      Date joiningDate =
          survayScheduler.getJoiningDateOfStudy(
              studyPreferences, ((SurveyActivity) context).getStudyId());
      title = studyList.getTitle();
      Filter filter = getFilterList();
      studyVideoAdapter =
          new SurveyActivitiesListAdapter(
              context,
              filter.getActivitiesArrayList1(),
              filter.getStatus(),
              filter.getCurrentRunStatusForActivities(),
              SurveyActivitiesFragment.this,
              paused,
              joiningDate);

      activityListDataDB = null;

      dbServiceSubscriber.closeRealmObj(realm);
      return activitiesArrayList1;
    }

    @Override
    protected void onPostExecute(ArrayList<ActivitiesWS> result) {

      realm = AppController.getRealmobj(context);

      surveyActivitiesRecyclerView.setLayoutManager(new LinearLayoutManager(context));
      surveyActivitiesRecyclerView.setAdapter(studyVideoAdapter);

      AppController.getHelperSharedPreference()
          .writePreference(
              context, context.getResources().getString(R.string.completedRuns), "" + completed);
      AppController.getHelperSharedPreference()
          .writePreference(
              context, context.getResources().getString(R.string.missedRuns), "" + missed);
      AppController.getHelperSharedPreference()
          .writePreference(
              context, context.getResources().getString(R.string.totalRuns), "" + total);

      double completion = 0;
      MotivationalNotification motivationalNotification =
          dbServiceSubscriber.getMotivationalNotification(
              ((SurveyActivity) context).getStudyId(), realm);
      if (total > 0) {
        completion = (((double) completed + (double) missed) / (double) total) * 100d;
      }

      boolean hundredPc = false;
      boolean fiftyPc = false;
      if (motivationalNotification == null) {
        if (completion >= 100) {
          hundredPc = true;
          fiftyPc = true;
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.study)
                  + " "
                  + title
                  + " "
                  + context.getResources().getString(R.string.percent_complete1),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        } else if (completion >= 50) {
          fiftyPc = true;
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.study)
                  + " "
                  + title
                  + " "
                  + context.getResources().getString(R.string.percent_complete2),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        } else if (missed > 0) {
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.missed_activity)
                  + " "
                  + ((SurveyActivity) context).getTitle1()
                  + " "
                  + context.getResources().getString(R.string.we_encourage),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        }
      } else if (!motivationalNotification.isFiftyPc() && !motivationalNotification.isHundredPc()) {
        if (completion >= 100) {
          hundredPc = true;
          fiftyPc = true;
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.study)
                  + " "
                  + title
                  + " "
                  + context.getResources().getString(R.string.percent_complete1),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        } else if (completion >= 50) {
          fiftyPc = true;
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.study)
                  + " "
                  + title
                  + " "
                  + context.getResources().getString(R.string.percent_complete2),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        } else if (motivationalNotification.getMissed() != missed) {
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.missed_activity)
                  + " "
                  + ((SurveyActivity) context).getTitle1()
                  + " "
                  + context.getResources().getString(R.string.we_encourage),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        }
      } else if (!motivationalNotification.isHundredPc()) {
        if (completion >= 100) {
          hundredPc = true;
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.study)
                  + " "
                  + title
                  + " "
                  + context.getResources().getString(R.string.percent_complete1),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        } else if (motivationalNotification.getMissed() != missed) {
          SetDialogHelper.setNeutralDialog(
              context,
              context.getResources().getString(R.string.missed_activity)
                  + " "
                  + ((SurveyActivity) context).getTitle1()
                  + " "
                  + context.getResources().getString(R.string.we_encourage),
              false,
              context.getResources().getString(R.string.ok),
              context.getResources().getString(R.string.app_name));
        }

      } else if (motivationalNotification.getMissed() != missed) {
        SetDialogHelper.setNeutralDialog(
            context,
            context.getResources().getString(R.string.missed_activity)
                + " "
                + ((SurveyActivity) context).getTitle1()
                + " "
                + context.getResources().getString(R.string.we_encourage),
            false,
            context.getResources().getString(R.string.ok),
            context.getResources().getString(R.string.app_name));
      }

      if (motivationalNotification != null && motivationalNotification.isHundredPc()) {
        hundredPc = true;
      }

      if (motivationalNotification != null && motivationalNotification.isFiftyPc()) {
        fiftyPc = true;
      }

      // update motivational table
      MotivationalNotification motivationalNotification1 = new MotivationalNotification();
      motivationalNotification1.setStudyId(((SurveyActivity) context).getStudyId());
      motivationalNotification1.setFiftyPc(fiftyPc);
      motivationalNotification1.setHundredPc(hundredPc);
      motivationalNotification1.setMissed(missed);
      dbServiceSubscriber.saveMotivationalNotificationToDB(context, motivationalNotification1);

      dbServiceSubscriber.closeRealmObj(realm);
      double adherence = 0;
      if (((double) completed + (double) missed + 1d) > 0) {
        adherence =
            (((double) completed + 1d) / ((double) completed + (double) missed + 1d)) * 100d;
      }

      updateStudyState("" + (int) completion, "" + (int) adherence);
    }

    @Override
    protected void onPreExecute() {}
  }

  public void updateStudyState(String completion, String adherence) {
    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(context, context.getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, context.getResources().getString(R.string.userid), ""));

    JSONObject jsonObject = new JSONObject();

    JSONArray studieslist = new JSONArray();
    JSONObject studiestatus = new JSONObject();
    try {
      studiestatus.put("studyId", ((SurveyActivity) context).getStudyId());
      studiestatus.put("completion", completion);
      studiestatus.put("adherence", adherence);

    } catch (JSONException e) {
      Logger.log(e);
    }

    studieslist.put(studiestatus);
    try {
      jsonObject.put("studies", studieslist);
    } catch (JSONException e) {
      Logger.log(e);
    }
    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "post_object",
            Urls.UPDATE_STUDY_PREFERENCE,
            UPDATE_STUDY_PREFERENCE,
            context,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    updatePreferenceEvent.setParticipantEnrollmentDatastoreConfigEvent(
        participantEnrollmentDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  private Filter getFilterList() {

    ArrayList<ActivitiesWS> filterActivitiesArrayList1 = new ArrayList<>();
    ArrayList<String> filterStatus = new ArrayList<>();
    ArrayList<ActivityStatus> filterCurrentRunStatusForActivities = new ArrayList<>();
    boolean isCurrentAvailable = false;
    boolean isUpcommingAvailable = false;
    boolean isCompletedAvailable = false;
    if (filterPos == 0) {
      isCurrentAvailable = true;
      isUpcommingAvailable = true;
      isCompletedAvailable = true;
      filterActivitiesArrayList1.addAll(activitiesArrayList1);
      filterStatus.addAll(status);
      filterCurrentRunStatusForActivities.addAll(currentRunStatusForActivities);
    } else if (filterPos == 1) {

      for (int i = 0; i < activitiesArrayList1.size(); i++) {
        if (activitiesArrayList1.get(i).getType() == null
            || activitiesArrayList1.get(i).getType().equalsIgnoreCase("questionnaire")) {

          if (status.get(i).equalsIgnoreCase(STATUS_CURRENT)) {
            isCurrentAvailable = true;
          } else if (status.get(i).equalsIgnoreCase(STATUS_UPCOMING)) {
            if (!isCurrentAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_CURRENT);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            isCurrentAvailable = true;
            isUpcommingAvailable = true;
          } else if (status.get(i).equalsIgnoreCase(STATUS_COMPLETED)) {
            if (!isCurrentAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_CURRENT);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            if (!isUpcommingAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_UPCOMING);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            isCurrentAvailable = true;
            isUpcommingAvailable = true;
            isCompletedAvailable = true;
          }

          filterActivitiesArrayList1.add(activitiesArrayList1.get(i));
          filterStatus.add(status.get(i));
          filterCurrentRunStatusForActivities.add(currentRunStatusForActivities.get(i));
        }
      }
    } else if (filterPos == 2) {
      for (int i = 0; i < activitiesArrayList1.size(); i++) {
        if (activitiesArrayList1.get(i).getType() == null
            || activitiesArrayList1.get(i).getType().equalsIgnoreCase("task")) {

          if (status.get(i).equalsIgnoreCase(STATUS_CURRENT)) {
            isCurrentAvailable = true;
          } else if (status.get(i).equalsIgnoreCase(STATUS_UPCOMING)) {
            if (!isCurrentAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_CURRENT);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            isCurrentAvailable = true;
            isUpcommingAvailable = true;
          } else if (status.get(i).equalsIgnoreCase(STATUS_COMPLETED)) {
            if (!isCurrentAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_CURRENT);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            if (!isUpcommingAvailable) {
              ActivitiesWS w = new ActivitiesWS();
              w.setActivityId("");
              filterActivitiesArrayList1.add(w);
              filterStatus.add(STATUS_UPCOMING);
              filterCurrentRunStatusForActivities.add(new ActivityStatus());
            }
            isCurrentAvailable = true;
            isUpcommingAvailable = true;
            isCompletedAvailable = true;
          }

          filterActivitiesArrayList1.add(activitiesArrayList1.get(i));
          filterStatus.add(status.get(i));
          filterCurrentRunStatusForActivities.add(currentRunStatusForActivities.get(i));
        }
      }
    }

    if (!isCurrentAvailable) {
      ActivitiesWS w = new ActivitiesWS();
      w.setActivityId("");
      filterActivitiesArrayList1.add(w);
      filterStatus.add(STATUS_CURRENT);
      filterCurrentRunStatusForActivities.add(new ActivityStatus());
    }
    if (!isUpcommingAvailable) {
      ActivitiesWS w = new ActivitiesWS();
      w.setActivityId("");
      filterActivitiesArrayList1.add(w);
      filterStatus.add(STATUS_UPCOMING);
      filterCurrentRunStatusForActivities.add(new ActivityStatus());
    }
    if (!isCompletedAvailable) {
      ActivitiesWS w = new ActivitiesWS();
      w.setActivityId("");
      filterActivitiesArrayList1.add(w);
      filterStatus.add(STATUS_COMPLETED);
      filterCurrentRunStatusForActivities.add(new ActivityStatus());
    }
    Filter filter = new Filter();
    filter.setActivitiesArrayList1(filterActivitiesArrayList1);
    filter.setCurrentRunStatusForActivities(filterCurrentRunStatusForActivities);
    filter.setStatus(filterStatus);
    return filter;
  }

  private <E> void insertAndUpdateToDB(Context context, E e) {
    DatabaseEvent databaseEvent = new DatabaseEvent();
    databaseEvent.setE(e);
    databaseEvent.setType(DbServiceSubscriber.TYPE_COPY_UPDATE);
    databaseEvent.setaClass(EligibilityConsent.class);
    databaseEvent.setOperation(DbServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
    dbServiceSubscriber.insert(context, databaseEvent);
  }

  boolean checkbefore(Date starttime) {
    return new Date().before(starttime);
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    if (statusCode.equalsIgnoreCase("401")) {
      onItemsLoadComplete();
      AppController.getHelperProgressDialog().dismissDialog();
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(context, errormsg);
    } else {
      if (responseCode == ACTIVTTYLIST_RESPONSECODE || responseCode == STUDY_UPDATES) {
        calculateStartAnsEndDateForActivities();
      } else if (responseCode == ACTIVTTYINFO_RESPONSECODE) {
        onItemsLoadComplete();
        AppController.getHelperProgressDialog().dismissDialog();
        launchSurvey(null);
      } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
        onItemsLoadComplete();
        AppController.getHelperProgressDialog().dismissDialog();
        dbServiceSubscriber.updateActivityPreferenceDB(
            context,
            activityId,
            ((SurveyActivity) context).getStudyId(),
            currentRunId,
            SurveyActivitiesFragment.IN_PROGRESS,
            activityStatusData.getTotalRun(),
            activityStatusData.getCompletedRun(),
            activityStatusData.getMissedRun(),
            activityVersion);
        launchSurvey(null);
      } else {
        try {
          onItemsLoadComplete();
        } catch (Exception e) {
          Logger.log(e);
        }
        try {
          AppController.getHelperProgressDialog().dismissDialog();
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    }
  }

  public void getActivityInfo(
      String activityId,
      int currentRunId,
      String status,
      boolean branching,
      String activityVersion,
      ActivityStatus activityStatus,
      ActivitiesWS activitiesWS) {
    this.currentRunId = currentRunId;
    activityStatusStr = status;
    this.activityStatusData = activityStatus;
    this.activityId = activityId;
    this.branching = branching;
    this.activityVersion = activityVersion;
    if (status.equalsIgnoreCase(YET_To_START)) {
      updateUserPreference(
          ((SurveyActivity) context).getStudyId(), status, activityId, currentRunId);
    } else {
      updateActivityInfo(activityId);
    }
  }

  private void updateActivityInfo(String activityId) {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);

    GetActivityInfoEvent getActivityInfoEvent = new GetActivityInfoEvent();
    HashMap<String, String> header = new HashMap();
    String url =
        Urls.ACTIVITY
            + "?studyId="
            + ((SurveyActivity) context).getStudyId()
            + "&activityId="
            + activityId
            + "&activityVersion="
            + activityVersion;
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            ACTIVTTYINFO_RESPONSECODE,
            context,
            ActivityInfoData.class,
            null,
            header,
            null,
            false,
            this);

    getActivityInfoEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetActivityInfo(getActivityInfoEvent);
  }

  private void launchSurvey(ActivityObj activity) {
    try {
      activityObj = new ActivityObj();
      activityObj =
          dbServiceSubscriber.getActivityBySurveyId(
              ((SurveyActivity) context).getStudyId(), activityId, realm);
      if (activityObj == null && activity != null) {
        activityObj = activity;
        activityObj.setSurveyId(activityObj.getMetadata().getActivityId());
        activityObj.setStudyId(((SurveyActivity) context).getStudyId());
        dbServiceSubscriber.saveActivity(context, activityObj);
      }

      if (activityObj != null) {
        AppController.getHelperSharedPreference()
            .writePreference(context, getString(R.string.mapCount), "0");
        stepsBuilder = new StepsBuilder(context, activityObj, branching, realm);
        task =
            ActivityBuilder.create(
                context,
                ((SurveyActivity) context).getStudyId()
                    + "_STUDYID_"
                    + activityObj.getSurveyId()
                    + "_"
                    + currentRunId,
                stepsBuilder.getsteps(),
                activityObj,
                branching,
                dbServiceSubscriber);
        if (task.getSteps().size() > 0) {
          for (int i = 0; i < activityObj.getSteps().size(); i++) {
            if (activityObj.getSteps().get(i).getResultType().equalsIgnoreCase("location")) {
              locationPermission = true;
            }
          }
          if (locationPermission) {
            if ((ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)) {
              String[] permission =
                  new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                  };
              if (!hasPermissions(permission)) {
                ActivityCompat.requestPermissions(
                    (Activity) context, permission, PERMISSION_REQUEST_CODE);
              } else {
                startsurvey();
              }
            } else {
              startsurvey();
            }
          } else {
            startsurvey();
          }
        } else {
          Toast.makeText(context, R.string.no_task_available, Toast.LENGTH_SHORT).show();
        }
      } else {
        Toast.makeText(context, R.string.no_ableto_getdata, Toast.LENGTH_SHORT).show();
      }
    } catch (Exception e) {
      Toast.makeText(context, R.string.couldnot_launch_survey, Toast.LENGTH_SHORT).show();
      Logger.log(e);
    }
  }

  private void startsurvey() {

    Intent intent =
        CustomSurveyViewTaskActivity.newIntent(
            context,
            ((SurveyActivity) context).getStudyId()
                + "_STUDYID_"
                + activityObj.getSurveyId()
                + "_"
                + currentRunId,
            ((SurveyActivity) context).getStudyId(),
            currentRunId,
            activityStatusStr,
            activityStatusData.getMissedRun(),
            activityStatusData.getCompletedRun(),
            activityStatusData.getTotalRun(),
            activityVersion,
            activityStatusData.getCurrentRunStartDate(),
            activityStatusData.getCurrentRunEndDate(),
            activityObj.getSurveyId(),
            branching);
    startActivityForResult(intent, 123);
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M
        && context != null
        && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  public void updateUserPreference(
      String studyId, String status, String activityId, int activityRunId) {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    HashMap<String, String> header = new HashMap();
    Realm realm = AppController.getRealmobj(context);
    Studies studies = dbServiceSubscriber.getStudies(studyId, realm);
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(context, context.getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, context.getResources().getString(R.string.userid), ""));
    header.put("participantId", studies.getParticipantId());
    dbServiceSubscriber.closeRealmObj(realm);
    JSONObject jsonObject = new JSONObject();

    JSONArray activitylist = new JSONArray();
    JSONObject activityStatus = new JSONObject();
    JSONObject activityRun = new JSONObject();
    try {
      activityStatus.put("activityState", IN_PROGRESS);
      activityStatus.put("activityId", activityId);
      activityStatus.put("activityRunId", "" + activityRunId);
      activityStatus.put("bookmarked", "false");
      activityStatus.put("activityVersion", activityVersion);

      activityRun.put("total", activityStatusData.getTotalRun());
      activityRun.put("completed", activityStatusData.getCompletedRun());
      activityRun.put("missed", activityStatusData.getMissedRun());

      activityStatus.put("activityRun", activityRun);

    } catch (JSONException e) {
      Logger.log(e);
    }

    activitylist.put(activityStatus);

    try {
      jsonObject.put("studyId", studyId);
      jsonObject.put("participantId", studies.getParticipantId());
      jsonObject.put("activity", activitylist);
    } catch (JSONException e) {
      Logger.log(e);
    }

    // offline data storing
    try {
      int number = dbServiceSubscriber.getUniqueID(realm);
      if (number == 0) {
        number = 1;
      } else {
        number += 1;
      }

      // studyId, activityId combines and handling the duplication
      String studyIdActivityId = studyId + activityId;

      OfflineData offlineData =
          dbServiceSubscriber.getActivityIdOfflineData(studyIdActivityId, realm);
      if (offlineData != null) {
        number = offlineData.getNumber();
      }
      deleteIndexNumberDb = number;

      AppController.pendingService(
          context,
          number,
          "post_object",
          Urls.UPDATE_ACTIVITY_PREFERENCE,
          "",
          jsonObject.toString(),
          "ResponseDatastore",
          "",
          "",
          studyIdActivityId);
    } catch (Exception e) {
      Logger.log(e);
    }

    ResponseDatastoreConfigEvent responseDatastoreConfigEvent =
        new ResponseDatastoreConfigEvent(
            "post_object",
            Urls.UPDATE_ACTIVITY_PREFERENCE,
            UPDATE_USERPREFERENCE_RESPONSECODE,
            context,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);
    ActivityStateEvent activityStateEvent = new ActivityStateEvent();
    activityStateEvent.setResponseDatastoreConfigEvent(responseDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performActivityState(activityStateEvent);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    switch (requestCode) {
      case PERMISSION_REQUEST_CODE:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
          Toast.makeText(context, R.string.current_locationwillnot_used, Toast.LENGTH_SHORT).show();
        }
        startsurvey();
        break;
    }
  }

  @Override
  public void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  private class ResponseData extends AsyncTask<String, Void, String> {

    String response = null;
    String responseCode = null;
    int position;
    Responsemodel responseModel;
    AnchorDateSchedulingDetails anchorDateSchedulingDetails;

    ResponseData(int position, AnchorDateSchedulingDetails anchorDateSchedulingDetails) {
      this.position = position;
      this.anchorDateSchedulingDetails = anchorDateSchedulingDetails;
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

      ConnectionDetector connectionDetector = new ConnectionDetector(context);

      if (connectionDetector.isConnectingToInternet()) {
        Realm realm = AppController.getRealmobj(context);
        HashMap<String, String> header = new HashMap<>();
        header.put(
            getString(R.string.clientToken),
            SharedPreferenceHelper.readPreference(context, getString(R.string.clientToken), ""));
        header.put(
            "Authorization",
            "Bearer "
                + SharedPreferenceHelper.readPreference(context, getString(R.string.auth), ""));
        header.put(
            "userId",
            SharedPreferenceHelper.readPreference(context, getString(R.string.userid), ""));
        Studies studies =
            realm
                .where(Studies.class)
                .equalTo("studyId", anchorDateSchedulingDetails.getStudyId())
                .findFirst();
        responseModel =
            HttpRequest.getRequest(
                Urls.PROCESSRESPONSEDATA
                    + AppConfig.APP_ID_KEY
                    + "="
                    + AppConfig.APP_ID_VALUE
                    + "&participantId="
                    + anchorDateSchedulingDetails.getParticipantId()
                    + "&tokenId="
                    + studies.getHashedToken()
                    + "&siteId="
                    + studies.getSiteId()
                    + "&studyId="
                    + studies.getStudyId()
                    + "&activityId="
                    + anchorDateSchedulingDetails.getSourceActivityId()
                    + "&questionKey="
                    + anchorDateSchedulingDetails.getSourceKey()
                    + "&activityVersion="
                    + anchorDateSchedulingDetails.getActivityVersion(),
                header,
                "");
        dbServiceSubscriber.closeRealmObj(realm);
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
    protected void onPostExecute(String response) {
      super.onPostExecute(response);
      if (response != null) {
        if (response.equalsIgnoreCase("session expired")) {
          AppController.getHelperProgressDialog().dismissDialog();
          AppController.getHelperSessionExpired(context, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          metadataProcess();
          Toast.makeText(
                  context,
                  context.getResources().getString(R.string.connection_timeout),
                  Toast.LENGTH_SHORT)
              .show();
        } else if (Integer.parseInt(responseCode) == 500) {
          try {
            JSONObject jsonObject = new JSONObject(String.valueOf(responseModel.getResponseData()));
            String exception = String.valueOf(jsonObject.get("exception"));
            if (exception.contains("Query or table not found")) {
              // call remaining service
              callLabkeyService(this.position);
            } else {
              metadataProcess();
            }
          } catch (JSONException e) {
            metadataProcess();
            Logger.log(e);
          }
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
          try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
            Gson gson = new Gson();

            JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonArray.get(0)));
            JSONArray jsonArray1 = (JSONArray) jsonObject1.get("data");
            Object value = null;
            for (int i = 0; i < jsonArray1.length(); i++) {
              Type type = new TypeToken<Map<String, Object>>() {}.getType();
              Map<String, Object> map = gson.fromJson(String.valueOf(jsonArray1.get(i)), type);
              for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String valueobj = gson.toJson(entry.getValue());
                Map<String, Object> vauleMap = gson.fromJson(String.valueOf(valueobj), type);
                value = vauleMap.get("value");
                try {
                  Date anchordate = AppController.getLabkeyDateFormat().parse("" + value);
                  value = AppController.getDateFormatForApi().format(anchordate);
                } catch (ParseException e) {
                  Logger.log(e);
                }
              }
            }

            // updating results back to DB
            StepRecordCustom stepRecordCustom = new StepRecordCustom();
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("answer", "" + value);
            stepRecordCustom.setResult(jsonObject2.toString());
            stepRecordCustom.setActivityID(
                anchorDateSchedulingDetails.getStudyId()
                    + "_STUDYID_"
                    + anchorDateSchedulingDetails.getSourceActivityId());
            stepRecordCustom.setStepId(anchorDateSchedulingDetails.getSourceKey());
            stepRecordCustom.setTaskStepID(
                anchorDateSchedulingDetails.getStudyId()
                    + "_STUDYID_"
                    + anchorDateSchedulingDetails.getSourceActivityId()
                    + "_"
                    + 1
                    + "_"
                    + anchorDateSchedulingDetails.getSourceKey());
            dbServiceSubscriber.updateStepRecord(context, stepRecordCustom);

            arrayList.get(this.position).setAnchorDate("" + value);

            callLabkeyService(this.position);
          } catch (Exception e) {
            Logger.log(e);
            metadataProcess();
          }
        } else {
          metadataProcess();
        }
      } else {
        metadataProcess();
        Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void callLabkeyService(int position) {
    if (arrayList.size() > position) {
      AnchorDateSchedulingDetails anchorDateSchedulingDetails = arrayList.get(position);
      if (anchorDateSchedulingDetails.getSourceType().equalsIgnoreCase("ActivityResponse")
          && anchorDateSchedulingDetails.getActivityState().equalsIgnoreCase("completed")) {
        Realm realm = AppController.getRealmobj(context);
        StepRecordCustom stepRecordCustom =
            dbServiceSubscriber.getSurveyResponseFromDB(
                anchorDateSchedulingDetails.getStudyId()
                    + "_STUDYID_"
                    + anchorDateSchedulingDetails.getSourceActivityId(),
                anchorDateSchedulingDetails.getSourceKey(),
                realm);
        if (stepRecordCustom != null) {
          String value = "";
          try {
            JSONObject jsonObject = new JSONObject(stepRecordCustom.getResult());
            value = jsonObject.getString("answer");
          } catch (JSONException e) {
            Logger.log(e);
          }
          arrayList.get(position).setAnchorDate("" + value);

          callLabkeyService(position + 1);
        } else {
          new ResponseData(position, anchorDateSchedulingDetails).execute();
        }
        dbServiceSubscriber.closeRealmObj(realm);
      } else {
        callLabkeyService(position + 1);
      }
    } else {
      metadataProcess();
    }
  }
}
