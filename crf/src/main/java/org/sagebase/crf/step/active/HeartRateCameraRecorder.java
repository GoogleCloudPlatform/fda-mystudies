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

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.sagebase.crf.camera.CameraSource;
import org.sagebase.crf.camera.CameraSourcePreview;
import org.sagebase.crf.step.CrfHeartRateStepLayout;

import java.io.File;
import java.io.IOException;

/**
 * Created by TheMDP on 10/19/17.
 */
public class HeartRateCameraRecorder extends Recorder  {
    private static final float SUGGESTED_FPS = 30.0f;
    private static final int SUGGESTED_WIDTH = 640;
    private static final int SUGGESTED_HEIGHT = 480;

    private CameraSourcePreview mCameraSourcePreview;

    private final BpmRecorder.HeartBeatJsonWriter heartBeatJsonWriter;

    public HeartRateCameraRecorder(String identifier, Step step, File outputDirectory, CrfHeartRateStepLayout stepLayout, CameraSourcePreview cameraSourcePreview) {
        super(identifier, step, outputDirectory);
        mCameraSourcePreview = cameraSourcePreview;

        heartBeatJsonWriter = new BpmRecorder.HeartBeatJsonWriter(stepLayout, stepLayout,
                identifier, step,
                outputDirectory );
    }

    private Context getContext() {
        return mCameraSourcePreview.getContext();
    }

    @Override
    public void start(Context context) {
        heartBeatJsonWriter.start(context);

        if (mCameraSourcePreview == null) {
            recordingFailed(new IllegalStateException("HeartRateCameraRecorder requires external setup " +
                    "with a CameraSourcePreview already added to a ViewGroup to function properly"));
            return;
        }

        HeartBeatDetector heartBeatDetector = new HeartBeatDetector(getContext());
        HeartbeatTrackerFactory heartbeatFactory = new HeartbeatTrackerFactory(heartBeatJsonWriter);

        heartBeatDetector.setProcessor(new MultiProcessor.Builder<>(heartbeatFactory).build());

        CameraSource.Builder builder = new CameraSource.Builder(getContext(), heartBeatDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .setRequestedPreviewSize(SUGGESTED_WIDTH, SUGGESTED_HEIGHT)
                .setRequestedFps(SUGGESTED_FPS);

        CameraSource cameraSource = builder.build();
        heartBeatDetector.cameraSource = cameraSource;

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

    private void recordingFailed(Throwable throwable) {
        cancel();
        if (getRecorderListener() != null) {
            getRecorderListener().onFail(this, throwable);
        }
    }

    @Override
    public void setRecorderListener(RecorderListener listener) {
        super.setRecorderListener(listener);
        heartBeatJsonWriter.setRecorderListener(listener);
    }

    @Override
    public void stop() {
        stopCamera();
        heartBeatJsonWriter.stop();
    }

    @Override
    public void cancel() {
        heartBeatJsonWriter.cancel();
        stopCamera();
    }

    private void stopCamera() {
        if (mCameraSourcePreview != null) {
            mCameraSourcePreview.stop();
            mCameraSourcePreview.release();
            mCameraSourcePreview = null;
        }
    }
}
