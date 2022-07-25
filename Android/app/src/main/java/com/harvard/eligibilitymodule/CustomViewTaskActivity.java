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

package com.harvard.eligibilitymodule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.harvard.R;
import com.harvard.studyappmodule.StandaloneStudyInfoActivity;
import com.harvard.studyappmodule.activitybuilder.model.Eligibility;
import com.harvard.studyappmodule.consent.model.CorrectAnswers;
import com.harvard.studyappmodule.custom.StepSwitcherCustom;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import com.harvard.utils.NetworkChangeReceiver;
import com.harvard.utils.SharedPreferenceHelper;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;

public class CustomViewTaskActivity extends AppCompatActivity
    implements StepCallbacks, NetworkChangeReceiver.NetworkChangeCallback {
  private static final String EXTRA_TASK = "ViewTaskActivity.ExtraTask";
  private static final String EXTRA_STUDYID = "ViewTaskActivity.ExtraStudyId";
  private static final String STUDYID = "ViewTaskActivity.StudyId";
  private static final String ENROLLID = "ViewTaskActivity.EnrollId";
  private static final String SITEID = "ViewTaskActivity.siteId";
  private static final String PDF_TITLE = "ViewTaskActivity.pdfTitle";
  private static final String EXTRA_TASK_RESULT = "ViewTaskActivity.ExtraTaskResult";
  private static final String EXTRA_STEP = "ViewTaskActivity.ExtraStep";
  private static final String ELIGIBILITY = "ViewTaskActivity.eligibility";
  private static final String TYPE = "ViewTaskActivity.type";
  private StepSwitcherCustom root;
  private Step currentStep;
  private Task task;
  private String studyId;
  private String enrollId;
  private String siteId;
  private String eligibility;
  private String type;
  private String pdfTitle;
  private TaskResult taskResult;
  private ArrayList<CorrectAnswers> correctAnswers;
  private CustomFirebaseAnalytics analyticsInstance;
  private NetworkChangeReceiver networkChangeReceiver;

  public static Intent newIntent(
      Context context,
      Task task,
      String surveyId,
      String studyId,
      Eligibility correctAnswers,
      String title,
      String enrollId,
      String siteId,
      String eligibility,
      String type) {
    Intent intent = new Intent(context, CustomViewTaskActivity.class);
    intent.putExtra(EXTRA_TASK, task);
    intent.putExtra(EXTRA_STUDYID, surveyId);
    intent.putExtra(PDF_TITLE, title);
    intent.putExtra(STUDYID, studyId);
    intent.putExtra(SITEID, siteId);
    intent.putExtra(ENROLLID, enrollId);
    intent.putExtra(ELIGIBILITY, eligibility);
    intent.putExtra(TYPE, type);
    ArrayList<CorrectAnswers> correctAnswersArrayList = new ArrayList<>();
    correctAnswersArrayList.addAll(correctAnswers.getCorrectAnswers());
    intent.putExtra("correctanswer", correctAnswersArrayList);
    return intent;
  }

  @SuppressLint("WrongViewCast")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setResult(RESULT_CANCELED);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(this);
    super.setContentView(R.layout.stepswitchercustom);
    Toolbar toolbar = findViewById(org.researchstack.backbone.R.id.toolbar);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    pdfTitle = getIntent().getStringExtra(PDF_TITLE);
    correctAnswers = (ArrayList<CorrectAnswers>) getIntent().getSerializableExtra("correctanswer");
    root = findViewById(R.id.container);
    if (savedInstanceState == null) {
      task = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
      studyId = (String) getIntent().getSerializableExtra(STUDYID);
      siteId = (String) getIntent().getSerializableExtra(SITEID);
      enrollId = (String) getIntent().getSerializableExtra(ENROLLID);
      eligibility = (String) getIntent().getSerializableExtra(ELIGIBILITY);
      type = (String) getIntent().getSerializableExtra(TYPE);
      taskResult = new TaskResult(task.getIdentifier());
      taskResult.setStartDate(new Date());
    } else {
      task = (Task) savedInstanceState.getSerializable(EXTRA_TASK);
      studyId = (String) savedInstanceState.getSerializable(STUDYID);
      siteId = (String) savedInstanceState.getSerializable(SITEID);
      enrollId = (String) savedInstanceState.getSerializable(ENROLLID);
      eligibility = (String) savedInstanceState.getSerializable(ELIGIBILITY);
      type = (String) savedInstanceState.getSerializable(TYPE);
      taskResult = (TaskResult) savedInstanceState.getSerializable(EXTRA_TASK_RESULT);
      currentStep = (Step) savedInstanceState.getSerializable(EXTRA_STEP);
    }
    task.validateParameters();

    if (currentStep == null) {
      currentStep = task.getStepAfterStep(null, taskResult);
    }

    showStep(currentStep);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.survey_menu, menu);
    MenuItem item = menu.findItem(R.id.action_settings);
    SpannableString s = new SpannableString(item.getTitle());
    s.setSpan(new ForegroundColorSpan(Color.parseColor("#ffffff")), 0, s.length(), 0);
    item.setTitle(s);
    return true;
  }

  protected void showNextStep() {
    Bundle eventProperties = new Bundle();
    eventProperties.putString(
        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
        getString(R.string.custom_view_task_next));
    analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
    boolean eligible = checkStepResult(currentStep, taskResult);
    Step nextStep;
    if (eligible || currentStep.getIdentifier().equalsIgnoreCase("Eligibility Test")) {
      nextStep = task.getStepAfterStep(currentStep, taskResult);
      if (nextStep == null) {
        saveAndFinish();
      } else {
        showStep(nextStep);
      }
    } else {
      Intent intent = new Intent(this, NotEligibleActivity.class);
      intent.putExtra("studyId", "" + studyId);
      intent.putExtra("siteId", "" + siteId);
      startActivity(intent);
      finish();
    }
  }

  private boolean checkStepResult(Step currentStep, TaskResult taskResult) {
    String answer = "";
    for (int i = 0; i < correctAnswers.size(); i++) {
      if (correctAnswers.get(i).getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
        Map<String, StepResult> map = taskResult.getResults();
        for (Map.Entry<String, StepResult> pair : map.entrySet()) {
          if (pair.getKey().equalsIgnoreCase(currentStep.getIdentifier())) {
            try {
              StepResult stepResult = pair.getValue();
              Object o = stepResult.getResults().get("answer");
              if (o instanceof Object[]) {
                Object[] objects = (Object[]) o;
                if (objects[0] instanceof String) {
                  answer = "" + ((String) objects[0]);
                } else if (objects[0] instanceof Integer) {
                  answer = "" + ((int) objects[0]);
                }
              } else {
                answer = "" + stepResult.getResults().get("answer");
              }
            } catch (Exception e) {
              answer = "";
              Logger.log(e);
            }
          }
          if (answer.equalsIgnoreCase(correctAnswers.get(i).getAnswer())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  protected void showPreviousStep() {
    Bundle eventProperties = new Bundle();
    eventProperties.putString(
        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
        getString(R.string.custom_view_task_back));
    analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
    Step previousStep = task.getStepBeforeStep(currentStep, taskResult);
    if (previousStep == null) {
      finish();
    } else {
      showStep(previousStep);
    }
  }

  private void showStep(Step step) {
    // branching logic here
    int currentStepPosition = task.getProgressOfCurrentStep(currentStep, taskResult).getCurrent();
    int newStepPosition = task.getProgressOfCurrentStep(step, taskResult).getCurrent();

    StepLayout stepLayout = getLayoutForStep(step);
    stepLayout
        .getLayout()
        .setTag(org.researchstack.backbone.R.id.rsb_step_layout_id, step.getIdentifier());
    root.show(
        stepLayout,
        newStepPosition >= currentStepPosition
            ? StepSwitcherCustom.SHIFT_LEFT
            : StepSwitcherCustom.SHIFT_RIGHT);
    currentStep = step;
    AppController.getHelperHideKeyboard(this);
  }

  protected StepLayout getLayoutForStep(Step step) {
    // Change the title on the activity
    String title = task.getTitleForStep(this, step);
    setActionBarTitle(title);

    // Get result from the TaskResult, can be null
    StepResult result = taskResult.getStepResult(step.getIdentifier());

    // Return the Class & constructor
    StepLayout stepLayout = createLayoutFromStep(step);
    stepLayout.initialize(step, result);
    stepLayout.setCallbacks(this);

    return stepLayout;
  }

  @NonNull
  private StepLayout createLayoutFromStep(Step step) {
    try {
      Class cls = step.getStepLayoutClass();
      Constructor constructor = cls.getConstructor(Context.class);
      return (StepLayout) constructor.newInstance(this);
    } catch (Exception e) {
      Logger.log(e);
      throw new RuntimeException(e);
    }
  }

  private void saveAndFinish() {
    taskResult.setEndDate(new Date());

    Intent intent = new Intent(this, EligibleActivity.class);
    intent.putExtra("studyId", studyId);
    intent.putExtra("siteId", siteId);
    intent.putExtra("enrollId", enrollId);
    intent.putExtra("title", pdfTitle);
    intent.putExtra("eligibility", eligibility);
    intent.putExtra("type", type);
    startActivity(intent);
    finish();
  }

  @Override
  protected void onPause() {
    hideKeyboard();
    super.onPause();
    if (networkChangeReceiver != null) {
      unregisterReceiver(networkChangeReceiver);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      notifyStepOfBackPress();
      return true;
    } else if (item.getItemId() == R.id.action_settings) {
      Bundle eventProperties = new Bundle();
      eventProperties.putString(
          CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
          getString(R.string.custom_view_task_exit));
      analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
      finish();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    notifyStepOfBackPress();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putSerializable(EXTRA_TASK, task);
    outState.putSerializable(EXTRA_TASK_RESULT, taskResult);
    outState.putSerializable(STUDYID, studyId);
    outState.putSerializable(ENROLLID, enrollId);
    outState.putSerializable(ELIGIBILITY, eligibility);
    outState.putSerializable(TYPE, type);
    outState.putSerializable(EXTRA_STEP, currentStep);
  }

  private void notifyStepOfBackPress() {
    StepLayout currentStepLayout =
        (StepLayout) findViewById(org.researchstack.backbone.R.id.rsb_current_step);
    currentStepLayout.isBackEventConsumed();
  }

  @Override
  public void onSaveStep(int action, Step step, StepResult result) {
    onSaveStepResult(step.getIdentifier(), result);

    onExecuteStepAction(action);
  }

  protected void onSaveStepResult(String id, StepResult result) {
    taskResult.setStepResultForStepIdentifier(id, result);
  }

  protected void onExecuteStepAction(int action) {
    if (action == StepCallbacks.ACTION_NEXT) {
      showNextStep();
    } else if (action == StepCallbacks.ACTION_PREV) {
      showPreviousStep();
    } else if (action == StepCallbacks.ACTION_END) {
      showConfirmExitDialog();
    } else if (action == StepCallbacks.ACTION_NONE) {
      // Used when onSaveInstanceState is called of a view. No action is taken.
    } else {
      throw new IllegalArgumentException(
          "Action with value "
              + action
              + " is invalid. "
              + "See StepCallbacks for allowable arguments");
    }
  }

  private void hideKeyboard() {
    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
    if (imm.isActive() && imm.isAcceptingText()) {
      imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
  }

  private void showConfirmExitDialog() {
    AlertDialog alertDialog =
        new AlertDialog.Builder(this, R.style.MyAlertDialogStyle)
            .setTitle(R.string.app_name)
            .setMessage(R.string.exit_activity)
            .setPositiveButton(
                R.string.endtask,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_view_task_edit_task));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                    finish();
                  }
                })
            .setNegativeButton(
                R.string.cancel,
                new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    Bundle eventProperties = new Bundle();
                    eventProperties.putString(
                        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                        getString(R.string.custom_view_task_cancel));
                    analyticsInstance.logEvent(
                        CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                  }
                })
            .create();
    alertDialog.show();
  }

  @Override
  public void onCancelStep() {
    setResult(Activity.RESULT_CANCELED);
    finish();
  }

  public void setActionBarTitle(String title) {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setTitle(title);
    }
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      if (SharedPreferenceHelper.readPreference(CustomViewTaskActivity.this, "offlineEnroll", "")
          .equalsIgnoreCase("")) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog =
            new androidx.appcompat.app.AlertDialog.Builder(
                CustomViewTaskActivity.this, R.style.Style_Dialog_Rounded_Corner);
        alertDialog.setTitle("              You are offline");
        alertDialog.setMessage("You are offline. Kindly check the internet connection.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                Bundle eventProperties = new Bundle();
                //          eventProperties.putString(
                //              CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                //              getString(R.string.app_update_next_time_ok));
                //          analyticsInstance.logEvent(
                //              CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK,
                // eventProperties);
                SharedPreferenceHelper.writePreference(
                    CustomViewTaskActivity.this, "offlineEnroll", "occured");
                dialogInterface.dismiss();
              }
            });
        final androidx.appcompat.app.AlertDialog dialog = alertDialog.create();
        dialog.show();
      }
    } else {
      SharedPreferenceHelper.writePreference(
          CustomViewTaskActivity.this, "offlineEnroll", "");
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(networkChangeReceiver, intentFilter);
  }
}
