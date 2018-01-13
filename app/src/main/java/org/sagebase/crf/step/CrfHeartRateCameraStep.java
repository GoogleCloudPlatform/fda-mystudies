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

import com.google.common.collect.ImmutableMap;

import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TheMDP on 10/31/17.
 */

public class CrfHeartRateCameraStep extends ActiveStep {

    public static final String MOTION_RECORDER_ID = "motion";
    public static final int SENSOR_FREQ = 100;

    public static final int STEP_DURATION = 60; // 1 minute

    static final Map<String, String> SPOKEN_TEXT_MAP =
            ImmutableMap.<String, String>builder()
                    .put(  "0", "Please keep still")
                    .put( "30", "You are half way there!")
                    .put( "45", "Just 15 seconds left")
                    .put("end", "You are all done!")
                    .build();

    public CrfHeartRateCameraStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public CrfHeartRateCameraStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    public void commonInit() {
        List<RecorderConfig> recorderConfigList = new ArrayList<>();
        setStepDuration(STEP_DURATION);
        recorderConfigList.add(new DeviceMotionRecorderConfig(MOTION_RECORDER_ID, SENSOR_FREQ));
        setRecorderConfigurationList(recorderConfigList);
        setShouldStartTimerAutomatically(true);
        setShouldContinueOnFinish(false);
        setShouldShowDefaultTimer(false);
        setSpokenInstructionMap(SPOKEN_TEXT_MAP);
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfHeartRateStepLayout.class;
    }
}
