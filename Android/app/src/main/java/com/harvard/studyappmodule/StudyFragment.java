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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.harvard.R;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.events.ConsentPdfEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.ConsentPDF;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.studymodel.StudyUpdate;
import com.harvard.studyappmodule.studymodel.StudyUpdateListdata;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.studyappmodule.surveyscheduler.model.CompletionAdherence;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.RegistrationServerConsentConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.WcpConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

public class StudyFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
  private static final int STUDY_UPDATES = 201;
  private static final int CONSENT_METADATA = 202;
  private static final int CONSENT_RESPONSECODE = 203;
  private static final int CONSENTPDF = 206;
  private RecyclerView studyRecyclerView;
  private Context context;
  private AppCompatTextView emptyListMessage;
  public static final String CONSENT = "consent";
  private static final int STUDY_LIST = 10;
  private static final int GET_PREFERENCES = 11;
  private static final int UPDATE_PREFERENCES = 12;
  private RealmList<StudyList> studyListArrayList;
  private StudyListAdapter studyListAdapter;
  private int lastUpdatedPosition = 0;
  private boolean lastUpdatedBookMark = false;
  private String lastUpdatedStudyId;
  private String lastUpdatedStatusStatus;
  private Study study;
  private String title;
  public static final String YET_TO_JOIN = "yetToJoin";
  public static final String IN_PROGRESS = "inProgress";
  public static final String COMPLETED = "completed";
  public static final String NOT_ELIGIBLE = "notEligible";
  public static final String WITHDRAWN = "withdrawn";

  private static final String ACTIVE = "active";
  public static final String UPCOMING = "upcoming";
  public static final String PAUSED = "paused";
  private static final String CLOSED = "closed";

  private static final int GET_CONSENT_DOC = 204;

  private String studyId;
  private String activityId;
  private String localNotification;
  private String eligibilityType;
  private EligibilityConsent eligibilityConsent;

  private int deleteIndexNumberDb;
  private String latestConsentVersion = "0";
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private boolean webserviceCall = false;
  private RealmList<StudyList> filteredStudyList = new RealmList<>();
  private String calledFor = "";
  private String from = "";
  private RealmList<StudyList> searchResultList = new RealmList<>();
  // if u click bookmark then that study(selected bookmark/ removed bookmark) should retain the list
  // until the screen refresh
  private RealmList<StudyList> tempStudyList = new RealmList<>();
  private SwipeRefreshLayout swipeRefreshLayout;
  private boolean swipeRefresh = false;
  private boolean swipeRefreshBookmarked = false;
  private ArrayList<CompletionAdherence> completionAdherenceCalcs = new ArrayList<>();
  // while filtering
  private ArrayList<CompletionAdherence> filteredCompletionAdherenceCalcs = new ArrayList<>();
  // while searching
  private ArrayList<CompletionAdherence> searchFilteredCompletionAdherenceCalcs =
      new ArrayList<>();

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_study, container, false);
    initializeXmlId(view);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(context);
    studyListArrayList = new RealmList<>();
    bindEvents();

    return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!webserviceCall) {
      webserviceCall = true;
      callGetStudyListWebservice();
    }
  }

  private void initializeXmlId(View view) {
    studyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
    emptyListMessage = (AppCompatTextView) view.findViewById(R.id.emptyListMessage);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
  }

  private void bindEvents() {

    swipeRefreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            // Refresh items
            if (!webserviceCall) {
              webserviceCall = true;
              // to identify callGetStudyListWebservice() called from swipe to refresh
              swipeRefresh = true;
              swipeRefreshBookmarked = true;
              callGetStudyListWebservice();
            }
          }
        });
  }

  void onItemsLoadComplete() {
    // Update the adapter and notify data set changed
    // Stop refresh animation
    swipeRefreshLayout.setRefreshing(false);
  }

  private void setStudyList(boolean offline) {
    if (!offline) {
      dbServiceSubscriber.saveStudyListToDB(context, study);
    }

    ArrayList<StudyList> activeInprogress = new ArrayList<>();
    ArrayList<StudyList> activeYetToJoin = new ArrayList<>();
    ArrayList<StudyList> activeOthers = new ArrayList<>();
    ArrayList<StudyList> upComing = new ArrayList<>();
    ArrayList<StudyList> paused = new ArrayList<>();
    ArrayList<StudyList> closed = new ArrayList<>();
    ArrayList<StudyList> others = new ArrayList<>();

    ArrayList<CompletionAdherence> activeInprogressCompletionAdherenceCalc =
        new ArrayList<>();
    ArrayList<CompletionAdherence> activeYetToJoinCompletionAdherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdherence> activeOthersCompletionAdherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdherence> upComingCompletionAdherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdherence> pausedCompletionAdherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdherence> closedCompletionAdherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdherence> othersCompletionAdherenceCalc = new ArrayList<>();

    CompletionAdherence completionAdherenceCalc;
    CompletionAdherence completionAdherenceCalcSort = null;

    SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
    for (int i = 0; i < studyListArrayList.size(); i++) {
      if (!AppController.getHelperSharedPreference()
          .readPreference(context, context.getResources().getString(R.string.userid), "")
          .equalsIgnoreCase("")) {
        completionAdherenceCalc =
            survayScheduler.completionAndAdherenceCalculation(
                studyListArrayList.get(i).getStudyId(), context);
        if (completionAdherenceCalc.isActivityAvailable()) {
          completionAdherenceCalcSort = completionAdherenceCalc;
        } else {
          Studies studies =
              dbServiceSubscriber.getStudies(studyListArrayList.get(i).getStudyId(), realm);
          if (studies != null) {
            try {
              CompletionAdherence completionAdherenceCalculation =
                  new CompletionAdherence();
              completionAdherenceCalculation.setCompletion(studies.getCompletion());
              completionAdherenceCalculation.setAdherence(studies.getAdherence());
              completionAdherenceCalculation.setActivityAvailable(false);
              completionAdherenceCalcSort = completionAdherenceCalculation;
            } catch (Exception e) {
              CompletionAdherence completionAdherenceCalculation =
                  new CompletionAdherence();
              completionAdherenceCalculation.setAdherence(0);
              completionAdherenceCalculation.setCompletion(0);
              completionAdherenceCalculation.setActivityAvailable(false);
              completionAdherenceCalcSort = completionAdherenceCalculation;
              Logger.log(e);
            }
          } else {
            CompletionAdherence completionAdherenceCalculation =
                new CompletionAdherence();
            completionAdherenceCalculation.setAdherence(0);
            completionAdherenceCalculation.setCompletion(0);
            completionAdherenceCalculation.setActivityAvailable(false);
            completionAdherenceCalcs.add(completionAdherenceCalculation);
            completionAdherenceCalcSort = completionAdherenceCalculation;
          }
        }
      }
      if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)
          && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(IN_PROGRESS)) {
        activeInprogress.add(studyListArrayList.get(i));
        try {
          activeInprogressCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)
          && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(YET_TO_JOIN)) {
        activeYetToJoin.add(studyListArrayList.get(i));
        try {
          activeYetToJoinCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)) {
        activeOthers.add(studyListArrayList.get(i));
        try {
          activeOthersCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(UPCOMING)) {
        upComing.add(studyListArrayList.get(i));
        try {
          upComingCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(PAUSED)) {
        paused.add(studyListArrayList.get(i));
        try {
          pausedCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(CLOSED)) {
        closed.add(studyListArrayList.get(i));
        try {
          closedCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else {
        others.add(studyListArrayList.get(i));
        try {
          othersCompletionAdherenceCalc.add(completionAdherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    }

    if (offline) {
      try {
        studyListArrayList = dbServiceSubscriber.clearStudyList(studyListArrayList, realm);
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        studyListArrayList =
            dbServiceSubscriber.updateStudyList(studyListArrayList, activeInprogress, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList =
            dbServiceSubscriber.updateStudyList(studyListArrayList, activeYetToJoin, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList =
            dbServiceSubscriber.updateStudyList(studyListArrayList, activeOthers, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList =
            dbServiceSubscriber.updateStudyList(studyListArrayList, upComing, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, paused, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, closed, realm);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList = dbServiceSubscriber.updateStudyList(studyListArrayList, others, realm);
      } catch (Exception e) {
        Logger.log(e);
      }
    } else {
      try {
        studyListArrayList.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        studyListArrayList.addAll(activeInprogress);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(activeYetToJoin);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(activeOthers);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(upComing);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(paused);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(closed);
      } catch (Exception e) {
        Logger.log(e);
      }

      try {
        studyListArrayList.addAll(others);
      } catch (Exception e) {
        Logger.log(e);
      }
    }

    try {
      completionAdherenceCalcs.clear();
    } catch (Exception e) {
      Logger.log(e);
    }
    try {
      completionAdherenceCalcs.addAll(activeInprogressCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(activeYetToJoinCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(activeOthersCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(upComingCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(pausedCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(closedCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdherenceCalcs.addAll(othersCompletionAdherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    activeInprogress.clear();
    activeInprogress = null;
    activeInprogressCompletionAdherenceCalc.clear();
    activeInprogressCompletionAdherenceCalc = null;

    activeYetToJoin.clear();
    activeYetToJoin = null;
    activeYetToJoinCompletionAdherenceCalc.clear();
    activeYetToJoinCompletionAdherenceCalc = null;

    activeOthers.clear();
    activeOthers = null;
    activeOthersCompletionAdherenceCalc.clear();
    activeOthersCompletionAdherenceCalc = null;

    upComing.clear();
    upComing = null;
    upComingCompletionAdherenceCalc.clear();
    upComingCompletionAdherenceCalc = null;

    paused.clear();
    paused = null;
    pausedCompletionAdherenceCalc.clear();
    pausedCompletionAdherenceCalc = null;

    closed.clear();
    closed = null;
    closedCompletionAdherenceCalc.clear();
    closedCompletionAdherenceCalc = null;

    others.clear();
    others = null;
    othersCompletionAdherenceCalc.clear();
    othersCompletionAdherenceCalc = null;

    studyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    studyRecyclerView.setNestedScrollingEnabled(false);

    String jsonObjectString =
        AppController.getHelperSharedPreference()
            .readPreference(context, getString(R.string.json_object_filter), "");
    // cheking filtered conditin is ther ; if "" means no filtered condition
    if (jsonObjectString.equalsIgnoreCase("")) {
      // chkng for showing search list (scenario search--->go details screen----> return back; )
      String searchKey = ((StudyActivity) getActivity()).getSearchKey();
      if (searchKey != null) {
        // search list retain

        studyListAdapter =
            new StudyListAdapter(
                context,
                searchResult(searchKey),
                StudyFragment.this,
                filteredCompletionAdherenceCalcs);
      } else {
        studyListAdapter =
            new StudyListAdapter(
                context,
                copyOfFilteredStudyList(),
                StudyFragment.this,
                filteredCompletionAdherenceCalcs);
      }
    } else {
      addFilterCriteria(jsonObjectString, completionAdherenceCalcs);
    }

    studyRecyclerView.setAdapter(studyListAdapter);

    if (!AppController.getHelperSharedPreference()
            .readPreference(context, context.getResources().getString(R.string.userid), "")
            .equalsIgnoreCase("")
        && AppController.getHelperSharedPreference()
            .readPreference(context, "firstStudyState", "")
            .equalsIgnoreCase("")) {
      studyRecyclerView.setVisibility(View.GONE);
    } else {
      studyRecyclerView.setVisibility(View.VISIBLE);
    }
  }

  private void addFilterCriteria(
      String jsonObjectString, ArrayList<CompletionAdherence> completionAdherenceCalcs) {
    ArrayList<String> temp1 = new ArrayList<>();
    try {
      JSONObject jsonObj = new JSONObject(jsonObjectString);

      boolean bookmarked = false;
      if (jsonObj.getBoolean("bookmarked")) {
        bookmarked = true;
      }
      JSONObject studyStatus = jsonObj.getJSONObject("studyStatus");
      if (studyStatus.getBoolean("active")) {
        temp1.add("active");
      }
      if (studyStatus.getBoolean("paused")) {
        temp1.add("paused");
      }
      if (studyStatus.getBoolean("upcoming")) {
        temp1.add("upcoming");
      }
      if (studyStatus.getBoolean("closed")) {
        temp1.add("closed");
      }
      JSONObject participationStatus = jsonObj.getJSONObject("participationStatus");
      ArrayList<String> temp2 = new ArrayList<>();
      if (participationStatus.getBoolean("inProgress")) {
        temp2.add(StudyFragment.IN_PROGRESS);
      }
      if (participationStatus.getBoolean("yetToJoin")) {
        temp2.add(StudyFragment.YET_TO_JOIN);
      }
      if (participationStatus.getBoolean("completed")) {
        temp2.add(StudyFragment.COMPLETED);
      }
      if (participationStatus.getBoolean("withdrawn")) {
        temp2.add(StudyFragment.WITHDRAWN);
      }
      if (participationStatus.getBoolean("notEligible")) {
        temp2.add(StudyFragment.NOT_ELIGIBLE);
      }
      JSONObject categories = jsonObj.getJSONObject("categories");
      ArrayList<String> temp3 = new ArrayList<>();
      if (categories.getBoolean("biologicsSafety")) {
        temp3.add("Biologics Safety");
      }
      if (categories.getBoolean("clinicalTrials")) {
        temp3.add("Clinical Trials");
      }
      if (categories.getBoolean("cosmeticsSafety")) {
        temp3.add("Cosmetics Safety");
      }
      if (categories.getBoolean("drugSafety")) {
        temp3.add("Drug Safety");
      }
      if (categories.getBoolean("foodSafety")) {
        temp3.add("Food Safety");
      }
      if (categories.getBoolean("medicalDeviceSafety")) {
        temp3.add("Medical Device Safety");
      }
      if (categories.getBoolean("observationalStudies")) {
        temp3.add("Observational Studies");
      }
      if (categories.getBoolean("publicHealth")) {
        temp3.add("Public Health");
      }
      if (categories.getBoolean("radiationEmittingProducts")) {
        temp3.add("Radiation-Emitting Products");
      }
      if (categories.getBoolean("tobaccoUse")) {
        temp3.add("Tobacco Use");
      }
      // to avoid duplicate list
      if (filteredStudyList.size() > 0) {
        filteredStudyList.clear();
      }
      filteredStudyList =
          fiterMatchingStudyList(filteredStudyList, temp1, temp2, temp3, bookmarked);
      if (filteredStudyList.size() == 0) {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.search_data_empty),
                Toast.LENGTH_SHORT)
            .show();
      }
      // chking that-----> currently is it working search functionality,
      String searchKey = ((StudyActivity) getActivity()).getSearchKey();
      if (searchKey != null) {
        // search list retain
        studyListAdapter =
            new StudyListAdapter(
                context,
                searchResult(searchKey),
                StudyFragment.this,
                filteredCompletionAdherenceCalcs);
      } else {
        studyListAdapter =
            new StudyListAdapter(
                context,
                copyOfFilteredStudyList(),
                StudyFragment.this,
                filteredCompletionAdherenceCalcs);
      }

    } catch (JSONException e) {
      Logger.log(e);
    }
  }

  // this method will retur based on the sitution, return default studylist or filter list
  private RealmList<StudyList> copyOfFilteredStudyList() {
    String jsonObjectString =
        AppController.getHelperSharedPreference()
            .readPreference(context, getString(R.string.json_object_filter), "");
    RealmList<StudyList> filteredStudyList = new RealmList<>();
    if (jsonObjectString.equalsIgnoreCase("")) {
      if (tempStudyList.size() > 0) {
        // while using swipe to refresh it should refresh whole data (scenario: after updating
        // bookmark then swipe to refresh)
        if (swipeRefreshBookmarked) {
          swipeRefreshBookmarked = false;
          filteredStudyList = defaultSelectedFilterOption(filteredStudyList);
          tempStudyList = filteredStudyList;
        } else {
          filteredStudyList = tempStudyList;
        }
      } else {
        filteredStudyList = defaultSelectedFilterOption(filteredStudyList);
        tempStudyList = filteredStudyList;
      }
    } else {
      filteredStudyList.addAll(this.filteredStudyList);
    }
    return filteredStudyList;
  }

  // default filter criteria; if any changes here then accordingly make changes in
  // FilterActivity.defaultSelectedFilterOption()
  private RealmList<StudyList> defaultSelectedFilterOption(RealmList<StudyList> filteredStudyList) {

    ArrayList<String> temp1 = new ArrayList<>();
    temp1.add("active");
    temp1.add("upcoming");

    ArrayList<String> temp2 = new ArrayList<>();
    temp2.add(StudyFragment.IN_PROGRESS);
    temp2.add(StudyFragment.YET_TO_JOIN);

    ArrayList<String> temp3 = new ArrayList<>();
    temp3.add("Biologics Safety");
    temp3.add("Clinical Trials");
    temp3.add("Cosmetics Safety");
    temp3.add("Drug Safety");
    temp3.add("Food Safety");
    temp3.add("Medical Device Safety");
    temp3.add("Observational Studies");
    temp3.add("Public Health");
    temp3.add("Radiation-Emitting Products");
    temp3.add("Tobacco Use");
    boolean bookmarked = false;
    return fiterMatchingStudyList(filteredStudyList, temp1, temp2, temp3, bookmarked);
  }

  private RealmList<StudyList> fiterMatchingStudyList(
      RealmList<StudyList> studyList,
      ArrayList<String> list1,
      ArrayList<String> list2,
      ArrayList<String> list3,
      boolean bookmarked) {
    try {
      try {
        if (filteredCompletionAdherenceCalcs.size() > 0) {
          filteredCompletionAdherenceCalcs.clear();
        }
      } catch (Exception e) {
        Logger.log(e);
      }
      String userId =
          AppController.getHelperSharedPreference()
              .readPreference(context, getResources().getString(R.string.userid), "");
      // list2(Participation status disabled) is disabled // before login
      if (userId.equalsIgnoreCase("")) {
        if (studyListArrayList.size() > 0) {
          for (int i = 0; studyListArrayList.size() > i; i++) {
            for (int j = 0; list1.size() > j; j++) {
              if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(list1.get(j))) {
                for (int l = 0; list3.size() > l; l++) {
                  if (studyListArrayList.get(i).getCategory().equalsIgnoreCase(list3.get(l))) {
                    studyList.add(studyListArrayList.get(i));
                    filteredCompletionAdherenceCalcs.add(completionAdherenceCalcs.get(i));
                    break;
                  }
                }
              }
            }
          }
        }
      } else {
        // list2 is mandatory // logged User
        if (studyListArrayList.size() > 0) {
          for (int i = 0; studyListArrayList.size() > i; i++) {
            // check only in bookmarked study list
            if (bookmarked) {
              if (studyListArrayList.get(i).isBookmarked()) {
                for (int j = 0; list1.size() > j; j++) {
                  if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(list1.get(j))) {
                    for (int k = 0; list2.size() > k; k++) {
                      if (studyListArrayList
                          .get(i)
                          .getStudyStatus()
                          .equalsIgnoreCase(list2.get(k))) {
                        for (int l = 0; list3.size() > l; l++) {
                          if (studyListArrayList
                              .get(i)
                              .getCategory()
                              .equalsIgnoreCase(list3.get(l))) {
                            studyList.add(studyListArrayList.get(i));
                            filteredCompletionAdherenceCalcs.add(completionAdherenceCalcs.get(i));
                            break;
                          }
                        }
                      }
                    }
                  }
                }
              }
            } else {
              // not bookmarked
              for (int j = 0; list1.size() > j; j++) {
                if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(list1.get(j))) {
                  for (int k = 0; list2.size() > k; k++) {
                    if (studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(list2.get(k))) {
                      for (int l = 0; list3.size() > l; l++) {
                        if (studyListArrayList
                            .get(i)
                            .getCategory()
                            .equalsIgnoreCase(list3.get(l))) {
                          studyList.add(studyListArrayList.get(i));
                          filteredCompletionAdherenceCalcs.add(completionAdherenceCalcs.get(i));
                          break;
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
    } catch (Exception e) {
      Logger.log(e);
    }
    return studyList;
  }

  private void callGetStudyListWebservice() {
    if (swipeRefresh) {
      swipeRefresh = false;
      AppController.getHelperProgressDialog()
          .showSwipeListCustomProgress(getActivity(), R.drawable.transparent, false);
    } else {
      AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    }
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get",
            Urls.STUDY_LIST,
            STUDY_LIST,
            context,
            Study.class,
            null,
            header,
            null,
            false,
            this);

    getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {

    if (responseCode == STUDY_LIST) {
      if (response != null) {
        study = (Study) response;
        studyListArrayList = study.getStudies();
        if (AppController.getHelperSharedPreference()
            .readPreference(context, context.getString(R.string.userid), "")
            .equalsIgnoreCase("")) {
          AppController.getHelperProgressDialog().dismissDialog();
          onItemsLoadComplete();
          webserviceCall = false;
          setStudyList(false);
        } else {
          HashMap<String, String> header = new HashMap();
          header.put(
              "accessToken",
              AppController.getHelperSharedPreference()
                  .readPreference(context, getResources().getString(R.string.auth), ""));
          header.put(
              "userId",
              AppController.getHelperSharedPreference()
                  .readPreference(context, getResources().getString(R.string.userid), ""));
          header.put(
              "clientToken",
              AppController.getHelperSharedPreference()
                  .readPreference(context, getResources().getString(R.string.clientToken), ""));

          RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent =
              new RegistrationServerEnrollmentConfigEvent(
                  "get",
                  Urls.STUDY_STATE,
                  GET_PREFERENCES,
                  context,
                  StudyData.class,
                  null,
                  header,
                  null,
                  false,
                  this);
          GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
          getPreferenceEvent.setRegistrationServerEnrollmentConfigEvent(
              registrationServerEnrollmentConfigEvent);
          UserModulePresenter userModulePresenter = new UserModulePresenter();
          userModulePresenter.performGetUserPreference(getPreferenceEvent);
        }
      } else {
        webserviceCall = false;
        AppController.getHelperProgressDialog().dismissDialog();
        onItemsLoadComplete();
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == CONSENT_METADATA) {
      AppController.getHelperProgressDialog().dismissDialog();
      eligibilityConsent = (EligibilityConsent) response;
      if (eligibilityConsent != null) {
        eligibilityConsent.setStudyId(studyId);
        saveConsentToDB(context, eligibilityConsent);
        startConsent(
            eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == GET_PREFERENCES) {
      AppController.getHelperSharedPreference().writePreference(context, "firstStudyState", "Done");
      AppController.getHelperProgressDialog().dismissDialog();
      onItemsLoadComplete();
      webserviceCall = false;
      StudyData studies = (StudyData) response;
      if (studies != null) {
        studies.setUserId(
            AppController.getHelperSharedPreference()
                .readPreference(context, getString(R.string.userid), ""));

        StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
        if (studyData == null) {
          dbServiceSubscriber.saveStudyPreferencesToDB(context, studies);
        } else {
          studies = studyData;
        }

        RealmList<Studies> userPreferenceStudies = studies.getStudies();
        if (userPreferenceStudies != null) {
          for (int i = 0; i < userPreferenceStudies.size(); i++) {
            for (int j = 0; j < studyListArrayList.size(); j++) {
              if (userPreferenceStudies
                  .get(i)
                  .getStudyId()
                  .equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                studyListArrayList
                    .get(j)
                    .setBookmarked(userPreferenceStudies.get(i).isBookmarked());
                studyListArrayList.get(j).setStudyStatus(userPreferenceStudies.get(i).getStatus());
              }
              // update study completed status
            }
          }
        } else {
          Toast.makeText(context, R.string.error_retriving_data, Toast.LENGTH_SHORT).show();
        }
        setStudyList(false);
        ((StudyActivity) getContext())
            .checkForNotification(((StudyActivity) getContext()).getIntent());
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == UPDATE_PREFERENCES) {
      LoginData loginData = (LoginData) response;
      AppController.getHelperProgressDialog().dismissDialog();
      if (loginData != null && loginData.getMessage().equalsIgnoreCase("success")) {
        Toast.makeText(context, R.string.update_success, Toast.LENGTH_SHORT).show();

        realm.beginTransaction();
        if (searchResultList.size() > 0) {
          // searchlist
          int pos = getFilterdArrayListPosition(lastUpdatedStudyId);
          copyOfFilteredStudyList().get(pos).setBookmarked(lastUpdatedBookMark);
        } else {
          // study or filtered list
          copyOfFilteredStudyList().get(lastUpdatedPosition).setBookmarked(lastUpdatedBookMark);
        }
        realm.commitTransaction();
        dbServiceSubscriber.updateStudyPreferenceToDb(
            context, lastUpdatedStudyId, lastUpdatedBookMark, lastUpdatedStatusStatus);
        studyListAdapter.notifyItemChanged(lastUpdatedPosition);
        /// delete offline row
        dbServiceSubscriber.deleteOfflineDataRow(context, deleteIndexNumberDb);
      }
    } else if (responseCode == STUDY_UPDATES) {
      StudyUpdate studyUpdate = (StudyUpdate) response;
      studyUpdate.setStudyId(studyId);
      StudyUpdateListdata studyUpdateListdata = new StudyUpdateListdata();
      RealmList<StudyUpdate> studyUpdates = new RealmList<>();
      studyUpdates.add(studyUpdate);
      studyUpdateListdata.setStudyUpdates(studyUpdates);
      dbServiceSubscriber.saveStudyUpdateListdataToDB(context, studyUpdateListdata);

      if (studyUpdate.getStudyUpdateData().isResources()) {
        dbServiceSubscriber.deleteResourcesFromDb(context, studyId);
      }
      if (studyUpdate.getStudyUpdateData().isInfo()) {
        dbServiceSubscriber.deleteStudyInfoFromDb(context, studyId);
      }
      if (studyUpdate.getStudyUpdateData().isConsent()) {
        callConsentMetaDataWebservice();
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra("studyId", studyId);
        intent.putExtra("to", calledFor);
        intent.putExtra("from", from);
        intent.putExtra("activityId", activityId);
        intent.putExtra("localNotification", localNotification);
        context.startActivity(intent);
      }
    } else if (responseCode == GET_CONSENT_DOC) {
      ConsentDocumentData consentDocumentData = (ConsentDocumentData) response;
      latestConsentVersion = consentDocumentData.getConsent().getVersion();
      callGetConsentPdfWebservice();
    } else if (responseCode == CONSENTPDF) {
      ConsentPDF consentPdfData = (ConsentPDF) response;
      if (latestConsentVersion != null
          && consentPdfData != null
          && consentPdfData.getConsent() != null
          && consentPdfData.getConsent().getVersion() != null) {
        if (!consentPdfData.getConsent().getVersion().equalsIgnoreCase(latestConsentVersion)) {
          callConsentMetaDataWebservice();
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Intent intent = new Intent(context, SurveyActivity.class);
          intent.putExtra("studyId", studyId);
          intent.putExtra("to", calledFor);
          intent.putExtra("from", from);
          intent.putExtra("activityId", activityId);
          intent.putExtra("localNotification", localNotification);
          context.startActivity(intent);
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Intent intent = new Intent(context, SurveyActivity.class);
        intent.putExtra("studyId", studyId);
        intent.putExtra("to", calledFor);
        intent.putExtra("from", from);
        intent.putExtra("activityId", activityId);
        intent.putExtra("localNotification", localNotification);
        context.startActivity(intent);
      }
    }
  }

  private void callGetConsentPdfWebservice() {
    ConsentPdfEvent consentPdfEvent = new ConsentPdfEvent();
    HashMap<String, String> header = new HashMap<>();
    header.put(
        "accessToken",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.userid), ""));
    String url = Urls.CONSENTPDF + "?studyId=" + studyId + "&consentVersion=";
    RegistrationServerConsentConfigEvent registrationServerConsentConfigEvent =
        new RegistrationServerConsentConfigEvent(
            "get",
            url,
            CONSENTPDF,
            context,
            ConsentPDF.class,
            null,
            header,
            null,
            false,
            StudyFragment.this);
    consentPdfEvent.setRegistrationServerConsentConfigEvent(registrationServerConsentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performConsentPdf(consentPdfEvent);
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
    ConsentBuilder consentBuilder = new ConsentBuilder();
    List<Step> consentstep = consentBuilder.createsurveyquestion(context, consent, title);
    Task consentTask = new OrderedTask(CONSENT, consentstep);
    Intent intent =
        CustomConsentViewTaskActivity.newIntent(
            context, consentTask, studyId, "", title, eligibilityType, "update");
    startActivityForResult(intent, CONSENT_RESPONSECODE);
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

      String url = Urls.BASE_URL_WCP_SERVER + Urls.CONSENT_METADATA + "?studyId=" + studyId;
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
          AppController.getHelperSessionExpired(context, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  context,
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
                  getResources().getString(R.string.unable_to_retrieve_data),
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

  public void getStudyUpdate(
      String studyId,
      String studyVersion,
      String title,
      String calledFor,
      String from,
      String activityId,
      String localNotification) {
    this.from = from;
    this.title = title;
    this.studyId = studyId;
    this.activityId = activityId;
    this.localNotification = localNotification;
    this.calledFor = calledFor;
    StudyData studyData = dbServiceSubscriber.getStudyPreferences(realm);
    Studies studies = null;
    if (studyData != null && studyData.getStudies() != null) {
      for (int i = 0; i < studyData.getStudies().size(); i++) {
        if (studyData.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
          studies = studyData.getStudies().get(i);
        }
      }
    }
    if (studies != null
        && studies.getVersion() != null
        && !studies.getVersion().equalsIgnoreCase(studyVersion)) {
      getStudyUpdateFomWS(studyId, studies.getVersion());
    } else {
      getCurrentConsentDocument(studyId);
    }
  }

  private void getCurrentConsentDocument(String studyId) {
    HashMap<String, String> header = new HashMap<>();
    String url =
        Urls.GET_CONSENT_DOC
            + "?studyId="
            + studyId
            + "&consentVersion=&activityId=&activityVersion=";
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get",
            url,
            GET_CONSENT_DOC,
            getActivity(),
            ConsentDocumentData.class,
            null,
            header,
            null,
            false,
            StudyFragment.this);

    getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  private void getStudyUpdateFomWS(String studyId, String studyVersion) {
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    String url = Urls.STUDY_UPDATES + "?studyId=" + studyId + "&studyVersion=" + studyVersion;
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get", url, STUDY_UPDATES, context, StudyUpdate.class, null, header, null, false, this);

    getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    onItemsLoadComplete();
    webserviceCall = false;
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(context, errormsg);
    } else if (responseCode == CONSENT_METADATA) {
      Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
    } else if (responseCode == STUDY_UPDATES
        || responseCode == GET_CONSENT_DOC
        || responseCode == CONSENTPDF) {
      Intent intent = new Intent(context, SurveyActivity.class);
      intent.putExtra("studyId", studyId);
      intent.putExtra("to", calledFor);
      intent.putExtra("from", from);
      intent.putExtra("activityId", activityId);
      intent.putExtra("localNotification", localNotification);
      context.startActivity(intent);
    } else {
      // offline handling
      if (responseCode == UPDATE_PREFERENCES) {
        realm.beginTransaction();
        if (searchResultList.size() > 0) {
          // searchlist
          int pos = getFilterdArrayListPosition(lastUpdatedStudyId);
          copyOfFilteredStudyList().get(pos).setBookmarked(lastUpdatedBookMark);
        } else {
          // study or filtered list
          copyOfFilteredStudyList().get(lastUpdatedPosition).setBookmarked(lastUpdatedBookMark);
        }

        realm.commitTransaction();
        dbServiceSubscriber.updateStudyPreferenceToDb(
            context, lastUpdatedStudyId, lastUpdatedBookMark, lastUpdatedStatusStatus);
        studyListAdapter.notifyItemChanged(lastUpdatedPosition);
      } else {
        emptyListMessage.setVisibility(View.GONE);

        study = dbServiceSubscriber.getStudyListFromDB(realm);
        if (study != null) {
          studyListArrayList = study.getStudies();
          studyListArrayList =
              dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
          setStudyList(true);
        } else {
          Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
        }
      }
    }
  }

  public void updatebookmark(boolean b, int position, String studyId, String studyStatus) {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);

    lastUpdatedPosition = position;
    lastUpdatedBookMark = b;
    lastUpdatedStudyId = studyId;
    lastUpdatedStatusStatus = studyStatus;
    HashMap<String, String> header = new HashMap();
    header.put(
        "accessToken",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.userid), ""));
    header.put(
        "clientToken",
        AppController.getHelperSharedPreference()
            .readPreference(context, getResources().getString(R.string.clientToken), ""));

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
          context,
          number,
          "post_object",
          Urls.UPDATE_STUDY_PREFERENCE,
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
            Urls.UPDATE_STUDY_PREFERENCE,
            UPDATE_PREFERENCES,
            context,
            LoginData.class,
            null,
            header,
            jsonObject,
            false,
            this);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    updatePreferenceEvent.setRegistrationServerEnrollmentConfigEvent(
        registrationServerEnrollmentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CONSENT_RESPONSECODE) {
      if (resultCode == getActivity().RESULT_OK) {
        Intent intent = new Intent(getActivity(), ConsentCompletedActivity.class);
        intent.putExtra("studyId", studyId);
        intent.putExtra("title", title);
        intent.putExtra("eligibility", eligibilityType);
        intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
        // get the encrypted file path
        intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
        startActivity(intent);
      }
    }
  }

  @Override
  public void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    AppController.getHelperProgressDialog().dismissDialog();
    super.onDestroy();
  }

  public void searchFromFilteredStudyList(String searchKey) {
    try {
      searchResult(searchKey);
      if (searchResultList.size() == 0) {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.search_data_not_available),
                Toast.LENGTH_LONG)
            .show();
      }
      studyListAdapter.modifyAdapter(searchResultList, searchFilteredCompletionAdherenceCalcs);
      studyListAdapter.notifyDataSetChanged();
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private RealmList<StudyList> searchResult(String searchKey) {
    try {
      RealmList<StudyList> tempStudyOrFilteredList = copyOfFilteredStudyList();
      if (tempStudyOrFilteredList.size() > 0) {
        if (searchResultList.size() > 0) {
          searchResultList.clear();
        }
        if (searchFilteredCompletionAdherenceCalcs.size() > 0) {
          searchFilteredCompletionAdherenceCalcs.clear();
        }
        for (int i = 0; tempStudyOrFilteredList.size() > i; i++) {
          if (tempStudyOrFilteredList
              .get(i)
              .getSponsorName()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            searchResultList.add(tempStudyOrFilteredList.get(i));
            searchFilteredCompletionAdherenceCalcs.add(filteredCompletionAdherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getCategory()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            searchResultList.add(tempStudyOrFilteredList.get(i));
            searchFilteredCompletionAdherenceCalcs.add(filteredCompletionAdherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getTitle()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            searchResultList.add(tempStudyOrFilteredList.get(i));
            searchFilteredCompletionAdherenceCalcs.add(filteredCompletionAdherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getTagline()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            searchResultList.add(tempStudyOrFilteredList.get(i));
            searchFilteredCompletionAdherenceCalcs.add(filteredCompletionAdherenceCalcs.get(i));
          }
        }
      }

    } catch (Exception e) {
      Logger.log(e);
    }
    return searchResultList;
  }

  public void setStudyFilteredStudyList() {
    if (searchResultList.size() > 0) {
      searchResultList.clear();
    }
    if (studyListAdapter != null) {
      studyListAdapter.modifyAdapter(copyOfFilteredStudyList(), filteredCompletionAdherenceCalcs);
      studyListAdapter.notifyDataSetChanged();
    }
  }

  private int getFilterdArrayListPosition(String studyId) {
    int position = 0;
    try {
      if (searchResultList.size() > 0) {
        RealmList<StudyList> tempStudyOrFilteredList = copyOfFilteredStudyList();
        for (int i = 0; tempStudyOrFilteredList.size() > i; i++) {
          if (tempStudyOrFilteredList.get(i).getStudyId().equalsIgnoreCase(studyId)) {
            position = i;
            break;
          }
        }
      }
    } catch (Exception e) {
      Logger.log(e);
    }
    return position;
  }
}
