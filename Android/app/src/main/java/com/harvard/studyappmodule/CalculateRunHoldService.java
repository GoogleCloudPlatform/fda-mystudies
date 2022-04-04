/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.utils.AppController;

public class CalculateRunHoldService extends Service {
  PowerManager.WakeLock wakeLock;
  private Thread thread;
  Context context;

  @Override
  public void onCreate() {
    super.onCreate();
    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getPackageName());
    wakeLock.acquire();
  }

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    context = this;
    Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
    Notification notification =
        new NotificationCompat.Builder(this)
            .setContentTitle(getResources().getString(R.string.app_name))
            .setTicker("Study Setup")
            .setContentText("Setting up study content for you")
            .setChannelId(FdaApplication.NOTIFICATION_CHANNEL_ID_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
            .setOngoing(true)
            .build();

    startForeground(103, notification);

    Runnable r =
        new Runnable() {
          public void run() {
              while (true) {
                if (AppController.getHelperSharedPreference()
                    .readPreference(getApplicationContext(), "runsCalculating", "false")
                    .equalsIgnoreCase("false")) {
                  Log.e("runsCalculating", "done");
                  stopSelf();
                  break;
                }
              }
          }
        };
    thread = new Thread(r);
    thread.start();

    return Service.START_NOT_STICKY;
  }


  @Override
  public void onDestroy() {
    if (wakeLock.isHeld())
      wakeLock.release();
    if (thread != null && thread.isAlive()) {
      thread.interrupt();
    }
    super.onDestroy();
  }
}
