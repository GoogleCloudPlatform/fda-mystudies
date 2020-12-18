/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.custom;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Constructor;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.ViewWebDocumentActivity;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.step.layout.TextViewLinkHandler;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.TextUtils;
import rx.functions.Action1;

public class SurveyStepLayoutCustom extends FixedSubmitBarLayoutCustom implements StepLayout {
  private QuestionStep questionStep;
  private StepResult stepResult;
  private StepCallbacks callbacks;
  private LinearLayout container;
  private StepBody stepBody;

  public SurveyStepLayoutCustom(Context context) {
    super(context);
  }

  public SurveyStepLayoutCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SurveyStepLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public SurveyStepLayoutCustom(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public void initialize(Step step) {
    initialize(step, null);
  }

  @Override
  public void initialize(Step step, StepResult result) {
    if (!(step instanceof QuestionStep)) {
      throw new RuntimeException("Step being used in SurveyStep is not a QuestionStep");
    }

    this.questionStep = (QuestionStep) step;
    this.stepResult = result;

    initializeStep();
  }

  @Override
  public View getLayout() {
    return this;
  }

  @Override
  public boolean isBackEventConsumed() {
    callbacks.onSaveStep(StepCallbacks.ACTION_PREV, getStep(), stepBody.getStepResult(false));
    return false;
  }

  @Override
  public void setCallbacks(StepCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override
  public int getContentResourceId() {
    return org.researchstack.backbone.R.layout.rsb_step_layout;
  }

  public void initializeStep() {
    initStepLayout();
    initStepBody();
  }

  public void initStepLayout() {
    container =
        (LinearLayout) findViewById(org.researchstack.backbone.R.id.rsb_survey_content_container);
    TextView title = (TextView) findViewById(org.researchstack.backbone.R.id.rsb_survey_title);
    TextView summary = (TextView) findViewById(org.researchstack.backbone.R.id.rsb_survey_text);
    SubmitBar submitBar = (SubmitBar) findViewById(org.researchstack.backbone.R.id.rsb_submit_bar);
    submitBar.setPositiveAction(
        new Action1() {
          @Override
          public void call(Object v) {
            SurveyStepLayoutCustom.this.onNextClicked();
          }
        });

    if (questionStep != null) {
      if (!TextUtils.isEmpty(questionStep.getTitle())) {
        title.setVisibility(View.VISIBLE);
        title.setText(questionStep.getTitle());
      }

      if (!TextUtils.isEmpty(questionStep.getText())) {
        summary.setVisibility(View.VISIBLE);
        summary.setText(Html.fromHtml(questionStep.getText()));
        summary.setMovementMethod(
            new TextViewLinkHandler() {
              @Override
              public void onLinkClick(String url) {
                String path =
                    ResourcePathManager.getInstance()
                        .generateAbsolutePath(ResourcePathManager.Resource.TYPE_HTML, url);
                Intent intent =
                    ViewWebDocumentActivity.newIntentForPath(
                        getContext(), questionStep.getTitle(), path);
                getContext().startActivity(intent);
              }
            });
      }

      if (questionStep.isOptional()) {
        submitBar.setNegativeTitle(org.researchstack.backbone.R.string.rsb_step_skip);
        submitBar.setNegativeAction(
            new Action1() {
              @Override
              public void call(Object v) {
                SurveyStepLayoutCustom.this.onSkipClicked();
              }
            });
      } else {
        submitBar.getNegativeActionView().setVisibility(View.GONE);
      }
    }
  }

  public void initStepBody() {

    LayoutInflater inflater = LayoutInflater.from(getContext());
    stepBody = createStepBody(questionStep, stepResult);
    View body = stepBody.getBodyView(StepBody.VIEW_TYPE_DEFAULT, inflater, this);

    if (body != null) {
      View oldView = container.findViewById(org.researchstack.backbone.R.id.rsb_survey_step_body);
      int bodyIndex = container.indexOfChild(oldView);
      container.removeView(oldView);
      container.addView(body, bodyIndex);
      body.setId(org.researchstack.backbone.R.id.rsb_survey_step_body);
    }
  }

  @NonNull
  private StepBody createStepBody(QuestionStep questionStep, StepResult result) {
    try {
      Class cls = questionStep.getStepBodyClass();
      Constructor constructor = cls.getConstructor(Step.class, StepResult.class);
      return (StepBody) constructor.newInstance(questionStep, result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Parcelable onSaveInstanceState() {
    callbacks.onSaveStep(StepCallbacks.ACTION_NONE, getStep(), stepBody.getStepResult(false));
    return super.onSaveInstanceState();
  }

  protected void onNextClicked() {
    BodyAnswer bodyAnswer = stepBody.getBodyAnswerState();

    if (bodyAnswer == null || !bodyAnswer.isValid()) {
      Toast.makeText(
              getContext(),
              bodyAnswer == null
                  ? BodyAnswer.INVALID.getString(getContext())
                  : bodyAnswer.getString(getContext()),
              Toast.LENGTH_SHORT)
          .show();
    } else {
      callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, getStep(), stepBody.getStepResult(false));
    }
  }

  public void onSkipClicked() {
    if (callbacks != null) {
      callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, getStep(), stepBody.getStepResult(true));
    }
  }

  public Step getStep() {
    return questionStep;
  }

  public String getString(@StringRes int stringResId) {
    return getResources().getString(stringResId);
  }
}
