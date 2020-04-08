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
import com.harvard.storagemodule.DBServiceSubscriber;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.CorrectAnswerString;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.events.ConsentPDFEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.studyappmodule.studymodel.ConsentPDF;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.studymodel.StudyUpdate;
import com.harvard.studyappmodule.studymodel.StudyUpdateListdata;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.studyappmodule.surveyscheduler.model.CompletionAdeherenceCalc;
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
import com.harvard.webservicemodule.events.RegistrationServerConsentConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.WCPConfigEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class StudyFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
  private static final int STUDY_UPDATES = 201;
  private static final int CONSENT_METADATA = 202;
  private static final int CONSENT_RESPONSECODE = 203;
  private static final int CONSENTPDF = 206;
  private RecyclerView mStudyRecyclerView;
  private Context mContext;
  private AppCompatTextView mEmptyListMessage;
  public static final String CONSENT = "consent";
  private final int STUDY_LIST = 10;
  private final int GET_PREFERENCES = 11;
  private final int UPDATE_PREFERENCES = 12;
  private RealmList<StudyList> studyListArrayList;
  private StudyListAdapter studyListAdapter;
  private int lastUpdatedPosition = 0;
  private boolean lastUpdatedBookMark = false;
  private String lastUpdatedStudyId;
  private String lastUpdatedStatusStatus;
  private Study mStudy;
  private String mtitle;
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

  private String mStudyId;
  private String mActivityId;
  private String mLocalNotification;
  private String eligibilityType;
  private EligibilityConsent eligibilityConsent;

  private int mDeleteIndexNumberDB;
  private String mLatestConsentVersion = "0";
  private DBServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private boolean webserviceCall = false;
  private RealmList<StudyList> mFilteredStudyList = new RealmList<>();
  private String mCalledFor = "";
  private String mFrom = "";
  private RealmList<StudyList> mSearchResultList = new RealmList<>();
  // if u click bookmark then that study(selected bookmark/ removed bookmark) should retain the list
  // until the screen refresh
  private RealmList<StudyList> mTempStudyList = new RealmList<>();
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private boolean mSwipeRefresh = false;
  private boolean mSwipeRefreshBookmarked = false;
  private ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs = new ArrayList<>();
  // while filtering
  private ArrayList<CompletionAdeherenceCalc> mFilteredCompletionAdeherenceCalcs =
      new ArrayList<>();
  // while searching
  private ArrayList<CompletionAdeherenceCalc> mSearchFilteredCompletionAdeherenceCalcs =
      new ArrayList<>();

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.mContext = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_study, container, false);
    dbServiceSubscriber = new DBServiceSubscriber();
    realm = AppController.getRealmobj(mContext);
    studyListArrayList = new RealmList<>();
    initializeXMLId(view);
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

  private void initializeXMLId(View view) {
    mStudyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
    mEmptyListMessage = (AppCompatTextView) view.findViewById(R.id.emptyListMessage);
    mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
  }

  private void bindEvents() {

    mSwipeRefreshLayout.setOnRefreshListener(
        new SwipeRefreshLayout.OnRefreshListener() {
          @Override
          public void onRefresh() {
            // Refresh items
            if (!webserviceCall) {
              webserviceCall = true;
              // to identify callGetStudyListWebservice() called from swipe to refresh
              mSwipeRefresh = true;
              mSwipeRefreshBookmarked = true;
              callGetStudyListWebservice();
            }
          }
        });
  }

  void onItemsLoadComplete() {
    // Update the adapter and notify data set changed
    // Stop refresh animation
    mSwipeRefreshLayout.setRefreshing(false);
  }

  private void setStudyList(boolean offline) {
    if (!offline) {
      dbServiceSubscriber.saveStudyListToDB(mContext, mStudy);
    }

    ArrayList<StudyList> activeInprogress = new ArrayList<>();
    ArrayList<StudyList> activeYetToJoin = new ArrayList<>();
    ArrayList<StudyList> activeOthers = new ArrayList<>();
    ArrayList<StudyList> upComing = new ArrayList<>();
    ArrayList<StudyList> paused = new ArrayList<>();
    ArrayList<StudyList> closed = new ArrayList<>();
    ArrayList<StudyList> others = new ArrayList<>();

    ArrayList<CompletionAdeherenceCalc> activeInprogressCompletionAdeherenceCalc =
        new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> activeYetToJoinCompletionAdeherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> activeOthersCompletionAdeherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> upComingCompletionAdeherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> pausedCompletionAdeherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> closedCompletionAdeherenceCalc = new ArrayList<>();
    ArrayList<CompletionAdeherenceCalc> othersCompletionAdeherenceCalc = new ArrayList<>();

    CompletionAdeherenceCalc completionAdeherenceCalc;
    CompletionAdeherenceCalc completionAdeherenceCalcSort = null;

    SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
    for (int i = 0; i < studyListArrayList.size(); i++) {
      if (!AppController.getHelperSharedPreference()
          .readPreference(mContext, mContext.getResources().getString(R.string.userid), "")
          .equalsIgnoreCase("")) {
        completionAdeherenceCalc =
            survayScheduler.completionAndAdherenceCalculation(
                studyListArrayList.get(i).getStudyId(), mContext);
        if (completionAdeherenceCalc.isActivityAvailable()) {
          completionAdeherenceCalcSort = completionAdeherenceCalc;
        } else {
          Studies studies =
              dbServiceSubscriber.getStudies(studyListArrayList.get(i).getStudyId(), realm);
          if (studies != null) {
            try {
              CompletionAdeherenceCalc completionAdeherenceCalculation =
                  new CompletionAdeherenceCalc();
              completionAdeherenceCalculation.setCompletion(studies.getCompletion());
              completionAdeherenceCalculation.setAdherence(studies.getAdherence());
              completionAdeherenceCalculation.setActivityAvailable(false);
              completionAdeherenceCalcSort = completionAdeherenceCalculation;
            } catch (Exception e) {
              CompletionAdeherenceCalc completionAdeherenceCalculation =
                  new CompletionAdeherenceCalc();
              completionAdeherenceCalculation.setAdherence(0);
              completionAdeherenceCalculation.setCompletion(0);
              completionAdeherenceCalculation.setActivityAvailable(false);
              completionAdeherenceCalcSort = completionAdeherenceCalculation;
              Logger.log(e);
            }
          } else {
            CompletionAdeherenceCalc completionAdeherenceCalculation =
                new CompletionAdeherenceCalc();
            completionAdeherenceCalculation.setAdherence(0);
            completionAdeherenceCalculation.setCompletion(0);
            completionAdeherenceCalculation.setActivityAvailable(false);
            completionAdeherenceCalcs.add(completionAdeherenceCalculation);
            completionAdeherenceCalcSort = completionAdeherenceCalculation;
          }
        }
      }
      if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)
          && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(IN_PROGRESS)) {
        activeInprogress.add(studyListArrayList.get(i));
        try {
          activeInprogressCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)
          && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(YET_TO_JOIN)) {
        activeYetToJoin.add(studyListArrayList.get(i));
        try {
          activeYetToJoinCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(ACTIVE)) {
        activeOthers.add(studyListArrayList.get(i));
        try {
          activeOthersCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(UPCOMING)) {
        upComing.add(studyListArrayList.get(i));
        try {
          upComingCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(PAUSED)) {
        paused.add(studyListArrayList.get(i));
        try {
          pausedCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(CLOSED)) {
        closed.add(studyListArrayList.get(i));
        try {
          closedCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
        } catch (Exception e) {
          Logger.log(e);
        }
      } else {
        others.add(studyListArrayList.get(i));
        try {
          othersCompletionAdeherenceCalc.add(completionAdeherenceCalcSort);
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
      completionAdeherenceCalcs.clear();
    } catch (Exception e) {
      Logger.log(e);
    }
    try {
      completionAdeherenceCalcs.addAll(activeInprogressCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(activeYetToJoinCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(activeOthersCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(upComingCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(pausedCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(closedCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    try {
      completionAdeherenceCalcs.addAll(othersCompletionAdeherenceCalc);
    } catch (Exception e) {
      Logger.log(e);
    }

    activeInprogress.clear();
    activeInprogress = null;
    activeInprogressCompletionAdeherenceCalc.clear();
    activeInprogressCompletionAdeherenceCalc = null;

    activeYetToJoin.clear();
    activeYetToJoin = null;
    activeYetToJoinCompletionAdeherenceCalc.clear();
    activeYetToJoinCompletionAdeherenceCalc = null;

    activeOthers.clear();
    activeOthers = null;
    activeOthersCompletionAdeherenceCalc.clear();
    activeOthersCompletionAdeherenceCalc = null;

    upComing.clear();
    upComing = null;
    upComingCompletionAdeherenceCalc.clear();
    upComingCompletionAdeherenceCalc = null;

    paused.clear();
    paused = null;
    pausedCompletionAdeherenceCalc.clear();
    pausedCompletionAdeherenceCalc = null;

    closed.clear();
    closed = null;
    closedCompletionAdeherenceCalc.clear();
    closedCompletionAdeherenceCalc = null;

    others.clear();
    others = null;
    othersCompletionAdeherenceCalc.clear();
    othersCompletionAdeherenceCalc = null;

    mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    mStudyRecyclerView.setNestedScrollingEnabled(false);

    String jsonObjectString =
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getString(R.string.json_object_filter), "");
    // cheking filtered conditin is ther ; if "" means no filtered condition
    if (jsonObjectString.equalsIgnoreCase("")) {
      // chkng for showing search list (scenario search--->go details screen----> return back; )
      String searchKey = ((StudyActivity) getActivity()).getSearchKey();
      if (searchKey != null) {
        // search list retain

        studyListAdapter =
            new StudyListAdapter(
                mContext,
                searchResult(searchKey),
                StudyFragment.this,
                mFilteredCompletionAdeherenceCalcs);
      } else {
        studyListAdapter =
            new StudyListAdapter(
                mContext,
                copyOfFilteredStudyList(),
                StudyFragment.this,
                mFilteredCompletionAdeherenceCalcs);
      }
    } else {
      addFilterCriteria(jsonObjectString, completionAdeherenceCalcs);
    }

    mStudyRecyclerView.setAdapter(studyListAdapter);

    if (!AppController.getHelperSharedPreference()
            .readPreference(mContext, mContext.getResources().getString(R.string.userid), "")
            .equalsIgnoreCase("")
        && AppController.getHelperSharedPreference()
            .readPreference(mContext, "firstStudyState", "")
            .equalsIgnoreCase("")) {
      mStudyRecyclerView.setVisibility(View.GONE);
    } else {
      mStudyRecyclerView.setVisibility(View.VISIBLE);
    }
  }

  private void addFilterCriteria(
      String jsonObjectString, ArrayList<CompletionAdeherenceCalc> completionAdeherenceCalcs) {
    ArrayList<String> temp1 = new ArrayList<>();
    try {
      JSONObject jsonObj = new JSONObject(jsonObjectString);
      JSONObject studyStatus = jsonObj.getJSONObject("studyStatus");
      JSONObject participationStatus = jsonObj.getJSONObject("participationStatus");
      JSONObject categories = jsonObj.getJSONObject("categories");
      boolean bookmarked = false;
      if (jsonObj.getBoolean("bookmarked")) {
        bookmarked = true;
      }

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
      if (mFilteredStudyList.size() > 0) mFilteredStudyList.clear();
      mFilteredStudyList =
          fiterMatchingStudyList(mFilteredStudyList, temp1, temp2, temp3, bookmarked);
      if (mFilteredStudyList.size() == 0)
        Toast.makeText(
                mContext,
                mContext.getResources().getString(R.string.search_data_empty),
                Toast.LENGTH_SHORT)
            .show();
      // chking that-----> currently is it working search functionality,
      String searchKey = ((StudyActivity) getActivity()).getSearchKey();
      if (searchKey != null) {
        // search list retain
        studyListAdapter =
            new StudyListAdapter(
                mContext,
                searchResult(searchKey),
                StudyFragment.this,
                mFilteredCompletionAdeherenceCalcs);
      } else {
        studyListAdapter =
            new StudyListAdapter(
                mContext,
                copyOfFilteredStudyList(),
                StudyFragment.this,
                mFilteredCompletionAdeherenceCalcs);
      }

    } catch (JSONException e) {
      Logger.log(e);
    }
  }

  // this method will retur based on the sitution, return default studylist or filter list
  private RealmList<StudyList> copyOfFilteredStudyList() {
    String jsonObjectString =
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getString(R.string.json_object_filter), "");
    RealmList<StudyList> filteredStudyList = new RealmList<>();
    if (jsonObjectString.equalsIgnoreCase("")) {
      if (mTempStudyList.size() > 0) {
        // while using swipe to refresh it should refresh whole data (scenario: after updating
        // bookmark then swipe to refresh)
        if (mSwipeRefreshBookmarked) {
          mSwipeRefreshBookmarked = false;
          filteredStudyList = defaultSelectedFilterOption(filteredStudyList);
          mTempStudyList = filteredStudyList;
        } else {
          filteredStudyList = mTempStudyList;
        }
      } else {
        filteredStudyList = defaultSelectedFilterOption(filteredStudyList);
        mTempStudyList = filteredStudyList;
      }
    } else {
      filteredStudyList.addAll(mFilteredStudyList);
    }
    return filteredStudyList;
  }

  // default filter criteria; if any changes here then accordingly make changes in
  // FilterActivity.defaultSelectedFilterOption()
  private RealmList<StudyList> defaultSelectedFilterOption(RealmList<StudyList> filteredStudyList) {

    ArrayList<String> temp1 = new ArrayList<>();
    boolean bookmarked = false;
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
        if (mFilteredCompletionAdeherenceCalcs.size() > 0)
          mFilteredCompletionAdeherenceCalcs.clear();
      } catch (Exception e) {
        Logger.log(e);
      }
      String userId =
          AppController.getHelperSharedPreference()
              .readPreference(mContext, getResources().getString(R.string.userid), "");
      // list2(Participation status disabled) is disabled // before login
      if (userId.equalsIgnoreCase("")) {
        if (studyListArrayList.size() > 0) {
          for (int i = 0; studyListArrayList.size() > i; i++) {
            for (int j = 0; list1.size() > j; j++) {
              if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(list1.get(j))) {
                for (int l = 0; list3.size() > l; l++) {
                  if (studyListArrayList.get(i).getCategory().equalsIgnoreCase(list3.get(l))) {
                    studyList.add(studyListArrayList.get(i));
                    mFilteredCompletionAdeherenceCalcs.add(completionAdeherenceCalcs.get(i));
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
                            mFilteredCompletionAdeherenceCalcs.add(
                                completionAdeherenceCalcs.get(i));
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
                          mFilteredCompletionAdeherenceCalcs.add(completionAdeherenceCalcs.get(i));
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
    if (mSwipeRefresh) {
      mSwipeRefresh = false;
      AppController.getHelperProgressDialog()
          .showSwipeListCustomProgress(getActivity(), R.drawable.transparent, false);
    } else {
      AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    }
    GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
    HashMap<String, String> header = new HashMap();
    WCPConfigEvent wcpConfigEvent =
        new WCPConfigEvent(
            "get",
            URLs.STUDY_LIST,
            STUDY_LIST,
            mContext,
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
        mStudy = (Study) response;
        studyListArrayList = mStudy.getStudies();
        if (AppController.getHelperSharedPreference()
            .readPreference(mContext, mContext.getString(R.string.userid), "")
            .equalsIgnoreCase("")) {
          AppController.getHelperProgressDialog().dismissDialog();
          onItemsLoadComplete();
          webserviceCall = false;
          setStudyList(false);
        } else {
          GetPreferenceEvent getPreferenceEvent = new GetPreferenceEvent();
          HashMap<String, String> header = new HashMap();
          header.put(
              "accessToken",
              AppController.getHelperSharedPreference()
                  .readPreference(mContext, getResources().getString(R.string.auth), ""));
          header.put(
              "userId",
              AppController.getHelperSharedPreference()
                  .readPreference(mContext, getResources().getString(R.string.userid), ""));
          header.put(
              "clientToken",
              AppController.getHelperSharedPreference()
                  .readPreference(mContext, getResources().getString(R.string.clientToken), ""));

          RegistrationServerEnrollmentConfigEvent registrationServerEnrollmentConfigEvent =
              new RegistrationServerEnrollmentConfigEvent(
                  "get",
                  URLs.STUDY_STATE,
                  GET_PREFERENCES,
                  mContext,
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
      } else {
        webserviceCall = false;
        AppController.getHelperProgressDialog().dismissDialog();
        onItemsLoadComplete();
        Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == CONSENT_METADATA) {
      AppController.getHelperProgressDialog().dismissDialog();
      eligibilityConsent = (EligibilityConsent) response;
      if (eligibilityConsent != null) {
        eligibilityConsent.setStudyId(mStudyId);
        saveConsentToDB(mContext, eligibilityConsent);
        startConsent(
            eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
      } else {
        Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == GET_PREFERENCES) {
      AppController.getHelperSharedPreference()
          .writePreference(mContext, "firstStudyState", "Done");
      AppController.getHelperProgressDialog().dismissDialog();
      onItemsLoadComplete();
      webserviceCall = false;
      StudyData studies = (StudyData) response;
      if (studies != null) {
        studies.setUserId(
            AppController.getHelperSharedPreference()
                .readPreference(mContext, getString(R.string.userid), ""));

        StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
        if (studyData == null) {
          dbServiceSubscriber.saveStudyPreferencesToDB(mContext, studies);
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
          Toast.makeText(mContext, R.string.error_retriving_data, Toast.LENGTH_SHORT).show();
        }
        setStudyList(false);
        ((StudyActivity) getContext())
            .checkForNotification(((StudyActivity) getContext()).getIntent());
      } else {
        Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == UPDATE_PREFERENCES) {
      LoginData loginData = (LoginData) response;
      AppController.getHelperProgressDialog().dismissDialog();
      if (loginData != null && loginData.getMessage().equalsIgnoreCase("success")) {
        Toast.makeText(mContext, R.string.update_success, Toast.LENGTH_SHORT).show();

        realm.beginTransaction();
        if (mSearchResultList.size() > 0) {
          // searchlist
          int pos = getFilterdArrayListPosition(lastUpdatedStudyId);
          copyOfFilteredStudyList().get(pos).setBookmarked(lastUpdatedBookMark);
        } else {
          // study or filtered list
          copyOfFilteredStudyList().get(lastUpdatedPosition).setBookmarked(lastUpdatedBookMark);
        }
        realm.commitTransaction();
        dbServiceSubscriber.updateStudyPreferenceToDb(
            mContext, lastUpdatedStudyId, lastUpdatedBookMark, lastUpdatedStatusStatus);
        studyListAdapter.notifyItemChanged(lastUpdatedPosition);
        /// delete offline row
        dbServiceSubscriber.deleteOfflineDataRow(mContext, mDeleteIndexNumberDB);
      }
    } else if (responseCode == STUDY_UPDATES) {
      StudyUpdate studyUpdate = (StudyUpdate) response;
      studyUpdate.setStudyId(mStudyId);
      StudyUpdateListdata studyUpdateListdata = new StudyUpdateListdata();
      RealmList<StudyUpdate> studyUpdates = new RealmList<>();
      studyUpdates.add(studyUpdate);
      studyUpdateListdata.setStudyUpdates(studyUpdates);
      dbServiceSubscriber.saveStudyUpdateListdataToDB(mContext, studyUpdateListdata);

      if (studyUpdate.getStudyUpdateData().isResources()) {
        dbServiceSubscriber.deleteResourcesFromDb(mContext, mStudyId);
      }
      if (studyUpdate.getStudyUpdateData().isInfo()) {
        dbServiceSubscriber.deleteStudyInfoFromDb(mContext, mStudyId);
      }
      if (studyUpdate.getStudyUpdateData().isConsent()) {
        callConsentMetaDataWebservice();
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Intent intent = new Intent(mContext, SurveyActivity.class);
        intent.putExtra("studyId", mStudyId);
        intent.putExtra("to", mCalledFor);
        intent.putExtra("from", mFrom);
        intent.putExtra("activityId", mActivityId);
        intent.putExtra("localNotification", mLocalNotification);
        mContext.startActivity(intent);
      }
    } else if (responseCode == GET_CONSENT_DOC) {
      ConsentDocumentData mConsentDocumentData = (ConsentDocumentData) response;
      mLatestConsentVersion = mConsentDocumentData.getConsent().getVersion();

      callGetConsentPDFWebservice();

    } else if (responseCode == CONSENTPDF) {
      ConsentPDF consentPDFData = (ConsentPDF) response;
      if (mLatestConsentVersion != null
          && consentPDFData != null
          && consentPDFData.getConsent() != null
          && consentPDFData.getConsent().getVersion() != null) {
        if (!consentPDFData.getConsent().getVersion().equalsIgnoreCase(mLatestConsentVersion)) {
          callConsentMetaDataWebservice();
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Intent intent = new Intent(mContext, SurveyActivity.class);
          intent.putExtra("studyId", mStudyId);
          intent.putExtra("to", mCalledFor);
          intent.putExtra("from", mFrom);
          intent.putExtra("activityId", mActivityId);
          intent.putExtra("localNotification", mLocalNotification);
          mContext.startActivity(intent);
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Intent intent = new Intent(mContext, SurveyActivity.class);
        intent.putExtra("studyId", mStudyId);
        intent.putExtra("to", mCalledFor);
        intent.putExtra("from", mFrom);
        intent.putExtra("activityId", mActivityId);
        intent.putExtra("localNotification", mLocalNotification);
        mContext.startActivity(intent);
      }
    }
  }

  private void callGetConsentPDFWebservice() {
    ConsentPDFEvent consentPDFEvent = new ConsentPDFEvent();
    HashMap<String, String> header = new HashMap<>();
    header.put(
        "accessToken",
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getResources().getString(R.string.userid), ""));
    String url = URLs.CONSENTPDF + "?studyId=" + mStudyId + "&consentVersion=";
    RegistrationServerConsentConfigEvent registrationServerConsentConfigEvent =
        new RegistrationServerConsentConfigEvent(
            "get",
            url,
            CONSENTPDF,
            mContext,
            ConsentPDF.class,
            null,
            header,
            null,
            false,
            StudyFragment.this);
    consentPDFEvent.setRegistrationServerConsentConfigEvent(registrationServerConsentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performConsentPDF(consentPDFEvent);
  }

  private void saveConsentToDB(Context context, EligibilityConsent eligibilityConsent) {
    DatabaseEvent databaseEvent = new DatabaseEvent();
    databaseEvent.setE(eligibilityConsent);
    databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY_UPDATE);
    databaseEvent.setaClass(EligibilityConsent.class);
    databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
    dbServiceSubscriber.insert(context, databaseEvent);
  }

  private void startConsent(Consent consent, String type) {
    eligibilityType = type;
    Toast.makeText(
            mContext,
            mContext.getResources().getString(R.string.please_review_the_updated_consent),
            Toast.LENGTH_SHORT)
        .show();
    ConsentBuilder consentBuilder = new ConsentBuilder();
    List<Step> consentstep = consentBuilder.createsurveyquestion(mContext, consent, mtitle);
    Task consentTask = new OrderedTask(CONSENT, consentstep);
    Intent intent =
        CustomConsentViewTaskActivity.newIntent(
            mContext, consentTask, mStudyId, "", mtitle, eligibilityType, "update");
    startActivityForResult(intent, CONSENT_RESPONSECODE);
  }

  private void callConsentMetaDataWebservice() {

    new callConsentMetaData().execute();
  }

  private class callConsentMetaData extends AsyncTask<String, Void, String> {
    String response = null;
    String responseCode = null;
    Responsemodel mResponseModel;

    @Override
    protected String doInBackground(String... params) {
      ConnectionDetector connectionDetector = new ConnectionDetector(mContext);

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
          AppController.getHelperSessionExpired(mContext, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  mContext,
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
            saveConsentToDB(mContext, eligibilityConsent);
            startConsent(
                eligibilityConsent.getConsent(), eligibilityConsent.getEligibility().getType());
          } else {
            Toast.makeText(mContext, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
          }
        } else {
          AppController.getHelperProgressDialog().dismissDialog();
          Toast.makeText(
                  mContext,
                  getResources().getString(R.string.unable_to_retrieve_data),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(mContext, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
      }
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
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
    mFrom = from;
    mtitle = title;
    mStudyId = studyId;
    mActivityId = activityId;
    mLocalNotification = localNotification;
    mCalledFor = calledFor;
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
        URLs.GET_CONSENT_DOC
            + "?studyId="
            + studyId
            + "&consentVersion=&activityId=&activityVersion=";
    AppController.getHelperProgressDialog().showProgress(getActivity(), "", "", false);
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    WCPConfigEvent wcpConfigEvent =
        new WCPConfigEvent(
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
    String url = URLs.STUDY_UPDATES + "?studyId=" + studyId + "&studyVersion=" + studyVersion;
    WCPConfigEvent wcpConfigEvent =
        new WCPConfigEvent(
            "get",
            url,
            STUDY_UPDATES,
            mContext,
            StudyUpdate.class,
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
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    onItemsLoadComplete();
    webserviceCall = false;
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(mContext, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(mContext, errormsg);
    } else if (responseCode == CONSENT_METADATA) {
      Toast.makeText(getActivity(), errormsg, Toast.LENGTH_LONG).show();
    } else if (responseCode == STUDY_UPDATES
        || responseCode == GET_CONSENT_DOC
        || responseCode == CONSENTPDF) {
      Intent intent = new Intent(mContext, SurveyActivity.class);
      intent.putExtra("studyId", mStudyId);
      intent.putExtra("to", mCalledFor);
      intent.putExtra("from", mFrom);
      intent.putExtra("activityId", mActivityId);
      intent.putExtra("localNotification", mLocalNotification);
      mContext.startActivity(intent);
    } else {
      // offline handling
      if (responseCode == UPDATE_PREFERENCES) {
        realm.beginTransaction();
        if (mSearchResultList.size() > 0) {
          // searchlist
          int pos = getFilterdArrayListPosition(lastUpdatedStudyId);
          copyOfFilteredStudyList().get(pos).setBookmarked(lastUpdatedBookMark);
        } else {
          // study or filtered list
          copyOfFilteredStudyList().get(lastUpdatedPosition).setBookmarked(lastUpdatedBookMark);
        }

        realm.commitTransaction();
        dbServiceSubscriber.updateStudyPreferenceToDb(
            mContext, lastUpdatedStudyId, lastUpdatedBookMark, lastUpdatedStatusStatus);
        studyListAdapter.notifyItemChanged(lastUpdatedPosition);
      } else {
        mEmptyListMessage.setVisibility(View.GONE);

        mStudy = dbServiceSubscriber.getStudyListFromDB(realm);
        if (mStudy != null) {
          studyListArrayList = mStudy.getStudies();
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
    AppController.getHelperProgressDialog().showProgress(mContext, "", "", false);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    lastUpdatedPosition = position;
    lastUpdatedBookMark = b;
    lastUpdatedStudyId = studyId;
    lastUpdatedStatusStatus = studyStatus;
    HashMap<String, String> header = new HashMap();
    header.put(
        "accessToken",
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getResources().getString(R.string.userid), ""));
    header.put(
        "clientToken",
        AppController.getHelperSharedPreference()
            .readPreference(mContext, getResources().getString(R.string.clientToken), ""));

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
      mDeleteIndexNumberDB = number;
      AppController.pendingService(
          mContext,
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
            mContext,
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CONSENT_RESPONSECODE) {
      if (resultCode == getActivity().RESULT_OK) {
        Intent intent = new Intent(getActivity(), ConsentCompletedActivity.class);
        intent.putExtra("studyId", mStudyId);
        intent.putExtra("title", mtitle);
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
    super.onDestroy();
  }

  public void searchFromFilteredStudyList(String searchKey) {
    try {
      searchResult(searchKey);
      if (mSearchResultList.size() == 0) {
        Toast.makeText(
                mContext,
                mContext.getResources().getString(R.string.search_data_not_available),
                Toast.LENGTH_LONG)
            .show();
      }
      studyListAdapter.modifyAdapter(mSearchResultList, mSearchFilteredCompletionAdeherenceCalcs);
      studyListAdapter.notifyDataSetChanged();
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private RealmList<StudyList> searchResult(String searchKey) {
    try {
      RealmList<StudyList> tempStudyOrFilteredList = copyOfFilteredStudyList();
      if (tempStudyOrFilteredList.size() > 0) {
        if (mSearchResultList.size() > 0) mSearchResultList.clear();
        if (mSearchFilteredCompletionAdeherenceCalcs.size() > 0)
          mSearchFilteredCompletionAdeherenceCalcs.clear();
        for (int i = 0; tempStudyOrFilteredList.size() > i; i++) {
          if (tempStudyOrFilteredList
              .get(i)
              .getSponsorName()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            mSearchResultList.add(tempStudyOrFilteredList.get(i));
            mSearchFilteredCompletionAdeherenceCalcs.add(mFilteredCompletionAdeherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getCategory()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            mSearchResultList.add(tempStudyOrFilteredList.get(i));
            mSearchFilteredCompletionAdeherenceCalcs.add(mFilteredCompletionAdeherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getTitle()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            mSearchResultList.add(tempStudyOrFilteredList.get(i));
            mSearchFilteredCompletionAdeherenceCalcs.add(mFilteredCompletionAdeherenceCalcs.get(i));
          } else if (tempStudyOrFilteredList
              .get(i)
              .getTagline()
              .toLowerCase()
              .contains(searchKey.toLowerCase())) {
            mSearchResultList.add(tempStudyOrFilteredList.get(i));
            mSearchFilteredCompletionAdeherenceCalcs.add(mFilteredCompletionAdeherenceCalcs.get(i));
          }
        }
      }

    } catch (Exception e) {
      Logger.log(e);
    }
    return mSearchResultList;
  }

  public void setStudyFilteredStudyList() {
    if (mSearchResultList.size() > 0) mSearchResultList.clear();
    if (studyListAdapter != null) {
      studyListAdapter.modifyAdapter(copyOfFilteredStudyList(), mFilteredCompletionAdeherenceCalcs);
      studyListAdapter.notifyDataSetChanged();
    }
  }

  private int getFilterdArrayListPosition(String studyId) {
    int position = 0;
    try {
      if (mSearchResultList.size() > 0) {
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
