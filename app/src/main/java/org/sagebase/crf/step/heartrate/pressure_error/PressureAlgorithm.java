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

package org.sagebase.crf.step.heartrate.pressure_error;

import android.graphics.Bitmap;

import org.sagebase.crf.step.active.HeartBeatSample;
import org.sagebase.crf.step.heartrate.OutputStateAlgorithm;

import java.util.ArrayList;

public class PressureAlgorithm implements OutputStateAlgorithm {

    private static int min_length = 10;

    private static ArrayList<Bitmap> previousState;

    public static ArrayList<Bitmap> getPreviousState() {
        if (previousState.size() == 0 || previousState == null) {
            previousState = new ArrayList<>();
        }
        return previousState;
    }

    public static double algorithm(Long timestamp, Bitmap bitmap) {

        ArrayList<Bitmap> state = getPreviousState();

        if (state == null || state.size() < min_length) {
            previousState.add(bitmap);
            return 0.0;
        }

        // This algorithm should be in this file
        /*if(HeartBeatSample.isPressureExcessive()) {
            return 1.0;
        }*/

        else{
            return 0.0;
        }
    }
}
