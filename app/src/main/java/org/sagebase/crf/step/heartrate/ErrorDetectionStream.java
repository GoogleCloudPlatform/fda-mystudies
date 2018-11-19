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

package org.sagebase.crf.step.heartrate;

import org.sagebase.crf.step.heartrate.camera_error.CameraError;
import org.sagebase.crf.step.heartrate.abnormal_hr.AbnormalHRError;
import org.sagebase.crf.step.heartrate.decline_hr.DeclineHRError;
import org.sagebase.crf.step.heartrate.pressure_error.PressureError;
import org.sagebase.crf.step.heartrate.confidence_error.ConfidenceError;

public class ErrorDetectionStream {
    // Possible errors to cycle through
    public ErrorType[] possible_errors = new ErrorType[]{ErrorType.CAMERA_COVERAGE,
            ErrorType.PRESSURE, ErrorType.ABNORMAL_HR, ErrorType.LOW_CONFIDENCE,
            ErrorType.DECLINE_HR};

    // Error to act on
    public ErrorType most_prominent_error;

    CameraError camera = new CameraError();
    DeclineHRError decline = new DeclineHRError();
    PressureError pressure = new PressureError();
    AbnormalHRError abnormal = new AbnormalHRError();
    ConfidenceError confidence = new ConfidenceError();

    // An array of the last 10 heart rate samples


    // A method to go through all of the error types
    public void detectError() {
        for (ErrorType e: possible_errors) {

        }
    }
}
