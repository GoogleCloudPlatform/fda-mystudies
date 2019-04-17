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

import android.os.Handler;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;

/**
 * Created by TheMDP on 10/17/17.
 */

public class HeartbeatSampleTracker extends Tracker<HeartBeatSample> {

    private Handler mMainHandler;
    private HeartRateUpdateListener mHeartRateUpdateListener;
    @MainThread
    public void setHeartRateUpdateListener(HeartRateUpdateListener listener) {
        mMainHandler = new Handler();
        mHeartRateUpdateListener = listener;
    }

    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * HeartRateUpdateListener interface method onHeartRateDetected.
     */
    public interface HeartRateUpdateListener {
        @AnyThread
        void onHeartRateSampleDetected(HeartBeatSample sample);
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    @Override
    public void onNewItem(int id, final HeartBeatSample item) {
        if (mMainHandler == null) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mHeartRateUpdateListener != null) {
                    mHeartRateUpdateListener.onHeartRateSampleDetected(item);
                }
            }
        });
    }
    
    /**
     * Update the position/characteristics of the item within the overlay.
     */
    @Override
    public void onUpdate(Detector.Detections<HeartBeatSample> detectionResults, final HeartBeatSample item) {
        if (mMainHandler == null) {
            return;
        }
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mHeartRateUpdateListener != null) {
                    mHeartRateUpdateListener.onHeartRateSampleDetected(item);
                }
            }
        });
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    @Override
    public void onMissing(Detector.Detections<HeartBeatSample> detectionResults) {
        int i = 0;
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    @Override
    public void onDone() {
        int i = 0;
    }
}
