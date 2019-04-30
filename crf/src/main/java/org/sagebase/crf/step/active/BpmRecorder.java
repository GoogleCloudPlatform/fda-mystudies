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
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.UiThread;

import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.JsonArrayDataRecorder;
import org.researchstack.backbone.utils.FormatHelper;
import org.sagebase.crf.step.active.HeartBeatSample;
import org.sagebase.crf.step.active.HeartRateBPM;
import org.sagebase.crf.step.active.HeartRateSampleProcessor;
import org.sagebase.crf.step.active.HeartbeatSampleTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by liujoshua on 2/19/2018.
 */

public interface BpmRecorder {

    interface BpmUpdateListener {
        class BpmHolder {
            public final int bpm;
            public final long timestamp;
            public final double confidence;

            public BpmHolder(int bpm, long timestamp, double confidence) {
                this.bpm = bpm;
                this.timestamp = timestamp;
                this.confidence = confidence;
            }
        }

        @UiThread
        void bpmUpdate(BpmHolder bpm);
    }

    interface IntelligentStartUpdateListener {
        /**
         * @param progress value from 0.0 to 1.0 communicating the progress to being ready
         * @param ready    true if the camera is now collecting data, false otherwise
         */
        void intelligentStartUpdate(float progress, boolean ready);
    }

    interface PressureListener {
        class PressureHolder {
            public final boolean pressureExcessive;

            public PressureHolder(boolean pressureExcessive) {
                this.pressureExcessive = pressureExcessive;
            }

        }
        @UiThread
        void pressureUpdate(PressureHolder pressure);
    }

    interface CameraCoveredListener {
        class CameraCoveredHolder {
            public final boolean cameraCovered;

            public CameraCoveredHolder(boolean cameraCovered) {
                this.cameraCovered = cameraCovered;
            }
        }
        @UiThread
        void cameraUpdate(CameraCoveredHolder camera);

    }

    interface AbnormalHRListener {
        class AbnormalHRHolder {
            public final boolean abnormal;

            public AbnormalHRHolder(boolean abnormal) {
                this.abnormal = abnormal;
            }
        }

        @UiThread
        void abnormalHRUpdate(AbnormalHRHolder abnormal);
    }

    interface DeclineHRListener {
        class DeclineHRHolder {
            public final boolean declining;

            public DeclineHRHolder(boolean declining) {
                this.declining = declining;
            }
        }

        @UiThread
        void declineHRUpdate(DeclineHRHolder decline);
    }

    class BpmCalculator {

        private static final Logger LOG = LoggerFactory.getLogger(BpmCalculator.class);

        private final HeartRateSampleProcessor sampleProcessor = new HeartRateSampleProcessor();

        /**
         * Calculates a simple running average bpm to display to the user for their heart rate. Updates the sample
         * with the calculated bpm, if there is one.
         *
         * @param heartBeatSample heartBeatSample
         */
        public void calculateBpm(HeartBeatSample heartBeatSample) {
            sampleProcessor.addSample(heartBeatSample);
            if (!sampleProcessor.isReadyToProcess()) {
                return;
            }

            // TODO: syoung 04/18/2019 This can take a while to process. Should it be moved to a different thread than the one used to write to the file?
            HeartRateBPM ret = sampleProcessor.processSamples();

            LOG.debug("HeartRateBPM {}", ret);
            heartBeatSample.bpm = (int)ret.getBpm();
            heartBeatSample.confidence = ret.getConfidence();
        }

        public double calculateVo2Max(Sex sex, double age, double startTime) {
            return sampleProcessor.vo2Max(sex, age, startTime);
        }
    }

