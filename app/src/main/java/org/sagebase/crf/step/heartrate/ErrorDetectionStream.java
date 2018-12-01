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
import java.util.*;

public class ErrorDetectionStream {

    // Error to act on
    public ArrayList<ErrorType> most_prominent_errors = new ArrayList<>();

    CameraError camera = new CameraError();
    DeclineHRError decline = new DeclineHRError();
    PressureError pressure = new PressureError();
    AbnormalHRError abnormal = new AbnormalHRError();
    ConfidenceError confidence = new ConfidenceError();

    // An array of the last 10 heart rate samples

    ErrorDetection[] possible_errors =
            new ErrorDetection[]{camera, decline, pressure, abnormal, confidence};

    // A method to go through all of the error types
    public void detectErrors() {
        for(ErrorDetection e: possible_errors) {
            if(e.hasError()) {
                most_prominent_errors.add(e.getErrorType());
            }
        }
    }

    // Act on the errors that we detected
    public void resolveErrors() {
        detectErrors();

        if(most_prominent_errors.isEmpty()) {
            System.out.println("No errors detected");
        }
        else {
            ErrorResolutionStream.resolveErrors(most_prominent_errors);
        }

    }

}
