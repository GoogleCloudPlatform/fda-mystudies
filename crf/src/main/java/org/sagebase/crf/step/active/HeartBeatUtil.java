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

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by liujoshua on 2/19/2018.
 *
 * The `HeartBeatUtil` is an internal class used to allow tracking the timestamp for a video feed
 * relative to a stored value marking the system time when the video was started.
 *
 */

class HeartBeatUtil {

    private double timestampZeroReference = -1;
    private double uptimeZeroReference = -1;

    HeartBeatSample getHeartBeatSample(double timestamp, Bitmap bitmap) {

        Date timestampDate = null;
        double zeroBasedTimeStamp = 0;
        if (timestampZeroReference < 0) {
            // set timestamp reference, which timestamps are measured relative to
            timestampZeroReference = timestamp;
            uptimeZeroReference = System.nanoTime() * 1e-9;
            timestampDate = new Date(System.currentTimeMillis());
        } else {
            zeroBasedTimeStamp = timestamp - timestampZeroReference;
        }

        double relativeTimestamp = timestamp - timestampZeroReference;
        double uptime = uptimeZeroReference + relativeTimestamp;

        long r = 0, g = 0, b = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int intArray[] = new int[width * height];
        bitmap.getPixels(intArray, 0, width, 0, 0, width, height);
        for (int i = 0; i < intArray.length; i++) {
            double red = (intArray[i] >> 16) & 0xFF; // Color.red
            double green = (intArray[i] >> 8) & 0xFF; // Color.green
            double blue = (intArray[i] & 0xFF); // Color.blue

            r += red;
            g += green;
            b += blue;
        }

        double meanRed = (r / (double)intArray.length);
        double meanGreen = (g / (double)intArray.length);
        double meanBlue = (b / (double)intArray.length);

        double rDiffSum = 0;
        for (int i = 0; i < intArray.length; i++) {
            double red = (intArray[i] >> 16) & 0xFF; // Color.red
            double rDiff = red - meanRed;
            rDiffSum += (rDiff * rDiff);
        }
        double redSD = Math.sqrt(rDiffSum / (double)(intArray.length - 1));

        HeartBeatSample sample = new HeartBeatSample();
        sample.timestamp = zeroBasedTimeStamp;
        sample.uptime = uptime;
        sample.timestampDate = timestampDate;

        // Per the need to match iOS data (which gives RGB data as 0.0 - 1.0, normalize these values
        sample.red = (meanRed / 255.0);
        sample.green = (meanGreen / 255.0);
        sample.blue = (meanBlue / 255.0);
        sample.redLevel = redSD;


        return sample;
    }
}
