/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public enum DataSharingStatus {
  UNDEFINED("Undefined"),
  NOT_APPLICABLE("Not Applicable"),
  NOT_PERMITTED("Not Permitted"),
  PERMITTED("Permitted");

  private String value;

  private DataSharingStatus(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static DataSharingStatus fromValue(String value) {
    for (DataSharingStatus e : DataSharingStatus.values()) {
      if (e.value.equals(value)) {
        return e;
      }
    }
    return null;
  }
}
