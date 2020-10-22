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
import static org.hamcrest.Matchers.is;

import android.support.test.InstrumentationRegistry;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.surveyscheduler.SurveyScheduler;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import java.text.ParseException;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SurveyActivitiesFragmentTest {
  private Realm realm;
  private DbServiceSubscriber dbServiceSubscriber;
  private static final String TEST_USER_ID = "Test_user1234";
  private static final String TEST_STUDY_ID = "Test_Study_1221";
  private static final String TEST_STATUS = "status1";
  private static final String TEST_SITE_ID = "Test_site62132";
  private static final String TEST_ENROLLMENTDATE = "2020-10-08T12:10:00.000+0000";
  private static final boolean TEST_BOOKMARKED = false;
  private static final String TEST_PARTICIPANT_ID = "Test_Participtaant12345";
  private static final int TEST_COMPLETION = 1;
  private static final int TEST_ADHERENCE = 2;
  private static final String TEST_VERSION = "2.0";
  private static final String TEST_HASHED_TOKEN = "Test_hashed_token132166";
  private static final String TEST_USERID_KEY = "userId";
  private static final String TEST_STUDYID_KEY = "studyId";
  private static final String TEST_USERID_VALUE = "Test_user1234";
  private static final String TEST_STUDYID_VALUE = "Test_Study_1221";
  private static final String INVALID_DATE_FORMAT = "Invalid date format";

  @Before
  public void setUp() {
    realm = AppController.getRealmobj(InstrumentationRegistry.getTargetContext());
    dbServiceSubscriber = new DbServiceSubscriber();
  }

  @Test
  public void testJoiningDateOfStudy() {
    Date testDate = null;
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(getStudyData());
    realm.commitTransaction();
    SurveyScheduler survayScheduler = new SurveyScheduler(dbServiceSubscriber, realm);
    RealmResults<StudyData> studyPreferences =
        realm.where(StudyData.class).equalTo(TEST_USERID_KEY, TEST_USERID_VALUE).findAll();
    Date joiningDate =
        survayScheduler.getJoiningDateOfStudy(
            studyPreferences.get(studyPreferences.size() - 1), TEST_STUDY_ID);
    try {
      testDate = AppController.getDateFormatForApi().parse(TEST_ENROLLMENTDATE);
    } catch (ParseException e) {
      fail(INVALID_DATE_FORMAT);
    }
    assertThat(joiningDate.toString(), equalTo(testDate.toString()));
  }

  @After
  public void tearDown() {
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<StudyData> studyDataRealmResults =
                realm.where(StudyData.class).equalTo(TEST_USERID_KEY, TEST_USERID_VALUE).findAll();
            studyDataRealmResults.deleteAllFromRealm();
            RealmResults<Studies> studiesRealmResults =
                realm.where(Studies.class).equalTo(TEST_STUDYID_KEY, TEST_STUDYID_VALUE).findAll();
            studiesRealmResults.deleteAllFromRealm();
          }
        });
    dbServiceSubscriber.closeRealmObj(realm);
  }

  private StudyData getStudyData() {
    StudyData studyData = new StudyData();
    RealmList<Studies> runslist = new RealmList<>();
    runslist.add(getStudies());
    studyData.setStudies(runslist);
    studyData.setUserId(TEST_USER_ID);
    return studyData;
  }

  private Studies getStudies() {
    Studies studies = new Studies();
    studies.setStudyId(TEST_STUDY_ID);
    studies.setStatus(TEST_STATUS);
    studies.setSiteId(TEST_SITE_ID);
    studies.setEnrolledDate(TEST_ENROLLMENTDATE);
    studies.setBookmarked(TEST_BOOKMARKED);
    studies.setParticipantId(TEST_PARTICIPANT_ID);
    studies.setCompletion(TEST_COMPLETION);
    studies.setAdherence(TEST_ADHERENCE);
    studies.setVersion(TEST_VERSION);
    studies.setHashedToken(TEST_HASHED_TOKEN);
    return studies;
  }
}
