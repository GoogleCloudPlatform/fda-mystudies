/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

public class CustomFirebaseAnalytics {

    private static volatile CustomFirebaseAnalytics instance;
    private static FirebaseAnalytics firebaseAnalytics;

    public static CustomFirebaseAnalytics getInstance(Context context) {
        if (instance == null) {
            synchronized (CustomFirebaseAnalytics.class) {
                if (instance == null){
                    instance = new CustomFirebaseAnalytics();
                }
            }
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return instance;
    }

    public static class Param {

        public static final String BUTTON_CLICK_REASON = "button_click_reason";

        protected Param() {
        }
    }

    public static class Event {

        public static final String ADD_BUTTON_CLICK = "add_button_click";

        protected Event() {
        }
    }

    public void logEvent(String eventName, Bundle eventProperties) {
        firebaseAnalytics.logEvent(eventName, eventProperties);
    }
}
