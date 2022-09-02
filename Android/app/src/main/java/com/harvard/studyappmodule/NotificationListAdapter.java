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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Notification;
import com.harvard.studyappmodule.studymodel.Study;
import com.harvard.studyappmodule.studymodel.StudyList;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import io.realm.Realm;
import io.realm.RealmList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.Holder> {
  private final Context context;
  private RealmList<Notification> items;
  private DbServiceSubscriber dbServiceSubscriber;
  private Realm realm;
  private CustomFirebaseAnalytics analyticsInstance;

  NotificationListAdapter(Context context, RealmList<Notification> notifications, Realm realm) {
    this.context = context;
    this.items = notifications;
    dbServiceSubscriber = new DbServiceSubscriber();
    this.realm = realm;
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.notification_list_item, parent, false);
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
    AppCompatTextView notificationMsg;
    AppCompatTextView dayTimeDisplay;

    Holder(View itemView) {
      super(itemView);
      container = (RelativeLayout) itemView.findViewById(R.id.container);
      notificationMsg = (AppCompatTextView) itemView.findViewById(R.id.notification_msg);
      dayTimeDisplay = (AppCompatTextView) itemView.findViewById(R.id.dayTimeDisplay);

      setFont();
    }

    private void setFont() {
      try {
        notificationMsg.setTypeface(AppController.getTypeface(context, "regular"));
        dayTimeDisplay.setTypeface(AppController.getTypeface(context, "medium"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, final int position) {
    try {
      holder.notificationMsg.setText(items.get(holder.getAdapterPosition()).getMessage());
      holder.dayTimeDisplay.setText(
          AppController.getDateFormatForNotification()
              .format(
                  AppController.getDateFormatForApi()
                      .parse(items.get(holder.getAdapterPosition()).getDate())));

      holder.container.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Bundle eventProperties = new Bundle();
              eventProperties.putString(
                  CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                  context.getString(R.string.notification_list));
              analyticsInstance.logEvent(
                  CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
              if (!AppController.getHelperSharedPreference()
                  .readPreference(context, context.getResources().getString(R.string.userid), "")
                  .equalsIgnoreCase("")) {
                if (items.get(holder.getAdapterPosition()).getType().equalsIgnoreCase("Gateway")) {
                  if (items
                      .get(holder.getAdapterPosition())
                      .getSubtype()
                      .equalsIgnoreCase("Study")
                      || items
                      .get(holder.getAdapterPosition())
                      .getSubtype().equalsIgnoreCase("Activity")
                      || items
                      .get(holder.getAdapterPosition())
                      .getSubtype().equalsIgnoreCase("Announcement")
                      || items
                      .get(holder.getAdapterPosition())
                      .getSubtype().equalsIgnoreCase("studyEvent")) {

                    Study study = dbServiceSubscriber.getStudyListFromDB(realm);
                    if (study != null) {
                      RealmList<StudyList> studyListArrayList = study.getStudies();
                      studyListArrayList =
                          dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                      boolean isStudyAvailable = false;
                      for (int i = 0; i < studyListArrayList.size(); i++) {
                        if (items
                            .get(holder.getAdapterPosition())
                            .getStudyId()
                            .equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                          try {
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.title),
                                    "" + studyListArrayList.get(i).getTitle());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.status),
                                    "" + studyListArrayList.get(i).getStatus());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.studyStatus),
                                    "" + studyListArrayList.get(i).getStudyStatus());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context, context.getString(R.string.position), "" + i);
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.enroll),
                                    "" + studyListArrayList.get(i).getSetting().isEnrolling());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.studyVersion),
                                    "" + studyListArrayList.get(i).getStudyVersion());
                          } catch (Exception e) {
                            Logger.log(e);
                          }
                          if (studyListArrayList
                                  .get(i)
                                  .getStatus()
                                  .equalsIgnoreCase(context.getString(R.string.active))
                              && studyListArrayList
                                  .get(i)
                                  .getStudyStatus()
                                  .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                            Intent intent = new Intent(context, SurveyActivity.class);
                            intent.putExtra(
                                "studyId", items.get(holder.getAdapterPosition()).getStudyId());
                            context.startActivity(intent);
                          } else if (studyListArrayList
                              .get(i)
                              .getStatus()
                              .equalsIgnoreCase(context.getString(R.string.paused))) {
                            Toast.makeText(context, R.string.study_paused, Toast.LENGTH_SHORT)
                                .show();
                          } else if (studyListArrayList
                              .get(i)
                              .getStatus()
                              .equalsIgnoreCase(context.getString(R.string.closed))) {
                            Toast.makeText(context, R.string.study_resume, Toast.LENGTH_SHORT)
                                .show();
                          } else {
                            Intent intent =
                                new Intent(
                                    context.getApplicationContext(), StudyInfoActivity.class);
                            intent.putExtra("studyId", studyListArrayList.get(i).getStudyId());
                            intent.putExtra("title", studyListArrayList.get(i).getTitle());
                            intent.putExtra("status", studyListArrayList.get(i).getStatus());
                            intent.putExtra(
                                "studyStatus", studyListArrayList.get(i).getStudyStatus());
                            intent.putExtra("position", "" + i);
                            intent.putExtra(
                                "enroll",
                                "" + studyListArrayList.get(i).getSetting().isEnrolling());
                            context.startActivity(intent);
                          }
                          isStudyAvailable = true;
                          break;
                        }
                      }
                      if (!isStudyAvailable) {
                        Intent intent =
                            new Intent(context.getApplicationContext(), StudyActivity.class);
                        context.startActivity(intent);
                      }
                    } else {
                      Toast.makeText(context, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                          .show();
                    }
                  } else if (items
                      .get(holder.getAdapterPosition())
                      .getSubtype()
                      .equalsIgnoreCase("Resource")) {
                    Intent intent = new Intent();
                    intent.putExtra("action", "refresh");
                    ((Activity) context).setResult(Activity.RESULT_OK, intent);
                    ((Activity) context).finish();
                  }
                } else if (items
                    .get(holder.getAdapterPosition())
                    .getType()
                    .equalsIgnoreCase("Study")) {
                  if (items
                          .get(holder.getAdapterPosition())
                          .getSubtype()
                          .equalsIgnoreCase("Activity")
                      || items
                          .get(holder.getAdapterPosition())
                          .getSubtype()
                          .equalsIgnoreCase("Resource")) {
                    Study study = dbServiceSubscriber.getStudyListFromDB(realm);
                    if (study != null) {
                      RealmList<StudyList> studyListArrayList = study.getStudies();
                      studyListArrayList =
                          dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, realm);
                      boolean isStudyAvailable = false;
                      boolean isStudyJoined = false;
                      for (int i = 0; i < studyListArrayList.size(); i++) {
                        if (items
                            .get(holder.getAdapterPosition())
                            .getStudyId()
                            .equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                          isStudyAvailable = true;
                          try {
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.title),
                                    "" + studyListArrayList.get(i).getTitle());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.status),
                                    "" + studyListArrayList.get(i).getStatus());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.studyStatus),
                                    "" + studyListArrayList.get(i).getStudyStatus());
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context, context.getString(R.string.position), "" + i);
                            AppController.getHelperSharedPreference()
                                .writePreference(
                                    context,
                                    context.getString(R.string.enroll),
                                    "" + studyListArrayList.get(i).getSetting().isEnrolling());
                          } catch (Exception e) {
                            Logger.log(e);
                          }
                          if (studyListArrayList
                                  .get(i)
                                  .getStatus()
                                  .equalsIgnoreCase(context.getString(R.string.active))
                              && studyListArrayList
                                  .get(i)
                                  .getStudyStatus()
                                  .equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                            Intent intent = new Intent(context, SurveyActivity.class);
                            intent.putExtra(
                                "studyId", items.get(holder.getAdapterPosition()).getStudyId());
                            intent.putExtra("from", "NotificationActivity");
                            intent.putExtra(
                                "to", items.get(holder.getAdapterPosition()).getSubtype());
                            context.startActivity(intent);
                            isStudyJoined = true;
                            break;
                          } else {
                            isStudyJoined = false;
                            break;
                          }
                        }
                      }
                      if (!isStudyAvailable) {
                        Toast.makeText(context, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                            .show();
                      } else if (!isStudyJoined) {
                        Toast.makeText(context, R.string.studyNotJoined, Toast.LENGTH_SHORT).show();
                      }
                    } else {
                      Toast.makeText(context, R.string.studyNotAvailable, Toast.LENGTH_SHORT)
                          .show();
                    }
                  }
                }
              } else {
                Toast.makeText(context, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
              }
            }
          });
    } catch (Exception e) {
      Logger.log(e);
    }
  }
}
