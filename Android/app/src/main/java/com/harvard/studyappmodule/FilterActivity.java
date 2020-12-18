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

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.Categories;
import com.harvard.studyappmodule.studymodel.Filter;
import com.harvard.studyappmodule.studymodel.ParticipationStatus;
import com.harvard.studyappmodule.studymodel.StudyStatus;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterActivity extends AppCompatActivity {

  private AppCompatTextView cancelTextView;
  private AppCompatTextView applyTextView;
  private AppCompatTextView allStudiesLabel;
  private AppCompatTextView activeLabel;
  private AppCompatTextView closedLabel;
  private AppCompatTextView upcomingLabel;
  private AppCompatTextView participationStatusLabel;
  private RelativeLayout participationStatusLayout;
  private AppCompatTextView inProgressLabel;
  private AppCompatTextView yettoJoinLabel;
  private AppCompatTextView bookmarkedLabel;
  private AppCompatTextView completedLabel;
  private AppCompatTextView withdrawnLabel;
  private AppCompatTextView notEligibleLabel;
  private AppCompatTextView categoriesLabel;
  private AppCompatTextView category1Label;
  private AppCompatTextView category2Label;
  private AppCompatTextView category3Label;
  private AppCompatTextView category4Label;
  private AppCompatTextView category5Label;
  private AppCompatTextView category6Label;
  private AppCompatTextView category7Label;
  private AppCompatTextView category8Label;
  private AppCompatTextView category9Label;
  private AppCompatTextView category10Label;
  private AppCompatCheckBox activeSelectBtn;
  private AppCompatCheckBox closedSelectBtn;
  private AppCompatCheckBox upcomingSelectBtn;
  private AppCompatCheckBox inProgressSelctBtn;
  private AppCompatCheckBox yettoJoinSelctBtn;
  private AppCompatCheckBox bookmarkedSelctBtn;
  private AppCompatCheckBox completedSelectBtn;
  private AppCompatCheckBox withdrawnSelectBtn;
  private AppCompatCheckBox notEligibleSelectBtn;
  private AppCompatCheckBox category1SelectBtn;
  private AppCompatCheckBox category2SelectBtn;
  private AppCompatCheckBox category3SelectBtn;
  private AppCompatCheckBox category4SelectBtn;
  private AppCompatCheckBox category5SelectBtn;
  private AppCompatCheckBox category6SelectBtn;
  private AppCompatCheckBox category7SelectBtn;
  private AppCompatCheckBox category8SelectBtn;
  private AppCompatCheckBox category9SelectBtn;
  private AppCompatCheckBox category10SelectBtn;

  private RelativeLayout inProgressLayout;
  private RelativeLayout yettoJoinLayout;
  private RelativeLayout completedLayout;
  private RelativeLayout withdrawnLayout;
  private RelativeLayout notEligibleLayout;
  private RelativeLayout bookmarkedLayout;

  private AppCompatCheckBox pausedSelectBtn;
  private AppCompatTextView pausedLabel;
  private String userId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_filter);
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
    upcomingLabel = (AppCompatTextView) findViewById(R.id.mUpcomingLabel);
    participationStatusLabel = (AppCompatTextView) findViewById(R.id.mParticipationStatusLabel);
    participationStatusLayout = findViewById(R.id.mParticipationStatusLayout);
    inProgressLabel = (AppCompatTextView) findViewById(R.id.mInProgressLabel);
    yettoJoinLabel = (AppCompatTextView) findViewById(R.id.mYettoJoinLabel);
    bookmarkedLabel = (AppCompatTextView) findViewById(R.id.mBookmarkedLabel);
    completedLabel = (AppCompatTextView) findViewById(R.id.mCompletedLabel);
    withdrawnLabel = (AppCompatTextView) findViewById(R.id.mWithdrawnLabel);
    notEligibleLabel = (AppCompatTextView) findViewById(R.id.mNotEligibleLabel);
    categoriesLabel = (AppCompatTextView) findViewById(R.id.mCategoriesLabel);
    category1Label = (AppCompatTextView) findViewById(R.id.mCategory1Label);
    category2Label = (AppCompatTextView) findViewById(R.id.mCategory2Label);
    category3Label = (AppCompatTextView) findViewById(R.id.mCategory3Label);
    category4Label = (AppCompatTextView) findViewById(R.id.mCategory4Label);
    category5Label = (AppCompatTextView) findViewById(R.id.mCategory5Label);
    category6Label = (AppCompatTextView) findViewById(R.id.mCategory6Label);
    category7Label = (AppCompatTextView) findViewById(R.id.mCategory7Label);
    category8Label = (AppCompatTextView) findViewById(R.id.mCategory8Label);
    category9Label = (AppCompatTextView) findViewById(R.id.mCategory9Label);
    category10Label = (AppCompatTextView) findViewById(R.id.mCategory10Label);
    cancelTextView = (AppCompatTextView) findViewById(R.id.mCancelTextView);
    applyTextView = (AppCompatTextView) findViewById(R.id.mApplyTextView);

    activeSelectBtn = (AppCompatCheckBox) findViewById(R.id.mActiveSelectBtn);
    pausedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mPausedSelectBtn);
    closedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mClosedSelectBtn);
    upcomingSelectBtn = (AppCompatCheckBox) findViewById(R.id.mUpcomingSelectBtn);
    inProgressSelctBtn = (AppCompatCheckBox) findViewById(R.id.mInProgressSelctBtn);
    yettoJoinSelctBtn = (AppCompatCheckBox) findViewById(R.id.mYettoJoinSelctBtn);
    bookmarkedSelctBtn = (AppCompatCheckBox) findViewById(R.id.mBookmarkedSelctBtn);
    completedSelectBtn = (AppCompatCheckBox) findViewById(R.id.mCompletedSelectBtn);
    withdrawnSelectBtn = (AppCompatCheckBox) findViewById(R.id.mWithdrawnSelectBtn);
    notEligibleSelectBtn = (AppCompatCheckBox) findViewById(R.id.mNotEligibleSelectBtn);
    category1SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory1SelectBtn);
    category2SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory2SelectBtn);
    category3SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory3SelectBtn);
    category4SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory4SelectBtn);
    category5SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory5SelectBtn);
    category6SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory6SelectBtn);
    category7SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory7SelectBtn);
    category8SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory8SelectBtn);
    category9SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory9SelectBtn);
    category10SelectBtn = (AppCompatCheckBox) findViewById(R.id.mCategory10SelectBtn);

    inProgressLayout = (RelativeLayout) findViewById(R.id.mInProgressLayout);
    yettoJoinLayout = (RelativeLayout) findViewById(R.id.mYettoJoinLayout);
    completedLayout = (RelativeLayout) findViewById(R.id.mCompletedLayout);
    withdrawnLayout = (RelativeLayout) findViewById(R.id.mWithdrawnLayout);
    notEligibleLayout = (RelativeLayout) findViewById(R.id.mNotEligibleLayout);
    bookmarkedLayout = (RelativeLayout) findViewById(R.id.mBookmarkedLayout);
  }

  private void disableParticipationStatusBtn() {
    participationStatusLabel.setVisibility(View.GONE);
    participationStatusLayout.setVisibility(View.GONE);
    inProgressLayout.setVisibility(View.GONE);
    yettoJoinLayout.setVisibility(View.GONE);
    completedLayout.setVisibility(View.GONE);
    withdrawnLayout.setVisibility(View.GONE);
    notEligibleLayout.setVisibility(View.GONE);
    bookmarkedLayout.setVisibility(View.GONE);
  }

  private void enableParticipationStatusBtn() {
    participationStatusLabel.setVisibility(View.VISIBLE);
    participationStatusLayout.setVisibility(View.VISIBLE);
    inProgressLayout.setVisibility(View.VISIBLE);
    yettoJoinLayout.setVisibility(View.VISIBLE);
    completedLayout.setVisibility(View.VISIBLE);
    withdrawnLayout.setVisibility(View.VISIBLE);
    notEligibleLayout.setVisibility(View.VISIBLE);
    bookmarkedLayout.setVisibility(View.VISIBLE);
  }

  // default filter criteria; if any changes here then accordingly make changes in
  // StudyFragment.defaultSelectedFilterOption()
  private void defaultSelectedFilterOption() {
    activeSelectBtn.setChecked(true);
    upcomingSelectBtn.setChecked(true);
    inProgressSelctBtn.setChecked(true);
    yettoJoinSelctBtn.setChecked(true);
    bookmarkedSelctBtn.setChecked(false);
    category1SelectBtn.setChecked(true);
    category2SelectBtn.setChecked(true);
    category3SelectBtn.setChecked(true);
    category4SelectBtn.setChecked(true);
    category5SelectBtn.setChecked(true);
    category6SelectBtn.setChecked(true);
    category7SelectBtn.setChecked(true);
    category8SelectBtn.setChecked(true);
    category9SelectBtn.setChecked(true);
    category10SelectBtn.setChecked(true);
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
      upcomingLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      participationStatusLabel.setTypeface(
          AppController.getTypeface(FilterActivity.this, "medium"));
      inProgressLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      yettoJoinLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      bookmarkedLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      completedLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      withdrawnLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      notEligibleLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      categoriesLabel.setTypeface(AppController.getTypeface(FilterActivity.this, "medium"));
      category1Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category2Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category3Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category4Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category5Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category6Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category7Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category8Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category9Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));
      category10Label.setTypeface(AppController.getTypeface(FilterActivity.this, "regular"));

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
            finish();
          }
        });
    applyTextView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {

            Filter filter = new Filter();
            StudyStatus studyStatus = new StudyStatus();
            ParticipationStatus participationStatus = new ParticipationStatus();
            Categories categories = new Categories();

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
            if (upcomingSelectBtn.isChecked()) {
              studyStatus.setUpcoming(true);
            } else {
              studyStatus.setUpcoming(false);
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
            if (bookmarkedSelctBtn.isChecked()) {
              filter.setBookmarked(true);
            } else {
              filter.setBookmarked(false);
            }
            if (category1SelectBtn.isChecked()) {
              categories.setBiologicsSafety(true);
            } else {
              categories.setBiologicsSafety(false);
            }
            if (category2SelectBtn.isChecked()) {
              categories.setClinicalTrials(true);
            } else {
              categories.setClinicalTrials(false);
            }
            if (category3SelectBtn.isChecked()) {
              categories.setCosmeticsSafety(true);
            } else {
              categories.setCosmeticsSafety(false);
            }
            if (category4SelectBtn.isChecked()) {
              categories.setDrugSafety(true);
            } else {
              categories.setDrugSafety(false);
            }
            if (category5SelectBtn.isChecked()) {
              categories.setFoodSafety(true);
            } else {
              categories.setFoodSafety(false);
            }
            if (category6SelectBtn.isChecked()) {
              categories.setMedicalDeviceSafety(true);
            } else {
              categories.setMedicalDeviceSafety(false);
            }
            if (category7SelectBtn.isChecked()) {
              categories.setObservationalStudies(true);
            } else {
              categories.setObservationalStudies(false);
            }
            if (category8SelectBtn.isChecked()) {
              categories.setPublicHealth(true);
            } else {
              categories.setPublicHealth(false);
            }
            if (category9SelectBtn.isChecked()) {
              categories.setRadiationEmittingProducts(true);
            } else {
              categories.setRadiationEmittingProducts(false);
            }
            if (category10SelectBtn.isChecked()) {
              categories.setTobaccoUse(true);
            } else {
              categories.setTobaccoUse(false);
            }

            filter.setStudyStatus(studyStatus);
            filter.setParticipationStatus(participationStatus);
            filter.setCategories(categories);

            boolean flag1;
            boolean flag2;
            boolean flag3;
            // enable/disable Apply button
            if (!filter.getStudyStatus().isActive()
                && !filter.getStudyStatus().isPaused()
                && !filter.getStudyStatus().isUpcoming()
                && !filter.getStudyStatus().isClosed()) {
              flag1 = true;
            } else {
              flag1 = false;
            }

            if (!filter.getCategories().isBiologicsSafety()
                && !filter.getCategories().isClinicalTrials()
                && !filter.getCategories().isCosmeticsSafety()
                && !filter.getCategories().isDrugSafety()
                && !filter.getCategories().isMedicalDeviceSafety()
                && !filter.getCategories().isObservationalStudies()
                && !filter.getCategories().isPublicHealth()
                && !filter.getCategories().isRadiationEmittingProducts()
                && !filter.getCategories().isTobaccoUse()) {
              flag3 = true;
            } else {
              flag3 = false;
            }

            if (userId.equalsIgnoreCase("")) {
              if (flag1 || flag3) {
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

              if (flag1 || flag2 || flag3) {
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

      if (jsonObj.getBoolean("bookmarked")) {
        bookmarkedSelctBtn.setChecked(true);
      }
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
      if (studyStatus.getBoolean("upcoming")) {
        upcomingSelectBtn.setChecked(true);
      } else {
        upcomingSelectBtn.setChecked(false);
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
      JSONObject categories = jsonObj.getJSONObject("categories");
      if (categories.getBoolean("biologicsSafety")) {
        category1SelectBtn.setChecked(true);
      } else {
        category1SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("clinicalTrials")) {
        category2SelectBtn.setChecked(true);
      } else {
        category2SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("cosmeticsSafety")) {
        category3SelectBtn.setChecked(true);
      } else {
        category3SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("drugSafety")) {
        category4SelectBtn.setChecked(true);
      } else {
        category4SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("foodSafety")) {
        category5SelectBtn.setChecked(true);
      } else {
        category5SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("medicalDeviceSafety")) {
        category6SelectBtn.setChecked(true);
      } else {
        category6SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("observationalStudies")) {
        category7SelectBtn.setChecked(true);
      } else {
        category7SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("publicHealth")) {
        category8SelectBtn.setChecked(true);
      } else {
        category8SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("radiationEmittingProducts")) {
        category9SelectBtn.setChecked(true);
      } else {
        category9SelectBtn.setChecked(false);
      }
      if (categories.getBoolean("tobaccoUse")) {
        category10SelectBtn.setChecked(true);
      } else {
        category10SelectBtn.setChecked(false);
      }

    } catch (JSONException e) {
      Logger.log(e);
    }
  }
}
