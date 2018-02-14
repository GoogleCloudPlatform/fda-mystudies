package org.sagebionetworks.bridge.researchstack;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;
import org.researchstack.backbone.AppPrefs;
import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.DataResponse;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.storage.NotificationHelper;
import org.sagebase.crf.reminder.CrfReminderManager;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.researchstack.wrapper.StorageAccessWrapper;
import org.sagebionetworks.bridge.rest.model.Message;
import org.sagebionetworks.bridge.rest.model.ScheduleStatus;
import org.sagebionetworks.bridge.rest.model.ScheduledActivity;
import org.sagebionetworks.bridge.rest.model.ScheduledActivityListV4;
import org.sagebionetworks.bridge.rest.model.StudyParticipant;
import org.sagebionetworks.bridge.rest.model.TaskReference;
import org.sagebionetworks.bridge.rest.model.UserSessionInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static com.google.common.base.Verify.verify;
import static org.sagebionetworks.bridge.android.util.ScheduledActivityUtil.TO_TASK_IDENTIFIER;
import static org.sagebionetworks.bridge.android.util.ScheduledActivityUtil.groupBySchedulePlan;

/**
 * Created by TheMDP on 12/12/16.
 */

public class CrfDataProvider extends BridgeDataProvider {

    private static final String LOG_TAG = CrfDataProvider.class.getCanonicalName();

    public static final String CLINIC1 = "clinic1";
    public static final String CLINIC2 = "clinic2";
    public static final String TEST_USER = "test_user";
    public static final String UX_TESTER = "ux_tester";

    public static final String ERROR_MISSING_CLINIC_DATA_GROUP = "Error: could not find both " +
            "clinic1 and clinic2, are you in the correct data groups?";
    public static final String ERROR_FOUND_MORE_THAN_TWO_CLINIC_SCHEDULES = "Found more than two " +
            "schedulePlans with multiple activities";

    public static final Set<String> CLINIC_SCHEDULE_TRIGGER_TASKS_IDS =
            ImmutableSet.of(CLINIC1, CLINIC2);
    public static final Set<String> TEST_DATA_GROUPS = ImmutableSet.of(TEST_USER, UX_TESTER);

    public static final ImmutableSet<String> CLINIC_DAY_1_SCHEDULE_PLAN_GUIDS =
            ImmutableSet.of(
                    "531a0bf8-647e-4dff-bdc7-eb37ec22e0d4", // clinic1 day 1
                    "9229a6c2-6ae1-4f7e-9fa8-d8a005e1808f" // clinic2 day 1
            );
    public static final ImmutableSet<String> CLINIC_DAY_14_SCHEDULE_PLAN_GUIDS =
            ImmutableSet.of(
                    "c419ac3f-bfc2-431d-b2bd-07a5f45baa30", // clinic1 day 14
                    "27a4a04a-f4fb-47e3-bd96-e8152216873a" // clinic2 day 14
            );
    public static final ImmutableSet<String> CLINIC_SCHEDULE_PLAN_GUIDS =
            ImmutableSet.<String>builder()
                    .addAll(CLINIC_DAY_1_SCHEDULE_PLAN_GUIDS)
                    .addAll(CLINIC_DAY_14_SCHEDULE_PLAN_GUIDS)
                    .build();

    public static final int STUDY_DURATION_IN_DAYS = 15;

    public String getExternalId(Context context) {
        String email = DataProvider.getInstance().getUserEmail(context);
        String externalIdFormat = bridgeConfig.getExternalIdEmailFormat();
        int indexOfExternalId = externalIdFormat.indexOf("%s");
        int lengthOfExternalId = (email.length() - externalIdFormat.length()) + "%s".length();
        return email.substring(indexOfExternalId, indexOfExternalId + lengthOfExternalId);
    }

    /**
     * Originally, the business logic was to assign a random clinic to the user behind the scenes,
     * but now we want the user to select their clinic during onboarding
     * If true, this will make sure the clinic was assigned during onboarding,
     * If false, the legacy clinic assignment will be used
     */
    private boolean shouldThrowErrorWithoutClinicDataGroup = true;

