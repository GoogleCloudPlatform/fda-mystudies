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

package com.harvard.usermodule;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.usermodule.event.ResendEmailEvent;
import com.harvard.usermodule.event.VerifyUserEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;

public class VerificationStepActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {
  private AppCompatTextView verificationStepsLabel;
  private AppCompatTextView verificationEmailMsgLabel;
  private AppCompatTextView tapBelowTxtLabel;
  private AppCompatTextView submitBtn;
  private AppCompatTextView hrLine1;
  private AppCompatTextView cancelTxt;
  private AppCompatTextView resend;
  private AppCompatEditText emailField;
  private AppCompatEditText verificationCode;
  private RelativeLayout backBtn;
  private RelativeLayout cancelBtn;
  private static final int CONFIRM_REGISTER_USER_RESPONSE = 100;
  private static final int RESEND_CONFIRMATION = 101;
  private static final int JOIN_STUDY_RESPONSE = 102;
  private String from;
  private String userId;
  private String auth;
  private String emailId;
  private String type;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verification_step);
    userId = getIntent().getStringExtra("userid");
    auth = getIntent().getStringExtra("auth");
    emailId = getIntent().getStringExtra("email");
    from = getIntent().getStringExtra("from");
    type = getIntent().getStringExtra("type");

    initializeXmlId();
    hrLine1.setVisibility(View.GONE);
    emailField.setVisibility(View.GONE);
    setTextForView();
    setFont();

    bindEvents();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    cancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
    resend = (AppCompatTextView) findViewById(R.id.resend);
    hrLine1 = (AppCompatTextView) findViewById(R.id.vrLine1);
    emailField = (AppCompatEditText) findViewById(R.id.emailField);
    verificationCode = (AppCompatEditText) findViewById(R.id.verificationCode);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    verificationStepsLabel = (AppCompatTextView) findViewById(R.id.verification_steps_label);
    verificationEmailMsgLabel = (AppCompatTextView) findViewById(R.id.verification_email_msg_label);
    tapBelowTxtLabel = (AppCompatTextView) findViewById(R.id.tap_below_txt_label);
    submitBtn = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setTextForView() {
    String msg = "";
    if (type.equalsIgnoreCase("signup")) {
      msg =
          getResources().getString(R.string.verification_email_content1)
              + " "
              + emailId
              + getResources().getString(R.string.verification_email_content2);
    } else if (type.equalsIgnoreCase("signin")) {
      msg = getResources().getString(R.string.verification_email_signin);
    } else {
      msg =
          getResources().getString(R.string.verification_email_forgotpassword)
              + "("
              + emailId
              + ")"
              + getResources().getString(R.string.verification_email_forgotpassword1);
    }
    verificationEmailMsgLabel.setText(msg);
  }

  private void setFont() {
    try {
      cancelTxt.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "medium"));
      verificationStepsLabel.setTypeface(
          AppController.getTypeface(VerificationStepActivity.this, "regular"));
      verificationEmailMsgLabel.setTypeface(
          AppController.getTypeface(VerificationStepActivity.this, "regular"));
      tapBelowTxtLabel.setTypeface(
          AppController.getTypeface(VerificationStepActivity.this, "regular"));
      submitBtn.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
      emailField.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
      verificationCode.setTypeface(
          AppController.getTypeface(VerificationStepActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SharedPreferences settings =
                SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null) {
              AppController.deleteKey("passcode_" + pass);
            }
            finish();
          }
        });

    cancelBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SharedPreferences settings =
                SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null) {
              AppController.deleteKey("passcode_" + pass);
            }
            finish();
          }
        });

    submitBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            VerifyUserEvent verifyUserEvent = new VerifyUserEvent();
            HashMap<String, String> params = new HashMap<>();
            HashMap<String, String> header = new HashMap<String, String>();
            if (verificationCode.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      VerificationStepActivity.this,
                      getResources().getString(R.string.validation_code_error),
                      Toast.LENGTH_SHORT)
                  .show();
            } else {
              AppController.getHelperProgressDialog()
                  .showProgress(VerificationStepActivity.this, "", "", false);

              params.put("emailId", emailId);
              params.put("code", verificationCode.getText().toString());
              ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
                  new ParticipantDatastoreConfigEvent(
                      "post",
                      Urls.CONFIRM_REGISTER_USER,
                      CONFIRM_REGISTER_USER_RESPONSE,
                      VerificationStepActivity.this,
                      LoginData.class,
                      params,
                      header,
                      null,
                      false,
                      VerificationStepActivity.this);
              verifyUserEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
              UserModulePresenter userModulePresenter = new UserModulePresenter();
              userModulePresenter.performVerifyRegistration(verifyUserEvent);
            }
          }
        });

    resend.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            AppController.getHelperProgressDialog()
                .showProgress(VerificationStepActivity.this, "", "", false);
            ResendEmailEvent resendEmailEvent = new ResendEmailEvent();
            HashMap<String, String> header = new HashMap<String, String>();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("emailId", emailId);
            ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
                new ParticipantDatastoreConfigEvent(
                    "post",
                    Urls.RESEND_CONFIRMATION,
                    RESEND_CONFIRMATION,
                    VerificationStepActivity.this,
                    LoginData.class,
                    params,
                    header,
                    null,
                    false,
                    VerificationStepActivity.this);
            resendEmailEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performResendEmail(resendEmailEvent);
          }
        });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == CONFIRM_REGISTER_USER_RESPONSE) {
      LoginData loginData = (LoginData) response;
      SharedPreferenceHelper.writePreference(
          VerificationStepActivity.this, getString(R.string.logintype), "signUp");
      CustomTabsIntent customTabsIntent =
          new CustomTabsIntent.Builder()
              .setToolbarColor(getResources().getColor(R.color.colorAccent))
              .setShowTitle(true)
              .setCloseButtonIcon(
                  BitmapFactory.decodeResource(getResources(), R.drawable.backeligibility))
              .setStartAnimations(
                  VerificationStepActivity.this, R.anim.slide_in_right, R.anim.slide_out_left)
              .setExitAnimations(
                  VerificationStepActivity.this, R.anim.slide_in_left, R.anim.slide_out_right)
              .build();
      StringBuilder loginUrl = new StringBuilder();
      loginUrl.append(Urls.LOGIN_URL);
      if (getIntent().getStringExtra("type") != null
          && !getIntent().getStringExtra("type").equalsIgnoreCase("ForgotPasswordActivity")
          && loginData.getTempRegId() != null) {
        loginUrl.append("&tempRegId=").append(loginData.getTempRegId());
      }
      customTabsIntent.intent.setData(Uri.parse(loginUrl.toString()));
      startActivity(customTabsIntent.intent);
    } else if (responseCode == RESEND_CONFIRMATION) {
      Toast.makeText(this, getResources().getString(R.string.resend_success), Toast.LENGTH_SHORT)
          .show();
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == CONFIRM_REGISTER_USER_RESPONSE || responseCode == RESEND_CONFIRMATION) {
      if (statusCode.equalsIgnoreCase("401")) {
        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        if (from != null && from.equalsIgnoreCase("Activity")) {
          SharedPreferences settings =
              SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
          settings.edit().clear().apply();
          // delete passcode from keystore
          String pass = AppController.refreshKeys("passcode");
          if (pass != null) {
            AppController.deleteKey("passcode_" + pass);
          }
          Intent intent = new Intent(VerificationStepActivity.this, GatewayActivity.class);
          ComponentName cn = intent.getComponent();
          Intent mainIntent = Intent.makeRestartActivityTask(cn);
          startActivity(mainIntent);
          finish();
        } else {
          AppController.getHelperSessionExpired(VerificationStepActivity.this, errormsg);
        }
      } else {
        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    SharedPreferences settings =
        SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
    settings.edit().clear().apply();
    // delete passcode from keystore
    String pass = AppController.refreshKeys("passcode");
    if (pass != null) {
      AppController.deleteKey("passcode_" + pass);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == JOIN_STUDY_RESPONSE) {
      Intent intent = new Intent();
      setResult(RESULT_OK, intent);
      finish();
    }
  }
}
