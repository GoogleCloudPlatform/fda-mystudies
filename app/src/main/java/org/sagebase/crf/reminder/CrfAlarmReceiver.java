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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.sagebase.crf.CrfMainActivity;
import org.sagebionetworks.research.crf.R;

import java.util.Date;

/**
 * Created by rianhouston on 11/13/17.
 */

public class CrfAlarmReceiver extends BroadcastReceiver {
    
    private static final String NOTIFICATION_CHANNEL_ID = "CrfReminderManager";
    private static final String NOTIFICATION_CHANNEL_TITLE = "Cardiorespiratory Fitness Reminders";
    private static final String NOTIFICATION_CHANNEL_DESC =
            "Cardiorespiratory Fitness reminders to complete your activities.";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getData() != null) {
            Date date = new Date(Long.valueOf(intent.getDataString()));
            Log.v(CrfReminderManager.LOG_TAG, "Reminder received from " + date);
        }
        // Trigger the notification
        showNotification(context, CrfMainActivity.class, "CRF Reminder", "You have an activity to complete.");
    }

    public void showNotification(Context context, Class<?> cls, String title, String content) {
    
    // Starting with API 26, notifications must be contained in a channel
    if (Build.VERSION.SDK_INT >= 26) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_TITLE,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
      Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

      Intent notificationIntent = new Intent(context, cls);
      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

      TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
      stackBuilder.addParentStack(cls);
      stackBuilder.addNextIntent(notificationIntent);

      PendingIntent pendingIntent = stackBuilder.getPendingIntent(
              CrfReminderManager.DAILY_REMINDER_REQUEST_CODE,PendingIntent.FLAG_UPDATE_CURRENT);

      NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
      Notification notification = builder.setContentTitle(title)
              .setContentText(content).setAutoCancel(true)
              .setSound(alarmSound).setSmallIcon(R.mipmap.ic_launcher)
              .setContentIntent(pendingIntent).build();

      NotificationManager notificationManager = (NotificationManager)
              context.getSystemService(Context.NOTIFICATION_SERVICE);
      notificationManager.notify(CrfReminderManager.DAILY_REMINDER_REQUEST_CODE, notification);
    }
}
