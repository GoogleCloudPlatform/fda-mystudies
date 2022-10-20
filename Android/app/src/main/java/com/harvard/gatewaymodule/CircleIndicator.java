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

package com.harvard.gatewaymodule;

import static androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import androidx.annotation.AnimatorRes;
import androidx.annotation.DrawableRes;
import androidx.viewpager.widget.ViewPager;
import com.harvard.R;

public class CircleIndicator extends LinearLayout {

  private static final int DEFAULT_INDICATOR_WIDTH = 5;
  private ViewPager viewpager;
  private int indicatorMargin = -1;
  private int indicatorWidth = -1;
  private int indicatorHeight = -1;
  private int animatorResId = R.animator.scale_with_alpha;
  private int animatorReverseResId = 0;
  private int indicatorBackgroundResId = R.drawable.blue_radius;
  private int indicatorUnselectedBackgroundResId = R.drawable.white_radius;
  private Animator animatorOut;
  private Animator animatorIn;
  private Animator immediateAnimatorOut;
  private Animator immediateAnimatorIn;
  private int lastPosition = -1;

  public CircleIndicator(Context context) {
    super(context);
    init(context, null);
  }

  public CircleIndicator(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public CircleIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    handleTypedArray(context, attrs);
    checkIndicatorConfig(context);
  }

