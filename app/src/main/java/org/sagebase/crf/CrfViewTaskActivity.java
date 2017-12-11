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

package org.sagebase.crf;

import android.support.annotation.ColorRes;

import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 11/30/17.
 * The CrfViewTaskActivity can be simplified by just re-using the CrfActiveTaskActivity
 * If CrfActiveTaskActivity is causing problems, this was originally done this way
 * to re-use the Toolbar functionality
 */

public class CrfViewTaskActivity extends CrfActiveTaskActivity {
    @Override
    public int getContentViewId() {
        return R.layout.crf_view_task_activity;
    }

    @Override
    protected @ColorRes int defaultToolbarTintColor() {
        return R.color.salmon;
    }

    @Override
    protected @ColorRes int defaultStepProgressColor() {
        return R.color.darkGrayText;
    }
}
