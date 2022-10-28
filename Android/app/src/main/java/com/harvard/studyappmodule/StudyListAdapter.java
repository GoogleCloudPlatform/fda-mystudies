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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.studyappmodule.surveyscheduler.model.CompletionAdherence;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import io.realm.RealmList;
import java.util.ArrayList;

public class StudyListAdapter extends RecyclerView.Adapter<StudyListAdapter.Holder> {
  private final Context context;
  private RealmList<StudyList> items;
  private StudyFragment studyFragment;
  private ArrayList<CompletionAdherence> completionAdherenceCalcs;
  private boolean click = true;
  private CustomFirebaseAnalytics analyticsInstance;

  StudyListAdapter(
      Context context,
      RealmList<StudyList> items,
      StudyFragment studyFragment,
      ArrayList<CompletionAdherence> completionAdherenceCalcs) {
    this.context = context;
    this.items = items;
    this.studyFragment = studyFragment;
    this.completionAdherenceCalcs = completionAdherenceCalcs;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.study_list_item, parent, false);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(context);
    return new Holder(v);
  }

  @Override
  public int getItemCount() {
    try {
      if (items == null) {
        return 0;
      }
      return items.size();
    } catch (Exception e) {
      Logger.log(e);
    }
    return 0;
  }

  class Holder extends RecyclerView.ViewHolder {

    RelativeLayout container;
    RelativeLayout progresslayout;
    AppCompatImageView stateIcon;
    AppCompatImageView defaultthumbnail;
    AppCompatTextView state;
    AppCompatImageView statusImg;
    AppCompatImageView studyImg;
    AppCompatTextView imgTitle;
    AppCompatTextView status;
    AppCompatTextView studyTitle;
    AppCompatTextView studyTitleLatin;
    AppCompatTextView sponser;
    AppCompatTextView completionVal;
    AppCompatTextView adherence;
    AppCompatTextView adherenceVal;
    ProgressBar progressBar1;
    ProgressBar progressBar2;

    Holder(View itemView) {
      super(itemView);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
      progresslayout = (RelativeLayout) itemView.findViewById(R.id.progresslayout);
      stateIcon = (AppCompatImageView) itemView.findViewById(R.id.stateIcon);
      studyImg = (AppCompatImageView) itemView.findViewById(R.id.studyImg);
      defaultthumbnail = (AppCompatImageView) itemView.findViewById(R.id.defaultthumbnail);
      imgTitle = (AppCompatTextView) itemView.findViewById(R.id.mImgTitle);
      state = (AppCompatTextView) itemView.findViewById(R.id.state);
      statusImg = (AppCompatImageView) itemView.findViewById(R.id.statusImg);
      status = (AppCompatTextView) itemView.findViewById(R.id.status);
      studyTitle = (AppCompatTextView) itemView.findViewById(R.id.study_title);
      studyTitleLatin = (AppCompatTextView) itemView.findViewById(R.id.study_title_latin);
      sponser = (AppCompatTextView) itemView.findViewById(R.id.sponser);
      completionVal = (AppCompatTextView) itemView.findViewById(R.id.completion_val);
      adherence = (AppCompatTextView) itemView.findViewById(R.id.adherence);
      adherenceVal = (AppCompatTextView) itemView.findViewById(R.id.adherence_val);
      progressBar1 = (ProgressBar) itemView.findViewById(R.id.progressBar1);
      progressBar2 = (ProgressBar) itemView.findViewById(R.id.progressBar2);
      setFont();
    }

    private void setFont() {
      try {
        imgTitle.setTypeface(AppController.getTypeface(context, "medium"));
        state.setTypeface(AppController.getHelveticaTypeface(context));
        status.setTypeface(AppController.getHelveticaTypeface(context));
        studyTitle.setTypeface(AppController.getHelveticaTypeface(context), Typeface.BOLD);
        studyTitleLatin.setTypeface(AppController.getTypeface(context, "regular"));
        sponser.setTypeface(AppController.getTypeface(context, "regular"));
        completionVal.setTypeface(AppController.getTypeface(context, "bold"));
        adherence.setTypeface(AppController.getTypeface(context, "regular"));
        adherenceVal.setTypeface(AppController.getTypeface(context, "bold"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, @SuppressLint("RecyclerView") final int position) {

    if (!AppController.getHelperSharedPreference()
        .readPreference(context, context.getResources().getString(R.string.userid), "")
        .equalsIgnoreCase("")) {
      holder.status.setVisibility(View.VISIBLE);
      holder.statusImg.setVisibility(View.VISIBLE);
      holder.completionVal.setVisibility(View.VISIBLE);
      holder.adherenceVal.setVisibility(View.VISIBLE);
      holder.adherence.setVisibility(View.VISIBLE);
      holder.progressBar1.setVisibility(View.VISIBLE);
      holder.progressBar2.setVisibility(View.VISIBLE);

      if (items.get(position).getStudyStatus() != null) {
        if (items.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.COMPLETED)) {
          holder.statusImg.setImageResource(R.drawable.completed_icn1);
          holder.status.setText(R.string.completed);
        } else if (items
            .get(position)
            .getStudyStatus()
            .equalsIgnoreCase(StudyFragment.NOT_ELIGIBLE)) {
          holder.statusImg.setImageResource(R.drawable.not_eligible_icn1);
          holder.status.setText(R.string.not_eligible);
        } else if (items
            .get(position)
            .getStudyStatus()
            .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
          holder.statusImg.setImageResource(R.drawable.in_progress_icn);
          if (items.get(position).getStatus().equalsIgnoreCase("closed")) {
            holder.status.setText(R.string.partial_participation);
          } else {
            holder.status.setText(R.string.in_progress);
          }
        } else if (items
            .get(position)
            .getStudyStatus()
            .equalsIgnoreCase(StudyFragment.YET_TO_JOIN)) {
          holder.statusImg.setImageResource(R.drawable.yet_to_join_icn1);
          if (items.get(position).getStatus().equalsIgnoreCase("closed")) {
            holder.status.setText(R.string.no_participation);
          } else {
            holder.status.setText(R.string.yet_to_join);
          }
        } else if (items.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.WITHDRAWN)) {
          holder.statusImg.setImageResource(R.drawable.withdrawn_icn1);
          holder.status.setText(R.string.withdrawn);
        } else {
          holder.statusImg.setImageResource(R.drawable.yet_to_join_icn1);
          holder.status.setText(R.string.yet_to_join);
        }
      } else {
        holder.statusImg.setImageResource(R.drawable.yet_to_join_icn1);
        holder.status.setText(R.string.yet_to_join);
      }

      if (items.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)
              || items.get(position).getStudyStatus().equalsIgnoreCase(StudyFragment.COMPLETED)) {
        holder.progresslayout.setVisibility(View.VISIBLE);
      } else {
        holder.progresslayout.setVisibility(View.GONE);
      }

      if (completionAdherenceCalcs.size() > 0) {
        try {
          holder.completionVal.setText(
              ""
                  + ((int)
                      completionAdherenceCalcs.get(holder.getAdapterPosition()).getCompletion())
                  + " %");
          holder.adherenceVal.setText(
              ""
                  + ((int)
                      completionAdherenceCalcs.get(holder.getAdapterPosition()).getAdherence())
                  + " %");
          holder.progressBar1.setProgress(
              (int) completionAdherenceCalcs.get(holder.getAdapterPosition()).getCompletion());
          holder.progressBar2.setProgress(
              (int) completionAdherenceCalcs.get(holder.getAdapterPosition()).getAdherence());
        } catch (IndexOutOfBoundsException e) {
          Logger.log(e);
        }
      }

    } else {
      holder.status.setVisibility(View.GONE);
      holder.statusImg.setVisibility(View.GONE);
      holder.completionVal.setVisibility(View.GONE);
      holder.adherenceVal.setVisibility(View.GONE);
      holder.adherence.setVisibility(View.GONE);
      holder.progressBar1.setVisibility(View.GONE);
      holder.progressBar2.setVisibility(View.GONE);
    }

    holder.state.setText(items.get(position).getStatus());
    GradientDrawable bgShape = (GradientDrawable) holder.stateIcon.getBackground();
    if (items.get(position).getStatus().equalsIgnoreCase("active")) {
      bgShape.setColor(context.getResources().getColor(R.color.bullet_green_color));
    } else if (items.get(position).getStatus().equalsIgnoreCase("closed")) {
      bgShape.setColor(context.getResources().getColor(R.color.red));
    } else if (items.get(position).getStatus().equalsIgnoreCase("paused")) {
      bgShape.setColor(context.getResources().getColor(R.color.rectangle_yellow));
    }

    RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false);

    Glide.with(context)
            .load(Base64.decode(items.get(holder.getAdapterPosition()).getLogo().split(",")[1], Base64.DEFAULT))
            .thumbnail(0.5f)
            .apply(requestOptions)
            .listener(new RequestListener<Drawable>() {
              @Override
              public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.defaultthumbnail.setVisibility(View.VISIBLE);
                return false;
              }

              @Override
              public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.defaultthumbnail.setVisibility(View.GONE);
                holder.studyImg.setImageDrawable(resource);
                return false;
              }
            }).into(holder.studyImg);

    holder.studyTitle.setText(items.get(position).getTitle());
    holder.studyTitleLatin.setText(Html.fromHtml(items.get(position).getTagline()));
    try {
      holder.imgTitle.setText(items.get(position).getCategory().toUpperCase());
    } catch (Exception e) {
      Logger.log(e);
    }
    String sponser = "";
    sponser = items.get(position).getSponsorName();
    holder.sponser.setText(sponser);

    holder.container.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                    CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                    context.getString(R.string.study_list));
            analyticsInstance.logEvent(
                    CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            if (click) {
              click = false;
              new Handler()
                  .postDelayed(
                      new Runnable() {
                        @Override
                        public void run() {
                          click = true;
                        }
                      },
                      1500);
              try {
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.title),
                        "" + items.get(holder.getAdapterPosition()).getTitle());
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.status),
                        "" + items.get(holder.getAdapterPosition()).getStatus());
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.studyStatus),
                        "" + items.get(holder.getAdapterPosition()).getStudyStatus());
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.position),
                        "" + holder.getAdapterPosition());
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.enroll),
                        "" + items.get(holder.getAdapterPosition()).getSetting().isEnrolling());
                AppController.getHelperSharedPreference()
                    .writePreference(
                        context,
                        context.getString(R.string.studyVersion),
                        "" + items.get(holder.getAdapterPosition()).getStudyVersion());
              } catch (Exception e) {
                Logger.log(e);
              }
              if (items
                      .get(position)
                      .getStatus()
                      .equalsIgnoreCase(context.getString(R.string.active))
                  && items
                      .get(position)
                      .getStudyStatus()
                      .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                studyFragment.getStudyUpdate(
                    items.get(holder.getAdapterPosition()).getStudyId(),
                    items.get(holder.getAdapterPosition()).getStudyVersion(),
                    items.get(holder.getAdapterPosition()).getTitle(),
                    "",
                    "",
                    "",
                    "");
              } else {
                Intent intent =
                    new Intent(context.getApplicationContext(), StudyInfoActivity.class);
                intent.putExtra("studyId", items.get(holder.getAdapterPosition()).getStudyId());
                intent.putExtra("title", items.get(holder.getAdapterPosition()).getTitle());
                intent.putExtra("status", items.get(holder.getAdapterPosition()).getStatus());
                intent.putExtra(
                    "studyStatus", items.get(holder.getAdapterPosition()).getStudyStatus());
                intent.putExtra("position", "" + holder.getAdapterPosition());
                intent.putExtra(
                    "enroll",
                    "" + items.get(holder.getAdapterPosition()).getSetting().isEnrolling());
                ((StudyActivity) context).startActivityForResult(intent, 100);
              }
            }
          }
        });
  }

  void modifyAdapter(
      RealmList<StudyList> searchResultList,
      ArrayList<CompletionAdherence> completionAdherenceCalcs) {
    items = searchResultList;
    this.completionAdherenceCalcs = completionAdherenceCalcs;
  }
}