  private void handleTypedArray(Context context, AttributeSet attrs) {
    if (attrs == null) {
      return;
    }

    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator);
    indicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1);
    indicatorHeight = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1);
    indicatorMargin = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1);

    animatorResId =
        typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_animator, R.animator.scale_with_alpha);
    animatorReverseResId =
        typedArray.getResourceId(R.styleable.CircleIndicator_ci_animator_reverse, 0);
    indicatorBackgroundResId =
        typedArray.getResourceId(R.styleable.CircleIndicator_ci_drawable, R.drawable.blue_radius);
    indicatorUnselectedBackgroundResId =
        typedArray.getResourceId(
            R.styleable.CircleIndicator_ci_drawable_unselected, R.drawable.white_radius);

    int orientation = typedArray.getInt(R.styleable.CircleIndicator_ci_orientation, -1);
    setOrientation(orientation == VERTICAL ? VERTICAL : HORIZONTAL);

    int gravity = typedArray.getInt(R.styleable.CircleIndicator_ci_gravity, -1);
    setGravity(gravity >= 0 ? gravity : Gravity.CENTER);

    typedArray.recycle();
  }

  /** Create and configure Indicator in Java code. */
  public void configureIndicator(int indicatorWidth, int indicatorHeight, int indicatorMargin) {
    configureIndicator(
        indicatorWidth,
        indicatorHeight,
        indicatorMargin,
        R.animator.scale_with_alpha,
        0,
        R.drawable.white_radius,
        R.drawable.white_radius);
  }

  public void configureIndicator(
      int indicatorWidth,
      int indicatorHeight,
      int indicatorMargin,
      @AnimatorRes int animatorId,
      @AnimatorRes int animatorReverseId,
      @DrawableRes int indicatorBackgroundId,
      @DrawableRes int indicatorUnselectedBackgroundId) {

    indicatorWidth = indicatorWidth;
    indicatorHeight = indicatorHeight;
    indicatorMargin = indicatorMargin;

    animatorResId = animatorId;
    animatorReverseResId = animatorReverseId;
    indicatorBackgroundResId = indicatorBackgroundId;
    indicatorUnselectedBackgroundResId = indicatorUnselectedBackgroundId;

    checkIndicatorConfig(getContext());
  }

  private void checkIndicatorConfig(Context context) {
    indicatorWidth = (indicatorWidth < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorWidth;
    indicatorHeight = (indicatorHeight < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorHeight;
    indicatorMargin = (indicatorMargin < 0) ? dip2px(DEFAULT_INDICATOR_WIDTH) : indicatorMargin;

    animatorResId = (animatorResId == 0) ? R.animator.scale_with_alpha : animatorResId;

    animatorOut = createAnimatorOut(context);
    immediateAnimatorOut = createAnimatorOut(context);
    immediateAnimatorOut.setDuration(0);

    animatorIn = createAnimatorIn(context);
    immediateAnimatorIn = createAnimatorIn(context);
    immediateAnimatorIn.setDuration(0);

    indicatorBackgroundResId =
        (indicatorBackgroundResId == 0) ? R.drawable.blue_radius : indicatorBackgroundResId;
    indicatorUnselectedBackgroundResId =
        (indicatorUnselectedBackgroundResId == 0)
            ? R.drawable.white_radius
            : indicatorUnselectedBackgroundResId;
  }

  private Animator createAnimatorOut(Context context) {
    return AnimatorInflater.loadAnimator(context, animatorResId);
  }

  private Animator createAnimatorIn(Context context) {
    Animator animatorIn;
    if (animatorReverseResId == 0) {
      animatorIn = AnimatorInflater.loadAnimator(context, animatorResId);
      animatorIn.setInterpolator(new ReverseInterpolator());
    } else {
      animatorIn = AnimatorInflater.loadAnimator(context, animatorReverseResId);
    }
    return animatorIn;
  }

  public void setViewPager(ViewPager viewPager) {
    viewpager = viewPager;
    if (viewpager != null && viewpager.getAdapter() != null) {
      lastPosition = -1;
      createIndicators();
      viewpager.removeOnPageChangeListener(internalPageChangeListener);
      viewpager.addOnPageChangeListener(internalPageChangeListener);
      internalPageChangeListener.onPageSelected(viewpager.getCurrentItem());
    }
  }

  private final OnPageChangeListener internalPageChangeListener =
      new OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {

          if (viewpager.getAdapter() == null || viewpager.getAdapter().getCount() <= 0) {
            return;
          }

          if (animatorIn.isRunning()) {
            animatorIn.end();
            animatorIn.cancel();
          }

          if (animatorOut.isRunning()) {
            animatorOut.end();
            animatorOut.cancel();
          }

          View currentIndicator;
          if (lastPosition >= 0 && (currentIndicator = getChildAt(lastPosition)) != null) {
            currentIndicator.setBackgroundResource(indicatorUnselectedBackgroundResId);
            animatorIn.setTarget(currentIndicator);
            animatorIn.start();
          }

          View selectedIndicator = getChildAt(position);
          if (selectedIndicator != null) {
            selectedIndicator.setBackgroundResource(indicatorBackgroundResId);
            animatorOut.setTarget(selectedIndicator);
            animatorOut.start();
          }
          lastPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
      };

  public DataSetObserver getDataSetObserver() {
    return internalDataSetObserver;
  }

  private DataSetObserver internalDataSetObserver =
      new DataSetObserver() {
        @Override
        public void onChanged() {
          super.onChanged();
          if (viewpager == null) {
            return;
          }

          int newCount = viewpager.getAdapter().getCount();
          int currentCount = getChildCount();

          if (newCount == currentCount) { // No change
            return;
          } else if (lastPosition < newCount) {
            lastPosition = viewpager.getCurrentItem();
          } else {
            lastPosition = -1;
          }

          createIndicators();
        }
      };

  /** @deprecated
   * User ViewPager addOnPageChangeListener */
  @Deprecated
  public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
    if (viewpager == null) {
      throw new NullPointerException("can not find Viewpager , setViewPager first");
    }
    viewpager.removeOnPageChangeListener(onPageChangeListener);
    viewpager.addOnPageChangeListener(onPageChangeListener);
  }

  private void createIndicators() {
    removeAllViews();
    int count = viewpager.getAdapter().getCount();
    if (count <= 0) {
      return;
    }
    int currentItem = viewpager.getCurrentItem();
    int orientation = getOrientation();

    for (int i = 0; i < count; i++) {
      if (currentItem == i) {
        addIndicator(orientation, indicatorBackgroundResId, immediateAnimatorOut);
      } else {
        addIndicator(orientation, indicatorUnselectedBackgroundResId, immediateAnimatorIn);
      }
    }
  }

  private void addIndicator(
      int orientation, @DrawableRes int backgroundDrawableId, Animator animator) {
    if (animator.isRunning()) {
      animator.end();
      animator.cancel();
    }

    View indicator = new View(getContext());
    indicator.setBackgroundResource(backgroundDrawableId);
    addView(indicator, indicatorWidth, indicatorHeight);
    LayoutParams lp = (LayoutParams) indicator.getLayoutParams();

    if (orientation == HORIZONTAL) {
      lp.leftMargin = indicatorMargin;
      lp.rightMargin = indicatorMargin;
    } else {
      lp.topMargin = indicatorMargin;
      lp.bottomMargin = indicatorMargin;
    }

    indicator.setLayoutParams(lp);

    animator.setTarget(indicator);
    animator.start();
  }

  private class ReverseInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float value) {
      return Math.abs(1.0f - value);
    }
  }

  public int dip2px(float dpValue) {
    final float scale = getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }
}
