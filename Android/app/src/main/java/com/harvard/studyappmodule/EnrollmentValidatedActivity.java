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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DBServiceSubscriber;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.utils.AppController;
import com.harvard.webservicemodule.apihelper.ApiCall;
import io.realm.Realm;
import java.util.List;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

public class EnrollmentValidatedActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private AppCompatTextView mValidatedlabel, mCompleteLabel, mContinueButton;
  private EligibilityConsent eligibilityConsent;
  private static final String CONSENT = "consent";
  private static final int CONSENT_RESPONSECODE = 100;
  private DBServiceSubscriber dbServiceSubscriber;
  private Realm mRealm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_enrollment_validated);
    dbServiceSubscriber = new DBServiceSubscriber();
    mRealm = AppController.getRealmobj(this);
    initializeXMLId();
    setFont();
    mContinueButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            eligibilityConsent =
                dbServiceSubscriber.getConsentMetadata(
                    getIntent().getStringExtra("studyId"), mRealm);
            if (getIntent().getStringExtra("eligibility").equalsIgnoreCase("token")) {
              startconsent(eligibilityConsent.getConsent());
            } else {
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);
              finish();
            }
          }
        });
  }

  private void initializeXMLId() {
    mValidatedlabel = (AppCompatTextView) findViewById(R.id.validatedlabel);
    mCompleteLabel = (AppCompatTextView) findViewById(R.id.complete_txt_label);
    mContinueButton = (AppCompatTextView) findViewById(R.id.continueButton);
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(mRealm);
    super.onDestroy();
  }

  private void setFont() {
    mValidatedlabel.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
    mCompleteLabel.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
    mContinueButton.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {}

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {}

  private void startconsent(Consent consent) {
    ConsentBuilder consentBuilder = new ConsentBuilder();
    List<Step> consentstep =
        consentBuilder.createsurveyquestion(this, consent, getIntent().getStringExtra("title"));
    Task consentTask = new OrderedTask(CONSENT, consentstep);
    Intent intent =
        CustomConsentViewTaskActivity.newIntent(
            this,
            consentTask,
            getIntent().getStringExtra("studyId"),
            getIntent().getStringExtra("enrollId"),
            getIntent().getStringExtra("title"),
            getIntent().getStringExtra("eligibility"),
            getIntent().getStringExtra("type"));
    startActivityForResult(intent, CONSENT_RESPONSECODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CONSENT_RESPONSECODE) {
      if (resultCode == RESULT_OK) {
        Intent intent =
            new Intent(EnrollmentValidatedActivity.this, ConsentCompletedActivity.class);
        intent.putExtra("enrollId", getIntent().getStringExtra("enrollId"));
        intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
        intent.putExtra("title", getIntent().getStringExtra("title"));
        intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
        intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
        // get the encrypted file path
        intent.putExtra("PdfPath", data.getStringExtra("PdfPath"));
        startActivity(intent);
        finish();
      } else if (resultCode == 12345) {
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
          Intent intent = new Intent(EnrollmentValidatedActivity.this, StudyActivity.class);
          ComponentName cn = intent.getComponent();
          Intent mainIntent = Intent.makeRestartActivityTask(cn);
          startActivity(mainIntent);
          finish();
        } else {
          Intent intent = new Intent(EnrollmentValidatedActivity.this, StandaloneActivity.class);
          ComponentName cn = intent.getComponent();
          Intent mainIntent = Intent.makeRestartActivityTask(cn);
          startActivity(mainIntent);
          finish();
        }
      } else {
        finish();
      }
    }
  }
}
