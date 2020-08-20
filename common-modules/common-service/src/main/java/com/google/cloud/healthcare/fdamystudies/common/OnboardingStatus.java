/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.Getter;

@Getter
public enum OnboardingStatus {
  INVITED("I", "Invited"),
  ENROLLED("E", "Enrolled"),
  NEW("N", "New"),
  DISABLED("D", "Disabled"),
  ALL("A", "All");

  private String code;

  private String status;

  private OnboardingStatus(String code, String status) {
    this.code = code;
    this.status = status;
  }

  public static OnboardingStatus fromCode(String code) {
    for (OnboardingStatus e : OnboardingStatus.values()) {
      if (e.code.equals(code)) {
        return e;
      }
    }
    return null;
  }
}
