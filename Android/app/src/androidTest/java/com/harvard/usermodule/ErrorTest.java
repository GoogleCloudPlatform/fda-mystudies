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
import com.harvard.AppConfig;
import com.harvard.BuildConfig;
import com.harvard.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ErrorTest {
  private static final String URL = "app://gcp/mystudies/error";

  @Rule
  public ActivityTestRule<Error> activityRule = new ActivityTestRule<>(Error.class, true, false);

  @Test
  public void errorTest() {
    AppConfig.AppType = BuildConfig.APP_TYPE;
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(URL))
            .setPackage(getTargetContext().getPackageName());
    activityRule.launchActivity(intent);
    onView(withId(R.id.activity_gateway)).check(matches(isDisplayed()));
    onView(withId(R.id.mNewUserButton)).check(matches(withText(R.string.new_user)));
    onView(withId(R.id.mSignInButton)).check(matches(withText(R.string.sign_in_btn)));
  }
}
