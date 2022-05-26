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

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.harvard.AppConfig;
import com.harvard.FdaApplication;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.usermodule.event.ForgotPasswordEvent;
import com.harvard.usermodule.model.Apps;
import com.harvard.usermodule.webservicemodel.ForgotPasswordData;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.AuthServerConfigEvent;
import io.realm.Realm;
import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private static final int FORGOT_PASSWORD_REQUEST = 10;
  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private RelativeLayout cancelBtn;
  private AppCompatTextView cancelTxt;
  private AppCompatEditText email;
  private AppCompatTextView submitButton;
  private static final int RESEND_CONFIRMATION = 101;
  public static String FROM = "ForgotPasswordActivity";
  private static final int GO_TO_SIGNIN = 111;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    cancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
    email = (AppCompatEditText) findViewById(R.id.edittxt_email);
    submitButton = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setTextForView() {
    cancelBtn.setVisibility(View.GONE);
    title.setText(getResources().getString(R.string.forgot_password_heading));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "medium"));
      cancelTxt.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "medium"));
      email.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "regular"));
      submitButton.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "regular"));
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
                getString(R.string.forgot_password_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            try {
              AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
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
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.forgot_password_cancel));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            try {
              AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
            } catch (Exception e) {
              Logger.log(e);
            }
            finish();
          }
        });

    submitButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.forgot_password_submit));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (email.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ForgotPasswordActivity.this,
                      getResources().getString(R.string.email_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (!AppController.getHelperIsValidEmail(email.getText().toString())) {
              Toast.makeText(
                      ForgotPasswordActivity.this,
                      getResources().getString(R.string.email_validation),
                      Toast.LENGTH_SHORT)
                  .show();
            } else {
              AppController.getHelperProgressDialog()
                  .showProgress(ForgotPasswordActivity.this, "", "", false);
              callForgotPasswordWebService();
            }
          }
        });
  }

  private void callForgotPasswordWebService() {
    HashMap<String, String> headers = new HashMap<>();
    headers.put("correlationId", FdaApplication.getRandomString());
    headers.put("appId", AppConfig.APP_ID_VALUE);
    headers.put("mobilePlatform", "ANDROID");
    DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
    Realm realm = AppController.getRealmobj(ForgotPasswordActivity.this);
    Apps apps = dbServiceSubscriber.getApps(realm);
    headers.put("contactEmail", apps.getContactUsEmail());
    headers.put("supportEmail", apps.getSupportEmail());
    headers.put("fromEmail", apps.getFromEmail());
    dbServiceSubscriber.closeRealmObj(realm);

    HashMap<String, String> params = new HashMap<>();
    params.put("email", email.getText().toString());
    params.put("appId", AppConfig.APP_ID_VALUE);

    ForgotPasswordEvent forgotPasswordEvent = new ForgotPasswordEvent();
    AuthServerConfigEvent authServerConfigEvent =
        new AuthServerConfigEvent(
            "post",
            Urls.FORGOT_PASSWORD,
            FORGOT_PASSWORD_REQUEST,
            ForgotPasswordActivity.this,
            ForgotPasswordData.class,
            params,
            headers,
            null,
            false,
            ForgotPasswordActivity.this);
    forgotPasswordEvent.setAuthServerConfigEvent(authServerConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performForgotPassword(forgotPasswordEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == RESEND_CONFIRMATION) {
      // for resend confirmation block
    } else {
      try {
        AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
      } catch (Exception e) {
        Logger.log(e);
      }
      Toast.makeText(
              this, getResources().getString(R.string.forgot_password_error), Toast.LENGTH_SHORT)
              .show();
      if (getIntent().getStringExtra("from") != null
              && getIntent().getStringExtra("from").equalsIgnoreCase("verification")) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
      } else {
        finish();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(ForgotPasswordActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(ForgotPasswordActivity.this, errormsg);
    } else if (statusCode.equalsIgnoreCase("403")) {
      Toast.makeText(this, errormsg, Toast.LENGTH_LONG).show();
      Intent intent = new Intent(ForgotPasswordActivity.this, VerificationStepActivity.class);
      intent.putExtra("from", FROM);
      intent.putExtra("type", "ForgotPasswordActivity");
      intent.putExtra("userid", "");
      intent.putExtra("auth", "");
      intent.putExtra("verified", false);
      intent.putExtra("email", email.getText().toString());
      startActivityForResult(intent, GO_TO_SIGNIN);
    } else {
      Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == GO_TO_SIGNIN) {
      if (resultCode == RESULT_OK) {
        try {
          AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
        } catch (Exception e) {
          Logger.log(e);
        }
        finish();
      }
    }
  }
}
