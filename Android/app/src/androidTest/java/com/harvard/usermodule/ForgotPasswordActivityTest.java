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
public class ForgotPasswordActivityTest {
  private static final String URL = "app://gcp/mystudies/forgotPassword";

  @Rule
  public ActivityTestRule<ForgotPasswordActivity> activityRule =
      new ActivityTestRule<>(ForgotPasswordActivity.class, true, false);

  @Test
  public void launchForgotPasswordActivity() {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(URL))
            .setPackage(InstrumentationRegistry.getInstrumentation().getTargetContext().getPackageName());
    activityRule.launchActivity(intent);
    onView(withId(R.id.activity_sign_in)).check(matches(isDisplayed()));
    onView(withId(R.id.email_text)).check(matches(withText(R.string.forgot_password_text)));
  }
}
