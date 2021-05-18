/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class ResponseServerUtil {
  private static final XLogger logger =
      XLoggerFactory.getXLogger(ResponseServerUtil.class.getName());

  public enum ErrorCodes {
    INVALID_INPUT("INVALID_INPUT"),
    INVALID_INPUT_ERROR_MSG("Invalid input."),
    STATUS_101("101"),
    STATUS_102("102"), // Unknown Error;
    STATUS_103("103"), // No Data available.
    STATUS_104("104"), // Invalid Inputs (If any of the input parameter is missing).
    STATUS_128("128"), // Invalid UserId
    STATUS_129("129"), // Client Id is missing
    STATUS_130("130"), // Secret Key is missing
    STATUS_131("131"), // No Study Found
    SESSION_EXPIRED_MSG("Session expired."),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");
    private final String value;

    ErrorCodes(final String newValue) {
      value = newValue;
    }

    public String getValue() {
      return value;
    }
  }

  public static void getFailureResponse(
      String status, String title, String message, HttpServletResponse response) {
    try {
      response.setHeader("status", status);
      response.setHeader("title", title);
      response.setHeader("StatusMessage", message);

      if (status.equalsIgnoreCase(ErrorCodes.STATUS_104.getValue())) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
      }

      if (status.equalsIgnoreCase(ErrorCodes.STATUS_102.getValue())) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
      }
      if (status.equalsIgnoreCase(ErrorCodes.STATUS_101.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_128.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_131.getValue())) {
        if (message.equalsIgnoreCase(ErrorCodes.SESSION_EXPIRED_MSG.getValue())) {
          response.sendError(
              HttpServletResponse.SC_UNAUTHORIZED, ErrorCodes.SESSION_EXPIRED_MSG.getValue());
        } else {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, message);
        }
      }

      if (status.equalsIgnoreCase(ErrorCodes.STATUS_103.getValue())) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, message);
      }

    } catch (Exception e) {
      logger.info("ResponseServerUtil - getFailureResponse() :: ERROR ", e);
    }
  }

  public static String getHashedValue(String secretToHash) {
    logger.entry("begin getHashedValue()");
    String generatedHash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(secretToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.info("ResponseServerUtil getHashedValue() - error() ", e);
    }
    logger.exit("getHashedValue() - ends");
    return generatedHash;
  }
}
