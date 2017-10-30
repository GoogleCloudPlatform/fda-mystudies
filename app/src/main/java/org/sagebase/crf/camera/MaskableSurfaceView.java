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

package org.sagebase.crf.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * Created by TheMDP on 10/30/17.
 *
 * The main purpose of this class is to make the SurfaceView display as a Circle
 */

public class MaskableSurfaceView extends SurfaceView {

    private Path mPathMask;

    private boolean mEnabledMask = false;
    public void setEnabledMask(boolean enableMask) {
        mEnabledMask = enableMask;
    }

    public MaskableSurfaceView(Context context) {
        super(context);
        commonInit();
    }

    public MaskableSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit();
    }

    public MaskableSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit();
    }

    @TargetApi(21)
    public MaskableSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        commonInit();
    }

    private void commonInit() {
        // This needs to be set to work
        setZOrderMediaOverlay(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mEnabledMask) {
            boolean maskNeedsUpdated = false;
            if (mPathMask == null) {
                mPathMask = new Path();
                int diameter = Math.min(getWidth(), getHeight());
                int radius = diameter / 2;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                mPathMask.addCircle(centerX, centerY, radius, Path.Direction.CW);
            }
            canvas.clipPath(mPathMask);
        }
        super.dispatchDraw(canvas);
    }
}
