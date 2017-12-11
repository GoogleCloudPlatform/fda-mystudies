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

import org.joda.time.DateTime;
import org.junit.Test;
import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.sagebase.crf.helper.CrfScheduleHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CrfScheduleHelperTest {

    private static final DateTime TASK_FINSHED_ON = DateTime.parse("2017-11-11T12:56:37.361Z");

    @Test
    public void scheduleIsEnabledIncompleteSchedule_test1() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {null});
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertTrue(isScheduleEnabled);
    }

    @Test
    public void scheduleIsEnabledIncompleteSchedule_test2() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {TASK_FINSHED_ON, null});
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertTrue(isScheduleEnabled);
    }

    @Test
    public void scheduleIsEnabledIncompleteScheduleWrongDay_test1() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {TASK_FINSHED_ON, null});
        schedule.scheduledOn = TASK_FINSHED_ON.toDate(); // incomplete clinics in the past should be enabled
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertTrue(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledCompleteSchedule_test1() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {TASK_FINSHED_ON});
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertFalse(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledCompleteSchedule_test2() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {TASK_FINSHED_ON, TASK_FINSHED_ON});
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertFalse(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledWrongDay_test1() {
        // Tests the past
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {null});
        schedule.scheduledOn = TASK_FINSHED_ON.toDate(); // must be today to be clickable for a single task
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertFalse(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledWrongDay_test2() {
        // Tests the future
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {null});
        schedule.scheduledOn = DateTime.now().plusDays(2).toDate();
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertFalse(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledWrongDay_test3() {
        // Tests the future
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {null, null});
        schedule.scheduledOn = DateTime.now().plusDays(2).toDate();
        boolean isScheduleEnabled = CrfScheduleHelper.isScheduleEnabled(schedule);
        assertFalse(isScheduleEnabled);
    }

    private SchedulesAndTasksModel.ScheduleModel scheduleWith(String[] id, DateTime[] finishedOn) {
        SchedulesAndTasksModel.ScheduleModel schedule = new SchedulesAndTasksModel.ScheduleModel();
        schedule.scheduledOn = new Date(); // scheduled today
        List<SchedulesAndTasksModel.TaskScheduleModel> tasks = new ArrayList<>();
        assertEquals(id.length, finishedOn.length);
        for (int i = 0; i < id.length; i++) {
            SchedulesAndTasksModel.TaskScheduleModel task = new SchedulesAndTasksModel.TaskScheduleModel();
            task.taskID = id[i];
            if (finishedOn[i] != null) {
                task.taskFinishedOn = finishedOn[i].toDate();
            }
            tasks.add(task);
        }
        schedule.tasks = tasks;
        return schedule;
    }
}
