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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.studymodel.DeleteAccountData;
import com.harvard.studyappmodule.studymodel.StudyHome;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Studies;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import com.harvard.webservicemodule.events.StudyDatastoreConfigEvent;
import io.realm.Realm;
import io.realm.RealmResults;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeleteAccountActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_delete_account);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
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
            finish();
          }
        });

    idisagree.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
          }
        });

    iagree.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
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
        SharedPreferences settings =
            SharedPreferenceHelper.getPreferences(DeleteAccountActivity.this);
        settings.edit().clear().apply();
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
}
