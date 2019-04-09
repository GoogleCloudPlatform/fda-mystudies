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

package org.sagebase.old;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.ResearchStack;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.sagebionetworks.research.crf.R;

;

public class HrOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int color = ResourcesCompat.getColor(getResources(), R.color.deepGreen, null);
        MainApplication.setStatusBarColor(this, color);
        MainApplication.mockAuthenticate(this);
        if (DataProvider.getInstance().isSignedIn(this)) {
            launchMainActivity();
            finish();
        } else {
            launchOnboardingActivity();
        }
    }

    protected void launchOnboardingActivity() {
        MainApplication.mockAuthenticate(this);
        ResearchStack.getInstance().getOnboardingManager().launchOnboarding(
                OnboardingTaskType.LOGIN, this);
    }

    protected void launchMainActivity() {
        startActivity(new Intent(this, HrMainActivity.class));
        finish();
    }


}
