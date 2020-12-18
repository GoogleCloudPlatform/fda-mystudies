/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse.Violation;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

public final class RequestParamValidator {

  private RequestParamValidator() {}

  public static ValidationErrorResponse validateRequiredParams(
      MultiValueMap<String, String> paramMap, String... paramNames) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    for (String param : paramNames) {
      if (StringUtils.isEmpty(paramMap.getFirst(param))) {
        error.getViolations().add(new Violation(param, "must not be blank"));
      }
    }
    return error;
  }

  public static ValidationErrorResponse validateRequiredParams(
      HttpServletRequest request, String... paramNames) {
    ValidationErrorResponse error = new ValidationErrorResponse();
    for (String param : paramNames) {
      if (StringUtils.isEmpty(request.getParameter(param))) {
        error.getViolations().add(new Violation(param, "must not be blank"));
      }
    }
    return error;
  }
}
