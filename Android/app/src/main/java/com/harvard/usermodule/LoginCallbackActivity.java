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

package com.harvard.usermodule;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.notificationmodule.NotificationModuleSubscriber;
import com.harvard.studyappmodule.ChangePasswordActivity;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.studyappmodule.StudyInfoActivity;
import com.harvard.usermodule.event.GetUserProfileEvent;
import com.harvard.usermodule.event.LoginEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.webservicemodel.TokenData;
import com.harvard.usermodule.webservicemodel.UpdateUserProfileData;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;
import java.util.Calendar;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginCallbackActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {
  private static final int TOKENS_REQUEST = 100;
  private static final int UPDATE_USER_PROFILE = 101;
  private static final int USER_PROFILE_REQUEST = 102;
  private String userId;
  private String userAuth;
  private String code;
  private String accountStatus;
  private String emailId;
  UserProfileData userProfileData;
  private static final int PASSCODE_RESPONSE = 103;
  private static final int STUDYINFO_REQUEST = 100;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_standalone);
    handleIntent(getIntent());
  }

  public void handleIntent(Intent intent) {
    String appLinkAction = intent.getAction();
    Uri appLinkData = intent.getData();
    if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData != null) {
      Uri uri = intent.getData();
      if (uri != null) {
        userId = uri.getQueryParameter("userId");
        if (uri.getPath().equalsIgnoreCase("/mystudies/callback")) {
          code = uri.getQueryParameter("code");
          accountStatus = uri.getQueryParameter("accountStatus");
          AppController.getHelperProgressDialog().showProgress(this, "", "", false);

          HashMap<String, String> headers = new HashMap<>();
          headers.put("Content-Type", "application/x-www-form-urlencoded");
          headers.put("correlationId", FdaApplication.getRandomString());
          headers.put("appId", BuildConfig.APP_ID);
          headers.put("mobilePlatform", "ANDROID");

          HashMap<String, String> params = new HashMap<>();
          params.put("grant_type", "authorization_code");
          params.put("scope", "openid offline");
          params.put("redirect_uri", Urls.AUTH_SERVER_REDIRECT_URL);
          params.put("code_verifier", FdaApplication.getRandomString());
          params.put("code", code);
          params.put("userId", userId);

          AuthServerConfigEvent authServerConfigEvent =
              new AuthServerConfigEvent(
                  "post",
                  Urls.TOKENS,
                  TOKENS_REQUEST,
                  this,
                  TokenData.class,
                  params,
                  headers,
                  null,
                  false,
                  this);
          LoginEvent loginEvent = new LoginEvent();
          loginEvent.setAuthServerConfigEvent(authServerConfigEvent);
          UserModulePresenter userModulePresenter = new UserModulePresenter();
          userModulePresenter.performLogin(loginEvent);
        } else if (uri.getPath().equalsIgnoreCase("/mystudies/activation")) {
          emailId = uri.getQueryParameter("email");
          Intent verificationIntent =
              new Intent(LoginCallbackActivity.this, VerificationStepActivity.class);
          verificationIntent.putExtra("email", uri.getQueryParameter("email"));
          verificationIntent.putExtra("type", "signin");
          startActivity(verificationIntent);
          finish();
        }
      }
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == TOKENS_REQUEST) {
      TokenData tokenData = (TokenData) response;
      // FilterActivity Screen json object clearing
      AppController.getHelperSharedPreference()
          .writePreference(LoginCallbackActivity.this, getString(R.string.json_object_filter), "");

      userAuth = tokenData.getAccess_token();
      AppController.getHelperSharedPreference()
          .writePreference(
              LoginCallbackActivity.this,
              getString(R.string.refreshToken),
              tokenData.getRefresh_token());
      if (accountStatus != null && accountStatus.equalsIgnoreCase("3")) {
        Intent changePasswordIntent =
            new Intent(LoginCallbackActivity.this, ChangePasswordActivity.class);
        changePasswordIntent.putExtra("userid", userId);
        changePasswordIntent.putExtra("auth", userAuth);
        startActivity(changePasswordIntent);
        finish();
      } else {
        new GetFcmRefreshToken().execute();
      }
    } else if (responseCode == UPDATE_USER_PROFILE) {
      UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
      if (updateUserProfileData != null) {
        callUserProfileWebService();
      } else {
        Toast.makeText(
                this, getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT)
            .show();
        finish();
      }
    } else if (responseCode == USER_PROFILE_REQUEST) {
      userProfileData = (UserProfileData) response;
      if (userProfileData != null) {
        if (userProfileData.getSettings().isPasscode()) {
          AppController.getHelperSharedPreference()
              .writePreference(
                  LoginCallbackActivity.this, getString(R.string.initialpasscodeset), "no");

          AppController.getHelperSharedPreference()
              .writePreference(LoginCallbackActivity.this, getString(R.string.userid), "" + userId);
          AppController.getHelperSharedPreference()
              .writePreference(LoginCallbackActivity.this, getString(R.string.auth), "" + userAuth);
          AppController.getHelperSharedPreference()
              .writePreference(
                  LoginCallbackActivity.this,
                  getString(R.string.email),
                  "" + userProfileData.getProfile().getEmailId());

          Intent intent = new Intent(LoginCallbackActivity.this, NewPasscodeSetupActivity.class);
          intent.putExtra("from", "signin");
          startActivityForResult(intent, PASSCODE_RESPONSE);
        } else {
          login();
        }
      } else {
        Toast.makeText(
                this, getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT)
            .show();
        finish();
      }
    }
  }

  private void login() {
    AppController.getHelperProgressDialog().dismissDialog();
    AppController.getHelperSharedPreference()
        .writePreference(LoginCallbackActivity.this, getString(R.string.userid), "" + userId);
    AppController.getHelperSharedPreference()
        .writePreference(LoginCallbackActivity.this, getString(R.string.auth), "" + userAuth);
    AppController.getHelperSharedPreference()
        .writePreference(
            LoginCallbackActivity.this,
            getString(R.string.email),
            "" + userProfileData.getProfile().getEmailId());
    AppController.getHelperSharedPreference()
        .writePreference(LoginCallbackActivity.this, getString(R.string.verified), "true");
    if (userProfileData != null
        && (!userProfileData.getSettings().isLocalNotifications()
            || userProfileData.getSettings().isRemoteNotifications())) {
      NotificationModuleSubscriber notificationModuleSubscriber =
          new NotificationModuleSubscriber(null, null);
      notificationModuleSubscriber.generateNotificationTurnOffNotification(
          Calendar.getInstance().getTime(), LoginCallbackActivity.this);
    }
    if (SharedPreferenceHelper.readPreference(
            LoginCallbackActivity.this, getString(R.string.loginflow), "")
        .equalsIgnoreCase("StudyInfo")) {
      Intent intent = new Intent(LoginCallbackActivity.this, StudyInfoActivity.class);
      intent.putExtra("flow", "login_callback");
      intent.putExtra(
          "studyId",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_studyId", ""));
      intent.putExtra(
          "title",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_title", ""));
      intent.putExtra(
          "bookmark",
          Boolean.parseBoolean(
              SharedPreferenceHelper.readPreference(
                  LoginCallbackActivity.this, "login_studyinfo_bookmark", "")));
      intent.putExtra(
          "status",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_status", ""));
      intent.putExtra(
          "studyStatus",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_studyStatus", ""));
      intent.putExtra(
          "position",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_position", ""));
      intent.putExtra(
          "enroll",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_enroll", ""));
      intent.putExtra(
          "rejoin",
          SharedPreferenceHelper.readPreference(
              LoginCallbackActivity.this, "login_studyinfo_rejoin", ""));
      startActivity(intent);
    } else {
      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        Intent intent = new Intent(LoginCallbackActivity.this, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
      } else {
        Intent intent = new Intent(LoginCallbackActivity.this, StandaloneActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        mainIntent.putExtra("flow", "login_callback");
        startActivity(mainIntent);
        finish();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    finish();
  }

  private class GetFcmRefreshToken extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
      String token = "";
      if (FirebaseInstanceId.getInstance().getToken() == null
          || FirebaseInstanceId.getInstance().getToken().isEmpty()) {
        boolean regIdStatus = false;
        while (!regIdStatus) {
          token =
              AppController.getHelperSharedPreference()
                  .readPreference(LoginCallbackActivity.this, "deviceToken", "");
          if (!token.isEmpty()) {
            regIdStatus = true;
          }
        }
      } else {
        AppController.getHelperSharedPreference()
            .writePreference(
                LoginCallbackActivity.this,
                "deviceToken",
                FirebaseInstanceId.getInstance().getToken());
        token =
            AppController.getHelperSharedPreference()
                .readPreference(LoginCallbackActivity.this, "deviceToken", "");
      }
      return token;
    }

    @Override
    protected void onPostExecute(String token) {
      callUpdateProfileWebService(token);
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog()
          .showProgress(LoginCallbackActivity.this, "", "", false);
    }
  }

  private void callUpdateProfileWebService(String deviceToken) {
    AppController.getHelperProgressDialog().showProgress(LoginCallbackActivity.this, "", "", false);
    HashMap<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Bearer " + userAuth);
    headers.put("userId", userId);

    JSONObject jsonObjBody = new JSONObject();
    JSONObject infoJson = new JSONObject();
    try {
      infoJson.put("os", "android");
      infoJson.put("appVersion", BuildConfig.VERSION_NAME + "." + BuildConfig.VERSION_CODE);
      infoJson.put("deviceToken", deviceToken);

      jsonObjBody.put("info", infoJson);
    } catch (JSONException e) {
      Logger.log(e);
    }
    if (SharedPreferenceHelper.readPreference(LoginCallbackActivity.this, "logintype", "")
        .equalsIgnoreCase("signUp")) {
      JSONObject settingJson = new JSONObject();
      try {
        settingJson.put("passcode", true);
        settingJson.put("remoteNotifications", true);
        settingJson.put("localNotifications", true);
        jsonObjBody.put("settings", settingJson);
      } catch (JSONException e) {
        Logger.log(e);
      }
    }

    RegistrationServerConfigEvent registrationServerConfigEvent =
        new RegistrationServerConfigEvent(
            "post_object",
            Urls.UPDATE_USER_PROFILE,
            UPDATE_USER_PROFILE,
            this,
            UpdateUserProfileData.class,
            null,
            headers,
            jsonObjBody,
            false,
            this);
    UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();
    updateUserProfileEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
  }

  private void callUserProfileWebService() {
    HashMap<String, String> header = new HashMap<>();
    header.put("Authorization", "Bearer " + userAuth);
    header.put("userId", userId);

    GetUserProfileEvent getUserProfileEvent = new GetUserProfileEvent();
    RegistrationServerConfigEvent registrationServerConfigEvent =
        new RegistrationServerConfigEvent(
            "get",
            Urls.GET_USER_PROFILE,
            USER_PROFILE_REQUEST,
            LoginCallbackActivity.this,
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    login();
  }
}
