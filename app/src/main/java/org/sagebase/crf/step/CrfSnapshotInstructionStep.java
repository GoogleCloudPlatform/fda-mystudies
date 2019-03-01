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

package org.sagebase.crf.step;

public class CrfSnapshotInstructionStep extends CrfInstructionStep {

    public String instruction;
    public String stepIdentifier;

    /* Default constructor needed for serialization/deserialization of object */
    public CrfSnapshotInstructionStep() {
        super();
    }

    public CrfSnapshotInstructionStep(String identifier, String title) {
        super(identifier, title, null);
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfSnapshotInstructionStepLayout.class;
    }
}
