/*
 *    Copyright 2018 Sage Bionetworks
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

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.model.SchedulesAndTasksModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.researchstack.backbone.model.SchedulesAndTasksModel.ScheduleModel;
import static org.sagebase.crf.helper.CrfScheduleHelper.isScheduledFor;

public class CrfScheduleHelperTest {

    private static final DateTime TASK_FINSHED_ON = DateTime.parse("2017-11-11T12:56:37.361Z");

    DateTime scheduledDateTime = DateTime.parse("2018-02-14T00:00:00.000Z");
    DateTime expiresOnDateTime = scheduledDateTime.plusDays(2);

    ScheduleModel scheduleNoExpiry;
    ScheduleModel scheduleWithExpiry;

    @Before
    public void setup() {
        scheduleNoExpiry= new ScheduleModel();
        scheduleNoExpiry.scheduledOn = scheduledDateTime.toDate();

        scheduleWithExpiry = new ScheduleModel();
        scheduleWithExpiry.scheduledOn = scheduledDateTime.toDate();
        scheduleWithExpiry.expiresOn = expiresOnDateTime.toDate();
    }

    @Test
    public void testIsScheduledForInterval_noExpiry() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.plusDays(100), scheduledDateTime.plusDays(101)),
                scheduleNoExpiry);

        assertTrue(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_noExpiry_interval_before() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.minusDays(5), scheduledDateTime.minusDays(3)),
                scheduleNoExpiry);

        assertFalse(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_scheduledOnEndOfInterval() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.minusDays(1), scheduledDateTime),
                scheduleNoExpiry);

        assertFalse(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_intervalContainsSchedule() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.minusDays(5), expiresOnDateTime.plusDays(3)),
                scheduleWithExpiry);

        assertTrue(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_intervalEndsInsideSchedule() {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.minusDays(1), expiresOnDateTime.minusDays(1)),
                scheduleWithExpiry);

        assertTrue(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_intervalBeginsInsideSchedule() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.plusDays(1), expiresOnDateTime.plusDays(1)),
                scheduleWithExpiry);

        assertTrue(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_expiresOnStartOfInterval() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(expiresOnDateTime, expiresOnDateTime.plusDays(3)),
                scheduleWithExpiry);

        assertFalse(isScheduledFor);
    }

    @Test
    public void testIsScheduledForDateTime_expiresOnStartOfInterval() throws ParseException {
        boolean isScheduledFor = isScheduledFor(
                new Interval(expiresOnDateTime, expiresOnDateTime.plusDays(3)),
                scheduleWithExpiry);

        assertFalse(isScheduledFor);
    }

    @Test
    public void testIsScheduledForInterval_scheduledOnInsideInterval() throws ParseException {
        DateTime scheduledDateTime = DateTime.parse("2018-02-14T00:00:00.000Z");

        ScheduleModel sm = new ScheduleModel();
        sm.scheduledOn = scheduledDateTime.toDate();

        boolean isScheduledFor = isScheduledFor(
                new Interval(scheduledDateTime.minusDays(1), scheduledDateTime), sm);

        assertFalse(isScheduledFor);
    }

    @Test
    public void scheduleIsEnabledIncompleteSchedule_test1() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {null});
        boolean isScheduleEnabled = isScheduledFor(new DateTime(), schedule);
        assertTrue(isScheduleEnabled);
    }

    @Test
    public void scheduleIsEnabledIncompleteSchedule_test2() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {TASK_FINSHED_ON, null});
        boolean isScheduleEnabled = isScheduledFor(new DateTime(), schedule);
        assertTrue(isScheduleEnabled);
    }

    @Test
    public void scheduleIsEnabledIncompleteScheduleWrongDay_test1() {
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {TASK_FINSHED_ON, null});
        schedule.scheduledOn = TASK_FINSHED_ON.toDate(); // incomplete clinics in the past should be enabled
        boolean isScheduleEnabled = isScheduledFor(new DateTime(), schedule);
        assertTrue(isScheduleEnabled);
    }

//    @Test
//    public void scheduleIsDisabledCompleteSchedule_test1() {
//        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
//                new String[] {"schedule1"},
//                new DateTime[] {TASK_FINSHED_ON});
//        boolean isScheduleEnabled = CrfScheduleHelper.isScheduledFor(new DateTime(), schedule);
//        assertFalse(isScheduleEnabled);
//    }
//
//    @Test
//    public void scheduleIsDisabledCompleteSchedule_test2() {
//        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
//                new String[] {"schedule1", "schedule2"},
//                new DateTime[] {TASK_FINSHED_ON, TASK_FINSHED_ON});
//        boolean isScheduleEnabled = CrfScheduleHelper.isScheduledFor(new DateTime(), schedule);
//        assertFalse(isScheduleEnabled);
//    }

//    @Test
//    public void scheduleIsDisabledWrongDay_test1() {
//        // Tests the past
//        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
//                new String[] {"schedule1"},
//                new DateTime[] {null});
//        schedule.scheduledOn = TASK_FINSHED_ON.toDate(); // must be today to be clickable for a single task
//        boolean isScheduleEnabled = CrfScheduleHelper.isScheduledFor(new DateTime(), schedule);
//        assertFalse(isScheduleEnabled);
//    }

    @Test
    public void scheduleIsDisabledWrongDay_test2() {
        // Tests the future
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1"},
                new DateTime[] {null});
        schedule.scheduledOn = DateTime.now().plusDays(2).toDate();
        boolean isScheduleEnabled = isScheduledFor(new DateTime(), schedule);
        assertFalse(isScheduleEnabled);
    }

    @Test
    public void scheduleIsDisabledWrongDay_test3() {
        // Tests the future
        SchedulesAndTasksModel.ScheduleModel schedule = scheduleWith(
                new String[] {"schedule1", "schedule2"},
                new DateTime[] {null, null});
        schedule.scheduledOn = DateTime.now().plusDays(2).toDate();
        boolean isScheduleEnabled = isScheduledFor(new DateTime(), schedule);
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