    public boolean isShouldThrowErrorWithoutClinicDataGroup() {
        return shouldThrowErrorWithoutClinicDataGroup;
    }

    public void setShouldThrowErrorWithoutClinicDataGroup(boolean shouldThrowErrorWithoutClinicDataGroup) {
        this.shouldThrowErrorWithoutClinicDataGroup = shouldThrowErrorWithoutClinicDataGroup;
    }

    public static final String NO_CLINIC_ERROR_MESSAGE = "NO_CLINIC_ID";

    /**
     * Hold onto weak context for reminders instead of passing it around the getCrfActivities
     * algorithm
     */
    private WeakReference<Context> weakContext;

    public CrfDataProvider() {
        super(BridgeManagerProvider.getInstance());
    }

    @VisibleForTesting
    CrfDataProvider(ResearchStackDAO researchStackDAO, StorageAccessWrapper storageAccessWrapper,
                    TaskHelper taskHelper) {
        super(researchStackDAO, storageAccessWrapper, taskHelper);
    }

    @Override
    public TaskHelper createTaskHelper(NotificationHelper notif, StorageAccessWrapper wrapper,
                                       BridgeManagerProvider provider) {
        return new CrfTaskHelper(wrapper, ResourceManager.getInstance(), AppPrefs.getInstance(),
                notif, provider);
    }

    @Override
    public Observable<DataResponse> signOut(Context context) {
        CrfReminderManager.cancelAllReminders(context, CrfPrefs.getInstance().getReminderDates());
        CrfPrefs.getInstance().clear();
        return super.signOut(context);
    }

    @Override
    public void processInitialTaskResult(Context context, TaskResult taskResult) {
        // no-op
    }

    /**
     * This method hides the complex logic of the CRF scheduling system
     * and simply returns the activities, or an error if something went wrong
     *
     * @param context, must be non-null on first call, used to set reminders for the activities
     * @param listener the callback listener for the events
     */
    public void getCrfActivities(@Nullable Context context,
                                 @NonNull final CrfActivitiesListener listener) {
        getCrfActivities(true, context, listener);
    }

    @VisibleForTesting
    void getCrfActivities(boolean filterByShouldDisplay, @Nullable Context context,
                          @NonNull final CrfActivitiesListener listener) {
        // Keep a reference to context for setting reminders once this method completes
        if (context != null) {
            weakContext = new WeakReference<>(context);
        }

        DateTime clinicDate = null;
        if (getCrfPrefs().hasClinicDate()) {
            clinicDate = getCrfPrefs().getClinicDate();
        } else if (isNoScheduleTester()){
            logV("No sign in date detected");
            // sign in date is used to retrieve clinic schedules, which are based on sign-in
            // ACTIVITY_TESTER receives persistent tasks, and has no clinic date
            clinicDate = DateTime.now();
            getCrfPrefs().setClinicDate(clinicDate);
        }

        if (clinicDate == null) {
            findOrCreateClinicDate(listener);
            // getCrfActivities method will be called again when sign in date is found, so return
            // here
            return;
        }

        DateTime endTimeForAllActivities = clinicDate.plusDays(STUDY_DURATION_IN_DAYS)
        .minusHours(1);

        logV(String.format(Locale.getDefault(),
                "Previous sign in date detected %s", clinicDate.toString()));
        // We have already done the clinic setup process, and can safely grab the schedules
        getActivitiesSubscribe(clinicDate, endTimeForAllActivities,
                activityList -> {
            displayActivities(filterByShouldDisplay, activityList.getItems(), listener);
        }, throwable -> listener.error(throwable.getLocalizedMessage()));
    }

    void displayActivities(boolean filterByShouldDisplay,
                           @NonNull List<ScheduledActivity> activityList,
                           @NonNull final CrfActivitiesListener listener) {
        logV("Raw Activities:");
        debugPrintActivities(activityList);

        List<ScheduledActivity> filteredActivities = activityList;
        if (filterByShouldDisplay) {
            filteredActivities = Lists.newArrayList(
                    Iterables.filter(activityList, this::shouldDisplay));
            logV("Filtered Activities:");
            debugPrintActivities(filteredActivities);
        }

        SchedulesAndTasksModel model = translateActivities(filteredActivities);

        // Set reminders for CRF app
        if (weakContext != null && weakContext.get() != null) {
            setReminders(weakContext.get(), model);
            weakContext = null;
        }

        listener.success(model);
    }

