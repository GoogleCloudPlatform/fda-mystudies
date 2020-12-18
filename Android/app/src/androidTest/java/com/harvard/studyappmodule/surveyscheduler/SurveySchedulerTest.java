/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.storagemodule.events.DatabaseEvent;
import com.harvard.studyappmodule.activitybuilder.model.ActivityRun;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.activitylistmodel.AnchorDate;
import com.harvard.studyappmodule.activitylistmodel.AnchorRuns;
import com.harvard.studyappmodule.activitylistmodel.Frequency;
import com.harvard.studyappmodule.activitylistmodel.FrequencyRuns;
import com.harvard.studyappmodule.activitylistmodel.QuestionInfo;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDate;
import com.harvard.studyappmodule.surveyscheduler.datamodel.DailyRuns;
import com.harvard.studyappmodule.surveyscheduler.datamodel.DailyWithinADayRuns;
import com.harvard.studyappmodule.surveyscheduler.datamodel.ManuallScheduleRuns;
import com.harvard.studyappmodule.surveyscheduler.datamodel.MonthlyRuns;
import com.harvard.studyappmodule.surveyscheduler.datamodel.OneTimeRuns;
import com.harvard.studyappmodule.surveyscheduler.datamodel.Runs;
import com.harvard.studyappmodule.surveyscheduler.datamodel.WeeklyRuns;
import com.harvard.studyappmodule.surveyscheduler.model.ActivityStatus;
import com.harvard.usermodule.FileReader;
import com.harvard.usermodule.webservicemodel.Activities;
import com.harvard.usermodule.webservicemodel.ActivityData;
import com.harvard.utils.AppController;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SurveySchedulerTest {
  private static final String TEST_FETALKICK = "FETALKICK_TEST";
  private static final String TEST_END_TIME = "12:11:59";
  private static final String TEST_START_TIME = "12:10:00";
  private static final String TEST_TIME = "19:44:00";
  private static final String TEST_ACTIVITY_VERSION = "1.0";
  private static final String TEST_ACTIVITYESWS_START_TIME = "2020-10-08T12:10:00.000+0000";
  private static final String TEST_ACTIVITYESWS_END_TIME = "2020-10-12T23:59:59.000+0000";
  private static final String TEST_ACTIVITYESWS_TYPE = "task";
  private static final String TEST_STATE = "active";
  private static final String TEST_SCHEDULE_TYPE = "Regular";
  private static final String TEST_BLANK = "";
  private static final String TEST_FREQUENCY_TYPE_ONE_TIME = "One Time";
  private static final String TEST_FREQUENCY_TYPE_WITHIN_A_DAY = "Within a day";
  private static final String TEST_FREQUENCY_TYPE_DAILY = "Daily";
  private static final String TEST_FREQUENCY_TYPE_WEEKLY = "Weekly";
  private static final String TEST_FREQUENCY_TYPE_MONTHLY = "Monthly";
  private static final String TEST_FREQUENCY_TYPE_MANUALLY_SCHEDULE = "Manually schedule";
  private static final String TEST_REGEX = "\\.";
  private static final int ONETIMEVALUE_CHECK = 1;
  private static final int WITHINADAY_CHECK = 0;
  private static final int WEEKLY_CHECK = 1;
  private static final int MONTHLY_CHECK = 1;
  private static final int MANNUALSCHEDULE_CHECK = 1;
  private static final int DAILY_CHECK = 5;
  private static final int TEST_END_DAYS = 1;
  private static final int TEST_START_DAYS = 10;
  private static final boolean TEST_BRANCHING = false;
  private static final String INVALID_DATE_FORMAT = "Invalid date format";
  private static final String SCHEDULE_RESPONSE = "schedulerresponse/";
  private static final String ACTIVITY_DATA_RESPONSE = "schedulerresponse/activitydataresponse/";
  private static final String RUNS_RESPONSE = "schedulerresponse/runs/";
  private static final String DAILY = "daily.json";
  private static final String DAILY_WITHINA_DAY = "dailywithinaday.json";
  private static final String MANUALLSCHEDULE = "manuallschedule.json";
  private static final String MONTHLY = "monthly.json";
  private static final String ONETIME = "onetime.json";
  private static final String WEEKLY = "weekly.json";
  private static final String ACTIVITY_IS_KEY = "activityId";
  private static final String STUDY_ID_KEY = "studyId";
  private static final String CURRENT_DATE = "2020-10-21T20:30:06.694+0530";
  private Realm realm;
  private DbServiceSubscriber dbServiceSubscriber;
  private Gson gson;
  private SimpleDateFormat simpleDateFormat = AppController.getDateFormatForApi();
  private static final int ONETIME_TEST_CURRENT_RUN_ID = 1;
  private static final String ONETIME_TEST_STATUS = "inProgress";
  private static final int ONETIME_TEST_COMPLETERUN = 1;
  private static final String ONETIME_TEST_CURRENT_RUN_END_TIME =
      "Sat Oct 31 23:59:59 GMT+05:30 2020";
  private static final String ONETIME_TEST_CURRENT_RUN_START_TIME =
      "Tue Oct 20 11:25:00 GMT+05:30 2020";
  private static final int ONETIME_TEST_MISSED = 0;
  private static final int ONETIME_TEST_TOTALRUN = 1;
  private static final boolean ONETIME_TEST_RUN_ID_AVALABLE = true;
  private static final int DAILY_TEST_CURRENT_RUN_ID = 2;
  private static final String DAILY_TEST_STATUS = "yetToJoin";
  private static final int DAILY_TEST_COMPLETERUN = 1;
  private static final String DAILY_TEST_CURRENT_RUN_END_TIME =
      "Wed Oct 21 23:59:59 GMT+05:30 2020";
  private static final String DAILY_TEST_CURRENT_RUN_START_TIME =
      "Wed Oct 21 11:25:00 GMT+05:30 2020";
  private static final int DAILY_TEST_MISSED = 0;
  private static final int DAILY_TEST_TOTALRUN = 3;
  private static final boolean DAILY_TEST_RUN_ID_AVALABLE = true;
  private static final int WEEKLY_TEST_CURRENT_RUN_ID = 1;
  private static final String WEEKLY_TEST_STATUS = "inProgress";
  private static final int WEEKLY_TEST_COMPLETERUN = 0;
  private static final String WEEKLY_TEST_CURRENT_RUN_END_TIME =
      "Tue Oct 27 11:24:59 GMT+05:30 2020";
  private static final String WEEKLY_TEST_CURRENT_RUN_START_TIME =
      "Tue Oct 20 11:40:17 GMT+05:30 2020";
  private static final int WEEKLY_TEST_MISSED = 0;
  private static final int WEEKLY_TEST_TOTALRUN = 5;
  private static final boolean WEEKLY_TEST_RUN_ID_AVALABLE = true;
  private static final int MONTHLY_TEST_CURRENT_RUN_ID = 1;
  private static final String MONTHLY_TEST_STATUS = "inProgress";
  private static final int MONTHLY_TEST_COMPLETERUN = 1;
  private static final String MONTHLY_TEST_CURRENT_RUN_END_TIME =
      "Fri Nov 20 11:29:59 GMT+05:30 2020";
  private static final String MONTHLY_TEST_CURRENT_RUN_START_TIME =
      "Tue Oct 20 11:40:17 GMT+05:30 2020";
  private static final int MONTHLY_TEST_MISSED = 0;
  private static final int MONTHLY_TEST_TOTALRUN = 8;
  private static final boolean MONTHLY_TEST_RUN_ID_AVALABLE = true;
  private static final int MANNUAL_SCHEDULE_TEST_CURRENT_RUN_ID = 1;
  private static final String MANNUAL_SCHEDULE_TEST_STATUS = "abandoned";
  private static final int MANNUAL_SCHEDULE_TEST_COMPLETERUN = 0;
  private static final String MANNUAL_SCHEDULE_TEST_CURRENT_RUN_END_TIME =
      "Wed Oct 21 11:30:00 GMT+05:30 2020";
  private static final String MANNUAL_SCHEDULE_TEST_CURRENT_RUN_START_TIME =
      "Tue Oct 20 11:40:17 GMT+05:30 2020";
  private static final int MANNUAL_SCHEDULE_TEST_MISSED = 1;
  private static final int MANNUAL_SCHEDULE_TEST_TOTALRUN = 2;
  private static final boolean MANNUAL_SCHEDULE_TEST_RUN_ID_AVALABLE = false;
  private static final int DAILY_WITHIN_DAY_SCHEDULE_TEST_CURRENT_RUN_ID = 3;
  private static final String DAILY_WITHIN_DAY_TEST_STATUS = "yetToJoin";
  private static final int DAILY_WITHIN_DAY_TEST_COMPLETERUN = 1;
  private static final String DAILY_WITHIN_DAY_TEST_CURRENT_RUN_END_TIME =
      "Wed Oct 21 21:27:59 GMT+05:30 2020";
  private static final String DAILY_WITHIN_DAY_TEST_CURRENT_RUN_START_TIME =
      "Wed Oct 21 11:25:00 GMT+05:30 2020";
  private static final int DAILY_WITHIN_DAY_TEST_MISSED = 1;
  private static final int DAILY_WITHIN_DAY_TEST_TOTALRUN = 6;
  private static final boolean DAILY_WITHIN_DAY_TEST_RUN_ID_AVALABLE = true;

  @Before
  public void setUp() {
    realm = AppController.getRealmobj(InstrumentationRegistry.getTargetContext());
    dbServiceSubscriber = new DbServiceSubscriber();
    gson = new Gson();
  }

  @Test
  public void testActivityStatusForOneTime() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(ONETIME);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(ONETIME);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(ONETIME);
    String schedulerresponseOnetime = FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseOnetime, ActivitiesWS.class);
    String activitydataresponseOnetime =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData = gson.fromJson(activitydataresponseOnetime, ActivityData.class);
    String runsOnetime = FileReader.readStringFromFile(run.toString());
    OneTimeRuns oneTimeRuns = gson.fromJson(runsOnetime, OneTimeRuns.class);
    for (int i = 0; i < oneTimeRuns.getOnetime().size(); i++) {
      Runs runs = oneTimeRuns.getOnetime().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(activityStatus.getCurrentRunId(), equalTo(ONETIME_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(ONETIME_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(ONETIME_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(ONETIME_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(ONETIME_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(ONETIME_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(ONETIME_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(ONETIME_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void testActivityStatusForDaily() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(DAILY);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(DAILY);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(DAILY);
    String schedulerresponseDaily = FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseDaily, ActivitiesWS.class);
    String activitydataresponseDaily =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData = gson.fromJson(activitydataresponseDaily, ActivityData.class);
    String runsDaily = FileReader.readStringFromFile(run.toString());
    DailyRuns dailyRuns = gson.fromJson(runsDaily, DailyRuns.class);
    for (int i = 0; i < dailyRuns.getDailyruns().size(); i++) {
      Runs runs = dailyRuns.getDailyruns().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(activityStatus.getCurrentRunId(), equalTo(DAILY_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(DAILY_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(DAILY_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(DAILY_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(DAILY_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(DAILY_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(DAILY_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(DAILY_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void testActivityStatusForWeekly() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(WEEKLY);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(WEEKLY);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(WEEKLY);
    String schedulerresponseWeekly = FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseWeekly, ActivitiesWS.class);
    String activitydataresponseWeekly =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData = gson.fromJson(activitydataresponseWeekly, ActivityData.class);
    String runsWeekly = FileReader.readStringFromFile(run.toString());
    WeeklyRuns weeklyRuns = gson.fromJson(runsWeekly, WeeklyRuns.class);
    for (int i = 0; i < weeklyRuns.getWeekly().size(); i++) {
      Runs runs = weeklyRuns.getWeekly().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(activityStatus.getCurrentRunId(), equalTo(WEEKLY_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(WEEKLY_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(WEEKLY_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(WEEKLY_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(WEEKLY_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(WEEKLY_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(WEEKLY_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(WEEKLY_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void testActivityStatusForMonthly() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(MONTHLY);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(MONTHLY);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(MONTHLY);
    String schedulerresponseMonthly = FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseMonthly, ActivitiesWS.class);
    String activitydataresponseMonthly =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData = gson.fromJson(activitydataresponseMonthly, ActivityData.class);
    String runsMonthly = FileReader.readStringFromFile(run.toString());
    MonthlyRuns monthlyRuns = gson.fromJson(runsMonthly, MonthlyRuns.class);
    for (int i = 0; i < monthlyRuns.getMonthly().size(); i++) {
      Runs runs = monthlyRuns.getMonthly().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(activityStatus.getCurrentRunId(), equalTo(MONTHLY_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(MONTHLY_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(MONTHLY_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(MONTHLY_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(MONTHLY_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(MONTHLY_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(MONTHLY_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(MONTHLY_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void testActivityStatusForManualSchedule() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(MANUALLSCHEDULE);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(MANUALLSCHEDULE);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(MANUALLSCHEDULE);
    String schedulerresponseManuallschedule =
        FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseManuallschedule, ActivitiesWS.class);
    String activitydataresponseManuallschedule =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData =
        gson.fromJson(activitydataresponseManuallschedule, ActivityData.class);
    String runsManuallschedule = FileReader.readStringFromFile(run.toString());
    ManuallScheduleRuns manuallScheduleRuns =
        gson.fromJson(runsManuallschedule, ManuallScheduleRuns.class);
    for (int i = 0; i < manuallScheduleRuns.getManuallschedule().size(); i++) {
      Runs runs = manuallScheduleRuns.getManuallschedule().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(activityStatus.getCurrentRunId(), equalTo(MANNUAL_SCHEDULE_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(MANNUAL_SCHEDULE_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(MANNUAL_SCHEDULE_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(MANNUAL_SCHEDULE_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(MANNUAL_SCHEDULE_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(MANNUAL_SCHEDULE_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(MANNUAL_SCHEDULE_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(MANNUAL_SCHEDULE_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void testActivityStatusForWithinDay() {
    Date currentDate = null;
    try {
      currentDate = AppController.getDateFormatForApi().parse(CURRENT_DATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    Calendar calendarCurrentTime = Calendar.getInstance();
    calendarCurrentTime.setTime(currentDate);
    calendarCurrentTime.setTimeInMillis(
        calendarCurrentTime.getTimeInMillis()
            - getOffset(InstrumentationRegistry.getTargetContext()));
    StringBuilder schedulerresponse = new StringBuilder();
    schedulerresponse.append(SCHEDULE_RESPONSE);
    schedulerresponse.append(DAILY_WITHINA_DAY);
    StringBuilder activitydataresponse = new StringBuilder();
    activitydataresponse.append(ACTIVITY_DATA_RESPONSE);
    activitydataresponse.append(DAILY_WITHINA_DAY);
    StringBuilder run = new StringBuilder();
    run.append(RUNS_RESPONSE);
    run.append(DAILY_WITHINA_DAY);
    String schedulerresponseDailywithinaday =
        FileReader.readStringFromFile(schedulerresponse.toString());
    ActivitiesWS activitiesWS = gson.fromJson(schedulerresponseDailywithinaday, ActivitiesWS.class);
    String activitydataresponseDailywithinaday =
        FileReader.readStringFromFile(activitydataresponse.toString());
    ActivityData activityData =
        gson.fromJson(activitydataresponseDailywithinaday, ActivityData.class);
    String runsDailywithinaday = FileReader.readStringFromFile(run.toString());
    DailyWithinADayRuns dailyWithinADayRuns =
        gson.fromJson(runsDailywithinaday, DailyWithinADayRuns.class);
    for (int i = 0; i < dailyWithinADayRuns.getDailywithinadayruns().size(); i++) {
      Runs runs = dailyWithinADayRuns.getDailywithinadayruns().get(i);
      ActivityRun activityRun = getActivityRun(runs);
      insertAndUpdateToDB(
          InstrumentationRegistry.getTargetContext().getApplicationContext(), activityRun);
    }
    ActivityListData activityListData =
        getActivityListData(activitiesWS, activityData.getStudyId());
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityData);
    realm.commitTransaction();
    ActivityStatus activityStatus =
        getactivityStatusResult(
            activityData,
            activityData.getStudyId(),
            activitiesWS.getActivityId(),
            calendarCurrentTime.getTime(),
            activitiesWS);
    assertThat(
        activityStatus.getCurrentRunId(), equalTo(DAILY_WITHIN_DAY_SCHEDULE_TEST_CURRENT_RUN_ID));
    assertThat(activityStatus.getStatus(), equalTo(DAILY_WITHIN_DAY_TEST_STATUS));
    assertThat(activityStatus.getCompletedRun(), equalTo(DAILY_WITHIN_DAY_TEST_COMPLETERUN));
    assertTrue(
        activityStatus
            .getCurrentRunEndDate()
            .toString()
            .toLowerCase()
            .contains(DAILY_WITHIN_DAY_TEST_CURRENT_RUN_END_TIME.toLowerCase()));
    assertTrue(
        activityStatus
            .getCurrentRunStartDate()
            .toString()
            .toLowerCase()
            .contains(DAILY_WITHIN_DAY_TEST_CURRENT_RUN_START_TIME.toLowerCase()));
    assertThat(activityStatus.getMissedRun(), equalTo(DAILY_WITHIN_DAY_TEST_MISSED));
    assertThat(activityStatus.getTotalRun(), equalTo(DAILY_WITHIN_DAY_TEST_TOTALRUN));
    assertThat(activityStatus.isRunIdAvailable(), equalTo(DAILY_WITHIN_DAY_TEST_RUN_ID_AVALABLE));
  }

  @Test
  public void getTotalRunsForUpcomingActivitiesTest() {
    SimpleDateFormat simpleDateFormat = AppController.getDateFormatUtcNoZone();
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = simpleDateFormat.parse(TEST_ACTIVITYESWS_START_TIME.split(TEST_REGEX)[0]);
      endDate = simpleDateFormat.parse(TEST_ACTIVITYESWS_END_TIME.split(TEST_REGEX)[0]);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    SurveyScheduler surveyScheduler = new SurveyScheduler(null, null);
    int oneTimeValue =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_ONE_TIME),
            startDate,
            endDate,
            simpleDateFormat);
    assertThat(oneTimeValue, equalTo(ONETIMEVALUE_CHECK));
    int withinADay =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_WITHIN_A_DAY),
            startDate,
            endDate,
            simpleDateFormat);
    assertThat(withinADay, equalTo(WITHINADAY_CHECK));
    int weekly =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_WEEKLY), startDate, endDate, simpleDateFormat);
    assertThat(weekly, equalTo(WEEKLY_CHECK));
    int monthly =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_MONTHLY), startDate, endDate, simpleDateFormat);
    assertThat(monthly, equalTo(MONTHLY_CHECK));
    int mannualSchedule =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_MANUALLY_SCHEDULE),
            startDate,
            endDate,
            simpleDateFormat);
    assertThat(mannualSchedule, equalTo(MANNUALSCHEDULE_CHECK));
    int daily =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_DAILY), startDate, endDate, simpleDateFormat);
    assertThat(daily, equalTo(DAILY_CHECK));
  }

  private ActivitiesWS getactivitieswsdata(String type) {
    FrequencyRuns frequencyRuns = new FrequencyRuns();
    frequencyRuns.setEndTime(TEST_END_TIME);
    frequencyRuns.setStartTime(TEST_START_TIME);
    AnchorRuns anchorRuns = new AnchorRuns();
    anchorRuns.setEndDays(TEST_END_DAYS);
    anchorRuns.setStartDays(TEST_START_DAYS);
    anchorRuns.setTime(TEST_TIME);
    RealmList<FrequencyRuns> runslist = new RealmList<>();
    runslist.add(frequencyRuns);
    RealmList<AnchorRuns> anchorRunslist = new RealmList<>();
    anchorRunslist.add(anchorRuns);
    Frequency frequency = new Frequency();
    frequency.setAnchorRuns(anchorRunslist);
    frequency.setRuns(runslist);
    switch (type) {
      case TEST_FREQUENCY_TYPE_ONE_TIME:
        frequency.setType(TEST_FREQUENCY_TYPE_ONE_TIME);
        break;
      case TEST_FREQUENCY_TYPE_WITHIN_A_DAY:
        frequency.setType(TEST_FREQUENCY_TYPE_WITHIN_A_DAY);
        break;
      case TEST_FREQUENCY_TYPE_DAILY:
        frequency.setType(TEST_FREQUENCY_TYPE_DAILY);
        break;
      case TEST_FREQUENCY_TYPE_WEEKLY:
        frequency.setType(TEST_FREQUENCY_TYPE_WEEKLY);
        break;
      case TEST_FREQUENCY_TYPE_MONTHLY:
        frequency.setType(TEST_FREQUENCY_TYPE_MONTHLY);
        break;
      case TEST_FREQUENCY_TYPE_MANUALLY_SCHEDULE:
        frequency.setType(TEST_FREQUENCY_TYPE_MANUALLY_SCHEDULE);
        break;
      default:
        frequency.setType(TEST_FREQUENCY_TYPE_DAILY);
        break;
    }
    SchedulingAnchorDate schedulingAnchorDate = new SchedulingAnchorDate();
    schedulingAnchorDate.setSourceType(TEST_BLANK);
    schedulingAnchorDate.setSourceActivityId(TEST_FETALKICK);
    schedulingAnchorDate.setSourceKey(TEST_BLANK);
    schedulingAnchorDate.setSourceFormKey(TEST_BLANK);
    ActivitiesWS activitiesws = new ActivitiesWS();
    activitiesws.setActivityId(TEST_FETALKICK);
    activitiesws.setActivityVersion(TEST_ACTIVITY_VERSION);
    activitiesws.setAnchorDate(schedulingAnchorDate);
    activitiesws.setBranching(TEST_BRANCHING);
    activitiesws.setEndTime(TEST_ACTIVITYESWS_END_TIME);
    activitiesws.setType(TEST_ACTIVITYESWS_TYPE);
    activitiesws.setStartTime(TEST_ACTIVITYESWS_START_TIME);
    activitiesws.setTitle(TEST_FETALKICK);
    activitiesws.setState(TEST_STATE);
    activitiesws.setStatus(TEST_BLANK);
    activitiesws.setSchedulingType(TEST_SCHEDULE_TYPE);
    activitiesws.setFrequency(frequency);
    return activitiesws;
  }

  private <E> void insertAndUpdateToDB(Context context, E e) {
    DatabaseEvent databaseEvent = new DatabaseEvent();
    databaseEvent.setE(e);
    databaseEvent.setType(DbServiceSubscriber.TYPE_COPY);
    databaseEvent.setaClass(ActivityRun.class);
    databaseEvent.setOperation(DbServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
    dbServiceSubscriber.insert(context, databaseEvent);
  }

  private ActivityRun getActivityRun(Runs runs) {
    Date enddate = null;
    Date startdate = null;
    try {
      enddate = simpleDateFormat.parse(runs.getEndDate());
      startdate = simpleDateFormat.parse(runs.getStartDate());
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    ActivityRun activityRun = new ActivityRun();
    activityRun.setActivityId(runs.getActivityId());
    activityRun.setCompleted(runs.isCompleted());
    activityRun.setEndDate(enddate);
    activityRun.setStartDate(startdate);
    activityRun.setRunId(runs.getRunId());
    activityRun.setStudyId(runs.getStudyId());
    return activityRun;
  }

  private int getOffset(Context context) {
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

  @After
  public void tearDown() {
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<ActivityRun> activityListDataRealmResults =
                realm.where(ActivityRun.class).equalTo(ACTIVITY_IS_KEY, TEST_FETALKICK).findAll();
            activityListDataRealmResults.deleteAllFromRealm();
            StringBuilder schedulerresponseweekly = new StringBuilder();
            schedulerresponseweekly.append(SCHEDULE_RESPONSE);
            schedulerresponseweekly.append(WEEKLY);
            StringBuilder activitydataresponseweekly = new StringBuilder();
            activitydataresponseweekly.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponseweekly.append(WEEKLY);
            StringBuilder runweekly = new StringBuilder();
            runweekly.append(RUNS_RESPONSE);
            runweekly.append(WEEKLY);
            String schedulerresponseWeekly =
                FileReader.readStringFromFile(schedulerresponseweekly.toString());
            ActivitiesWS activitieswsWeekly =
                gson.fromJson(schedulerresponseWeekly, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistweekly =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsWeekly.getActivityId())
                    .findAll();
            activitiesWSlistweekly.deleteAllFromRealm();
            RealmResults<Activities> activitieslistweekly =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsWeekly.getActivityId())
                    .findAll();
            activitieslistweekly.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfoweekly =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsWeekly.getActivityId())
                    .findAll();
            questioninfoweekly.deleteAllFromRealm();
            String activitydataresponseWeekly =
                FileReader.readStringFromFile(activitydataresponseweekly.toString());
            ActivityData activityData =
                gson.fromJson(activitydataresponseWeekly, ActivityData.class);
            RealmResults<ActivityData> activityDatalistweekly =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityData.getStudyId())
                    .findAll();
            activityDatalistweekly.deleteAllFromRealm();
            String runsWeekly = FileReader.readStringFromFile(runweekly.toString());
            WeeklyRuns weeklyRuns = gson.fromJson(runsWeekly, WeeklyRuns.class);
            for (int i = 0; i < weeklyRuns.getWeekly().size(); i++) {
              Runs runs = weeklyRuns.getWeekly().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
            StringBuilder schedulerresponseontime = new StringBuilder();
            schedulerresponseontime.append(SCHEDULE_RESPONSE);
            schedulerresponseontime.append(ONETIME);
            StringBuilder activitydataresponseontime = new StringBuilder();
            activitydataresponseontime.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponseontime.append(ONETIME);
            StringBuilder runontime = new StringBuilder();
            runontime.append(RUNS_RESPONSE);
            runontime.append(ONETIME);
            String schedulerresponseOnetime =
                FileReader.readStringFromFile(schedulerresponseontime.toString());
            ActivitiesWS activitieswsOnetime =
                gson.fromJson(schedulerresponseOnetime, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistonetime =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsOnetime.getActivityId())
                    .findAll();
            activitiesWSlistonetime.deleteAllFromRealm();
            RealmResults<Activities> activitieslistonetime =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsOnetime.getActivityId())
                    .findAll();
            activitieslistonetime.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfoonetime =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsOnetime.getActivityId())
                    .findAll();
            questioninfoonetime.deleteAllFromRealm();
            String activitydataresponseOnetime =
                FileReader.readStringFromFile(activitydataresponseontime.toString());
            ActivityData activityDataOnetime =
                gson.fromJson(activitydataresponseOnetime, ActivityData.class);
            RealmResults<ActivityData> activityDatalistonetime =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityDataOnetime.getStudyId())
                    .findAll();
            activityDatalistonetime.deleteAllFromRealm();
            String runsOnetime = FileReader.readStringFromFile(runontime.toString());
            OneTimeRuns oneTimeRuns = gson.fromJson(runsOnetime, OneTimeRuns.class);
            for (int i = 0; i < oneTimeRuns.getOnetime().size(); i++) {
              Runs runs = oneTimeRuns.getOnetime().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
            StringBuilder schedulerresponsedaily = new StringBuilder();
            schedulerresponsedaily.append(SCHEDULE_RESPONSE);
            schedulerresponsedaily.append(DAILY);
            StringBuilder activitydataresponsedaily = new StringBuilder();
            activitydataresponsedaily.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponsedaily.append(DAILY);
            StringBuilder rundaily = new StringBuilder();
            rundaily.append(RUNS_RESPONSE);
            rundaily.append(DAILY);
            String schedulerresponseDaily =
                FileReader.readStringFromFile(schedulerresponsedaily.toString());
            ActivitiesWS activitieswsDaily =
                gson.fromJson(schedulerresponseDaily, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistdaily =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDaily.getActivityId())
                    .findAll();
            activitiesWSlistdaily.deleteAllFromRealm();
            RealmResults<Activities> activitieslistdaily =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDaily.getActivityId())
                    .findAll();
            activitieslistdaily.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfodaily =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDaily.getActivityId())
                    .findAll();
            questioninfodaily.deleteAllFromRealm();
            String activitydataresponseDaily =
                FileReader.readStringFromFile(activitydataresponsedaily.toString());
            ActivityData activityDataDaily =
                gson.fromJson(activitydataresponseDaily, ActivityData.class);
            RealmResults<ActivityData> activityDatalistdaily =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityDataDaily.getStudyId())
                    .findAll();
            activityDatalistdaily.deleteAllFromRealm();
            String runsDaily = FileReader.readStringFromFile(rundaily.toString());
            DailyRuns dailyRuns = gson.fromJson(runsDaily, DailyRuns.class);
            for (int i = 0; i < dailyRuns.getDailyruns().size(); i++) {
              Runs runs = dailyRuns.getDailyruns().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
            StringBuilder schedulerresponsemonthly = new StringBuilder();
            schedulerresponsemonthly.append(SCHEDULE_RESPONSE);
            schedulerresponsemonthly.append(MONTHLY);
            StringBuilder activitydataresponsemonthly = new StringBuilder();
            activitydataresponsemonthly.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponsemonthly.append(MONTHLY);
            StringBuilder runmonthly = new StringBuilder();
            runmonthly.append(RUNS_RESPONSE);
            runmonthly.append(MONTHLY);
            String schedulerresponseMonthly =
                FileReader.readStringFromFile(schedulerresponsemonthly.toString());
            ActivitiesWS activitieswsMonthly =
                gson.fromJson(schedulerresponseMonthly, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistmonthly =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsMonthly.getActivityId())
                    .findAll();
            activitiesWSlistmonthly.deleteAllFromRealm();
            RealmResults<Activities> activitieslistmonthly =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsMonthly.getActivityId())
                    .findAll();
            activitieslistmonthly.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfomonthly =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsMonthly.getActivityId())
                    .findAll();
            questioninfomonthly.deleteAllFromRealm();
            String activitydataresponseMonthly =
                FileReader.readStringFromFile(activitydataresponsemonthly.toString());
            ActivityData activityDataMonthly =
                gson.fromJson(activitydataresponseMonthly, ActivityData.class);
            RealmResults<ActivityData> activityDatalistmonthly =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityDataMonthly.getStudyId())
                    .findAll();
            activityDatalistmonthly.deleteAllFromRealm();
            String runsMonthly = FileReader.readStringFromFile(runmonthly.toString());
            MonthlyRuns monthlyRuns = gson.fromJson(runsMonthly, MonthlyRuns.class);
            for (int i = 0; i < monthlyRuns.getMonthly().size(); i++) {
              Runs runs = monthlyRuns.getMonthly().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
            StringBuilder schedulerresponsemanuallschedule = new StringBuilder();
            schedulerresponsemanuallschedule.append(SCHEDULE_RESPONSE);
            schedulerresponsemanuallschedule.append(MANUALLSCHEDULE);
            StringBuilder activitydataresponsemanuallschedule = new StringBuilder();
            activitydataresponsemanuallschedule.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponsemanuallschedule.append(MANUALLSCHEDULE);
            StringBuilder runmanuallschedule = new StringBuilder();
            runmanuallschedule.append(RUNS_RESPONSE);
            runmanuallschedule.append(MANUALLSCHEDULE);
            String schedulerresponseManuallschedule =
                FileReader.readStringFromFile(schedulerresponsemanuallschedule.toString());
            ActivitiesWS activitieswsManuallschedule =
                gson.fromJson(schedulerresponseManuallschedule, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistmanuallschedule =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsManuallschedule.getActivityId())
                    .findAll();
            activitiesWSlistmanuallschedule.deleteAllFromRealm();
            RealmResults<Activities> activitieslistmanuallschedule =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsManuallschedule.getActivityId())
                    .findAll();
            activitieslistmanuallschedule.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfomanuallschedule =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsManuallschedule.getActivityId())
                    .findAll();
            questioninfomanuallschedule.deleteAllFromRealm();
            String activitydataresponseManuallschedule =
                FileReader.readStringFromFile(activitydataresponsemanuallschedule.toString());
            ActivityData activityDataManuallschedule =
                gson.fromJson(activitydataresponseManuallschedule, ActivityData.class);
            RealmResults<ActivityData> activityDatalistmanuallschedule =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityDataManuallschedule.getStudyId())
                    .findAll();
            activityDatalistmanuallschedule.deleteAllFromRealm();
            String runsManuallschedule =
                FileReader.readStringFromFile(runmanuallschedule.toString());
            ManuallScheduleRuns manuallScheduleRuns =
                gson.fromJson(runsManuallschedule, ManuallScheduleRuns.class);
            for (int i = 0; i < manuallScheduleRuns.getManuallschedule().size(); i++) {
              Runs runs = manuallScheduleRuns.getManuallschedule().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
            StringBuilder schedulerresponsewithin = new StringBuilder();
            schedulerresponsewithin.append(SCHEDULE_RESPONSE);
            schedulerresponsewithin.append(DAILY_WITHINA_DAY);
            StringBuilder activitydataresponsewithin = new StringBuilder();
            activitydataresponsewithin.append(ACTIVITY_DATA_RESPONSE);
            activitydataresponsewithin.append(DAILY_WITHINA_DAY);
            StringBuilder runwithin = new StringBuilder();
            runwithin.append(RUNS_RESPONSE);
            runwithin.append(DAILY_WITHINA_DAY);
            String schedulerresponseDailywithinaday =
                FileReader.readStringFromFile(schedulerresponsewithin.toString());
            ActivitiesWS activitieswsDailywithinaday =
                gson.fromJson(schedulerresponseDailywithinaday, ActivitiesWS.class);
            RealmResults<ActivitiesWS> activitiesWSlistdailywithinaday =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDailywithinaday.getActivityId())
                    .findAll();
            activitiesWSlistdailywithinaday.deleteAllFromRealm();
            RealmResults<Activities> activitieslistdailywithinaday =
                realm
                    .where(Activities.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDailywithinaday.getActivityId())
                    .findAll();
            activitieslistdailywithinaday.deleteAllFromRealm();
            RealmResults<QuestionInfo> questioninfodailywithinaday =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(ACTIVITY_IS_KEY, activitieswsDailywithinaday.getActivityId())
                    .findAll();
            questioninfodailywithinaday.deleteAllFromRealm();
            String activitydataresponseDailywithinaday =
                FileReader.readStringFromFile(activitydataresponsewithin.toString());
            ActivityData activityDataDailywithinaday =
                gson.fromJson(activitydataresponseDailywithinaday, ActivityData.class);
            RealmResults<ActivityData> activityDatalistdailywithinaday =
                realm
                    .where(ActivityData.class)
                    .equalTo(STUDY_ID_KEY, activityDataDailywithinaday.getStudyId())
                    .findAll();
            activityDatalistdailywithinaday.deleteAllFromRealm();
            String runsDailywithinaday = FileReader.readStringFromFile(runwithin.toString());
            DailyWithinADayRuns dailyWithinADayRuns =
                gson.fromJson(runsDailywithinaday, DailyWithinADayRuns.class);
            for (int i = 0; i < dailyWithinADayRuns.getDailywithinadayruns().size(); i++) {
              Runs runs = dailyWithinADayRuns.getDailywithinadayruns().get(i);
              ActivityRun activityRun = getActivityRun(runs);
              RealmResults<ActivityRun> activityRuns =
                  realm
                      .where(ActivityRun.class)
                      .equalTo(STUDY_ID_KEY, activityRun.getStudyId())
                      .findAll();
              activityRuns.deleteAllFromRealm();
            }
          }
        });
    dbServiceSubscriber.closeRealmObj(realm);
  }

  private ActivityStatus getactivityStatusResult(
      ActivityData activityData,
      String studyid,
      String activityid,
      Date currenttime,
      ActivitiesWS activitiesWS) {
    SurveyScheduler surveyScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
    ActivityStatus activityStatus =
        surveyScheduler.getActivityStatus(
            activityData, studyid, activityid, currenttime, activitiesWS);
    return activityStatus;
  }

  private ActivityListData getActivityListData(ActivitiesWS activitiesWS, String studyId) {
    RealmList<ActivitiesWS> activities = new RealmList<>();
    activities.add(activitiesWS);
    QuestionInfo questionInfo = new QuestionInfo();
    questionInfo.setActivityId(activitiesWS.getActivityId());
    questionInfo.setActivityVersion(activitiesWS.getActivityVersion());
    questionInfo.setKey(TEST_BLANK);
    AnchorDate anchorDate = new AnchorDate();
    anchorDate.setQuestionInfo(questionInfo);
    anchorDate.setType(activitiesWS.getAnchorDate().getSourceType());
    ActivityListData activityListData = new ActivityListData();
    activityListData.setActivities(activities);
    activityListData.setAnchorDate(anchorDate);
    activityListData.setMessage(TEST_BLANK);
    activityListData.setStudyId(studyId);
    activityListData.setWithdrawalConfig(TEST_BLANK);
    return activityListData;
  }
}
