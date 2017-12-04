/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.researchstack.backbone.model.SchedulesAndTasksModel;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.ui.adapter.TaskAdapter;
import org.sagebase.crf.helper.CrfDateHelper;
import org.sagebase.crf.helper.CrfScheduleHelper;
import org.sagebionetworks.bridge.researchstack.CrfTaskFactory;
import org.sagebionetworks.research.crf.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import rx.subjects.PublishSubject;

/**
 * Created by rianhouston on 2/25/17.
 */

public class CrfTaskAdapter extends TaskAdapter {
    private static final String LOG_TAG = CrfTaskAdapter.class.getCanonicalName();

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_FOOTER = 2;
    private static final int VIEW_TYPE_FILTER = 3;
    private static final int VIEW_TYPE_START = 4;

    private Context mContext;
    private static SimpleDateFormat sFormatter = new SimpleDateFormat("MMM d");
    private boolean mFiltered = false;
    private int mPositionForToday;

    protected PublishSubject<SchedulesAndTasksModel.ScheduleModel>
            publishScheduleSubject = PublishSubject.create();

    public CrfTaskAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "onCreateViewHolder(): " + viewType);
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.crf_item_schedule_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_FOOTER){
            View view = inflater.inflate(R.layout.crf_item_schedule_footer, parent, false);
            return new FooterViewHolder(view);
        } else if (viewType == VIEW_TYPE_FILTER) {
            View view = inflater.inflate(R.layout.crf_item_schedule_filtered, parent, false);
            return new FilteredViewHolder(view);
        } else if (viewType == VIEW_TYPE_START) {
            View view = inflater.inflate(R.layout.crf_item_schedule_start, parent, false);
            return new StartItemViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.crf_item_schedule, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hldr, int position) {
        int viewType = getItemViewType(position);
        Log.d(LOG_TAG, "onBindViewHolder(): " + position + ", " + viewType);
        Object obj = tasks.get(position);

        SchedulesAndTasksModel.ScheduleModel schedule = null;
        SchedulesAndTasksModel.TaskScheduleModel firstTask = null;
        if (obj instanceof SchedulesAndTasksModel.ScheduleModel) {
            schedule = (SchedulesAndTasksModel.ScheduleModel) obj;
        } else if (obj instanceof StartItem) {
            schedule = ((StartItem)obj).schedule;
        }
         if (schedule != null && schedule.tasks != null && !schedule.tasks.isEmpty()) {
             firstTask = schedule.tasks.get(0);
         }
         if (obj instanceof SchedulesAndTasksModel.TaskScheduleModel) {
            firstTask = (SchedulesAndTasksModel.TaskScheduleModel)obj;
         }

         boolean isHldrClickable = false;
        if (schedule != null) {
            isHldrClickable = CrfScheduleHelper.isScheduleEnabled(schedule);
            if (isHldrClickable) {
                final SchedulesAndTasksModel.ScheduleModel finalSchedule = schedule;
                hldr.itemView.setOnClickListener(v -> {
                    LogExt.d(LOG_TAG, "Item clicked: " + finalSchedule.scheduleString);
                    publishScheduleSubject.onNext(finalSchedule);
                });
            }
        } else if (firstTask != null) {
            isHldrClickable = CrfScheduleHelper.isTaskEnabled(firstTask);
            if (isHldrClickable) {
                final SchedulesAndTasksModel.TaskScheduleModel finalTask = firstTask;
                hldr.itemView.setOnClickListener(v -> {
                    LogExt.d(LOG_TAG, "Item clicked: " + finalTask.taskID);
                    publishSubject.onNext(finalTask);
                });
            }
        }

        hldr.itemView.setEnabled(true);
        if (!isHldrClickable) {
            hldr.itemView.setOnClickListener(null);
            hldr.itemView.setEnabled(false);
        }

         if(hldr instanceof ViewHolder) {
             ViewHolder holder = (ViewHolder) hldr;
             boolean isToday = CrfDateHelper.isToday(schedule.scheduledOn);

             int iconSize = 0;
             float titleSize = 0f;
             float dateSize = 0f;
             if (isToday) {
                 iconSize = (int) mContext.getResources().getDimension(R.dimen.crf_activity_icon_size_today);
                 dateSize = mContext.getResources().getDimension(R.dimen.crf_activity_date_size_today);
                 titleSize = mContext.getResources().getDimension(R.dimen.crf_activity_title_size_today);
                 holder.today.setVisibility(View.VISIBLE);
                 holder.subtitle.setVisibility(View.VISIBLE);
                 holder.overlay.setVisibility(View.GONE);
             } else {
                 iconSize = (int) mContext.getResources().getDimension(R.dimen.crf_activity_icon_size);
                 dateSize = mContext.getResources().getDimension(R.dimen.crf_activity_date_size);
                 titleSize = mContext.getResources().getDimension(R.dimen.crf_activity_title_size);
                 holder.today.setVisibility(View.GONE);
                 holder.subtitle.setVisibility(View.GONE);
                 holder.overlay.setVisibility(View.VISIBLE);
             }

             boolean allTasksComplete = true;
             for (SchedulesAndTasksModel.TaskScheduleModel task : schedule.tasks) {
                 if (task.taskFinishedOn == null) {
                     allTasksComplete = false;
                 }
             }
             if (allTasksComplete) {
                 holder.iconCompleted.setVisibility(View.VISIBLE);
             } else {
                 holder.iconCompleted.setVisibility(View.GONE);
             }

             FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(iconSize, iconSize);
             holder.icon.setLayoutParams(params);
             holder.date.setTextSize(TypedValue.COMPLEX_UNIT_PX, dateSize);
             holder.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);

             if (schedule.tasks.size() == 1) {
                 holder.icon.setImageResource(getIcon(firstTask));
                 holder.title.setText(firstTask.taskTitle);
                 holder.subtitle.setText(firstTask.taskCompletionTime);
                 holder.date.setText(formatDate(schedule.scheduledOn));
             } else {
                 holder.icon.setImageResource(R.drawable.crf_task_clinic);
                 holder.date.setText(formatDate(schedule.scheduledOn));
                 holder.title.setText(mContext.getString(R.string.crf_clinic_fitness_test));
             }
         } else if(hldr instanceof FilteredViewHolder) {
             FilteredViewHolder holder = (FilteredViewHolder) hldr;

             holder.icon.setImageResource(getIcon(firstTask));
             holder.title.setText(firstTask.taskTitle);
             holder.iconCompleted.setVisibility(View.GONE);
             if (firstTask.taskFinishedOn != null) {
                 holder.icon.setAlpha(0.5f);
                 holder.title.setAlpha(0.5f);
             } else {
                 holder.icon.setAlpha(1.0f);
                 holder.title.setAlpha(1.0f);
             }
         } else if(hldr instanceof StartItemViewHolder) {
             StartItemViewHolder holder = (StartItemViewHolder) hldr;
             StartItem item = (StartItem) obj;

             holder.date.setText(formatDate(schedule.scheduledOn));
             holder.title.setText(mContext.getString(R.string.crf_clinic_fitness_test));
             holder.subtitle.setText(firstTask.taskCompletionTime);
         } else if(hldr instanceof HeaderViewHolder) {
             HeaderViewHolder holder = (HeaderViewHolder) hldr;
             Header header = (Header)obj;
             holder.title.setText(header.title);
             holder.message.setText(header.message);
        } else if(hldr instanceof FooterViewHolder) {
             FooterViewHolder holder = (FooterViewHolder) hldr;
             Footer footer = (Footer)obj;
             holder.title.setText(footer.title);
             holder.message.setText(footer.message);
         }
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = tasks.get(position);
        if(item instanceof Header) {
            return VIEW_TYPE_HEADER;
        } else if(item instanceof Footer) {
            return VIEW_TYPE_FOOTER;
        } else if(item instanceof StartItem) {
            return VIEW_TYPE_START;
        } else {
            if(mFiltered) {
                return VIEW_TYPE_FILTER;
            } else {
                return VIEW_TYPE_ITEM;
            }
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        tasks.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Object> list, boolean filtered) {
        tasks.addAll(list);
        mFiltered = filtered;
        notifyDataSetChanged();
        setPositionForToday(list);
    }

    private void setPositionForToday(List<Object> list) {
        // Find position for today
        int pos = 0;
        for(Object obj: list) {
            if (obj instanceof SchedulesAndTasksModel.ScheduleModel) {
                SchedulesAndTasksModel.ScheduleModel schedule = (SchedulesAndTasksModel.ScheduleModel)obj;
                if(CrfDateHelper.isToday(schedule.scheduledOn)) {
                    mPositionForToday = pos;
                    break;
                }
            }
            pos++;
        }
    }

    public int getPositionForToday() {
        return mPositionForToday;
    }

    private String formatDate(Date d) {
        if(CrfDateHelper.isToday(d)) {
            return mContext.getString(R.string.crf_today);
        } else {
            return sFormatter.format(d);
        }
    }

    private int getIcon(SchedulesAndTasksModel.TaskScheduleModel task) {
        int icon = R.drawable.crf_task_clinic;

        if(task.taskID != null) {
            switch (task.taskID) {
                case CrfTaskFactory.TASK_ID_CARDIO_12MT:
                    icon = R.drawable.crf_task_12_min;
                    break;
                case CrfTaskFactory.TASK_ID_HEART_RATE_MEASUREMENT:
                    icon = R.drawable.crf_task_demographics;
                    break;
                case CrfTaskFactory.TASK_ID_STAIR_STEP:
                    icon = R.drawable.crf_task_stair_step;
                    break;
                default:
                    icon = R.drawable.crf_task_clinic;
            }
        }

        return icon;
    }

    public static class FilteredViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        ImageView iconCompleted;
        TextView title;

        public FilteredViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.task_title);
            icon = (ImageView) itemView.findViewById(R.id.task_icon);
            iconCompleted = (ImageView) itemView.findViewById(R.id.task_icon_completed);
        }
    }
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView message;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.activity_header_title);
            message = (TextView) itemView.findViewById(R.id.activity_header_message);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView message;

        public FooterViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.activity_footer_title);
            message = (TextView) itemView.findViewById(R.id.activity_footer_message);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        ImageView iconCompleted;
        TextView date;
        TextView title;
        TextView subtitle;
        View today;
        View overlay;

        public ViewHolder(View itemView) {
            super(itemView);
            today = itemView.findViewById(R.id.today_marker);
            icon = (ImageView) itemView.findViewById(R.id.task_icon);
            iconCompleted = (ImageView) itemView.findViewById(R.id.task_icon_completed);
            title = (TextView) itemView.findViewById(R.id.task_title);
            subtitle = (TextView) itemView.findViewById(R.id.task_subtitle);
            date = (TextView) itemView.findViewById(R.id.task_date);
            overlay = itemView.findViewById(R.id.task_overlay);
        }
    }

    public static class StartItemViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView title;
        TextView message;
        TextView subtitle;

        public StartItemViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.task_date);
            title = (TextView) itemView.findViewById(R.id.task_title);
            //message = (TextView) itemView.findViewById(R.id.task_message);
            subtitle = (TextView) itemView.findViewById(R.id.task_subtitle);
        }
    }

    public static class StartItem {
        SchedulesAndTasksModel.ScheduleModel schedule;

        public StartItem(SchedulesAndTasksModel.ScheduleModel s) {
            schedule = s;
        }
    }

    public static class Header {
        String title;
        String message;

        public Header(String t, String m) {
            title = t;
            message = m;
        }
    }

    public static class Footer {
        String title;
        String message;

        public Footer(String t, String m) {
            title = t;
            message = m;
        }
    }

}
