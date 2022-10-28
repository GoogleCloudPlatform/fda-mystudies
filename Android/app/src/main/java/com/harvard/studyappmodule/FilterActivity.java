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

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatTextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.Categories;
import com.harvard.studyappmodule.studymodel.Filter;
import com.harvard.studyappmodule.studymodel.ParticipationStatus;
import com.harvard.studyappmodule.studymodel.StudyStatus;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterActivity extends AppCompatActivity {

  private AppCompatTextView cancelTextView;
  private AppCompatTextView applyTextView;
  private AppCompatTextView allStudiesLabel;
  private AppCompatTextView activeLabel;
  private AppCompatTextView closedLabel;
  private AppCompatTextView participationStatusLabel;
  private RelativeLayout participationStatusLayout;
  private AppCompatTextView inProgressLabel;
  private AppCompatTextView yettoJoinLabel;
  private AppCompatTextView completedLabel;
  private AppCompatTextView withdrawnLabel;
  private AppCompatTextView notEligibleLabel;
  private AppCompatCheckBox activeSelectBtn;
  private AppCompatCheckBox closedSelectBtn;
  private AppCompatCheckBox inProgressSelctBtn;
  private AppCompatCheckBox yettoJoinSelctBtn;
  private AppCompatCheckBox completedSelectBtn;
  private AppCompatCheckBox withdrawnSelectBtn;
  private AppCompatCheckBox notEligibleSelectBtn;
  private RelativeLayout inProgressLayout;
  private RelativeLayout yettoJoinLayout;
  private RelativeLayout completedLayout;
  private RelativeLayout withdrawnLayout;
  private RelativeLayout notEligibleLayout;
  private AppCompatCheckBox pausedSelectBtn;
  private AppCompatTextView pausedLabel;
  private String userId;
  private CustomFirebaseAnalytics analyticsInstance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    initializeXmlId();
    userId =
        AppController.getHelperSharedPreference()
            .readPreference(this, getResources().getString(R.string.userid), "");
    if (userId.equalsIgnoreCase("")) {
      disableParticipationStatusBtn();
    } else {
      enableParticipationStatusBtn();
    }
    String jsonObjectString =
        AppController.getHelperSharedPreference()
            .readPreference(FilterActivity.this, getString(R.string.json_object_filter), "");
    if (jsonObjectString.equalsIgnoreCase("")) {
      defaultSelectedFilterOption();
    } else {
      setFilterSelected(jsonObjectString);
    }

    setBackgroundColorView();
    setFont();
    bindEvents();
  }

  private void initializeXmlId() {
    allStudiesLabel = (AppCompatTextView) findViewById(R.id.mAllStudiesLabel);
    activeLabel = (AppCompatTextView) findViewById(R.id.mActiveLabel);
    pausedLabel = (AppCompatTextView) findViewById(R.id.mPausedLabel);
    closedLabel = (AppCompatTextView) findViewById(R.id.mClosedLabel);
    participationStatusLabel = (AppCompatTextView) findViewById(R.id.mParticipationStatusLabel);
    participationStatusLayout = findViewById(R.id.mParticipationStatusLayout);
    inProgressLabel = (AppCompatTextView) findViewById(R.id.mInProgressLabel);
    yettoJoinLabel = (AppCompatTextView) findViewById(R.id.mYettoJoinLabel);
    completedLabel = (AppCompatTextView) findViewById(R.id.mCompletedLabel);
    withdrawnLabel = (AppCompatTextView) findViewById(R.id.mWithdrawnLabel);
    notEligibleLabel = (AppCompatTextView) findViewById(R.id.mNotEligibleLabel);
    cancelTextView = (AppCompatTextView) findViewById(R.id.mCancelTextView);
    applyTextView = (AppCompatTextView) findViewById(R.id.mApplyTextView);

    activeSelectBtn = (AppCompatCheckBox) findViewById(R.id.mActiveSelectBtn);
    pausedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mPausedSelectBtn);
    closedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mClosedSelectBtn);
    inProgressSelctBtn = (AppCompatCheckBox) findViewById(R.id.mInProgressSelctBtn);
    yettoJoinSelctBtn = (AppCompatCheckBox) findViewById(R.id.mYettoJoinSelctBtn);
    completedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mCompletedSelectBtn);
    withdrawnSelectBtn = (AppCompatCheckBox) findViewById(R.id.mWithdrawnSelectBtn);
    notEligibleSelectBtn = (AppCompatCheckBox) findViewById(R.id.mNotEligibleSelectBtn);

    inProgressLayout = (RelativeLayout) findViewById(R.id.mInProgressLayout);
    yettoJoinLayout = (RelativeLayout) findViewById(R.id.mYettoJoinLayout);
    completedLayout = (RelativeLayout) findViewById(R.id.mCompletedLayout);
    withdrawnLayout = (RelativeLayout) findViewById(R.id.mWithdrawnLayout);
    notEligibleLayout = (RelativeLayout) findViewById(R.id.mNotEligibleLayout);
  }

  private void disableParticipationStatusBtn() {
    participationStatusLabel.setVisibility(View.GONE);
    participationStatusLayout.setVisibility(View.GONE);
    inProgressLayout.setVisibility(View.GONE);
    yettoJoinLayout.setVisibility(View.GONE);
    completedLayout.setVisibility(View.GONE);
    withdrawnLayout.setVisibility(View.GONE);
    notEligibleLayout.setVisibility(View.GONE);
  }

  private void enableParticipationStatusBtn() {
    participationStatusLabel.setVisibility(View.VISIBLE);
    participationStatusLayout.setVisibility(View.VISIBLE);
    inProgressLayout.setVisibility(View.VISIBLE);
    yettoJoinLayout.setVisibility(View.VISIBLE);
    completedLayout.setVisibility(View.VISIBLE);
    withdrawnLayout.setVisibility(View.VISIBLE);
    notEligibleLayout.setVisibility(View.VISIBLE);
  }

  // default filter criteria; if any changes here then accordingly make changes in
  // StudyFragment.defaultSelectedFilterOption()
  private void defaultSelectedFilterOption() {
    activeSelectBtn.setChecked(true);
    inProgressSelctBtn.setChecked(true);
    yettoJoinSelctBtn.setChecked(true);
  }

  private void setBackgroundColorView() {
    GradientDrawable bgShape1 = (GradientDrawable) cancelTextView.getBackground();
    bgShape1.setColor(getResources().getColor(R.color.tab_color));

    GradientDrawable bgShape2 = (GradientDrawable) applyTextView.getBackground();
    bgShape2.setColor(getResources().getColor(R.color.tab_color));
  }

  private void setFont() {
    try {
      allStudiesLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "medium"));
      activeLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      closedLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      participationStatusLabel.setTypeface(
          AppController.getTypeface(FilterActivity.this, "medium"));
      inProgressLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      yettoJoinLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      completedLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      withdrawnLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      notEligibleLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));

      cancelTextView.setTypeface(AppController.getTypeface(FilterActivity.this, "medium"));
      applyTextView.setTypeface(AppController.getTypeface(FilterActivity.this, "medium"));
      pausedLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents() {
    cancelTextView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.filter_cancel));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            finish();
          }
        });
    applyTextView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                getString(R.string.filter_apply));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            StudyStatus studyStatus = new StudyStatus();
            ParticipationStatus participationStatus = new ParticipationStatus();

            if (activeSelectBtn.isChecked()) {
              studyStatus.setActive(true);
            } else {
              studyStatus.setActive(false);
            }
            if (pausedSelectBtn.isChecked()) {
              studyStatus.setPaused(true);
            } else {
              studyStatus.setPaused(false);
            }
            if (closedSelectBtn.isChecked()) {
              studyStatus.setClosed(true);
            } else {
              studyStatus.setClosed(false);
            }
            if (inProgressSelctBtn.isChecked()) {
              participationStatus.setEnrolled(true);
            } else {
              participationStatus.setEnrolled(false);
            }

            if (yettoJoinSelctBtn.isChecked()) {
              participationStatus.setYetToEnroll(true);
            } else {
              participationStatus.setYetToEnroll(false);
            }

            if (completedSelectBtn.isChecked()) {
              participationStatus.setCompleted(true);
            } else {
              participationStatus.setCompleted(false);
            }

            if (withdrawnSelectBtn.isChecked()) {
              participationStatus.setWithdrawn(true);
            } else {
              participationStatus.setWithdrawn(false);
            }
            if (notEligibleSelectBtn.isChecked()) {
              participationStatus.setNotEligible(true);
            } else {
              participationStatus.setNotEligible(false);
            }
            Filter filter = new Filter();
            Categories categories = new Categories();
            filter.setStudyStatus(studyStatus);
            filter.setParticipationStatus(participationStatus);
            filter.setCategories(categories);

            boolean flag1;
            boolean flag2;
            // enable/disable Apply button
            if (!filter.getStudyStatus().isActive()
                && !filter.getStudyStatus().isPaused()
                && !filter.getStudyStatus().isClosed()) {
              flag1 = true;
            } else {
              flag1 = false;
            }

            if (userId.equalsIgnoreCase("")) {
              if (flag1) {
                Toast.makeText(
                        FilterActivity.this,
                        getResources().getString(R.string.search_data_empty),
                        Toast.LENGTH_LONG)
                    .show();
              } else {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(filter); // obj is your object

                try {
                  JSONObject jsonObj = new JSONObject(json);
                  AppController.getHelperSharedPreference()
                      .writePreference(
                          FilterActivity.this,
                          getString(R.string.json_object_filter),
                          jsonObj.toString());
                  finish();
                } catch (JSONException e) {
                  Logger.log(e);
                }
              }
            } else {
              if (!filter.getParticipationStatus().isEnrolled()
                  && !filter.getParticipationStatus().isYetToEnroll()
                  && !filter.getParticipationStatus().isCompleted()
                  && !filter.getParticipationStatus().isWithdrawn()
                  && !filter.getParticipationStatus().isNotEligible()) {
                flag2 = true;
              } else {
                flag2 = false;
              }

              if (flag1 || flag2) {
                Toast.makeText(
                        FilterActivity.this,
                        getResources().getString(R.string.search_data_empty),
                        Toast.LENGTH_LONG)
                    .show();
              } else {
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(filter); // obj is your object

                try {
                  JSONObject jsonObj = new JSONObject(json);
                  AppController.getHelperSharedPreference()
                      .writePreference(
                          FilterActivity.this,
                          getString(R.string.json_object_filter),
                          jsonObj.toString());
                  finish();
                } catch (JSONException e) {
                  Logger.log(e);
                }
              }
            }
          }
        });
  }

  private void setFilterSelected(String jsonObjectString) {

    try {
      JSONObject jsonObj = new JSONObject(jsonObjectString);
      JSONObject studyStatus = jsonObj.getJSONObject("studyStatus");

      if (studyStatus.getBoolean("active")) {
        activeSelectBtn.setChecked(true);
      } else {
        activeSelectBtn.setChecked(false);
      }
      if (studyStatus.getBoolean("paused")) {
        pausedSelectBtn.setChecked(true);
      } else {
        pausedSelectBtn.setChecked(false);
      }
      if (studyStatus.getBoolean("closed")) {
        closedSelectBtn.setChecked(true);
      } else {
        closedSelectBtn.setChecked(false);
      }
      JSONObject participationStatus = jsonObj.getJSONObject("participationStatus");
      if (participationStatus.getBoolean("enrolled")) {
        inProgressSelctBtn.setChecked(true);
      } else {
        inProgressSelctBtn.setChecked(false);
      }
      if (participationStatus.getBoolean("yetToEnroll")) {
        yettoJoinSelctBtn.setChecked(true);
      } else {
        yettoJoinSelctBtn.setChecked(false);
      }
      if (participationStatus.getBoolean("completed")) {
        completedSelectBtn.setChecked(true);
      } else {
        completedSelectBtn.setChecked(false);
      }
      if (participationStatus.getBoolean("withdrawn")) {
        withdrawnSelectBtn.setChecked(true);
      } else {
        withdrawnSelectBtn.setChecked(false);
      }
      if (participationStatus.getBoolean("notEligible")) {
        notEligibleSelectBtn.setChecked(true);
      } else {
        notEligibleSelectBtn.setChecked(false);
      }

    } catch (JSONException e) {
      Logger.log(e);
    }
  }
}