    class HeartBeatJsonWriter extends JsonArrayDataRecorder
            implements HeartbeatSampleTracker
            .HeartRateUpdateListener {

        private static final Logger LOG = LoggerFactory.getLogger(HeartBeatJsonWriter.class);

        private static final float RED_INTENSITY_FACTOR_THRESHOLD = 2;
        private static final String TIMESTAMP_DATE_KEY = "timestampDate";
        private static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
        private static final String UPTIME_IN_SECONDS_KEY = "uptime";
        private static final String HEART_RATE_KEY = "bpm_camera";
        private static final String RED_KEY = "red";
        private static final String GREEN_KEY = "green";
        private static final String BLUE_KEY = "blue";
        private static final String RED_LEVEL_KEY = "redLevel";
        private static final String IS_COVERING_KEY = "isCoveringLens";

        private static final int INTELLIGENT_START_FRAMES_TO_PASS = 30;

        private final JsonObject mJsonObject = new JsonObject();

        private double timestampZeroReference = -1;
        private double uptimeZeroReference = -1;

        /**
         * Intelligent start is a feature that delays recording until
         * an algorithm determines the user's finger is in front of the camera
         * Disabled by default
         */
        private boolean mEnableIntelligentStart = true;
        private boolean mIntelligentStartPassed = false;
        private int mIntelligentStartCounter = 0;
        private boolean isRecordingStarted = false;

        private final BpmRecorder.BpmUpdateListener mBpmUpdateListener;
        private final BpmRecorder.IntelligentStartUpdateListener mIntelligentStartListener;
        private final BpmRecorder.PressureListener mPressureListener;
        private final BpmRecorder.CameraCoveredListener mCameraListener;
        private final BpmRecorder.AbnormalHRListener mAbnormalListener;
        private final BpmRecorder.DeclineHRListener mDeclineListener;

        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        private final BpmCalculator bpmCalculator;

        public HeartBeatJsonWriter(BpmUpdateListener
                                           mBpmUpdateListener, IntelligentStartUpdateListener
                                           mIntelligentStartListener,
                                   CameraCoveredListener mCameraListener,
                                   PressureListener mPressureListener, AbnormalHRListener mAbnormalListener,
                                   DeclineHRListener mDeclineListener, String identifier,
                                   Step step, File outputDirectory) {
            super(identifier, step, outputDirectory);

            this.mBpmUpdateListener = mBpmUpdateListener;
            this.mIntelligentStartListener = mIntelligentStartListener;
            this.mPressureListener = mPressureListener;
            this.mCameraListener = mCameraListener;
            this.bpmCalculator = new BpmCalculator();
            this.mAbnormalListener = mAbnormalListener;
            this.mDeclineListener = mDeclineListener;
        }

        private int sampleCount = 0;
        private double timestampReference = -1;
        private double firstTimeStamp = -1;
        private Date zeroReferenceDate = null;

        @AnyThread
        @Override
        public void onHeartRateSampleDetected(HeartBeatSample sample) {
            if (firstTimeStamp == -1) {
                firstTimeStamp = sample.timestamp;
            }

            // syoung 11/19/2018 Debug code added to get the sampling rate.
            if (timestampReference == -1) {
                timestampReference = sample.timestamp;
            } else if (sample.timestamp - timestampReference >= 1.0) {
                LOG.debug("preprocessed sample count:{}", sampleCount);
                timestampReference = sample.timestamp;
                sampleCount = 0;
            } else {
                sampleCount++;
            }

            if (sample.timestampDate != null) {
                mJsonObject.addProperty(TIMESTAMP_DATE_KEY,
                        new SimpleDateFormat(FormatHelper.DATE_FORMAT_ISO_8601, new Locale("en", "us", "POSIX"))
                                .format(sample.timestampDate));
                LOG.debug("TIMESTAMP Date key: " + mJsonObject.get(TIMESTAMP_DATE_KEY).getAsString());
                if (zeroReferenceDate == null) {
                    zeroReferenceDate = sample.timestampDate;
                }
            } else {
                mJsonObject.remove(TIMESTAMP_DATE_KEY);
            }

            mJsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY, sample.timestamp);
            mJsonObject.addProperty(UPTIME_IN_SECONDS_KEY, sample.uptime);
            mJsonObject.addProperty(RED_KEY, sample.red);
            mJsonObject.addProperty(GREEN_KEY, sample.green);
            mJsonObject.addProperty(BLUE_KEY, sample.blue);
            mJsonObject.addProperty(RED_LEVEL_KEY, sample.redLevel);
            mJsonObject.addProperty(IS_COVERING_KEY, sample.isCoveringLens());

            mJsonObject.remove(HEART_RATE_KEY);


            if(!sample.isCoveringLens()) {
                mainHandler.post(() ->
                        mCameraListener.cameraUpdate(new
                                BpmRecorder.CameraCoveredListener.CameraCoveredHolder(false)));

                mIntelligentStartCounter = 0;
            } else {
                bpmCalculator.calculateBpm(sample);

                if (sample.bpm > 0) {
                    mJsonObject.addProperty(HEART_RATE_KEY, sample.bpm);
                    if (mBpmUpdateListener != null) {
                        //Sample timestamp is in seconds from start of recording
                        // BpmHolder is expecting a date timestamp in ms
                        long sampleTimestamp = zeroReferenceDate.getTime() + (long)(sample.timestamp * 1000);
                        mainHandler.post(() ->
                                mBpmUpdateListener.bpmUpdate(
                                        new BpmRecorder.BpmUpdateListener.BpmHolder(sample.bpm, sampleTimestamp, sample.confidence)));
                    }
                } else {
                    mJsonObject.remove(HEART_RATE_KEY);
                }

                if (!mEnableIntelligentStart || mIntelligentStartPassed) {
                    mainHandler.post(() ->
                            mCameraListener.cameraUpdate(new
                                    BpmRecorder.CameraCoveredListener.CameraCoveredHolder(true)));
                } else {
                    updateIntelligentStart(sample);
                }
            }
            if (LOG.isTraceEnabled()) {
                LOG.trace("HeartBeatSample : {}", sample);
            }
            writeJsonObjectToFile(mJsonObject);
        }

