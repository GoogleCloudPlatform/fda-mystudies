/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebionetworks.bridge.researchstack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.ui.ActiveTaskActivity;
import org.researchstack.skin.AppPrefs;
import org.sagebionetworks.bridge.android.BridgeConfig;
import org.sagebionetworks.bridge.android.manager.ActivityManager;
import org.sagebionetworks.bridge.android.manager.AuthenticationManager;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.android.manager.dao.AccountDAO;
import org.sagebionetworks.bridge.android.manager.dao.ConsentDAO;
import org.sagebionetworks.bridge.android.manager.upload.SchemaKey;
import org.sagebionetworks.bridge.researchstack.wrapper.StorageAccessWrapper;
import org.sagebionetworks.bridge.rest.ApiClientProvider;
import org.sagebionetworks.bridge.rest.api.AuthenticationApi;
import org.sagebionetworks.bridge.rest.api.ForConsentedUsersApi;
import org.sagebionetworks.bridge.rest.model.Activity;
import org.sagebionetworks.bridge.rest.model.Message;
import org.sagebionetworks.bridge.rest.model.ScheduledActivity;
import org.sagebionetworks.bridge.rest.model.ScheduledActivityListV4;
import org.sagebionetworks.bridge.rest.model.SignIn;
import org.sagebionetworks.bridge.rest.model.StudyParticipant;
import org.sagebionetworks.bridge.rest.model.TaskReference;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import java.util.ArrayList;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;
import rx.Observable;
import rx.Single;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mdephillips on 11/5/17
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PreferenceManager.class, Looper.class})
public class CrfDataProviderTest {
    private static final String SCHEMA_ID = "my-schema-id";
    private static final int SCHEMA_REV = 3;
    private static final SchemaKey SCHEMA_KEY = new SchemaKey(SCHEMA_ID, SCHEMA_REV);
    private static final String TASK_ID = "my-task-id";

    private MockCrfDataProvider dataProvider;
    @Mock
    private ApiClientProvider apiClientProvider;
    @Mock
    private ForConsentedUsersApi forConsentedUsersApi;
    @Mock
    private AuthenticationApi authenticationApi;
    private StorageAccessWrapper storageAccess;
    private PinCodeConfig pinCodeConfig;
    private FileAccess fileAccess;
    private BridgeEncryptedDatabase appDatabase;
    @Mock
    private TaskHelper taskHelper;

    @Mock
    protected BridgeManagerProvider bridgeManagerProvider;
    @Mock
    protected Context context;
    @Mock
    protected BridgeConfig bridgeConfig;
    @Mock
    protected AccountDAO accountDAO;
    @Mock
    protected ConsentDAO consentDAO;
    @Mock
    protected ResearchStackDAO researchStackDAO;
    @Mock
    protected AuthenticationManager authenticationManager;
    @Mock
    private ActivityManager activityManager;

    @Before
    public void setupTest() {
        MockitoAnnotations.initMocks(this);

        BridgeManagerProvider.init(bridgeManagerProvider);

        when(bridgeManagerProvider.getApplicationContext()).thenReturn(context);
        when(bridgeManagerProvider.getBridgeConfig()).thenReturn(bridgeConfig);
        when(bridgeManagerProvider.getAccountDao()).thenReturn(accountDAO);
        when(bridgeManagerProvider.getConsentDao()).thenReturn(consentDAO);
        when(bridgeManagerProvider.getAuthenticationManager()).thenReturn(authenticationManager);
        when(bridgeManagerProvider.getActivityManager()).thenReturn(activityManager);

        pinCodeConfig = mock(PinCodeConfig.class);
        fileAccess = mock(FileAccess.class);
        appDatabase = mock(BridgeEncryptedDatabase.class);

        storageAccess = mock(StorageAccessWrapper.class);
        when(storageAccess.getPinCodeConfig()).thenReturn(pinCodeConfig);
        when(storageAccess.getAppDatabase()).thenReturn(appDatabase);
        when(storageAccess.getFileAccess()).thenReturn(fileAccess);

        when(apiClientProvider.getClient(AuthenticationApi.class)).thenReturn(authenticationApi);
        when(apiClientProvider
                .getClient(same(ForConsentedUsersApi.class), any(SignIn.class)))
                .thenReturn(forConsentedUsersApi);

        resetTest(null, true, null);

        PowerMockito.mockStatic(PreferenceManager.class);
        PowerMockito.mockStatic(Looper.class);

        AppPrefs.init(context);
    }

    private void resetTest(DateTime firstSignInDate, boolean assignClinic1, String initialDataGroup) {
        dataProvider = new MockCrfDataProvider(
                firstSignInDate, assignClinic1, initialDataGroup,
                researchStackDAO, storageAccess, taskHelper);
        dataProvider.mockGetAllActiities(successList(assignClinic1));
    }

    @Test
    public void testInitialize() {
        dataProvider.initialize(context).test().assertCompleted();
    }

