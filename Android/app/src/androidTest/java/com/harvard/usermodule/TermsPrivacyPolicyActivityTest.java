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
import com.harvard.R;
import org.junit.Rule;
import org.junit.Test;

public class TermsPrivacyPolicyActivityTest {
  private static final String TERMS_URL = "app://gcp/mystudies/terms";
  private static final String PRIVACY_POLICY_URL = "app://gcp/mystudies/privacyPolicy";
  private static final String INTENT_TITLE_TERMS_VALUE = "Terms";
  private static final String INTENT_TITLE_PRIVACY_POLICY_VALUE = "Privacy Policy";
  private static final String INTENT_URL_TERMS_VALUE = "https://developer.android.com/";
  private static final String INTENT_URL_PRIVACY_POLICY_VALUE = "https://developer.android.com/";
  private static final String INTENT_TITLE_KEY = "title";
  private static final String INTENT_URL_KEY = "url";

  @Rule
  public ActivityTestRule<TermsPrivacyPolicyActivity> activityRule =
      new ActivityTestRule<>(TermsPrivacyPolicyActivity.class, true, false);

  @Test
  public void termsTest() {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(TERMS_URL))
            .setPackage(getTargetContext().getPackageName());
    intent.putExtra(INTENT_TITLE_KEY, INTENT_TITLE_TERMS_VALUE);
    intent.putExtra(INTENT_URL_KEY, INTENT_URL_TERMS_VALUE);
    activityRule.launchActivity(intent);
    onView(withId(R.id.webView)).check(matches(isDisplayed()));
    onView(withId(R.id.title)).check(matches(withText(INTENT_TITLE_TERMS_VALUE)));
  }

  @Test
  public void privacyPolicyTest() {
    Intent intent =
        new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL))
            .setPackage(getTargetContext().getPackageName());
    intent.putExtra(INTENT_TITLE_KEY, INTENT_TITLE_PRIVACY_POLICY_VALUE);
    intent.putExtra(INTENT_URL_KEY, INTENT_URL_PRIVACY_POLICY_VALUE);
    activityRule.launchActivity(intent);
    onView(withId(R.id.webView)).check(matches(isDisplayed()));
    onView(withId(R.id.title)).check(matches(withText(INTENT_TITLE_PRIVACY_POLICY_VALUE)));
  }
}
