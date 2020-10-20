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

package com.harvard.eligibilitymodule;

import static com.harvard.studyappmodule.StudyFragment.CONSENT;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.ConsentCompletedActivity;
import com.harvard.studyappmodule.StandaloneActivity;
import com.harvard.studyappmodule.StudyActivity;
import com.harvard.studyappmodule.StudyFragment;
import com.harvard.studyappmodule.consent.ConsentBuilder;
import com.harvard.studyappmodule.consent.CustomConsentViewTaskActivity;
import com.harvard.studyappmodule.consent.model.Consent;
import com.harvard.studyappmodule.consent.model.EligibilityConsent;
import com.harvard.usermodule.UserModulePresenter;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.webservicemodel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreServerEnrollmentConfigEvent;
import io.realm.Realm;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;

public class EligibleActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

  private static final int CONSENT_RESPONSE_CODE = 100;
  private EligibilityConsent eligibilityConsent;
  private DbServiceSubscriber dbServiceSubscriber;
  private static final int UPDATE_USER_PREFERENCE_RESPONSE_CODE = 200;
  private Realm realm;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eligible);
    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(this);

    TextView button = (TextView) findViewById(R.id.continueButton);
    button.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            eligibilityConsent =
                dbServiceSubscriber.getConsentMetadata(
                    getIntent().getStringExtra("studyId"), realm);
            startconsent(eligibilityConsent.getConsent());
          }
        });
    updateuserpreference();
  }

  @Override
  protected void onDestroy() {
    dbServiceSubscriber.closeRealmObj(realm);
    super.onDestroy();
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
    startActivityForResult(intent, CONSENT_RESPONSE_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CONSENT_RESPONSE_CODE) {
      if (resultCode == RESULT_OK) {
        Intent intent = new Intent(this, ConsentCompletedActivity.class);
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
          Intent intent = new Intent(this, StudyActivity.class);
          ComponentName cn = intent.getComponent();
          Intent mainIntent = Intent.makeRestartActivityTask(cn);
          startActivity(mainIntent);
          finish();
        } else {
          Intent intent = new Intent(this, StandaloneActivity.class);
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

  public void updateuserpreference() {
    AppController.getHelperProgressDialog().showProgress(EligibleActivity.this, "", "", false);

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
      studiestatus.put("status", StudyFragment.YET_TO_JOIN);
    } catch (JSONException e) {
      Logger.log(e);
    }

    studieslist.put(studiestatus);
    try {
      jsonObject.put("studies", studieslist);
    } catch (JSONException e) {
      Logger.log(e);
    }
    ParticipantDatastoreServerEnrollmentConfigEvent participantDatastoreServerEnrollmentConfigEvent =
        new ParticipantDatastoreServerEnrollmentConfigEvent(
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
    updatePreferenceEvent.setParticipantDatastoreServerEnrollmentConfigEvent(
        participantDatastoreServerEnrollmentConfigEvent);
    UserModulePresenter userModulePresenter = new UserModulePresenter();
    userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    dbServiceSubscriber.updateStudyPreferenceDB(
        this, getIntent().getStringExtra("studyId"), StudyFragment.YET_TO_JOIN, "", "", "", "", "");
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();

    dbServiceSubscriber.updateStudyPreferenceDB(
        this, getIntent().getStringExtra("studyId"), StudyFragment.YET_TO_JOIN, "", "", "", "", "");
  }
}
