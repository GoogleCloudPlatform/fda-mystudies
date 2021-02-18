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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.harvard.R;
import com.harvard.studyappmodule.surveyscheduler.model.ActivityStatus;
import java.util.ArrayList;

public class CustomActivitiesDailyDialogClass extends Dialog implements View.OnClickListener {

  public Context context;
  private int limit;
  private int selectedTime;
  private int selectedDateBefore = 0;
  private ArrayList<String> scheduledTime;
  private boolean isClickableItem;
  private DialogClick dialogClick;
  private String status;
  private ActivityStatus activityStatus;

  CustomActivitiesDailyDialogClass(
          Context context,
          ArrayList<String> scheduledTime,
          int selectedTime,
          boolean isClickableItem,
          DialogClick dialogClick, String status, ActivityStatus activityStatus) {
    super(context);
    this.context = context;
    this.scheduledTime = scheduledTime;
    this.selectedTime = selectedTime;
    this.isClickableItem = isClickableItem;
    this.dialogClick = dialogClick;
    this.status = status;
    this.activityStatus = activityStatus;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_cutom_activities_daily_dialog);
    // for dialog screen to get full width using this
    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    RelativeLayout closeBtnLayout = (RelativeLayout) findViewById(R.id.mCloseBtnLayout);
    closeBtnLayout.setOnClickListener(this);
    LinearLayout l = (LinearLayout) findViewById(R.id.lin_layout_hours);
    if (status
            .equalsIgnoreCase(SurveyActivitiesFragment.STATUS_CURRENT)
            && (activityStatus.getStatus().equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)
            || activityStatus.getStatus().equalsIgnoreCase(SurveyActivitiesFragment.INCOMPLETE)
            || activityStatus.getCurrentRunId() == 0)) {
      selectedTime++;
    }
    for (int i = 0; i < scheduledTime.size(); i++) {
      TextView textDynamic = new TextView(getContext());
      textDynamic.setLayoutParams(
          new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT,
              LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
      textDynamic.setText(scheduledTime.get(i));
      textDynamic.setBackgroundColor(Color.WHITE);
      if (i == selectedTime) {
        selectedDateBefore = 1;
        textDynamic.setTextColor(context.getResources().getColor(R.color.colorPrimary));
      } else if (selectedDateBefore == 0) {
        textDynamic.setTextColor(context.getResources().getColor(R.color.colorSecondaryBg));
      } else {
        textDynamic.setTextColor(context.getResources().getColor(R.color.colorSecondary));
      }
      textDynamic.setTextSize(16);
      textDynamic.setGravity(Gravity.CENTER);
      textDynamic.setPadding(0, 20, 0, 20);
      if (isClickableItem) {
        final int finalI = i;
        textDynamic.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                dialogClick.clicked(finalI);
                dismiss();
              }
            });
      }
      l.addView(textDynamic);
    }
  }

  @Override
  public void onClick(View v) {
    dismiss();
  }

  public interface DialogClick {
    void clicked(int positon);
  }
}
