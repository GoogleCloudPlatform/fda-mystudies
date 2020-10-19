/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import android.support.test.InstrumentationRegistry;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.AnchorRuns;
import com.harvard.studyappmodule.activitylistmodel.Frequency;
import com.harvard.studyappmodule.activitylistmodel.FrequencyRuns;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDate;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDateEnd;
import com.harvard.utils.AppController;
import io.realm.RealmList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.junit.Test;

public class SurveyActivitiesListAdapterTest {
  private static final String TEST_FETALKICK = "FETALKICK_TEST";
  private static final String TEST_END_TIME = "12:11:59";
  private static final String TEST_START_TIME = "12:10:00";
  private static final String TEST_TIME = "19:44:00";
  private static final String TEST_FREQUENCY_TYPE = "Daily";
  private static final String TEST_ACTIVITY_VERSION = "1.0";
  private static final String TEST_ACTIVITYESWS_START_TIME = "2020-10-09T12:10:00.000+0000";
  private static final String TEST_ACTIVITYESWS_JOIN_TIME = "2020-10-08T12:10:00.000+0000";
  private static final String TEST_ACTIVITYESWS_END_TIME = "2020-10-12T23:59:59.000+0000";
  private static final String TEST_ACTIVITYESWS_TYPE = "task";
  private static final String TEST_STATE = "active";
  private static final String TEST_SCHEDULE_TYPE = "AnchorDate";
  private static final String TEST_BLANK = "";
  private static final String TEST_SOURCE_TYPE = "EnrollmentDate";
  private static final int TEST_ANCHOR_DAYS = 2;
  private static final int TEST_REPEAT_NTERVAL = 4;
  private static final int TEST_START_DAYS = 10;
  private static final int TEST_END_DAYS = 1;
  private static final boolean TEST_BRANCHING = false;
  private static final int TEST_POSITION = 0;
  private static final String TEST_REGEX = "\\.";
  private static final String TEST_RESULT_ONE = "05:40PM, Oct 09, 2020 to 11:59PM, Oct 12, 2020";
  private static final String TEST_RESULT_TWO = "12:10PM, Oct 09, 2020 to 11:59PM, Oct 12, 2020";
  private static final String TEST_RESULT_THREE = "From : 12:10PM, Oct 09, 2020";
  private static final String TEST_EXCEPTION_MESSAGE = "Invalid date format";

  @Test
  public void getDatesAdapterTest() {
    ArrayList<ActivitiesWS> activitiesws = new ArrayList<>();
    Date startDate = null;
    Date endDate = null;
    Date joiningdate = null;
    activitiesws.add(getactivitieswsdata());
    SimpleDateFormat simpleDateFormat5 = AppController.getDateFormatUtcNoZone();
    try {
      startDate = simpleDateFormat5.parse(TEST_ACTIVITYESWS_START_TIME.split(TEST_REGEX)[0]);
      endDate = simpleDateFormat5.parse(TEST_ACTIVITYESWS_END_TIME.split(TEST_REGEX)[0]);
      joiningdate = AppController.getDateFormatForApi().parse(TEST_ACTIVITYESWS_JOIN_TIME);
    } catch (ParseException e) {
      fail(TEST_EXCEPTION_MESSAGE);
    }
    SurveyActivitiesListAdapter surveyActivitiesListAdapter =
        new SurveyActivitiesListAdapter(null, null, null, null, null, false, null);
    String conditionOne =
        surveyActivitiesListAdapter.getDateRange(
            activitiesws,
            endDate,
            TEST_POSITION,
            joiningdate,
            startDate,
            InstrumentationRegistry.getTargetContext());
    assertThat(conditionOne, equalTo(TEST_RESULT_ONE));
    String conditionTwo =
        surveyActivitiesListAdapter.getDateRange(
            activitiesws,
            endDate,
            TEST_POSITION,
            startDate,
            joiningdate,
            InstrumentationRegistry.getTargetContext());
    assertThat(conditionTwo, equalTo(TEST_RESULT_TWO));
    String conditionThree =
        surveyActivitiesListAdapter.getDateRange(
            activitiesws,
            null,
            TEST_POSITION,
            startDate,
            joiningdate,
            InstrumentationRegistry.getTargetContext());
    assertThat(conditionThree, equalTo(TEST_RESULT_THREE));
  }

  private ActivitiesWS getactivitieswsdata() {
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
    frequency.setType(TEST_FREQUENCY_TYPE);
    SchedulingAnchorDate schedulingAnchorDate = new SchedulingAnchorDate();
    schedulingAnchorDate.setSourceType(TEST_SOURCE_TYPE);
    schedulingAnchorDate.setSourceActivityId(TEST_FETALKICK);
    schedulingAnchorDate.setSourceKey(TEST_BLANK);
    schedulingAnchorDate.setSourceFormKey(TEST_BLANK);
    schedulingAnchorDate.setStart(null);
    SchedulingAnchorDateEnd schedulingAnchorDateEnd = new SchedulingAnchorDateEnd();
    schedulingAnchorDateEnd.setAnchorDays(TEST_ANCHOR_DAYS);
    schedulingAnchorDateEnd.setRepeatInterval(TEST_REPEAT_NTERVAL);
    schedulingAnchorDateEnd.setTime(TEST_TIME);
    schedulingAnchorDate.setEnd(schedulingAnchorDateEnd);
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
