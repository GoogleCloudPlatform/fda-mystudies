package org.sagebase.crf;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.view.WindowManager;

import org.researchstack.skin.ResearchStack;
import org.sagebionetworks.bridge.android.BridgeApplication;
import org.sagebionetworks.bridge.researchstack.CrfResearchStack;

/**
 * Created by TheMDP on 12/9/16.
 */

public class MainApplication extends BridgeApplication {

    CrfResearchStack researchStack;

    @Override
    public void onCreate() {
        super.onCreate();

        researchStack = new CrfResearchStack(this);
        ResearchStack.init(this, researchStack);

        registerActivityLifecycleCallbacks(transparentStatusBar);
    }

    @Override
    protected void attachBaseContext(Context base) {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    // This code below is for transparent status bar only
    private ActivityLifecycleCallbacks transparentStatusBar = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // Sets status bar to transparent
                activity.getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };
}
