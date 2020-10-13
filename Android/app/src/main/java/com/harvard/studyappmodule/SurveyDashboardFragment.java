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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.circularprogressbar.DonutProgress;
import com.harvard.studyappmodule.custom.result.StepRecordCustom;
import com.harvard.studyappmodule.events.GetActivityListEvent;
import com.harvard.studyappmodule.studymodel.DashboardData;
import com.harvard.studyappmodule.studymodel.ResponseInfoActiveTaskModel;
import com.harvard.studyappmodule.studymodel.Statistics;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.studyappmodule.surveyscheduler.model.CompletionAdherence;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.apihelper.ConnectionDetector;
import com.harvard.webservicemodule.apihelper.HttpRequest;
import com.harvard.webservicemodule.apihelper.Responsemodel;
import com.harvard.webservicemodule.events.WcpConfigEvent;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SurveyDashboardFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
  private static final int DASHBOARD_INFO = 111;
  private Context context;
  private RelativeLayout backBtn;
  private AppCompatTextView title1;
  private RelativeLayout shareBtn;
  private AppCompatTextView completionValue;
  private DonutProgress progressBar1;
  private AppCompatTextView adherenceValue;
  private DonutProgress progressBar2;
  private AppCompatTextView statisticsLabel;
  private RelativeLayout monthLayout;
  private RelativeLayout weekLayout;
  private RelativeLayout dayLayout;
  private AppCompatTextView dayLabel;
  private AppCompatTextView weekLabel;
  private AppCompatTextView monthLabel;
  private AppCompatTextView changeDateLabel;
  private RelativeLayout previousDateLayout;
  private RelativeLayout nextDateLayout;
  private LinearLayout totalStaticsLayout;
  private AppCompatImageView statsIcon;
  private AppCompatTextView totalHoursSleep;
  private AppCompatTextView totalHoursSleepVal;
  private RelativeLayout trendLayout;
  private AppCompatTextView trends;
  private int currentYear;
  private int currentMonth;
  private int currentDay;
  // It is used identify which one is selected  DAY, WEEK, MONTH
  // store the value eg: 21, Apr 2017
  private String fromDayVal;
  private String toDayVal;
  private View view;
  private DashboardData dashboardData;
  private String dateType = "day";
  private static final String MONTH = "month";
  private static final String DAY = "day";
  private static final String WEEK = "week";
  private AppCompatTextView studyStatusLabel;
  private AppCompatTextView studyStatus;
  private AppCompatTextView participationStatusLabel;
  private AppCompatTextView participationStatus;
  private AppCompatTextView completionText1;
  private AppCompatTextView completionPercentage;
  private AppCompatTextView completionText2;
  private AppCompatTextView adherenceText1;
  private AppCompatTextView adherencePercentage;
  private AppCompatTextView adherenceText2;
  private ScrollView scrollView;
  private RelativeLayout middleView;
  private HorizontalScrollView scrollViewHor;
  private AppCompatImageView rightArrow;
  private AppCompatImageView previousArrow;
  private AppCompatTextView noStatsAvailable;
  private Studies studies;
  private ArrayList<ResponseInfoActiveTaskModel> arrayList;
  private ArrayList<String> arrayListDup;

  // NOTE: Regarding Day, Week and Month functionality
  //  currently day functionality next, previous are working
  //  week funtionality also working but month is not implemented
  //  till now we don't the exact functionality
  //  while changing next month dispaly Apr 2017 - May 2017, so againg change to week
  //  what we need to dispaly
  //  for handling day, week and month click added flags
  //  Once exact functionalities discussed thn bettr to implement
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private static final int PERMISSION_REQUEST_CODE = 2000;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    view = inflater.inflate(R.layout.fragment_survey_dashboard, container, false);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(context);
    initializeXmlId(view);
    setFont();
    setCurrentDateMonthYear();
    // default for day settings
    fromDayVal = setCurrentDay();
    toDayVal = fromDayVal;
    // first 2 digit set color
    changeDateLabel.setText(setColorSpannbleString(fromDayVal, 2));
    setColorForSelectedDayMonthYear(dayLayout);
    bindEvents();
    setStudyStatus();
    setParticipationStatus();
    getDashboardData();
    return view;
  }

  private void setStudyStatus() {
    String status = ((SurveyActivity) context).getStatus();
    studyStatus.setText(status);
    if (status.equalsIgnoreCase("active")) {
      studyStatus.setTextColor(context.getResources().getColor(R.color.bullet_green_color));
    } else if (status.equalsIgnoreCase("upcoming")) {
      studyStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    } else if (status.equalsIgnoreCase("closed")) {
      studyStatus.setTextColor(context.getResources().getColor(R.color.red));
    } else if (status.equalsIgnoreCase("paused")) {
      studyStatus.setTextColor(context.getResources().getColor(R.color.rectangle_yellow));
    }
  }

  private void setParticipationStatus() {
    String participationStatusVal = ((SurveyActivity) context).getStudyStatus();
    if (participationStatusVal != null) {
      if (participationStatusVal.equalsIgnoreCase(StudyFragment.COMPLETED)) {
        participationStatus.setText(R.string.completed);
        participationStatus.setTextColor(
            context.getResources().getColor(R.color.bullet_green_color));
      } else if (participationStatusVal.equalsIgnoreCase(StudyFragment.NOT_ELIGIBLE)) {
        participationStatus.setText(R.string.not_eligible);
        participationStatus.setTextColor(context.getResources().getColor(R.color.red));
      } else if (participationStatusVal.equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
        participationStatus.setText(R.string.in_progress);
        participationStatus.setTextColor(context.getResources().getColor(R.color.rectangle_yellow));
      } else if (participationStatusVal.equalsIgnoreCase(StudyFragment.YET_TO_JOIN)) {
        participationStatus.setText(R.string.yet_to_join);
        participationStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
      } else if (participationStatusVal.equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
        participationStatus.setText(R.string.withdrawn);
        participationStatus.setTextColor(context.getResources().getColor(R.color.colorSecondary));
      } else {
        participationStatus.setText(R.string.yet_to_join);
        participationStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
      }
    } else {
      participationStatus.setText(R.string.yet_to_join);
      participationStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
    }
  }

  private void getDashboardData() {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    GetActivityListEvent getActivityListEvent = new GetActivityListEvent();
    HashMap<String, String> header = new HashMap();
    String url = Urls.DASHBOARD_INFO + "?studyId=" + ((SurveyActivity) context).getStudyId();
    WcpConfigEvent wcpConfigEvent =
        new WcpConfigEvent(
            "get",
            url,
            DASHBOARD_INFO,
            context,
            DashboardData.class,
            null,
            header,
            null,
            false,
            this);

    getActivityListEvent.setWcpConfigEvent(wcpConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetActivityList(getActivityListEvent);
  }

  private void initializeXmlId(View view) {
    backBtn = (RelativeLayout) view.findViewById(R.id.backBtn);
    title1 = (AppCompatTextView) view.findViewById(R.id.mTitle1);
    shareBtn = (RelativeLayout) view.findViewById(R.id.mShareBtn);
    completionValue = (AppCompatTextView) view.findViewById(R.id.mCompletionValue);
    progressBar1 = view.findViewById(R.id.progressBar1);
    adherenceValue = (AppCompatTextView) view.findViewById(R.id.mAdherenceValue);
    progressBar2 = view.findViewById(R.id.progressBar2);
    statisticsLabel = (AppCompatTextView) view.findViewById(R.id.mStatisticsLabel);

    monthLayout = (RelativeLayout) view.findViewById(R.id.mMonthLayout);
    weekLayout = (RelativeLayout) view.findViewById(R.id.mWeekLayout);
    dayLayout = (RelativeLayout) view.findViewById(R.id.mDayLayout);
    dayLabel = (AppCompatTextView) view.findViewById(R.id.mDayLabel);
    weekLabel = (AppCompatTextView) view.findViewById(R.id.mWeekLabel);
    monthLabel = (AppCompatTextView) view.findViewById(R.id.mMonthLabel);
    changeDateLabel = (AppCompatTextView) view.findViewById(R.id.mChangeDateLabel);
    previousDateLayout = (RelativeLayout) view.findViewById(R.id.mPreviousDateLayout);
    nextDateLayout = (RelativeLayout) view.findViewById(R.id.mNextDateLayout);

    totalStaticsLayout = (LinearLayout) view.findViewById(R.id.mTotalStaticsLayout);
    trendLayout = (RelativeLayout) view.findViewById(R.id.mTrendLayout);
    trends = (AppCompatTextView) view.findViewById(R.id.mTrends);

    RelativeLayout statisticsLayout = (RelativeLayout) view.findViewById(R.id.mStatisticsLayout);
    RelativeLayout changeDateLayout = (RelativeLayout) view.findViewById(R.id.mChangeDateLayout);

    studyStatusLabel = (AppCompatTextView) view.findViewById(R.id.mStudyStatusLabel);
    studyStatus = (AppCompatTextView) view.findViewById(R.id.mStudyStatus);
    participationStatusLabel =
        (AppCompatTextView) view.findViewById(R.id.mParticipationStatusLabel);
    participationStatus = (AppCompatTextView) view.findViewById(R.id.mParticipationStatus);
    completionText1 = (AppCompatTextView) view.findViewById(R.id.mCompletionText1);
    completionPercentage = (AppCompatTextView) view.findViewById(R.id.mCompletionPercentage);
    completionText2 = (AppCompatTextView) view.findViewById(R.id.mCompletionText2);
    adherenceText1 = (AppCompatTextView) view.findViewById(R.id.mAdherenceText1);
    adherencePercentage = (AppCompatTextView) view.findViewById(R.id.mAdherencePercentage);
    adherenceText2 = (AppCompatTextView) view.findViewById(R.id.mAdherenceText2);
    scrollView = (ScrollView) view.findViewById(R.id.mScrollView);
    middleView = (RelativeLayout) view.findViewById(R.id.middleView);
    scrollViewHor = (HorizontalScrollView) view.findViewById(R.id.mHScrollView);
    rightArrow = (AppCompatImageView) view.findViewById(R.id.mRightArrow);
    previousArrow = (AppCompatImageView) view.findViewById(R.id.mPreviousArrow);
    noStatsAvailable = (AppCompatTextView) view.findViewById(R.id.mNoStatsAvailable);

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

  // DAY, WEEK, YEAR dynamically change bg and text color
  private void setColorForSelectedDayMonthYear(RelativeLayout layout) {

    if (layout == dayLayout) {
      dayLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
      dayLabel.setTextColor(getResources().getColor(R.color.white));
      GradientDrawable layoutBgShape = (GradientDrawable) dayLayout.getBackground();
      layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
      weekLayout.setBackgroundResource(0);
      monthLayout.setBackgroundResource(0);

      weekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
      monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));

    } else if (layout == weekLayout) {
      weekLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
      weekLabel.setTextColor(getResources().getColor(R.color.white));
      GradientDrawable layoutBgShape = (GradientDrawable) weekLayout.getBackground();
      layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
      dayLayout.setBackgroundResource(0);
      monthLayout.setBackgroundResource(0);

      dayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
      monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
    } else if (layout == monthLayout) {
      monthLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
      monthLabel.setTextColor(getResources().getColor(R.color.white));
      GradientDrawable layoutBgShape = (GradientDrawable) monthLayout.getBackground();
      layoutBgShape.setColor(getResources().getColor(R.color.colorPrimary));
      weekLayout.setBackgroundResource(0);
      dayLayout.setBackgroundResource(0);

      dayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
      weekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
    }
  }

  private void setFont() {
    try {
      title1.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      completionValue.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      adherenceValue.setTypeface(AppController.getTypeface(getActivity(), "regular"));

      statisticsLabel.setTypeface(AppController.getTypeface(getActivity(), "medium"));
      dayLabel.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      weekLabel.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      monthLabel.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      trends.setTypeface(AppController.getTypeface(getActivity(), "medium"));
      changeDateLabel.setTypeface(AppController.getTypeface(getActivity(), "medium"));
      studyStatusLabel.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      studyStatus.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      participationStatusLabel.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      participationStatus.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      completionText1.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      completionPercentage.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      completionText2.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      adherenceText1.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      adherencePercentage.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      adherenceText2.setTypeface(AppController.getTypeface(getActivity(), "regular"));
      noStatsAvailable.setTypeface(AppController.getTypeface(getActivity(), "medium"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void setCurrentDateMonthYear() {
    // Get current date by calender
    try {
      final Calendar c = Calendar.getInstance();
      currentYear = c.get(Calendar.YEAR);
      currentMonth = c.get(Calendar.MONTH);
      currentDay = c.get(Calendar.DAY_OF_MONTH);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
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

    shareBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            screenshotWritingPermission(view);
          }
        });

    dayLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!dateType.equalsIgnoreCase(DAY)) {
              setDay();
              addViewStatisticsValuesRefresh();
            }
          }
        });

    weekLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!dateType.equalsIgnoreCase(WEEK)) {
              setWeek();
              addViewStatisticsValuesRefresh();
            }
          }
        });
    monthLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              if (!dateType.equalsIgnoreCase(MONTH)) {
                setMonth();
                addViewStatisticsValuesRefresh();
              }
            } catch (Exception e) {
              Logger.log(e);
            }
          }
        });
    previousDateLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            if (dateType.equalsIgnoreCase(DAY)) {
              try {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
                Date selectedStartDAte = simpleDateFormat.parse(fromDayVal);
                Date selectedEndDate = simpleDateFormat.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, -1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, -1);
                fromDayVal = simpleDateFormat.format(calendarStart.getTime());
                toDayVal = simpleDateFormat.format(calendarEnd.getTime());
                SimpleDateFormat dateFormatForDashboardCurrentDayOut =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                changeDateLabel.setText(
                    dateFormatForDashboardCurrentDayOut.format(calendarStart.getTime()));

              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateType.equalsIgnoreCase(WEEK)) {
              try {
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte = dateFormatForApi.parse(fromDayVal);
                Date selectedEndDate = dateFormatForApi.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, -7);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, -7);
                fromDayVal = dateFormatForApi.format(calendarStart.getTime());
                toDayVal = dateFormatForApi.format(calendarEnd.getTime());
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                changeDateLabel.setText(
                    simpleDateFormat.format(calendarStart.getTime())
                        + " - "
                        + simpleDateFormat.format(calendarEnd.getTime()));
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateType.equalsIgnoreCase(MONTH)) {
              try {
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte = dateFormatForApi.parse(fromDayVal);
                Date selectedEndDate = dateFormatForApi.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.MONTH, -1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.MONTH, -1);
                fromDayVal = dateFormatForApi.format(calendarStart.getTime());
                toDayVal = dateFormatForApi.format(calendarEnd.getTime());
                SimpleDateFormat dateFormatForChartAndStat =
                    AppController.getDateFormatForChartAndStat();
                changeDateLabel.setText(dateFormatForChartAndStat.format(calendarStart.getTime()));

              } catch (ParseException e) {
                Logger.log(e);
              }
            }
            addViewStatisticsValuesRefresh();
          }
        });
    nextDateLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            if (dateType.equalsIgnoreCase(DAY)) {
              try {
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte = dateFormatForApi.parse(fromDayVal);
                Date selectedEndDate = dateFormatForApi.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, 1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, 1);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVal = dateFormatForApi.format(calendarStart.getTime());
                  toDayVal = dateFormatForApi.format(calendarEnd.getTime());

                  changeDateLabel.setText(simpleDateFormat.format(calendarStart.getTime()));
                  addViewStatisticsValuesRefresh();
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateType.equalsIgnoreCase(WEEK)) {
              try {
                SimpleDateFormat simpleDateFormat =
                    AppController.getDateFormatForDashboardAndChartCurrentDayOut();
                SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
                Date selectedStartDAte = dateFormatForApi.parse(fromDayVal);
                Date selectedEndDate = dateFormatForApi.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.DATE, 7);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.DATE, 7);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVal = dateFormatForApi.format(calendarStart.getTime());
                  toDayVal = dateFormatForApi.format(calendarEnd.getTime());

                  if (calendarEnd.getTime().after(new Date())) {
                    changeDateLabel.setText(
                        simpleDateFormat.format(calendarStart.getTime())
                            + " - "
                            + simpleDateFormat.format(new Date()));
                  } else {
                    changeDateLabel.setText(
                        simpleDateFormat.format(calendarStart.getTime())
                            + " - "
                            + simpleDateFormat.format(calendarEnd.getTime()));
                  }
                  addViewStatisticsValuesRefresh();
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            } else if (dateType.equalsIgnoreCase(MONTH)) {
              try {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
                SimpleDateFormat dateFormatForChartAndStat =
                    AppController.getDateFormatForChartAndStat();
                Date selectedStartDAte = simpleDateFormat.parse(fromDayVal);
                Date selectedEndDate = simpleDateFormat.parse(toDayVal);
                Calendar calendarStart = Calendar.getInstance();
                calendarStart.setTime(selectedStartDAte);
                calendarStart.add(Calendar.MONTH, 1);
                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(selectedEndDate);
                calendarEnd.add(Calendar.MONTH, 1);
                if (!calendarStart.getTime().after(new Date())) {
                  fromDayVal = simpleDateFormat.format(calendarStart.getTime());
                  toDayVal = simpleDateFormat.format(calendarEnd.getTime());

                  changeDateLabel.setText(
                      dateFormatForChartAndStat.format(calendarStart.getTime()));
                  addViewStatisticsValuesRefresh();
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            }
          }
        });
    trendLayout.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (dashboardData != null && dashboardData.getDashboard().getCharts().size() > 0) {
              Intent intent = new Intent(context, ChartActivity.class);
              intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
              intent.putExtra("studyName", ((SurveyActivity) context).getTitle1());
              startActivity(intent);
            } else {
              Toast.makeText(
                      context,
                      context.getResources().getString(R.string.no_charts_display),
                      Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
  }

  private void screenshotWritingPermission(View view) {
    // checking the permissions
    if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)
        || (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED)) {
      String[] permission =
          new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
          };
      if (!hasPermissions(permission)) {
        ActivityCompat.requestPermissions((Activity) context, permission, PERMISSION_REQUEST_CODE);
      } else {
        // sharing pdf creating
        shareFunctionality(view);
      }
    } else {
      // sharing pdf creating
      shareFunctionality(view);
    }
  }

  public boolean hasPermissions(String[] permissions) {
    if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.M && permissions != null) {
      for (String permission : permissions) {
        if (ActivityCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
        Toast.makeText(
                context,
                getResources().getString(R.string.permission_enable_message_screenshot),
                Toast.LENGTH_LONG)
            .show();
      } else {
        shareFunctionality(view);
      }
    }
  }

  private void shareFunctionality(View v) {
    View v1 = v.getRootView();
    v1.setDrawingCacheEnabled(true);
    Bitmap bm = v1.getDrawingCache();
    saveBitmap(bm);
  }

  private void saveBitmap(Bitmap bitmap) {
    String root;
    if (Build.VERSION.SDK_INT < VERSION_CODES.Q) {
      root = Environment.getExternalStorageDirectory().getAbsolutePath();
    } else {
      root = getActivity().getExternalFilesDir(getString(R.string.app_name)).getAbsolutePath();
    }
    File dir = new File(root + "/Android/FDA/Screenshot");
    dir.mkdirs();
    String fname = ((SurveyActivity) context).getTitle1() + "_Dashboard.png";
    File file = new File(dir, fname);
    if (file.exists()) {
      file.delete();
    }
    try {
      FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      Logger.log(e);
    }
    sendMail(file, fname.split("\\.")[0]);
  }

  public void sendMail(File file, String subject) {
    Intent shareIntent = new Intent(Intent.ACTION_SEND);
    shareIntent.setData(Uri.parse("mailto:"));
    shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    shareIntent.setType("text/plain");
    Uri fileUri =
        FileProvider.getUriForFile(context, getString(R.string.FileProvider_authorities), file);
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
    startActivity(shareIntent);
  }

  private String setCurrentDay() {
    try {
      int month = currentMonth + 1;
      String originDate = currentDay + " " + month + " " + currentYear;
      SimpleDateFormat dateFormatForDashboardCurrentDay =
          AppController.getDateFormatForDashboardCurrentDay();
      SimpleDateFormat formatOut = AppController.getDateFormatForDashboardAndChartCurrentDayOut();
      SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(dateFormatForDashboardCurrentDay.parse(originDate));
      String newDate = formatOut.format(calendar.getTime());
      return newDate;
    } catch (ParseException e) {
      Logger.log(e);
      return "";
    }
  }

  private Spannable setColorSpannbleString(String str, int endVal) {
    Spannable wordtoSpan = new SpannableString(str);
    wordtoSpan.setSpan(
        new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),
        0,
        endVal,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    return wordtoSpan;
  }

  // Statistics Dynamically genarate
  private void addViewStatisticsValues() {

    if (dashboardData != null && dashboardData.getDashboard().getStatistics().size() > 0) {
      setDay();
      for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
        RelativeLayout activitiesLayout =
            (RelativeLayout)
                view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
        addViewStatisticsInitializeXmlId(activitiesLayout);
        addViewStatisticsSetFont();
        addViewStatisticsSetText(
            dashboardData.getDashboard().getStatistics().get(i), activitiesLayout);
        totalStaticsLayout.addView(activitiesLayout);
      }
    } else {
      setWeekUnSelected();
      drawableImageColorChange();
      changeDateLabel.setText(getResources().getString(R.string.date_range));
      for (int i = 0; i < 3; i++) {
        RelativeLayout activitiesLayout =
            (RelativeLayout)
                view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
        RelativeLayout rel = (RelativeLayout) activitiesLayout.findViewById(R.id.mRectBoxLayout);
        rel.setBackground(getResources().getDrawable(R.color.colorSecondaryBg));
        totalStaticsLayout.addView(activitiesLayout);
      }
      disableHorizontalView(middleView);
      scrollViewHor.setOnTouchListener(
          new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              return true;
            }
          });
      noStatsAvailable.setVisibility(View.VISIBLE);
    }
  }

  private void setWeekUnSelected() {
    try {
      weekLayout.setBackground(getResources().getDrawable(R.drawable.blue_radius));
      weekLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
      GradientDrawable layoutBgShape = (GradientDrawable) weekLayout.getBackground();
      layoutBgShape.setColor(getResources().getColor(R.color.colorSecondaryBg));
      dayLayout.setBackgroundResource(0);
      monthLayout.setBackgroundResource(0);
      dayLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
      monthLabel.setTextColor(getResources().getColor(R.color.colorSecondary));
    } catch (Resources.NotFoundException e) {
      Logger.log(e);
    }
  }

  private void drawableImageColorChange() {
    try {
      Resources res = getResources();
      final Drawable drawableRight = res.getDrawable(R.drawable.arrow2_right);
      drawableRight.setColorFilter(
          getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
      rightArrow.setBackgroundDrawable(drawableRight);

      final Drawable drawableLeft = res.getDrawable(R.drawable.arrow2_left);
      drawableLeft.setColorFilter(
          getResources().getColor(R.color.colorSecondary), PorterDuff.Mode.SRC_ATOP);
      previousArrow.setBackgroundDrawable(drawableLeft);

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private static void disableHorizontalView(ViewGroup layout) {
    layout.setEnabled(false);
    for (int i = 0; i < layout.getChildCount(); i++) {
      View child = layout.getChildAt(i);
      if (child instanceof ViewGroup) {
        disableHorizontalView((ViewGroup) child);
      } else {
        child.setEnabled(false);
      }
    }
  }

  private void addViewStatisticsValuesRefresh() {

    if (dashboardData != null) {
      if (dashboardData.getDashboard().getStatistics().size() > 0) {
        for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
          addViewStatisticsSetText(
              dashboardData.getDashboard().getStatistics().get(i),
              totalStaticsLayout.getChildAt(i));
        }
      } else {
        setWeekUnSelected();
        drawableImageColorChange();
        changeDateLabel.setText(getResources().getString(R.string.date_range));
        for (int i = 0; i < 3; i++) {
          RelativeLayout activitiesLayout =
              (RelativeLayout)
                  view.inflate(getActivity(), R.layout.content_survey_dashboard_statistics, null);
          RelativeLayout rel = (RelativeLayout) activitiesLayout.findViewById(R.id.mRectBoxLayout);
          rel.setBackground(getResources().getDrawable(R.color.colorSecondaryBg));
          totalStaticsLayout.addView(activitiesLayout);
        }
        disableHorizontalView(middleView);
        scrollViewHor.setOnTouchListener(
            new View.OnTouchListener() {
              @Override
              public boolean onTouch(View v, MotionEvent event) {
                return true;
              }
            });
        noStatsAvailable.setVisibility(View.VISIBLE);
      }
    }
  }

  private void addViewStatisticsInitializeXmlId(View view) {
    statsIcon = (AppCompatImageView) view.findViewById(R.id.mStatsIcon);
    totalHoursSleep = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleep);
    totalHoursSleepVal = (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleepVal);
  }

  private void addViewStatisticsSetFont() {

    totalHoursSleep.setTypeface(AppController.getTypeface(getActivity(), "regular"));
    totalHoursSleepVal.setTypeface(AppController.getTypeface(getActivity(), "regular"));
  }

  private void addViewStatisticsSetText(Statistics statistics, View view) {

    SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
    switch (statistics.getStatType()) {
      case "Activity":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_activity));
        break;
      case "Sleep":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_sleep));
        break;
      case "Weight":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_weight));
        break;
      case "Heart Rate":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_heart_rate));
        break;
      case "Nutrition":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_nutrition));
        break;
      case "Blood Glucose":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_glucose));
        break;
      case "Active Task":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_active_task));
        break;
      case "Baby Kicks":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_baby_kicks));
        break;
      case "Other":
        statsIcon.setBackground(getResources().getDrawable(R.drawable.stat_icn_other));
        break;
    }

    AppCompatTextView totalHoursSleep =
        (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleep);

    AppCompatTextView unit = (AppCompatTextView) view.findViewById(R.id.mUnit);
    totalHoursSleep.setText(statistics.getDisplayName());
    unit.setText(statistics.getUnit());

    RealmResults<StepRecordCustom> stepRecordCustomList = null;
    try {
      stepRecordCustomList =
          dbServiceSubscriber.getResultForStat(
              ((SurveyActivity) context).getStudyId()
                  + "_STUDYID_"
                  + statistics.getDataSource().getActivity().getActivityId(),
              statistics.getDataSource().getKey(),
              simpleDateFormat.parse(fromDayVal),
              simpleDateFormat.parse(toDayVal),
              realm);
    } catch (ParseException e) {
      Logger.log(e);
    }

    ArrayList<Double> resultlist = new ArrayList<>();
    for (int i = 0; i < stepRecordCustomList.size(); i++) {
      if (stepRecordCustomList.get(i) != null) {
        JSONObject formResultObj = null;
        try {
          formResultObj = new JSONObject(stepRecordCustomList.get(i).result);
          String answer;
          String[] id = stepRecordCustomList.get(i).activityID.split("_STUDYID_");
          ActivitiesWS activityObj = dbServiceSubscriber.getActivityObj(id[1], id[0], realm);
          if (activityObj.getType().equalsIgnoreCase("task")) {
            JSONObject answerjson = new JSONObject(formResultObj.getString("answer"));
            answer = answerjson.getString("duration");
            answer = Double.toString(Integer.parseInt(answer) / 60f);
          } else {
            answer = formResultObj.getString("answer");
          }
          resultlist.add(Double.parseDouble(answer));
        } catch (JSONException e) {
          Logger.log(e);
        }
      }
    }
    String result = "N/A";
    if (resultlist.size() > 0) {
      if (statistics.getCalculation().equalsIgnoreCase("Minimum")) {
        result = "" + Collections.min(resultlist);
      } else if (statistics.getCalculation().equalsIgnoreCase("Maximum")) {
        result = "" + Collections.max(resultlist);
      } else if (statistics.getCalculation().equalsIgnoreCase("Average")) {
        result = "" + calculateAverage(resultlist);
      } else if (statistics.getCalculation().equalsIgnoreCase("Summation")) {
        double val = 0.0;
        for (int i = 0; i < resultlist.size(); i++) {
          val += Double.parseDouble("" + resultlist.get(i));
        }
        result = "" + val;
      }
      result = String.format("%.2f", Double.parseDouble(result));
    }
    AppCompatTextView totalHoursSleepVal =
        (AppCompatTextView) view.findViewById(R.id.mTotalHoursSleepVal);
    totalHoursSleepVal.setText(result);
  }

  private double calculateAverage(ArrayList<Double> marks) {
    Double sum = 0.0;
    if (!marks.isEmpty()) {
      for (Double mark : marks) {
        sum += mark;
      }
      return sum / marks.size();
    }
    return sum;
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == DASHBOARD_INFO) {
      dashboardData = (DashboardData) response;
      if (dashboardData != null) {
        dashboardData.setStudyId(((SurveyActivity) context).getStudyId());
        if (dashboardData.getDashboard().getCharts().isEmpty()) {
          trendLayout.setVisibility(View.GONE);
        }
        scrollView.setVisibility(View.VISIBLE);
        dbServiceSubscriber.saveStudyDashboardToDB(context, dashboardData);
        new ProcessData().execute();
      } else {
        scrollView.setVisibility(View.VISIBLE);
        new ProcessData().execute();
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == DASHBOARD_INFO) {
      if (statusCode.equalsIgnoreCase("401")) {
        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
        AppController.getHelperSessionExpired(context, errormsg);
      } else {
        scrollView.setVisibility(View.VISIBLE);
        dashboardData =
            dbServiceSubscriber.getDashboardDataFromDB(
                ((SurveyActivity) context).getStudyId(), realm);
        if (dashboardData != null) {
          new ProcessData().execute();
        } else {
          Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  private class ProcessData extends AsyncTask<String, Void, String> {
    CompletionAdherence completionAdherenceCalc;

    @Override
    protected String doInBackground(String... params) {

      return null;
    }

    @Override
    protected void onPostExecute(String result) {
      SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
      completionAdherenceCalc =
          survayScheduler.completionAndAdherenceCalculation(
              ((SurveyActivity) context).getStudyId(), context);
      if (completionAdherenceCalc.isNoCompletedAndMissed()) {
        completionValue.setText("-- ");
        progressBar1.setProgress(0);
        adherenceValue.setText("-- ");
        progressBar2.setProgress(0);
      } else {
        completionValue.setText("" + (int) completionAdherenceCalc.getCompletion());
        progressBar1.setProgress((int) completionAdherenceCalc.getCompletion());
        adherenceValue.setText("" + (int) completionAdherenceCalc.getAdherence());
        progressBar2.setProgress((int) completionAdherenceCalc.getAdherence());
      }

      title1.setText(((SurveyActivity) context).getTitle1());

      arrayList = new ArrayList<>();
      arrayListDup = new ArrayList<>();
      if (dashboardData != null) {
        for (int i = 0; i < dashboardData.getDashboard().getStatistics().size(); i++) {
          ResponseInfoActiveTaskModel responseInfoActiveTaskModel =
              new ResponseInfoActiveTaskModel();
          if (!arrayListDup.contains(
              dashboardData
                  .getDashboard()
                  .getStatistics()
                  .get(i)
                  .getDataSource()
                  .getActivity()
                  .getActivityId())) {
            responseInfoActiveTaskModel.setActivityId(
                dashboardData
                    .getDashboard()
                    .getStatistics()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getActivityId());
            responseInfoActiveTaskModel.setActivityVersion(
                dashboardData
                    .getDashboard()
                    .getStatistics()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getVersion());
            responseInfoActiveTaskModel.setKey(
                dashboardData.getDashboard().getStatistics().get(i).getDataSource().getKey());
            arrayList.add(responseInfoActiveTaskModel);
            arrayListDup.add(
                dashboardData
                    .getDashboard()
                    .getStatistics()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getActivityId());
          }
        }
        for (int i = 0; i < dashboardData.getDashboard().getCharts().size(); i++) {
          ResponseInfoActiveTaskModel responseInfoActiveTaskModel =
              new ResponseInfoActiveTaskModel();
          if (!arrayListDup.contains(
              dashboardData
                  .getDashboard()
                  .getCharts()
                  .get(i)
                  .getDataSource()
                  .getActivity()
                  .getActivityId())) {
            responseInfoActiveTaskModel.setActivityId(
                dashboardData
                    .getDashboard()
                    .getCharts()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getActivityId());
            responseInfoActiveTaskModel.setActivityVersion(
                dashboardData
                    .getDashboard()
                    .getCharts()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getVersion());
            responseInfoActiveTaskModel.setKey(
                dashboardData.getDashboard().getCharts().get(i).getDataSource().getKey());
            arrayList.add(responseInfoActiveTaskModel);
            arrayListDup.add(
                dashboardData
                    .getDashboard()
                    .getCharts()
                    .get(i)
                    .getDataSource()
                    .getActivity()
                    .getActivityId());
          }
        }
      }

      studies = dbServiceSubscriber.getStudies(((SurveyActivity) context).getStudyId(), realm);
      if (arrayList.size() > 0) {
        new ResponseData(
                ((SurveyActivity) context).getStudyId(),
                arrayList.get(0),
                studies.getParticipantId(),
                0)
            .execute();
      } else {
        addViewStatisticsValues();
      }
    }

    @Override
    protected void onPreExecute() {}
  }

  private void setDay() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
    fromDayVal = dateFormatForApi.format(calendar.getTime());

    Calendar calendar1 = Calendar.getInstance();
    calendar1.set(Calendar.HOUR_OF_DAY, 23);
    calendar1.set(Calendar.MINUTE, 59);
    calendar1.set(Calendar.SECOND, 59);
    calendar1.set(Calendar.MILLISECOND, 999);
    toDayVal = dateFormatForApi.format(calendar1.getTime());
    SimpleDateFormat simpleDateFormat =
        AppController.getDateFormatForDashboardAndChartCurrentDayOut();
    changeDateLabel.setText(simpleDateFormat.format(calendar.getTime()));
    setColorForSelectedDayMonthYear(dayLayout);
    dateType = DAY;
  }

  private void setWeek() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    SimpleDateFormat dateFormatForApi = AppController.getDateFormatForApi();
    fromDayVal = dateFormatForApi.format(calendar.getTime());

    calendar.add(Calendar.DATE, 6);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    toDayVal = dateFormatForApi.format(calendar.getTime());

    SimpleDateFormat simpleDateFormat =
        AppController.getDateFormatForDashboardAndChartCurrentDayOut();
    String text =
        simpleDateFormat.format(calendar.getTime()) + " - " + simpleDateFormat.format(new Date());
    changeDateLabel.setText(text);
    setColorForSelectedDayMonthYear(weekLayout);
    dateType = WEEK;
  }

  private void setMonth() {

    Calendar calendar = Calendar.getInstance(); // this takes current date
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
    fromDayVal = simpleDateFormat.format(calendar.getTime());
    toDayVal = simpleDateFormat.format(new Date());

    calendar.add(Calendar.MONTH, 1);
    calendar.add(Calendar.DATE, -1);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    calendar.set(Calendar.MILLISECOND, 999);
    toDayVal = simpleDateFormat.format(calendar.getTime());

    SimpleDateFormat dateFormatForChartAndStat = AppController.getDateFormatForChartAndStat();
    String text = dateFormatForChartAndStat.format(calendar.getTime());
    changeDateLabel.setText(text);
    setColorForSelectedDayMonthYear(monthLayout);
    dateType = MONTH;
  }

  @Override
  public void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  private class ResponseData extends AsyncTask<String, Void, String> {

    String participateId;
    ResponseInfoActiveTaskModel responseInfoActiveTaskModel;
    String response = null;
    String responseCode = null;
    String studyId;
    int position;
    Responsemodel responseModel;
    String id;
    String stepKey;
    String queryParam = "*";

    ResponseData(
        String studyId,
        ResponseInfoActiveTaskModel responseInfoActiveTaskModel,
        String participateId,
        int position) {
      this.studyId = studyId;
      this.responseInfoActiveTaskModel = responseInfoActiveTaskModel;
      this.participateId = participateId;
      this.position = position;
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
        Studies studies = realm.where(Studies.class).equalTo("studyId", studyId).findFirst();
        responseModel =
            HttpRequest.getRequest(
                Urls.PROCESSRESPONSEDATA
                    + AppConfig.APP_ID_KEY
                    + "="
                    + AppConfig.APP_ID_VALUE
                    + "&participantId="
                    + participateId
                    + "&tokenIdentifier="
                    + studies.getHashedToken()
                    + "&siteId="
                    + studies.getSiteId()
                    + "&studyId="
                    + studies.getStudyId()
                    + "&activityId="
                    + responseInfoActiveTaskModel.getActivityId()
                    + "&questionKey="
                    + responseInfoActiveTaskModel.getKey()
                    + "&activityVersion="
                    + responseInfoActiveTaskModel.getActivityVersion(),
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
    protected void onPreExecute() {
      super.onPreExecute();
      AppController.getHelperProgressDialog().showProgress(context, "", "", false);
      id = responseInfoActiveTaskModel.getActivityId();
      stepKey = responseInfoActiveTaskModel.getKey();
      ActivityListData activityListData = dbServiceSubscriber.getActivities(studyId, realm);
      if (activityListData != null) {
        RealmList<ActivitiesWS> activitiesWSes = activityListData.getActivities();
        for (int i = 0; i < activitiesWSes.size(); i++) {
          if (activitiesWSes
              .get(i)
              .getActivityId()
              .equalsIgnoreCase(responseInfoActiveTaskModel.getActivityId())) {
            if (activitiesWSes.get(i).getType().equalsIgnoreCase("task")) {
              id =
                  responseInfoActiveTaskModel.getActivityId()
                      + responseInfoActiveTaskModel.getKey();
              queryParam = "%22count%22,%22Created%22,%22duration%22";
            }
          }
        }
      }
    }

    @Override
    protected void onPostExecute(String response) {
      super.onPostExecute(response);
      if (response != null) {
        if (response.equalsIgnoreCase("session expired")) {
          AppController.getHelperProgressDialog().dismissDialog();
          AppController.getHelperSessionExpired(context, "session expired");
        } else if (response.equalsIgnoreCase("timeout")) {
          addViewStatisticsValues();
          AppController.getHelperProgressDialog().dismissDialog();
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
              if (arrayList.size() > (position + 1)) {
                new ResponseData(
                        ((SurveyActivity) context).getStudyId(),
                        arrayList.get((position + 1)),
                        studies.getParticipantId(),
                        position + 1)
                    .execute();
              } else {
                addViewStatisticsValues();
                AppController.getHelperProgressDialog().dismissDialog();
              }
            } else {
              addViewStatisticsValues();
              AppController.getHelperProgressDialog().dismissDialog();
            }
          } catch (JSONException e) {
            addViewStatisticsValues();
            AppController.getHelperProgressDialog().dismissDialog();
            Logger.log(e);
          }
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK) {
          try {
            SimpleDateFormat simpleDateFormat = AppController.getLabkeyDateFormat();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = (JSONArray) jsonObject.get("rows");
            Gson gson = new Gson();
            for (int i = 0; i < jsonArray.length(); i++) {
              JSONObject jsonObject1 = new JSONObject(String.valueOf(jsonArray.get(i)));
              JSONArray jsonArray1 = (JSONArray) jsonObject1.get("data");
              int duration = 0;
              for (int j = 0; j < jsonArray1.length(); j++) {
                JSONObject jsonObjectData = (JSONObject) jsonArray1.get(j);
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> map = gson.fromJson(String.valueOf(jsonObjectData), type);
                StepRecordCustom stepRecordCustom = new StepRecordCustom();
                Date completedDate = new Date();
                try {
                  Object completedDateValMap = gson.toJson(map.get("Created"));
                  Map<String, Object> completedDateVal =
                      gson.fromJson(String.valueOf(completedDateValMap), type);
                  if (completedDateVal != null) {
                    completedDate =
                        simpleDateFormat.parse(String.valueOf(completedDateVal.get("value")));
                  }
                } catch (JsonSyntaxException | ParseException e) {
                  Logger.log(e);
                }

                try {
                  Object durationValMap = gson.toJson(map.get("duration"));
                  Map<String, Object> completedDateVal =
                      gson.fromJson(String.valueOf(durationValMap), type);
                  if (completedDateVal != null) {
                    duration = (int) Double.parseDouble("" + completedDateVal.get("value"));
                  }
                } catch (Exception e) {
                  Logger.log(e);
                }
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                  String key = entry.getKey();
                  String valueobj = gson.toJson(entry.getValue());
                  Map<String, Object> vauleMap = gson.fromJson(String.valueOf(valueobj), type);
                  Object value = vauleMap.get("value");
                  if (!key.equalsIgnoreCase("container")
                      && !key.equalsIgnoreCase("ParticipantId")
                      && !key.equalsIgnoreCase("EntityId")
                      && !key.equalsIgnoreCase("Modified")
                      && !key.equalsIgnoreCase("lastIndexed")
                      && !key.equalsIgnoreCase("ModifiedBy")
                      && !key.equalsIgnoreCase("CreatedBy")
                      && !key.equalsIgnoreCase("Key")
                      && !key.equalsIgnoreCase("duration")
                      && !key.equalsIgnoreCase(stepKey + "Id")
                      && !key.equalsIgnoreCase("Created")) {
                    int runId =
                        dbServiceSubscriber.getActivityRunForStatsAndCharts(
                            responseInfoActiveTaskModel.getActivityId(),
                            studyId,
                            completedDate,
                            realm);
                    if (key.equalsIgnoreCase("count")) {
                      stepRecordCustom.setStepId(stepKey);
                      stepRecordCustom.setTaskStepID(
                          studyId
                              + "_STUDYID_"
                              + responseInfoActiveTaskModel.getActivityId()
                              + "_"
                              + runId
                              + "_"
                              + stepKey);
                    } else {
                      stepRecordCustom.setStepId(key);
                      stepRecordCustom.setTaskStepID(
                          studyId
                              + "_STUDYID_"
                              + responseInfoActiveTaskModel.getActivityId()
                              + "_"
                              + runId
                              + "_"
                              + key);
                    }
                    stepRecordCustom.setStudyId(studyId);
                    stepRecordCustom.setActivityID(
                        studyId + "_STUDYID_" + responseInfoActiveTaskModel.getActivityId());
                    stepRecordCustom.setTaskId(
                        studyId
                            + "_STUDYID_"
                            + responseInfoActiveTaskModel.getActivityId()
                            + "_"
                            + runId);

                    stepRecordCustom.setCompleted(completedDate);
                    stepRecordCustom.setStarted(completedDate);

                    try {
                      Date anchordate = AppController.getLabkeyDateFormat().parse("" + value);
                      value = AppController.getDateFormatForApi().format(anchordate);
                    } catch (ParseException e) {
                      Logger.log(e);
                    }
                  }
                  JSONObject jsonObject2 = new JSONObject();
                  ActivitiesWS activityObj =
                      dbServiceSubscriber.getActivityObj(
                          responseInfoActiveTaskModel.getActivityId(), studyId, realm);
                  if (activityObj.getType().equalsIgnoreCase("task")) {
                    JSONObject jsonObject3 = new JSONObject();
                    jsonObject3.put("value", value);
                    jsonObject3.put("duration", duration);

                    jsonObject2.put("answer", jsonObject3);
                  } else {
                    jsonObject2.put("answer", value);
                  }

                  stepRecordCustom.setResult(String.valueOf(jsonObject2));
                  Number currentIdNum = dbServiceSubscriber.getStepRecordCustomId(realm);
                  if (currentIdNum == null) {
                    stepRecordCustom.setId(1);
                  } else {
                    stepRecordCustom.setId(currentIdNum.intValue() + 1);
                  }
                  dbServiceSubscriber.updateStepRecord(context, stepRecordCustom);
                }
              }
            }
            if (arrayList.size() > (position + 1)) {
              new ResponseData(
                      ((SurveyActivity) context).getStudyId(),
                      arrayList.get((position + 1)),
                      studies.getParticipantId(),
                      position + 1)
                  .execute();
            } else {
              addViewStatisticsValues();
              AppController.getHelperProgressDialog().dismissDialog();
            }
          } catch (Exception e) {
            Logger.log(e);
            if (arrayList.size() > (position + 1)) {
              new ResponseData(
                      ((SurveyActivity) context).getStudyId(),
                      arrayList.get((position + 1)),
                      studies.getParticipantId(),
                      position + 1)
                  .execute();
            } else {
              addViewStatisticsValues();
              AppController.getHelperProgressDialog().dismissDialog();
            }
          }
        } else {
          if (arrayList.size() > (position + 1)) {
            new ResponseData(
                    ((SurveyActivity) context).getStudyId(),
                    arrayList.get((position + 1)),
                    studies.getParticipantId(),
                    position + 1)
                .execute();
          } else {
            addViewStatisticsValues();
            AppController.getHelperProgressDialog().dismissDialog();
          }
        }
      } else {
        addViewStatisticsValues();
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(context, getString(R.string.unknown_error), Toast.LENGTH_SHORT).show();
      }
    }
  }
}
