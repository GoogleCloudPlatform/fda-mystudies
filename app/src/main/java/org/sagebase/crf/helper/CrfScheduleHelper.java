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

import org.researchstack.backbone.model.SchedulesAndTasksModel;

import java.util.Date;

/**
 * Created by TheMDP on 11/17/17.
 */

public class CrfScheduleHelper {
    /**
     * This method contains the business logic for if a schedule is clickable
     * @param schedule the schedule containing the task group or single task
     * @return true if view should be clickable, false otherwise
     */
    public static boolean isScheduleEnabled(SchedulesAndTasksModel.ScheduleModel schedule) {
        if (schedule == null) {
            return false;
        }

        boolean isScheduledOnTodayOrBefore= CrfDateHelper.isToday(schedule.scheduledOn) ||
                new Date().after(schedule.scheduledOn);

        boolean isExpiresOnTodayOrAfter = schedule.expiresOn == null || CrfDateHelper.isToday
                (schedule.expiresOn) &&
                new Date().before(schedule.scheduledOn);

        return isScheduledOnTodayOrBefore && !allTasksCompleteOn(schedule) &&
                isExpiresOnTodayOrAfter;
//
//        boolean singleTaskSelectable = schedule.tasks.size() == 1 &&
//                !allTasksCompleteOn(schedule) &&
//                (CrfDateHelper.isToday(schedule.scheduledOn);
//
//        boolean isTodaySameDayOrAfterClinic =
//                CrfDateHelper.isToday(schedule.scheduledOn) ||
//                new Date().after(schedule.scheduledOn);
//
//        boolean clinicGroupSelectable = schedule.tasks.size() > 1 &&
//                !allTasksCompleteOn(schedule) &&
//                isTodaySameDayOrAfterClinic;
//
//        return singleTaskSelectable || clinicGroupSelectable;
    }

    /**
     * This method contains the business logic for if a task is clickable
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
     * @return true if every task in the schedule on this date is complete, false in all other scenarios
     */
    public static boolean allTasksCompleteOn(SchedulesAndTasksModel.ScheduleModel schedule) {
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
