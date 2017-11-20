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

package org.sagebase.crf.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.sagebionetworks.bridge.researchstack.CrfPrefs;

import java.util.Date;
import java.util.List;

/**
 * Created by TheMDP on 11/19/17.
 *
 * This broadcast receiver is called when the user has changed the time or time zone of their device
 * If we do not listen for these and re-schedule our reminders, they will never fire
 */

public class CrfRescheduleRemindersReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(CrfReminderManager.LOG_TAG, "Received reschedule intent with action: " + intent.getAction());
        List<Date> reminderDates = CrfPrefs.getInstance().getReminderDates();
        if (reminderDates == null) {
            return; // we never scheduled any reminders
        }
        CrfReminderManager.setReminderDates(context, reminderDates);
    }
}
