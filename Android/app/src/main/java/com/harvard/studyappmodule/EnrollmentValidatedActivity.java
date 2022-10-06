/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
 *
 */

package com.harvard.studyappmodule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.studyappmodule.studymodel.ConsentDocumentData;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import io.realm.Realm;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

public class EnrollmentValidatedActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private AppCompatTextView validatedlabel;
  private AppCompatTextView completeLabel;
  private AppCompatTextView continueButton;
  private EligibilityConsent eligibilityConsent;
  private static final String CONSENT = "consent";
  private static final int CONSENT_RESPONSECODE = 100;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private static final int UPDATE_USER_PREFERENCE_RESPONSE_CODE = 200;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_enrollment_validated);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    initializeXmlId();
    setFont();
    continueButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.enrollment_validated_continue));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            eligibilityConsent =
                dbServiceSubscriber.getConsentMetadata(
                    getIntent().getStringExtra("studyId"), realm);
            if (getIntent().getStringExtra("eligibility").equalsIgnoreCase("token")) {
              startconsent(eligibilityConsent.getConsent());
            } else {
              Intent intent = new Intent();
              setResult(RESULT_OK, intent);
              finish();
            }
          }
        });
    updateuserpreference();
  }

  private void initializeXmlId() {
    validatedlabel = (AppCompatTextView) findViewById(R.id.validatedlabel);
    completeLabel = (AppCompatTextView) findViewById(R.id.complete_txt_label);
    continueButton = (AppCompatTextView) findViewById(R.id.continueButton);
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
  }

  private void setFont() {
    validatedlabel.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
    completeLabel.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
    continueButton.setTypeface(
        AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
  }

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

  public void updateuserpreference() {

    AppController.getHelperProgressDialog().showProgress(EnrollmentValidatedActivity.this,
        "", "", false);
    ConsentDocumentData consentDocumentData =
        dbServiceSubscriber.getConsentDocumentFromDB(getIntent().getStringExtra("studyId"), realm);
    HashMap<String, String> header = new HashMap();
    header.put(
            "Authorization",
            "Bearer "
                    + AppController.getHelperSharedPreference()
                    .readPreference(this, getResources().getString(R.string.auth), ""));
    header.put(
            "userId",
            AppController.getHelperSharedPreference()
                    .readPreference(this, getResources().getString(R.string.userid), ""));

    JSONObject jsonObject = new JSONObject();

    JSONArray studieslist = new JSONArray();
    JSONObject studiestatus = new JSONObject();
    try {
      studiestatus.put("studyId", getIntent().getStringExtra("studyId"));
      if (getIntent().getStringExtra("siteId") != null
              && !getIntent().getStringExtra("siteId").equalsIgnoreCase("")) {
        studiestatus.put("siteId", getIntent().getStringExtra("siteId"));
      }
      studiestatus.put("status", StudyFragment.YET_TO_JOIN);
      studiestatus.put("userStudyVersion", consentDocumentData.getConsent().getVersion());
    } catch (JSONException e) {
      Logger.log(e);
    }

    studieslist.put(studiestatus);
    try {
      jsonObject.put("studies", studieslist);
    } catch (JSONException e) {
      Logger.log(e);
    }
    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
            new ParticipantEnrollmentDatastoreConfigEvent(
                    "post_object",
                    Urls.UPDATE_STUDY_PREFERENCE,
                    UPDATE_USER_PREFERENCE_RESPONSE_CODE,
                    this,
                    LoginData.class,
                    null,
                    header,
                    jsonObject,
                    false,
                    this);
    UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
    updatePreferenceEvent.setParticipantEnrollmentDatastoreConfigEvent(
            participantEnrollmentDatastoreConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    dbServiceSubscriber.updateStudyPreferenceDB(
            this, getIntent().getStringExtra("studyId"),
        StudyFragment.YET_TO_JOIN, "", "", "", "", "");
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();

    dbServiceSubscriber.updateStudyPreferenceDB(
            this, getIntent().getStringExtra("studyId"),
        StudyFragment.YET_TO_JOIN, "", "", "", "", "");
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
