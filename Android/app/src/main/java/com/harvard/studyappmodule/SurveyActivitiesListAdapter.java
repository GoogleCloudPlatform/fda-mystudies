/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
 *
 */

package com.harvard.studyappmodule;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.studyappmodule.surveyscheduler.model.ActivityStatus;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SurveyActivitiesListAdapter
        extends RecyclerView.Adapter<SurveyActivitiesListAdapter.Holder>
        implements CustomActivitiesDailyDialogClass.DialogClick {
  private final Context context;
  public ArrayList<ActivitiesWS> items;
  private SurveyActivitiesFragment surveyActivitiesFragment;
  ArrayList<String> status;
  private static final String TEXT_EVERY = " every ";
  private static final String TEXT_EVERY_MONTH = " each month";
  ArrayList<ActivityStatus> currentRunStatusForActivities;
  private boolean click = true;
  private boolean paused;
  private Date joiningDate;
  private ArrayList<Integer> timePos = new ArrayList<>();
  private CustomFirebaseAnalytics analyticsInstance;

  SurveyActivitiesListAdapter(
          Context context,
          ArrayList<ActivitiesWS> items,
          ArrayList<String> status,
          ArrayList<ActivityStatus> currentRunStatusForActivities,
          SurveyActivitiesFragment surveyActivitiesFragment,
          boolean paused,
          Date joiningDate) {
    this.context = context;
    this.items = items;
    this.status = status;
    this.surveyActivitiesFragment = surveyActivitiesFragment;
    this.currentRunStatusForActivities = currentRunStatusForActivities;
    this.paused = paused;
    this.joiningDate = joiningDate;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
            LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.survey_activities_list_item, parent, false);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(context);
    return new Holder(v);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  @Override
  public void clicked(int positon) {
  }

  class Holder extends RecyclerView.ViewHolder {
    final RelativeLayout stateLayout;
    final AppCompatTextView state;
    final LinearLayout itemlayout;
    final RelativeLayout container;
    final RelativeLayout box1;
    final RelativeLayout box2;
    final AppCompatImageView surveyIcon;
    final AppCompatTextView whenWasSurvey;
    final AppCompatTextView surveyTitle;
    final AppCompatTextView time;
    final AppCompatTextView date;
    final AppCompatTextView process;
    final AppCompatTextView run;
    final View hrLine1;
    final RelativeLayout container2;
    final AppCompatTextView more;

    Holder(View itemView) {
      super(itemView);

      stateLayout = (RelativeLayout) itemView.findViewById(R.id.stateLayout);
      state = (AppCompatTextView) itemView.findViewById(R.id.state);
      run = (AppCompatTextView) itemView.findViewById(R.id.run);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
      itemlayout = (LinearLayout) itemView.findViewById(R.id.itemlayout);
      box1 = (RelativeLayout) itemView.findViewById(R.id.box1);
      box2 = (RelativeLayout) itemView.findViewById(R.id.box2);
      surveyIcon = (AppCompatImageView) itemView.findViewById(R.id.surveyIcon);
      whenWasSurvey = (AppCompatTextView) itemView.findViewById(R.id.whenWasSurvey);
      surveyTitle = (AppCompatTextView) itemView.findViewById(R.id.surveyTitle);
      time = (AppCompatTextView) itemView.findViewById(R.id.time);
      date = (AppCompatTextView) itemView.findViewById(R.id.date);
      process = (AppCompatTextView) itemView.findViewById(R.id.process);
      hrLine1 = itemView.findViewById(R.id.hrLine1);
      container2 = (RelativeLayout) itemView.findViewById(R.id.container2);
      more = (AppCompatTextView) itemView.findViewById(R.id.more);
      setFont();
    }

    private void setFont() {
      try {
        state.setTypeface(AppController.getTypeface(context, "medium"));
        whenWasSurvey.setTypeface(AppController.getTypeface(context, "bold"));
        surveyTitle.setTypeface(AppController.getTypeface(context, "bold"));
        time.setTypeface(AppController.getTypeface(context, "regular"));
        date.setTypeface(AppController.getTypeface(context, "regular"));
        process.setTypeface(AppController.getTypeface(context, "medium"));
        run.setTypeface(AppController.getTypeface(context, "medium"));
        more.setTypeface(AppController.getTypeface(context, "medium"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, int position) {
   final ArrayList<String> mScheduledTime = new ArrayList<>();
    timePos.add(-1);
    GradientDrawable bgShape = (GradientDrawable) holder.process.getBackground();
    if (status
            .get(holder.getAdapterPosition())
            .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)) {
      holder.state.setText(R.string.current1);
    } else if (status
            .get(holder.getAdapterPosition())
            .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
      holder.state.setText(R.string.upcoming1);
    } else {
      holder.state.setText(R.string.past);
    }
    if (holder.getAdapterPosition() == 0
            || !status
            .get(holder.getAdapterPosition())
            .equalsIgnoreCase(status.get(holder.getAdapterPosition() - 1))) {
      holder.stateLayout.setVisibility(View.VISIBLE);
      holder.hrLine1.setVisibility(View.GONE);
    } else {
      holder.hrLine1.setVisibility(View.VISIBLE);
      holder.stateLayout.setVisibility(View.GONE);
    }

    if (items.get(holder.getAdapterPosition()).getActivityId().equalsIgnoreCase("")
            || (items.get(holder.getAdapterPosition()).getActivityId().equalsIgnoreCase(null))) {
      holder.container2.setVisibility(View.VISIBLE);
      holder.container.setVisibility(View.GONE);
      holder.hrLine1.setVisibility(View.GONE);
    } else {
      holder.container2.setVisibility(View.GONE);
      holder.container.setVisibility(View.VISIBLE);
      if (status
              .get(holder.getAdapterPosition())
              .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)
              || status
              .get(holder.getAdapterPosition())
              .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
        holder.process.setVisibility(View.GONE);
      } else if (currentRunStatusForActivities
              .get(holder.getAdapterPosition())
              .getStatus()
              .equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START)) {
        holder.process.setVisibility(View.VISIBLE);
        holder.process.setText(R.string.start);
        bgShape.setColor(context.getResources().getColor(R.color.colorPrimary));
      } else if (currentRunStatusForActivities
              .get(holder.getAdapterPosition())
              .getStatus()
              .equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)) {
        holder.process.setVisibility(View.VISIBLE);
        holder.process.setText(R.string.resume);
        bgShape.setColor(context.getResources().getColor(R.color.rectangle_yellow));
      } else if (currentRunStatusForActivities
              .get(holder.getAdapterPosition())
              .getStatus()
              .equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
        holder.process.setVisibility(View.VISIBLE);
        holder.process.setText(R.string.completed2);
        bgShape.setColor(context.getResources().getColor(R.color.bullet_green_color));
      } else if (currentRunStatusForActivities
              .get(holder.getAdapterPosition())
              .getStatus()
              .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
        holder.process.setVisibility(View.VISIBLE);
        holder.process.setText(R.string.missedrun);
        bgShape.setColor(context.getResources().getColor(R.color.red));
      } else {
        holder.process.setVisibility(View.VISIBLE);
      }

      if (items.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("questionnaire")) {
        holder.surveyIcon.setImageResource(R.drawable.survey_icn_small);
      } else if (items.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("task")) {
        holder.surveyIcon.setImageResource(R.drawable.task_icn_small);
      } else {
        holder.surveyIcon.setVisibility(View.INVISIBLE);
      }

      if (status
              .get(holder.getAdapterPosition())
              .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
        holder.run.setVisibility(View.GONE);
      } else {
        if (currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId() == 0) {
          holder.process.setVisibility(View.GONE);
        }
        holder.run.setVisibility(View.VISIBLE);
        holder.run.setText(
                context.getResources().getString(R.string.run)
                        + ": "
                        + currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId()
                        + " of "
                        + currentRunStatusForActivities.get(holder.getAdapterPosition()).getTotalRun()
                        + ", "
                        + currentRunStatusForActivities.get(holder.getAdapterPosition()).getCompletedRun()
                        + " "
                        + context.getResources().getString(R.string.done2)
                        + ", "
                        + currentRunStatusForActivities.get(holder.getAdapterPosition()).getMissedRun()
                        + " "
                        + context.getResources().getString(R.string.missed));
      }
      // completed status incomplete/complete settings
      if (status
              .get(holder.getAdapterPosition())
              .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
        int missedRunVal = currentRunStatusForActivities.get(holder.getAdapterPosition()).getMissedRun();
        int currentRunVal = currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId();
        int totalRunVal = currentRunStatusForActivities.get(holder.getAdapterPosition()).getTotalRun();
        int completedRunVal = currentRunStatusForActivities.get(holder.getAdapterPosition()).getCompletedRun();
        if (missedRunVal == 0 && currentRunVal == 0 && totalRunVal == 0 && completedRunVal == 0) {
          holder.process.setVisibility(View.VISIBLE);
          holder.process.setText(R.string.expired);
          bgShape.setColor(context.getResources().getColor(R.color.black_shade));
        } else if (missedRunVal > 0) {
          holder.process.setVisibility(View.VISIBLE);
          holder.process.setText(R.string.missedrun);
          bgShape.setColor(context.getResources().getColor(R.color.red));
        } else {
          // completed
          holder.process.setVisibility(View.VISIBLE);
          holder.process.setText(R.string.completed2);
          bgShape.setColor(context.getResources().getColor(R.color.bullet_green_color));
        }
      }

      holder.surveyTitle.setText(items.get(holder.getAdapterPosition()).getTitle());
      if (items.get(holder.getAdapterPosition()).getFrequency().getType().equalsIgnoreCase("Manually Schedule")) {
        holder.whenWasSurvey.setText(context.getResources().getString(R.string.as_scheduled));
      } else if (items.get(holder.getAdapterPosition()).getFrequency().getType().equalsIgnoreCase("One time")) {
        holder.whenWasSurvey.setText(context.getResources().getString(R.string.onetime));
      } else {
        holder.whenWasSurvey.setText(items.get(holder.getAdapterPosition()).getFrequency().getType());
      }
      Date startDate = null;
      Date endDate = null;
      SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
      SimpleDateFormat simpleDateFormatForActivityList =
              AppController.getDateFormatForActivityList();
      SimpleDateFormat simpleDateFormatForActivityListMonthly =
              AppController.getDateFormatForActivityListMonthly();
      SimpleDateFormat simpleDateFormatForOtherFreq = AppController.getDateFormatForOtherFreq();
      SimpleDateFormat simpleDateFormat5 = AppController.getDateFormatUtcNoZone();
      try {
        if (!items.get(holder.getAdapterPosition()).getStartTime().equalsIgnoreCase("")) {
          startDate = simpleDateFormat5.parse(items.get(holder.getAdapterPosition()).getStartTime().split("\\.")[0]);
        } else {
          startDate = new Date();
        }
        endDate = simpleDateFormat5.parse(items.get(holder.getAdapterPosition()).getEndTime().split("\\.")[0]);

      } catch (ParseException e) {
        Logger.log(e);
      }
      if (items
              .get(holder.getAdapterPosition())
              .getFrequency()
              .getType()
              .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_DAILY)) {
        try {
          String abc = "";
          if (!items.get(holder.getAdapterPosition()).getFrequency().getRuns().isEmpty()) {

            for (int i = 0; i < items.get(holder.getAdapterPosition()).getFrequency().getRuns().size(); i++) {

              try {
                String dateString =
                        items.get(holder.getAdapterPosition()).getFrequency().getRuns().get(i).getStartTime().toString();
                SimpleDateFormat sdf = AppController.getHourMinuteSecondFormat();
                Date date = sdf.parse(dateString);
                SimpleDateFormat dateFormat = AppController.getHourAmPmFormat1();
                String formattedDate = dateFormat.format(date).toString();
                if (i == 0) {
                  abc = formattedDate;
                } else {
                  abc = abc + "<font color=\"#8c95a3\"> | </font>" + formattedDate;
                }
              } catch (ParseException e) {
                Logger.log(e);
              }
            }
          }
          if (!abc.isEmpty()) {
            holder.time.setText(Html.fromHtml(abc) + " everyday", TextView.BufferType.SPANNABLE);
            holder.time.setVisibility(View.VISIBLE);
          }
          holder.date.setText(
                  simpleDateFormatForActivityList.format(startDate)
                          + " "
                          + context.getResources().getString(R.string.to)
                          + " "
                          + simpleDateFormatForActivityList.format(endDate));
        } catch (Exception e) {
          Logger.log(e);
        }
        holder.more.setVisibility(View.GONE);
      } else if (items
              .get(holder.getAdapterPosition())
              .getFrequency()
              .getType()
              .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MONTHLY)) {
        try {
          String dateString = items.get(holder.getAdapterPosition()).getStartTime().toString();
          Date date = simpleDateFormat5.parse(dateString.split("\\.")[0]);
          SimpleDateFormat dateFormat1 = AppController.getHourAmPmFormat1();
          String formattedDate1 = dateFormat1.format(date).toString();
          SimpleDateFormat dateFormat2 = AppController.getDdFormat();
          String formattedDate2 = dateFormat2.format(date).toString();
          String text =
                  formattedDate1
                          + " "
                          + context.getResources().getString(R.string.on)
                          + " day"
                          + " "
                          + formattedDate2
                          + TEXT_EVERY_MONTH;
          holder.time.setText(text);
          holder.time.setVisibility(View.VISIBLE);

          holder.date.setText(
                  simpleDateFormatForActivityListMonthly.format(startDate)
                          + " "
                          + context.getResources().getString(R.string.to)
                          + " "
                          + simpleDateFormatForActivityListMonthly.format(endDate));
        } catch (Exception e) {
          Logger.log(e);
        }
        holder.more.setVisibility(View.GONE);
      } else if (items
              .get(holder.getAdapterPosition())
              .getFrequency()
              .getType()
              .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WEEKLY)) {
        try {
          String dateString = items.get(holder.getAdapterPosition()).getStartTime().toString();
          Date date = simpleDateFormat5.parse(dateString.split("\\.")[0]);
          SimpleDateFormat dateFormat1 = AppController.getHourAmPmFormat1();
          String formattedDate1 = dateFormat1.format(date).toString();
          SimpleDateFormat dateFormat2 = AppController.getEeFormat();
          String formattedDate2 = dateFormat2.format(date).toString();
          String text = formattedDate1 + TEXT_EVERY + formattedDate2;
          holder.time.setText(text);
          holder.time.setVisibility(View.VISIBLE);

          holder.date.setText(
                  simpleDateFormatForActivityList.format(startDate)
                          + " "
                          + context.getResources().getString(R.string.to)
                          + " "
                          + simpleDateFormatForActivityList.format(endDate));
        } catch (Exception e) {
          Logger.log(e);
        }
        holder.more.setVisibility(View.GONE);
      } else if (items
              .get(holder.getAdapterPosition())
              .getFrequency()
              .getType()
              .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)) {
        try {
          holder.date.setText(getDateRange(items, endDate, holder.getAdapterPosition(), startDate, joiningDate, context, status));
        } catch (Exception e) {
          Logger.log(e);
        }
        holder.time.setVisibility(View.GONE);
        holder.more.setVisibility(View.GONE);
      } else if (items
              .get(holder.getAdapterPosition())
              .getFrequency()
              .getType()
              .equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
        try {

          /// Scheduled
          if (!items.get(holder.getAdapterPosition()).getFrequency().getRuns().isEmpty()) {
            int size = items.get(holder.getAdapterPosition()).getFrequency().getRuns().size();
            String startTime = "";
            String endTime = "";
            String finalTime;
            int finalpos = 0;
            int pos = -1;
            for (int i = 0; i < size; i++) {
              if (!items
                      .get(holder.getAdapterPosition())
                      .getFrequency()
                      .getRuns()
                      .get(i)
                      .getStartTime()
                      .toString()
                      .isEmpty()) {
                startTime =
                        getDateFormatedString(
                                items
                                        .get(holder.getAdapterPosition())
                                        .getFrequency()
                                        .getRuns()
                                        .get(i)
                                        .getStartTime()
                                        .toString()
                                        .split("\\.")[0]);
              }
              if (!items
                      .get(holder.getAdapterPosition())
                      .getFrequency()
                      .getRuns()
                      .get(i)
                      .getEndTime()
                      .toString()
                      .isEmpty()) {
                endTime =
                        getDateFormatedString(
                                items
                                        .get(holder.getAdapterPosition())
                                        .getFrequency()
                                        .getRuns()
                                        .get(i)
                                        .getEndTime()
                                        .toString()
                                        .split("\\.")[0]);
              }
              if (i < items
                      .get(holder.getAdapterPosition())
                      .getFrequency()
                      .getRuns().size() - 1) {
                pos = checkCurrentTimeInBetweenDates(items
                        .get(holder.getAdapterPosition())
                        .getFrequency()
                        .getRuns()
                        .get(i)
                        .getStartTime()
                        .split("\\.")[0], items
                        .get(holder.getAdapterPosition())
                        .getFrequency()
                        .getRuns()
                        .get(i + 1)
                        .getStartTime()
                        .split("\\.")[0], i);
              } else {
                pos = checkCurrentTimeInBetweenDates(items
                        .get(holder.getAdapterPosition())
                        .getFrequency()
                        .getRuns()
                        .get(i)
                        .getStartTime()
                        .split("\\.")[0], items
                        .get(holder.getAdapterPosition())
                        .getFrequency()
                        .getRuns()
                        .get(i)
                        .getEndTime()
                        .split("\\.")[0], i);
              }

              finalTime = startTime + " to " + endTime;
              mScheduledTime.add(finalTime);

              if (status
                      .get(holder.getAdapterPosition())
                      .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_COMPLETED)) {
                try {
                  finalpos = i;
                  holder.date.setText(
                          simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(i)
                                                  .getStartTime()
                                                  .toString()
                                                  .split("\\.")[0]))
                                  + " to "
                                  + simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(i)
                                                  .getEndTime()
                                                  .toString()
                                                  .split("\\.")[0])));
                } catch (ParseException e) {
                  Logger.log(e);
                }
              } else {
                if (i == 0) {
                  finalpos = i;
                  // if only 0 then show
                  holder.date.setText(
                          simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(i)
                                                  .getStartTime()
                                                  .toString()
                                                  .split("\\.")[0]))
                                  + " to "
                                  + simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(i)
                                                  .getEndTime()
                                                  .toString()
                                                  .split("\\.")[0])));

                  if ((currentRunStatusForActivities
                          .get(holder.getAdapterPosition())
                          .getStatus()
                          .equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED) || currentRunStatusForActivities
                          .get(holder.getAdapterPosition())
                          .getStatus()
                          .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) && pos < items
                          .get(holder.getAdapterPosition())
                          .getFrequency()
                          .getRuns()
                          .size() && status
                          .get(holder.getAdapterPosition())
                          .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT) || currentRunStatusForActivities
                          .get(holder.getAdapterPosition()).getCurrentRunId() == 0) {
                    int modifiedPos;
                    if (pos + 1 < items.get(holder.getAdapterPosition()).getFrequency().getRuns().size()) {
                      modifiedPos = pos + 1;
                    } else {
                      modifiedPos = pos;
                    }
                    finalpos = modifiedPos;
                      holder.date.setText(
                              simpleDateFormatForOtherFreq.format(
                                      simpleDateFormat5.parse(
                                              items
                                                      .get(holder.getAdapterPosition())
                                                      .getFrequency()
                                                      .getRuns()
                                                      .get(modifiedPos)
                                                      .getStartTime()
                                                      .toString()
                                                      .split("\\.")[0]))
                                      + " to "
                                      + simpleDateFormatForOtherFreq.format(
                                      simpleDateFormat5.parse(
                                              items
                                                      .get(holder.getAdapterPosition())
                                                      .getFrequency()
                                                      .getRuns()
                                                      .get(modifiedPos)
                                                      .getEndTime()
                                                      .toString()
                                                      .split("\\.")[0])));

                  }
                }

                if (pos > 0) {
                  try {
                    final Date d1 =
                            simpleDateFormat5.parse(
                                    items
                                            .get(holder.getAdapterPosition())
                                            .getFrequency()
                                            .getRuns()
                                            .get(i)
                                            .getStartTime()
                                            .toString()
                                            .split("\\.")[0]);
                    final Date d2 =
                            simpleDateFormat5.parse(
                                    items
                                            .get(holder.getAdapterPosition())
                                            .getFrequency()
                                            .getRuns()
                                            .get(i)
                                            .getEndTime()
                                            .toString()
                                            .split("\\.")[0]);
                    finalpos = i;
                    holder.date.setText(
                            simpleDateFormatForOtherFreq.format(d1)
                                    + " to "
                                    + simpleDateFormatForOtherFreq.format(d2));

                    if ((currentRunStatusForActivities
                            .get(holder.getAdapterPosition())
                            .getStatus()
                            .equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED) || currentRunStatusForActivities
                            .get(holder.getAdapterPosition())
                            .getStatus()
                            .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) && pos < items
                            .get(holder.getAdapterPosition())
                            .getFrequency()
                            .getRuns()
                            .size() && status
                            .get(holder.getAdapterPosition())
                            .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT) || currentRunStatusForActivities
                            .get(holder.getAdapterPosition()).getCurrentRunId() == 0) {
                      int modifiedPos;
                      if (pos + 1 < items.get(holder.getAdapterPosition()).getFrequency().getRuns().size()) {
                        modifiedPos = pos + 1;
                      } else {
                        modifiedPos = pos;
                      }
                      finalpos = modifiedPos;
                      holder.date.setText(
                              simpleDateFormatForOtherFreq.format(
                                      simpleDateFormat5.parse(
                                              items
                                                      .get(holder.getAdapterPosition())
                                                      .getFrequency()
                                                      .getRuns()
                                                      .get(modifiedPos)
                                                      .getStartTime()
                                                      .toString()
                                                      .split("\\.")[0]))
                                      + " to "
                                      + simpleDateFormatForOtherFreq.format(
                                      simpleDateFormat5.parse(
                                              items
                                                      .get(holder.getAdapterPosition())
                                                      .getFrequency()
                                                      .getRuns()
                                                      .get(modifiedPos)
                                                      .getEndTime()
                                                      .toString()
                                                      .split("\\.")[0])));
                    }
                  } catch (Exception e) {
                    Logger.log(e);
                  }
                }
                if (status
                        .get(holder.getAdapterPosition())
                        .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
                  finalpos = pos;
                  holder.date.setText(
                          simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(pos)
                                                  .getStartTime()
                                                  .toString()
                                                  .split("\\.")[0]))
                                  + " to "
                                  + simpleDateFormatForOtherFreq.format(
                                  simpleDateFormat5.parse(
                                          items
                                                  .get(holder.getAdapterPosition())
                                                  .getFrequency()
                                                  .getRuns()
                                                  .get(pos)
                                                  .getEndTime()
                                                  .toString()
                                                  .split("\\.")[0])));
                }
              }
            }
            timePos.set(holder.getAdapterPosition(), finalpos);

            try {
              if (mScheduledTime.size() > 1) {
                int pickerSize = mScheduledTime.size() - 1;
                String val =
                        "<u>"
                                + "+"
                                + pickerSize
                                + " "
                                + context.getResources().getString(R.string.more)
                                + "</u>";
                holder.more.setText(Html.fromHtml(val));
                holder.more.setVisibility(View.VISIBLE);
              } else {
                holder.more.setVisibility(View.GONE);
              }
            } catch (Exception e) {
              Logger.log(e);
            }
          }
          holder.time.setVisibility(View.GONE);

        } catch (Exception e) {
          Logger.log(e);
        }
      }

      holder.container.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  context.getString(R.string.survey_activities_list));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              int currentRunVal =
                  currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId();
              int totalRunVal =
                  currentRunStatusForActivities.get(holder.getAdapterPosition()).getTotalRun();
              if (click) {
                click = false;
                new Handler()
                    .postDelayed(
                        new Runnable() {
                          @Override
                          public void run() {
                            click = true;
                          }
                        },
                        1500);
                if (paused) {
                  Toast.makeText(context, R.string.study_Joined_paused, Toast.LENGTH_SHORT).show();
                } else {
                  if (status
                          .get(holder.getAdapterPosition())
                          .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)
                      && (currentRunStatusForActivities
                              .get(holder.getAdapterPosition())
                              .getStatus()
                              .equalsIgnoreCase(SurveyActivitiesFragment.IN_PROGRESS)
                          || currentRunStatusForActivities
                              .get(holder.getAdapterPosition())
                              .getStatus()
                              .equalsIgnoreCase(SurveyActivitiesFragment.YET_To_START))) {
                    if (currentRunStatusForActivities
                        .get(holder.getAdapterPosition())
                        .isRunIdAvailable()) {
                      surveyActivitiesFragment.getActivityInfo(
                          items.get(holder.getAdapterPosition()).getActivityId(),
                          currentRunStatusForActivities
                              .get(holder.getAdapterPosition())
                              .getCurrentRunId(),
                          currentRunStatusForActivities
                              .get(holder.getAdapterPosition())
                              .getStatus(),
                          items.get(holder.getAdapterPosition()).getBranching(),
                          items.get(holder.getAdapterPosition()).getActivityVersion(),
                          currentRunStatusForActivities.get(holder.getAdapterPosition()),
                          items.get(holder.getAdapterPosition()));
                    } else {
                      Toast.makeText(
                              context,
                              context.getResources().getString(R.string.survey_message),
                              Toast.LENGTH_SHORT)
                          .show();
                    }
                  } else if (status
                      .get(holder.getAdapterPosition())
                      .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
                    Toast.makeText(context, R.string.upcoming_event, Toast.LENGTH_SHORT).show();
                  } else if (currentRunStatusForActivities
                      .get(holder.getAdapterPosition())
                      .getStatus()
                      .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)) {
                    if (currentRunVal != totalRunVal) {
                      Toast.makeText(context, R.string.incomple_event, Toast.LENGTH_SHORT).show();
                    }
                  } else {
                    if (currentRunVal != totalRunVal) {
                      Toast.makeText(context, R.string.completed_event, Toast.LENGTH_SHORT).show();
                    }
                  }
                }
              }
            }
          });
      holder.more.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  context.getString(R.string.survey_activities_list_more));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              int p = 0;
              try {
                p = timePos.get(holder.getAdapterPosition());
              } catch (Exception e) {
                Logger.log(e);
              }
              CustomActivitiesDailyDialogClass c =
                  new CustomActivitiesDailyDialogClass(
                      context,
                      mScheduledTime,
                      p,
                      false,
                      SurveyActivitiesListAdapter.this,
                      status.get(holder.getAdapterPosition()),
                      currentRunStatusForActivities.get(holder.getAdapterPosition()));
              c.show();
            }
          });

      if (status
              .get(holder.getAdapterPosition())
              .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)) {
        if (!items.get(holder.getAdapterPosition()).getFrequency().getType().equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)) {
          if (currentRunStatusForActivities
                  .get(holder.getAdapterPosition())
                  .getStatus()
                  .equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED) || currentRunStatusForActivities
                  .get(holder.getAdapterPosition())
                  .getStatus()
                  .equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE) || currentRunStatusForActivities
                  .get(holder.getAdapterPosition()).getCurrentRunId() == 0) {
            holder.container.setBackgroundColor(Color.parseColor("#c6ccd0"));
            holder.itemlayout.setBackgroundColor(Color.parseColor("#c6ccd0"));
            holder.box1.setAlpha(.5f);
            holder.box2.setAlpha(.5f);

            if (currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId() <= currentRunStatusForActivities.get(holder.getAdapterPosition()).getTotalRun()) {
              holder.run.setText(
                      context.getResources().getString(R.string.run)
                              + ": "
                              + (currentRunStatusForActivities.get(holder.getAdapterPosition()).getCurrentRunId() + 1)
                              + " of "
                              + currentRunStatusForActivities.get(holder.getAdapterPosition()).getTotalRun()
                              + ", "
                              + currentRunStatusForActivities.get(holder.getAdapterPosition()).getCompletedRun()
                              + " "
                              + context.getResources().getString(R.string.done2)
                              + ", "
                              + currentRunStatusForActivities.get(holder.getAdapterPosition()).getMissedRun()
                              + " "
                              + context.getResources().getString(R.string.missed));
              holder.process.setText(R.string.start);
              bgShape.setColor(context.getResources().getColor(R.color.colorPrimary));
            }
          } else {
            holder.container.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.itemlayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.box1.setAlpha(1f);
            holder.box2.setAlpha(1f);
          }
        } else {
          holder.container.setBackgroundColor(context.getResources().getColor(R.color.white));
          holder.itemlayout.setBackgroundColor(context.getResources().getColor(R.color.white));
          holder.box1.setAlpha(1f);
          holder.box2.setAlpha(1f);
        }
      } else {
        holder.container.setBackgroundColor(context.getResources().getColor(R.color.white));
        holder.itemlayout.setBackgroundColor(context.getResources().getColor(R.color.white));
        holder.box1.setAlpha(1f);
        holder.box2.setAlpha(1f);
      }
    }
  }

  private String getDateFormatedString(String startTime) {
    try {
      SimpleDateFormat sdf = AppController.getDateFormatUtcNoZone();
      Date date = sdf.parse(startTime);
      SimpleDateFormat dateFormat1 = AppController.getHourAmPmMonthDayYearFormat();
      String formattedDate = dateFormat1.format(date).toString();
      return formattedDate;
    } catch (ParseException e) {
      Logger.log(e);
      return "";
    }
  }

  private int checkCurrentTimeInBetweenDates(String date1, String date2, int i) {
    int pos = 0;
    try {
      if (!date1.isEmpty() && !date2.isEmpty()) {
        Date time1 = AppController.getDateFormatUtcNoZone().parse(date1);
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(time1);

        Date time2 = AppController.getDateFormatUtcNoZone().parse(date2);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(time2);

        Calendar current = Calendar.getInstance();
        Date x = current.getTime();
        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
          pos = i;
        }
      }
    } catch (ParseException e) {
      Logger.log(e);
    }
    return pos;
  }

  public String getDateRange(
          ArrayList<ActivitiesWS> items,
          Date endDate,
          int position,
          Date startDate,
          Date joiningDate,
          Context context,
          ArrayList<String> status) {
    SimpleDateFormat simpleDateFormatForOtherFreq = AppController.getDateFormatForOneTime();
    String date = "";
    if (endDate != null) {
      if (items.get(position).isStudyLifeTime() && items.get(position).isLaunchStudy()) {
        date = "";
      } else if (!items.get(position).isStudyLifeTime()) {
        if (!items.get(position).isLaunchStudy()) {
          if (items.get(position).getSchedulingType().equalsIgnoreCase("AnchorDate")
                  && items.get(position).getAnchorDate() != null
                  && items.get(position).getAnchorDate().getSourceType() != null
                  && items.get(position).getAnchorDate().getSourceType().equalsIgnoreCase("EnrollmentDate")
                  && items.get(position).getAnchorDate().getStart() == null
                  && items.get(position).getAnchorDate().getEnd() != null
                  && joiningDate.after(startDate)) {
            Calendar joiningCalendar = Calendar.getInstance();
            joiningCalendar.setTime(joiningDate);
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(startDate);
            startCalendar.set(Calendar.DAY_OF_MONTH, joiningCalendar.get(Calendar.DAY_OF_MONTH));
            startCalendar.set(Calendar.MONTH, joiningCalendar.get(Calendar.MONTH));
            startCalendar.set(Calendar.YEAR, joiningCalendar.get(Calendar.YEAR));

            date =
                    simpleDateFormatForOtherFreq.format(startCalendar.getTime())
                            + " - "
                            + simpleDateFormatForOtherFreq.format(endDate);
          } else {

            date =
                    simpleDateFormatForOtherFreq.format(startDate)
                            + " - "
                            + simpleDateFormatForOtherFreq.format(endDate);
          }
        } else {
          if (status.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)) {
            date =
                    context.getResources().getString(R.string.ends)
                            + " : "
                            + simpleDateFormatForOtherFreq.format(endDate);
          } else {
            date =
                    context.getResources().getString(R.string.ended)
                            + " : "
                            + simpleDateFormatForOtherFreq.format(endDate);
          }
        }
      } else {
        if (status.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
          date =
                  context.getResources().getString(R.string.starts)
                          + " : "
                          + simpleDateFormatForOtherFreq.format(startDate);
        } else {
          date = "";
        }
      }
    } else {
      if (status.get(position).equalsIgnoreCase(SurveyActivitiesFragment.STATUS_UPCOMING)) {
        date =
                context.getResources().getString(R.string.starts)
                        + " : "
                        + simpleDateFormatForOtherFreq.format(startDate);
      } else {
        date = "";
      }
    }
    return date;
  }
}
