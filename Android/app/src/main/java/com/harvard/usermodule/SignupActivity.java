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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.model.TermsAndConditionData;
import com.harvard.usermodule.webservicemodel.RegistrationData;
import com.harvard.usermodule.webservicemodel.UpdateUserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
  private static final int UPDATE_USER_PROFILE = 101;
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private RelativeLayout cancelBtn;
  private RelativeLayout infoIcon;
  private AppCompatTextView cancelTxt;
  private AppCompatEditText firstName;
  private AppCompatEditText lastName;
  private AppCompatEditText email;
  private AppCompatEditText password;
  private AppCompatEditText confirmPassword;
  private AppCompatTextView firstNameLabel;
  private AppCompatTextView lastNameLabel;
  private AppCompatTextView emailLabel;
  private AppCompatTextView passwordLabel;
  private AppCompatTextView confirmPasswordLabel;
  private AppCompatTextView touchIdLabel;
  private AppCompatTextView agreeLabel;
  private AppCompatCheckBox agree;
  private AppCompatTextView submitBtn;
  private static final int REGISTRATION_REQUEST = 2;
  private static final int GET_TERMS_AND_CONDITION = 3;
  private static final int STUDYINFO_REQUEST = 100;
  private boolean clicked;
  private TermsAndConditionData termsAndConditionData;
  private RegistrationData registrationData;
  private String userAuth;
  private String userID;
  String passwordPattern =
          "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,64}$";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);
    clicked = false;
    initializeXmlId();
    setTextForView();
    customTextView(agreeLabel);
    setFont();
    bindEvents();
    termsAndConditionData = new TermsAndConditionData();
    termsAndConditionData.setPrivacy(getString(R.string.privacyurl));
    termsAndConditionData.setTerms(getString(R.string.termsurl));
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    infoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
    title = (AppCompatTextView) findViewById(R.id.title);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    cancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
    firstNameLabel = (AppCompatTextView) findViewById(R.id.first_name_label);
    firstName = (AppCompatEditText) findViewById(R.id.edittxt_first_name);
    lastNameLabel = (AppCompatTextView) findViewById(R.id.last_name_label);
    lastName = (AppCompatEditText) findViewById(R.id.edittxt_last_name);
    emailLabel = (AppCompatTextView) findViewById(R.id.email_label);
    email = (AppCompatEditText) findViewById(R.id.edittxt_email);
    passwordLabel = (AppCompatTextView) findViewById(R.id.password_label);
    password = (AppCompatEditText) findViewById(R.id.edittxt_password);
    confirmPasswordLabel = (AppCompatTextView) findViewById(R.id.confirm_password_label);
    confirmPassword = (AppCompatEditText) findViewById(R.id.edittxt_confirm_password);
    touchIdLabel = (AppCompatTextView) findViewById(R.id.touch_id_label);
    agreeLabel = (AppCompatTextView) findViewById(R.id.agree_label);
    agree = (AppCompatCheckBox) findViewById(R.id.agreeButton);
    submitBtn = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setTextForView() {
    cancelBtn.setVisibility(View.GONE);
    infoIcon.setVisibility(View.GONE);
    title.setText(getResources().getString(R.string.signup));
  }

  // set link for privacy and policy
  private void customTextView(AppCompatTextView view) {
    SpannableStringBuilder spanTxt =
        new SpannableStringBuilder(getResources().getString(R.string.i_agree) + " ");
    spanTxt.setSpan(
        new ForegroundColorSpan(
            ContextCompat.getColor(SignupActivity.this, R.color.colorPrimaryBlack)),
        0,
        spanTxt.length(),
        0);
    spanTxt.append(getResources().getString(R.string.terms2));
    spanTxt.setSpan(
        new ClickableSpan() {
          @Override
          public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimary));
            ds.setUnderlineText(false);
          }

          @Override
          public void onClick(View widget) {
            if (termsAndConditionData != null && !termsAndConditionData.getTerms().isEmpty()) {
              Intent termsIntent =
                  new Intent(SignupActivity.this, TermsPrivacyPolicyActivity.class);
              termsIntent.putExtra("title", getResources().getString(R.string.terms));
              termsIntent.putExtra("url", termsAndConditionData.getTerms());
              startActivity(termsIntent);
            }
          }
        },
        spanTxt.length() - getResources().getString(R.string.terms2).length(),
        spanTxt.length(),
        0);

    spanTxt.append(" " + getResources().getString(R.string.and));
    spanTxt.setSpan(
        new ForegroundColorSpan(
            ContextCompat.getColor(SignupActivity.this, R.color.colorPrimaryBlack)),
        20,
        spanTxt.length(),
        0);

    spanTxt.append(" " + getResources().getString(R.string.privacy_policy2));
    String temp = " " + getResources().getString(R.string.privacy_policy2);
    spanTxt.setSpan(
        new ClickableSpan() {

          @Override
          public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimary));
            ds.setUnderlineText(false);
          }

          @Override
          public void onClick(View widget) {
            if (termsAndConditionData != null && !termsAndConditionData.getPrivacy().isEmpty()) {
              Intent termsIntent =
                  new Intent(SignupActivity.this, TermsPrivacyPolicyActivity.class);
              termsIntent.putExtra("title", getResources().getString(R.string.privacy_policy));
              termsIntent.putExtra("url", termsAndConditionData.getPrivacy());
              startActivity(termsIntent);
            }
          }
        },
        spanTxt.length() - temp.length(),
        spanTxt.length(),
        0);

    view.setMovementMethod(LinkMovementMethod.getInstance());
    view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    email.requestFocus();
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(SignupActivity.this, "medium"));
      cancelTxt.setTypeface(AppController.getTypeface(SignupActivity.this, "medium"));
      firstNameLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      firstName.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      lastNameLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      lastName.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      emailLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      email.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      passwordLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      password.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      confirmPasswordLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      confirmPassword.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      touchIdLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      agreeLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      agree.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
      submitBtn.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            try {
              AppController.getHelperHideKeyboard(SignupActivity.this);
            } catch (Exception e) {
              Logger.log(e);
            }
            finish();
          }
        });

    cancelBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            finish();
          }
        });

    submitBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (clicked == false) {
              clicked = true;
              callRegisterUserWebService();
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
    infoIcon.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SetDialogHelper.setNeutralDialog(
                SignupActivity.this,
                getResources().getString(R.string.registration_message),
                false,
                getResources().getString(R.string.ok),
                getResources().getString(R.string.why_register));
          }
        });

    password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
          if (!password.getText().toString().matches(passwordPattern)) {
            password.setError(getResources().getString(R.string.password_validation));
          }
        }
      }
    });
  }

  private void callRegisterUserWebService() {
    if (password.getText().toString().isEmpty()
        && email.getText().toString().isEmpty()
        && confirmPassword.getText().toString().isEmpty()) {
      Toast.makeText(
              this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (email.getText().toString().isEmpty()) {
      Toast.makeText(this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (!AppController.getHelperIsValidEmail(email.getText().toString())) {
      Toast.makeText(this, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT)
          .show();
    } else if (password.getText().toString().isEmpty()) {
      Toast.makeText(this, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (!password.getText().toString().matches(passwordPattern)) {
      password.setError(getResources().getString(R.string.password_validation));
    } else if (checkPasswordContainsEmailID(
        email.getText().toString(), password.getText().toString())) {
      Toast.makeText(
              this, getResources().getString(R.string.password_contain_email), Toast.LENGTH_SHORT)
          .show();
    } else if (confirmPassword.getText().toString().isEmpty()) {
      Toast.makeText(
              this, getResources().getString(R.string.confirm_password_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
      Toast.makeText(
              this, getResources().getString(R.string.password_mismatch_error), Toast.LENGTH_SHORT)
          .show();
    } else if (!agree.isChecked()) {
      Toast.makeText(
              this,
              getResources().getString(R.string.terms_and_condition_validation),
              Toast.LENGTH_SHORT)
          .show();
    } else {
      AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
      HashMap<String, String> params = new HashMap<>();
      params.put("emailId", email.getText().toString());
      params.put("password", password.getText().toString());
      ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
          new ParticipantDatastoreConfigEvent(
              "post",
              Urls.REGISTER_USER,
              REGISTRATION_REQUEST,
              this,
              RegistrationData.class,
              params,
              null,
              null,
              false,
              this);
      RegisterUserEvent registerUserEvent = new RegisterUserEvent();
      registerUserEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performRegistration(registerUserEvent);
    }
  }

  private boolean checkPasswordContainsEmailID(String email, String password) {
    if (password.contains(email)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (GET_TERMS_AND_CONDITION == responseCode) {
      termsAndConditionData = (TermsAndConditionData) response;
    } else if (responseCode == REGISTRATION_REQUEST) {
      registrationData = (RegistrationData) response;
      if (registrationData != null) {
        Intent intent = new Intent(SignupActivity.this, VerificationStepActivity.class);
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("type", "signup");
        startActivity(intent);

      } else {
        Toast.makeText(
                this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == UPDATE_USER_PROFILE) {
      UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
      if (updateUserProfileData != null) {
        if (updateUserProfileData.getMessage().equalsIgnoreCase("Profile Updated successfully")) {
          signup(registrationData);
        } else {
          Toast.makeText(
                  this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        Toast.makeText(
                this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  private void signup(RegistrationData registrationData) {
    if (registrationData != null) {
      if (registrationData.isVerified()) {
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this,
                getString(R.string.refreshToken),
                registrationData.getRefreshToken());
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this,
                getString(R.string.clientToken),
                registrationData.getClientToken());
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this, getString(R.string.userid), "" + registrationData.getUserId());
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this, getString(R.string.auth), "" + registrationData.getAuth());
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this,
                getString(R.string.verified),
                "" + registrationData.isVerified());
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this, getString(R.string.email), "" + email.getText().toString());
        if (getIntent().getStringExtra("from") != null
            && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
          Intent intent = new Intent();
          setResult(RESULT_OK, intent);
          finish();
        } else {
          if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            Intent intent = new Intent(SignupActivity.this, StudyActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
          } else {
            Intent intent = new Intent(SignupActivity.this, StandaloneActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
          }
        }
      } else {
        if (getIntent().getStringExtra("from") != null
            && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
          Intent intent = new Intent(SignupActivity.this, VerificationStepActivity.class);
          intent.putExtra("from", "StudyInfo");
          intent.putExtra("type", "Signup");
          intent.putExtra("userid", registrationData.getUserId());
          intent.putExtra("auth", registrationData.getAuth());
          intent.putExtra("verified", registrationData.isVerified());
          intent.putExtra("email", email.getText().toString());
          startActivityForResult(intent, STUDYINFO_REQUEST);
        } else {
          Intent intent = new Intent(SignupActivity.this, VerificationStepActivity.class);
          intent.putExtra("from", "Activity");
          intent.putExtra("type", "Signup");
          intent.putExtra("userid", registrationData.getUserId());
          intent.putExtra("auth", registrationData.getAuth());
          intent.putExtra("verified", registrationData.isVerified());
          intent.putExtra("email", email.getText().toString());
          startActivity(intent);
        }
      }
    } else {
      Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == STUDYINFO_REQUEST) {
      if (resultCode == RESULT_OK) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == UPDATE_USER_PROFILE) {
      if (statusCode.equalsIgnoreCase("401")) {
        Toast.makeText(SignupActivity.this, errormsg, Toast.LENGTH_SHORT).show();
        AppController.getHelperSessionExpired(SignupActivity.this, errormsg);
      } else {
        Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  private void callUpdateProfileWebService(String deviceToken) {
    AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
    HashMap<String, String> params = new HashMap<>();
    params.put("Authorization", "Bearer " + userAuth);
    params.put("userId", userID);

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

    JSONObject settingJson = new JSONObject();
    try {
      settingJson.put("passcode", true);
      settingJson.put("remoteNotifications", true);
      settingJson.put("localNotifications", true);
      jsonObjBody.put("settings", settingJson);
    } catch (JSONException e) {
      Logger.log(e);
    }

    ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
        new ParticipantDatastoreConfigEvent(
            "post_object",
            Urls.UPDATE_USER_PROFILE,
            UPDATE_USER_PROFILE,
            this,
            UpdateUserProfileData.class,
            null,
            params,
            jsonObjBody,
            false,
            this);
    UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();
    updateUserProfileEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
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
                  .readPreference(SignupActivity.this, "deviceToken", "");
          if (!token.isEmpty()) {
            regIdStatus = true;
          }
        }
      } else {
        AppController.getHelperSharedPreference()
            .writePreference(
                SignupActivity.this, "deviceToken", FirebaseInstanceId.getInstance().getToken());
        token =
            AppController.getHelperSharedPreference()
                .readPreference(SignupActivity.this, "deviceToken", "");
      }
      return token;
    }

    @Override
    protected void onPostExecute(String token) {
      callUpdateProfileWebService(token);
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
    }
  }
}
