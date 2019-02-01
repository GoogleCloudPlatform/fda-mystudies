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

package org.sagebase.crf.step.heartrate.camera_error;

import android.graphics.Bitmap;
import org.sagebase.crf.step.heartrate.OutputStateAlgorithm;
import java.util.ArrayList;

/**
 * Runs an algorithm to determine if the camera is covered.
 */

public class CameraAlgorithm implements OutputStateAlgorithm {

    private static int min_length = 10;

    /**
     * Gets the previous x number of heart beat samples
     *
     * @return A list of previous sample bitmaps
     */
    public static ArrayList<Bitmap> getPreviousState() {

        return null;
    }

    /**
     * Runs the algorithm to determine if the camera is covered.
     *
     * @param timestamp
     * @param bitmap
     * @return A double representing how badly the camera is uncovered.
     */
    public double algorithm(Long timestamp, Bitmap bitmap) {
        ArrayList<Bitmap> state = getPreviousState();

        if (state == null || state.size() < min_length) {
            return 0.0;
        }

        boolean cameraResult = false;
        if (cameraResult) {
            return 1.0;
        }
        return 0.0;
    }
}
