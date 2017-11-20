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
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import org.sagebionetworks.bridge.researchstack.CrfPrefs;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by rianhouston on 11/13/17.
 */

public class CrfReminderManager {

    static final String LOG_TAG = CrfReminderManager.class.getCanonicalName();

    public static final int DAILY_REMINDER_REQUEST_CODE = 2398;

    private static final int DEFAULT_HOUR = 17; // Afternoon 5 PM
    private static final int DEFAULT_MINUTE = 0;

    /**
     * @param date will be used to pull hour of day and minute from to use when making reminders
     */
    public static void setReminderTimeHourAndMinute(Context context, Date date) {
        // Cancel all previous reminders
        CrfReminderManager.cancelAllReminders(context, CrfPrefs.getInstance().getReminderDates());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        Log.v(LOG_TAG, String.format("Setting reminder time to %d hours and %d minutes", hour, min));
        CrfPrefs.getInstance().setKeyReminderTimeHour(hour);
        CrfPrefs.getInstance().setKeyReminderTimeMinute(min);

        // Re-schedule with the new reminder dates
        setReminderDates(context, CrfPrefs.getInstance().getReminderDates());
    }

    /**
     * @param context used to schedule the alarm for the local notification
     * @param dateList the list of dates that alarms should be scheduled for
     */
    public static void setReminderDates(Context context, List<Date> dateList) {
        // cancel already scheduled reminders
        cancelAllReminders(context, dateList);

        if (dateList == null) {
            return;
        }

        enableReceiver(context, true);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            Log.e(LOG_TAG, "AlarmManager is null! Can't schedule alarms");
            return;
        }

        for (Date date : dateList) {
            Calendar setcalendar = Calendar.getInstance();
            setcalendar.setTime(date);

            int hour = CrfPrefs.getInstance().getReminderTimeHour(DEFAULT_HOUR);
            int minute = CrfPrefs.getInstance().getReminderTimeMinute(DEFAULT_MINUTE);

            setcalendar.set(Calendar.HOUR_OF_DAY, hour);
            setcalendar.set(Calendar.MINUTE, minute);
            setcalendar.set(Calendar.SECOND, 0);

            // if date is in the past, skip it
            if(setcalendar.before(Calendar.getInstance())) {
                Log.d(LOG_TAG, "Skipping reminder for date: " + date.toString());
            } else {
                Log.v(LOG_TAG,"Setting reminder for scheduled date: \n" +
                        "at reminder time " + setcalendar.getTime().toString() + "\n");
                PendingIntent pendingIntent = pendingIntent(context, date);
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    am.setExact(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                } else {
                    am.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);
                }
            }
        }

        // Save the reminder dates for later in case time or timezone is changed, we can re-schedule
        CrfPrefs.getInstance().setReminderDates(dateList);
    }

    private static void enableReceiver(Context context, boolean enable) {
        int state = enable ?
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        ComponentName receiver = new ComponentName(context, CrfAlarmReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, state, PackageManager.DONT_KILL_APP);
    }

    /**
     * @param context used to cancel the reminders
     * @param dateList list of dates that the reminders are scheduled on, need to be exact
     */
    public static void cancelAllReminders(Context context, List<Date> dateList) {
        if (dateList == null) {
            return;
        }

        enableReceiver(context, false);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for (Date date : dateList) {
            PendingIntent pendingIntent = pendingIntent(context, date);
            if (am != null) {
                am.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    private static PendingIntent pendingIntent(Context context, Date date) {
        Intent intent1 = new Intent(context, CrfAlarmReceiver.class);
        intent1.setData(Uri.parse(Long.toString(date.getTime())));
        return PendingIntent.getBroadcast(
                context, DAILY_REMINDER_REQUEST_CODE,
                intent1, PendingIntent.FLAG_ONE_SHOT);
    }
}
