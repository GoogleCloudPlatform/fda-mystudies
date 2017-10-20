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

import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 10/19/17.
 */

public class HeartRateCameraStep extends ActiveStep {
    public HeartRateCameraStep(String identifier) {
        super(identifier);
        commonInit();
    }

    public HeartRateCameraStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
        commonInit();
    }

    private void commonInit() {
        List<RecorderConfig> recorderConfigList = new ArrayList<>();
        recorderConfigList.add(new HeartRateCameraRecorderConfig("HeartRateCamera"));
        setRecorderConfigurationList(recorderConfigList);
        setShouldStartTimerAutomatically(true);
        setShouldContinueOnFinish(true);
        setStepDuration(60);
        setShouldShowDefaultTimer(true);
    }

    @Override
    public Class getStepLayoutClass() {
        return HeartRateStepLayout.class;
    }
}
