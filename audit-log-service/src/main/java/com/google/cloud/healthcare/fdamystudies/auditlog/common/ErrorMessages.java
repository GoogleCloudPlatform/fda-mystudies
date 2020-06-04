/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum ErrorMessages {
  BAD_REQUEST(
      400, "EC-400", "Bad Request", "Malformed request syntax or invalid request message framing."),

  UNAUTHORIZED(401, "EC-401", "Unauthorized", "Invalid token"),

  APPLICATION_ERROR(
      500,
      "EC-500",
      "Internal Server Error",
      "Sorry, an error has occurred and your request could not be processed. Please try again later.");

  private final int statusCode;
  private final String description;
  private final String errorCode;
  private final String errorType;
}
