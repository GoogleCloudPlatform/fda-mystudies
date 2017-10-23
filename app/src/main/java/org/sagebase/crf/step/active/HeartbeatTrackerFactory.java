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

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;

/**
 * Created by TheMDP on 10/17/17.
 */

public class HeartbeatTrackerFactory implements MultiProcessor.Factory<HeartBeatSample> {

    private HeartbeatSampleTracker tracker;

    public HeartbeatTrackerFactory(HeartbeatSampleTracker.HeartRateUpdateListener listener) {
        tracker = new HeartbeatSampleTracker();
        tracker.setHeartRateUpdateListener(listener);
    }

    @Override
    public Tracker<HeartBeatSample> create(HeartBeatSample heartBeatSample) {
        return tracker;
    }
}
