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

package org.sagebase.crf;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.ui.adapter.TaskAdapter;
import org.researchstack.backbone.ui.fragment.ActivitiesFragment;
import org.researchstack.backbone.utils.LogExt;
import org.sagebase.crf.helper.CrfScheduleHelper;
import org.sagebase.crf.view.CrfFilterableActivityDisplay;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfPrefs;
import org.sagebionetworks.bridge.researchstack.CrfResourceManager;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.BuildConfig;
import org.sagebionetworks.research.crf.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;

import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertNotNull;

/**
 * Created by TheMDP on 10/19/17
 */

public class CrfActivitiesFragment extends ActivitiesFragment implements CrfFilterableActivityDisplay {

    private ImageButton mBackButton;
    private ImageButton mSettingsButton;
    private View mClinicHeader;

    private SchedulesAndTasksModel mScheduleModel;
    private SchedulesAndTasksModel.ScheduleModel mSelectedSchedule;

    private String mDataGroups;

    private static final String LOG_TAG = CrfActivitiesFragment.class.getCanonicalName();

    private CrfTaskFactory taskFactory = new CrfTaskFactory();

    @VisibleForTesting
    final CrfDataProvider.CrfActivitiesListener mCrfActivitiesListener = new
            CrfDataProvider.CrfActivitiesListener() {
        @Override
        public void success(SchedulesAndTasksModel model) {

            if (getActivity() != null && isAdded()) {
                mScheduleModel = model;
                refreshAdapterSuccess(mScheduleModel);

                if (((CrfDataProvider) DataProvider.getInstance()).isNoScheduleTester()) {
                    showActivitiesForTestUser();
                } else {
                    // JOLIU check
                    // UX logic for participants and UX_TESTER
                    if (mSelectedSchedule == null) {
                        showAllActivities();
                    } else { // If there is a filter date, only show the clinic filtered activities
                        showActivitiesTheme();
                        showActivitiesForSchedule(mSelectedSchedule);
                    }
                }
            }
        }

        @Override
        public void error(String localizedError) {
            Log.e(LOG_TAG, localizedError);
            if (getActivity() != null && isAdded()) {
                if (CrfDataProvider.NO_CLINIC_ERROR_MESSAGE.equals(localizedError)) {
                    Log.e(LOG_TAG, "No clinic data group means user is in a bad state, send them" +
                            " back to overview");
                    startActivity(new Intent(getActivity(), CrfOverviewActivity.class));
                    getActivity().finish();
                } else {
                    refreshAdapterFailure(localizedError);
                }
            }
        }
    };

