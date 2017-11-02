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

import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.AccelerometerRecorderConfig;
import org.researchstack.backbone.step.active.recorder.DeviceMotionRecorderConfig;
import org.researchstack.backbone.step.active.recorder.LocationRecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 11/1/17.
 */

public class CrfStairStep extends ActiveStep {
    public static final String ACCEL_RECORDER_ID = "accel";
    public static final String MOTION_RECORDER_ID = "motion";

    public static final int SENSOR_FREQ = 100;

    /**
     * The interval in sec between step movement instructions
     */
    public int stairInterval;
    public static final int defaultStairInterval = 2; // 2 seconds

    public CrfStairStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public CrfStairStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        setStepDuration(3 * 60); // 3 min
        List<RecorderConfig> configList = new ArrayList<>();
        configList.add(new AccelerometerRecorderConfig(ACCEL_RECORDER_ID, SENSOR_FREQ));
        configList.add(new DeviceMotionRecorderConfig(MOTION_RECORDER_ID, SENSOR_FREQ));
        setRecorderConfigurationList(configList);
        setShouldContinueOnFinish(true);
        setShouldStartTimerAutomatically(true);
        stairInterval = defaultStairInterval;
    }

    // Stair step has verbal instructions
    @Override
    public boolean hasVoice() {
        return true;
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfStairStepLayout.class;
    }
}
