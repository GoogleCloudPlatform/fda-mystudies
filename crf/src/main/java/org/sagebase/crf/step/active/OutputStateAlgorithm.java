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

package org.sagebase.crf.step.active;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * An interface that implements an error state algorithm with supplemental functions
 */
public interface OutputStateAlgorithm {

    /**
     * Get the previous x states to be used in the algorithm
     * @return  a list of previous states's bitmaps
     */
    static ArrayList<Bitmap> getPreviousState(){return null;};

    /**
     *  Return a double reporting how likely it is that an error is present. The closer to 0, the
     *  sless likely there is an error, the closer to 1, the more likely
     */
    static double algorithm(Long timestamp, Bitmap bitmap){
        return 0.0;
    }

}
