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

import org.sagebase.crf.step.heartrate.abnormal_hr.AbnormalHRSolution;
import org.sagebase.crf.step.heartrate.camera_error.CameraSolution;
import org.sagebase.crf.step.heartrate.confidence_error.ConfidenceSolution;
import org.sagebase.crf.step.heartrate.decline_hr.DeclineSolution;
import org.sagebase.crf.step.heartrate.pressure_error.PressureSolution;

import java.util.*;

public class ErrorResolutionStream {

    public static ErrorType[] errorPriority;

    public ErrorResolutionStream(ErrorType[] errorPriority) {
        this.errorPriority = errorPriority;
    }


    static CameraSolution camera = new CameraSolution();
    static DeclineSolution decline = new DeclineSolution();
    static PressureSolution pressure = new PressureSolution();
    static AbnormalHRSolution abnormal = new AbnormalHRSolution();
    static ConfidenceSolution confidence = new ConfidenceSolution();

    private static HashMap<ErrorType, ErrorSolution> solutions = new HashMap<>();

    public static void resolveErrors(ArrayList<ErrorType> errors) {
        solutions.put(ErrorType.CAMERA_COVERAGE, camera);
        solutions.put(ErrorType.ABNORMAL_HR, abnormal);
        solutions.put(ErrorType.DECLINE_HR, decline);
        solutions.put(ErrorType.LOW_CONFIDENCE, confidence);
        solutions.put(ErrorType.PRESSURE,pressure);

        // Sort errors appropriately
        ArrayList<ErrorType> prominent_errors = new ArrayList<ErrorType>();
        for (ErrorType e: errorPriority) {
            if(errors.contains(e)) {
                prominent_errors.add(e);
            }
        }

        // Resolve errors
        for(ErrorType e: prominent_errors) {
            ErrorSolution sol = solutions.get(e);
            sol.displayUI();
        }
    }
}
