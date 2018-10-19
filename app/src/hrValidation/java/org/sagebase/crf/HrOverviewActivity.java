package org.sagebase.crf;

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
