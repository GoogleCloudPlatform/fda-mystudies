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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import com.harvard.usermodule.SignupActivity;
import com.harvard.usermodule.TermsPrivacyPolicyActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.VerificationStepActivity;
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
import com.harvard.webservicemodule.events.ParticipantDatastoreServerConfigEvent;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
  private static final int GET_TERMS_AND_CONDITION = 3;
  private static final int UPDATE_USER_PROFILE = 100;
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
  private Context context;
  private boolean clicked;
  private TermsAndConditionData termsAndConditionData;
  private String userAuth;
  private String userID;
  private RegistrationData registrationData;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.content_signup, container, false);
    clicked = false;
    initializeXmlId(view);
    customTextView(agreeLabel);
    setFont();
    bindEvents();
    termsAndConditionData = new TermsAndConditionData();
    termsAndConditionData.setPrivacy(getString(R.string.privacyurl));
    termsAndConditionData.setTerms(getString(R.string.termsurl));
    return view;
  }

  private void initializeXmlId(View view) {
    firstNameLabel = (AppCompatTextView) view.findViewById(R.id.first_name_label);
    firstName = (AppCompatEditText) view.findViewById(R.id.edittxt_first_name);
    lastNameLabel = (AppCompatTextView) view.findViewById(R.id.last_name_label);
    lastName = (AppCompatEditText) view.findViewById(R.id.edittxt_last_name);
    emailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
    email = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
    passwordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
    password = (AppCompatEditText) view.findViewById(R.id.edittxt_password);
    confirmPasswordLabel = (AppCompatTextView) view.findViewById(R.id.confirm_password_label);
    confirmPassword = (AppCompatEditText) view.findViewById(R.id.edittxt_confirm_password);
    touchIdLabel = (AppCompatTextView) view.findViewById(R.id.touch_id_label);
    agreeLabel = (AppCompatTextView) view.findViewById(R.id.agree_label);
    agree = (AppCompatCheckBox) view.findViewById(R.id.agreeButton);
    submitBtn = (AppCompatTextView) view.findViewById(R.id.submitButton);
  }

  // set link for privacy and policy
  private void customTextView(AppCompatTextView view) {
    SpannableStringBuilder spanTxt =
        new SpannableStringBuilder(context.getResources().getString(R.string.i_agree) + " ");
    spanTxt.setSpan(
        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryBlack)),
        0,
        spanTxt.length(),
        0);
    spanTxt.append(context.getResources().getString(R.string.terms2));
    spanTxt.setSpan(
        new ClickableSpan() {
          @Override
          public void updateDrawState(TextPaint ds) {
            ds.setColor(
                ContextCompat.getColor(context, R.color.colorPrimary)); // you can use custom color
            ds.setUnderlineText(false); // this remove the underline
          }

          @Override
          public void onClick(View widget) {
            if (termsAndConditionData != null
                && !termsAndConditionData.getTerms().equalsIgnoreCase("")) {
              Intent termsIntent = new Intent(context, TermsPrivacyPolicyActivity.class);
              termsIntent.putExtra("title", getResources().getString(R.string.terms));
              termsIntent.putExtra("url", termsAndConditionData.getTerms());
              startActivity(termsIntent);
            }
          }
        },
        spanTxt.length() - context.getResources().getString(R.string.terms2).length(),
        spanTxt.length(),
        0);

    spanTxt.append(" " + context.getResources().getString(R.string.and));
    spanTxt.setSpan(
        new ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorPrimaryBlack)),
        20,
        spanTxt.length(),
        0);

    spanTxt.append(" " + context.getResources().getString(R.string.privacy_policy2));
    String temp = " " + context.getResources().getString(R.string.privacy_policy2);
    spanTxt.setSpan(
        new ClickableSpan() {

          @Override
          public void updateDrawState(TextPaint ds) {
            ds.setColor(
                ContextCompat.getColor(context, R.color.colorPrimary)); // you can use custom color
            ds.setUnderlineText(false); // this remove the underline
          }

          @Override
          public void onClick(View widget) {
            if (termsAndConditionData != null && !termsAndConditionData.getPrivacy().isEmpty()) {
              Intent termsIntent = new Intent(context, TermsPrivacyPolicyActivity.class);
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
      firstNameLabel.setTypeface(AppController.getTypeface(context, "regular"));
      firstName.setTypeface(AppController.getTypeface(context, "regular"));
      lastNameLabel.setTypeface(AppController.getTypeface(context, "regular"));
      lastName.setTypeface(AppController.getTypeface(context, "regular"));
      emailLabel.setTypeface(AppController.getTypeface(context, "regular"));
      email.setTypeface(AppController.getTypeface(context, "regular"));
      passwordLabel.setTypeface(AppController.getTypeface(context, "regular"));
      password.setTypeface(AppController.getTypeface(context, "regular"));
      confirmPasswordLabel.setTypeface(AppController.getTypeface(context, "regular"));
      confirmPassword.setTypeface(AppController.getTypeface(context, "regular"));
      touchIdLabel.setTypeface(AppController.getTypeface(context, "regular"));
      agreeLabel.setTypeface(AppController.getTypeface(context, "regular"));
      agree.setTypeface(AppController.getTypeface(context, "regular"));
      submitBtn.setTypeface(AppController.getTypeface(context, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {

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
  }

  private void callRegisterUserWebService() {
    String passwordPattern =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~]).{8,64}$";
    if (password.getText().toString().isEmpty()
        && email.getText().toString().isEmpty()
        && confirmPassword.getText().toString().isEmpty()) {
      Toast.makeText(
              context, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (email.getText().toString().isEmpty()) {
      Toast.makeText(context, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (!AppController.getHelperIsValidEmail(email.getText().toString())) {
      Toast.makeText(
              context, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT)
          .show();
    } else if (password.getText().toString().isEmpty()) {
      Toast.makeText(context, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT)
          .show();
    } else if (!password.getText().toString().matches(passwordPattern)) {
      SetDialogHelper.setNeutralDialog(
          context,
          getResources().getString(R.string.password_validation),
          false,
          context.getResources().getString(R.string.ok),
          context.getResources().getString(R.string.app_name));
    } else if (checkPasswordContainsEmailID(
        email.getText().toString(), password.getText().toString())) {
      Toast.makeText(
              context,
              getResources().getString(R.string.password_contain_email),
              Toast.LENGTH_SHORT)
          .show();
    } else if (confirmPassword.getText().toString().isEmpty()) {
      Toast.makeText(
              context,
              getResources().getString(R.string.confirm_password_empty),
              Toast.LENGTH_SHORT)
          .show();
    } else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
      Toast.makeText(
              context,
              getResources().getString(R.string.password_mismatch_error),
              Toast.LENGTH_SHORT)
          .show();
    } else if (!agree.isChecked()) {
      Toast.makeText(
              context,
              getResources().getString(R.string.terms_and_condition_validation),
              Toast.LENGTH_SHORT)
          .show();
    } else {
      try {
        AppController.getHelperHideKeyboard((Activity) context);
      } catch (Exception e) {
        Logger.log(e);
      }
      AppController.getHelperProgressDialog().showProgress(getContext(), "", "", false);

      HashMap<String, String> params = new HashMap<>();
      params.put("emailId", email.getText().toString());
      params.put("password", password.getText().toString());
      ParticipantDatastoreServerConfigEvent participantDatastoreServerConfigEvent =
          new ParticipantDatastoreServerConfigEvent(
              "post",
              Urls.REGISTER_USER,
              REGISTRATION_REQUEST,
              getContext(),
              RegistrationData.class,
              params,
              null,
              null,
              false,
              this);
      RegisterUserEvent registerUserEvent = new RegisterUserEvent();
      registerUserEvent.setParticipantDatastoreServerConfigEvent(participantDatastoreServerConfigEvent);
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
    if (responseCode == REGISTRATION_REQUEST) {
      registrationData = (RegistrationData) response;
      if (registrationData != null) {
        Intent intent = new Intent(context, VerificationStepActivity.class);
        intent.putExtra("email", email.getText().toString());
        intent.putExtra("type", "signup");
        startActivity(intent);
      } else {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.unable_to_signup),
                Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == GET_TERMS_AND_CONDITION) {
      termsAndConditionData = (TermsAndConditionData) response;
    } else if (responseCode == UPDATE_USER_PROFILE) {
      UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
      if (updateUserProfileData != null) {
        if (updateUserProfileData.getMessage().equalsIgnoreCase("Profile Updated successfully")) {
          signup(registrationData);
        } else {
          Toast.makeText(
                  context,
                  context.getResources().getString(R.string.unable_to_signup),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.unable_to_signup),
                Toast.LENGTH_SHORT)
            .show();
      }
    }
  }

  private void signup(RegistrationData registrationData) {
    if (registrationData != null && registrationData.isVerified()) {
      AppController.getHelperSharedPreference()
          .writePreference(
              getContext(), getString(R.string.userid), "" + registrationData.getUserId());
      AppController.getHelperSharedPreference()
          .writePreference(getContext(), getString(R.string.auth), "" + registrationData.getAuth());
      AppController.getHelperSharedPreference()
          .writePreference(
              getContext(), getString(R.string.verified), "" + registrationData.isVerified());
      AppController.getHelperSharedPreference()
          .writePreference(
              getContext(),
              getString(R.string.refreshToken),
              "" + registrationData.getRefreshToken());
      AppController.getHelperSharedPreference()
          .writePreference(
              context, context.getString(R.string.clientToken), registrationData.getClientToken());
      AppController.getHelperSharedPreference()
          .writePreference(
              getContext(), getString(R.string.email), "" + email.getText().toString());

      if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
        ((StudyActivity) context).loadstudylist();
      } else {
        Intent intent = new Intent(context, StandaloneActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        ((Activity) context).finish();
      }
    } else {
      Intent intent = new Intent(context, VerificationStepActivity.class);
      intent.putExtra("userid", registrationData.getUserId());
      intent.putExtra("from", "SignupFragment");
      intent.putExtra("type", "Signup");
      intent.putExtra("auth", registrationData.getAuth());
      intent.putExtra("verified", registrationData.isVerified());
      intent.putExtra("email", email.getText().toString());
      startActivity(intent);
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == UPDATE_USER_PROFILE) {
      if (statusCode.equalsIgnoreCase("401")) {
        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
        AppController.getHelperSessionExpired(context, errormsg);
      } else {
        Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      }
    } else {
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  private void callUpdateProfileWebService(String deviceToken) {
    AppController.getHelperProgressDialog().showProgress(context, "", "", false);

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

    ParticipantDatastoreServerConfigEvent participantDatastoreServerConfigEvent =
        new ParticipantDatastoreServerConfigEvent(
            "post_object",
            Urls.UPDATE_USER_PROFILE,
            UPDATE_USER_PROFILE,
            context,
            UpdateUserProfileData.class,
            null,
            params,
            jsonObjBody,
            false,
            this);
    UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();
    updateUserProfileEvent.setParticipantDatastoreServerConfigEvent(participantDatastoreServerConfigEvent);
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
              AppController.getHelperSharedPreference().readPreference(context, "deviceToken", "");
          if (!token.isEmpty()) {
            regIdStatus = true;
          }
        }
      } else {
        AppController.getHelperSharedPreference()
            .writePreference(context, "deviceToken", FirebaseInstanceId.getInstance().getToken());
        token =
            AppController.getHelperSharedPreference().readPreference(context, "deviceToken", "");
      }
      return token;
    }

    @Override
    protected void onPostExecute(String token) {
      callUpdateProfileWebService(token);
    }

    @Override
    protected void onPreExecute() {
      AppController.getHelperProgressDialog().showProgress(context, "", "", false);
    }
  }
}
