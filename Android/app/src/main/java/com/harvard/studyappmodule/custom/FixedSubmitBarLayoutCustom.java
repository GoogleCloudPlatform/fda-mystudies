/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.harvard.R;
import org.researchstack.backbone.ui.step.layout.StepLayout;
import org.researchstack.backbone.ui.views.ObservableScrollView;

public abstract class FixedSubmitBarLayoutCustom extends FrameLayout implements StepLayout {
  public FixedSubmitBarLayoutCustom(Context context) {
    super(context);
    init();
  }

  public FixedSubmitBarLayoutCustom(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public FixedSubmitBarLayoutCustom(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(21)
  public FixedSubmitBarLayoutCustom(
      Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public abstract int getContentResourceId();

  private void init() {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.submitbar, this, true);
    ViewGroup contentContainer = (ViewGroup) findViewById(R.id.rsb_content_container);
    View content = inflater.inflate(getContentResourceId(), contentContainer, false);
    contentContainer.addView(content, 0);
    final ObservableScrollView scrollView =
        (ObservableScrollView) findViewById(R.id.rsb_content_container_scrollview);
    scrollView.setScrollbarFadingEnabled(false);
  }
}
