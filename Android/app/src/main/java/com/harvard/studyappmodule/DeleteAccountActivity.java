/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
 *
 */

package com.harvard.studyappmodule;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
import com.harvard.studyappmodule.studymodel.DeleteAccountData;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAccountActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete, NetworkChangeReceiver.NetworkChangeCallback {
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private View hrLine;
  private AppCompatTextView content;
  private AppCompatTextView iagree;
  private AppCompatTextView idisagree;
  private LinearLayout middleLayaout;
  private static final int DELETE_ACCOUNT_REPSONSECODE = 101;
  private ArrayList<String> studyIdList = new ArrayList<>();
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private CustomFirebaseAnalytics analyticsInstance;
  private TextView offlineIndicatior;
  private NetworkChangeReceiver networkChangeReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_delete_account);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
    checkAndCreateDataList();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    hrLine = findViewById(R.id.mHrLine);
    content = (AppCompatTextView) findViewById(R.id.mContent);
    iagree = (AppCompatTextView) findViewById(R.id.mIAgree);
    idisagree = (AppCompatTextView) findViewById(R.id.mIDisagree);
    middleLayaout = (LinearLayout) findViewById(R.id.middleLayaout);
    offlineIndicatior = findViewById(R.id.offlineIndicatior);
  }

  private void setTextForView() {
    title.setText(getResources().getString(R.string.confirmation));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "medium"));
      content.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
      iagree.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
      idisagree.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.delete_account_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });

    idisagree.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.delete_account_disagree));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });

    iagree.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.delete_account_agree));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            AppController.getHelperProgressDialog()
                .showProgress(DeleteAccountActivity.this, "", "", false);
            deactivateAccount();
          }
        });
  }

  private void checkAndCreateDataList() {
    try {
      // get all study id []
      RealmResults<Studies> realmStudies = dbServiceSubscriber.getAllStudyIds(realm);
      // study Ids are storing to studyIdList
      for (int i = 0; i < realmStudies.size(); i++) {
        studyIdList.add(realmStudies.get(i).getStudyId());
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == DELETE_ACCOUNT_REPSONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {

        try {
          dbServiceSubscriber.deleteDb(this);
        } catch (Exception e) {
          Logger.log(e);
        }

        Toast.makeText(
                DeleteAccountActivity.this,
                getResources().getString(R.string.account_deletion),
                Toast.LENGTH_SHORT)
            .show();
        SharedPreferenceHelper.deletePreferences(this);
        // delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null) {
          AppController.deleteKey("passcode_" + pass);
        }
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
      } else {
        Toast.makeText(DeleteAccountActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  public void deactivateAccount() {
    HashMap<String, String> header = new HashMap();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(
                    DeleteAccountActivity.this, getResources().getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(
                DeleteAccountActivity.this, getResources().getString(R.string.userid), ""));
    DeleteAccountEvent deleteAccountEvent = new DeleteAccountEvent();
    Gson gson = new Gson();
    DeleteAccountData deleteAccountData = new DeleteAccountData();
    String json = gson.toJson(deleteAccountData);
    JSONObject obj = null;
    try {
      obj = new JSONObject();
      JSONArray jsonArray1 = new JSONArray();
      if (studyIdList.size() > 0) {
        JSONObject jsonObject;
        for (int i = 0; i < studyIdList.size(); i++) {
          jsonObject = new JSONObject();
          jsonObject.put("studyId", studyIdList.get(i));

          jsonArray1.put(jsonObject);
        }
      }
      obj.put("studyData", jsonArray1);
    } catch (JSONException e) {
      Logger.log(e);
    }
    try {
      ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
          new ParticipantDatastoreConfigEvent(
              "delete_object",
              Urls.DELETE_ACCOUNT,
              DELETE_ACCOUNT_REPSONSECODE,
              DeleteAccountActivity.this,
              LoginData.class,
              null,
              header,
              obj,
              false,
              this);
      deleteAccountEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performDeleteAccount(deleteAccountEvent);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(DeleteAccountActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(DeleteAccountActivity.this, errormsg);
    } else {
      Toast.makeText(DeleteAccountActivity.this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      offlineIndicatior.setVisibility(View.VISIBLE);
      iagree.setClickable(false);
      iagree.setAlpha(0.5F);
      idisagree.setAlpha(0.5F);
      idisagree.setClickable(false);
    } else {
      offlineIndicatior.setVisibility(View.GONE);
      iagree.setClickable(true);
      iagree.setAlpha(1F);
      idisagree.setAlpha(1F);
      idisagree.setClickable(true);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }
}
