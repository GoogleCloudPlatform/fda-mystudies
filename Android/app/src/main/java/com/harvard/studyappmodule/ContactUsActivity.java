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
import com.harvard.webservicemodule.events.ParticipantDatastoreServerConfigEvent;
import java.util.HashMap;

public class ContactUsActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

  private RelativeLayout backBtn;
  private AppCompatTextView title;
  private AppCompatTextView firstNameText;
  private AppCompatTextView emailText;
  private AppCompatTextView subjectText;
  private AppCompatTextView messageText;
  private AppCompatEditText email;
  private AppCompatEditText subject;
  private AppCompatEditText message;
  private AppCompatEditText firstName;
  private static final int CONTACT_US = 15;
  private AppCompatTextView submitButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contact_us);
    initializeXmlId();
    setFont();
    bindEvents();
    email.setText(
        ""
            + AppController.getHelperSharedPreference()
                .readPreference(this, getString(R.string.email), ""));
  }

  private void initializeXmlId() {
    backBtn = (RelativeLayout) findViewById(R.id.backBtn);

    title = (AppCompatTextView) findViewById(R.id.title);
    firstNameText = (AppCompatTextView) findViewById(R.id.firstName);
    emailText = (AppCompatTextView) findViewById(R.id.email_label);
    subjectText = (AppCompatTextView) findViewById(R.id.subject_label);
    messageText = (AppCompatTextView) findViewById(R.id.message_label);

    firstName = (AppCompatEditText) findViewById(R.id.edittxt_firstName);
    email = (AppCompatEditText) findViewById(R.id.edittxt_email);
    subject = (AppCompatEditText) findViewById(R.id.edittxt_subject);
    message = (AppCompatEditText) findViewById(R.id.edittxt_message);

    submitButton = (AppCompatTextView) findViewById(R.id.submitButton);
  }

  private void setFont() {
    try {
      title.setTypeface(AppController.getTypeface(ContactUsActivity.this, "medium"));
      firstNameText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      emailText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      subjectText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      messageText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));

      firstName.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      email.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      subject.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
      message.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));

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
            if (firstName.getText().toString().equalsIgnoreCase("")
                && email.getText().toString().equalsIgnoreCase("")
                && subject.getText().toString().equalsIgnoreCase("")
                && message.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.enter_all_field_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (firstName.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.first_name_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (email.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.email_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (!AppController.getHelperIsValidEmail(email.getText().toString())) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.email_validation),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (subject.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.subject_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else if (message.getText().toString().equalsIgnoreCase("")) {
              Toast.makeText(
                      ContactUsActivity.this,
                      getResources().getString(R.string.message_empty),
                      Toast.LENGTH_SHORT)
                  .show();
            } else {
              callContactUsWebservice();
            }
          }
        });
  }

  private void callContactUsWebservice() {
    AppController.getHelperProgressDialog().showProgress(ContactUsActivity.this, "", "", false);

    HashMap<String, String> params = new HashMap<>();
    params.put("subject", subject.getText().toString());
    params.put("body", message.getText().toString());
    params.put("firstName", firstName.getText().toString());
    params.put("email", email.getText().toString());
    ParticipantDatastoreServerConfigEvent participantDatastoreServerConfigEvent =
        new ParticipantDatastoreServerConfigEvent(
            "post",
            Urls.CONTACT_US,
            CONTACT_US,
            ContactUsActivity.this,
            ReachOut.class,
            params,
            null,
            null,
            false,
            this);
    ContactUsEvent contactUsEvent = new ContactUsEvent();
    contactUsEvent.setParticipantDatastoreServerConfigEvent(participantDatastoreServerConfigEvent);
    StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
    studyModulePresenter.performContactUsEvent(contactUsEvent);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    if (responseCode == CONTACT_US) {
      if (response != null) {
        AppController.getHelperProgressDialog().dismissDialog();
        Toast.makeText(
                ContactUsActivity.this,
                getResources().getString(R.string.contact_us_message),
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
      Toast.makeText(ContactUsActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(ContactUsActivity.this, errormsg);
    } else {
      if (responseCode == CONTACT_US) {
        Toast.makeText(ContactUsActivity.this, errormsg, Toast.LENGTH_SHORT).show();
      }
    }
  }
}
