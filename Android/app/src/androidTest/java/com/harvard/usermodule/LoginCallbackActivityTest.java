/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.net.Uri;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.harvard.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginCallbackActivityTest {
  private static final String URL = "app://gcp/mystudies/activation?email=test@grr.la";

  @Rule
  public ActivityTestRule<LoginCallbackActivity> activityRule =
      new ActivityTestRule<>(LoginCallbackActivity.class, true, false);

  @Test
  public void loginCallbackhandleIntent() {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(URL))
            .setPackage(InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName());
    activityRule.launchActivity(intent);
    onView(withId(R.id.activity_verification_step)).check(matches(isDisplayed()));
    onView(withId(R.id.verification_steps_label))
        .check(matches(withText(R.string.verification_step)));
    onView(withId(R.id.resend)).check(matches(withText(R.string.resend)));
  }
}