    /**
     * @return true if user is a TEST_USER and not a UX_TESTER and should see only test tasks,
     *         false otherwise, and normal app behavior should be followed
     */
    public boolean isNoScheduleTester() {
        // test users come in two types. ux_testers should see normal ux, non
        // UX_TESTER (often marked with ACTIVITY_TESTER) receive persistent tasks
        return getLocalDataGroups().contains(TEST_USER) &&
                !getLocalDataGroups().contains(UX_TESTER);
    }

    // region clinic schedule trigger completion
    /**
     * A first sign in date is needed to get the study's activities
     * The first sign in date represents the finished on date of either CLINIC1 or CLINIC2
     * activities
     * This method will try and find the clinic activities to see if one of them is already finished
     * Or it may continue trying to find the sign in date, or trigger the process to create one
     *
     * @param listener the listener for success/fail response
     */
    @VisibleForTesting
    void findOrCreateClinicDate(final CrfActivitiesListener listener) {
        logV("findOrCreateClinicDate");

        DateTime endTimeForClinicActivities = addTime(DateTime.now(), 1, 0);
        getActivitiesSubscribe(DateTime.now(), endTimeForClinicActivities, activityList -> {
            // We are only interested in the clinic1 and clinic2 activities

            List<ScheduledActivity> clinicScheduleTriggers =
                    Lists.newArrayList(
                            Iterables.filter(activityList.getItems(),
                            Predicates.compose(
                                    Predicates.in(CLINIC_SCHEDULE_TRIGGER_TASKS_IDS),
                                    TO_TASK_IDENTIFIER)));

            if (clinicScheduleTriggers.size() != 2) {
                listener.error(ERROR_MISSING_CLINIC_DATA_GROUP);
                return;
            }

            // throws if multiple clinics found
            ScheduledActivity myTriggeredClinicSchedule =
                    Iterables.getOnlyElement(
                            Iterables.filter(
                                    clinicScheduleTriggers,
                                    Predicates.compose(
                                            Predicates.notNull(),
                                            ScheduledActivity::getFinishedOn)),
                            null);

            // Whichever clinic activity is finished is the one this user is a part of
            if (myTriggeredClinicSchedule != null) {  // Found date, go back to loading activities
                logV(String.format(Locale.getDefault(),
                        "Setting firstSignInDate on clinic1 = %s",
                        myTriggeredClinicSchedule.getFinishedOn().toString()));
                getCrfPrefs().setClinicDate(myTriggeredClinicSchedule.getFinishedOn());
                getCrfActivities(null, listener);
            } else {
                List<ScheduledActivity> clinicDay1Activities =
                        getClinicDay1Activities(activityList.getItems());

                boolean clinicDay1ActivitiesFinished = Iterables.all(clinicDay1Activities,
                        Predicates.compose(
                                Predicates.equalTo(ScheduleStatus.FINISHED),
                                ScheduledActivity::getStatus));

                if (!clinicDay1ActivitiesFinished) {
                    // display unfinished activities
                    displayActivities(false, clinicDay1Activities, listener);
                    return;
                }
                // They've completed their first clinic visit, so assign their clinic
                // group
                findClinicScheduleTrigger(listener, clinicScheduleTriggers);
            }
        }, throwable -> listener.error(throwable.getLocalizedMessage()));
    }

