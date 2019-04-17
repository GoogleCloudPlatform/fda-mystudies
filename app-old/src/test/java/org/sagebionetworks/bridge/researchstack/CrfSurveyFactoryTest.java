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

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.researchstack.backbone.ResourceManager;
import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.factory.SurveyFactory;
import org.researchstack.backbone.step.Step;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by TheMDP on 11/17/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ResourcePathManager.class, ResourceManager.class})
public class CrfSurveyFactoryTest {

    private SurveyFactoryHelper helper;

    @Before
    public void setUp() throws Exception
    {
        helper = new SurveyFactoryHelper();

        // All of this, along with the @PrepareForTest and @RunWith above, is needed
        // to mock the resource manager to load resources from the directory src/test/resources
        PowerMockito.mockStatic(ResourcePathManager.class);
        PowerMockito.mockStatic(ResourceManager.class);
        MockResourceManager mockManager = new MockResourceManager();
        PowerMockito.when(ResourceManager.getInstance()).thenReturn(mockManager);
        mockManager.addReference(ResourcePathManager.Resource.TYPE_JSON, "background_survey");
    }

    private String getJsonResource(String resourceName) {
        ResourcePathManager.Resource resource = ResourceManager.getInstance().getResource(resourceName);
        return ResourceManager.getResourceAsString(helper.mockContext, resourceName);
    }

    @Test
    public void backgroundSurveyParsing_test() {
        Type listType = new TypeToken<List<SurveyItem>>() {}.getType();
        String eligibilityJson = getJsonResource("background_survey");
        List<SurveyItem> surveyItemList = helper.gson.fromJson(eligibilityJson, listType);

        SurveyFactory factory = new SurveyFactory();
        List<Step> stepList = factory.createSurveySteps(helper.mockContext, surveyItemList);

        assertNotNull(stepList);
        assertTrue(stepList.size() > 0);
    }
}
