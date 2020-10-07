/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.matchers;

import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class HasLastName extends TypeSafeMatcher<UserDetailsEntity> {

  private String lastName;

  private HasLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  protected boolean matchesSafely(UserDetailsEntity userDetails) {
    return userDetails.getLastName() == this.lastName;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("has last name " + lastName);
  }

  public static Matcher<UserDetailsEntity> hasLastName(String lastName) {
    return new HasLastName(lastName);
  }
}