    @VisibleForTesting
    void getActivitiesSubscribe(DateTime start, DateTime end,
                                final Action1<ScheduledActivityListV4> onNext,
                                final Action1<Throwable> onError) {
        getActivities(start, end).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext,
                onError);
    }

    /**
     * @param listener the listener for success/fail response
     * @param clinicActivities the clinic activities to trigger the clinic schedule
     */
    private void findClinicScheduleTrigger(
            final CrfActivitiesListener listener,
            List<ScheduledActivity> clinicActivities) {

        logV("findClinicScheduleTrigger");

        // First let's check if the user has a clinic data group already
        // This check also allows the server to pre-populate clinic groups
        Set<String> dataGroups = Sets.newHashSet(
                BridgeManagerProvider.getInstance()
                        .getAuthenticationManager()
                        .getUserSessionInfo()
                        .getDataGroups());

        if (Sets.intersection(dataGroups, CLINIC_SCHEDULE_TRIGGER_TASKS_IDS).size() != 1) {
            listener.error("NO_CLINIC_ID");
            return;
        }

        ScheduledActivity myClinic = Iterables.find(
                clinicActivities,
                Predicates.compose(
                        Predicates.in(dataGroups),
                        TO_TASK_IDENTIFIER));

        // We already have the clinic data group assigned, so simply read it and
        // trigger the corresponding clinic activity schedule flow
        logV("Completing " + myClinic.toString());

        // These fields are what is needed to trigger the completion of an activity
        final DateTime completed = createClinicCompletionDate();
        myClinic.setStartedOn(completed);
        myClinic.setFinishedOn(completed);

        updateActivitySubscribe(myClinic, message -> {
            logV("Completed clinic successful");
            // We have completed the clinic activity which will automatically
            // trigger the app's clinic schedules and we can simply pull activities now
            getCrfPrefs().setClinicDate(completed);
            getCrfActivities(null, listener);
        }, throwable -> listener.error(throwable.getLocalizedMessage()));
    }

    // endregion

    @VisibleForTesting
    void getStudyParticipantSubscribe(final Action1<StudyParticipant> onNext,
                                      final Action1<Throwable> onError) {
        getStudyParticipant().observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    @VisibleForTesting
    void updateStudyParticipantSubscribe(StudyParticipant studyParticipant,
                                         final Action1<UserSessionInfo> onNext,
                                         final Action1<Throwable> onError) {
        updateStudyParticipant(studyParticipant).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    @VisibleForTesting
    void signOutSubscribe(Context context,
                          final Action1<DataResponse> onNext,
                          final Action1<Throwable> onError) {
        signOut(context).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext, onError);
    }

    @VisibleForTesting
    void updateActivitySubscribe(ScheduledActivity activity,
                                 final Action1<Message> onNext,
                                 final Action1<Throwable> onError) {
        updateActivity(activity).observeOn(AndroidSchedulers.mainThread()).subscribe(onNext,
                onError);
    }

    @VisibleForTesting
    DateTime createClinicCompletionDate() {
        return DateTime.now();
    }

    @VisibleForTesting
    CrfPrefs getCrfPrefs() {
        return CrfPrefs.getInstance();
    }

    @VisibleForTesting
    void logV(String msg) {
        Log.v(LOG_TAG, msg);
    }

    @VisibleForTesting
    void logE(String msg) {
        Log.e(LOG_TAG, msg);
    }

    /**
     * @param activityList to search through
     * @param identifier   of the activity to return
     * @return activity with identifier, or null if none was found
     */
    @VisibleForTesting
    ScheduledActivity findActivity(@NonNull ScheduledActivityListV4 activityList,
                                   @NonNull String identifier) {
        return Iterables.tryFind(
                activityList.getItems(),
                Predicates.compose(Predicates.equalTo(identifier), TO_TASK_IDENTIFIER)
        ).orNull();
    }

    DateTime addTime(DateTime dateTime, int days, int hours) {
        return dateTime.plusDays(days).plusHours(hours);
    }

    /**
     * @param activityList the raw activityList returned from the server
     * @return a filtered list of activities for the purpose of CRF
     */
    public List<ScheduledActivity> filterResults(ScheduledActivityListV4 activityList) {
        if (activityList == null || activityList.getItems() == null) {
            return Collections.emptyList();
        }
        List<ScheduledActivity> activities = new ArrayList<>(activityList.getItems());
        List<ScheduledActivity> filteredActivities = Lists.newArrayListWithCapacity(activities.size());

        for (ScheduledActivity activity : activities) {
            if (shouldDisplay(activity)) {
                filteredActivities.add(activity);
            }
        }

        return filteredActivities;
    }

    @VisibleForTesting
    boolean shouldDisplay(ScheduledActivity scheduledActivity) {
        if (scheduledActivity.getActivity() == null) {
            return false;
        }

        boolean isPersistent = scheduledActivity.getPersistent() != null && scheduledActivity
                .getPersistent();
        boolean isSurvey = scheduledActivity.getActivity().getSurvey() != null;

        // don't show hidden tasks, e.g. clinic1 and clinic2 activities programatically used as
        // event/trigger
        TaskReference task = scheduledActivity.getActivity().getTask();
        boolean isDisplayableTask = task != null
                && task.getIdentifier() != null
                && !CLINIC_SCHEDULE_TRIGGER_TASKS_IDS.contains(task.getIdentifier());

        return isPersistent || isSurvey || isDisplayableTask;
    }

    @VisibleForTesting
    void setReminders(Context context, SchedulesAndTasksModel model) {
        // Set reminders

        // JOLIU CHECK
        List<Date> reminderDates = new ArrayList<>();
        for (SchedulesAndTasksModel.ScheduleModel schedule : model.schedules) {
            if (schedule.scheduledOn != null) {
                reminderDates.add(schedule.scheduledOn);
            }
        }
        CrfReminderManager.setReminderDates(context, reminderDates);
    }

    @NonNull
    List<ScheduledActivity> getClinicDay1Activities(@NonNull List<ScheduledActivity>
                                                            activityList) {
        Multimap<String, ScheduledActivity> clinicDay1SchedulesToActivities =
                Multimaps.filterKeys(
                        groupBySchedulePlan(activityList),
                        CLINIC_DAY_1_SCHEDULE_PLAN_GUIDS::contains);

        logV(clinicDay1SchedulesToActivities.toString());

        verify(clinicDay1SchedulesToActivities.keySet().size() <= 1,
                ERROR_FOUND_MORE_THAN_TWO_CLINIC_SCHEDULES);

        return Lists.newArrayList(clinicDay1SchedulesToActivities.values());
    }

    @NonNull
    @Override
    protected SchedulesAndTasksModel translateActivities(@NonNull List<ScheduledActivity>
                                                                     activityList) {
        SchedulesAndTasksModel model = super.translateActivities(activityList);

        DateTime clinicDateTime = getCrfPrefs().getClinicDate();

        if (clinicDateTime != null) {
            Date clinicDate = clinicDateTime.toDate();

            Set<String> day1TaskGuids = Sets.newHashSet(
                    Iterables.transform(
                            getClinicDay1Activities(activityList),
                            ScheduledActivity::getGuid));

            for (SchedulesAndTasksModel.ScheduleModel schedule : model.schedules) {
                // rewrite clinic day 1 to display clinic date instead of account creation date
                Set<String> scheduleTaskGuids = Sets.newHashSet(
                        Iterables.transform(schedule.tasks, tm -> tm.taskGUID));
                if (scheduleTaskGuids.containsAll(day1TaskGuids)) {
                    schedule.scheduledOn = clinicDate;
                }
                // set non-persistent tasks to expire after 2 days
                if ("once".equals(schedule.scheduleType)){
                    schedule.expiresOn = new DateTime(schedule.scheduledOn).plusDays(2).toDate();
                }
            }
        }

        // Sort in reverse time order per CRF journey screen requirements
        Collections.sort(model.schedules, (o1, o2) -> o2.scheduledOn.compareTo(o1.scheduledOn));

        return model;
    }

    @VisibleForTesting
    void debugPrintActivities(List<ScheduledActivity> activityList) {
        StringBuilder debugActivityList = new StringBuilder();
        for (ScheduledActivity activity : activityList) {
            if (activity.getPersistent() == null || !activity.getPersistent()) {
                if (activity.getActivity().getTask() == null) {
                    debugActivityList.append(activity.getActivity().getSurvey().getIdentifier());
                } else {
                    debugActivityList.append(activity.getActivity().getTask().getIdentifier());
                }
                debugActivityList.append(" on ");
                if (activity.getScheduledOn() != null) {
                    debugActivityList.append(activity.getScheduledOn().toString());
                }
                debugActivityList.append("\n");
            }
        }
        logV(debugActivityList.toString());
    }

    public interface CrfActivitiesListener {
        void success(SchedulesAndTasksModel model);

        void error(String localizedError);
    }
}
