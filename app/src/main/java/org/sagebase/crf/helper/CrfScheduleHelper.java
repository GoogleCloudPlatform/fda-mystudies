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

package org.sagebase.crf.helper;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.model.SchedulesAndTasksModel.ScheduleModel;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by TheMDP on 11/17/17.
 */
public class CrfScheduleHelper {
    @VisibleForTesting
    static final DateTime NEVER = new DateTime(Long.MAX_VALUE);

    /**
     * Check if schedule applies at any point during an interval.
     *
     * @param interval interval
     * @param schedule schedule
     * @return true if schedule overlaps with an interval.
     */
    public static boolean isScheduledFor(@NonNull Interval interval,
                                         @NonNull ScheduleModel schedule) {
        checkNotNull(interval);
        checkNotNull(schedule);

        return interval.overlaps(getInterval(schedule));
    }

    /**
     * Checks if schedule applies to a time.
     *
     * @param dateTime time
     * @param schedule schedule
     * @return true if schedule applies to a time
     */
    public static boolean isScheduledFor(@NonNull DateTime dateTime,
                                         @NonNull ScheduleModel schedule) {
        checkNotNull(dateTime);
        checkNotNull(schedule);

        return getInterval(schedule).contains(dateTime);
    }

    /**
     * Checks if a schedule applies at any time during a day.
     *
     * @param localDate day
     * @param schedule  schedule
     * @return true if schedule overlaps with day
     */
    public static boolean isScheduledFor(@NonNull LocalDate localDate,
                                         @NonNull ScheduleModel schedule) {
        checkNotNull(localDate);
        checkNotNull(schedule);
        return isScheduledFor(localDate.toInterval(), schedule);
    }

    private static Interval getInterval(@NonNull ScheduleModel schedule) {
        DateTime scheduledOn = new DateTime(schedule.scheduledOn);
        DateTime expiresOn = NEVER;
        if (schedule.expiresOn != null) {
            expiresOn = new DateTime(schedule.expiresOn);
        }
        return new Interval(scheduledOn, expiresOn);
    }

    /**
     * This method contains the business logic for if a task is clickable
     *
     * @param task the task containing a single executable task
     * @return true if the task can be run by the user, false otherwise
     */
    public static boolean isTaskEnabled(SchedulesAndTasksModel.TaskScheduleModel task) {
        if (task == null) {
            return false;
        }
        return task.taskFinishedOn == null;
    }

    /**
     * @return true if every task in the schedule on this date is complete, false in all other
     * scenarios
     */
    public static boolean allTasksComplete(ScheduleModel schedule) {
        boolean allTasksComplete = true;
        if (schedule != null) {
            for (SchedulesAndTasksModel.TaskScheduleModel task : schedule.tasks) {
                if (task.taskFinishedOn == null) {
                    allTasksComplete = false;
                }
            }
        }
        return allTasksComplete;
    }
}
