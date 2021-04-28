/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class MyStudiesUserRegUtil {

  private static XLogger logger = XLoggerFactory.getXLogger(MyStudiesUserRegUtil.class);

  static String email = "";

  public enum ErrorCodes {
    INVALID_INPUT("INVALID_INPUT"),
    UNKNOWN("UNKNOWN"),
    STATUS_100("100"), // OK
    STATUS_101("101"), // Invalid Authentication (authKey is not valid).
    STATUS_102("102"), // Invalid Inputs (If any of the input parameter is missing).
    STATUS_103("103"), // No Data available.
    STATUS_104("104"), // Unknown Error
    STATUS_105("105"), // If there is no data to update.
    STATUS_106("106"), // Failed to generate token.
    STATUS_107("107"), // Failed to complete transaction.
    EC_500("500"), // Internal Server Error
    SESSION_EXPIRED_MSG("Session expired."),

    INVALID_CLIENTID_OR_SECRET_KEY("Invalid Client Id or Secret Key"),
    ACCESS_TOKEN_OR_USER_ID_INVALID("Access token or User id is invalid"),
    INVALID_AUTH_CODE("INVALID_AUTH_CODE"),
    INVALID_EMAIL("Invalid Email"),
    ACCOUNT_DEACTIVATE_ERROR_MSG("Your account has been deactivated"),
    INVALID_USERNAME_PASSWORD_MSG("Invalid username or password"),
    EMAIL_EXISTS("This email has already been used. Please try with different email address."),
    INVALID_INPUT_ERROR_MSG("Invalid input."),
    INACTIVE("INACTIVE"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    JOINED("Joined"),
    COMPLETED("Completed"),
    STARTED("Started"),
    PAUSED("Paused"),
    PROFILE("profile"),
    SETTINGS("settings"),
    MESSAGE("message"),
    PARTICIPANTINFO("participantInfo"),
    STUDIES("studies"),
    ACTIVITIES("activities"),
    NO_DATA_AVAILABLE("No data available"),
    CONSENT_VERSION_REQUIRED("Consent version is required"),
    CONNECTION_ERROR_MSG("Oops, something went wrong. Please try again after sometime"),
    EMAIL_NOT_EXISTS("Email Doesn't Exists"),
    RESEND_EMAIL_NOT_EXISTS("Email Doesn't Exists OR Email Already Verified"),
    USER_NOT_EXISTS("User Doesn't Exists"),
    FAILURE_TO_SENT_MAIL("Oops, something went wrong. Failed to send Email"),
    OLD_PASSWORD_NOT_EXISTS("Invalid old password"),
    OLD_PASSWORD_AND_NEW_PASSWORD_NOT_SAME("Current Password and New Password cannot be same"),
    NEW_PASSWORD_NOT_SAME_LAST_PASSWORD(
        "New Password should not be the same as the last 10 passwords."),
    USER_ALREADY_VERIFIED("User already verified"),
    INVALID_CODE("Invalid code"),
    CODE_EXPIRED("Code Expired"),
    YET_TO_JOIN("yetToJoin"),
    IN_PROGRESS("inProgress"),
    STUDY_LEVEL("ST"),
    GATEWAY_LEVEL("GT"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCOUNT_LOCKED("As a security measure, this account has been locked for 15 minutes."),
    ACCOUNT_TEMP_LOCKED("As a security measure, this account has been locked for 15 minutes."),
    EMAIL_VERIFICATION_SUCCESS_MESSAGE(
        "Thanks, your email has been successfully verified!"
            + " You can now proceed to completing the sign up process on the mobile app."),
    EMAIL_NOT_VERIFIED(
        "Your account is not verified. Please verify your account by clicking on verification link"
            + " which has been sent to your registered email. If not received, would you like to"
            + " resend verification link?"),
    STUDY("Study"),
    GATEWAY("Gateway"),
    DEVICE_ANDROID("android"),
    DEVICE_IOS("ios"),
    INVALID_REFRESHTOKEN("Invalid refresh token."),
    APP_EXIST_NOTEXIST(
        "You already have a valid account for this app."
            + " Please directly sign in using the same email and associated password."),
    ORG_NOTEXIST(
        "Sorry, this email is already in use for platform-powered app(s)"
            + " belonging to another organization. "
            + "Please use another email to sign up for this app."),
    LOGIN_ORG_NOTEXIST(
        "Sorry, this account is in use for platform-powered app(s)"
            + " belonging to another organization."
            + " Please sign up with a different email and try again."),
    UNAUTHORIZED("Unauthorized");

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
      if (status.equalsIgnoreCase(ErrorCodes.STATUS_101.getValue())) {
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
      logger.info("MyStudiesUserRegUtil - getFailureResponse() :: ERROR ", e);
    }
  }

  public static Date getCurrentUtilDateTime() {
    Date date = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow = new SimpleDateFormat("yyyy-MM-dd").format(currentDate.getTime());
    try {
      date = new SimpleDateFormat("yyyy-MM-dd").parse(dateNow);
    } catch (Exception e) {
      logger.info("MyStudiesUserRegUtil - getCurrentUtilDateTime() :: ERROR ", e);
    }
    return date;
  }

  public static LocalDateTime getCurrentDateTime() {
    logger.entry("MyStudiesUserRegUtil - getCurrentDateTime() :: starts");
    LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
    logger.exit("MyStudiesUserRegUtil - getCurrentDateTime() :: ends");
    return currentDateTime;
  }

  public static String getCurrentDateTime(String pattern) {
    logger.entry("MyStudiesUserRegUtil - getCurrentDateTime() :: starts");
    String currentDateTime = "";
    try {
      LocalDateTime now = LocalDateTime.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
      DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(pattern);
      currentDateTime = now.format(datePattern);
    } catch (Exception e) {
      logger.error("ERROR: MyStudiesUserRegUtil - getCurrentDateTime()", e);
    }
    logger.exit("MyStudiesUserRegUtil - getCurrentDateTime() :: ends");
    return currentDateTime;
  }
}
