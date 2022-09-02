/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public enum UserAccountStatus {
  ACTIVE(0),
  PENDING_CONFIRMATION(1),
  ACCOUNT_LOCKED(2),
  PASSWORD_RESET(3),
  DEACTIVATED(4),
  SUPER_ADMIN(5);

  private int status = 1;

  private UserAccountStatus(int status) {
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  public static UserAccountStatus valueOf(int status) {
    for (UserAccountStatus type : UserAccountStatus.values()) {
      if (status == type.getStatus()) {
        return type;
      }
    }
    return null;
  }
}
