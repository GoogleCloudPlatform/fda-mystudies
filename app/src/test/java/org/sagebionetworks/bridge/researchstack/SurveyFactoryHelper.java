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

import android.content.Context;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.researchstack.backbone.R;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSectionAdapter;
import org.researchstack.backbone.model.survey.SurveyItem;
import org.researchstack.backbone.model.survey.SurveyItemAdapter;
import org.researchstack.backbone.model.taskitem.TaskItem;
import org.researchstack.backbone.model.taskitem.TaskItemAdapter;
import org.researchstack.backbone.onboarding.OnboardingManager;

/**
 * Created by TheMDP on 1/6/17.
 *
 * The SurveyFactorHelper can be used to help the SurveyFactory, ConsentDocumentFactory, etc
 * It provides a mock Context with resources in it, along with
 */

public class SurveyFactoryHelper {
    public Gson gson;
    @Mock public Context mockContext;
    @Mock private Resources mockResources;

    static final String PRIVACY_TITLE = "Privacy";
    static final String PRIVACY_LEARN_MORE = "Learn more about how your privacy and identity are protected";

    public SurveyFactoryHelper() {
        mockContext = Mockito.mock(Context.class);

        Mockito.when(mockContext.getString(R.string.rsb_yes))       .thenReturn("Yes");
        Mockito.when(mockContext.getString(R.string.rsb_no))        .thenReturn("No");
        Mockito.when(mockContext.getString(R.string.rsb_not_sure))  .thenReturn("Not sure");

        Mockito.when(mockContext.getString(R.string.rsb_gender_male))   .thenReturn("Male");
        Mockito.when(mockContext.getString(R.string.rsb_gender_female)) .thenReturn("Female");

        mockResources = Mockito.mock(Resources.class);
        Mockito.when(mockResources.getInteger(R.integer.rsb_sensor_frequency_default)).thenReturn(100);
        Mockito.when(mockContext.getResources()).thenReturn(mockResources);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(TaskItem.class, new TaskItemAdapter());
        builder.registerTypeAdapter(SurveyItem.class, new SurveyItemAdapter());
        builder.registerTypeAdapter(ConsentSection.class, new ConsentSectionAdapter(new OnboardingManager.AdapterContextProvider() {
            @Override
            public Context getContext() {
                return mockContext;
            }
        }));
        gson = builder.create();
    }
}
