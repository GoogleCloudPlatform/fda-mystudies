/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

public enum ErrorCode {
  EC_30(30, "Profile Updated successfully"),

  EC_33(33, "Leave cancelled successfully"),

  EC_34(34, " Unable to update user profile"),

  EC_41(41, "There was an issue with the provided data for the request."),

  EC_61(61, "User does not exists."),

  EC_200(200, "Success"),

  EC_400(400, "Bad Request."),

  EC_401(401, "You are not authorized to access this information"),

  EC_403(403, "You are forbidden to access this information."),

  EC_500(500, "Internal Server Error."),

  EC_701(701, "The argument that you provided is a required argument but it is blank or null."),

  EC_711(711, "Invalid Input"),

  EC_718(718, "UNAUTHORIZED"),

  EC_719(719, "Invalid clientId or secretKey");

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
