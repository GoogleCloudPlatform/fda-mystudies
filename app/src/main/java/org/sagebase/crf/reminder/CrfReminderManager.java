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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by rianhouston on 11/13/17.
 */

public class CrfReminderManager {

    private static final String LOG_TAG = CrfReminderManager.class.getCanonicalName();

    public static final int DAILY_REMINDER_REQUEST_CODE = 2398;

    public static void setReminder(Context context,Class<?> cls, Date date) {
        Calendar calendar = Calendar.getInstance();
        Calendar setcalendar = Calendar.getInstance();
        setcalendar.setTime(date);
        setcalendar.set(Calendar.HOUR_OF_DAY, 8); // schedule for 8am?
        setcalendar.set(Calendar.MINUTE, 0);
        setcalendar.set(Calendar.SECOND, 0);
        // cancel already scheduled reminders
        cancelReminder(context,cls);

        // if date is in the past, skip it
        if(setcalendar.before(calendar)) {
            Log.d(LOG_TAG, "Skipping reminder for date: " + date.toString());
            return;
        } else {
            Log.d(LOG_TAG, "Setting reminder for date: " + date.toString());
        }

        // Enable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
            DAILY_REMINDER_REQUEST_CODE, intent1,
            PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
    }

    public static void cancelReminder(Context context, Class<?> cls) {
        // Disable a receiver
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
            DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