        private void updateIntelligentStart(HeartBeatSample sample) {
            /*if (mIntelligentStartPassed) {
                return; // we already computed that we could start
            }*/

            // If the red factor is large enough, we update the trigger
            if (sample.isCoveringLens()) {
                mIntelligentStartCounter++;
                if (mIntelligentStartCounter >= INTELLIGENT_START_FRAMES_TO_PASS) {
                    mIntelligentStartPassed = true;
                }
                if (mIntelligentStartListener != null) {
                    float progress = (float) mIntelligentStartCounter / (float)
                            INTELLIGENT_START_FRAMES_TO_PASS;

                    mainHandler.post(() ->
                            mIntelligentStartListener.intelligentStartUpdate(progress,
                                    mIntelligentStartPassed)
                    );
                    mainHandler.post(() ->
                            mCameraListener.cameraUpdate(new
                                    BpmRecorder.CameraCoveredListener.CameraCoveredHolder(true)));

                }
                if(sample.abnormalHR()) {
                    mainHandler.post(() ->
                            mAbnormalListener.abnormalHRUpdate(new
                                    BpmRecorder.AbnormalHRListener.AbnormalHRHolder(true)));
                }
                if(sample.declineHR()) {
                    mainHandler.post(() ->
                            mDeclineListener.declineHRUpdate(new
                                    DeclineHRListener.DeclineHRHolder(true)));
                }

            } else {  // We need thresholds to be passed sequentially otherwise it is restarted
                LOG.warn("Lens isn't covered");
                mainHandler.post(() ->
                        mCameraListener.cameraUpdate(new
                                BpmRecorder.CameraCoveredListener.CameraCoveredHolder(false)));

                mIntelligentStartCounter = 0;

            }
        }

        public double calculateVo2Max(Sex sex, double age) {
            double startTime = firstTimeStamp + 30;
            return bpmCalculator.calculateVo2Max(sex, age, startTime);
        }

        @Override
        public void start(Context context) {
            startJsonDataLogging();
            isRecordingStarted = true;
            mIntelligentStartPassed = false;
            mIntelligentStartCounter = 0;
        }

        @Override
        public void stop() {
            if (isRecordingStarted) {
                isRecordingStarted = false;
                stopJsonDataLogging();
            }
        }

    }
}