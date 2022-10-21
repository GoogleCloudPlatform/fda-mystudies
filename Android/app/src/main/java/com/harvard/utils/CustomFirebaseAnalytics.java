/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
        if (instance == null) {
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
