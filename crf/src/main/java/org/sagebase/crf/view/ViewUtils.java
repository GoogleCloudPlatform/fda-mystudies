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

package org.sagebase.crf.view;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import org.sagebase.crf.R;

public class ViewUtils {

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
}
