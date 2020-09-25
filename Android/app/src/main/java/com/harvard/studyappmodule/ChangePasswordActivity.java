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

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.ChangePasswordEvent;
import com.harvard.usermodule.webservicemodel.ChangePasswordData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private static final int CHANGE_PASSWORD_REQUEST = 8;
  private RelativeLayout backBtn;
  private RelativeLayout relpassword;
  private AppCompatTextView title;
  private AppCompatEditText oldPassword;
  private AppCompatEditText confirmPassword;
  private AppCompatEditText newPassword;
  private AppCompatTextView oldPasswordLabel;
  private AppCompatTextView confirmPasswordLabel;
  private AppCompatTextView newPasswordLabel;
  private AppCompatTextView submitButton;
  private String from = null;
  private String userId;
  private String auth;
  private String password;
  private boolean isVerified;
  private String emailId;
  private boolean clicked;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userId = getIntent().getStringExtra("userid");
    auth = getIntent().getStringExtra("auth");
    isVerified = getIntent().getBooleanExtra("verified", false);
    emailId = getIntent().getStringExtra("email");
    try {
      password = getIntent().getStringExtra("password");
    } catch (Exception e) {
      password = "";
      Logger.log(e);
    }
    setContentView(R.layout.activity_change_password);
    clicked = false;
    from = getIntent().getStringExtra("from");
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    relpassword = (RelativeLayout) findViewById(R.id.rel_password);
    title = (AppCompatTextView) findViewById(R.id.title);
    oldPasswordLabel = (AppCompatTextView) findViewById(R.id.oldpassword_label);
    newPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label_new);
    confirmPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label_confirm);
    oldPassword = (AppCompatEditText) findViewById(R.id.edittxt_oldpassword);
    newPassword = (AppCompatEditText) findViewById(R.id.edittxt_password_new);
    confirmPassword = (AppCompatEditText) findViewById(R.id.edittxt_password_confirm);
    submitButton = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setTextForView() {
    relpassword.setVisibility(View.VISIBLE);
    title.setText(getResources().getString(R.string.change_password_heading));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "medium"));
      oldPasswordLabel.setTypeface(
          AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      newPasswordLabel.setTypeface(
          AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      confirmPasswordLabel.setTypeface(
          AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      oldPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      newPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      confirmPassword.setTypeface(
          AppController.getTypeface(ChangePasswordActivity.this, "regular"));
      submitButton.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            onBackPressed();
          }
        });

    submitButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (!clicked) {
              clicked = true;
              String passwordPattern =
                  "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,64}$";
              if (newPassword.getText().toString().equalsIgnoreCase("")
                  && oldPassword.getText().toString().equalsIgnoreCase("")
                  && confirmPassword.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.enter_all_field_empty),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (oldPassword.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.password_old_empty),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (newPassword.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.password_new_empty),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (!newPassword.getText().toString().matches(passwordPattern)) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.password_validation),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (checkPasswordContainsEmailID(newPassword.getText().toString())) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.password_contain_email),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (confirmPassword.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.confirm_password_empty),
                        Toast.LENGTH_SHORT)
                    .show();
              } else if (!confirmPassword
                  .getText()
                  .toString()
                  .equals(newPassword.getText().toString())) {
                Toast.makeText(
                        ChangePasswordActivity.this,
                        getResources().getString(R.string.password_mismatch_error1),
                        Toast.LENGTH_SHORT)
                    .show();
              } else {
                AppController.getHelperProgressDialog()
                    .showProgress(ChangePasswordActivity.this, "", "", false);
                callChangePasswordWebService();
              }
              new Handler()
                  .postDelayed(
                      new Runnable() {
                        @Override
                        public void run() {
                          clicked = false;
                        }
                      },
                      2000);
            }
          }
        });
  }

  private boolean checkPasswordContainsEmailID(String password) {
    if (password.contains(emailId)) {
      return true;
    } else {
      return false;
    }
  }

  private void callChangePasswordWebService() {

    HashMap<String, String> header = new HashMap<>();
    header.put("Authorization", "Bearer " + auth);
    header.put("correlation_id", FdaApplication.getRandomString());
    header.put("appId", BuildConfig.APP_ID);
    header.put("mobilePlatform", "ANDROID");

    JSONObject params = new JSONObject();
    try {
      params.put("currentPassword", oldPassword.getText().toString());
      params.put("newPassword", newPassword.getText().toString());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    AuthServerConfigEvent authServerConfigEvent =
        new AuthServerConfigEvent(
            "put",
            Urls.AUTH_SERVICE + userId + Urls.CHANGE_PASSWORD,
            CHANGE_PASSWORD_REQUEST,
            ChangePasswordActivity.this,
            ChangePasswordData.class,
            null,
            header,
            params,
            false,
            ChangePasswordActivity.this);
    ChangePasswordEvent changePasswordEvent = new ChangePasswordEvent();
    changePasswordEvent.setAuthServerConfigEvent(authServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performChangePassword(changePasswordEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    try {
      AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
    } catch (Exception e) {
      Logger.log(e);
    }
    if (from != null && from.equalsIgnoreCase("ProfileFragment")) {
      Toast.makeText(
              this, getResources().getString(R.string.password_change_message), Toast.LENGTH_SHORT)
          .show();
      finish();
    } else {
      Toast.makeText(
              this,
              getResources().getString(R.string.password_change_message_signin),
              Toast.LENGTH_SHORT)
          .show();
      AppController.forceSignout(ChangePasswordActivity.this);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(ChangePasswordActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(ChangePasswordActivity.this, errormsg);
    } else {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onBackPressed() {
    try {
      AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
    } catch (Exception e) {
      Logger.log(e);
    }
    if (from != null && from.equalsIgnoreCase("ProfileFragment")) {
      finish();
    } else {
      SharedPreferences settings =
          SharedPreferenceHelper.getPreferences(ChangePasswordActivity.this);
      settings.edit().clear().apply();
      // delete passcode from keystore
      String pass = AppController.refreshKeys("passcode");
      if (pass != null) {
        AppController.deleteKey("passcode_" + pass);
      }
      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        Intent intent = new Intent(ChangePasswordActivity.this, GatewayActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
      } else {
        Intent intent = new Intent(ChangePasswordActivity.this, StandaloneActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
      }
    }
  }
}
