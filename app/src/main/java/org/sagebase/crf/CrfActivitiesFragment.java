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

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.support.annotation.VisibleForTesting;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.task.Task;
import org.researchstack.skin.ui.adapter.TaskAdapter;
import org.researchstack.skin.ui.fragment.ActivitiesFragment;
import org.sagebase.crf.reminder.AlarmReceiver;
import org.sagebase.crf.reminder.CrfReminderManager;
import org.sagebase.crf.view.CrfFilterableActivityDisplay;
import org.sagebionetworks.bridge.researchstack.CrfDataProvider;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.bridge.rest.model.ScheduledActivity;
import org.sagebionetworks.bridge.rest.model.ScheduledActivityListV4;
import org.sagebionetworks.research.crf.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.Set;

/**
 * Created by TheMDP on 10/19/17
 */

public class CrfActivitiesFragment extends ActivitiesFragment implements CrfFilterableActivityDisplay {

    private SchedulesAndTasksModel mScheduleModel;
    private Date mFilteredDate;

    // Task IDs that should be hidden from the activities page. Visible to enable unit tests.
    @VisibleForTesting
    static final Set<String> HIDDEN_TASK_IDS = ImmutableSet.of(CrfDataProvider.CLINIC1,
            CrfDataProvider.CLINIC2);

    private static final String LOG_TAG = CrfActivitiesFragment.class.getCanonicalName();

    /**
     * When true, we will use the base class' fetch activities
     * When false, we will use the new clinic assignment groupings of activities
     */
    private static final boolean USE_LEGACY_GET_ACTIVITIES = true;

