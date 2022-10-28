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

package com.harvard.studyappmodule.custom.activetask;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.harvard.R;
import com.harvard.studyappmodule.activitybuilder.CustomSurveyViewTaskActivity;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import com.harvard.utils.ActiveTaskService;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;
import com.ikovac.timepickerwithseconds.TimePicker;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class Tappingactivity implements StepBody {
  private QuestionStepCustom step;
  private StepResult<TappingResultFormat> result;
  private int tappingcount = 0;
  private Context context;
  private TappingAnswerFormat tappingAnswerFormat;
  private boolean timeup = false;
  private EditText kickcounter;
  private Intent serviceintent;
  private ImageView tapButton;
  private ImageView editButton;
  private TextView timer;
  private int maxTime;
  private RelativeLayout timereditlayout;
  private int finalSecond;
  private CustomFirebaseAnalytics analyticsInstance;

  public Tappingactivity(Step step, StepResult result) {
    this.step = (QuestionStepCustom) step;
    this.result = result == null ? new StepResult<>(this.step) : result;
    tappingAnswerFormat = (TappingAnswerFormat) ((QuestionStepCustom) step).getAnswerFormat1();
  }

  @Override
  public View getBodyView(int viewType, final LayoutInflater inflater, ViewGroup parent) {
    View view = inflater.inflate(R.layout.content_fetal_kick_counter, null);
    context = inflater.getContext();
    tapButton = (ImageView) view.findViewById(R.id.tapbutton);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(inflater.getContext());
    editButton = (ImageView) view.findViewById(R.id.editButton);
    final ImageView startTimer = (ImageView) view.findViewById(R.id.startTimer);
    timer = (TextView) view.findViewById(R.id.mTimer);
    final TextView starttxt = (TextView) view.findViewById(R.id.starttxt);
    final TextView mTapStart = (TextView) view.findViewById(R.id.mTapStart);
    kickcounter = (EditText) view.findViewById(R.id.kickcounter);

    timereditlayout = view.findViewById(R.id.timereditlayout);

    kickcounter.setEnabled(false);
    final int[] second = {0};

    maxTime = tappingAnswerFormat.getDuration();

    final int maxcount = tappingAnswerFormat.getKickCount();

    timer.setText(formathrs(second[0]));

    timer.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (!timeup) {
              tapButton.setEnabled(true);
            } else {
              tapButton.setEnabled(false);
              timereditlayout.setVisibility(View.GONE);
            }
          }
        });

    startTimer.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                context.getString(R.string.start_timer));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            activateservice(maxTime);
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.harvard.ActiveTask");
            context.registerReceiver(receiver, filter);

            if (!kickcounter.isEnabled()) {
              kickcounter.setEnabled(true);
            }

            starttxt.setVisibility(View.GONE);
            mTapStart.setVisibility(View.VISIBLE);

            startTimer.setVisibility(View.GONE);
            tapButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
          }
        });
    timer.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                context.getString(R.string.tap_time));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            final String[] duration = timer.getText().toString().split(":");
            if (timeup) {
              new MyTimePickerDialog(
                  context,
                  new MyTimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(
                        TimePicker view, int hourOfDay, int minute, int seconds) {
                      String hrs;
                      String min;
                      String sec;
                      if (hourOfDay < 10) {
                        hrs = "0" + hourOfDay;
                      } else {
                        hrs = "" + hourOfDay;
                      }
                      if (minute < 10) {
                        min = "0" + minute;
                      } else {
                        min = "" + minute;
                      }
                      if (seconds < 10) {
                        sec = "0" + seconds;
                      } else {
                        sec = "" + seconds;
                      }
                      finalSecond = (hourOfDay * 60 * 60) + (minute * 60) + (seconds);
                      if (finalSecond <= maxTime) {
                        timer.setText(hrs + ":" + min + ":" + sec);
                      } else {
                        Toast.makeText(
                            inflater.getContext(),
                            "Max duration you can enter is " + formathrs(maxTime),
                            Toast.LENGTH_SHORT)
                            .show();
                        finalSecond =
                            (Integer.parseInt(duration[0]) * 60 * 60)
                                + (Integer.parseInt(duration[1]) * 60)
                                + (Integer.parseInt(duration[2]));
                      }
                    }
                  },
                  Integer.parseInt(duration[0]),
                  Integer.parseInt(duration[1]),
                  Integer.parseInt(duration[2]),
                  true)
                  .show();
            }
          }
        });

    tapButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Bundle eventProperties = new Bundle();
            eventProperties.putString(
                CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                context.getString(R.string.tap_btn));
            analyticsInstance.logEvent(
                CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
            kickcounter.setFocusable(false);
            kickcounter.setFocusableInTouchMode(false);
            kickcounter.setFocusable(true);
            kickcounter.setFocusableInTouchMode(true);
            editButton.setVisibility(View.VISIBLE);
            if (!kickcounter.getText().toString().equalsIgnoreCase("")) {
              tappingcount = Integer.parseInt(kickcounter.getText().toString());
            } else {
              tappingcount = 0;
            }

            if (!timeup) {
              kickcounter.setText(String.valueOf(tappingcount + 1));
              if (Integer.parseInt(kickcounter.getText().toString()) >= maxcount) {
                timeup = true;
                endAlert(
                    "You have recorded "
                        + kickcounter.getText().toString()
                        + " kicks in "
                        + timer.getText().toString()
                        + ". Proceed to submitting count and time?");
                timereditlayout.setVisibility(View.GONE);
                try {
                  context.stopService(serviceintent);
                } catch (Exception e) {
                  Logger.log(e);
                }
                try {
                  context.unregisterReceiver(receiver);
                } catch (Exception e) {
                  Logger.log(e);
                }
              }
            } else {
              Toast.makeText(
                  inflater.getContext(),
                  "max kick you can enter is " + maxcount,
                  Toast.LENGTH_SHORT)
                  .show();
            }
          }
        });

    final String[] pervioustxt = {""};
    kickcounter.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            pervioustxt[0] = s.toString();
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
          }

          @Override
          public void afterTextChanged(Editable s) {
            if (!s.toString().equalsIgnoreCase("") && Integer.parseInt(s.toString()) > maxcount) {
              Toast.makeText(
                  inflater.getContext(),
                  "max kick you can enter is " + maxcount,
                  Toast.LENGTH_SHORT)
                  .show();
              kickcounter.setText(pervioustxt[0]);
              kickcounter.setSelection(kickcounter.getText().toString().length());
            }
          }
        });

    kickcounter.setOnTouchListener(
        new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
            editButton.setVisibility(View.GONE);
            return false;
          }
        });

    return view;
  }

  private String formathrs(int second) {
    String hrs;
    String min;
    String sec;
    if (second / 3600 < 10) {
      hrs = "0" + (second / 3600);
    } else {
      hrs = "" + (second / 3600);
    }
    if (((second % 3600) / 60) < 10) {
      min = "0" + ((second % 3600) / 60);
    } else {
      min = "" + ((second % 3600) / 60);
    }
    if ((second % 60) < 10) {
      sec = "0" + (second % 60);
    } else {
      sec = "" + (second % 60);
    }

    return hrs + ":" + min + ":" + sec;
  }

  @Override
  public StepResult getStepResult(boolean skipped) {
    if (skipped) {
      result.setResult(null);
    } else {
      TappingResultFormat tappingResultFormat = new TappingResultFormat();
      tappingResultFormat.setDuration("" + finalSecond);
      if (kickcounter.getText().toString().trim().equalsIgnoreCase("")) {
        tappingResultFormat.setValue(0);
      } else {
        tappingResultFormat.setValue(Double.parseDouble(kickcounter.getText().toString()));
      }
      result.setResult(tappingResultFormat);
    }
    return result;
  }

  @Override
  public BodyAnswer getBodyAnswerState() {
    if (!timeup) {
      return BodyAnswer.INVALID;
    } else {
      return BodyAnswer.VALID;
    }
  }

  private void activateservice(long sec) {
    serviceintent = new Intent(context, ActiveTaskService.class);
    if (!isMyServiceRunning(ActiveTaskService.class)) {
      serviceintent.putExtra("remaining_sec", "" + sec);
      context.startService(serviceintent);
    } else {
      try {
        context.stopService(serviceintent);
      } catch (Exception e) {
        Logger.log(e);
      }
      try {
        context.unregisterReceiver(receiver);
      } catch (Exception e) {
        Logger.log(e);
      }
      serviceintent.putExtra("remaining_sec", "" + sec);
      context.startService(serviceintent);
    }

    ((CustomSurveyViewTaskActivity) context).serviceStarted(receiver, serviceintent);
  }

  private final BroadcastReceiver receiver =
      new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
          if (Integer.parseInt(intent.getStringExtra("sec")) >= maxTime) {
            try {
              context.stopService(serviceintent);
            } catch (Exception e) {
              Logger.log(e);
            }
            try {
              context.unregisterReceiver(receiver);
            } catch (Exception e) {
              Logger.log(e);
            }
            timeup = true;
            tapButton.setEnabled(false);
            timereditlayout.setVisibility(View.GONE);
            endAlert(
                "You have recorded "
                    + kickcounter.getText().toString()
                    + " kicks in "
                    + timer.getText().toString()
                    + ". Proceed to submitting count and time?");
            timer.setText(formathrs(Integer.parseInt(intent.getStringExtra("sec"))));
          }
          finalSecond = Integer.parseInt(intent.getStringExtra("sec"));
          if (!timeup) {
            timer.setText(formathrs(Integer.parseInt(intent.getStringExtra("sec"))));
          }
        }
      };

  private boolean isMyServiceRunning(Class<?> serviceClass) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service :
        manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  private void endAlert(String message) {
    AlertDialog.Builder alertDialogBuilder =
        new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
    alertDialogBuilder.setTitle("Confirmation");
    alertDialogBuilder
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(
            context.getString(R.string.proceed),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                Bundle eventProperties = new Bundle();
                eventProperties.putString(
                    CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                    context.getString(R.string.tap_proceed));
                analyticsInstance.logEvent(
                    CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                ((CustomSurveyViewTaskActivity) context)
                    .onSaveStep(StepCallbacks.ACTION_NEXT, step, getStepResult(false));
              }
            })
        .setNegativeButton(
            context.getString(R.string.edit),
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                Bundle eventProperties = new Bundle();
                eventProperties.putString(
                    CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                    context.getString(R.string.tap_edit));
                analyticsInstance.logEvent(
                    CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                dialog.dismiss();
              }
            });
    AlertDialog alertDialog = alertDialogBuilder.create();
    alertDialog.show();
  }
}
