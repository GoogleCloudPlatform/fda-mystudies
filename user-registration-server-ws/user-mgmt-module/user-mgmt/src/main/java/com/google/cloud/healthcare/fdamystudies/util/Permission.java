/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

public enum Permission {
  NO_PERMISSION(null),
  READ_VIEW(0),
  READ_EDIT(1);

  private Integer value;

  private Permission(Integer value) {
    this.value = value;
  }

  public Integer value() {
    return value;
  }

  public static Permission fromValue(Integer value) {
    for (Permission e : Permission.values()) {
      if (e.value == value) {
        return e;
      }
    }
    return NO_PERMISSION;
  }
}