    private CrfTaskFactory taskFactory = new CrfTaskFactory();
    // To allow unit tests to mock.
    @VisibleForTesting
    void setTaskFactory(CrfTaskFactory taskFactory) {
        this.taskFactory = taskFactory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate - mFilteredDate: " + (mFilteredDate == null));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume - mFilteredDate: " + (mFilteredDate == null));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause - mFilteredDate: " + (mFilteredDate == null));
    }

    @Override
    public void fetchData() {
        getSwipeFreshLayout().setRefreshing(true);

        if (USE_LEGACY_GET_ACTIVITIES) {
            super.fetchData();
            return;
        }

        if (!(DataProvider.getInstance() instanceof  CrfDataProvider)) {
            throw new IllegalStateException("Special activities algorithm only available with CrfDataProvider");
        }

        CrfDataProvider crfDataProvider = (CrfDataProvider)DataProvider.getInstance();
        crfDataProvider.getCrfActivities(new CrfDataProvider.CrfActivitiesListener() {
            @Override
            public void success(ScheduledActivityListV4 activityList) {
                getSwipeFreshLayout().setRefreshing(false);
                if (getAdapter() == null) {
                    unsubscribe();
                    setAdapter(createTaskAdapter());
                    getRecyclerView().setAdapter(getAdapter());

                    setRxSubscription(getAdapter().getPublishSubject().subscribe(task -> {
                        taskSelected(task);
                    }));
                } else {
                    getAdapter().clear();
                }


                List<ScheduledActivity> scheduledActivities = processResults(activityList);
                //Log.d(LOG_TAG, scheduledActivities.toString());

                mScheduleModel = translateActivities(scheduledActivities);
                if(mFilteredDate == null) {
                    showAllActivities();
                } else {
                    filterActivities();
                }

            }

            @Override
            public void error(String localizedError) {
                getSwipeFreshLayout().setRefreshing(false);
                new AlertDialog.Builder(getContext()).setMessage(localizedError).create().show();
            }
        });
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

    // Mapping from task ID to resource name. Visible to enable unit tests.
    @VisibleForTesting
    static final Map<String, String> TASK_ID_TO_RESOURCE_NAME =
            ImmutableMap.<String, String>builder()
                    .put(CrfTaskFactory.TASK_ID_HEART_RATE_MEASUREMENT, "heart_rate_measurement")
                    .put(CrfTaskFactory.TASK_ID_CARDIO_12MT, "12_minute_walk")
                    .put(CrfTaskFactory.TASK_ID_STAIR_STEP, "stair_step")
                    .build();

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
        CrfTaskAdapter adapter = (CrfTaskAdapter) getAdapter();
        adapter.clear();

        //boolean completed = CrfPrefs.getInstance().hasCompletedFirstClinic();
        boolean completed = true;

        if(completed) {
            adapter.addAll(processResults(mScheduleModel), false);

            // set reminders
            for(Object obj: processResults(mScheduleModel)) {
                if(obj instanceof CrfTask) {
                    CrfTask task = (CrfTask)obj;
                    CrfReminderManager.setReminder(getContext(), AlarmReceiver.class, task.scheduledOn);
                }
            }

            int todayPosition = adapter.getPositionForToday();
            Log.d(LOG_TAG, "Adapter today position: " + todayPosition);
            getRecyclerView().scrollToPosition(adapter.getPositionForToday());
        } else {
            List<Object> tasks = new ArrayList<>();
            for(Object obj: processResults(mScheduleModel)) {
                CrfTask task = (CrfTask)obj;
                if (CrfTaskAdapter.isToday(task.scheduledOn)) {
                    tasks.add(new CrfTaskAdapter.StartItem(task));
                    break;
                }
            }

            CrfTaskAdapter.Footer footer = new CrfTaskAdapter.Footer(getString(R.string.crf_start_footer_title),
                    getString(R.string.crf_start_footer_message));
            tasks.add(footer);
            adapter.addAll(tasks, false);
        }
    }

    /**
     * Filter the activities to display based on the supplied date.
     *
     */
    private void filterActivities() {
        getSwipeFreshLayout().setEnabled(false);
        ViewGroup.LayoutParams params = getRecyclerView().getLayoutParams();

        for(SchedulesAndTasksModel.ScheduleModel sched: mScheduleModel.schedules) {
            if(CrfTaskAdapter.isSameDay(mFilteredDate, sched.scheduledOn)) {
                getAdapter().clear();
                List<Object> tasks = new ArrayList<>();

                CrfTaskAdapter.Header header = new CrfTaskAdapter.Header(getString(R.string.crf_clinic_fitness_test),
                        getString(R.string.crf_clinic_message));
                tasks.add(header);
                for(SchedulesAndTasksModel.TaskScheduleModel t: sched.tasks) {
                    CrfTask ct = new CrfTask(t);
                    ct.scheduledOn = sched.scheduledOn;
                    tasks.add(ct);
                }
                ((CrfTaskAdapter)getAdapter()).addAll(tasks, true);
                break;
            }
        }

    }

    @Override
    public void clearFilter() {
        Log.d(LOG_TAG, "clearFilter - mFilteredDate: " + (mFilteredDate == null));
        mFilteredDate = null;
        getSwipeFreshLayout().setEnabled(true);
        showAllActivities();
    }

    @Override
    public boolean isFiltered() {
        return (mFilteredDate != null);
    }

    /**
     * Legacy way of processing results, it will just show whatever is handed to it
     * @param model SchedulesAndTasksModel object
     * @return a list of TaskScheduleModel
     */
    @Override
    public List<Object> processResults(SchedulesAndTasksModel model) {
        if (model == null || model.schedules == null) {
            return new ArrayList<>();
        }
        List<Object> tasks = new ArrayList<>();

        Collections.sort(model.schedules, new Comparator<SchedulesAndTasksModel.ScheduleModel>() {
            public int compare(SchedulesAndTasksModel.ScheduleModel o1, SchedulesAndTasksModel.ScheduleModel o2) {
                return o2.scheduledOn.compareTo(o1.scheduledOn);
            }
        });

        for (SchedulesAndTasksModel.ScheduleModel scheduleModel : model.schedules) {
            CrfTask ct = null;
            if(scheduleModel.tasks != null && scheduleModel.tasks.size() == 1) {
                ct = new CrfTask(scheduleModel.tasks.get(0));
            } else {
                // create a fake task for clinics
                ct = new CrfTask();
                ct.taskID = CrfTaskFactory.TASK_ID_CLINIC;
                ct.taskTitle = getString(R.string.crf_clinic_fitness_test);
                ct.taskCompletionTime = "40 minutes";  // TODO: where to get this?  sum up tasks?
            }
            ct.scheduledOn = scheduleModel.scheduledOn;
            tasks.add(ct);
        }

        return tasks;
    }

    /**
     * TODO: Rian this is where you can access the data model
     */
    public List<ScheduledActivity> processResults(ScheduledActivityListV4 activityList) {
        if (activityList == null || activityList.getItems() == null) {
            return new ArrayList<>();
        }
        List<ScheduledActivity> activities = new ArrayList<>(activityList.getItems());

        List<ScheduledActivity> finalActivities = new ArrayList<>();
        // For now, the filter is only on whatever identifiers are in HIDDEN_TASK_IDS
        for (ScheduledActivity activity : activities) {
            if (activity.getActivity() != null &&
                    activity.getActivity().getTask() != null &&
                    activity.getActivity().getTask().getIdentifier() != null) {
                if (!HIDDEN_TASK_IDS.contains(activity.getActivity().getTask().getIdentifier())) {
                    finalActivities.add(activity);
                }
            } else {
                finalActivities.add(activity);
            }
        }

        return finalActivities;
    }

    private SchedulesAndTasksModel translateActivities(@NonNull List<ScheduledActivity> activityList) {
        // first, group activities by day
        Map<Integer, List<ScheduledActivity>> activityMap = new HashMap<>();
        for (ScheduledActivity sa : activityList) {
            int day = sa.getScheduledOn().dayOfYear().get();
            List<ScheduledActivity> actList = activityMap.get(day);
            if (actList == null) {
                actList = new ArrayList<>();
                actList.add(sa);
                activityMap.put(day, actList);
            } else {
                actList.add(sa);
            }
        }

        SchedulesAndTasksModel model = new SchedulesAndTasksModel();
        model.schedules = new ArrayList<>();
        for (int day : activityMap.keySet()) {
            List<ScheduledActivity> aList = activityMap.get(day);
            ScheduledActivity temp = aList.get(0);

            SchedulesAndTasksModel.ScheduleModel sm = new SchedulesAndTasksModel.ScheduleModel();
            sm.scheduleType = "once";
            sm.scheduledOn = temp.getScheduledOn().toLocalDate().toDate();
            model.schedules.add(sm);
            sm.tasks = new ArrayList<>();

            for (ScheduledActivity sa : aList) {
                SchedulesAndTasksModel.TaskScheduleModel tsm = new SchedulesAndTasksModel
                        .TaskScheduleModel();
                tsm.taskTitle = sa.getActivity().getLabel();
                tsm.taskCompletionTime = sa.getActivity().getLabelDetail();
                if (sa.getActivity().getTask() != null) {
                    tsm.taskID = sa.getActivity().getTask().getIdentifier();
                }
                tsm.taskIsOptional = sa.getPersistent();
                tsm.taskType = sa.getActivity().getType();
                sm.tasks.add(tsm);
            }
        }

        return model;
    }

    public class CrfTask extends SchedulesAndTasksModel.TaskScheduleModel {
        public Date scheduledOn;

        public CrfTask() {

        }

        public CrfTask(SchedulesAndTasksModel.TaskScheduleModel tsm) {
            taskTitle = tsm.taskTitle;
            taskID = tsm.taskID;
            taskFileName = tsm.taskFileName;
            taskClassName = tsm.taskClassName;
            taskIsOptional = tsm.taskIsOptional;
            taskType = tsm.taskType;
            taskCompletionTime = tsm.taskCompletionTime;
        }
    }
}
