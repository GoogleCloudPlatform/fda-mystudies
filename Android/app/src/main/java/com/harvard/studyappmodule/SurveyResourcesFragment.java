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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitylistmodel.AnchorDateSchedulingDetails;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
import com.harvard.studyappmodule.events.GetResourceListEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.studymodel.DeleteAccountData;
import com.harvard.studyappmodule.studymodel.NotificationDbResources;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyResource;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.Activities;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SurveyResourcesFragment<T> extends Fragment implements ApiCall.OnAsyncRequestComplete {

  private static final int STUDY_INFO = 10;
  private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 100;
  private static final int DELETE_ACCOUNT_REPSONSECODE = 101;
  private static final int RESOURCE_REQUEST_CODE = 213;
  private static final int WITHDRAWFROMSTUDY = 105;
  private RecyclerView studyRecyclerView;
  private Context context;
  private AppCompatTextView title;
  private RealmList<Resource> resourceArrayList;
  private String studyId;
  private StudyHome studyHome;
  private StudyResource studyResource;
  private DbServiceSubscriber dbServiceSubscriber;
  private static String RESOURCES = "resources";
  private Realm realm;
  private ArrayList<AnchorDateSchedulingDetails> arrayList;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_survey_resources, container, false);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(context);
    initializeXmlId(view);
    setTextForView();
    setFont();
    getResourceListWebservice();

    return view;
  }

  private void getResourceListWebservice() {
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    HashMap<String, String> header = new HashMap<>();
    studyId = ((SurveyActivity) context).getStudyId();
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
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
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

  private void initializeXmlId(View view) {
    title = (AppCompatTextView) view.findViewById(R.id.title);
    studyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
    RelativeLayout backBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
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
    title.setText(getResources().getString(R.string.resources));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(getActivity(), "bold"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {

    // RESOURCE_REQUEST_CODE: while coming screen, every time after resourcelist service calling
    // study info
    // stop and again start progress bar, to avoid that using this
    if (responseCode != RESOURCE_REQUEST_CODE) {
      AppController.getHelperProgressDialog().dismissDialog();
    }
    if (responseCode == RESOURCE_REQUEST_CODE) {
      // call study info

      if (response != null) {
        studyResource = (StudyResource) response;

        callGetStudyInfoWebservice();
      }

    } else if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {

      dbServiceSubscriber.updateStudyWithddrawnDB(context, studyId, StudyFragment.WITHDRAWN);
      dbServiceSubscriber.deleteActivityDataRow(context, studyId);
      dbServiceSubscriber.deleteActivityWsData(context, studyId);

      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        Intent intent = new Intent(context, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        context.startActivity(mainIntent);
        ((Activity) context).finish();
      } else {
        deactivateAccount();
      }
    } else if (responseCode == DELETE_ACCOUNT_REPSONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        AppController.getHelperSessionExpired(context, "");
        Toast.makeText(context, R.string.account_deletion, Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == STUDY_INFO) {
      if (response != null) {
        studyHome = (StudyHome) response;
        studyHome.setStudyId(studyId);
        dbServiceSubscriber.saveStudyInfoToDB(context, studyHome);

        if (studyResource != null) {
          resourceArrayList = studyResource.getResources();
          if (resourceArrayList == null) {
            resourceArrayList = new RealmList<>();
          }
          addStaticVal();

          // primary key studyId
          studyResource.setStudyId(studyId);
          // remove duplicate and
          dbServiceSubscriber.deleteStudyResourceDuplicateRow(context, studyId);
          dbServiceSubscriber.saveResourceList(context, studyResource);

          calculatedResources(resourceArrayList);
        }
      }
    }
  }

  private void calculatedResources(RealmList<Resource> resourceArrayList) {
    // call to resp server to get anchorDate
    arrayList = new ArrayList<>();
    resourceArrayList = resourceArrayList;
    AnchorDateSchedulingDetails anchorDateSchedulingDetails;
    Studies studies =
        dbServiceSubscriber.getStudies(((SurveyActivity) context).getStudyId(), realm);

    for (int i = 0; i < resourceArrayList.size(); i++) {
      if (resourceArrayList.get(i).getAvailability() != null
          && resourceArrayList.get(i).getAvailability().getAvailabilityType() != null) {
        if (resourceArrayList
            .get(i)
            .getAvailability()
            .getAvailabilityType()
            .equalsIgnoreCase("AnchorDate")) {

          if (resourceArrayList
              .get(i)
              .getAvailability()
              .getSourceType()
              .equalsIgnoreCase("ActivityResponse")) {
            anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
            anchorDateSchedulingDetails.setSourceActivityId(
                resourceArrayList.get(i).getAvailability().getSourceActivityId());
            anchorDateSchedulingDetails.setSourceKey(
                resourceArrayList.get(i).getAvailability().getSourceKey());
            anchorDateSchedulingDetails.setSourceFormKey(
                resourceArrayList.get(i).getAvailability().getSourceFormKey());

            anchorDateSchedulingDetails.setSchedulingType(
                resourceArrayList.get(i).getAvailability().getAvailabilityType());
            anchorDateSchedulingDetails.setSourceType(
                resourceArrayList.get(i).getAvailability().getSourceType());
            anchorDateSchedulingDetails.setStudyId(((SurveyActivity) context).getStudyId());
            anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
            // targetActivityid is resourceId in this case just to handle with case variable
            anchorDateSchedulingDetails.setTargetActivityId(
                resourceArrayList.get(i).getResourcesId());

            Activities activities =
                dbServiceSubscriber.getActivityPreferenceBySurveyId(
                    ((SurveyActivity) context).getStudyId(),
                    anchorDateSchedulingDetails.getSourceActivityId(),
                    realm);
            if (activities != null) {
              anchorDateSchedulingDetails.setActivityState(activities.getStatus());
              arrayList.add(anchorDateSchedulingDetails);
            }
          } else {
            // For enrollmentDate
            anchorDateSchedulingDetails = new AnchorDateSchedulingDetails();
            anchorDateSchedulingDetails.setSchedulingType(
                resourceArrayList.get(i).getAvailability().getAvailabilityType());
            anchorDateSchedulingDetails.setSourceType(
                resourceArrayList.get(i).getAvailability().getSourceType());
            anchorDateSchedulingDetails.setStudyId(((SurveyActivity) context).getStudyId());
            anchorDateSchedulingDetails.setParticipantId(studies.getParticipantId());
            // targetActivityid is resourceId in this case just to handle with case variable
            anchorDateSchedulingDetails.setTargetActivityId(
                resourceArrayList.get(i).getResourcesId());
            anchorDateSchedulingDetails.setAnchorDate(studies.getEnrolledDate());
            arrayList.add(anchorDateSchedulingDetails);
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

  private void setResourceAdapter() {
    RealmList<Resource> resources = new RealmList<>();
    if (resourceArrayList != null) {
      for (int i = 0; i < resourceArrayList.size(); i++) {
        if (resourceArrayList.get(i).getAudience() != null
            && resourceArrayList.get(i).getAudience().equalsIgnoreCase("All")) {
          if (resourceArrayList.get(i).getAvailability() != null
              && resourceArrayList.get(i).getAvailability().getAvailableDate() != null
              && !resourceArrayList
                  .get(i)
                  .getAvailability()
                  .getAvailableDate()
                  .equalsIgnoreCase("")) {
            try {
              Calendar expiryDate = Calendar.getInstance();
              expiryDate.setTime(
                  AppController.getDateFormatForResourceAvailability()
                      .parse(resourceArrayList.get(i).getAvailability().getExpiryDate()));
              expiryDate.set(Calendar.HOUR, 11);
              expiryDate.set(Calendar.MINUTE, 59);
              expiryDate.set(Calendar.SECOND, 59);
              expiryDate.set(Calendar.AM_PM, Calendar.PM);

              Calendar availableDate = Calendar.getInstance();
              availableDate.setTime(
                  AppController.getDateFormatForResourceAvailability()
                      .parse(resourceArrayList.get(i).getAvailability().getAvailableDate()));
              availableDate.set(Calendar.HOUR, 0);
              availableDate.set(Calendar.MINUTE, 0);
              availableDate.set(Calendar.SECOND, 0);
              availableDate.set(Calendar.AM_PM, Calendar.AM);

              Calendar currentday = Calendar.getInstance();
              if ((currentday.getTime().before(expiryDate.getTime())
                      || currentday.getTime().equals(expiryDate.getTime()))
                  && (currentday.getTime().after(availableDate.getTime())
                      || currentday.getTime().equals(availableDate.getTime()))) {
                resources.add(resourceArrayList.get(i));
              }
            } catch (ParseException e) {
              Logger.log(e);
            }
          } else {
            resources.add(resourceArrayList.get(i));
          }

        } else if (resourceArrayList.get(i).getAudience() != null
            && resourceArrayList.get(i).getAudience().equalsIgnoreCase("Limited")) {
          if (resourceArrayList
              .get(i)
              .getAvailability()
              .getAvailabilityType()
              .equalsIgnoreCase("AnchorDate")) {
            if (resourceArrayList
                .get(i)
                .getAvailability()
                .getSourceType()
                .equalsIgnoreCase("ActivityResponse")) {
              if (resourceArrayList
                  .get(i)
                  .getAvailability()
                  .getAvailableDate()
                  .equalsIgnoreCase("")) {
                StepRecordCustom stepRecordCustom =
                    dbServiceSubscriber.getSurveyResponseFromDB(
                        ((SurveyActivity) context).getStudyId()
                            + "_STUDYID_"
                                + AppController.getSourceActivityId(resourceArrayList.get(i)),
                            AppController.getSourceKey(resourceArrayList.get(i)),
                        realm);
                if (stepRecordCustom != null) {
                  Calendar startCalender = Calendar.getInstance();

                  Calendar endCalender = Calendar.getInstance();

                  JSONObject jsonObject = null;
                  try {
                    jsonObject = new JSONObject(stepRecordCustom.getResult());
                    startCalender.setTime(
                        AppController.getDateFormatForApi().parse("" + jsonObject.get("answer")));
                    startCalender.add(
                        Calendar.DATE, resourceArrayList.get(i).getAvailability().getStartDays());
                    if (resourceArrayList.get(i).getAvailability().getStartTime() == null
                        || resourceArrayList
                            .get(i)
                            .getAvailability()
                            .getStartTime()
                            .equalsIgnoreCase("")) {
                      startCalender.set(Calendar.HOUR_OF_DAY, 0);
                      startCalender.set(Calendar.MINUTE, 0);
                      startCalender.set(Calendar.SECOND, 0);
                    } else {
                      String[] time =
                          resourceArrayList.get(i).getAvailability().getStartTime().split(":");
                      startCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                      startCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                      startCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                    }
                    NotificationDbResources notificationsDb = null;
                    RealmResults<NotificationDbResources> notificationsDbs =
                        dbServiceSubscriber.getNotificationDbResources(
                            AppController.getSourceActivityId(resourceArrayList.get(i)),
                            ((SurveyActivity) context).getStudyId(),
                            RESOURCES,
                            realm);
                    if (notificationsDbs != null && notificationsDbs.size() > 0) {
                      for (int j = 0; j < notificationsDbs.size(); j++) {
                        if (notificationsDbs
                            .get(j)
                            .getResourceId()
                            .equalsIgnoreCase(resourceArrayList.get(i).getResourcesId())) {
                          notificationsDb = notificationsDbs.get(j);
                          break;
                        }
                      }
                    }
                    if (notificationsDb == null) {
                      setRemainder(
                          startCalender,
                          AppController.getSourceActivityId(resourceArrayList.get(i)),
                          ((SurveyActivity) context).getStudyId(),
                          resourceArrayList.get(i).getNotificationText(),
                          resourceArrayList.get(i).getResourcesId());
                    }

                    endCalender.setTime(
                        AppController.getDateFormatForApi().parse("" + jsonObject.get("answer")));
                    endCalender.add(
                        Calendar.DATE, resourceArrayList.get(i).getAvailability().getEndDays());

                    if (resourceArrayList.get(i).getAvailability().getEndTime() == null
                        || resourceArrayList
                            .get(i)
                            .getAvailability()
                            .getEndTime()
                            .equalsIgnoreCase("")) {
                      endCalender.set(Calendar.HOUR_OF_DAY, 23);
                      endCalender.set(Calendar.MINUTE, 59);
                      endCalender.set(Calendar.SECOND, 59);
                    } else {
                      String[] time =
                          resourceArrayList.get(i).getAvailability().getEndTime().split(":");
                      endCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                      endCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                      endCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                    }

                    Calendar currentday = Calendar.getInstance();

                    if ((currentday.getTime().after(startCalender.getTime())
                            || currentday.getTime().equals(startCalender.getTime()))
                        && (currentday.getTime().before(endCalender.getTime())
                            || currentday.getTime().equals(endCalender.getTime()))) {
                      resources.add(resourceArrayList.get(i));
                    }
                  } catch (JSONException | ParseException e) {
                    Logger.log(e);
                  }
                }
              }
            } else {
              // if anchordate is enrollment date
              Calendar startCalender = Calendar.getInstance();
              Calendar endCalender = Calendar.getInstance();
              try {
                for (int j = 0; j < arrayList.size(); j++) {
                  if (resourceArrayList
                      .get(i)
                      .getResourcesId()
                      .equalsIgnoreCase(arrayList.get(j).getTargetActivityId())) {
                    startCalender.setTime(
                        AppController.getDateFormatForApi()
                            .parse(arrayList.get(j).getAnchorDate()));
                    startCalender.add(
                        Calendar.DATE, resourceArrayList.get(i).getAvailability().getStartDays());

                    endCalender.setTime(
                        AppController.getDateFormatForApi()
                            .parse(arrayList.get(j).getAnchorDate()));
                    endCalender.add(
                        Calendar.DATE, resourceArrayList.get(i).getAvailability().getEndDays());
                    break;
                  }
                }
                if (resourceArrayList.get(i).getAvailability().getStartTime() == null
                    || resourceArrayList
                        .get(i)
                        .getAvailability()
                        .getStartTime()
                        .equalsIgnoreCase("")) {
                  startCalender.set(Calendar.HOUR_OF_DAY, 0);
                  startCalender.set(Calendar.MINUTE, 0);
                  startCalender.set(Calendar.SECOND, 0);
                } else {
                  String[] time =
                      resourceArrayList.get(i).getAvailability().getStartTime().split(":");
                  startCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                  startCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                  startCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                }

                NotificationDbResources notificationsDb = null;
                RealmResults<NotificationDbResources> notificationsDbs =
                    dbServiceSubscriber.getNotificationDbResources(
                        AppController.getSourceActivityId(resourceArrayList.get(i)),
                        ((SurveyActivity) context).getStudyId(),
                        RESOURCES,
                        realm);
                if (notificationsDbs != null && notificationsDbs.size() > 0) {
                  for (int j = 0; j < notificationsDbs.size(); j++) {
                    if (notificationsDbs
                        .get(j)
                        .getResourceId()
                        .equalsIgnoreCase(resourceArrayList.get(i).getResourcesId())) {
                      notificationsDb = notificationsDbs.get(j);
                      break;
                    }
                  }
                }
                if (notificationsDb == null) {
                  setRemainder(
                      startCalender,
                      AppController.getSourceActivityId(resourceArrayList.get(i)),
                      ((SurveyActivity) context).getStudyId(),
                      resourceArrayList.get(i).getNotificationText(),
                      resourceArrayList.get(i).getResourcesId());
                }

                if (resourceArrayList.get(i).getAvailability().getEndTime() == null
                    || resourceArrayList
                        .get(i)
                        .getAvailability()
                        .getEndTime()
                        .equalsIgnoreCase("")) {
                  endCalender.set(Calendar.HOUR_OF_DAY, 23);
                  endCalender.set(Calendar.MINUTE, 59);
                  endCalender.set(Calendar.SECOND, 59);
                } else {
                  String[] time =
                      resourceArrayList.get(i).getAvailability().getEndTime().split(":");
                  endCalender.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
                  endCalender.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                  endCalender.set(Calendar.SECOND, Integer.parseInt(time[2]));
                }

                Calendar currentday = Calendar.getInstance();

                if ((currentday.getTime().after(startCalender.getTime())
                        || currentday.getTime().equals(startCalender.getTime()))
                    && (currentday.getTime().before(endCalender.getTime())
                        || currentday.getTime().equals(endCalender.getTime()))) {
                  resources.add(resourceArrayList.get(i));
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            }
          } else {
            resources.add(resourceArrayList.get(i));
          }
        } else if (resourceArrayList.get(i).getAudience() == null) {
          resources.add(resourceArrayList.get(i));
        }
      }
    } else {
      addStaticVal();
    }
    studyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    studyRecyclerView.setNestedScrollingEnabled(false);
    ResourcesListAdapter resourcesListAdapter =
        new ResourcesListAdapter(getActivity(), resources, this);
    studyRecyclerView.setAdapter(resourcesListAdapter);
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
            for (int j = 0; j < jsonArray1.length(); j++) {
              Type type = new TypeToken<Map<String, Object>>() {}.getType();
              JSONObject jsonObjectData = (JSONObject) jsonArray1.get(j);
              Map<String, Object> map = gson.fromJson(String.valueOf(jsonObjectData), type);

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

  private void metadataProcess() {
    AppController.getHelperProgressDialog().dismissDialog();
    setResourceAdapter();
  }

  private void setRemainder(
      Calendar startCalender,
      String activityId,
      String studyId,
      String notificationTest,
      String resourceId) {
    NotificationModuleSubscriber notificationModuleSubscriber =
        new NotificationModuleSubscriber(dbServiceSubscriber, realm);
    notificationModuleSubscriber.generateAnchorDateLocalNotification(
        startCalender.getTime(), activityId, studyId, context, notificationTest, resourceId);
  }

  private void addStaticVal() {
    ArrayList<String> labelArray = new ArrayList<String>();
    ArrayList<Resource> tempResourceArrayList = new ArrayList<>();
    tempResourceArrayList.addAll(resourceArrayList);
    resourceArrayList.clear();
    labelArray.add(getResources().getString(R.string.about_study));
    labelArray.add(getResources().getString(R.string.consent_pdf));
    labelArray.add(getResources().getString(R.string.leave_study));

    for (int i = 0; i < labelArray.size(); i++) {
      Resource r = new Resource();
      r.setTitle(labelArray.get(i));
      resourceArrayList.add(r);
    }
    resourceArrayList.addAll(tempResourceArrayList);

    tempResourceArrayList.clear();
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(context, errormsg);
    } else {
      // offline functionality
      if (responseCode == RESOURCE_REQUEST_CODE) {
        try {
          if (dbServiceSubscriber.getStudyResource(studyId, realm) == null) {
            Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
          } else if (dbServiceSubscriber.getStudyResource(studyId, realm).getResources() == null) {
            Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
          } else {
            resourceArrayList = dbServiceSubscriber.getStudyResource(studyId, realm).getResources();
            if (resourceArrayList == null || resourceArrayList.size() == 0) {
              Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
            } else {
              calculatedResources(resourceArrayList);
            }
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      } else {
        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      }
    }
  }

  public void updateuserpreference() {
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

    Studies studies =
        dbServiceSubscriber.getStudies(((SurveyActivity) context).getStudyId(), realm);

    try {
      jsonObject.put("participantId", studies.getParticipantId());
      jsonObject.put("studyId", ((SurveyActivity) context).getStudyId());
    } catch (JSONException e) {
      Logger.log(e);
    }

    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "post_object",
            Urls.WITHDRAW,
            UPDATE_USERPREFERENCE_RESPONSECODE,
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

  public void responseServerWithdrawFromStudy() {
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    dbServiceSubscriber.deleteActivityRunsFromDbByStudyID(
        context, ((SurveyActivity) context).getStudyId());
    dbServiceSubscriber.deleteResponseFromDb(((SurveyActivity) context).getStudyId(), realm);
    updateuserpreference();
  }

  @Override
  public void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
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

  public void deactivateAccount() {
    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(context, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.userid), ""));
    DeleteAccountEvent deleteAccountEvent = new DeleteAccountEvent();
    Gson gson = new Gson();
    DeleteAccountData deleteAccountData = new DeleteAccountData();
    String json = gson.toJson(deleteAccountData);
    JSONObject obj = null;
    try {
      obj = new JSONObject();
      JSONArray jsonArray1 = new JSONArray();
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("studyId", AppConfig.StudyId);
      jsonArray1.put(jsonObject);
      obj.put("studyData", jsonArray1);
    } catch (JSONException e) {
      Logger.log(e);
    }
    ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
        new ParticipantDatastoreConfigEvent(
            "delete_object",
            Urls.DELETE_ACCOUNT,
            DELETE_ACCOUNT_REPSONSECODE,
            context,
            LoginData.class,
            null,
            header,
            obj,
            false,
            this);
    deleteAccountEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performDeleteAccount(deleteAccountEvent);
  }
}
