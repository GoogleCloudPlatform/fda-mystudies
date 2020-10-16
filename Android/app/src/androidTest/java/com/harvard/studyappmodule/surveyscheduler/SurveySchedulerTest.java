/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.AnchorRuns;
import com.harvard.studyappmodule.activitylistmodel.Frequency;
import com.harvard.studyappmodule.activitylistmodel.FrequencyRuns;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDate;
import com.harvard.utils.AppController;
import io.realm.RealmList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

  @Test
  public void getTotalRunsForUpcomingActivitiesTest() {
    SimpleDateFormat simpleDateFormat5 = AppController.getDateFormatUtcNoZone();
    Date startDate = null;
    Date endDate = null;
    try {
      startDate = simpleDateFormat5.parse(TEST_ACTIVITYESWS_START_TIME.split(TEST_REGEX)[0]);
      endDate = simpleDateFormat5.parse(TEST_ACTIVITYESWS_END_TIME.split(TEST_REGEX)[0]);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    SurveyScheduler surveyScheduler = new SurveyScheduler(null, null);
    int oneTimeValue =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_ONE_TIME),
            startDate,
            endDate,
            simpleDateFormat5);
    assertThat(oneTimeValue, equalTo(ONETIMEVALUE_CHECK));
    int withinADay =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_WITHIN_A_DAY),
            startDate,
            endDate,
            simpleDateFormat5);
    assertThat(withinADay, equalTo(WITHINADAY_CHECK));
    int weekly =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_WEEKLY), startDate, endDate, simpleDateFormat5);
    assertThat(weekly, equalTo(WEEKLY_CHECK));
    int monthly =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_MONTHLY),
            startDate,
            endDate,
            simpleDateFormat5);
    assertThat(monthly, equalTo(MONTHLY_CHECK));
    int mannualSchedule =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_MANUALLY_SCHEDULE),
            startDate,
            endDate,
            simpleDateFormat5);
    assertThat(mannualSchedule, equalTo(MANNUALSCHEDULE_CHECK));
    int daily =
        surveyScheduler.getTotalRunsForUpcomingActivities(
            getactivitieswsdata(TEST_FREQUENCY_TYPE_DAILY), startDate, endDate, simpleDateFormat5);
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
}
