/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.beans.UserBean;
import com.google.cloud.healthcare.fdamystudies.exceptions.BadRequest;

public class Validator {

  private Validator() {}

  public static boolean isValid(UserBean user) {
    if ((user.getEmailId() == null) || (user.getPassword() == null)) {
      throw new BadRequest("emailId or password is blank in request");
    }
    return true;
  }

  public static boolean isValid(String applicationId) {
    if ((applicationId == null)) {
      throw new BadRequest("applicationId is missing in request header");
    }
    return true;
  }
}
