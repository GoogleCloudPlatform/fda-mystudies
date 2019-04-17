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

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by TheMDP on 10/11/17.
 * Directly modeled after iOS' ORKHeartRateCameraRecorder class
 * Currently this code does not correctly output a BPM that is valid
 * It will need additional investigation and comparison to iOS to work
 * However, I included the code just in case it needs referenced at some point
 */

public class HeartRateManager {

    static final boolean USE_LIGHT_KEY_ALGORITHM = true;

    static final long ORKHeartRateSampleRate = 1000;
    static final int ORKHeartRateFramesPerSecond = 30;
    static final int ORKHeartRateSettleSeconds = 3;
    static final int ORKHeartRateWindowSeconds = 10;
    static final int ORKHeartRateMinFrameCount = (ORKHeartRateSettleSeconds + ORKHeartRateWindowSeconds) * ORKHeartRateFramesPerSecond;

    Long startTime = null;
    float averageFps;
    int fpsCounter = 0;
    int totalSampleCount = 0;

    static final String ORKColorHueKey = "hue";
    static final String ORKColorSaturationKey = "saturation";
    static final String ORKColorBrightnessKey = "brightness";
    static final String ORKColorRedKey = "red";
    static final String ORKColorGreenKey = "green";
    static final String ORKColorBlueKey = "blue";
    static final String ORKCameraHeartRateKey = "bpm_camera";
    static final String ORKWatchHeartRateKey = "bpm_watch";

    List<Float> dataPointsHue = new ArrayList<Float>();

    public double calculateRunningBpm(float hue) {

        if (startTime == null) {
            startTime = System.currentTimeMillis();
        }
        totalSampleCount++;
        long timeElapsed = System.currentTimeMillis() - startTime;
        if ((timeElapsed / 1000) > fpsCounter) {
            averageFps = 1000f / (timeElapsed / totalSampleCount);
            Log.d("FPS", String.format("New Avf FPS = %f", averageFps));
            fpsCounter++;
        }

        if (hue > 0) {
            // Since the hue for blood is in the red zone which cross the degrees point,
            // offset that value by 180.
            float offsetHue = hue + 180.0f;
            if (offsetHue > 360.0) {
                offsetHue -= 360.0;
            }
            dataPointsHue.add(offsetHue);
            //Log.d("HeartRate", "Hue added " + offsetHue);
        } else {
            dataPointsHue.clear();
            //Log.d("HeartRate", "Hue unknown, cleared");
        }

        // Only send UI updates once a second and only after min window of time
        return calculateBPM();
    }

    private double calculateBPM() {
        // If a valid heart rate cannot be calculated then return -1 as an invalid marker
        if (dataPointsHue.size() < ORKHeartRateMinFrameCount) {
            Log.e("HeartRate", "hue data points are not longer than heart rate min frame count");
            return -1;
        }

        // Get a window of data points that is the length of the window we are looking at
        int len = (int)(ORKHeartRateWindowSeconds * ORKHeartRateFramesPerSecond);
        List<Float> dataPoints = new ArrayList<>(dataPointsHue.subList(dataPointsHue.size() - len, dataPointsHue.size()));

        // If we have enough data points then remove from beginning
        if (dataPointsHue.size() > ORKHeartRateMinFrameCount) {
            int len2 = dataPointsHue.size() - ORKHeartRateMinFrameCount;
            dataPointsHue.removeAll(dataPointsHue.subList(0, len2));
        }

        for (int i = 0; i < dataPoints.size(); i++) {
            Log.d("Data Points", String.format("%05f", dataPoints.get(i)));
        }

        // If the heart rate calculated is too low, then it isn't valid
        double heartRate = calculateBPMWithData(dataPoints);
        return heartRate >= 40 ? heartRate : -1;
    }

    double calculateBPMWithData(List<Float> dataPoints) {
        List<Float> bandpassFilteredItems = butterworthBandpassFilter(dataPoints);
        List<Float> smoothedBandpassItems = medianSmoothing(bandpassFilteredItems);
        int peak = medianPeak(smoothedBandpassItems);
        Log.d("HeartRate", "Median Peak " + peak);
        int heartRate = (int)((60 * ORKHeartRateFramesPerSecond) / peak);
        Log.d("HeartRate", "Heart Rate " + heartRate);
        return heartRate;
    }

    int medianPeak(List<Float> inputData) {
        List<Float> peaks = new ArrayList<Float>();
        int count = 4;
        for (int i = 3; i < inputData.size() - 3; i++) {
            if (inputData.get(i) > 0 &&
                    inputData.get(i).doubleValue() > inputData.get(i-1).doubleValue() &&
                    inputData.get(i).doubleValue() > inputData.get(i-2).doubleValue() &&
                    inputData.get(i).doubleValue() > inputData.get(i-3).doubleValue() &&
                    inputData.get(i).doubleValue() >= inputData.get(i+1).doubleValue() &&
                    inputData.get(i).doubleValue() >= inputData.get(i+2).doubleValue() &&
                    inputData.get(i).doubleValue() >= inputData.get(i+3).doubleValue()) {
                peaks.add((float)count);
                i += 3;
                count = 3;
            }
            count++;
        }

        if (peaks.size() == 0) {
            return -1;
        }

        peaks.set(0, (float)(peaks.get(0).intValue() + count + 3));
        Collections.sort(peaks);
        int medianPeak = peaks.get(peaks.size() * 2 / 3).intValue();
        return (medianPeak != 0) ? medianPeak : -1;
    }

