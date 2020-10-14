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
  IN_PROGRESS("inProgress"),
  ENROLLED("Enrolled"),
  YET_TO_JOIN("yetToJoin"),
  WITHDRAWN("Withdrawn");

  private String code;

  private String status;

  private EnrollmentStatus(String status) {
    this.status = status;
  }
}
