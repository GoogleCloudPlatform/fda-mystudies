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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import com.google.gson.Gson;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.offlinemodule.model.OfflineData;
import com.harvard.passcodemodule.PasscodeSetupActivity;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.usermodule.NewPasscodeSetupActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.GetUserProfileEvent;
import com.harvard.usermodule.event.LogoutEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.Settings;
import com.harvard.usermodule.webservicemodel.UpdateProfileRequestData;
import com.harvard.usermodule.webservicemodel.UpdateUserProfileData;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;
import io.realm.Realm;
import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONObject;

public class ProfileFragment extends Fragment
    implements ApiCall.OnAsyncRequestComplete, CompoundButton.OnCheckedChangeListener {
  private Context context;
  private AppCompatEditText firstName;
  private AppCompatEditText lastName;
  private AppCompatEditText email;
  private AppCompatTextView password;
  private Switch switchUsePasscode;
  private AppCompatTextView usePasscodeLabel;
  private Switch switchTouch;
  private AppCompatTextView touchIdLabel;
  private Switch switchRecvPushNotifctn;
  private AppCompatTextView recvPushNotifctnLabel;
  private Switch switchRecvStdyRemindr;
  private AppCompatTextView recvStdyActRemLabel;
  private RelativeLayout pickerReminderBtn;
  private AppCompatTextView pickerReminderLabel;
  private AppCompatTextView reminderLabel;
  private AppCompatTextView signOutButton;
  private AppCompatTextView deleteMyAccount;
  private AppCompatTextView firstNameLabel;
  private AppCompatTextView lastNameLabel;
  private AppCompatTextView emailLabel;
  private AppCompatTextView passwordLabel;
  private AppCompatTextView hrLine12;
  private AppCompatTextView passcode;
  private static final int USER_PROFILE_REQUEST = 6;
  private static final int UPDATE_USER_PROFILE_REQUEST = 7;
  private static final int LOGOUT_REPSONSECODE = 100;
  private static final int DELETE_ACCOUNT_REPSONSECODE = 101;
  private static final int PASSCODE_REPSONSE = 102;
  private static final int NEW_PASSCODE_REPSONSE = 103;
  private static final int CHANGE_PASSCODE_REPSONSE = 104;
  private static final int PASSCODE_CHANGE_REPSONSE = 105;
  private UserProfileData userProfileData = null;
  private UpdateProfileRequestData updateProfileRequestData = null;
  private static final int DELETE_ACCOUNT = 5;
  private int deleteIndexNumberDb;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_profile, container, false);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(context);
    initializeXmlId(view);
    setFont();
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    callUserProfileWebService();
    bindEvents();
    return view;
  }

  private void callUserProfileWebService() {
    HashMap<String, String> header = new HashMap<>();
    header.put(
        "Authorization",
        "Bearer "
            + AppController.getHelperSharedPreference()
                .readPreference(context, context.getString(R.string.auth), ""));
    header.put(
        "userId",
        AppController.getHelperSharedPreference()
            .readPreference(context, context.getString(R.string.userid), ""));
    GetUserProfileEvent getUserProfileEvent = new GetUserProfileEvent();
    RegistrationServerConfigEvent registrationServerConfigEvent =
        new RegistrationServerConfigEvent(
            "get",
            Urls.GET_USER_PROFILE,
            USER_PROFILE_REQUEST,
            context,
            UserProfileData.class,
            null,
            header,
            null,
            false,
            this);
    getUserProfileEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performGetUserProfile(getUserProfileEvent);
  }

  private void initializeXmlId(View view) {
    firstNameLabel = (AppCompatTextView) view.findViewById(R.id.first_name_label);
    firstName = (AppCompatEditText) view.findViewById(R.id.edittxt_first_name);
    lastNameLabel = (AppCompatTextView) view.findViewById(R.id.last_name_label);
    lastName = (AppCompatEditText) view.findViewById(R.id.edittxt_last_name);
    emailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
    email = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
    passwordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
    password = (AppCompatTextView) view.findViewById(R.id.edittxt_password);
    switchUsePasscode = (Switch) view.findViewById(R.id.switch_use_passcode);
    usePasscodeLabel = (AppCompatTextView) view.findViewById(R.id.use_passcode_label);
    switchTouch = (Switch) view.findViewById(R.id.switch_touch);
    touchIdLabel = (AppCompatTextView) view.findViewById(R.id.touch_id_label);
    switchRecvPushNotifctn = (Switch) view.findViewById(R.id.switch_recv_push_notifctn);
    recvPushNotifctnLabel = (AppCompatTextView) view.findViewById(R.id.recv_push_notifctn_label);
    switchRecvStdyRemindr = (Switch) view.findViewById(R.id.switch_recv_stdy_actrem);
    recvStdyActRemLabel = (AppCompatTextView) view.findViewById(R.id.recv_stdy_actrem_label);
    pickerReminderBtn = (RelativeLayout) view.findViewById(R.id.rel_picker_reminder);
    pickerReminderLabel = (AppCompatTextView) view.findViewById(R.id.picker_reminder);
    reminderLabel = (AppCompatTextView) view.findViewById(R.id.reminder_label);
    signOutButton = (AppCompatTextView) view.findViewById(R.id.signOutButton);
    deleteMyAccount = (AppCompatTextView) view.findViewById(R.id.deleteMyAccount);
    hrLine12 = (AppCompatTextView) view.findViewById(R.id.hrline12);
    passcode = (AppCompatTextView) view.findViewById(R.id.edittxt_passcode);

    disableEditText();
  }

  private void setFont() {
    try {
      firstNameLabel.setTypeface(AppController.getTypeface(context, "regular"));
      firstName.setTypeface(AppController.getTypeface(context, "regular"));
      lastNameLabel.setTypeface(AppController.getTypeface(context, "regular"));
      lastName.setTypeface(AppController.getTypeface(context, "regular"));
      emailLabel.setTypeface(AppController.getTypeface(context, "regular"));
      email.setTypeface(AppController.getTypeface(context, "regular"));
      passwordLabel.setTypeface(AppController.getTypeface(context, "regular"));
      password.setTypeface(AppController.getTypeface(context, "regular"));
      usePasscodeLabel.setTypeface(AppController.getTypeface(context, "regular"));
      touchIdLabel.setTypeface(AppController.getTypeface(context, "regular"));
      recvPushNotifctnLabel.setTypeface(AppController.getTypeface(context, "regular"));
      recvStdyActRemLabel.setTypeface(AppController.getTypeface(context, "regular"));
      reminderLabel.setTypeface(AppController.getTypeface(context, "regular"));
      signOutButton.setTypeface(AppController.getTypeface(context, "bold"));
      deleteMyAccount.setTypeface(AppController.getTypeface(context, "bold"));

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {

    pickerReminderBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (switchRecvStdyRemindr.isChecked()) {
              CustomDialogClass cdd =
                  new CustomDialogClass(((Activity) context), ProfileFragment.this);
              cdd.show();
            } else {
              Toast.makeText(context, R.string.remainder_settings, Toast.LENGTH_SHORT).show();
            }
          }
        });

    password.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(context, ChangePasswordActivity.class);
            intent.putExtra("from", "ProfileFragment");

            intent.putExtra(
                "userid",
                AppController.getHelperSharedPreference()
                    .readPreference(context, context.getString(R.string.userid), ""));
            intent.putExtra(
                "auth",
                AppController.getHelperSharedPreference()
                    .readPreference(context, context.getString(R.string.auth), ""));
            intent.putExtra(
                "verified",
                AppController.getHelperSharedPreference()
                    .readPreference(context, getString(R.string.verified), ""));
            intent.putExtra(
                "email",
                AppController.getHelperSharedPreference()
                    .readPreference(context, getString(R.string.email), ""));
            startActivity(intent);
          }
        });

    passcode.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            Intent intent = new Intent(context, PasscodeSetupActivity.class);
            intent.putExtra("from", "profile");
            startActivityForResult(intent, PASSCODE_CHANGE_REPSONSE);
          }
        });

    switchRecvPushNotifctn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            callUpdateUserProfileWebService(true, "mSwitchRecvPushNotifctn");
          }
        });

    switchRecvStdyRemindr.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            callUpdateUserProfileWebService(true, "mSwitchRecvStdyRemindr");
          }
        });

    if (AppController.getHelperSharedPreference()
        .readPreference(context, getString(R.string.usepasscode), "")
        .equalsIgnoreCase("yes")) {
      switchUsePasscode.setOnCheckedChangeListener(null);
      switchUsePasscode.setChecked(true);
      switchUsePasscode.setOnCheckedChangeListener(this);
      passcode.setEnabled(true);
      passcode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));

    } else {
      switchUsePasscode.setOnCheckedChangeListener(null);
      switchUsePasscode.setChecked(false);
      switchUsePasscode.setOnCheckedChangeListener(this);
      passcode.setEnabled(false);
      passcode.setTextColor(Color.LTGRAY);
    }
    switchUsePasscode.setEnabled(true);
    switchUsePasscode.setOnCheckedChangeListener(this);

    switchRecvPushNotifctn.setEnabled(true);
    switchRecvStdyRemindr.setEnabled(true);

    signOutButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (signOutButton
                .getText()
                .toString()
                .equalsIgnoreCase(getResources().getString(R.string.sign_out))) {
              logout();
            } else if (signOutButton
                .getText()
                .toString()
                .equalsIgnoreCase(getResources().getString(R.string.update))) {
              callUpdateUserProfileWebService(false, "mSwitchRecvPushNotifctn");
            }
          }
        });

    deleteMyAccount.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(context, DeleteAccountActivity.class);
            startActivityForResult(intent, DELETE_ACCOUNT);
          }
        });
  }

  private void logout() {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);

    HashMap<String, String> params = new HashMap<>();

    HashMap<String, String> header = new HashMap<String, String>();
    header.put(
        "Authorization",
        "Bearer " + SharedPreferenceHelper.readPreference(context, getString(R.string.auth), ""));
    header.put("correlationId", "" + FdaApplication.getRandomString());
    header.put("appId", "" + BuildConfig.APP_ID);
    header.put("mobilePlatform", "ANDROID");

    AuthServerConfigEvent authServerConfigEvent =
        new AuthServerConfigEvent(
            "post",
            Urls.AUTH_SERVICE
                + "/"
                + SharedPreferenceHelper.readPreference(context, getString(R.string.userid), "")
                + Urls.LOGOUT,
            LOGOUT_REPSONSECODE,
            context,
            LoginData.class,
            params,
            header,
            null,
            false,
            this);
    LogoutEvent logoutEvent = new LogoutEvent();
    logoutEvent.setAuthServerConfigEvent(authServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performLogout(logoutEvent);
  }

  public void enableEditText() {
    firstName.setEnabled(true);
    lastName.setEnabled(true);
    signOutButton.setText(getResources().getString(R.string.update));
    hrLine12.setVisibility(View.VISIBLE);
    deleteMyAccount.setVisibility(View.VISIBLE);

    switchUsePasscode.setEnabled(true);
    switchTouch.setEnabled(true);
    switchRecvPushNotifctn.setEnabled(true);
    switchRecvStdyRemindr.setEnabled(true);
    pickerReminderBtn.setEnabled(true);
    signOutButton.setVisibility(View.VISIBLE);
  }

  public void disableEditText() {
    firstName.setEnabled(false);
    lastName.setEnabled(false);
    signOutButton.setVisibility(View.GONE);
    signOutButton.setText(getResources().getString(R.string.sign_out));
    hrLine12.setVisibility(View.VISIBLE);
    hrLine12.setVisibility(View.GONE);
    deleteMyAccount.setVisibility(View.VISIBLE);

    switchUsePasscode.setEnabled(false);
    switchTouch.setEnabled(false);
    switchRecvPushNotifctn.setEnabled(false);
    switchRecvStdyRemindr.setEnabled(false);
    pickerReminderBtn.setEnabled(false);
    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
      ((StudyActivity) context).disableEditTextFromFragment();
    }
    if (userProfileData != null) {
      updateUI();
    }
    signOutButton.setVisibility(View.GONE);
  }

  public void updatePickerTime(String val) {
    pickerReminderLabel.setText(val);

    if (!val.equalsIgnoreCase("")) {
      String hours = val.split(":")[0];
      String minutes = val.split(":")[1];
      if (("" + hours).length() > 1 && ("" + minutes).length() > 1) {
        pickerReminderLabel.setText("" + hours + ":" + minutes);
      } else if (("" + hours).length() > 1) {
        pickerReminderLabel.setText("" + hours + ":0" + minutes);
      } else if (("" + minutes).length() > 1) {
        pickerReminderLabel.setText("0" + hours + ":" + minutes);
      } else {
        pickerReminderLabel.setText("0" + hours + ":0" + minutes);
      }
    }
  }

  @Override
  public void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    disableEditText();
    super.onDestroy();
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (USER_PROFILE_REQUEST == responseCode) {
      userProfileData = (UserProfileData) response;
      if (userProfileData != null) {
        updateUI();
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
      try {
        // if already having data then delete it (avoid duplication)
        dbServiceSubscriber.deleteUserProfileDataDuplicateRow(context);
        // save userProfileData to db
        dbServiceSubscriber.saveUserProfileData(context, userProfileData);
      } catch (Exception e) {
        Logger.log(e);
      }

    } else if (UPDATE_USER_PROFILE_REQUEST == responseCode) {
      Toast.makeText(
              context, getResources().getString(R.string.profile_updated), Toast.LENGTH_SHORT)
          .show();
      try {
        Realm realm = AppController.getRealmobj(context);
        realm.beginTransaction();
        userProfileData
            .getSettings()
            .setLocalNotifications(updateProfileRequestData.getSettings().isLocalNotifications());
        userProfileData
            .getSettings()
            .setPasscode(updateProfileRequestData.getSettings().isPasscode());
        userProfileData
            .getSettings()
            .setRemindersTime(updateProfileRequestData.getSettings().getRemindersTime());
        userProfileData
            .getSettings()
            .setRemoteNotifications(updateProfileRequestData.getSettings().isRemoteNotifications());
        userProfileData
            .getSettings()
            .setTouchId(updateProfileRequestData.getSettings().isTouchId());
        realm.commitTransaction();
        dbServiceSubscriber.closeRealmObj(realm);
        // save userProfileData to db
        dbServiceSubscriber.saveUserProfileData(context, userProfileData);
        // delete offline row sync
        dbServiceSubscriber.deleteOfflineDataRow(context, deleteIndexNumberDb);
      } catch (Exception e) {
        Logger.log(e);
      }

    } else if (responseCode == LOGOUT_REPSONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        Toast.makeText(context, loginData.getMessage(), Toast.LENGTH_SHORT).show();
        NotificationModuleSubscriber notificationModuleSubscriber =
            new NotificationModuleSubscriber(dbServiceSubscriber, realm);
        notificationModuleSubscriber.cancelNotificationTurnOffNotification(context);
        SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
        settings.edit().clear().apply();
        // delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        AppController.deleteKey("passcode_" + pass);
        Toast.makeText(context, R.string.signed_out, Toast.LENGTH_SHORT).show();
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
          ((StudyActivity) context).loadstudylist();
        } else {
          ((SurveyActivity) context).signout();
        }
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    } else if (responseCode == DELETE_ACCOUNT_REPSONSECODE) {
      LoginData loginData = (LoginData) response;
      if (loginData != null) {
        Toast.makeText(
                context, getResources().getString(R.string.account_deletion), Toast.LENGTH_SHORT)
            .show();
        SharedPreferences settings = SharedPreferenceHelper.getPreferences(context);
        settings.edit().clear().apply();
        // delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null) {
          AppController.deleteKey("passcode_" + pass);
        }
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
          ((StudyActivity) context).loadstudylist();
        } else {
          ((SurveyActivity) context).signout();
        }
      } else {
        Toast.makeText(context, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    }
  }

  private void updateUI() {
    try {
      firstName.setText(userProfileData.getProfile().getFirstName());
      lastName.setText(userProfileData.getProfile().getLastName());
      email.setText(userProfileData.getProfile().getEmailId());

      switchRecvPushNotifctn.setEnabled(true);
      switchRecvPushNotifctn.setChecked(userProfileData.getSettings().isRemoteNotifications());

      switchRecvStdyRemindr.setEnabled(true);
      switchRecvStdyRemindr.setChecked(userProfileData.getSettings().isLocalNotifications());
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(context, errormsg);
    } else if (responseCode == USER_PROFILE_REQUEST) {
      userProfileData = dbServiceSubscriber.getUserProfileData(realm);
      if (userProfileData != null) {
        updateUI();
      } else {
        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      }
    } else if (UPDATE_USER_PROFILE_REQUEST == responseCode) {
      try {

        if (userProfileData != null) {
          Realm realm = AppController.getRealmobj(context);
          realm.beginTransaction();
          userProfileData
              .getSettings()
              .setLocalNotifications(updateProfileRequestData.getSettings().isLocalNotifications());
          userProfileData
              .getSettings()
              .setPasscode(updateProfileRequestData.getSettings().isPasscode());
          userProfileData
              .getSettings()
              .setRemindersTime(updateProfileRequestData.getSettings().getRemindersTime());
          userProfileData
              .getSettings()
              .setRemoteNotifications(
                  updateProfileRequestData.getSettings().isRemoteNotifications());
          userProfileData
              .getSettings()
              .setTouchId(updateProfileRequestData.getSettings().isTouchId());
          realm.commitTransaction();
          dbServiceSubscriber.closeRealmObj(realm);
          // save userProfileData to db
          dbServiceSubscriber.saveUserProfileData(context, userProfileData);
        }

      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  private void callUpdateUserProfileWebService(
      boolean isNotificationToggle, String notificationType) {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();

    if (isNotificationToggle
        && notificationType.equalsIgnoreCase("mSwitchRecvPushNotifctn")
        && switchRecvStdyRemindr.isChecked()
        && !switchRecvPushNotifctn.isChecked()) {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(dbServiceSubscriber, realm);
      notificationModuleSubscriber.generateNotificationTurnOffNotification(
          Calendar.getInstance().getTime(), context);
    } else if (isNotificationToggle
        && notificationType.equalsIgnoreCase("mSwitchRecvStdyRemindr")
        && !switchRecvStdyRemindr.isChecked()
        && switchRecvPushNotifctn.isChecked()) {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(dbServiceSubscriber, realm);
      notificationModuleSubscriber.generateNotificationTurnOffNotification(
          Calendar.getInstance().getTime(), context);
    } else if (isNotificationToggle
        && switchRecvStdyRemindr.isChecked()
        && switchRecvPushNotifctn.isChecked()) {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(dbServiceSubscriber, realm);
      notificationModuleSubscriber.cancelNotificationTurnOffNotification(context);
    }

    updateProfileRequestData = new UpdateProfileRequestData();
    Settings settings = new Settings();
    settings.setLocalNotifications(switchRecvStdyRemindr.isChecked());
    settings.setPasscode(switchUsePasscode.isChecked());
    settings.setRemoteNotifications(switchRecvPushNotifctn.isChecked());
    settings.setTouchId(switchTouch.isChecked());
    pickerReminderLabel.getText().toString();
    int time =
        (Integer.parseInt(pickerReminderLabel.getText().toString().split(":")[0]) * 60)
            + (Integer.parseInt(pickerReminderLabel.getText().toString().split(":")[1]));
    settings.setRemindersTime("" + time);

    updateProfileRequestData.setSettings(settings);
    Gson gson = new Gson();
    String json = gson.toJson(updateProfileRequestData);
    try {
      JSONObject obj = new JSONObject(json);

      HashMap<String, String> header = new HashMap<>();
      header.put(
          "Authorization",
          "Bearer "
              + AppController.getHelperSharedPreference()
                  .readPreference(context, getString(R.string.auth), ""));
      header.put(
          "userId",
          AppController.getHelperSharedPreference()
              .readPreference(context, getString(R.string.userid), ""));

      // offline data storing
      try {
        int number = dbServiceSubscriber.getUniqueID(realm);
        if (number == 0) {
          number = 1;
        } else {
          number += 1;
        }

        String userProfileId =
            AppController.getHelperSharedPreference()
                .readPreference(context, context.getString(R.string.userid), "");
        OfflineData offlineData = dbServiceSubscriber.getUserIdOfflineData(userProfileId, realm);
        if (offlineData != null) {
          number = offlineData.getNumber();
        }
        deleteIndexNumberDb = number;
        AppController.pendingService(
            context,
            number,
            "post_object",
            Urls.UPDATE_USER_PROFILE,
            "",
            obj.toString(),
            "RegistrationServer",
            userProfileId,
            "",
            "");
      } catch (Exception e) {
        Logger.log(e);
      }

      RegistrationServerConfigEvent registrationServerConfigEvent =
          new RegistrationServerConfigEvent(
              "post_object",
              Urls.UPDATE_USER_PROFILE,
              UPDATE_USER_PROFILE_REQUEST,
              context,
              UpdateUserProfileData.class,
              null,
              header,
              obj,
              false,
              ProfileFragment.this);
      updateUserProfileEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == DELETE_ACCOUNT) {
      if (resultCode == ((Activity) context).RESULT_OK) {
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
          ((StudyActivity) context).loadstudylist();
        } else {
          ((SurveyActivity) context).signout();
        }
      }
    } else if (requestCode == PASSCODE_REPSONSE) {
      if (resultCode == ((Activity) context).RESULT_OK) {
        callUpdateUserProfileWebService(false, "mSwitchRecvPushNotifctn");
        // delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null) {
          AppController.deleteKey("passcode_" + pass);
        }

        AppController.getHelperSharedPreference()
            .writePreference(context, getString(R.string.usepasscode), "no");
        passcode.setEnabled(false);
        passcode.setTextColor(Color.LTGRAY);
      } else {
        AppController.getHelperSharedPreference()
            .writePreference(context, getString(R.string.usepasscode), "yes");
        passcode.setEnabled(true);
        passcode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));
        switchUsePasscode.setOnCheckedChangeListener(null);
        switchUsePasscode.setChecked(true);
        switchUsePasscode.setOnCheckedChangeListener(this);
      }
    } else if (requestCode == NEW_PASSCODE_REPSONSE) {
      if (resultCode != ((Activity) context).RESULT_OK) {
        switchUsePasscode.setOnCheckedChangeListener(null);
        switchUsePasscode.setChecked(false);
        switchUsePasscode.setOnCheckedChangeListener(this);
        // delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null) {
          AppController.deleteKey("passcode_" + pass);
        }
        AppController.getHelperSharedPreference()
            .writePreference(context, getString(R.string.usepasscode), "no");
        passcode.setEnabled(false);
        passcode.setTextColor(Color.LTGRAY);
      } else {
        callUpdateUserProfileWebService(false, "mSwitchRecvPushNotifctn");
        AppController.getHelperSharedPreference()
            .writePreference(context, getString(R.string.usepasscode), "yes");
        passcode.setEnabled(true);
        passcode.setTextColor(getResources().getColor(R.color.colorSecondaryStatBar));
        switchUsePasscode.setOnCheckedChangeListener(null);
        switchUsePasscode.setChecked(true);
        switchUsePasscode.setOnCheckedChangeListener(this);
      }
    } else if (requestCode == CHANGE_PASSCODE_REPSONSE) {
      if (resultCode == ((Activity) context).RESULT_OK) {
        Toast.makeText(context, "Passcode updated", Toast.LENGTH_SHORT).show();
      }
    } else if (requestCode == PASSCODE_CHANGE_REPSONSE) {
      if (resultCode == ((Activity) context).RESULT_OK) {
        Intent intent = new Intent(((Activity) context), NewPasscodeSetupActivity.class);
        intent.putExtra("from", "profile_change");
        startActivityForResult(intent, CHANGE_PASSCODE_REPSONSE);
      }
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      Intent intent = new Intent(((Activity) context), NewPasscodeSetupActivity.class);
      intent.putExtra("from", "profile");
      startActivityForResult(intent, NEW_PASSCODE_REPSONSE);
    } else {
      Intent intent = new Intent(context, PasscodeSetupActivity.class);
      intent.putExtra("from", "profile");
      startActivityForResult(intent, PASSCODE_REPSONSE);
    }
  }
}
