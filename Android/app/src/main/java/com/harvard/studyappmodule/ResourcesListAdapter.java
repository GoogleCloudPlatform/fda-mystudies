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
 */

package com.harvard.studyappmodule;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.usermodule.TermsPrivacyPolicyActivity;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResourcesListAdapter extends RecyclerView.Adapter<ResourcesListAdapter.Holder> {
  private final Context context;
  private final ArrayList<Resource> items = new ArrayList<>();
  private Fragment fragment;
  private CustomFirebaseAnalytics analyticsInstance;

  ResourcesListAdapter(Context context, RealmList<Resource> items, Fragment fragment) {
    this.context = context;
    this.items.addAll(items);
    this.fragment = fragment;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.resources_list_item, parent, false);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(context);
    return new Holder(v);
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    final RelativeLayout container;
    final AppCompatTextView resourcesTitle;
    final AppCompatTextView resourcesDesc;

    Holder(View itemView) {
      super(itemView);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
      resourcesTitle = (AppCompatTextView) itemView.findViewById(R.id.resourcesTitle);
      resourcesDesc = (AppCompatTextView) itemView.findViewById(R.id.resourcesDesc);
      setFont();
    }

    private void setFont() {
      try {
        resourcesTitle.setTypeface(AppController.getTypeface(context, "regular"));
        resourcesDesc.setTypeface(AppController.getTypeface(context, "regular"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, final int position) {
    final int i = holder.getAdapterPosition();
    try {
      Comparator<Resource> comparator = new Comparator<Resource>() {
        @Override
        public int compare(final Resource o1, final Resource o2) {
          if (o1.getTitle().contains(context.getResources().getString(R.string.leave_study))
              && !o2.getTitle()
              .contains(context.getResources().getString(R.string.leave_study))) {
            return 1;
          } else if (!o1.getTitle()
              .contains(context.getResources().getString(R.string.leave_study))
              && o2.getTitle()
              .contains(context.getResources().getString(R.string.leave_study))) {
            return -1;
          } else if (o1.getTitle().contains(context.getResources().getString(R.string.resourcePolicy))
              && !o2.getTitle()
              .contains(context.getResources().getString(R.string.resourcePolicy))) {
            return 1;
          } else if (!o1.getTitle()
              .contains(context.getResources().getString(R.string.resourcePolicy))
              && o2.getTitle()
              .contains(context.getResources().getString(R.string.resourcePolicy))) {
            return -1;
          } else if (o1.getTitle().contains(context.getResources().getString(R.string.resourceTerms))
              && !o2.getTitle()
              .contains(context.getResources().getString(R.string.resourceTerms))) {
            return 1;
          } else if (!o1.getTitle()
              .contains(context.getResources().getString(R.string.resourceTerms))
              && o2.getTitle()
              .contains(context.getResources().getString(R.string.resourceTerms))) {
            return -1;
          }
          return 0;
        }
      };
      Collections.sort(items, comparator);
      holder.resourcesTitle.setText(items.get(i).getTitle());

      if (items.get(i).getTitle().equalsIgnoreCase(context.getResources().getString(R.string.leave_study))
              && AppConfig.AppType.equalsIgnoreCase(context.getString(R.string.app_standalone))) {
        holder.resourcesDesc.setVisibility(View.VISIBLE);
        holder.resourcesDesc.setText(context.getString(R.string.delete_account_msg));
      } else {
        holder.resourcesDesc.setVisibility(View.INVISIBLE);
      }

      holder.container.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  context.getString(R.string.resources_list));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
              Realm realm = AppController.getRealmobj(context);
              if (items.get(i).getType() != null) {
                Intent intent = new Intent(context, ResourcesWebViewActivity.class);
                intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
                intent.putExtra("title", "" + items.get(i).getTitle().toString());
                intent.putExtra("type", "" + items.get(i).getType().toString());
                intent.putExtra("resourceId", "" + items.get(i).getResourcesId().toString());
                context.startActivity(intent);
              } else if (items
                  .get(i)
                  .getTitle()
                  .equalsIgnoreCase(view.getResources().getString(R.string.about_study1))) {
                Intent intent = new Intent(context, StudyInfoActivity.class);
                intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
                intent.putExtra("title", ((SurveyActivity) context).getTitle1());
                intent.putExtra("bookmark", ((SurveyActivity) context).getBookmark());
                intent.putExtra("status", ((SurveyActivity) context).getStatus());
                intent.putExtra("studyStatus", ((SurveyActivity) context).getStudyStatus());
                intent.putExtra("position", "" + ((SurveyActivity) context).getPosition());
                intent.putExtra("enroll", "" + ((SurveyActivity) context).getTitle1());
                intent.putExtra("about_this_study", true);
                (context).startActivity(intent);

              } else if (items
                  .get(i)
                  .getTitle()
                  .equalsIgnoreCase(view.getResources().getString(R.string.consent_pdf))) {
                try {
                  Intent intent = new Intent(context, PdfDisplayActivity.class);
                  intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
                  intent.putExtra("title", ((SurveyActivity) context).getTitle1());
                  (context).startActivity(intent);
                } catch (Exception e) {
                  Logger.log(e);
                }
              } else if (items
                  .get(i)
                  .getTitle()
                  .equalsIgnoreCase(view.getResources().getString(R.string.resourceTerms))) {
                try {
                  Intent termsIntent = new Intent(context, TermsPrivacyPolicyActivity.class);
                  termsIntent.putExtra(
                      "title", context.getResources().getString(R.string.resourceTerms));
                  termsIntent.putExtra("url", dbServiceSubscriber.getApps(realm).getTermsUrl());
                  context.startActivity(termsIntent);
                } catch (Exception e) {
                  Logger.log(e);
                }
              } else if (items
                  .get(i)
                  .getTitle()
                  .equalsIgnoreCase(view.getResources().getString(R.string.resourcePolicy))) {
                try {
                  Intent termsIntent = new Intent(context, TermsPrivacyPolicyActivity.class);
                  termsIntent.putExtra(
                      "title", context.getResources().getString(R.string.resourcePolicy));
                  termsIntent.putExtra(
                      "url", dbServiceSubscriber.getApps(realm).getPrivacyPolicyUrl());
                  context.startActivity(termsIntent);
                } catch (Exception e) {
                  Logger.log(e);
                }
              } else if (items
                  .get(i)
                  .getTitle()
                  .equalsIgnoreCase(view.getResources().getString(R.string.leave_study))) {

                String message;
                if (items
                        .get(i)
                        .getTitle()
                        .equalsIgnoreCase(context.getResources().getString(R.string.leave_study))
                    && AppConfig.AppType.equalsIgnoreCase(
                        context.getString(R.string.app_standalone))) {
                  message = context.getString(R.string.leaveStudyDeleteAccount);
                } else {
                  message = context.getString(R.string.leaveStudy);
                }
                AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                builder.setTitle(context.getResources().getString(R.string.leave_study) + "?");
                builder.setMessage(message);
                builder.setPositiveButton(
                    context.getResources().getString(R.string.yes),
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        Bundle eventProperties = new Bundle();
                        eventProperties.putString(
                            CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                            context.getString(R.string.resources_list_leave_study_yes));
                        analyticsInstance.logEvent(
                            CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                        ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy();
                      }
                    });

                builder.setNegativeButton(
                    context.getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        Bundle eventProperties = new Bundle();
                        eventProperties.putString(
                            CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                            context.getString(R.string.resources_list_leave_study_no));
                        analyticsInstance.logEvent(
                            CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                        dialog.cancel();
                      }
                    });
                AlertDialog diag = builder.create();
                diag.show();
              }
              dbServiceSubscriber.closeRealmObj(realm);
            }
          });
    } catch (Exception e) {
      Logger.log(e);
    }
  }
}
