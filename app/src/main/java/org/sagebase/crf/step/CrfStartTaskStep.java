/*
 *    Copyright 2017 Sage Bionetworks
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

import com.google.gson.annotations.SerializedName;

import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 10/25/17.
 */

public class CrfStartTaskStep extends CrfInstructionStep {
    /**
     * If true, the remind me later button will show, if false it will hide, defaults to true
     */
    public boolean remindMeLater = true;

    /**
     * The filename of the html that will show with more info on the task
     */
    public String infoHtmlFilename;

    /**
     * A String representing a color resource
     */
    public String textColorRes;

    /* Default constructor needed for serialization/deserialization of object */
    public CrfStartTaskStep() {
        super();
    }

    public CrfStartTaskStep(String identifier, String title, String detailText) {
        super(identifier, title, detailText);
    }

    @Override
    public Class getStepLayoutClass() {
        return CrfStartTaskStepLayout.class;
    }
}
