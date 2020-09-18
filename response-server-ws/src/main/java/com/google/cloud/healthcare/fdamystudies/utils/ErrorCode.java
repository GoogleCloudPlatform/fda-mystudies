/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

public enum ErrorCode {
  EC_110(110, "Activity state has been updated successfully"),

  EC_111(96, "Failed to update activity state"),

  EC_200(200, "OK"),

  EC_201(201, "No Content"),

  EC_302(302, "Redirect"),

  EC_304(304, "Not Modified"),

  EC_400(400, "Bad Request"),

  EC_401(401, "You are not authorized to access this information"),

  EC_403(403, "Permission Denied/Forbidden"),

  EC_404(404, "Information not found"),

  EC_500(500, "Internal Server Error"),

  EC_700(700, "A required argument is missing."),

  EC_701(701, "The argument that you provided is a required argument but it is blank or null."),

  EC_702(702, "Exception when saving StudyMetadata to datastore."),

  EC_703(703, "Could not generate a unique participant ID"),

  EC_704(704, "Exception when processing the response data. Response data storage failed."),

  EC_705(705, "Could not retrieve study activity metadata information to process response."),

  EC_706(706, "Could not validate the participant."),

  EC_707(707, "Could not process and store the response data."),

  EC_708(708, "Could not get the storedresponse data for participant."),

  EC_709(709, "Something went wrong. Please try again after sometime"),

  EC_710(710, "Missing Required Parameter"),

  EC_711(711, "Invalid Input"),

  EC_712(712, "Could not delete/update response data for withdrawn participant."),

  EC_713(713, "Could not get the activity state data for participant."),

  EC_714(714, "Could not update the activity state data for participant."),

  EC_715(715, "Could not retrieve participant study info data to process response."),

  EC_716(
      716,
      "Could not process and store the response data as the "
          + "participant has withdrawn from the study."),

  EC_717(
      717,
      "Deleted/Updated response data for withdrawn participant,"
          + " but could not delete participant activity state data."),

  EC_718(718, "UNAUTHORIZED"),
  EC_719(719, "Invalid clientId or secretKey"),
  EC_720(720, "Invalid activity state status value .");

  private final int code;
  private final String errorMessage;

  /**
   * @param code the error code value
   * @param errorMessage the error message details
   */
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

  /**
   * @param code the error code value
   * @return the {@link ErrorCode} details
   */
  public static ErrorCode fromCode(int code) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.code == code) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + code + "]");
  }

  /**
   * @param errorMessage the error message details
   * @return the {@link ErrorCode} details
   */
  public static ErrorCode fromErrorMessage(String errorMessage) {
    for (ErrorCode ec : ErrorCode.values()) {
      if (ec.errorMessage == errorMessage) {
        return ec;
      }
    }
    throw new IllegalArgumentException("No matching constant for [" + errorMessage + "]");
  }
}
