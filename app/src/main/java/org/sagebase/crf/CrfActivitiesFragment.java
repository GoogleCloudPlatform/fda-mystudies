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

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.annotation.VisibleForTesting;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.skin.ui.adapter.TaskAdapter;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;
import org.sagebase.crf.view.CrfFilterableActivityDisplay;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfPrefs;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 10/19/17
 */

public class CrfActivitiesFragment extends ActivitiesFragment implements CrfFilterableActivityDisplay {

    private SchedulesAndTasksModel mScheduleModel;
    private Date mClinicDate;

    private static final String LOG_TAG = CrfActivitiesFragment.class.getCanonicalName();

    private CrfTaskFactory taskFactory = new CrfTaskFactory();
    // To allow unit tests to mock.
    @VisibleForTesting
    void setTaskFactory(CrfTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    // Mapping from task ID to resource name. Visible to enable unit tests.
    @VisibleForTesting
    static final Map<String, String> TASK_ID_TO_RESOURCE_NAME =
            ImmutableMap.<String, String>builder()
                    .put(CrfTaskFactory.TASK_ID_HEART_RATE_MEASUREMENT, "heart_rate_measurement")
                    .put(CrfTaskFactory.TASK_ID_CARDIO_12MT, "12_minute_walk")
                    .put(CrfTaskFactory.TASK_ID_STAIR_STEP, "stair_step")
                    .build();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate - mClinicDate: " + (mClinicDate == null));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume - mClinicDate: " + (mClinicDate == null));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause - mClinicDate: " + (mClinicDate == null));
    }

    @Override
    public void fetchData() {
        getSwipeFreshLayout().setRefreshing(true);

        if (!(DataProvider.getInstance() instanceof  CrfDataProvider)) {
            throw new IllegalStateException("Special activities algorithm only available with CrfDataProvider");
        }

        CrfDataProvider crfDataProvider = (CrfDataProvider)DataProvider.getInstance();
        crfDataProvider.getCrfActivities(getContext(), new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(SchedulesAndTasksModel model) {
                mScheduleModel = model;
                refreshAdapterSuccess(mScheduleModel);

                if(mClinicDate == null) {
                    showAllActivities();
                } else { // If there is a filter date, only show the clinic filtered activities
                    showClinicActivities();
                }
            }

            @Override
            public void error(String localizedError) {
                refreshAdapterFailure(localizedError);
            }
        });
    }

    private void setupCrfScheduleSelection() {
        if (getAdapter() != null && getAdapter() instanceof CrfTaskAdapter) {
            CrfTaskAdapter crfTaskAdapter = (CrfTaskAdapter)getAdapter();

            unsubscribe();
            if (mClinicDate == null) {
                setRxSubscription(crfTaskAdapter.publishScheduleSubject.subscribe(schedule -> {

                    // Business logic for if a task is clickable
                    boolean singleTaskSelectable = schedule.tasks.size() == 1 &&
                            !allTasksCompleteOn(schedule.scheduledOn) &&
                            DateUtils.isToday(schedule.scheduledOn.getTime());

                    boolean isTodaySameDayOrAfterClinic =
                            DateUtils.isToday(schedule.scheduledOn.getTime()) ||
                            new Date().after(schedule.scheduledOn);

                    boolean clinicGroupSelectable = schedule.tasks.size() > 1 &&
                            !allTasksCompleteOn(schedule.scheduledOn) &&
                            isTodaySameDayOrAfterClinic;

                    if (singleTaskSelectable || clinicGroupSelectable) {
                        scheduleSelected(schedule);
                    }
                }));
            } else {
                setRxSubscription(crfTaskAdapter.getPublishSubject().subscribe(task -> {
                    LogExt.d(LOG_TAG, "Publish subject subscribe clicked.");
                    if (task.taskFinishedOn == null) {
                        taskSelected(task);
                    }
                }));
            }
        } else {
            Log.e(LOG_TAG, "Adapter must be CrfTaskAdapter");
        }
    }

    protected TaskAdapter createTaskAdapter() {
        return new CrfTaskAdapter(getActivity());
    }

    protected void setUpAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(),
