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
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.researchstack.backbone.DataProvider;
import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.onboarding.OnboardingTaskType;
import org.researchstack.backbone.utils.ObservableUtils;
import org.researchstack.skin.ResearchStack;
import org.researchstack.skin.ui.MainActivity;
import org.sagebionetworks.research.crf.R;

import rx.functions.Action1;

/**
 * Created by TheMDP on 10/19/17.
 * Zeplin 0.0 CRF - Intro
 */

public class OverviewActivity extends AppCompatActivity {

    // We don't use a pin code for CRF, so just plug in a useless one the app remembers
    private static final String PIN_CODE = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mockAuthenticate();
        DataProvider.getInstance()
                .initialize(this)
                .compose(ObservableUtils.applyDefault())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object response) {
                        if (DataProvider.getInstance().isSignedIn(OverviewActivity.this)) {
                            launchMainActivity();
                            finish();
                        }
                    }
                });
    }

    // We don't use a pin code for CRF, so just plug in a useless one the app remembers
    protected void mockAuthenticate() {
        if (StorageAccess.getInstance().hasPinCode(this)) {
            StorageAccess.getInstance().authenticate(this, PIN_CODE);
        } else {
            StorageAccess.getInstance().createPinCode(this, PIN_CODE);
        }
    }

    protected void launchOnboardingActivity() {
        mockAuthenticate();
        ResearchStack.getInstance().getOnboardingManager().launchOnboarding(
                OnboardingTaskType.LOGIN, this);
    }

    protected void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void goForwardClicked(View v) {
        launchOnboardingActivity();
    }
}
