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

package org.sagebase.crf.step.active;

import android.os.Build;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.sagebase.crf.camera.CameraSourcePreview;
import org.sagebase.crf.step.CrfHeartRateStepLayout;

import java.io.File;

/**
 * Created by TheMDP on 10/19/17.
 */

public class HeartRateCameraRecorderConfig extends RecorderConfig {

    public HeartRateCameraRecorderConfig() {
        super();
    }

    public HeartRateCameraRecorderConfig(String identifier) {
        super(identifier);
    }

    @Override
    public Recorder recorderForStep(Step step, File outputDirectory) {
        // Return null because this recorder config requires special setup that is different than the rest
        return null;
    }

    /**
     * @param cameraSourcePreview a valid view that has been added to a ViewGroup already

     */
    public Recorder recorderForStep(CameraSourcePreview cameraSourcePreview, Step step,
                                    CrfHeartRateStepLayout heartRateStepLayout,
                                    File outputDirectory) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new HeartRateCamera2Recorder(getIdentifier(), step, outputDirectory,
                    heartRateStepLayout);
        }
        return new HeartRateCameraRecorder(getIdentifier(), step, outputDirectory,
                heartRateStepLayout, cameraSourcePreview);
    }
}
