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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.gatewaymodule.GatewayActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import java.util.ArrayList;

public class StudySignInActivity extends AppCompatActivity {

  private RecyclerView studyRecyclerView;
  private AppCompatTextView fdaListenTitle;
  private RelativeLayout filterBtn;
  private RelativeLayout resourceBtn;
  private AppCompatTextView signInButton;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sign_in_study);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    initializeXmlId();
    setTextForView();
    setFont();
    bindEvents();
    setRecyclerView();
    Intent intent = new Intent(StudySignInActivity.this, GatewayActivity.class);
    startActivity(intent);
  }

  private void initializeXmlId() {

    fdaListenTitle = (AppCompatTextView) findViewById(R.id.fda_listen);
    filterBtn = (RelativeLayout) findViewById(R.id.filterBtn);
    resourceBtn = (RelativeLayout) findViewById(R.id.resourceBtn);
    studyRecyclerView = (RecyclerView) findViewById(R.id.studyRecyclerView);
    signInButton = (AppCompatTextView) findViewById(R.id.signInButton);
  }

  private void setTextForView() {
    fdaListenTitle.setText(getResources().getString(R.string.app_name));
  }

  private void setFont() {
    try {
      fdaListenTitle.setTypeface(AppController.getTypeface(StudySignInActivity.this, "bold"));
      signInButton.setTypeface(AppController.getTypeface(StudySignInActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    filterBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.filter_clicked));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            Toast.makeText(
                    StudySignInActivity.this,
                    getResources().getString(R.string.filter_clicked),
                    Toast.LENGTH_LONG)
                .show();
          }
        });
    resourceBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.resource_btn_clicked));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            Toast.makeText(
                    StudySignInActivity.this,
                    getResources().getString(R.string.resource_btn_clicked),
                    Toast.LENGTH_LONG)
                .show();
          }
        });
    signInButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.sign_in_btn_clicked));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            Toast.makeText(
                    StudySignInActivity.this,
                    getResources().getString(R.string.sign_in_btn_clicked),
                    Toast.LENGTH_LONG)
                .show();
          }
        });
  }

  private void setRecyclerView() {
    studyRecyclerView.setLayoutManager(new LinearLayoutManager(StudySignInActivity.this));
    studyRecyclerView.setNestedScrollingEnabled(false);
    ArrayList<String> videoList = new ArrayList<>();
    videoList.add("abc");
    videoList.add("abc");
    videoList.add("abc");
    videoList.add("abc");
    StudySignInListAdapter studyVideoAdapter =
        new StudySignInListAdapter(StudySignInActivity.this, videoList);
    studyRecyclerView.setAdapter(studyVideoAdapter);
  }
}
