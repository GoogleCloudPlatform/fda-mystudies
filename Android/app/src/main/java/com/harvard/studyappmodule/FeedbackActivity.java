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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.studyappmodule.events.ContactUsEvent;
import com.harvard.studyappmodule.studymodel.ReachOut;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import com.harvard.utils.Urls;
import com.harvard.webservicemodule.apihelper.ApiCall;
import com.harvard.webservicemodule.events.ParticipantDatastoreConfigEvent;
import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
  private AppCompatTextView title;
  private AppCompatTextView feedbackText;
  private AppCompatEditText feedbackEdittext;
  private AppCompatEditText subject;
  private RelativeLayout backBtn;
  private AppCompatTextView submitButton;
  private static final int FEEDBACK = 16;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
    initializeXmlId();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);
    title = (AppCompatTextView) findViewById(R.id.title);
    feedbackText = (AppCompatTextView) findViewById(R.id.feedback_label);
    feedbackEdittext = (AppCompatEditText) findViewById(R.id.edittxt_feedback);
    subject = (AppCompatEditText) findViewById(R.id.subject);
    submitButton = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(FeedbackActivity.this, "medium"));
      feedbackText.setTypeface(AppController.getTypeface(FeedbackActivity.this, "regular"));

      feedbackEdittext.setTypeface(AppController.getTypeface(FeedbackActivity.this, "regular"));

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    backBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            try {
              InputMethodManager inputMethodManager =
                  (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
              inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
              Logger.log(e);
            }
            finish();
          }
        });
    submitButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (subject.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      FeedbackActivity.this,
                      getResources().getString(R.string.subject_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (feedbackEdittext.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      FeedbackActivity.this,
                      getResources().getString(R.string.feedback_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else {
              callmFeedbackWebservice();
            }
          }
        });
  }

  private void callmFeedbackWebservice() {
    AppController.getHelperProgressDialog().showProgress(FeedbackActivity.this, "", "", false);
    ContactUsEvent contactUsEvent = new ContactUsEvent();
    HashMap<String, String> params = new HashMap<>();
    params.put("subject", subject.getText().toString());
    params.put("body", feedbackEdittext.getText().toString());
    ParticipantDatastoreConfigEvent participantDatastoreConfigEvent =
        new ParticipantDatastoreConfigEvent(
            "post",
            Urls.FEEDBACK,
            FEEDBACK,
            FeedbackActivity.this,
            ReachOut.class,
            params,
            null,
            null,
            false,
            this);

    contactUsEvent.setParticipantDatastoreConfigEvent(participantDatastoreConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performContactUsEvent(contactUsEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == FEEDBACK) {
      if (response != null) {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(
                FeedbackActivity.this,
                getResources().getString(R.string.feedback_submit_success),
                Toast.LENGTH_SHORT)
            .show();
        finish();
      }
    }
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(FeedbackActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(FeedbackActivity.this, errormsg);
    } else {
      if (responseCode == FEEDBACK) {
        Toast.makeText(FeedbackActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      }
    }
  }
}
