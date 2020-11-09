/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard;

import android.content.Context;
import android.os.Bundle;
import android.support.test.InstrumentationRegistry;
import com.google.firebase.messaging.RemoteMessage;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.utils.AppController;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AppFirebaseMessagingServiceTest {
  private Realm realm;
  private DbServiceSubscriber dbServiceSubscriber;
  public static String TYPE = "type";
  public static String SUBTYPE = "subtype";
  public static String STUDYID = "studyId";
  public static String AUDIENCE = "audience";
  private static String TITLE = "title";
  private static String MESSAGE = "message";
  private static String TESTID = "Test123";
  private static String TESTSTUDY = "Study";
  private static String TESTOTHERS = "Others";
  private static String TESTSUBTYPE = "TestSubStudy";
  private static String TESTAUDIENC = "TestAudience";
  private static String TESTTITLE = "TestTitle";
  private static String TESTSTUDYMESSAGE = "Test Study Notification Message";
  private static String TESTOTHERMESSAGE = "Test Others Notification Message";
  private static String TESTSTUDYIDKEY = "studyId";
  private static String TESTUSERIDKEY = "userId";
  private static String IDKEY = "id";
  private static int IDVALUE = 1;
  private static String STATUS = "inProgress";
  private static String SITEID = "TestSiteId";
  private static String ENROLLEDDATE = "2020-11-04T09:45:00.000+02:00";
  private static String PARTICIPANTID = "Testp123";
  private static int COMPLETION = 1;
  private static int ADHERENCE = 2;
  private static String VERSION = "1.0";
  private static String HASHEDTOKEN = "TestHashedToken";
  private static String STUDYLISTTITLE = "TestNotification";
  private static String STUDYLISTCATEGORY = "TestCategory";
  private static String STUDYLISTSPONSORNAME = "TestSponser";
  private static String STUDYLISTSTUDYVERSION = "1.0";
  private static String STUDYLISTTAGLINE = "TestTagLine";
  private static String STUDYLISTSTATUS = "inProgress";
  private static String STUDYLISTSTUDYSTATUS = "inProgress";
  private static String STUDYLISTLOGO = "TestLogo";
  private static boolean STUDYLISTBOOKMARKED = false;
  private static String STUDYLISTPDFPATH = "TestPath";
  private static String STUDYMESSAGE = "Test Message";
  private static int STUDY_ID = 1;
  Context context;

  @Before
  public void setUp() {
    realm = AppController.getRealmobj(InstrumentationRegistry.getTargetContext());
    dbServiceSubscriber = new DbServiceSubscriber();
    context = InstrumentationRegistry.getTargetContext();
  }

  @Test
  public void testNotificationTypeStudy() {
    Study study = getstudy();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(study);
    realm.commitTransaction();
    StudyData studyData = getStudyData();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(studyData);
    realm.commitTransaction();
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, TESTSTUDY);
    bundle.putString(SUBTYPE, TESTSUBTYPE);
    bundle.putString(STUDYID, TESTID);
    bundle.putString(AUDIENCE, TESTAUDIENC);
    bundle.putString(TITLE, TESTTITLE);
    bundle.putString(MESSAGE, TESTSTUDYMESSAGE);
    RemoteMessage remoteMessage = new RemoteMessage(bundle);
    AppFirebaseMessagingService messagingService = new AppFirebaseMessagingService();
    messagingService.setNotification(remoteMessage, context, realm, dbServiceSubscriber);
  }

  @Test
  public void testNotificationTypeOthers() {
    Study study = getstudy();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(study);
    realm.commitTransaction();
    StudyData studyData = getStudyData();
    realm.beginTransaction();
    realm.copyToRealmOrUpdate(studyData);
    realm.commitTransaction();
    Bundle bundle = new Bundle();
    bundle.putString(TYPE, TESTOTHERS);
    bundle.putString(SUBTYPE, TESTSUBTYPE);
    bundle.putString(STUDYID, TESTID);
    bundle.putString(AUDIENCE, TESTAUDIENC);
    bundle.putString(TITLE, TESTTITLE);
    bundle.putString(MESSAGE, TESTOTHERMESSAGE);
    RemoteMessage remoteMessage = new RemoteMessage(bundle);
    AppFirebaseMessagingService messagingService = new AppFirebaseMessagingService();
    messagingService.setNotification(remoteMessage, context, realm, dbServiceSubscriber);
  }

  @After
  public void tearDown() {
    realm.executeTransaction(
        new Realm.Transaction() {
          @Override
          public void execute(Realm realm) {
            RealmResults<StudyList> studyListRealmResults =
                realm.where(StudyList.class).equalTo(TESTSTUDYIDKEY, TESTID).findAll();
            studyListRealmResults.deleteAllFromRealm();
            RealmResults<Study> studyRealmResults =
                realm.where(Study.class).equalTo(IDKEY, IDVALUE).findAll();
            studyRealmResults.deleteAllFromRealm();
            RealmResults<Studies> studiesRealmResults =
                realm.where(Studies.class).equalTo(TESTSTUDYIDKEY, TESTID).findAll();
            studiesRealmResults.deleteAllFromRealm();
            RealmResults<StudyData> studyDataRealmResults =
                realm.where(StudyData.class).equalTo(TESTUSERIDKEY, TESTID).findAll();
            studyDataRealmResults.deleteAllFromRealm();
          }
        });
    dbServiceSubscriber.closeRealmObj(realm);
  }

  private Study getstudy() {
    StudyList studyList = new StudyList();
    studyList.setBookmarked(STUDYLISTBOOKMARKED);
    studyList.setCategory(STUDYLISTCATEGORY);
    studyList.setLogo(STUDYLISTLOGO);
    studyList.setPdfPath(STUDYLISTPDFPATH);
    studyList.setStudyId(TESTID);
    studyList.setStatus(STUDYLISTSTATUS);
    studyList.setTagline(STUDYLISTTAGLINE);
    studyList.setStudyVersion(STUDYLISTSTUDYVERSION);
    studyList.setSponsorName(STUDYLISTSPONSORNAME);
    studyList.setTitle(STUDYLISTTITLE);
    studyList.setStudyStatus(STUDYLISTSTUDYSTATUS);
    RealmList<StudyList> studyLists = new RealmList<>();
    studyLists.add(studyList);
    Study study = new Study();
    study.setMessage(STUDYMESSAGE);
    study.setId(STUDY_ID);
    study.setStudies(studyLists);
    return study;
  }

  private StudyData getStudyData() {
    Studies studies = new Studies();
    studies.setBookmarked(false);
    studies.setSiteId(SITEID);
    studies.setEnrolledDate(ENROLLEDDATE);
    studies.setParticipantId(PARTICIPANTID);
    studies.setStudyId(TESTID);
    studies.setCompletion(COMPLETION);
    studies.setAdherence(ADHERENCE);
    studies.setStatus(STATUS);
    studies.setVersion(VERSION);
    studies.setHashedToken(HASHEDTOKEN);
    RealmList<Studies> studiesRealmList = new RealmList<>();
    studiesRealmList.add(studies);
    StudyData study = new StudyData();
    study.setUserId(TESTID);
    study.setStudies(studiesRealmList);
    return study;
  }
}
