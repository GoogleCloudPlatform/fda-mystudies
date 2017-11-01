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

package org.sagebase.crf.step;

import android.content.Context;
import android.graphics.Path;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.researchstack.backbone.step.active.recorder.LocationRecorder;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;
import org.researchstack.backbone.ui.views.ArcDrawable;
import org.sagebionetworks.research.crf.R;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by TheMDP on 10/31/17.
 */

public class Crf12MinWalkingStepLayout extends ActiveStepLayout {

    private TextView crfCountdownText;

    protected TextView distanceNumber;

    protected View arcDrawableContainer;
    protected ArcDrawable arcDrawable;

    public Crf12MinWalkingStepLayout(Context context) {
        super(context);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Crf12MinWalkingStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.crf_step_layout_12_min_walk;
    }

    @Override
    public int getContentContainerLayoutId() {
        return R.id.crf_step_layout_container;
    }

    @Override
    public int getFixedSubmitBarLayoutId() {
        return R.layout.crf_step_layout_container;
    }

    @Override
    public void doUIAnimationPerSecond() {
        long min = secondsLeft / 60;
        long sec = secondsLeft % 60;
        crfCountdownText.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));

        float progress = 1.0f - ((float)secondsLeft - (float)activeStep.getStepDuration());
        arcDrawable.setSweepAngle(ArcDrawable.FULL_SWEEPING_ANGLE * progress);
    }

    @Override
    public void start() {
        super.start();

        distanceNumber.setText("--");

        for (Recorder recorder : recorderList) {
            if (recorder instanceof LocationRecorder) {
                LocationRecorder locationRecorder = (LocationRecorder)recorder;
                locationRecorder.setLocationUpdateListener(new LocationRecorder.LocationUpdateListener() {
                    @Override
                    public void onLocationUpdated(double longitude, double latitude, double distance) {
                        int distanceInFeet = (int)(3.28084 * distance);
                        DecimalFormat formatter = new DecimalFormat("#,###,###");
                        distanceNumber.setText(formatter.format((int)distanceInFeet));
                    }
                });
            }
        }
    }

    @Override
    public void setupActiveViews() {
        super.setupActiveViews();

        crfCountdownText = findViewById(R.id.crf_12_min_walk_countdown_text);

        distanceNumber = findViewById(R.id.crf_distance_number);

        arcDrawableContainer = findViewById(R.id.crf_arc_drawable_container);
        arcDrawable = new ArcDrawable();
        arcDrawable.setColor(ResourcesCompat.getColor(getResources(), R.color.greenyBlue, null));
        arcDrawable.setArchWidth(getResources().getDimensionPixelOffset(R.dimen.crf_ard_drawable_width));
        arcDrawable.setDirection(Path.Direction.CW);
        arcDrawableContainer.setBackground(arcDrawable);
    }
}
