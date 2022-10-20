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
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;

public class SignupProcessCompleteActivity extends AppCompatActivity {
  private AppCompatTextView congratsLabel;
  private AppCompatTextView nextButton;
  private AppCompatTextView signupCompleteLabel;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup_process_complete);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);

    initializeXmlId();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    congratsLabel = (AppCompatTextView) findViewById(R.id.congrats_label);
    signupCompleteLabel = (AppCompatTextView) findViewById(R.id.signup_complete_txt_label);
    nextButton = (AppCompatTextView) findViewById(R.id.nextButton);
  }

  private void setFont() {
    try {
      congratsLabel.setTypeface(
          AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
      signupCompleteLabel.setTypeface(
          AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
      nextButton.setTypeface(
          AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {

    nextButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.signup_process_complete_done));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (getIntent().getStringExtra("from") != null
                && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);
              finish();
            } else {
              if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                Intent intent = new Intent(SignupProcessCompleteActivity.this, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
              } else {
                Intent intent =
                    new Intent(SignupProcessCompleteActivity.this, StandaloneActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = Intent.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
              }
            }
          }
        });
  }

  @Override
  public void onBackPressed() {}
}
