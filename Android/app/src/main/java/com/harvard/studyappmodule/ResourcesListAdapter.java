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
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.RealmList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ResourcesListAdapter extends RecyclerView.Adapter<ResourcesListAdapter.Holder> {
  private final Context context;
  private final ArrayList<Resource> items = new ArrayList<>();
  private Fragment fragment;

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
      }

      holder.container.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (items.get(i).getType() != null) {
                Intent intent = new Intent(context, ResourcesWebViewActivity.class);
                intent.putExtra("studyId", ((SurveyActivity) context).getStudyId());
                intent.putExtra("title", "" + items.get(i).getTitle().toString());
                intent.putExtra("type", "" + items.get(i).getType().toString());
                intent.putExtra("content", "" + items.get(i).getContent().toString());
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
                intent.putExtra("rejoin", "" + ((SurveyActivity) context).getTitle1());
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
                  .equalsIgnoreCase(view.getResources().getString(R.string.leave_study))) {

                String message = ((SurveyResourcesFragment) fragment).getLeaveStudyMessage();
                AlertDialog.Builder builder =
                    new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                builder.setTitle(context.getResources().getString(R.string.leave_study) + "?");
                builder.setMessage(message);
                builder.setPositiveButton(
                    context.getResources().getString(R.string.proceed_caps),
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        typeUserShowDialog();
                      }
                    });

                builder.setNegativeButton(
                    context.getResources().getString(R.string.cancel_caps),
                    new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                      }
                    });
                AlertDialog diag = builder.create();
                diag.show();
              }
            }
          });
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void typeUserShowDialog() {
    try {
      String withdrawalType = ((SurveyResourcesFragment) fragment).getType();
      switch (withdrawalType) {
        case "ask_user":
          showDialog(3);
          break;
        case "delete_data":
          showDialog(2);

          break;
        case "no_action":
          showDialog(1);
          break;
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void showDialog(int count) {
    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
    // withdrawalType ask_user
    if (count == 3) {
      builder.setMessage(
          context.getResources().getString(R.string.leave_study_retained_or_deleted_message));

      builder.setPositiveButton(
          context.getResources().getString(R.string.retain_my_data_caps),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("false");
            }
          });

      builder.setNeutralButton(
          context.getResources().getString(R.string.cancel_caps),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.cancel();
            }
          });

      builder.setNegativeButton(
          context.getResources().getString(R.string.delete_my_data_caps),
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("true");
              dialog.cancel();
            }
          });

      AlertDialog diag = builder.create();
      diag.show();
    } else if (count == 2) {
      // withdrawalType delete_data
      ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("true");
    } else if (count == 1) {
      // withdrawalType no_action
      ((SurveyResourcesFragment) fragment).responseServerWithdrawFromStudy("false");
    }
  }
}
