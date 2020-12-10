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
public enum EnrollmentStatus {
  ENROLLED("Enrolled", "Enrolled"),
  YET_TO_ENROLL("yetToEnroll", "Yet to enroll"),
  WITHDRAWN("Withdrawn", "Withdrawn"),
  NOT_ELIGIBLE("notEligible", "Not eligible");

  private String status;

  private String displayValue;

  private EnrollmentStatus(String status, String displayValue) {
    this.status = status;
    this.displayValue = displayValue;
  }
}
