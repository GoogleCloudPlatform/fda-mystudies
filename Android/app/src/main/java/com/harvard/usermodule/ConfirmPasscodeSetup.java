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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.harvard.R;
import com.harvard.passcodemodule.PasscodeView;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;

public class ConfirmPasscodeSetup extends AppCompatActivity {

  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private RelativeLayout cancelBtn;
  private PasscodeView passcodeView;
  private static final int JOIN_STUDY_RESPONSE = 100;
  private TextView passcodetitle;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_passcode_setup);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);

    initializeXmlId();
    setTextForView();
    setFont();
    bindEvent();
    title.setText(R.string.confirmPascode);
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    passcodeView = (PasscodeView) findViewById(R.id.passcode_view);
    passcodetitle = (TextView) findViewById(R.id.passcodetitle);
    TextView passcodedesc = (TextView) findViewById(R.id.passcodedesc);

    TextView forgot = (TextView) findViewById(R.id.forgot);
    forgot.setVisibility(View.GONE);
  }

  private void setTextForView() {
    cancelBtn.setVisibility(View.GONE);
    passcodetitle.setText(getResources().getString(R.string.passcode_confirm_reenter));
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(ConfirmPasscodeSetup.this, "medium"));
      passcodetitle.setTypeface(AppController.getTypeface(ConfirmPasscodeSetup.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvent() {
    passcodeView.postDelayed(
        new Runnable() {
          @Override
          public void run() {
            passcodeView.requestToShowKeyboard();
          }
        },
        400);

    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.confirm_passcode_back));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });

    passcodeView.setPasscodeEntryListener(
        new PasscodeView.PasscodeEntryListener() {
          @Override
          public void onPasscodeEntered(String passcode) {
            if (passcode.equalsIgnoreCase(getIntent().getStringExtra("passcode"))) {
              AppController.getHelperHideKeyboard(ConfirmPasscodeSetup.this);
              AppController.getHelperSharedPreference()
                  .writePreference(
                      ConfirmPasscodeSetup.this, getString(R.string.initialpasscodeset), "Yes");
              AppController.getHelperSharedPreference()
                  .writePreference(
                      ConfirmPasscodeSetup.this, getString(R.string.usepasscode), "yes");
              new CreateNewPasscode().execute(passcode);
              if (getIntent().getStringExtra("from") != null
                  && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                passcodeView.clearText();
                Intent intent =
                    new Intent(ConfirmPasscodeSetup.this, SignupProcessCompleteActivity.class);
                intent.putExtra("from", "StudyInfo");
                startActivityForResult(intent, JOIN_STUDY_RESPONSE);
              } else if (getIntent().getStringExtra("from") != null
                  && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
                passcodeView.clearText();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
              } else if (getIntent().getStringExtra("from") != null
                  && getIntent().getStringExtra("from").equalsIgnoreCase("profile_change")) {
                passcodeView.clearText();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
              } else if (getIntent().getStringExtra("from") != null
                  && getIntent().getStringExtra("from").equalsIgnoreCase("signin")) {
                passcodeView.clearText();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
              } else {
                passcodeView.clearText();
                Intent intent =
                    new Intent(ConfirmPasscodeSetup.this, SignupProcessCompleteActivity.class);
                startActivity(intent);
              }
            } else {
              Toast.makeText(
                      ConfirmPasscodeSetup.this, R.string.passcodeNotMatching, Toast.LENGTH_SHORT)
                  .show();
              passcodeView.clearText();
            }
          }
        });
  }

  private class CreateNewPasscode extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
      String passcode = params[0];
      // delete passcode from keystore if already exist
      String pass = AppController.refreshKeys("passcode");
      if (pass != null) {
        AppController.deleteKey("passcode_" + pass);
      }
      // storing into keystore
      AppController.createNewKeys(ConfirmPasscodeSetup.this, "passcode_" + passcode);
      return null;
    }

    @Override
    protected void onPostExecute(String token) {}

    @Override
    protected void onPreExecute() {}
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

  @Override
  public void onBackPressed() {
    super.onBackPressed();
  }
}
