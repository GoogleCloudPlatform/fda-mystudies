/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.consent;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import com.harvard.R;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import rx.functions.Action1;

public class ConsentDocumentStepLayoutCustom extends LinearLayout implements StepLayout {
  private StepCallbacks callbacks;

  private String confirmationDialogBody;
  private String htmlContent;

  private ConsentDocumentStep step;
  private StepResult<Boolean> stepResult;

  public ConsentDocumentStepLayoutCustom(Context context) {
    super(context);
  }

  public ConsentDocumentStepLayoutCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ConsentDocumentStepLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public void initialize(Step step, StepResult result) {
    this.step = (ConsentDocumentStep) step;
    this.confirmationDialogBody = ((ConsentDocumentStep) step).getConfirmMessage();
    this.htmlContent = ((ConsentDocumentStep) step).getConsentHTML();
    this.stepResult = result;

    if (stepResult == null) {
      stepResult = new StepResult<>(step);
    }

    initializeStep();
  }

  @Override
  public View getLayout() {
    return this;
  }

  @Override
  public boolean isBackEventConsumed() {
    stepResult.setResult(false);
    callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, stepResult);
    return false;
  }

  @Override
  public void setCallbacks(StepCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  private void initializeStep() {
    setOrientation(VERTICAL);
    LayoutInflater.from(getContext()).inflate(R.layout.rsb_step_layout_consent_doc, this, true);

    WebView pdfView = (WebView) findViewById(R.id.webview);
    String htmlBase64 = Base64.encodeToString(htmlContent.getBytes(), Base64.NO_WRAP);
    pdfView.loadData(htmlBase64, "text/html", "base64");

    SubmitBar submitBar = (SubmitBar) findViewById(R.id.submit_bar);
    submitBar.setPositiveAction(new Action1() {
      @Override
      public void call(Object v) {
        ConsentDocumentStepLayoutCustom.this.showDialog();
      }
    });
    submitBar.setNegativeAction(new Action1() {
      @Override
      public void call(Object v) {
        callbacks.onCancelStep();
      }
    });
  }

  private void showDialog() {
    new AlertDialog.Builder(getContext())
            .setTitle(R.string.rsb_consent_review_alert_title)
            .setMessage(confirmationDialogBody)
            .setCancelable(false)
            .setPositiveButton(
                    R.string.rsb_agree, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        stepResult.setResult(true);
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                      }
                    })
            .setNegativeButton(
                    R.string.rsb_consent_review_cancel, new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                        // Gives them a chance to read it again
                      }
                    })
            .show();
  }
}