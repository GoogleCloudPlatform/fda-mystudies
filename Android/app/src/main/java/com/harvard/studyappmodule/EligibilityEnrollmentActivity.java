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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.studyappmodule.enroll.EnrollData;
import com.harvard.studyappmodule.events.VerifyEnrollmentIdEvent;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantEnrollmentDatastoreConfigEvent;
import java.util.HashMap;

public class EligibilityEnrollmentActivity extends AppCompatActivity
    implements ApiCall.OnAsyncRequestComplete {

  private static final int VERIFY_ENROLLMENT_ID = 101;
  private RelativeLayout backBtn;
  private RelativeLayout cancelBtn;
  private AppCompatTextView title;
  private TextView enrollmentdesc;
  private EditText enrollmentID;
  private TextView submit;
  private String enteredId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_eligibility_enrollment);
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
    enrollmentdesc.setText(getIntent().getStringExtra("enrollmentDesc"));
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    cancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
    enrollmentdesc = (TextView) findViewById(R.id.enrollmentdesc);
    enrollmentID = (EditText) findViewById(R.id.enrollmentidtxt);
    submit = (TextView) findViewById(R.id.submitbutton);
  }

  private void setTextForView() {
    title.setText(getResources().getString(R.string.eligibility));
    cancelBtn.setVisibility(View.GONE);
    enrollmentID.setText("");
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(EligibilityEnrollmentActivity.this, "medium"));
      enrollmentdesc.setTypeface(
          AppController.getTypeface(EligibilityEnrollmentActivity.this, "regular"));
      enrollmentID.setTypeface(
          AppController.getTypeface(EligibilityEnrollmentActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    AppController.getHelperHideKeyboard(this);
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            finish();
          }
        });
    submit.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (!enrollmentID.getText().toString().equalsIgnoreCase("")) {
              callValidateEnrollmentId();
            } else {
              Toast.makeText(
                      EligibilityEnrollmentActivity.this,
                      R.string.enter_enrollment_id,
                      Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });
  }

  private void callValidateEnrollmentId() {
    AppController.getHelperProgressDialog()
        .showProgress(EligibilityEnrollmentActivity.this, "", "", false);

    HashMap<String, String> params = new HashMap<>();
    params.put("studyId", "" + getIntent().getStringExtra("studyId"));
    params.put("token", enrollmentID.getText().toString());

    HashMap<String, String> header = new HashMap<>();
    header.put(
        "userId",
        SharedPreferenceHelper.readPreference(
            EligibilityEnrollmentActivity.this, getString(R.string.userid), ""));
    header.put(
        "Authorization",
        "Bearer "
            + SharedPreferenceHelper.readPreference(
                EligibilityEnrollmentActivity.this, getString(R.string.auth), ""));

    ParticipantEnrollmentDatastoreConfigEvent participantEnrollmentDatastoreConfigEvent =
        new ParticipantEnrollmentDatastoreConfigEvent(
            "post_json",
            Urls.VALIDATE_ENROLLMENT_ID,
            VERIFY_ENROLLMENT_ID,
            EligibilityEnrollmentActivity.this,
            EnrollData.class,
            params,
            header,
            null,
            false,
            EligibilityEnrollmentActivity.this);
    VerifyEnrollmentIdEvent verifyEnrollmentIdEvent = new VerifyEnrollmentIdEvent();
    verifyEnrollmentIdEvent.setParticipantEnrollmentDatastoreConfigEvent(
        participantEnrollmentDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performVerifyEnrollmentId(verifyEnrollmentIdEvent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 12345) {
      if (resultCode == RESULT_OK) {
        Intent intent = new Intent();
        intent.putExtra("enrollId", "" + enteredId);
        setResult(RESULT_OK, intent);
        finish();
      }
    }
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == VERIFY_ENROLLMENT_ID) {
      EnrollData enrollData = (EnrollData) response;
      if (enrollData != null) {

        Intent intent = new Intent(this, EnrollmentValidatedActivity.class);
        intent.putExtra("enrollId", enrollmentID.getText().toString());
        intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
        intent.putExtra("title", getIntent().getStringExtra("title"));
        intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
        intent.putExtra("type", getIntent().getStringExtra("type"));
        enteredId = enrollmentID.getText().toString();
        enrollmentID.setText("");
        if (getIntent().getStringExtra("eligibility").equalsIgnoreCase("combined")) {
          Intent intent1 = new Intent();
          intent1.putExtra("enrollId", "" + enteredId);
          setResult(RESULT_OK, intent1);
          finish();
        } else {
          startActivity(intent);
          finish();
        }
      } else {
        Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
  }
}