    // To allow unit tests to mock.
    @VisibleForTesting
    void setTaskFactory(CrfTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    // Mapping from task ID to resource name. Visible to enable unit tests.
    @VisibleForTesting
    static final Map<String, String> TASK_ID_TO_RESOURCE_NAME =
            ImmutableMap.<String, String>builder()
                    .put(CrfTaskFactory.TASK_ID_HEART_RATE_MEASUREMENT, CrfResourceManager.HEART_RATE_MEASUREMENT_TEST_RESOURCE)
                    .put(CrfTaskFactory.TASK_ID_CARDIO_STRESS_TEST, CrfResourceManager.CARDIO_STRESS_TEST_RESOURCE)
                    .put(CrfTaskFactory.TASK_ID_CARDIO_12MT, CrfResourceManager.CARDIO_12MT_WALK_RESOURCE)
                    .put(CrfTaskFactory.TASK_ID_STAIR_STEP, CrfResourceManager.STAIR_STEP_RESOURCE)
                    .put(CrfTaskFactory.TASK_ID_BACKGROUND_SURVEY, CrfResourceManager.BACKGROUND_SURVEY_RESOURCE)
                    .build();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBackButton = view.findViewById(R.id.crf_back_button);
        mBackButton.setOnClickListener(v -> clearFilter());
        mSettingsButton = view.findViewById(R.id.crf_settings_button);
        mSettingsButton.setOnClickListener(this::onSettingsClicked);
        mClinicHeader = view.findViewById(R.id.crf_clinic_header);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof  LinearLayoutManager) {
            ((LinearLayoutManager) recyclerView.getLayoutManager()).setStackFromEnd(true);
        } else {
            Log.e(LOG_TAG, "RecyclerView did not have a LinearLayoutManager");
        }
        int color = ResourcesCompat.getColor(getResources(), R.color.white, null);
        MainApplication.setStatusBarColor(getActivity(), color);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume - mSelectedSchedule: " + (mSelectedSchedule == null));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause - mSelectedSchedule: " + (mSelectedSchedule == null));
    }

    @Override
    public void fetchData() {
        checkState(DataProvider.getInstance() instanceof CrfDataProvider,
                "Special activities algorithm only available with CrfDataProvider");

        getSwipeFreshLayout().setRefreshing(true);

        CrfDataProvider crfDataProvider = (CrfDataProvider)DataProvider.getInstance();

        // Needed for settings
        if (mDataGroups == null) {
            crfDataProvider.getStudyParticipant().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(studyParticipant -> {
                        if (studyParticipant != null && studyParticipant.getDataGroups() != null) {
                            mDataGroups = studyParticipant.getDataGroups().toString();
                        }
                    }, throwable -> {
                        Log.w(LOG_TAG, "failed to load data groups", throwable);
                    });
        }

        crfDataProvider.getCrfActivities(getContext(), mCrfActivitiesListener);
    }
    

    @SuppressLint("RxSubscribeOnError")
    private void setupSelectionHandler(boolean isSchedule) {
        if (getAdapter() != null && getAdapter() instanceof CrfTaskAdapter) {
            CrfTaskAdapter crfTaskAdapter = (CrfTaskAdapter)getAdapter();

            unsubscribe();
            if (isSchedule) {
                setRxSubscription(crfTaskAdapter.publishScheduleSubject.subscribe(schedule -> {
                    LogExt.d(LOG_TAG, "Schedule clicked.");
                    if (schedule == null) {
                        LogExt.d(LOG_TAG, "Null schedule, cannot register click handler.");
                        return;
                    }
                    scheduleSelected(schedule);
                }));
            } else {
                setRxSubscription(crfTaskAdapter.getPublishSubject().subscribe(task -> {
                    LogExt.d(LOG_TAG, "Task clicked.");
                    taskSelected(task);
                }));
            }
        } else {
            Log.e(LOG_TAG, "Adapter must be CrfTaskAdapter");
        }
    }

    @Override
    protected TaskAdapter createTaskAdapter() {
        return new CrfTaskAdapter(getActivity());
    }

    @Override
    protected void startCustomTask(SchedulesAndTasksModel.TaskScheduleModel task) {
        if (TASK_ID_TO_RESOURCE_NAME.containsKey(task.taskID)) {
            if (task.taskID.equals(CrfTaskFactory.TASK_ID_BACKGROUND_SURVEY)) {
                Task activeTask = taskFactory.createTask(getActivity(), TASK_ID_TO_RESOURCE_NAME.get(task
                        .taskID));
                startActivityForResult(getIntentFactory().newTaskIntent(getActivity(),
                        CrfViewTaskActivity.class, activeTask), REQUEST_TASK);
            } else {
                Task activeTask = taskFactory.createTask(getActivity(), TASK_ID_TO_RESOURCE_NAME.get(task
                        .taskID));
                startActivityForResult(getIntentFactory().newTaskIntent(getActivity(),
                        CrfActiveTaskActivity.class, activeTask), REQUEST_TASK);
            }
        } else {
            Toast.makeText(getActivity(),
                    org.researchstack.backbone.R.string.rsb_local_error_load_task,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Class<? extends ViewTaskActivity> getDefaultViewTaskActivityClass() {
        return CrfSurveyTaskActivity.class;
    }

    public void showAllActivities() {
        CrfTaskAdapter adapter = (CrfTaskAdapter) getAdapter();
        adapter.clear();

        showTimelineTheme();

        List tasks = new ArrayList();

        // Per Zeplin design, if first clinic is not complete, that is all the user will see
        // Otherwise the whole journey is visible
        if(isFirstClinicComplete()) {
            for (SchedulesAndTasksModel.ScheduleModel schedule: mScheduleModel.schedules) {
                tasks.add(schedule);
            }
            adapter.addAll(tasks, false);

            int todayPosition = adapter.getPositionForToday();
            Log.d(LOG_TAG, "Adapter today position: " + todayPosition);
//            getRecyclerView().scrollToPosition(adapter.getPositionForToday());
//            getRecyclerView().scrollToPosition(adapter.getPositionForToday());
            ((LinearLayoutManager)getRecyclerView().getLayoutManager())
                    .scrollToPositionWithOffset(adapter.getPositionForToday(), 0);
        } else {
//            if (!CrfPrefs.getInstance().hasClinicDate()) {
//                Log.e(LOG_TAG, "We shouldnt even have gotten here, aborting UI setup");
//                return;
//            }
            DateTime firstClinicDateTime = CrfPrefs.getInstance().getClinicDate();
            if (firstClinicDateTime == null) {
                firstClinicDateTime = DateTime.now();
            }

            SchedulesAndTasksModel.ScheduleModel firstClinicSchedule = scheduleFor(firstClinicDateTime);
            if (firstClinicSchedule != null) {
                tasks.add(new CrfTaskAdapter.StartItem(firstClinicSchedule));
            } else {
                Log.e(LOG_TAG, "We need the clinic schedule, aborting UI setup");
                return;
            }
            CrfTaskAdapter.Footer footer = new CrfTaskAdapter.Footer(getString(R.string.crf_start_footer_title),
                    getString(R.string.crf_start_footer_message));
            tasks.add(footer);
            adapter.addAll(tasks, false);
        }

        setupSelectionHandler(true);
    }

    /**
     * CrfActivitiesFragment adapter uses schedules as the model to display,
     * so we must have custom selection handling
     * @param schedule the schedule that was selected
     */
    public void scheduleSelected(@NonNull SchedulesAndTasksModel.ScheduleModel schedule) {
        if (schedule.tasks.size() == 1) {
            // Single task, use default handling
            super.taskSelected(schedule.tasks.get(0));
        } else {
            // transition to clinic detail screen
            mSelectedSchedule = schedule;

            showActivitiesTheme();
            showActivitiesForSchedule(schedule);
        }
    }

    /**
     * @return true if every task in the first clinic has been complete, false in all other scenarios
     */
    private boolean isFirstClinicComplete() {
        if (!CrfPrefs.getInstance().hasClinicDate()) {
            return false;
        }
        // work around for people who have been doing daily tasks without completed schedule.
//        SchedulesAndTasksModel.ScheduleModel firstClinicSchedule =
//                scheduleFor(CrfPrefs.getInstance().getClinicDate());
//        return CrfScheduleHelper.allTasksComplete(firstClinicSchedule);
        return true;
    }

    private void showTimelineTheme() {
        mBackButton.setVisibility(View.GONE);
        mSettingsButton.setVisibility(View.VISIBLE);
        mClinicHeader.setVisibility(View.GONE);
    }

    private void showActivitiesTheme() {
        getSwipeFreshLayout().setEnabled(false);

        mBackButton.setVisibility(View.VISIBLE);
        mSettingsButton.setVisibility(View.GONE);
        mClinicHeader.setVisibility(View.VISIBLE);
    }

    private void showActivitiesForTestUser() {
        // This is a hack to show persistent tasks for test users.
        // CRF only shows activities scheduled for a day, and currently the way ScheduleModels
        // are filtered/build only will include tasks scheduledOn today, whereas persistent tasks
        // can be scheduled in the past and never expire
        SchedulesAndTasksModel.ScheduleModel model = new SchedulesAndTasksModel.ScheduleModel();
        model.scheduledOn = mScheduleModel.schedules.get(0).scheduledOn;
        model.tasks = Lists.newArrayList();

        for(SchedulesAndTasksModel.ScheduleModel sm : mScheduleModel.schedules) {
            model.tasks.addAll(sm.tasks);
        }

        // don't show the usual activities theme, since we don't need to switch between timeline
        // and activities mode
        showTimelineTheme();
        showActivitiesForSchedule(model);
    }

    private void showActivitiesForSchedule(@NonNull SchedulesAndTasksModel.ScheduleModel clinicSchedule) {
        assertNotNull(clinicSchedule);

        getAdapter().clear();

        ((CrfTaskAdapter)getAdapter()).addAll(Lists.newArrayList(clinicSchedule.tasks), true);
        setupSelectionHandler(false);
    }

    /**
     * @return the schedule for the date, or null if nothing is scheduled for the date
     */
    private SchedulesAndTasksModel.ScheduleModel scheduleFor(DateTime date) {
        for(SchedulesAndTasksModel.ScheduleModel sched: mScheduleModel.schedules) {
            if (CrfScheduleHelper.isScheduledFor(date, sched)
                    && !CrfScheduleHelper.allTasksComplete(sched)) {
                return sched;
            }
        }
        return null;  // no schedule today
    }

    @Override
    public void clearFilter() {
        Log.d(LOG_TAG, "clearFilter - mSelectedSchedule: " + (mSelectedSchedule == null));
        mSelectedSchedule = null;
        getSwipeFreshLayout().setEnabled(true);
        showAllActivities();
    }

    @Override
    public boolean isFiltered() {
        return (mSelectedSchedule != null);
    }

    public void onSettingsClicked(View v) {
        if (!(DataProvider.getInstance() instanceof CrfDataProvider)) {
            throw new IllegalStateException("CRF Settings Screen only works with a CrfDataProvider");
        }
        CrfDataProvider crfDataProvider = (CrfDataProvider)DataProvider.getInstance();
        String externalId = crfDataProvider.getExternalId(getActivity());
        taskFactory.startSettingsScreen(getActivity(),
                externalId, BuildConfig.VERSION_NAME, "shannon.young@sagebase.org", mDataGroups);
    }
}
