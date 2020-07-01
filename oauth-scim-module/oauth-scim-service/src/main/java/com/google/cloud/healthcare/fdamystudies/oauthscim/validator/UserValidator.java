/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.validator;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CHANGE_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD;

import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse.Violation;
import org.apache.commons.lang3.StringUtils;

public final class UserValidator {

  private static final String MUST_NOT_BE_BLANK = "must not be blank";

  private UserValidator() {}

  public static ValidationErrorResponse validate(UpdateUserRequest request) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    if (StringUtils.isEmpty(request.getAction())) {
      error.getViolations().add(new Violation("action", MUST_NOT_BE_BLANK));
      return error;
    }

    // validate fields required for change password action
    if (CHANGE_PASSWORD.equalsIgnoreCase(request.getAction())) {
      if (StringUtils.isEmpty(request.getCurrentPassword())) {
        error.getViolations().add(new Violation("currentPassword", MUST_NOT_BE_BLANK));
      }

      if (StringUtils.isEmpty(request.getNewPassword())) {
        error.getViolations().add(new Violation("newPassword", MUST_NOT_BE_BLANK));
      }

      if (StringUtils.equalsIgnoreCase(request.getCurrentPassword(), request.getNewPassword())) {
        error
            .getViolations()
            .add(
                new Violation(
                    "newPassword",
                    "Your new password must be different from your previous password."));
      }
    }

    // validate fields required for forgot password action
    if (FORGOT_PASSWORD.equalsIgnoreCase(request.getAction())) {
      if (StringUtils.isEmpty(request.getEmail())) {
        error.getViolations().add(new Violation("email", MUST_NOT_BE_BLANK));
      }
      if (StringUtils.isEmpty(request.getAppId())) {
        error.getViolations().add(new Violation("appId", MUST_NOT_BE_BLANK));
      }
      if (StringUtils.isEmpty(request.getOrgId())) {
        error.getViolations().add(new Violation("orgId", MUST_NOT_BE_BLANK));
      }
    }

    return error;
  }
}
