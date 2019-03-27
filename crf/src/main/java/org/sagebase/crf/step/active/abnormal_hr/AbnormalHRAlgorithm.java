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

package org.sagebase.crf.step.active.abnormal_hr;

import android.graphics.Bitmap;
import org.sagebase.crf.step.active.OutputStateAlgorithm;
import java.util.ArrayList;

/**
 * Runs an algorithm to determine if the heart beat sample is abnormal.
 */

public class AbnormalHRAlgorithm implements OutputStateAlgorithm {

    /**
     * Gets the previous x number of heart beat samples
     *
     * @return A list of previous sample bitmaps
     */
    public ArrayList<Bitmap> getPreviousState() {
        return null;
    }

    /**
     * Runs the algorithm to determine if a sample is abnormal.
     *
     * @param timestamp
     * @param bitmap
     * @return A double representing how abnormal this sample is.
     */
    public static double algorithm(Long timestamp, Bitmap bitmap) {
        return 0.0;
    }
}
