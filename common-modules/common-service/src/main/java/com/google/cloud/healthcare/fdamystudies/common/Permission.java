/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public enum Permission {
  VIEW(0),
  EDIT(1),
  NO_PERMISSION(null);

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
