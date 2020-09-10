/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import lombok.Getter;

@Getter
public enum ParticipantStudyStateStatus {
  ENROLLED("Enrolled"),
  WITHDRAWN("Withdrawn"),
  INPROGRESS("inProgress"),
  YETTOJOIN("yetToJoin"),
  NOTELIGIBLE("notEligible");

  private final String value;

  private ParticipantStudyStateStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
