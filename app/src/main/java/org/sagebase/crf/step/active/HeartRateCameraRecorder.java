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

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.gson.JsonObject;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.JsonArrayDataRecorder;

import org.sagebase.crf.camera.CameraSource;
import org.sagebase.crf.camera.CameraSourcePreview;

import java.io.File;
import java.io.IOException;

/**
 * Created by TheMDP on 10/19/17.
 */

public class HeartRateCameraRecorder extends JsonArrayDataRecorder {

    private static final String TIMESTAMP_IN_SECONDS_KEY = "timestamp";
    private static final String HEART_RATE_KEY   = "bpm_camera";
    private static final String HUE_KEY          = "hue";
    private static final String SATURATION_KEY   = "saturation";
    private static final String BRIGHTNESS_KEY   = "brightness";
    private static final String RED_KEY          = "red";
    private static final String GREEN_KEY        = "green";
    private static final String BLUE_KEY         = "blue";

    private static final float SUGGESTED_FPS = 30.0f;
    private static final int SUGGESTED_WIDTH = 640;
    private static final int SUGGESTED_HEIGHT = 480;

    private JsonObject mJsonObject = new JsonObject();

    private CameraSourcePreview mCameraSourcePreview;

    /**
     * Intelligent start is a feature that delays recording until
     * an algorithm determines the user's finger is in front of the camera
     * Disabled by default
     */
    private boolean mEnableIntelligentStart = false;
    public void setEnableIntelligentStart(boolean enable) {
        mEnableIntelligentStart = enable;
    }
    private boolean mIntelligentStartPassed = false;
    private int mIntelligentStartCounter = 0;
    private static final int INTELLIGENT_START_FRAMES_TO_PASS = 10;

    private BpmUpdateListener mBpmUpdateListener;
    public void setBpmUpdateListener(BpmUpdateListener listener) {
        mBpmUpdateListener = listener;
    }

    private IntelligentStartUpdateListener mIntelligentStartListener;
    public void setIntelligentStartListener(IntelligentStartUpdateListener listener) {
        mIntelligentStartListener = listener;
    }

    public HeartRateCameraRecorder(String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
    }

    public HeartRateCameraRecorder(CameraSourcePreview cameraSourcePreview,
                                   String identifier, Step step, File outputDirectory) {
        super(identifier, step, outputDirectory);
        mCameraSourcePreview = cameraSourcePreview;
    }

    private Context getContext() {
        return mCameraSourcePreview.getContext();
    }

    @Override
    public void start(Context context) {

        startJsonDataLogging();

        mIntelligentStartPassed = false;
        mIntelligentStartCounter = 0;

        if (mCameraSourcePreview == null) {
            recordingFailed(new IllegalStateException("HeartRateCameraRecorder requires external setup " +
                    "with a CameraSourcePreview already added to a ViewGroup to function properly"));
            return;
        }

        HeartBeatDetector heartBeatDetector = new HeartBeatDetector();
        HeartbeatTrackerFactory heartbeatFactory = new HeartbeatTrackerFactory(
                new HeartbeatSampleTracker.HeartRateUpdateListener() {
            @Override
            public void onHeartRateSampleDetected(HeartBeatSample sample) {
                mJsonObject.addProperty(TIMESTAMP_IN_SECONDS_KEY,  sample.t/1000F);
                mJsonObject.addProperty(HUE_KEY,        sample.h);
                mJsonObject.addProperty(SATURATION_KEY, sample.s);
                mJsonObject.addProperty(BRIGHTNESS_KEY, sample.v);
                mJsonObject.addProperty(RED_KEY,        sample.r);
                mJsonObject.addProperty(GREEN_KEY,      sample.g);
                mJsonObject.addProperty(BLUE_KEY,       sample.b);

                if (sample.bpm > 0) {
                    mJsonObject.addProperty(HEART_RATE_KEY, sample.bpm);
                    if (mBpmUpdateListener != null) {
                        mBpmUpdateListener.bpmUpdate(
                                new BpmUpdateListener.BpmHolder(sample.bpm, sample.t));
                    }
                } else {
                    mJsonObject.remove(HEART_RATE_KEY);
                }

                if (!mEnableIntelligentStart || mIntelligentStartPassed) {
                    writeJsonObjectToFile(mJsonObject);
                } else {
                    updateIntelligentStart(sample);
                }
            }
        });
        heartBeatDetector.setProcessor(new MultiProcessor.Builder<>(heartbeatFactory).build());

        CameraSource.Builder builder = new CameraSource.Builder(getContext(), heartBeatDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setRequestedPreviewSize(SUGGESTED_WIDTH, SUGGESTED_HEIGHT)
                .setRequestedFps(SUGGESTED_FPS);

        CameraSource cameraSource = builder.build();
        heartBeatDetector.cameraSource = cameraSource;
        heartBeatDetector.context = getContext();

        // Check that the device has play services available, which is required for the camera code
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (code != ConnectionResult.SUCCESS) {
            String errorString = GoogleApiAvailability.getInstance().getErrorString(code);
            recordingFailed(new IllegalStateException(errorString));
            return;
        }

        try {
            mCameraSourcePreview.start(cameraSource);
        } catch (IOException | SecurityException e) {
            Log.e("HeartRateCameraRecorder", "Unable to start camera source.", e);
            cameraSource.release();
            recordingFailed(e);
        }
    }

    private void updateIntelligentStart(HeartBeatSample sample) {
        if (mIntelligentStartPassed) {
            return; // we already computed that we could start
        }

        // When a finger is placed in front of the camera with the flash on,
        // the camera image will be almost entirely red, so use a simple lenient algorithm for this
        int redIntensityFactorThreshold = 20;
        long greenBlueSum = ((sample.g == 0) ? 1 : sample.g) + ((sample.b == 0) ? 1 : sample.b);
        long redFactor = sample.r / greenBlueSum;

        // If the red factor is large enough, we update the trigger
        if (redFactor >= redIntensityFactorThreshold) {
            mIntelligentStartCounter++;
            if (mIntelligentStartCounter >= INTELLIGENT_START_FRAMES_TO_PASS) {
                mIntelligentStartPassed = true;
            }
            if (mIntelligentStartListener != null) {
                float progress = (float)mIntelligentStartCounter / (float)INTELLIGENT_START_FRAMES_TO_PASS;
                mIntelligentStartListener.intelligentStartUpdate(progress, mIntelligentStartPassed);
            }

        } else {  // We need thresholds to be passed sequentially otherwise it is restarted
            mIntelligentStartCounter = 0;
        }
    }

    private void recordingFailed(Throwable throwable) {
        cancel();
        if (getRecorderListener() != null) {
            getRecorderListener().onFail(this, throwable);
        }
    }

    @Override
    public void stop() {
        stopCamera();
        stopJsonDataLogging();
    }

    @Override
    public void cancel() {
        super.cancel();
        stopCamera();
    }

    private void stopCamera() {
        if (mCameraSourcePreview != null) {
            mCameraSourcePreview.stop();
            mCameraSourcePreview.release();
            mCameraSourcePreview = null;
        }
    }

    public interface BpmUpdateListener {
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

    public interface IntelligentStartUpdateListener {
        /**
         * @param progress value from 0.0 to 1.0 communicating the progress to being ready
         * @param ready true if the camera is now collecting data, false otherwise
         */
        void intelligentStartUpdate(float progress, boolean ready);
    }
}
