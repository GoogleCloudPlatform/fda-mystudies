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
        params.rightMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.rsb_padding_small);
        params.gravity = Gravity.CENTER;
        addView(progressBar, params);
    }

    /**
     * Helper method for passing in any object that could possibly implement a Manipulator interface
     * @param manipulator for anything to happen, Object must be an instanceof
     *                    CrfTaskToolbarActionManipulator,
     *                    CrfTaskToolbarIconManipulator,
     *                    CrfTaskToolbarTintManipulator, or
     *                    CrfTaskToolbarProgressManipulator
     */
    public void refreshToolbar(ActionBar actionBar,
                               Object manipulator,
                               @ColorRes int defaultTint,
                               @DrawableRes int defaultLeftIcon,
                               @DrawableRes int defaultRightIcon) {

        // Progress manipulator
        if (manipulator instanceof CrfTaskToolbarProgressManipulator) {
            CrfTaskToolbarProgressManipulator progressManipulator = (CrfTaskToolbarProgressManipulator)manipulator;
            showProgressInToolbar(progressManipulator.crfToolbarShowProgress());
        }

        // Icon manipulator
        if (manipulator instanceof CrfTaskToolbarIconManipulator) {
            CrfTaskToolbarIconManipulator iconManipulator = (CrfTaskToolbarIconManipulator)manipulator;
            setIcons(actionBar, iconManipulator.crfToolbarLeftIcon(), iconManipulator.crfToolbarRightIcon());
        } else {
            setIcons(actionBar, defaultLeftIcon, defaultRightIcon);
        }

        // Tint manipulator
        if (manipulator instanceof CrfTaskToolbarTintManipulator) {
            CrfTaskToolbarTintManipulator tintManipulator = (CrfTaskToolbarTintManipulator) manipulator;
            setTint(tintManipulator.crfToolbarTintColor());
        } else {
            setTint(defaultTint);
        }
    }

    public void setIcons(ActionBar actionBar, @DrawableRes int leftIcon, @DrawableRes int rightIcon) {
        if (actionBar != null && leftIcon != CrfTaskToolbarIconManipulator.NO_ICON) {
            actionBar.setHomeAsUpIndicator(leftIcon);
        }

        MenuItem rightItem = getMenu().findItem(R.id.rsb_clear_menu_item);
        if (rightItem != null) {
            if (rightIcon == CrfTaskToolbarIconManipulator.NO_ICON) {
                rightItem.setVisible(false);
            } else {
                rightItem.setVisible(true);
                rightItem.setIcon(ContextCompat.getDrawable(getContext(), rightIcon));
            }
        }
    }

    public void setTint(@ColorRes int color) {
        int colorRes = ContextCompat.getColor(getContext(), color);
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
