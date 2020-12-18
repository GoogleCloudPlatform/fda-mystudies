/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.storagemodule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import android.support.test.InstrumentationRegistry;
import com.harvard.studyappmodule.activitylistmodel.ActivitiesWS;
import com.harvard.studyappmodule.activitylistmodel.ActivityListData;
import com.harvard.studyappmodule.activitylistmodel.AnchorDate;
import com.harvard.studyappmodule.activitylistmodel.AnchorRuns;
import com.harvard.studyappmodule.activitylistmodel.Frequency;
import com.harvard.studyappmodule.activitylistmodel.FrequencyRuns;
import com.harvard.studyappmodule.activitylistmodel.QuestionInfo;
import com.harvard.studyappmodule.activitylistmodel.SchedulingAnchorDate;
import com.harvard.utils.AppController;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DbServiceSubscriberTest {
  private Realm realm;
  private DbServiceSubscriber dbServiceSubscriber;
  private static final String TEST_FETALKICK = "FETALKICK_TEST";
  private static final String TEST_END_TIME = "12:11:59";
  private static final String TEST_START_TIME = "12:10:00";
  private static final String TEST_TIME = "19:44:00";
  private static final String TEST_FREQUENCY_TYPE = "Daily";
  private static final String TEST_ACTIVITY_VERSION = "1.0";
  private static final String TEST_ACTIVITYESWS_START_TIME = "2020-10-08T12:10:00.000+0000";
  private static final String TEST_ACTIVITYESWS_END_TIME = "2020-10-12T23:59:59.000+0000";
  private static final String TEST_ACTIVITYESWS_TYPE = "task";
  private static final String TEST_STATE = "active";
  private static final String TEST_SCHEDULE_TYPE = "Regular";
  private static final String TEST_MESSAGE = "test";
  private static final String TEST_ACTIVITY_ID_KEY = "activityId";
  private static final String TEST_STUDY_ID_KEY = "studyId";
  private static final String TEST_START_TIME_KEY = "startTime";
  private static final String TEST_TYPE_KEY = "type";
  private static final String TEST_SOURCEACTIVITYID_KEY = "sourceActivityId";
  private static final String TEST_BLANK = "";
  private static final String TEST_START_DAYS_KEY = "startDays";
  private static final int TEST_START_DAYS = 10;
  private static final int TEST_END_DAYS = 1;
  private static final boolean TEST_BRANCHING = false;

  @Before
  public void setUp() {
    realm = AppController.getRealmobj(InstrumentationRegistry.getTargetContext());
    dbServiceSubscriber = new DbServiceSubscriber();
  }

  @Test
  public void saveActivityStartAndEndTimeTest() {
    ActivityListData activityListData = getActivityListData();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(activityListData);
    realm.commitTransaction();
    dbServiceSubscriber.saveActivityStartTime(
        getactivitieswsdata(), realm, TEST_ACTIVITYESWS_START_TIME);
    dbServiceSubscriber.saveActivityEndTime(
        getactivitieswsdata(), realm, TEST_ACTIVITYESWS_END_TIME);
    RealmResults<ActivitiesWS> date =
        realm.where(ActivitiesWS.class).equalTo(TEST_ACTIVITY_ID_KEY, TEST_FETALKICK).findAll();
    assertThat(date.get(0).getStartTime(), equalTo(TEST_ACTIVITYESWS_START_TIME));
    assertThat(date.get(0).getEndTime(), equalTo(TEST_ACTIVITYESWS_END_TIME));
  }

  @After
  public void tearDown() {
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<ActivityListData> activityListDataRealmResults =
                realm
                    .where(ActivityListData.class)
                    .equalTo(TEST_STUDY_ID_KEY, TEST_FETALKICK)
                    .findAll();
            activityListDataRealmResults.deleteAllFromRealm();
            RealmResults<ActivitiesWS> activitieswsrealmresults =
                realm
                    .where(ActivitiesWS.class)
                    .equalTo(TEST_ACTIVITY_ID_KEY, TEST_FETALKICK)
                    .findAll();
            activitieswsrealmresults.deleteAllFromRealm();
            RealmResults<FrequencyRuns> frequencyRunsRealmResults =
                realm
                    .where(FrequencyRuns.class)
                    .equalTo(TEST_START_TIME_KEY, TEST_START_TIME)
                    .findAll();
            frequencyRunsRealmResults.deleteAllFromRealm();
            RealmResults<Frequency> frequencyRealmResults =
                realm.where(Frequency.class).equalTo(TEST_TYPE_KEY, TEST_FREQUENCY_TYPE).findAll();
            frequencyRealmResults.deleteAllFromRealm();
            RealmResults<SchedulingAnchorDate> schedulingAnchorDateRealmResults =
                realm
                    .where(SchedulingAnchorDate.class)
                    .equalTo(TEST_SOURCEACTIVITYID_KEY, TEST_FETALKICK)
                    .findAll();
            schedulingAnchorDateRealmResults.deleteAllFromRealm();
            RealmResults<QuestionInfo> questionInfoRealmResults =
                realm
                    .where(QuestionInfo.class)
                    .equalTo(TEST_ACTIVITY_ID_KEY, TEST_FETALKICK)
                    .findAll();
            questionInfoRealmResults.deleteAllFromRealm();
            RealmResults<AnchorDate> anchorDateRealmResults =
                realm.where(AnchorDate.class).equalTo(TEST_TYPE_KEY, TEST_FREQUENCY_TYPE).findAll();
            anchorDateRealmResults.deleteAllFromRealm();
            RealmResults<AnchorRuns> anchorRunsRealmResults =
                realm
                    .where(AnchorRuns.class)
                    .equalTo(TEST_START_DAYS_KEY, TEST_START_DAYS)
                    .findAll();
            anchorRunsRealmResults.deleteAllFromRealm();
          }
        });
    dbServiceSubscriber.closeRealmObj(realm);
  }

  private ActivityListData getActivityListData() {
    RealmList<ActivitiesWS> activities = new RealmList<>();
    activities.add(getactivitieswsdata());
    QuestionInfo questionInfo = new QuestionInfo();
    questionInfo.setActivityId(TEST_FETALKICK);
    questionInfo.setActivityVersion(TEST_ACTIVITY_VERSION);
    questionInfo.setKey(TEST_BLANK);
    AnchorDate anchorDate = new AnchorDate();
    anchorDate.setQuestionInfo(questionInfo);
    anchorDate.setType(TEST_FREQUENCY_TYPE);
    ActivityListData activityListData = new ActivityListData();
    activityListData.setActivities(activities);
    activityListData.setAnchorDate(anchorDate);
    activityListData.setMessage(TEST_MESSAGE);
    activityListData.setStudyId(TEST_FETALKICK);
    activityListData.setWithdrawalConfig(TEST_BLANK);
    return activityListData;
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
