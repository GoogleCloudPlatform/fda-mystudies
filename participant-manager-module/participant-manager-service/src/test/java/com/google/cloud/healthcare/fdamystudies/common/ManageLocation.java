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
public enum ManageLocation {
  DENY(1),
  ALLOW(2);

  private int value;

  private ManageLocation(int value) {
    this.value = value;
  }

  public static ManageLocation valueOf(int status) {
    for (ManageLocation type : ManageLocation.values()) {
      if (status == type.getValue()) {
        return type;
      }
    }
    return null;
  }
}
