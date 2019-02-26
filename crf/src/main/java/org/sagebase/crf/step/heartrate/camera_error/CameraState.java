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
import org.sagebase.crf.step.heartrate.StateDetection;

/**
 * Encompasses state detection for if the camera is covered.
 */
public class CameraState implements StateDetection {

    public CameraState() {

    }

    /**
     * Runs the camera covered algorithm
     * @param timestamp
     * @param bitmap
     * @return boolean representing whether this is an issue
     */
    public static boolean containsIssue(Long timestamp, Bitmap bitmap) {
        return true;
    }
}