    float[] getHSVFrom(float r, float g, float b) {
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float delta = max - min;

        if (Math.round(delta * 1000.0f) == 0) {
            return null;
        }

        float hue;
        if (r == max) {
            hue = (g - b) / delta;
        } else if (g == max) {
            hue = 2 + (b - r) / delta;
        } else {
            hue = 4 + (r - g) / delta;
        }
        hue *= 60;
        if (hue < 0) {
            hue += 360;
        }

        return new float[] { hue, (delta / max), max };
    }

    List<Float> butterworthBandpassFilter(List<Float> inputData) {
        final int NZEROS = 8;
        final int NPOLES = 8;
        double[] xv = new double[NZEROS+1], yv = new double[NPOLES+1];

        // http://www-users.cs.york.ac.uk/~fisher/cgi-bin/mkfscript
        // Butterworth Bandpass filter
        // 4th order
        // sample rate - varies between possible camera frequencies. Either 30, 60, 120, or 240 FPS
        // corner1 freq. = 0.667 Hz (assuming a minimum heart rate of 40 bpm, 40 beats/60 seconds = 0.667 Hz)
        // corner2 freq. = 4.167 Hz (assuming a maximum heart rate of 250 bpm, 250 beats/60 secods = 4.167 Hz)
        // Bandpass filter was chosen because it removes frequency noise outside of our target range (both higher and lower)
        double dGain = 1.232232910e+02;

        List<Float> outputData = new ArrayList<>();
        for (Float data : inputData) {
            double input = data.doubleValue();

            xv[0] = xv[1]; xv[1] = xv[2]; xv[2] = xv[3]; xv[3] = xv[4]; xv[4] = xv[5]; xv[5] = xv[6]; xv[6] = xv[7]; xv[7] = xv[8];
            xv[8] = input / dGain;
            yv[0] = yv[1]; yv[1] = yv[2]; yv[2] = yv[3]; yv[3] = yv[4]; yv[4] = yv[5]; yv[5] = yv[6]; yv[6] = yv[7]; yv[7] = yv[8];
            yv[8] = (xv[0] + xv[8]) - 4 * (xv[2] + xv[6]) + 6 * xv[4]
                    + ( -0.1397436053 * yv[0]) + (  1.2948188815 * yv[1])
                    + ( -5.4070037946 * yv[2]) + ( 13.2683981280 * yv[3])
                    + (-20.9442560520 * yv[4]) + ( 21.7932169160 * yv[5])
                    + (-14.5817197500 * yv[6]) + (  5.7161939252 * yv[7]);
            outputData.add((float) yv[8]);
        }
        return outputData;
    }


// Smoothed data helps remove outliers that may be caused by interference, finger movement or pressure changes.
// This will only help with small interference changes.
// This also helps keep the data more consistent.
//- (NSArray *)medianSmoothing:(NSArray *)inputData {
//        NSMutableArray *newData = [[NSMutableArray alloc] init];
//
//        for (int i = 0; i < inputData.count; i++) {
//            if (i == 0 ||
//                    i == 1 ||
//                    i == 2 ||
//                    i == inputData.count - 1 ||
//                    i == inputData.count - 2 ||
//                    i == inputData.count - 3)        {
//            [newData addObject:inputData[i]];
//            } else {
//                NSArray *items = [@[
//                inputData[i-2],
//                        inputData[i-1],
//                        inputData[i],
//                        inputData[i+1],
//                        inputData[i+2],
//                                ] sortedArrayUsingDescriptors:@[[NSSortDescriptor sortDescriptorWithKey:@"self" ascending:YES]]];
//
//            [newData addObject:items[2]];
//            }
//        }
//
//        return newData;
//    }

    List<Float> medianSmoothing(List<Float> inputData) {
        List<Float> newData = new ArrayList<Float>();
        List<Float> items = new ArrayList<Float>();
        for (int i = 0; i < inputData.size(); i++) {
            if (i == 0 ||
                    i == 1 ||
                    i == 2 ||
                    i == inputData.size() - 1 ||
                    i == inputData.size() - 2 ||
                    i == inputData.size() - 3) {
                newData.add(inputData.get(i));
            } else {
                items.clear();
                items.add(inputData.get(i-2));
                items.add(inputData.get(i));
                items.add(inputData.get(i+1));
                items.add(inputData.get(i+2));
                Collections.sort(items);
                newData.add(items.get(2));
            }
        }

        return newData;
    }
}
