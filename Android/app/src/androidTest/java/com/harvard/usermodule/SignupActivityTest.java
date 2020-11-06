/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.usermodule;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import android.content.Intent;
import android.net.Uri;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.harvard.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SignupActivityTest {
  private static final String URL = "app://gcp/mystudies/signup";

  @Rule
  public ActivityTestRule<SignupActivity> activityRule =
      new ActivityTestRule<>(SignupActivity.class, true, false);

  @Test
  public void launchForgotPasswordActivity() {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(URL))
            .setPackage(getTargetContext().getPackageName());
    activityRule.launchActivity(intent);
    onView(withId(R.id.activity_signup)).check(matches(isDisplayed()));
    onView(withId(R.id.email_label)).check(matches(withText(R.string.email_id)));
    onView(withId(R.id.password_label)).check(matches(withText(R.string.password)));
    onView(withId(R.id.confirm_password_label)).check(matches(withText(R.string.confirm_password)));
  }
}
