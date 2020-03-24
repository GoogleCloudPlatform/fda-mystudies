/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public enum ErrorCode {
  EC_92(92, "Invalid credentials / User not Active"),

  EC_102(102, "Your Account has been locked."),

  EC_109(109, "Invalid Inputs"),

  EC_113(113, "Unknown Error Found"),

  EC_116(116, "Invalid User Id"),

  EC_118(118, "System error found"),

  EC_131(131, "Invalid Client Id or Secret Key"),

  EC_134(134, "Invalid User Id"),

  EC_136(136, "Invalid credentials"),

  EC_138(138, "Unable to genarate auth code"),

  EC_139(139, "No Data available."),

  EC_140(140, "Code Expired."), // Code Expired

  EC_200(200, "OK"),

  EC_400(400, "Bad Request."),

  EC_401(401, "You are not authorized to access this information"),

  EC_403(403, "You are forbidden to access this information."),

  EC_404(404, "Information not found."),

  EC_500(500, "Internal Server Error."),

  EC_1001(1001, "Your Session has been expired."),

  EC_1003(1003, "Invalid refresh token.");

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

  public static ErrorCode fromCode(int code) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.code == code) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + code + "]");
  }

  public static ErrorCode fromErrorMessage(String errorMessage) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.errorMessage == errorMessage) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + errorMessage + "]");
  }
}
