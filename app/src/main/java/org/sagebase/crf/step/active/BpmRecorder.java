/*
 *    Copyright 2018 Sage Bionetworks
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

import android.content.Context;

import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.JsonArrayDataRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by liujoshua on 2/19/2018.
 */

public interface BpmRecorder {
    public abstract void setEnableIntelligentStart(boolean enableIntelligenetStart);

    public abstract void setIntelligentStartListener(IntelligentStartUpdateListener
                                                             intelligentStartListener);

    public abstract void setBpmUpdateListener(BpmUpdateListener bpmUpdateListener);

    interface BpmUpdateListener {
        class BpmHolder {
            public final int bpm;
            public final long timestamp;

            public BpmHolder(int bpm, long timestamp) {
                this.bpm = bpm;
                this.timestamp = timestamp;
            }
        }

        void bpmUpdate(BpmHolder bpm);
    }

    interface IntelligentStartUpdateListener {
        /**
         * @param progress value from 0.0 to 1.0 communicating the progress to being ready
         * @param ready    true if the camera is now collecting data, false otherwise
         */
        void intelligentStartUpdate(float progress, boolean ready);
    }

    class HeartBeatJsonWriter extends JsonArrayDataRecorder
            implements HeartbeatSampleTracker
            .HeartRateUpdateListener {
        private static final Logger LOG = LoggerFactory.getLogger(HeartBeatJsonWriter.class);
        
        private static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
        private static final String HEART_RATE_KEY = "bpm_camera";
        private static final String HUE_KEY = "hue";
        private static final String SATURATION_KEY = "saturation";
        private static final String BRIGHTNESS_KEY = "brightness";
        private static final String RED_KEY = "red";
        private static final String GREEN_KEY = "green";
        private static final String BLUE_KEY = "blue";

        private static final int INTELLIGENT_START_FRAMES_TO_PASS = 10;

        private final JsonObject mJsonObject = new JsonObject();

        /**
         * Intelligent start is a feature that delays recording until
         * an algorithm determines the user's finger is in front of the camera
         * Disabled by default
         */
        private boolean mEnableIntelligentStart = true;
        private boolean mIntelligentStartPassed = false;
        private int mIntelligentStartCounter = 0;

        private final BpmRecorder.BpmUpdateListener mBpmUpdateListener;
        private final BpmRecorder.IntelligentStartUpdateListener mIntelligentStartListener;

        public HeartBeatJsonWriter(BpmUpdateListener
                                           mBpmUpdateListener, IntelligentStartUpdateListener
                mIntelligentStartListener,
                                   String identifier, Step step, File outputDirectory) {
            super(identifier, step, outputDirectory);

            this.mBpmUpdateListener = mBpmUpdateListener;
            this.mIntelligentStartListener = mIntelligentStartListener;
        }

        @Override
        public void onHeartRateSampleDetected(HeartBeatSample sample) {
            mJsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY, sample.t / 1000F);
            mJsonObject.addProperty(HUE_KEY, sample.h);
            mJsonObject.addProperty(SATURATION_KEY, sample.s);
            mJsonObject.addProperty(BRIGHTNESS_KEY, sample.v);
            mJsonObject.addProperty(RED_KEY, sample.r);
            mJsonObject.addProperty(GREEN_KEY, sample.g);
            mJsonObject.addProperty(BLUE_KEY, sample.b);

            if (sample.bpm > 0) {
                mJsonObject.addProperty(HEART_RATE_KEY, sample.bpm);
                if (mBpmUpdateListener != null) {
                    mBpmUpdateListener.bpmUpdate(
                            new BpmRecorder.BpmUpdateListener.BpmHolder(sample.bpm, sample.t));
                }
            } else {
                mJsonObject.remove(HEART_RATE_KEY);
            }
            
            LOG.trace("HeartBeatSample: {}", sample);

            if (!mEnableIntelligentStart || mIntelligentStartPassed) {
                writeJsonObjectToFile(mJsonObject);
            } else {
                updateIntelligentStart(sample);
            }
        }

        private void updateIntelligentStart(HeartBeatSample sample) {
            if (mIntelligentStartPassed) {
                return; // we already computed that we could start
            }

            // When a finger is placed in front of the camera with the flash on,
            // the camera image will be almost entirely red, so use a simple lenient algorithm
            // for this
            int redIntensityFactorThreshold = 4;
            float greenBlueSum = sample.g + sample.b;
            if (greenBlueSum == 0.0f) {
                greenBlueSum = 0.0000000001f;
            }
            float redFactor = sample.r / greenBlueSum;
            LOG.debug("RedFactor: {}", redFactor);
            
            // If the red factor is large enough, we update the trigger
            if (redFactor >= redIntensityFactorThreshold) {
                mIntelligentStartCounter++;
                if (mIntelligentStartCounter >= INTELLIGENT_START_FRAMES_TO_PASS) {
                    mIntelligentStartPassed = true;
                }
                if (mIntelligentStartListener != null) {
                    float progress = (float) mIntelligentStartCounter / (float)
                            INTELLIGENT_START_FRAMES_TO_PASS;
                    mIntelligentStartListener.intelligentStartUpdate(progress,
                            mIntelligentStartPassed);
                }

            } else {  // We need thresholds to be passed sequentially otherwise it is restarted
                mIntelligentStartCounter = 0;
            }
        }

        @Override
        public void start(Context context) {
            startJsonDataLogging();
            mIntelligentStartPassed = false;
            mIntelligentStartCounter = 0;
        }

        @Override
        public void stop() {
            stopJsonDataLogging();
        }
    }
}
