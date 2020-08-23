/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

public enum SiteStatus {
  UNKNOWN(null),
  DEACTIVE(0),
  ACTIVE(1);

  private Integer value;

  private SiteStatus(Integer value) {
    this.value = value;
  }

  public Integer value() {
    return value;
  }

  public static SiteStatus fromValue(Integer value) {
    for (SiteStatus e : SiteStatus.values()) {
      if (e.value == value) {
        return e;
      }
    }
    return null;
  }
}
