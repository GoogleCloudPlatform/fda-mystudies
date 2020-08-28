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
public enum UserStatus {
  DEACTIVATED(0, "Deactivated"),
  ACTIVE(1, "Active"),
  INVITED(2, "Invited");

  private Integer value;

  private String description;

  private UserStatus(Integer value, String description) {
    this.value = value;
    this.description = description;
  }

  public static UserStatus fromValue(Integer value) {
    for (UserStatus e : UserStatus.values()) {
      if (e.value.equals(value)) {
        return e;
      }
    }
    return null;
  }
}
