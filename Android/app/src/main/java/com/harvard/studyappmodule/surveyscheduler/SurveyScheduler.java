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

package com.harvard.studyappmodule.surveyscheduler;

import android.content.Context;
import com.harvard.R;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.SurveyActivitiesFragment;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.surveyscheduler.model.ActivityStatus;
import com.harvard.studyappmodule.surveyscheduler.model.CompletionAdherence;
import com.harvard.usermodule.webservicemodel.Activities;
import com.harvard.usermodule.webservicemodel.ActivityData;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SurveyScheduler {

  public static final String FREQUENCY_TYPE_ONE_TIME = "One Time";
  public static final String FREQUENCY_TYPE_WITHIN_A_DAY = "Within a day";
  public static final String FREQUENCY_TYPE_DAILY = "Daily";
  public static final String FREQUENCY_TYPE_WEEKLY = "Weekly";
  public static final String FREQUENCY_TYPE_MONTHLY = "Monthly";
  public static final String FREQUENCY_TYPE_MANUALLY_SCHEDULE = "Manually schedule";
  private Date startTime;
  private Date endTime;
  private Date joiningTime;
  private String studyId;
  private Context context;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;

  public SurveyScheduler(DbServiceSubscriber dbServiceSubscriber, Realm realm) {
    this.dbServiceSubscriber = dbServiceSubscriber;
    this.realm = realm;
  }

  public Date getJoiningDateOfStudy(StudyData userPreferences, String studyId) {
    Date joiningTime = null;
    for (int i = 0; i < userPreferences.getStudies().size(); i++) {
      if (userPreferences.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
        try {
          joiningTime =
              AppController.getDateFormatForApi()
                  .parse(userPreferences.getStudies().get(i).getEnrolledDate());
        } catch (ParseException e) {
          Logger.log(e);
        }
        break;
      }
    }
    return joiningTime;
  }

  public int getOffset(Context context) {
    Calendar calendarCurrent = Calendar.getInstance();
    TimeZone currentTimeZone = TimeZone.getDefault();
    int currentOffset = currentTimeZone.getOffset(calendarCurrent.getTimeInMillis());
    if (AppController.getHelperSharedPreference()
        .readPreference(context, context.getResources().getString(R.string.startOffset), "")
        .equalsIgnoreCase("")) {
      AppController.getHelperSharedPreference()
          .writePreference(
              context,
              context.getResources().getString(R.string.startOffset),
              "" + currentTimeZone.getOffset(calendarCurrent.getTimeInMillis()));
    }

    return Integer.parseInt(
            AppController.getHelperSharedPreference()
                .readPreference(
                    context, context.getResources().getString(R.string.startOffset), ""))
        - currentOffset;
  }

  public void setRuns(
      ActivitiesWS activity,
      String studyId,
      Date startTime,
      Date endTime,
      Date joiningTime,
      Context context) {
    this.startTime = startTime;
    this.endTime = endTime;
    this.studyId = studyId;
    this.joiningTime = joiningTime;
    this.context = context;
    int offset = getOffset(context);

    if (endTime != null && joiningTime.after(endTime)) {
    } else {
      if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_DAILY)) {
        setDailyRun(activity, offset);
      } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_WEEKLY)) {
        setWeeklyRun(activity, offset);
      } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_MONTHLY)) {
        setMonthlyRun(activity, offset);
      } else if (activity
          .getFrequency()
          .getType()
          .equalsIgnoreCase(FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
        setScheduledRun(activity, offset);
      } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_ONE_TIME)) {
        setOneTimeRun(activity, offset);
      }
    }
  }

  private Date appleyOffset(Date date, int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
    return calendar.getTime();
  }

  private Date removeOffset(Date date, int offset) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
    return calendar.getTime();
  }

  private void setDailyRun(ActivitiesWS activity, int offset) {
    if (startTime != null) {
      Calendar startCalendar = Calendar.getInstance();
      startCalendar.setTime(startTime);

      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endTime);

      int run = 1;
      while (startCalendar.before(endCalendar)) {
        Date startDate = startCalendar.getTime();
        startCalendar.add(Calendar.DATE, 1);
        for (int j = 0; j < activity.getFrequency().getRuns().size(); j++) {
          try {
            String date = AppController.getDateFormatForDailyRun().format(startDate);
            String startDateString =
                date + " " + activity.getFrequency().getRuns().get(j).getStartTime();
            Date startDateDate =
                AppController.getDateFormatForDailyRunStartAndEnd().parse(startDateString);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(startDateDate);
            Calendar calendarStartDate = Calendar.getInstance();
            calendarStartDate.setTimeInMillis(calendar1.getTimeInMillis());
            String endDateString =
                date + " " + activity.getFrequency().getRuns().get(j).getEndTime();

            Date endDateDate =
                AppController.getDateFormatForDailyRunStartAndEnd().parse(endDateString);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(endDateDate);
            Calendar calendarEndDate = Calendar.getInstance();
            calendarEndDate.setTimeInMillis(calendar2.getTimeInMillis());
            ActivityRun activityRun = null;
            if (joiningTime.after(calendarEndDate.getTime())) {
            } else if (joiningTime.after(calendarStartDate.getTime())) {
              if ((joiningTime.after(startTime) || joiningTime.equals(startTime))) {
                if ((calendarEndDate.getTime().before(endTime)
                    || calendarEndDate.getTime().equals(endTime))) {
                  activityRun =
                      getActivityRun(
                          activity.getActivityId(),
                          studyId,
                          false,
                          appleyOffset(joiningTime, offset),
                          appleyOffset(calendarEndDate.getTime(), offset),
                          run);
                } else {
                  if (joiningTime.before(endTime)) {
                    activityRun =
                        getActivityRun(
                            activity.getActivityId(),
                            studyId,
                            false,
                            appleyOffset(joiningTime, offset),
                            appleyOffset(endTime, offset),
                            run);
                  }
                }
              }
            } else {
              if ((calendarStartDate.getTime().after(startTime)
                  || calendarStartDate.getTime().equals(startTime))) {
                if ((calendarEndDate.getTime().before(endTime)
                    || calendarEndDate.getTime().equals(endTime))) {
                  activityRun =
                      getActivityRun(
                          activity.getActivityId(),
                          studyId,
                          false,
                          appleyOffset(calendarStartDate.getTime(), offset),
                          appleyOffset(calendarEndDate.getTime(), offset),
                          run);
                } else {
                  if (calendarStartDate.getTime().before(endTime)) {
                    activityRun =
                        getActivityRun(
                            activity.getActivityId(),
                            studyId,
                            false,
                            appleyOffset(calendarStartDate.getTime(), offset),
                            appleyOffset(endTime, offset),
                            run);
                  }
                }
              }
            }
            if (activityRun != null) {
              insertAndUpdateToDB(context, activityRun);
              NotificationModuleSubscriber notificationModuleSubscriber =
                  new NotificationModuleSubscriber(dbServiceSubscriber, realm);
              if (activity.getFrequency().getRuns().size() > 1) {
                if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
                  notificationModuleSubscriber.generateActivityLocalNotification(
                      activityRun, context, FREQUENCY_TYPE_WITHIN_A_DAY, offset);
                }
              } else {
                if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
                  notificationModuleSubscriber.generateActivityLocalNotification(
                      activityRun, context, FREQUENCY_TYPE_DAILY, offset);
                }
              }
              run++;
            }
          } catch (ParseException e) {
            Logger.log(e);
          }
        }
      }
    }
  }

  private void setOneTimeRun(ActivitiesWS activity, int offset) {
    if (startTime != null) {
      SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
      ActivityRun activityRun = null;
      Calendar calendarStart = Calendar.getInstance();
      try {
        Date endDate;

        if (!activity.getStartTime().equalsIgnoreCase("")) {
          calendarStart.setTime(simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]));
          calendarStart.setTimeInMillis(calendarStart.getTimeInMillis());
        }

        if (activity.getEndTime().equalsIgnoreCase("")) {
          Calendar calendar = Calendar.getInstance();
          calendar.add(Calendar.YEAR, 25);
          endDate = calendar.getTime();
        } else {
          endDate = simpleDateFormat.parse(activity.getEndTime().split("\\.")[0]);
        }

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.setTime(endDate);
        calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());

        if (!activity.getStartTime().equalsIgnoreCase("")) {
          if (simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]).before(endDate)) {
            activityRun =
                getActivityRun(
                    activity.getActivityId(),
                    studyId,
                    false,
                    appleyOffset(
                        simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]), offset),
                    appleyOffset(endDate, offset),
                    1);
          }
        } else {
          activityRun =
              getActivityRun(
                  activity.getActivityId(),
                  studyId,
                  false,
                  appleyOffset(calendarStart.getTime(), offset),
                  appleyOffset(endDate, offset),
                  1);
        }
      } catch (ParseException e) {
        Logger.log(e);
      }
      if (activityRun != null) {
        insertAndUpdateToDB(context, activityRun);
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
          notificationModuleSubscriber.generateActivityLocalNotification(
              activityRun, context, FREQUENCY_TYPE_ONE_TIME, offset);
        }
      }
    }
  }

  private void setMonthlyRun(ActivitiesWS activity, int offset) {
    if (startTime != null) {
      Calendar startCalendar = Calendar.getInstance();
      if (joiningTime.after(startTime)) {
        startCalendar.setTime(joiningTime);
      } else {
        startCalendar.setTime(startTime);
      }

      Calendar startTimeCalender = Calendar.getInstance();
      startTimeCalender.setTime(startTime);

      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endTime);

      int run = 1;
      while (startCalendar.before(endCalendar)) {
        Date startDate = startCalendar.getTime();
        Calendar startCalendarTime = Calendar.getInstance();
        startCalendarTime.setTime(startDate);
        startCalendarTime.setTimeInMillis(startCalendarTime.getTimeInMillis());
        startCalendar.set(Calendar.DAY_OF_MONTH, startTimeCalender.get(Calendar.DAY_OF_MONTH));
        startCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalender.get(Calendar.HOUR_OF_DAY));
        startCalendar.set(Calendar.MINUTE, startTimeCalender.get(Calendar.MINUTE));
        startCalendar.set(Calendar.SECOND, startTimeCalender.get(Calendar.SECOND));
        startCalendar.add(Calendar.MONTH, 1);
        Date endDate = new Date(startCalendar.getTimeInMillis() - 1000);
        if (endDate.after(endCalendar.getTime())) {
          endDate = endCalendar.getTime();
        }

        Calendar endCalendarTime = Calendar.getInstance();
        endCalendarTime.setTime(endDate);
        endCalendarTime.setTimeInMillis(endCalendarTime.getTimeInMillis());

        ActivityRun activityRun =
            getActivityRun(
                activity.getActivityId(),
                studyId,
                false,
                appleyOffset(startCalendarTime.getTime(), offset),
                appleyOffset(endCalendarTime.getTime(), offset),
                run);
        if (activityRun != null) {
          insertAndUpdateToDB(context, activityRun);
          NotificationModuleSubscriber notificationModuleSubscriber =
              new NotificationModuleSubscriber(dbServiceSubscriber, realm);
          if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
            notificationModuleSubscriber.generateActivityLocalNotification(
                activityRun, context, FREQUENCY_TYPE_MONTHLY, offset);
          }
          run++;
        }
      }
    }
  }

  private void setScheduledRun(ActivitiesWS activity, int offset) {

    if (startTime != null) {
      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endTime);

      int run = 1;
      for (int j = 0; j < activity.getFrequency().getRuns().size(); j++) {
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
        ActivityRun activityRun = null;
        try {
          if (joiningTime.after(
              simpleDateFormat.parse(
                  activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]))) {
          } else if (joiningTime.after(
              simpleDateFormat.parse(
                  activity.getFrequency().getRuns().get(j).getStartTime().split("\\.")[0]))) {
            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(
                simpleDateFormat.parse(
                    activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]));
            calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());
            if (joiningTime.before(calendarEnd.getTime())) {
              if (joiningTime.before(endCalendar.getTime())) {
                activityRun =
                    getActivityRun(
                        activity.getActivityId(),
                        studyId,
                        false,
                        appleyOffset(joiningTime, offset),
                        appleyOffset(calendarEnd.getTime(), offset),
                        run);
              }
            }
          } else {
            Calendar calendarStart = Calendar.getInstance();
            calendarStart.setTime(
                simpleDateFormat.parse(
                    activity.getFrequency().getRuns().get(j).getStartTime().split("\\.")[0]));
            calendarStart.setTimeInMillis(calendarStart.getTimeInMillis());

            Calendar calendarEnd = Calendar.getInstance();
            calendarEnd.setTime(
                simpleDateFormat.parse(
                    activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]));
            calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());
            if (calendarStart.getTime().before(calendarEnd.getTime())) {
              if (appleyOffset(calendarStart.getTime(), offset)
                  .before(appleyOffset(endCalendar.getTime(), offset))) {
                activityRun =
                    getActivityRun(
                        activity.getActivityId(),
                        studyId,
                        false,
                        appleyOffset(calendarStart.getTime(), offset),
                        appleyOffset(calendarEnd.getTime(), offset),
                        run);
              }
            }
          }
        } catch (ParseException e) {
          Logger.log(e);
        }
        if (activityRun != null) {
          insertAndUpdateToDB(context, activityRun);
          NotificationModuleSubscriber notificationModuleSubscriber =
              new NotificationModuleSubscriber(dbServiceSubscriber, realm);
          if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
            notificationModuleSubscriber.generateActivityLocalNotification(
                activityRun, context, FREQUENCY_TYPE_MANUALLY_SCHEDULE, offset);
          }

          run++;
        }
      }
    }
  }

  private void setWeeklyRun(ActivitiesWS activity, int offset) {
    if (startTime != null) {
      Calendar startCalendar = Calendar.getInstance();
      if (joiningTime.after(startTime)) {
        startCalendar.setTime(joiningTime);
      } else {
        startCalendar.setTime(startTime);
      }
      Calendar startTimeCalender = Calendar.getInstance();
      startTimeCalender.setTime(startTime);

      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endTime);

      int run = 1;
      while (startCalendar.before(endCalendar)) {
        Date startDate = startCalendar.getTime();
        Calendar startCalenderTime = Calendar.getInstance();
        startCalenderTime.setTime(startDate);
        startCalenderTime.setTimeInMillis(startCalenderTime.getTimeInMillis());

        if (startTimeCalender.get(Calendar.DAY_OF_WEEK) < startCalendar.get(Calendar.DAY_OF_WEEK)) {
          startCalendar.add(
              Calendar.DATE,
              -(startCalendar.get(Calendar.DAY_OF_WEEK)
                  - startTimeCalender.get(Calendar.DAY_OF_WEEK)));
        } else if (startTimeCalender.get(Calendar.DAY_OF_WEEK)
            > startCalendar.get(Calendar.DAY_OF_WEEK)) {
          startCalendar.add(
              Calendar.DATE,
              -7
                  + (startTimeCalender.get(Calendar.DAY_OF_WEEK)
                      - startCalendar.get(Calendar.DAY_OF_WEEK)));
        }
        startCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalender.get(Calendar.HOUR_OF_DAY));
        startCalendar.set(Calendar.MINUTE, startTimeCalender.get(Calendar.MINUTE));
        startCalendar.set(Calendar.SECOND, startTimeCalender.get(Calendar.SECOND));
        startCalendar.add(Calendar.DATE, 7);
        Date endDate = new Date(startCalendar.getTimeInMillis() - 1000);
        if (endDate.after(endCalendar.getTime())) {
          endDate = endCalendar.getTime();
        }
        Calendar endCalenderTime = Calendar.getInstance();
        endCalenderTime.setTime(endDate);
        endCalenderTime.setTimeInMillis(endCalenderTime.getTimeInMillis());

        ActivityRun activityRun =
            getActivityRun(
                activity.getActivityId(),
                studyId,
                false,
                appleyOffset(startCalenderTime.getTime(), offset),
                appleyOffset(endCalenderTime.getTime(), offset),
                run);
        if (activityRun != null) {
          insertAndUpdateToDB(context, activityRun);
          NotificationModuleSubscriber notificationModuleSubscriber =
              new NotificationModuleSubscriber(dbServiceSubscriber, realm);
          if (!removeOffset(activityRun.getEndDate(), offset).before(new Date())) {
            notificationModuleSubscriber.generateActivityLocalNotification(
                activityRun, context, FREQUENCY_TYPE_WEEKLY, offset);
          }
          run++;
        }
      }
    }
  }

  /** get activity run for insert. */
  private ActivityRun getActivityRun(
      String activityId,
      String studyId,
      boolean isCompleted,
      Date startDate,
      Date endDate,
      int runId) {
    ActivityRun activityRun = new ActivityRun();
    activityRun.setActivityId(activityId);
    activityRun.setStudyId(studyId);
    activityRun.setCompleted(isCompleted);
    activityRun.setStartDate(startDate);
    activityRun.setEndDate(endDate);
    activityRun.setRunId(runId);
    return activityRun;
  }

  private <E> void insertAndUpdateToDB(Context context, E e) {
    DatabaseEvent databaseEvent = new DatabaseEvent();
    databaseEvent.setE(e);
    databaseEvent.setType(DbServiceSubscriber.TYPE_COPY);
    databaseEvent.setaClass(ActivityRun.class);
    databaseEvent.setOperation(DbServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
    dbServiceSubscriber.insert(context, databaseEvent);
  }

  // if currentRunId = 0 then no need to show the current run in UI
  public ActivityStatus getActivityStatus(
      ActivityData activityData,
      String studyId,
      String activityId,
      Date currentDate,
      ActivitiesWS activityListItem) {
    String activityStatus = SurveyActivitiesFragment.YET_To_START;
    int currentRunId = 0;

    RealmResults<ActivityRun> activityRuns =
        dbServiceSubscriber.getAllActivityRunFromDB(studyId, activityId, realm);
    activityRuns = activityRuns.sort("runId", Sort.ASCENDING);

    int missedRun = 0;
    int completedRun = 0;
    Date currentRunStartDate = null;
    Date currentRunEndDate = null;
    boolean runAvailable = false;
    Activities activitiesForStatus = null;
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();

    ActivityRun activityRun = null;
    ActivityRun activityPreviousRun = null;
    boolean previousRun = true;
    for (int i = 0; i < activityRuns.size(); i++) {
      Date activityRunStDate = null;
      Date activityRunEndDate = null;
      try {
        activityRunStDate =
            simpleDateFormat.parse(simpleDateFormat.format(activityRuns.get(i).getStartDate()));
        activityRunEndDate =
            simpleDateFormat.parse(simpleDateFormat.format(activityRuns.get(i).getEndDate()));
      } catch (ParseException e) {
        Logger.log(e);
      }

      if ((currentDate.equals(activityRunStDate) || currentDate.after(activityRunStDate))
          && (currentDate.equals(activityRunEndDate) || currentDate.before(activityRunEndDate))) {
        activityRun = activityRuns.get(i);
      } else if (currentDate.after(activityRunStDate)) {
        activityPreviousRun = activityRuns.get(i);
        previousRun = false;
      }
    }

    if (activityRun != null) {
      runAvailable = true;
      currentRunId = activityRun.getRunId();
      currentRunStartDate = activityRun.getStartDate();
      currentRunEndDate = activityRun.getEndDate();
    } else {
      if (activityPreviousRun != null) {
        currentRunId = activityPreviousRun.getRunId();
        currentRunStartDate = activityPreviousRun.getStartDate();
        currentRunEndDate = activityPreviousRun.getEndDate();
      }
    }
    boolean activityIdAvailable = false;
    if (activityData.getActivities() != null) {
      for (int i = 0; i < activityData.getActivities().size(); i++) {
        if (activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
          activitiesForStatus = activityData.getActivities().get(i);
          if (activitiesForStatus.getActivityRunId() != null
              && !activitiesForStatus.getActivityRunId().equalsIgnoreCase("")) {
            activityIdAvailable = true;
          }
        }
      }
    }

    if (runAvailable && activityIdAvailable) {
      if (currentRunId == Integer.parseInt(activitiesForStatus.getActivityRunId())) {
        activityStatus = activitiesForStatus.getStatus();
      } else {
        activityStatus = SurveyActivitiesFragment.YET_To_START;
      }
    } else if (runAvailable) {
      activityStatus = SurveyActivitiesFragment.YET_To_START;
    } else if (activityIdAvailable) {
      if (currentRunId == Integer.parseInt(activitiesForStatus.getActivityRunId())) {
        if (activitiesForStatus.getStatus().equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
          activityStatus = activitiesForStatus.getStatus();
        } else {
          activityStatus = SurveyActivitiesFragment.INCOMPLETE;
        }
      } else {
        activityStatus = SurveyActivitiesFragment.INCOMPLETE;
      }
    } else {
      if (activityPreviousRun == null) {
        activityStatus = SurveyActivitiesFragment.YET_To_START;
      } else {
        activityStatus = SurveyActivitiesFragment.INCOMPLETE;
      }
    }

    Activities activities =
        dbServiceSubscriber.getActivityPreferenceBySurveyId(studyId, activityId, realm);
    int totalRun;
    SimpleDateFormat startTimeDateFormat = AppController.getDateFormatUtcNoZone();
    Date starttime = null;
    try {
      starttime = startTimeDateFormat.parse(activityListItem.getStartTime().split("\\.")[0]);
    } catch (ParseException e) {
      Logger.log(e);
    }
    Date endtime = null;
    try {
      if (activityListItem.getEndTime() != null
          && !activityListItem.getEndTime().equalsIgnoreCase("")) {
        endtime = startTimeDateFormat.parse(activityListItem.getEndTime().split("\\.")[0]);
      }
    } catch (ParseException e) {
      Logger.log(e);
    }
    if (starttime != null) {
      if (AppController.checkafter(starttime)) {
        missedRun = 0;
        completedRun = 0;
        totalRun =
            getTotalRunsForUpcomingActivities(
                activityListItem, starttime, endtime, startTimeDateFormat);
      } else if (AppController.isWithinRange(starttime, endtime)) {
        if (activities != null && activities.getActivityRun() != null) {
          completedRun = activities.getActivityRun().getCompleted();
        }
        if (currentRunId <= 0) {
          missedRun = 0;
          currentRunStartDate = new Date();
          currentRunEndDate = new Date();
        } else {
          missedRun = currentRunId - completedRun;
        }

        if (runAvailable && !activityStatus.equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
          missedRun--;
        }

        if (missedRun < 0) {
          missedRun = 0;
        }

        totalRun = activityRuns.size();
      } else {
        if (activities != null && activities.getActivityRun() != null) {
          completedRun = activities.getActivityRun().getCompleted();
        }
        totalRun = activityRuns.size();
        missedRun = totalRun - completedRun;
        if (missedRun < 0) {
          missedRun = 0;
        }
      }
    } else {
      completedRun = 0;
      currentRunId = 0;
      missedRun = 0;
      totalRun = 0;
      currentRunStartDate = new Date();
      currentRunEndDate = new Date();
      activityStatus = SurveyActivitiesFragment.YET_To_START;
    }
    ActivityStatus activityStatusData = new ActivityStatus();
    activityStatusData.setCompletedRun(completedRun);
    activityStatusData.setCurrentRunId(currentRunId);
    activityStatusData.setMissedRun(missedRun);
    activityStatusData.setCurrentRunStartDate(currentRunStartDate);
    activityStatusData.setCurrentRunEndDate(currentRunEndDate);
    activityStatusData.setStatus(activityStatus);
    activityStatusData.setTotalRun(totalRun);
    activityStatusData.setRunIdAvailable(runAvailable);
    return activityStatusData;
  }

  public int getTotalRunsForUpcomingActivities(
      ActivitiesWS activityListItem,
      Date starttime,
      Date endtime,
      SimpleDateFormat startTimeDateFormat) {
    if (activityListItem.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_DAILY)) {
      if (starttime != null) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(starttime);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endtime);
        int totalRun = 0;
        while (startCalendar.before(endCalendar)) {
          startCalendar.add(Calendar.DATE, 1);
          totalRun = totalRun + 1;
        }
        return totalRun * activityListItem.getFrequency().getRuns().size();
      }
    } else if (activityListItem.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_WEEKLY)) {
      Calendar startCalendar = Calendar.getInstance();
      startCalendar.setTime(starttime);

      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endtime);
      int totalRun = 0;
      while (startCalendar.before(endCalendar)) {
        startCalendar.add(Calendar.DATE, 7);
        totalRun = totalRun + 1;
      }
      return totalRun;
    } else if (activityListItem.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_MONTHLY)) {
      Calendar startCalendar = Calendar.getInstance();
      startCalendar.setTime(starttime);

      Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(endtime);
      int totalRun = 0;
      while (startCalendar.before(endCalendar)) {
        startCalendar.add(Calendar.MONTH, 1);
        totalRun = totalRun + 1;
      }
      return totalRun;
    } else if (activityListItem
        .getFrequency()
        .getType()
        .equalsIgnoreCase(FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
      return activityListItem.getFrequency().getRuns().size();
    } else if (activityListItem
        .getFrequency()
        .getType()
        .equalsIgnoreCase(FREQUENCY_TYPE_ONE_TIME)) {
      return 1;
    }
    return 0;
  }

  private Activities getActivitiesDb(ActivityData activityData, String activityId) {
    Activities activities = null;
    if (activityData != null && activityData.getActivities() != null) {
      for (int i = 0; i < activityData.getActivities().size(); i++) {
        if (activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
          return activityData.getActivities().get(i);
        }
      }
    }
    return null;
  }

  public CompletionAdherence completionAndAdherenceCalculation(String studyId, Context context) {
    double completion = 0;
    double adherence = 0;
    boolean activityListAvailable = false;

    int completed = 0;
    int missed = 0;
    int total = 0;

    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
    ActivityData activityData = dbServiceSubscriber.getActivityPreference(studyId, realm);
    ActivityListData activityListDataDB = dbServiceSubscriber.getActivities(studyId, realm);
    Date currentDate = new Date();

    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(calendarCurrentTime.getTimeInMillis() - getOffset(context));

    if (activityListDataDB != null) {
      activityListAvailable = true;
      for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {

        try {
          if (!activityListDataDB.getActivities().get(i).getState().equalsIgnoreCase("deleted")) {
            ActivityStatus activityStatus =
                getActivityStatus(
                    activityData,
                    studyId,
                    activityListDataDB.getActivities().get(i).getActivityId(),
                    calendarCurrentTime.getTime(),
                    activityListDataDB.getActivities().get(i));
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
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
    }

    if (total > 0) {
      completion = (((double) completed + (double) missed) / (double) total) * 100d;
    }

    if (((double) completed + (double) missed) != 0) {
      adherence = ((double) completed / ((double) completed + (double) missed)) * 100;
    }

    CompletionAdherence completionAdherenceCalc = new CompletionAdherence();
    completionAdherenceCalc.setAdherence(adherence);
    completionAdherenceCalc.setCompletion(completion);
    completionAdherenceCalc.setActivityAvailable(activityListAvailable);
    if (completed == 0 && missed == 0) {
      completionAdherenceCalc.setNoCompletedAndMissed(true);
    } else {
      completionAdherenceCalc.setNoCompletedAndMissed(false);
    }
    return completionAdherenceCalc;
  }

  private boolean checkafter(Date starttime) {
    return starttime.after(new Date());
  }
}
