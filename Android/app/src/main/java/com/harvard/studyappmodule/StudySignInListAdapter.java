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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import java.util.ArrayList;

public class StudySignInListAdapter extends RecyclerView.Adapter<StudySignInListAdapter.Holder> {
  private final Context context;
  private ArrayList<String> items = new ArrayList<>();
  private CustomFirebaseAnalytics analyticsInstance;

  StudySignInListAdapter(Context context, ArrayList<String> items) {
    this.context = context;
    this.items.addAll(items);
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.study_sign_in_list_item, parent, false);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(context);
    return new Holder(v);
  }

  @Override
  public int getItemCount() {
    if (items == null) {
      return 0;
    }
    return items.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    RelativeLayout container;
    AppCompatImageView stateIcon;
    AppCompatTextView state;
    AppCompatTextView studyTitle;
    AppCompatTextView studyTitleLatin;
    AppCompatTextView sponser;

    Holder(View itemView) {
      super(itemView);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
      stateIcon = (AppCompatImageView) itemView.findViewById(R.id.stateIcon);
      state = (AppCompatTextView) itemView.findViewById(R.id.state);
      studyTitle = (AppCompatTextView) itemView.findViewById(R.id.study_title);
      studyTitleLatin = (AppCompatTextView) itemView.findViewById(R.id.study_title_latin);
      sponser = (AppCompatTextView) itemView.findViewById(R.id.sponser);
      setFont();
    }

    private void setFont() {
      try {
        state.setTypeface(AppController.getTypeface(context, "medium"));
        studyTitle.setTypeface(AppController.getTypeface(context, "medium"));
        studyTitleLatin.setTypeface(AppController.getTypeface(context, "regular"));
        sponser.setTypeface(AppController.getTypeface(context, "regular"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, final int position) {
    final int i = holder.getAdapterPosition();
    try {
      // changing the bg color of the round shape
      GradientDrawable bgShape = (GradientDrawable) holder.stateIcon.getBackground();
      if (i == 3) {
        bgShape.setColor(context.getResources().getColor(R.color.colorPrimary));
      } else {
        bgShape.setColor(context.getResources().getColor(R.color.bullet_green_color));
      }

      if (i == 3) {
        holder.state.setText(context.getResources().getString(R.string.upcoming_caps));
      } else {
        holder.state.setText(context.getResources().getString(R.string.active1));
      }

      holder.stateIcon.setImageResource(R.drawable.bullet);

      if (i == 1) {
        holder.studyTitle.setText(
            context.getResources().getString(R.string.study_for_fitness) + " ");
      } else {
        holder.studyTitle.setText(
            context.getResources().getString(R.string.study_pregnant_women) + " ");
      }
      holder.studyTitleLatin.setText("Lorem ipsum dolor sit amet ");
      String category = context.getResources().getString(R.string.pregnancy);
      if (i == 1) {
        category = context.getResources().getString(R.string.fitness);
      }
      holder.sponser.setText(
          context.getResources().getString(R.string.sponsor_name) + "  |  " + category);

      holder.container.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  context.getString(R.string.study_signup_list));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              Toast.makeText(context, "GOTO Details Screen", Toast.LENGTH_LONG).show();
            }
          });

    } catch (Exception e) {
      Logger.log(e);
    }
  }
}
