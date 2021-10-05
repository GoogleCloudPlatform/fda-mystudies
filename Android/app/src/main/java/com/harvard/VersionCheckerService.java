/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.model.Apps;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionChecker;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;

public class VersionCheckerService extends Service implements ApiCall.OnAsyncRequestComplete {
  private static final int APPS_RESPONSE = 100;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }


  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    getAppsInfo();
    return Service.START_NOT_STICKY;
  }

  private void getAppsInfo() {
    ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
        new ParticipantDatastoreConfigEvent(
            "get",
            Urls.APPS + "?appId=" + AppConfig.APP_ID_VALUE,
            APPS_RESPONSE,
            this,
            Apps.class,
            new HashMap<String, String>(),
            null,
            null,
            false,
            this);
    RegisterUserEvent registerUserEvent = new RegisterUserEvent();
    registerUserEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performRegistration(registerUserEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == APPS_RESPONSE) {
      Apps apps = (Apps) response;
      if (apps != null && apps.getVersion().getAndroid().getLatestVersion() != null) {
        DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
        apps.setAppId(AppConfig.APP_ID_VALUE);
        dbServiceSubscriber.saveApps(this, apps);

        if (!Boolean.parseBoolean(apps.getVersion().getAndroid().getForceUpdate())) {
          String latestVer = apps.getVersion().getAndroid().getLatestVersion();
          if (!AppController.getHelperSharedPreference().readPreference(this, "latestVersion", "").equals(latestVer)) {
            AppController.getHelperSharedPreference().writePreference(this, "versionalert", "");
            AppController.getHelperSharedPreference().writePreference(this, "latestVersion", apps.getVersion().getAndroid().getLatestVersion());
          }

          if (!AppController.getHelperSharedPreference().readPreference(this, "versionalert", "").equalsIgnoreCase("done")) {

            Intent intent = new Intent();
            intent.setAction(BuildConfig.APPLICATION_ID);
            intent.putExtra("api", "success");
            intent.putExtra("latestVersion", apps.getVersion().getAndroid().getLatestVersion());
            intent.putExtra("force", apps.getVersion().getAndroid().getForceUpdate());
            sendBroadcast(intent);
          }
        } else {
          Intent intent = new Intent();
          intent.setAction(BuildConfig.APPLICATION_ID);
          intent.putExtra("api", "success");
          intent.putExtra("latestVersion", apps.getVersion().getAndroid().getLatestVersion());
          intent.putExtra("force", apps.getVersion().getAndroid().getForceUpdate());
          sendBroadcast(intent);
        }
      }
    }
    stopSelf();
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    Logger.info("VersionCheckerService", "Failed");
    stopSelf();
    Intent intent = new Intent();
    intent.setAction(BuildConfig.APPLICATION_ID);
    intent.putExtra("api", "fail");
    sendBroadcast(intent);
  }

}