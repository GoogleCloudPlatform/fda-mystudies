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

package com.harvard.offlinemodule.auth;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import com.harvard.R;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.StudyModulePresenter;
import com.harvard.studyappmodule.events.ProcessResponseEvent;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.ActiveTaskService;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreEnrollmentConfigEvent;
import com.harvard.webservicemodule.events.ResponseDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class SyncAdapter extends AbstractThreadedSyncAdapter
    implements ApiCall.OnAsyncRequestComplete {

  private Context context;
  private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
  private DbServiceSubscriber dbServiceSubscriber;

  public SyncAdapter(Context context, boolean autoInitialize) {
    super(context, autoInitialize);
    this.context = context;
    dbServiceSubscriber = new DbServiceSubscriber();
  }

  @Override
  public void onPerformSync(
      Account account,
      Bundle extras,
      String authority,
      ContentProviderClient contentProviderClient,
      SyncResult syncResult) {

    if (!isMyServiceRunning(ActiveTaskService.class)) {
      Intent intent = new Intent(context, ActiveTaskService.class);
      intent.putExtra("SyncAdapter", "yes");
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent);
      } else {
        context.startService(intent);
      }
    }
  }

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service :
        manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  private void getPendingData() {
    try {
      dbServiceSubscriber = new DbServiceSubscriber();
      Realm realm = AppController.getRealmobj(context);
      RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(realm);
      if (!results.isEmpty()) {
        for (int i = 0; i < results.size(); i++) {
          String httpMethod = results.get(i).getHttpMethod();
          String url = results.get(i).getUrl();
          String jsonObject = results.get(i).getJsonParam();
          String serverType = results.get(i).getServerType();
          updateServer(httpMethod, url, jsonObject, serverType);
          break;
        }
      } else {
        dbServiceSubscriber.closeRealmObj(realm);
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void updateServer(
      String httpMethod, String url, String jsonObjectString, String serverType) {

    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(jsonObjectString);
    } catch (JSONException e) {
      Logger.log(e);
    }

    if (serverType.equalsIgnoreCase("ParticipantDatastoreEnrollment")) {
      HashMap<String, String> header = new HashMap();
      header.put(
          "auth",
          AppController.getHelperSharedPreference()
              .readPreference(context, context.getResources().getString(R.string.auth), ""));
      header.put(
          "userId",
          AppController.getHelperSharedPreference()
              .readPreference(context, context.getResources().getString(R.string.userid), ""));

      UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
      ParticipantDatastoreEnrollmentConfigEvent participantDatastoreEnrollmentConfigEvent =
          new ParticipantDatastoreEnrollmentConfigEvent(
              httpMethod,
              url,
              UPDATE_USERPREFERENCE_RESPONSECODE,
              context,
              LoginData.class,
              null,
              header,
              jsonObject,
              false,
              this);
      updatePreferenceEvent.setParticipantDatastoreEnrollmentConfigEvent(
          participantDatastoreEnrollmentConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    } else if (serverType.equalsIgnoreCase("ResponseDatastore")) {
      HashMap<String, String> header = new HashMap();
      header.put(
          "auth",
          AppController.getHelperSharedPreference()
              .readPreference(context, context.getResources().getString(R.string.auth), ""));
      header.put(
          "userId",
          AppController.getHelperSharedPreference()
              .readPreference(context, context.getResources().getString(R.string.userid), ""));
      ProcessResponseEvent processResponseEvent = new ProcessResponseEvent();
      ResponseDatastoreConfigEvent responseDatastoreConfigEvent =
          new ResponseDatastoreConfigEvent(
              httpMethod,
              url,
              UPDATE_USERPREFERENCE_RESPONSECODE,
              context,
              LoginData.class,
              null,
              header,
              jsonObject,
              false,
              this);

      processResponseEvent.setResponseDatastoreConfigEvent(responseDatastoreConfigEvent);
      StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
      studyModulePresenter.performProcessResponse(processResponseEvent);
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
      dbServiceSubscriber.removeOfflineData(context);
      getPendingData();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {}
}
