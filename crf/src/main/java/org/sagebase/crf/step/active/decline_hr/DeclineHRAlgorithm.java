/*
 *    Copyright 2019 Sage Bionetworks
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

package org.sagebase.crf.step.active.decline_hr;

import android.graphics.Bitmap;
import org.sagebase.crf.step.active.OutputStateAlgorithm;
import java.util.ArrayList;

/**
 * Runs an algorithm to determine if the heart rate is declining.
 */

public class DeclineHRAlgorithm implements OutputStateAlgorithm {
    public static ArrayList<Bitmap> previousState;

    private static double decline_threshold = 0.1;
    private static int min_length = 10;

    /**
     * Gets the previous x number of heart beat samples
     *
     * @return A list of previous sample bitmaps
     */
    public static ArrayList<Bitmap> getPreviousState() {
        if (previousState.size() == 0 || previousState == null) {
            previousState = new ArrayList<>();
        }
        return previousState;
    }

    /**
     * Runs the algorithm to determine if the heart rate is declining.
     *
     * @param timestamp
     * @param bitmap
     * @return A double representing how much the heart rate is declining.
     */
    public static double algorithm(Long timestamp, Bitmap bitmap) {
        ArrayList<Bitmap> state = getPreviousState();

        if (state == null || state.size() < min_length) {
            previousState.add(bitmap);
            return 0.0;
        }
        // Look at the difference between the first and last elements
        /*int first = previousState[0];
        int last = previousState[previousState.length - 1];
        if (last - first == (first * decline_threshold)) {
            return 0.8;
        }

        boolean decreasing = false;
        int backups = 3;
        for (int i = 1; i < previousState.length; i++) {
            if (previousState[i-1] > previousState[i]) {
                decreasing = true;
            }
            else if (previousState[i-1] < previousState[i]) {
                if (decreasing) {
                    backups -= 1;
                }
                if (backups == 0) {
                    decreasing = false;
                }
            }
        }

        if (decreasing) {
            return 1.0;
        }*/

        return 0.0;
    }
}
