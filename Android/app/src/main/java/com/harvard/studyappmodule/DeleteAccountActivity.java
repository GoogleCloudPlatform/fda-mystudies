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
  private ArrayList<String> storeWithdrawalTypeDeleteFlag = new ArrayList<>();
  private ArrayList<String> studyIdList = new ArrayList<>();
  private ArrayList<String> studyTitleList = new ArrayList<>();
  private ArrayList<String> withdrawalTypeList = new ArrayList<>();
  private String noData = "nodata";
  private boolean noDataFlag = false;
  private int tempPos;
  private static final int STUDY_INFO = 10;
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
            boolean noDataAvailable = false;
            if (storeWithdrawalTypeDeleteFlag.size() > 0) {
              for (int i = 0; i < storeWithdrawalTypeDeleteFlag.size(); i++) {
                if (storeWithdrawalTypeDeleteFlag.get(i).equalsIgnoreCase(noData)) {
                  Toast.makeText(
                          DeleteAccountActivity.this,
                          getResources().getString(R.string.select_option),
                          Toast.LENGTH_LONG)
                      .show();
                  break;
                }
                if (i == (storeWithdrawalTypeDeleteFlag.size() - 1)) {
                  noDataAvailable = true;
                }
              }
            } else {
              noDataAvailable = true;
            }
            if (noDataAvailable) {
              AppController.getHelperProgressDialog()
                  .showProgress(DeleteAccountActivity.this, "", "", false);
              deactivateAccount();
            }
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

      for (int i = 0; i < studyIdList.size(); i++) {
        // get all study title, studyTitleList
        StudyList studyList = dbServiceSubscriber.getStudyTitle(studyIdList.get(i), realm);
        String title = null;
        if (studyList != null) {
          title = studyList.getTitle();
        }
        if (title == null || title.equalsIgnoreCase("")) {
          studyTitleList.add(noData);
        } else {
          studyTitleList.add(title);
        }
        // get all withdawalType, withdrawalTypeList[]
        try {
          StudyHome studyHome = dbServiceSubscriber.getWithdrawalType(studyIdList.get(i), realm);
          if (studyHome == null) {
            withdrawalTypeList.add(noData);
          } else {
            String type = studyHome.getWithdrawalConfig().getType();
            if (type == null || type.equalsIgnoreCase("")) {
              withdrawalTypeList.add(noData);
            } else {
              withdrawalTypeList.add(type);
            }
          }
        } catch (Exception e) {
          Logger.log(e);
        }
      }
      checkWithdrawalTypeListContainsNoData();
      if (noDataFlag) {
        setListLeaveStudy();
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void checkWithdrawalTypeListContainsNoData() {
    noDataFlag = false;
    for (int i = 0; i < withdrawalTypeList.size(); i++) {
      if (withdrawalTypeList.get(i).equalsIgnoreCase(noData)) {
        tempPos = i;
        // missing details to get eg: withdrawal type
        callGetStudyInfoWebservice();
        break;
      }
      if (i == (withdrawalTypeList.size() - 1)) {
        noDataFlag = true;
      }
    }
  }

  private void callGetStudyInfoWebservice() {
    AppController.getHelperProgressDialog().showProgress(DeleteAccountActivity.this, "", "", false);
    HashMap<String, String> header = new HashMap<>();
    String url = Urls.STUDY_INFO + "?studyId=" + studyIdList.get(tempPos);
    GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
    StudyDatastoreConfigEvent studyDatastoreConfigEvent =
        new StudyDatastoreConfigEvent(
            "get",
            url,
            STUDY_INFO,
            DeleteAccountActivity.this,
            StudyHome.class,
            null,
            header,
            null,
            false,
            this);

    getUserStudyInfoEvent.setStudyDatastoreConfigEvent(studyDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
  }

  private void setListLeaveStudy() {
    try {
      for (int i = 0; i < withdrawalTypeList.size(); i++) {
        final int pos = i;
        if (withdrawalTypeList.get(i).equalsIgnoreCase("ask_user")) {
          final View child2 = getLayoutInflater().inflate(R.layout.content_delete_account2, null);
          final AppCompatTextView mentalHealthSurveyTitle =
              (AppCompatTextView) child2.findViewById(R.id.mentalHealthSurveyTitle);
          final AppCompatCheckBox mDeleteButton1 =
              (AppCompatCheckBox) child2.findViewById(R.id.mDeleteButton1);
          final AppCompatCheckBox mRetainButton1 =
              (AppCompatCheckBox) child2.findViewById(R.id.mRetainButton1);
          // font setting
          mentalHealthSurveyTitle.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          mDeleteButton1.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          mRetainButton1.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          // value setting
          mentalHealthSurveyTitle.setText(studyTitleList.get(i));
          middleLayaout.addView(child2);
          storeWithdrawalTypeDeleteFlag.add(pos, noData);
          // click listner
          mDeleteButton1.setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mDeleteButton1.setChecked(true);
                  mRetainButton1.setChecked(false);
                  storeWithdrawalTypeDeleteFlag.set(pos, "true");
                }
              });
          mRetainButton1.setOnClickListener(
              new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  mRetainButton1.setChecked(true);
                  mDeleteButton1.setChecked(false);
                  storeWithdrawalTypeDeleteFlag.set(pos, "false");
                }
              });
        }
        if (withdrawalTypeList.get(i).equalsIgnoreCase("delete_data")) {
          final View child1 = getLayoutInflater().inflate(R.layout.content_delete_account1, null);
          final AppCompatTextView mMedicationSurveyTitle =
              (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyTitle);
          final AppCompatTextView mMedicationSurveyValue =
              (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyValue);
          // font setting
          mMedicationSurveyTitle.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          mMedicationSurveyValue.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          // value settings
          mMedicationSurveyTitle.setText(studyTitleList.get(i));
          mMedicationSurveyValue.setText(getResources().getString(R.string.response_deleted));
          middleLayaout.addView(child1);
          storeWithdrawalTypeDeleteFlag.add(pos, "true");
        } else if (withdrawalTypeList.get(i).equalsIgnoreCase("no_action")) {
          final View child1 = getLayoutInflater().inflate(R.layout.content_delete_account1, null);

          final AppCompatTextView mMedicationSurveyTitle =
              (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyTitle);
          final AppCompatTextView mMedicationSurveyValue =
              (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyValue);
          final RelativeLayout r1 =
              (RelativeLayout) child1.findViewById(R.id.activity_delete_account1);
          // font setting
          mMedicationSurveyTitle.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          mMedicationSurveyValue.setTypeface(
              AppController.getTypeface(DeleteAccountActivity.this, "regular"));
          // value setting
          mMedicationSurveyTitle.setText(studyTitleList.get(i));
          mMedicationSurveyValue.setText(getResources().getString(R.string.response_retained));
          middleLayaout.addView(child1);
          storeWithdrawalTypeDeleteFlag.add(pos, "false");
        }
      }
    } catch (Resources.NotFoundException e) {
      Logger.log(e);
    }
    hrLine.setVisibility(View.VISIBLE);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode != STUDY_INFO) {
      AppController.getHelperProgressDialog().dismissDialog();
    }
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
    } else if (responseCode == STUDY_INFO) {
      if (response != null) {
        StudyHome studyHome = (StudyHome) response;
        // adding withdrawal type
        withdrawalTypeList.set(tempPos, studyHome.getWithdrawalConfig().getType());
        // saving to db
        studyHome.setStudyId(studyIdList.get(tempPos));

        dbServiceSubscriber.saveStudyInfoToDB(this, studyHome);
      }
      checkWithdrawalTypeListContainsNoData();
      if (noDataFlag) {
        AppController.getHelperProgressDialog().dismissDialog();
        setListLeaveStudy();
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
          jsonObject.put("delete", storeWithdrawalTypeDeleteFlag.get(i));

          jsonArray1.put(jsonObject);
        }
      }
      obj.put("deleteData", jsonArray1);
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
    } else if (responseCode == STUDY_INFO) {
      Toast.makeText(DeleteAccountActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      finish();
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