    @Test
    public void testFindClinc1() {
        final ScheduledActivityListV4 successList = successList(true);
        final ScheduledActivity clinic = dataProvider.findActivity(successList, "clinic1");
        assertNotNull(clinic);
        assertEquals("clinic1", clinic.getActivity().getTask().getIdentifier());
    }

    @Test
    public void testFindClinc2() {
        final ScheduledActivityListV4 successList = successList(true);
        final ScheduledActivity clinic = dataProvider.findActivity(successList, "clinic2");
        assertNotNull(clinic);
        assertEquals("clinic2", clinic.getActivity().getTask().getIdentifier());
    }

    @Test
    public void testFindFail() {
        final ScheduledActivityListV4 successList = successList(true);
        final ScheduledActivity clinic = dataProvider.findActivity(successList, "clinic3");
        assertNull(clinic);
    }

    @Test
    public void testAssignClinic1() {
        resetTest(null, true, null);
        final ScheduledActivityListV4 successList = successList(true);
        dataProvider.mockGetAllActiities(successList);

        dataProvider.mockGetClinicActiities(clinicActivities(null, null));
        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC1);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testAssignClinic2() {
        resetTest(null, false, null);
        final ScheduledActivityListV4 successList = successList(false);
        dataProvider.mockGetAllActiities(successList);

        dataProvider.mockGetClinicActiities(clinicActivities(null, null));
        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC2);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testExistingServerDataGroupClinic1() {
        resetTest(null, true, "clinic1");
        final ScheduledActivityListV4 successList = successList(true);
        dataProvider.mockGetAllActiities(successList);

        dataProvider.mockGetClinicActiities(clinicActivities(null, null));
        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC1);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testExistingServerDataGroupClinic2() {
        resetTest(null, false, "clinic2");
        final ScheduledActivityListV4 successList = successList(false);
        dataProvider.mockGetAllActiities(successList);

        dataProvider.mockGetClinicActiities(clinicActivities(null, null));
        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);

                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC2);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testExistingFinishedOnClinic1() {
        resetTest(null, true, "clinic1");
        final ScheduledActivityListV4 successList = successList(true);
        dataProvider.mockGetAllActiities(successList);
        dataProvider.mockGetClinicActiities(successList);

        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC1);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testExistingFinishedOnClinic2() {
        resetTest(null, true, "clinic2");
        final ScheduledActivityListV4 successList = successList(false);
        dataProvider.mockGetAllActiities(successList);
        dataProvider.mockGetClinicActiities(successList);

        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC2);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    @Test
    public void testExistingFinishedOnInPrefs() {
        resetTest(dataProvider.COMPLETION_TIME_CLINIC1, true, "clinic2");
        final ScheduledActivityListV4 successList = successList(false);
        dataProvider.mockGetAllActiities(successList);
        dataProvider.mockGetClinicActiities(successList);

        dataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                assertEquals(activityList, successList);
                assertEquals(dataProvider.getCrfPrefs().getFirstSignInDate(), dataProvider.COMPLETION_TIME_CLINIC1);
            }

            @Override
            public void error(String localizedError) {
                assertFalse(true);  // fail
            }
        });
    }

    private ScheduledActivityListV4 clinicActivities(DateTime clinic1FinishedOn, DateTime clinic2FinishedOn) {
        ScheduledActivityListV4 activitiesModel = new ScheduledActivityListV4();
        List<ScheduledActivity> activitiesList = new ArrayList<>();
        activitiesList.add(activityWith("clinic1", clinic1FinishedOn));
        activitiesList.add(activityWith("clinic2", clinic2FinishedOn));
        activitiesModel.setItems(activitiesList);
        return activitiesModel;
    }

    private ScheduledActivityListV4 successList(boolean clinic1Complete) {
        ScheduledActivityListV4 activitiesModel = new ScheduledActivityListV4();
        List<ScheduledActivity> activitiesList = new ArrayList<>();
        activitiesList.add(activityWith("success", null));
        activitiesList.add(activityWith("clinic1",  clinic1Complete ? dataProvider.COMPLETION_TIME_CLINIC1 : null));
        activitiesList.add(activityWith("clinic2", !clinic1Complete ? dataProvider.COMPLETION_TIME_CLINIC2 : null));
        activitiesModel.setItems(activitiesList);
        return activitiesModel;
    }

    private ScheduledActivity activityWith(String id, DateTime finishedOn) {
        ScheduledActivity activityModel = new ScheduledActivity();
        activityModel.setFinishedOn(finishedOn);
        Activity activity = new Activity();
        TaskReference task = new TaskReference();
        task.setIdentifier(id);
        activity.setTask(task);
        activityModel.setActivity(activity);
        return activityModel;
    }

    private static TaskResult makeActivityTask(String taskId) {
        TaskResult taskResult = new TaskResult(taskId);
        taskResult.getTaskDetails().put(ActiveTaskActivity.ACTIVITY_TASK_RESULT_KEY, true);
        return taskResult;
    }

    private class MockCrfDataProvider extends CrfDataProvider {
        MockCrfPrefs prefs;
        boolean isRandomClient1;
        String initialDataGroup;

        ScheduledActivityListV4 clinicActivities;
        ScheduledActivityListV4 allActivities;

        private DateTime START_TIME = DateTime.parse("2017-11-01T07:00-0700");
        private DateTime END_TIME_FOR_CLINICS = DateTime.parse("2017-11-02T07:00-0700");
        private DateTime END_TIME_FOR_ALL = DateTime.parse("2017-11-15T06:00-0700");

        private DateTime COMPLETION_TIME_CLINIC1 = DateTime.parse("2017-11-01T07:01-0700");
        private DateTime COMPLETION_TIME_CLINIC2 = DateTime.parse("2017-11-01T07:02-0700");

        @VisibleForTesting
        MockCrfDataProvider(DateTime firstSignInDate, boolean assignClinic1, String initialDataGroup,
                ResearchStackDAO researchStackDAO, StorageAccessWrapper storageAccessWrapper, TaskHelper taskHelper) {

            super(researchStackDAO, storageAccessWrapper, taskHelper);
            prefs = new MockCrfPrefs(firstSignInDate);
            isRandomClient1 = assignClinic1;
            this.initialDataGroup = initialDataGroup;
        }

        void mockGetClinicActiities(ScheduledActivityListV4 activityList) {
            clinicActivities = activityList;
        }

        void mockGetAllActiities(ScheduledActivityListV4 activityList) {
            allActivities = activityList;
        }

        @VisibleForTesting
        DateTime startTime() {
            return START_TIME;
        }

        @VisibleForTesting
        DateTime endTimeForClinicActivities() {
            return END_TIME_FOR_CLINICS;
        }

        @VisibleForTesting
        DateTime endTimeForAllActivities(DateTime firstSignInDate) {
            return END_TIME_FOR_ALL;
        }

        @VisibleForTesting
        DateTime createClinicCompletionDate() {
            if (isRandomClient1 || (initialDataGroup != null && initialDataGroup.equals("clinic1"))) {
                return COMPLETION_TIME_CLINIC1;
            } else {
                return COMPLETION_TIME_CLINIC2;
            }
        }

        @VisibleForTesting
        boolean generateRandomClient() {
            return isRandomClient1;
        }

        @VisibleForTesting
        CrfPrefs getCrfPrefs() {
            return prefs;
        }

        @VisibleForTesting void logV(String msg) {} // no op
        @VisibleForTesting void logE(String msg) {} // no op
        @VisibleForTesting void debugPrintActivities(ScheduledActivityListV4 activityList) {}

        @VisibleForTesting
        void getActivitiesSubscribe(DateTime start, DateTime end,
                                    final Action1<ScheduledActivityListV4> onNext,
                                    final Action1<Throwable> onError) {
            if (start.equals(START_TIME) && end.equals(END_TIME_FOR_CLINICS)) {
                onNext.call(clinicActivities);
            } else {
                onNext.call(allActivities);
            }
        }

        @VisibleForTesting
        void getStudyParticipantSubscribe(final Action1<StudyParticipant> onNext,
                                          final Action1<Throwable> onError) {
            StudyParticipant studyParticipant = new StudyParticipant();
            studyParticipant.setDataGroups(Collections.singletonList(initialDataGroup));
            onNext.call(studyParticipant);
        }

        @VisibleForTesting
        void updateStudyParticipantSubscribe(StudyParticipant studyParticipant,
                                             final Action1<UserSessionInfo> onNext,
                                             final Action1<Throwable> onError) {
            // Test if provider is assigning the user the expected clinic data group
            if (isRandomClient1) {
                assertTrue(studyParticipant.getDataGroups().contains("clinic1"));
            } else {
                assertTrue(studyParticipant.getDataGroups().contains("clinic2"));
            }
            onNext.call(new UserSessionInfo());
        }

        @VisibleForTesting
        void updateActivitySubscribe(ScheduledActivity activity,
                                     final Action1<Message> onNext,
                                     final Action1<Throwable> onError) {

            // Test if provider is completing the expected clinic
            if (isRandomClient1 || (initialDataGroup != null && initialDataGroup.equals("clinic1"))) {
                assertEquals("clinic1", activity.getActivity().getTask().getIdentifier());
            } else {
                assertEquals("clinic2", activity.getActivity().getTask().getIdentifier());
            }
            onNext.call(new Message());
        }
    }

    private class MockCrfPrefs extends CrfPrefs {
        private DateTime firstSignInDate = null;

        MockCrfPrefs(DateTime firstSignInDateTime) {
            super(context);
            this.firstSignInDate = firstSignInDateTime;
        }

        SharedPreferences createPrefs(Context context) {
            return null;
        }

        public DateTime getFirstSignInDate() {
            return firstSignInDate;
        }

        public void setFirstSignInDate(DateTime dateTime) {
            this.firstSignInDate = dateTime;
        }
    }
}