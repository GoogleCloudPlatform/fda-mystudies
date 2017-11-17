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

package org.sagebionetworks.bridge.researchstack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.Test;
import org.researchstack.backbone.model.survey.SurveyItem;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CrfSurveyItemAdapterTest {
    @Test
    public void getCustomClass() {
        CrfSurveyItemAdapter adapter = new CrfSurveyItemAdapter();

        // JSON doesn't matter for this class
        JsonElement json = new JsonObject();

        // Survey adapter is data driven. Make sure the class output matches the map.
        for (Map.Entry<String, Class<? extends SurveyItem>> oneEntry :
                CrfSurveyItemAdapter.TYPE_TO_CLASS.entrySet()) {
            assertEquals(oneEntry.getValue(), adapter.getCustomClass(oneEntry.getKey(), json));
        }
    }
}
