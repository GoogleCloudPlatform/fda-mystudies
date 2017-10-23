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
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;

import org.sagebase.crf.camera.CameraSource;

/**
 * Created by TheMDP on 10/17/17.
 */

public class HeartBeatDetector extends Detector<HeartBeatSample> {

    public Context context;
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
            Log.d("FPS", String.format("%f", 1000f / ((System.currentTimeMillis() - fpsStartTime) / frameCounter)));
            frameCounter = 0;
            fpsStartTime = -1;
        }

        if (cameraSource == null || cameraSource.getPreviewSize() == null) {
            Log.e("HeartBeatDetector", "Camera source or preview size is null");
            return new SparseArray<>();
        }

        HeartBeatSample sample = new HeartBeatSample();
        sample.t = now;

        long startTime = System.currentTimeMillis();
        int w = cameraSource.getPreviewSize().getWidth();
        int h = cameraSource.getPreviewSize().getHeight();

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs)).setX(frame.getGrayscaleImageData().array().length);
        Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);

        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(w).setY(h);
        Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

        in.copyFrom(frame.getGrayscaleImageData().array());

        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        out.copyTo(bitmap);

        yuvToRgbIntrinsic.destroy();
        rs.destroy();

        // Takes about 10 ms on Samsung Galaxy S6
        //Log.d("Timing", "rgb " + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();

        // Get all the pixels of the bitmap as an array
        long r = 0, g = 0, b = 0;
        int intArray[] = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < intArray.length; i++) {
            r += (intArray[i] >> 16) & 0xFF; // Color.red
            g += (intArray[i] >> 8) & 0xFF; // Color.green
            b += (intArray[i] & 0xFF); // Color.blue
        }

        sample.r = r / intArray.length;
        sample.g = g / intArray.length;
        sample.b = b / intArray.length;

        //Log.d("RGB", "" + sample.r + ", " + sample.g + ", " + sample.b);
        SparseArray<HeartBeatSample> samples = new SparseArray<>();
        samples.append(0, sample);

        // Takes about 4 ms on Samsung Galaxy S6
        //Log.d("Timing", "avg rgb " + (System.currentTimeMillis() - startTime));
        startTime = System.currentTimeMillis();



        //Log.d("Timing", "Hue " + (System.currentTimeMillis() - startTime));
        //Log.d("Hue", "" + sample.h);

        int bpm = calculateBpm((int)sample.r);
        if (bpm > 0) {
            //Log.d("BPM", "" + bpm);
            sample.bpm = bpm;
        }

        return samples;
    }

    void fillHsv(HeartBeatSample sample) {
        float min = Math.min(sample.r, Math.min(sample.g, sample.b));
        float max = Math.max(sample.r, Math.max(sample.g, sample.b));
        float delta = max - min;

        if (Math.round(delta * 1000.0f) == 0) {
            return;
        }

        float hue;
        if (sample.r == max) {
            hue = (float)(sample.g - sample.b) / delta;
        } else if (sample.g == max) {
            hue = 2f + (float)(sample.b - sample.r) / delta;
        } else {
            hue = 4f + (float)(sample.r - sample.g) / delta;
        }
        hue *= 60f;
        if (hue < 0) {
            hue += 360f;
        }

        sample.h = hue;
        sample.s = (delta / max);
        sample.v = max;
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
