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
import android.graphics.Bitmap;
import android.support.v8.renderscript.RenderScript;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import org.sagebase.crf.camera.CameraSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.sagebase.crf.step.active.HeartBeatUtil.getHeartBeatSample;
import static org.sagebase.crf.step.active.ImageUtils.toBitmap;

/**
 * Created by TheMDP on 10/17/17.
 */

public class HeartBeatDetector extends Detector<HeartBeatSample> {
    private static final Logger LOG = LoggerFactory.getLogger(HeartBeatDetector.class);
    
    public final Context context;
    private final RenderScript rs;

    public CameraSource cameraSource;

    private int frameCounter = 0;
    private long fpsStartTime = -1;

    public enum TYPE {
        GREEN, RED
    };
    private static TYPE currentType = TYPE.GREEN;
    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = -1;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public HeartBeatDetector(Context applicationContext) {
        this.context = applicationContext;
        rs = RenderScript.create(context);
    }
    @Override
    public SparseArray<HeartBeatSample> detect(Frame frame) {

        long now = System.currentTimeMillis();
        if (startTime < 0) {
            startTime = now;
        }
        if (fpsStartTime < 0) {
            fpsStartTime = now;
        }
        frameCounter++;
        if (frameCounter % 100 == 0) {
            LOG.debug("FPS: {}", 1000f / ((System.currentTimeMillis() - fpsStartTime) / frameCounter));
            frameCounter = 0;
            fpsStartTime = -1;
        }

        if (cameraSource == null || cameraSource.getPreviewSize() == null) {
            LOG.error("Camera source or preview size is null");
            return new SparseArray<>();
        }

        //long startTime = System.currentTimeMillis();
        int w = cameraSource.getPreviewSize().getWidth();
        int h = cameraSource.getPreviewSize().getHeight();

        Bitmap bitmap = toBitmap(rs, frame, w, h);

        // Takes about 10 ms on Samsung Galaxy S6
        //Log.d("Timing", "rgb " + (System.currentTimeMillis() - startTime));
        //startTime = System.currentTimeMillis();

        HeartBeatSample sample = getHeartBeatSample(now, bitmap);

        //Log.d("RGB", "" + sample.r + ", " + sample.g + ", " + sample.b);
        SparseArray<HeartBeatSample> samples = new SparseArray<>();
        samples.append(0, sample);

        // Takes about 4 ms on Samsung Galaxy S6
        //Log.d("Timing", "avg rgb " + (System.currentTimeMillis() - startTime));
        //startTime = System.currentTimeMillis();

        //Log.d("Timing", "Hue " + (System.currentTimeMillis() - startTime));
        //Log.d("Hue", "" + sample.h);

        float bpm = calculateBpm((int)(sample.r * 255));
        if (bpm > 0) {
            //Log.d("BPM", "" + bpm);
            sample.bpm = Math.round(bpm);
        }

        return samples;
    }

    /**
     * Calculates a simple running average bpm to display to the user for their heart rate
     * @param imgAvg the average red color in the algorithm, can also be average hue
     * @return
     */
    int calculateBpm(int imgAvg) {
        int averageArrayAvg = 0;
        int averageArrayCnt = 0;
        for (int i = 0; i < averageArray.length; i++) {
            if (averageArray[i] > 0) {
                averageArrayAvg += averageArray[i];
                averageArrayCnt++;
            }
        }

        int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
        TYPE newType = currentType;
        if (imgAvg < rollingAverage) {
            newType = TYPE.RED;
            if (newType != currentType) {
                beats++;
                // Log.d(TAG, "BEAT!! beats="+beats);
            }
        } else if (imgAvg > rollingAverage) {
            newType = TYPE.GREEN;
        }

        if (averageIndex == averageArraySize) averageIndex = 0;
        averageArray[averageIndex] = imgAvg;
        averageIndex++;

        // Transitioned from one state to another to the same
        if (newType != currentType) {
            currentType = newType;
        }

        long endTime = System.currentTimeMillis();
        double totalTimeInSecs = (endTime - startTime) / 1000d;
        if (totalTimeInSecs >= 10) {
            double bps = (beats / totalTimeInSecs);
            int dpm = (int) (bps * 60d);
            if (dpm < 30 || dpm > 180) {
                startTime = System.currentTimeMillis();
                beats = 0;
                return -1;
            }

            // Log.d(TAG,
            // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

            if (beatsIndex == beatsArraySize) beatsIndex = 0;
            beatsArray[beatsIndex] = dpm;
            beatsIndex++;

            int beatsArrayAvg = 0;
            int beatsArrayCnt = 0;
            for (int i = 0; i < beatsArray.length; i++) {
                if (beatsArray[i] > 0) {
                    beatsArrayAvg += beatsArray[i];
                    beatsArrayCnt++;
                }
            }
            int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
            startTime = System.currentTimeMillis();
            beats = 0;
            return beatsAvg;
        }
        return -1;
    }
}
