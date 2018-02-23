package org.sagebase.crf;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.multidex.MultiDex;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.ResearchStack;
import org.sagebionetworks.bridge.android.BridgeApplication;
import org.sagebionetworks.bridge.android.di.ApplicationModule;
import org.sagebionetworks.bridge.android.di.BridgeServiceModule;
import org.sagebionetworks.bridge.android.manager.BridgeManagerProvider;
import org.sagebionetworks.bridge.android.manager.DaggerBridgeManagerProvider;
import org.sagebionetworks.bridge.researchstack.CrfResearchStack;
import org.sagebionetworks.research.crf.R;

/**
 * Created by TheMDP on 12/9/16.
 */

public class MainApplication extends BridgeApplication {

    private static final String LOG_TAG = MainApplication.class.getCanonicalName();

    // We don't use a pin code for CRF, so just plug in a useless one the app remembers
    public static final String PIN_CODE = "1234";

    CrfResearchStack researchStack;

    @Override
    public void onCreate() {
        super.onCreate();

        researchStack = new CrfResearchStack(this);
        ResearchStack.init(this, researchStack);
    }
    
    @Override
    protected BridgeManagerProvider initBridgeManagerProvider() {
    return DaggerBridgeManagerProvider.builder()
            .applicationModule(new ApplicationModule(this))
            .s3Module(new CrfS3Module())
            .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        // This is needed for android versions < 5.0 or you can extend MultiDexApplication
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // On Android M and above, we can change status bar background and text color
            activity.getWindow().setStatusBarColor(statusBarColor);
            // Do a rough calculation to see if this is a "light" or "dark" color
            // And change the status bar text color to be either white or black
            View decorView = activity.getWindow().getDecorView();
            final int currentFlags = decorView.getSystemUiVisibility();
            if (isColorDark(statusBarColor)) {
                decorView.setSystemUiVisibility(currentFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decorView.setSystemUiVisibility(currentFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // On Android Lollipop and above, we can only change status bar color
            if (!isColorDark(statusBarColor)) {
                Log.d(LOG_TAG, "The requested color is too light and you won't be able to see " +
                        "the white text anyways so setting to default colorPrimaryDark");
                int defaultColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorPrimaryDark, null);
                activity.getWindow().setStatusBarColor(defaultColor);
            } else {
                activity.getWindow().setStatusBarColor(statusBarColor);
            }
        } else {
            // We have no control over status bar color
        }
    }

    private static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) +
                               0.587 *Color.green(color) +
                               0.114 * Color.blue(color)) / 255;
        if (darkness < 0.2f) {
            return false; // It's a light color
        }else{
            return true; // It's a dark color
        }
    }

    public static void mockAuthenticate(Context context) {
        if (StorageAccess.getInstance().hasPinCode(context)) {
            StorageAccess.getInstance().authenticate(context, PIN_CODE);
        } else {
            StorageAccess.getInstance().createPinCode(context, PIN_CODE);
        }
    }
}
