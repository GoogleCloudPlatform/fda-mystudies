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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.backbone.ResearchStack;
import org.sagebionetworks.research.crf.R;

import rx.functions.Action1;

/**
 * Created by TheMDP on 10/19/17.
 * Zeplin 0.0 CRF - Intro
 */

public class CrfOverviewActivity extends AppCompatActivity {

    protected Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        nextButton = findViewById(R.id.button_go_forward);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nextButton.setEnabled(true);
        int color = ResourcesCompat.getColor(getResources(), R.color.deepGreen, null);
        MainApplication.setStatusBarColor(this, color);
        MainApplication.mockAuthenticate(this);
        DataProvider.getInstance()
                .initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object response) {
                        if (DataProvider.getInstance().isSignedIn(CrfOverviewActivity.this)) {
                            launchMainActivity();
                            finish();
                        }
                    }
                });
    }

    protected void launchOnboardingActivity() {
        MainApplication.mockAuthenticate(this);
        ResearchStack.getInstance().getOnboardingManager().launchOnboarding(
                OnboardingTaskType.LOGIN, this);
    }

    protected void launchMainActivity() {
        startActivity(new Intent(this, CrfMainActivity.class));
        finish();
    }

    public void goForwardClicked(View v) {
        nextButton.setEnabled(false);
        launchOnboardingActivity();
    }
}
