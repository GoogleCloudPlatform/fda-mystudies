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

package org.sagebase.crf.view;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.sagebase.crf.step.CrfTaskToolbarManipulator;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/26/17.
 */

public class CrfTransparentToolbar extends Toolbar {

    private static final long PROGRESS_BAR_ANIM_DURATION = 200; // ms
    private static final int PROGRESS_BAR_SCALE_FACTOR = 1000; // allows smooth animation

    private ProgressBar progressBar;
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public CrfTransparentToolbar(Context context) {
        super(context);
        commonInit();
    }

    public CrfTransparentToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        commonInit();
    }

    public CrfTransparentToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit();
    }

    private void commonInit() {
        setBackgroundResource(R.drawable.crf_toolbar_background);

        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.crf_progress_bar));
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                getContext().getResources().getDimensionPixelOffset(R.dimen.crf_progress_bar_height));
        params.gravity = Gravity.CENTER;
        addView(progressBar, params);
    }

    public void refreshToolbar(ActionBar actionBar, CrfTaskToolbarManipulator toolbarManipulator) {
        showProgressInToolbar(toolbarManipulator.showProgress());

        @ColorRes int tint = toolbarManipulator.tintColor();
        @DrawableRes int leftIcon = toolbarManipulator.leftIcon();
        @DrawableRes int rightIcon = toolbarManipulator.rightIcon();
        tintToolbar(actionBar, tint, leftIcon, rightIcon);
    }

    public void tintToolbar(ActionBar actionBar,
                            @ColorRes int tintColor,
                            @DrawableRes int leftIcon,
                            @DrawableRes int rightIcon) {

        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(leftIcon);
        }

        MenuItem rightItem = getMenu().findItem(R.id.rsb_clear_menu_item);
        if (rightItem != null) {
            if (rightIcon == CrfTaskToolbarManipulator.NO_ICON) {
                rightItem.setVisible(false);
            } else {
                rightItem.setVisible(true);
                rightItem.setIcon(ContextCompat.getDrawable(getContext(), rightIcon));
            }
        }

        int colorRes = ContextCompat.getColor(getContext(), tintColor);
        Drawable drawable = getNavigationIcon();
        if (drawable != null) {
            drawable.setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
        }
        for (int i = 0; i < getMenu().size(); i++) {
            MenuItem menuItem = getMenu().getItem(i);
            if (menuItem != null && menuItem.getIcon() != null) {
                menuItem.getIcon().setColorFilter(colorRes, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    public void setProgress(int progress, int max) {
        int scaledFrom = progressBar.getProgress();
        int scaledTo = progress * PROGRESS_BAR_SCALE_FACTOR;
        int scaledMax = max * PROGRESS_BAR_SCALE_FACTOR;

        // if the max changed we can't safely animate the change
        boolean animate = (progressBar.getMax() == scaledMax);

        if (animate) {
            ProgressBarAnimation anim = new ProgressBarAnimation(progressBar, scaledFrom, scaledTo);
            anim.setDuration(PROGRESS_BAR_ANIM_DURATION);
            progressBar.startAnimation(anim);
        } else {
            progressBar.setMax(scaledMax);
            progressBar.setProgress(scaledTo);
        }
    }

    public void showProgressInToolbar(boolean showProgress) {
        // Hide with INVISIBLE so that the "Step 1 of 4" title does not show automatically
        progressBar.setVisibility(showProgress ? View.VISIBLE : View.INVISIBLE);
    }
}
