/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.studyappmodule.StudyFragment;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.webservicemodel.StudyData;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.Random;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

  private NotificationManagerCompat notificationManager;
  public static String TYPE = "type";
  public static String SUBTYPE = "subtype";
  public static String STUDYID = "studyId";
  public static String AUDIENCE = "audience";
  private static String TITLE = "title";
  private static String MESSAGE = "message";
  private static String NOTIFICATION_INTENT = "notificationIntent";
  private static String LOCAL_NOTIFICATION = "localNotification";
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    handleNotification(remoteMessage);
    dbServiceSubscriber.closeRealmObj(realm);
  }

  private void handleNotification(RemoteMessage remoteMessage) {
    if (!AppController.getHelperSharedPreference()
        .readPreference(this, getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      AppController.getHelperSharedPreference()
          .writePreference(this, getString(R.string.notification), "true");

      Intent intent = new Intent();
      intent.setAction("com.fda.notificationReceived");
      sendBroadcast(intent);

      Intent notificationIntent;
      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        notificationIntent = new Intent(this, StudyActivity.class);
      } else {
        notificationIntent = new Intent(this, StandaloneActivity.class);
      }
      String type = "";
      try {
        type = remoteMessage.getData().get(TYPE);
      } catch (Exception e) {
        type = "";
        Logger.log(e);
      }
      String subtype = "";
      try {
        subtype = remoteMessage.getData().get(SUBTYPE);
      } catch (Exception e) {
        subtype = "";
        Logger.log(e);
      }
      String studyId = "";
      try {
        studyId = remoteMessage.getData().get(STUDYID);
      } catch (Exception e) {
        studyId = "";
        Logger.log(e);
      }
      String audience = "";
      try {
        audience = remoteMessage.getData().get(AUDIENCE);
      } catch (Exception e) {
        audience = "";
        Logger.log(e);
      }
      String title = "";
      try {
        title = remoteMessage.getData().get(TITLE);
      } catch (Exception e) {
        title = "";
        Logger.log(e);
      }
      String message = "";
      try {
        message = remoteMessage.getData().get(MESSAGE);
      } catch (Exception e) {
        message = "";
        Logger.log(e);
      }

      if (message.contains("has been resumed.")) {
        type = "Study";
        subtype = "Activity";
      }

      notificationIntent.putExtra(StudyActivity.FROM, NOTIFICATION_INTENT);

      notificationIntent.putExtra(TYPE, type);
      notificationIntent.putExtra(SUBTYPE, subtype);
      notificationIntent.putExtra(STUDYID, studyId);
      notificationIntent.putExtra(AUDIENCE, audience);
      notificationIntent.putExtra(LOCAL_NOTIFICATION, "");
      notificationIntent.putExtra(TITLE, title);
      notificationIntent.putExtra(MESSAGE, message);
      notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      Random random = new Random();
      int m = random.nextInt(9999 - 1000) + 1000;
      PendingIntent contentIntent =
          PendingIntent.getActivity(this, m, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

      if (type.equalsIgnoreCase("Study")) {

        realm = AppController.getRealmobj(this);
        Study study = dbServiceSubscriber.getStudyListFromDB(realm);
        StudyData studyData = dbServiceSubscriber.getStudyPreferencesListFromDB(realm);
        dbServiceSubscriber.closeRealmObj(realm);
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
          if (study != null) {
            RealmList<StudyList> studyListArrayList = study.getStudies();
            for (int j = 0; j < studyListArrayList.size(); j++) {
              if (studyId.equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                if (studyListArrayList
                        .get(j)
                        .getStudyStatus()
                        .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                  sendNotification(message, contentIntent);
                }
              }
            }
          }
        } else {
          if (studyData != null
                  && studyData.getStudies() != null
                  && studyData.getStudies().get(0) != null
                  && studyData.getStudies().get(0).getStatus() != null
                  && studyData
                  .getStudies()
                  .get(0)
                  .getStatus()
                  .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
            sendNotification(message, contentIntent);
          }
        }
      } else {
        sendNotification(message, contentIntent);
      }
    }
  }

  // This method is only generating push notification
  // It is same as we did in earlier posts
  private void sendNotification(String messageBody, PendingIntent pendingIntent) {
    if (notificationManager == null) {
      notificationManager = NotificationManagerCompat.from(AppFirebaseMessagingService.this);
    }
    realm = AppController.getRealmobj(this);
    UserProfileData userProfileData = dbServiceSubscriber.getUserProfileData(realm);
    boolean isNotification = true;
    if (userProfileData != null) {
      isNotification = userProfileData.getSettings().isRemoteNotifications();
    }
    dbServiceSubscriber.closeRealmObj(realm);
    int notifyIcon = R.mipmap.ic_launcher;
    Bitmap icon = BitmapFactory.decodeResource(getResources(), notifyIcon);
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
      Drawable drawable = getDrawable(notifyIcon);
      icon =
              Bitmap.createBitmap(
                      drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
      Canvas canvas = new Canvas(icon);
      drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
      drawable.draw(canvas);
    } else {
      icon = BitmapFactory.decodeResource(getResources(), notifyIcon);
    }
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    NotificationCompat.Builder notificationBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.icon)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setContentTitle(getResources().getString(R.string.app_name))
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setChannelId(FdaApplication.NOTIFICATION_CHANNEL_ID_INFO)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
            .setGroup("group");

    Notification notification = notificationBuilder.build();
    Random random = new Random();
    int m = random.nextInt(9999 - 1000) + 1000;
    if (isNotification) {
      notificationManager.notify(m, notification);
    }
  }
}
