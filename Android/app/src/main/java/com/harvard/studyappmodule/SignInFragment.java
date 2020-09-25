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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
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
import com.harvard.usermodule.ForgotPasswordActivity;
import com.harvard.usermodule.NewPasscodeSetupActivity;
import com.harvard.usermodule.TermsPrivacyPolicyActivity;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.VerificationStepActivity;
import com.harvard.usermodule.event.GetUserProfileEvent;
import com.harvard.usermodule.event.LoginEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.model.TermsAndConditionData;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.usermodule.webservicemodel.UpdateUserProfileData;
import com.harvard.usermodule.webservicemodel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

public class SignInFragment extends Fragment implements ApiCall.OnAsyncRequestComplete {
  private static final int UPDATE_USER_PROFILE = 100;
  private static final int USER_PROFILE_REQUEST = 101;
  private static final int PASSCODE_RESPONSE = 102;
  private AppCompatEditText email;
  private AppCompatEditText password;
  private AppCompatTextView emailLabel;
  private AppCompatTextView passwordLabel;
  private AppCompatTextView signInLabel;
  private AppCompatTextView forgotPasswordLabel;
  private AppCompatTextView newUsrSignUp;
  private static final int LOGIN_REQUEST = 1;
  private Context context;
  private boolean clicked;
  private String userId;
  private String userAuth;
  private LoginData loginData;
  private AppCompatTextView agreeLabel;
  private static final int GET_TERMS_AND_CONDITION = 3;
  private TermsAndConditionData termsAndConditionData;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.content_sign_in, container, false);
    clicked = false;
    initializeXmlId(view);
    customTextViewAgree(agreeLabel);
    setFont();
    customTextView();
    bindEvents();
    termsAndConditionData = new TermsAndConditionData();
    termsAndConditionData.setPrivacy(getString(R.string.privacyurl));
    termsAndConditionData.setTerms(getString(R.string.termsurl));
    return view;
  }

  private void initializeXmlId(View view) {
    emailLabel = (AppCompatTextView) view.findViewById(R.id.email_label);
    email = (AppCompatEditText) view.findViewById(R.id.edittxt_email);
    passwordLabel = (AppCompatTextView) view.findViewById(R.id.password_label);
    password = (AppCompatEditText) view.findViewById(R.id.edittxt_password);
    signInLabel = (AppCompatTextView) view.findViewById(R.id.signInButton);
    forgotPasswordLabel = (AppCompatTextView) view.findViewById(R.id.forgot_password);
    newUsrSignUp = (AppCompatTextView) view.findViewById(R.id.newUsrSignUp);
    agreeLabel = (AppCompatTextView) view.findViewById(R.id.agree_terms);
  }

  @SuppressWarnings("deprecation")
  private void customTextView() {
    email.requestFocus();
    String html =
        context.getResources().getString(R.string.new_user)
            + " <font color=\"#007cba\">"
            + context.getResources().getString(R.string.sign_up)
            + "</font>";
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      newUsrSignUp.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
    } else {
      newUsrSignUp.setText(Html.fromHtml(html));
    }
  }

  // set link for privacy and policy
  private void customTextViewAgree(AppCompatTextView view) {
    SpannableStringBuilder spanTxt =
        new SpannableStringBuilder(
            context.getResources().getString(R.string.you_agree_this_app) + "\n");
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
            ds.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            ds.setUnderlineText(false);
          }

          @Override
          public void onClick(View widget) {
            if (termsAndConditionData != null && !termsAndConditionData.getTerms().isEmpty()) {
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
        spanTxt.length() - " and".length(),
        spanTxt.length(),
        0);

    spanTxt.append(" " + context.getResources().getString(R.string.privacy_policy2));
    String temp = " " + context.getResources().getString(R.string.privacy_policy2);
    spanTxt.setSpan(
        new ClickableSpan() {

          @Override
          public void updateDrawState(TextPaint ds) {
            ds.setColor(ContextCompat.getColor(context, R.color.colorPrimary));
            ds.setUnderlineText(false);
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

      emailLabel.setTypeface(AppController.getTypeface(context, "regular"));
      email.setTypeface(AppController.getTypeface(context, "regular"));
      passwordLabel.setTypeface(AppController.getTypeface(context, "regular"));
      password.setTypeface(AppController.getTypeface(context, "regular"));
      signInLabel.setTypeface(AppController.getTypeface(context, "regular"));
      forgotPasswordLabel.setTypeface(AppController.getTypeface(context, "regular"));
      newUsrSignUp.setTypeface(AppController.getTypeface(context, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    signInLabel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if (clicked == false) {
              clicked = true;
              callLoginWebService();
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

    forgotPasswordLabel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            email.setText("");
            password.setText("");
            email.requestFocus();
            Intent intent = new Intent(context, ForgotPasswordActivity.class);
            startActivity(intent);
          }
        });
    newUsrSignUp.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            ((StudyActivity) getContext()).loadsignup();
          }
        });
  }

  private void callLoginWebService() {
    if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
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
    } else {
      try {
        AppController.getHelperHideKeyboard((Activity) context);
      } catch (Exception e) {
        Logger.log(e);
      }
      AppController.getHelperProgressDialog().showProgress(context, "", "", false);

      HashMap<String, String> params = new HashMap<>();
      params.put("emailId", email.getText().toString());
      params.put("password", password.getText().toString());
      params.put("appId", BuildConfig.APPLICATION_ID);

      AuthServerConfigEvent authServerConfigEvent =
          new AuthServerConfigEvent(
              "post",
              Urls.LOGIN,
              LOGIN_REQUEST,
              context,
              LoginData.class,
              params,
              null,
              null,
              false,
              this);
      LoginEvent loginEvent = new LoginEvent();
      loginEvent.setAuthServerConfigEvent(authServerConfigEvent);
      UserModulePresenter userModulePresenter = new UserModulePresenter();
      userModulePresenter.performLogin(loginEvent);
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == LOGIN_REQUEST) {
      // FilterActivity Screen json object clearing
      AppController.getHelperSharedPreference()
          .writePreference(context, getString(R.string.json_object_filter), "");
      loginData = (LoginData) response;
      if (loginData != null) {
        userId = loginData.getUserId();
        userAuth = loginData.getAuth();
        AppController.getHelperSharedPreference()
            .writePreference(
                context, getString(R.string.refreshToken), loginData.getRefreshToken());
        AppController.getHelperSharedPreference()
            .writePreference(
                context, context.getString(R.string.clientToken), loginData.getClientToken());
        new GetFcmRefreshToken().execute();
      } else {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.not_able_to_login),
                Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == UPDATE_USER_PROFILE) {
      UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
      if (updateUserProfileData != null) {
        if (updateUserProfileData.getMessage().equalsIgnoreCase("Profile Updated successfully")) {
          callUserProfileWebService();
        } else {
          Toast.makeText(
                  context,
                  context.getResources().getString(R.string.not_able_to_login),
                  Toast.LENGTH_SHORT)
              .show();
        }
      } else {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.not_able_to_login),
                Toast.LENGTH_SHORT)
            .show();
      }
    } else if (responseCode == USER_PROFILE_REQUEST) {
      UserProfileData userProfileData = (UserProfileData) response;
      if (userProfileData != null) {
        if (loginData.getResetPassword()) {
          Intent intent = new Intent(context, ChangePasswordActivity.class);
          intent.putExtra("userid", loginData.getUserId());
          intent.putExtra("password", password.getText().toString());
          intent.putExtra("auth", loginData.getAuth());
          intent.putExtra("verified", loginData.isVerified());
          intent.putExtra("email", email.getText().toString());
          startActivity(intent);
        } else {
          if (userProfileData.getSettings().isPasscode()) {
            AppController.getHelperSharedPreference()
                .writePreference(context, getString(R.string.initialpasscodeset), "no");
            if (loginData.isVerified()) {
              AppController.getHelperSharedPreference()
                  .writePreference(
                      getContext(), getString(R.string.userid), "" + loginData.getUserId());
              AppController.getHelperSharedPreference()
                  .writePreference(
                      getContext(), getString(R.string.auth), "" + loginData.getAuth());
              AppController.getHelperSharedPreference()
                  .writePreference(
                      getContext(), getString(R.string.verified), "" + loginData.isVerified());
              AppController.getHelperSharedPreference()
                  .writePreference(
                      getContext(), getString(R.string.email), "" + email.getText().toString());
              AppController.getHelperSharedPreference()
                  .writePreference(
                      context, getString(R.string.refreshToken), loginData.getRefreshToken());
              Intent intent = new Intent(context, NewPasscodeSetupActivity.class);
              intent.putExtra("from", "signin");
              startActivityForResult(intent, PASSCODE_RESPONSE);
            } else {
              Intent intent = new Intent(context, VerificationStepActivity.class);
              intent.putExtra("from", "SignInFragment");
              intent.putExtra("type", "Signin");
              intent.putExtra("userid", loginData.getUserId());
              intent.putExtra("auth", loginData.getAuth());
              intent.putExtra("verified", loginData.isVerified());
              intent.putExtra("email", email.getText().toString());
              intent.putExtra("password", password.getText().toString());
              startActivity(intent);
            }
          } else {
            login();
          }
        }
      } else {
        Toast.makeText(
                context,
                context.getResources().getString(R.string.not_able_to_login),
                Toast.LENGTH_SHORT)
            .show();
      }
    } else if (GET_TERMS_AND_CONDITION == responseCode) {
      termsAndConditionData = (TermsAndConditionData) response;
    }
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

  private void login() {
    if (loginData.isVerified()) {
      AppController.getHelperSharedPreference()
          .writePreference(getContext(), getString(R.string.userid), "" + loginData.getUserId());
      AppController.getHelperSharedPreference()
          .writePreference(getContext(), getString(R.string.auth), "" + loginData.getAuth());
      AppController.getHelperSharedPreference()
          .writePreference(getContext(), getString(R.string.verified), "" + loginData.isVerified());
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
      intent.putExtra("from", "SignInFragment");
      intent.putExtra("type", "Signin");
      intent.putExtra("userid", loginData.getUserId());
      intent.putExtra("auth", loginData.getAuth());
      intent.putExtra("verified", loginData.isVerified());
      intent.putExtra("email", email.getText().toString());
      intent.putExtra("password", password.getText().toString());
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
    params.put("userId", userId);

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

    RegistrationServerConfigEvent registrationServerConfigEvent =
        new RegistrationServerConfigEvent(
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
    updateUserProfileEvent.setRegistrationServerConfigEvent(registrationServerConfigEvent);
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

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == PASSCODE_RESPONSE) {
      login();
    }
  }
}
