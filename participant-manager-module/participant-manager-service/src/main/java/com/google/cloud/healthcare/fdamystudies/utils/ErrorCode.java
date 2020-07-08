/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public enum ErrorCode {
  EC_200(200, "OK"),
  EC_61(61, "User does not exists."),
  EC_93(93, "User not Active"),
  EC_812(812, "Missing required argument"),
  EC_813(813, "Provided argument value is invalid"),
  EC_882(882, "You do not have permission to view or add or update locations"),
  EC_883(883, "customId already exists"),
  EC_861(861, "User does not exist"),
  EC_500(500, "Internal Server Error");

  private final int code;
  private final String errorMessage;

  private ErrorCode(int code, String errorMessage) {
    this.code = code;
    this.errorMessage = errorMessage;
  }

  public int code() {
    return this.code;
  }

  public String errorMessage() {
    return this.errorMessage;
  }

  public static ErrorCode fromErrorMessage(String errorMessage) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.errorMessage.equals(errorMessage)) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + errorMessage + "]");
  }
}
