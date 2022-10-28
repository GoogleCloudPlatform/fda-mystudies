/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.custom.question;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import androidx.browser.customtabs.CustomTabsIntent;
import com.harvard.R;
import com.harvard.studyappmodule.activitybuilder.CustomSurveyViewTaskActivity;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.ui.views.SubmitBar;
import org.researchstack.backbone.utils.TextUtils;
import rx.functions.Action1;

public class InstructionStepLayoutCustom extends FixedSubmitBarLayout implements StepLayout {
  private StepCallbacks callbacks;
  private Step step;
  private WebView summary;
  SubmitBar submitBar;
  private Context context;

  public InstructionStepLayoutCustom(Context context) {
    super(context);
    this.context = context;
  }

  public InstructionStepLayoutCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }

  public InstructionStepLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.context = context;
  }

  @Override
  public void initialize(Step step, StepResult result) {
    this.step = step;
    initializeStep();
  }

  @Override
  public View getLayout() {
    return this;
  }

  @Override
  public boolean isBackEventConsumed() {
    if (summary.canGoBack()) {
      summary.goBack();
      return true;
    } else {
      callbacks.onSaveStep(StepCallbacks.ACTION_PREV, step, null);
      return false;
    }
  }

  @Override
  public void setCallbacks(StepCallbacks callbacks) {
    this.callbacks = callbacks;
  }

  @Override
  public int getContentResourceId() {
    return R.layout.rsb_step_layout_instruction_custom;
  }

  private void initializeStep() {
    if (step != null) {

      // Set Title
      if (!TextUtils.isEmpty(step.getTitle())) {
        TextView title = (TextView) findViewById(R.id.rsb_intruction_title);
        title.setVisibility(View.VISIBLE);
        title.setText(step.getTitle());
      }

      // Set Summary
      if (!TextUtils.isEmpty(step.getText())) {
        summary = (WebView) findViewById(R.id.rsb_intruction_text);
        summary.setVisibility(View.VISIBLE);
        summary.setWebViewClient(new WebViewClient() {
          @Override
          public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CustomTabsIntent customTabsIntent =
                    new CustomTabsIntent.Builder()
                            .setToolbarColor(getResources().getColor(R.color.colorAccent))
                            .setShowTitle(true)
                            .setCloseButtonIcon(
                                    BitmapFactory.decodeResource(
                                            getResources(), R.drawable.backeligibility))
                            .setStartAnimations(
                                    context.getApplicationContext(),
                                    R.anim.slide_in_right,
                                    R.anim.slide_out_left)
                            .setExitAnimations(
                                    context.getApplicationContext(),
                                    R.anim.slide_in_left,
                                    R.anim.slide_out_right)
                            .build();
            customTabsIntent.intent.setData(Uri.parse(url));
            ((CustomSurveyViewTaskActivity) context).startActivity(customTabsIntent.intent);
            return true;
          }
        });
        String all = (step.getText()).replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        if (Build.VERSION.SDK_INT >= 24) {
          summary.loadDataWithBaseURL(null,
                  Html.fromHtml((all), Html.FROM_HTML_MODE_LEGACY).toString(), "text/html", "UTF-8", null);
        } else {
          summary.loadDataWithBaseURL(null, Html.fromHtml((all)).toString(), "text/html", "UTF-8", null);
        }
      }

      // Set Next / Skip
      submitBar = (SubmitBar) findViewById(R.id.rsb_submit_bar);
      submitBar.setPositiveTitle(R.string.rsb_next);
      submitBar.setPositiveAction(new Action1() {
        @Override
        public void call(Object v) {
          callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
        }
      });

      if (step.isOptional()) {
        submitBar.setNegativeTitle(R.string.rsb_step_skip);
        submitBar.setNegativeAction(new Action1() {
          @Override
          public void call(Object v) {
            if (callbacks != null) {
              callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
            }
          }
        });
      } else {
        submitBar.getNegativeActionView().setVisibility(View.GONE);
      }
    }
  }
}