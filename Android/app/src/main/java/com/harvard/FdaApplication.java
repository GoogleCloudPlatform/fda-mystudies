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

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.multidex.MultiDex;
import android.util.Base64;

import com.harvard.passcodemodule.PasscodeSetupActivity;
import com.harvard.studyappmodule.StudyModuleSubscriber;
import com.harvard.usermodule.UserModuleSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.AppVisibilityDetector;
import com.harvard.utils.Logger;
import com.harvard.webservicemodule.WebserviceSubscriber;

import io.realm.Realm;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class FdaApplication extends Application {
  private static FdaApplication instance;
  private FdaEventBusRegistry registry;
  private static String randomString;
  private static final String ALPHA_NUMERIC_STRING =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  public static final String NOTIFICATION_CHANNEL_ID_SERVICE = AppConfig.PackageName + ".service";
  public static final String NOTIFICATION_CHANNEL_ID_INFO = AppConfig.PackageName + ".general";

  public static FdaApplication getInstance() {
    return instance;
  }

  @Override
  public void onCreate() {
    instance = this;
    super.onCreate();
    dbInitialize();
    initChannel();
    randomAlphaNumeric(50);
    startEventProcessing();

    AppVisibilityDetector.init(
        FdaApplication.this,
        new AppVisibilityDetector.AppVisibilityCallback() {
          @Override
          public void onAppGotoForeground() {
            if (!AppController.isMyServiceRunning(getInstance(), VersionCheckerService.class)) {
              startService(new Intent(getInstance(), VersionCheckerService.class));
            }
            if (AppController.getHelperSharedPreference()
                .readPreference(
                    getApplicationContext(), getResources().getString(R.string.usepasscode), "")
                .equalsIgnoreCase("yes")) {
              AppController.getHelperSharedPreference()
                  .writePreference(getApplicationContext(), "passcodeAnswered", "no");
              Intent intent = new Intent(getApplicationContext(), PasscodeSetupActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
              startActivity(intent);
            }
          }

          @Override
          public void onAppGotoBackground() {
            // app is from foreground to background

          }
        });
  }

  private void dbInitialize() {
    Realm.init(this);
  }

  private void startEventProcessing() {
    registry = new FdaEventBusRegistry();
    registry.registerDefaultSubscribers();
    registry.registerSubscriber(new StudyModuleSubscriber());
    registry.registerSubscriber(new UserModuleSubscriber());
    registry.registerSubscriber(new WebserviceSubscriber());
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    instance = null;
    registry.unregisterAllSubscribers();
    registry = null;
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  public void initChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      nm.createNotificationChannel(
          new NotificationChannel(
              NOTIFICATION_CHANNEL_ID_SERVICE,
              "App Service",
              NotificationManager.IMPORTANCE_LOW));
      nm.createNotificationChannel(
          new NotificationChannel(
              NOTIFICATION_CHANNEL_ID_INFO, "General", NotificationManager.IMPORTANCE_HIGH));
    }
  }

  public static void randomAlphaNumeric(int count) {
    StringBuilder builder = new StringBuilder();
    while (count-- != 0) {
      int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
      builder.append(ALPHA_NUMERIC_STRING.charAt(character));
    }
    randomString = builder.toString();
  }

  public static String getRandomString() {
    return randomString;
  }

  public static String getCodeChallenge(String codeVerifier) {
    String codeChallenge = "";
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
      codeChallenge =
          Base64.encodeToString(digest, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    } catch (Exception e) {
      Logger.log(e);
    }
    return codeChallenge;
  }
}
