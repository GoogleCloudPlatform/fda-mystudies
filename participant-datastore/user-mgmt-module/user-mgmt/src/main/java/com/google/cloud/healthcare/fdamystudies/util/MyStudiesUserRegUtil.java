/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDao;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

public class MyStudiesUserRegUtil {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(MyStudiesUserRegUtil.class.getName());
  @Autowired UserProfileManagementDao userProfileManagementDao;

  static String Email = "";

  public enum ErrorCodes {
    INVALID_INPUT("INVALID_INPUT"),
    UNKNOWN("UNKNOWN"),
    STATUS_200("200"), // OK
    STATUS_101("101"), // Invalid Authentication (authKey is not valid).
    STATUS_102("102"), // Invalid Inputs (If any of the input parameter is missing).
    STATUS_103("103"), // No Data available.
    STATUS_104("104"), // Unknown Error
    STATUS_105("105"), // If there is no data to update.
    STATUS_106("106"), // Failed to generate token.
    STATUS_107("107"), // Failed to complete transaction.
    STATUS_112("112"), // User is already registered
    STATUS_114("114"), // Email Id is missing
    STATUS_119("119"), // Password is missing
    STATUS_120("120"), // Connection error msg
    STATUS_126("126"), // appId is missing

    STATUS_128("128"), // User Form is empty
    STATUS_129("129"), // Client Id is missing
    STATUS_130("130"), // Secret Key is missing

    EC_500("500"), // Internal Server Error

    INVALID_EMAILID("Enter a valid emailId"),

    CLIENT_ID_MISSING("Client id is missing"),
    SECRET_KEY_MISSING("Secret key is missing"),

    APP_ID_MISSING("appId is missing"),
    ACCESS_TOKEN_MISSING("Access token is missing"),
    CODE_MISSING("Code is missing"),

    EMAIL_ID_MISSING("Email Id is required in body"),
    PASSWORD_MISSING("Password is required in body"),

    USER_FORM_EMPTY("User form is empty"),
    CONNECTION_ERROR_MSG("Oops, something went wrong. Please try again after sometime"),
    VERIFICATION_FORM_EMPTY("Verification form is empty"),

    DUPLICATE_REGISTRATION_FOUND("User is already registered"),

    USER_ID_MISSING("User Id is required in body"),
    EMAIL_ID_NOT_REGISTERED("Email Id is not registered"),
    REFRESH_TOKEN_REQUIRED("Refresh token required in the header"),

    INVALID_ACCESS_TOKEN("Invalid access token for this User"),
    INVALID_CLIENT("Invalid clientId or secretKey"),

    INVALID_CLIENT_TOKEN("Invalid client token"),

    ACCESS_TOKEN_OR_USER_ID_INVALID("INVALID access token or userid"),
    INVALID_CLIENTID_OR_SECRET_KEY("Invalid clientid or secret key"),

    INVALID_REFRESH_TOKEN("Invalid refresh token"),
    INVALID_EMAIL_ID("Invalid email id"),
    INVALID_USER_ID("Invalid user id"),
    INVALID_EMAIL_CODE("Invalid email code"),
    UNAUTHORIZED("Unauthorized"),

    SESSION_EXPIRED_MSG("Session expired."),
    INVALID_AUTH_CODE("INVALID_AUTH_CODE"),
    INVALID_EMAIL("Invalid Email"),
    ACCOUNT_DEACTIVATE_ERROR_MSG("Your account has been deactivated"),
    INVALID_USERNAME_PASSWORD_MSG("Invalid username or password"),
    EMAIL_EXISTS("This email has already been used. Please try with different email address."),
    INVALID_INPUT_ERROR_MSG("Invalid input."),
    INACTIVE("INACTIVE"),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    PARTIAL("PARTIALS"),
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
    WITHDRAWN_STUDY("You are already Withdrawn from study"),
    EMAIL_NOT_EXISTS("Email Doesn't Exists"),
    DOMAIN_NOT_WHITELISTED("Email does not belong to a whitelisted domain."),
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
        "Thanks, your email has been successfully verified! You can now proceed to completing the sign up process on the mobile app."),
    EMAIL_NOT_VERIFIED(
        "Your account is not verified. Please verify your account by clicking on verification link which has been sent to your registered email. If not received, would you like to resend verification link?"),
    STUDY("Study"),
    GATEWAY("Gateway"),
    DEVICE_ANDROID("android"),
    DEVICE_IOS("ios"),
    INVALID_REFRESHTOKEN("Invalid refresh token."),
    APP_EXIST_NOTEXIST(
        "You already have a valid account for this app. Please directly sign in using the same email and associated password."),
    ORG_NOTEXIST(
        "Sorry, this email is already in use for platform-powered app(s) belonging to another organization. Please use another email to sign up for this app."),
    LOGIN_ORG_NOTEXIST(
        "Sorry, this account is in use for platform-powered app(s) belonging to another organization. Please sign up with a different email and try again."),
    RP_SUBJECT("subject"),
    RP_BODY("body");

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
      logger.error("MyStudiesUserRegUtil - getFailureResponse() :: ERROR ", e);
    }
  }

  public static Date addMinutes(String currentDate, int minutes) {
    Date futureDate = null;
    try {
      Date dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MINUTE, minutes);
      Date newDate = cal.getTime();
      futureDate =
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
              .parse(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(newDate));
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - addMinutes() :: ERROR ", e);
    }
    return futureDate;
  }

  public static String getEncryptedString(String input) {
    StringBuffer sb = new StringBuffer();
    if (!StringUtils.isEmpty(input)) {
      input = input + "StudyGateway";
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(input.getBytes("UTF-8"));
        byte[] digestBytes = messageDigest.digest();
        String hex = null;
        for (int i = 0; i < 8; i++) {
          hex = Integer.toHexString(0xFF & digestBytes[i]);
          if (hex.length() < 2) {
            sb.append("0");
          }
          sb.append(hex);
        }
      } catch (Exception e) {
        logger.error("MyStudiesUserRegUtil - addMinutes() :: ERROR ", e);
      }
    }
    return sb.toString();
  }

  public static String generateEmailContent(String emailContent, Map<String, String> keyValue) {
    logger.entry("Begin genarateEmailContent()");
    try {
      if (!StringUtils.isEmpty(emailContent)) {
        for (Map.Entry<String, String> entry : keyValue.entrySet()) {
          emailContent =
              emailContent.replace(
                  entry.getKey(), StringUtils.isEmpty(entry.getValue()) ? "" : entry.getValue());
        }
      }
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - genarateEmailContent() - error() ", e);
    }
    logger.exit("genarateEmailContent() end");
    return emailContent;
  }

  public static boolean isValidEmailId(String emailId) {
    logger.entry("isValidEmailId() called");

    if (emailId != null) {
      return emailId.matches("([A-Za-z0-9-_.]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)");
    } else {
      logger.exit("invalid emailId format");
      return false;
    }
  }

  public static boolean isPasswordStrong(String password) {
    if (password != null) {
      return password.matches(
          "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\\"#$%&'()*+,-.:;<=>?@\\\\[\\\\]^_`{|}~]).{8,64}$");
    } else return false;
  }
}