//                DividerItemDecoration.VERTICAL_LIST,
//                0,
//                false));

        fetchData();
    }

    @Override
    protected void startCustomTask(SchedulesAndTasksModel.TaskScheduleModel task) {
        if (TASK_ID_TO_RESOURCE_NAME.containsKey(task.taskID)) {
            Task testTask = taskFactory.createTask(getActivity(), TASK_ID_TO_RESOURCE_NAME.get(task
                    .taskID));
            startActivityForResult(getIntentFactory().newTaskIntent(getActivity(),
                    CrfActiveTaskActivity.class, testTask), REQUEST_TASK);
        } else {
            Toast.makeText(getActivity(),
                    org.researchstack.skin.R.string.rss_local_error_load_task,
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void showAllActivities() {
        recyclerView.setPadding(0, 0, 0, 0);

        CrfTaskAdapter adapter = (CrfTaskAdapter) getAdapter();
        adapter.clear();

        // Per Zeplin design, if first clinic is not complete, that is all the user will see
        // Otherwise the whole journey is visible
        if(isFirstClinicComplete()) {
            List<Object> objList = new ArrayList<>();
            for (SchedulesAndTasksModel.ScheduleModel schedule: mScheduleModel.schedules) {
                objList.add(schedule);
            }
            adapter.addAll(objList, false);
            int todayPosition = adapter.getPositionForToday();
            Log.d(LOG_TAG, "Adapter today position: " + todayPosition);
            getRecyclerView().scrollToPosition(adapter.getPositionForToday());
        } else {
            List<Object> tasks = new ArrayList<>();
            if (!CrfPrefs.getInstance().hasFirstSignInDate()) {
                Log.e(LOG_TAG, "We shouldnt even have gotten here, aborting UI setup");
                return;
            }
            Date firstClinicDate = CrfPrefs.getInstance().getFirstSignInDate().toDate();
            SchedulesAndTasksModel.ScheduleModel firstClinicSchedule = scheduleFor(firstClinicDate);
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

        setupCrfScheduleSelection();
    }

    /**
     * CrfActivitiesFragment adapter uses schedules as the model to display,
     * so we must have custom selection handling
     * @param schedule the schedule that was selected
     */
    public void scheduleSelected(SchedulesAndTasksModel.ScheduleModel schedule) {
        if (schedule.tasks.size() == 1) {  // Single task, use default handling
            super.taskSelected(schedule.tasks.get(0));
        } else {
            // transition to clinic detail screen
            mClinicDate = schedule.scheduledOn;
            showClinicActivities();
        }
    }

    /**
     * @return true if every task in the first clinic has been complete, false in all other scenarios
     */
    private boolean isFirstClinicComplete() {
        if (!CrfPrefs.getInstance().hasFirstSignInDate()) {
            return false;
        }
        Date firstClinicDate = CrfPrefs.getInstance().getFirstSignInDate().toDate();
        return allTasksCompleteOn(firstClinicDate);
    }

    /**
     * @return true if every task in the schedule on this date is complete, false in all other scenarios
     */
    private boolean allTasksCompleteOn(Date date) {
        boolean allTasksComplete = true;
        SchedulesAndTasksModel.ScheduleModel firstClinicSchedule = scheduleFor(date);
        if (firstClinicSchedule != null) {
            for (SchedulesAndTasksModel.TaskScheduleModel task : firstClinicSchedule.tasks) {
                if (task.taskFinishedOn == null) {
                    allTasksComplete = false;
                }
            }
        }
        return allTasksComplete;
    }

    /**
     * Filter the activities to display based on the supplied date.
     */
    private void showClinicActivities() {
        getSwipeFreshLayout().setEnabled(false);

        int padding = getResources().getDimensionPixelOffset(R.dimen.rsb_padding_large);
        recyclerView.setPadding(padding, padding, padding, padding);

        SchedulesAndTasksModel.ScheduleModel clinicSchedule = scheduleFor(mClinicDate);
        if (clinicSchedule != null) {
            getAdapter().clear();
            List<Object> tasks = new ArrayList<>();

            CrfTaskAdapter.Header header = new CrfTaskAdapter.Header(getString(R.string.crf_clinic_fitness_test),
                    getString(R.string.crf_clinic_message));
            tasks.add(header);

            // Show all the tasks in the clinic
            for (SchedulesAndTasksModel.TaskScheduleModel task : clinicSchedule.tasks) {
                tasks.add(task);
            }

            ((CrfTaskAdapter)getAdapter()).addAll(tasks, true);
        }
        setupCrfScheduleSelection();
    }

    /**
     * @return the schedule for the date, or null if nothing is scheduled for the date
     */
    private SchedulesAndTasksModel.ScheduleModel scheduleFor(Date date) {
        for(SchedulesAndTasksModel.ScheduleModel sched: mScheduleModel.schedules) {
            if (CrfTaskAdapter.isSameDay(date, sched.scheduledOn)) {
                return sched;
            }
        }
        return null;  // no schedule today
    }

    @Override
    public void clearFilter() {
        Log.d(LOG_TAG, "clearFilter - mClinicDate: " + (mClinicDate == null));
        mClinicDate = null;
        getSwipeFreshLayout().setEnabled(true);
        showAllActivities();
    }

    @Override
    public boolean isFiltered() {
        return (mClinicDate != null);
    }
}
