/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;

public class MyStudiesUserRegUtil {

  private static final Logger logger = LoggerFactory.getLogger(MyStudiesUserRegUtil.class);

  public enum ErrorCodes {
    INVALID_INPUT("INVALID_INPUT"),
    UNKNOWN("UNKNOWN"),
    STATUS_200("200"), // OK
    STATUS_101("101"), // Invalid Authentication (authKey is not valid).
    STATUS_102("102"), // Invalid Inputs (If any of the input parameter is missing).
    STATUS_103("103"), // No Data available.
    STATUS_104("104"), // Unknown Error

    STATUS_111("111"), // Access Token is missing
    STATUS_115("115"), // Invalid input access token
    STATUS_116("116"), // Invalid input user

    STATUS_121("121"), // Invalid emailId or password
    STATUS_123("123"), // Unauthorized
    STATUS_124("124"), // EmailId is not registered
    STATUS_128("128"), // Access token or User id is invalid

    STATUS_500("500"), // Internal Server Error

    INVALID_USER_ID("Invalid user id"),
    ACCESS_TOKEN_GENERATION_ERROR("Access token generation error"),
    EMAIL_ID_NOT_VERIFIED("Email id is not verified"),
    UNAUTHORIZED("Unauthorized"),
    INVALID_ACCESS_TOKEN_USER_ID("Invalid access token or userId"),
    EMAIL_ID_VERIFIED("EmailId has already verified"),
    INVALID_CLIENTID_OR_SECRET_KEY("Invalid Client Id or Secret Key"),
    INVALID_CLIENT_TOKEN("Invalid client token"),
    SYSTEM_ERROR_FOUND(
        "Sorry, an error has occurred and your request could not be processed. Please try again later."),
    SESSION_EXPIRED_MSG("Session expired."),
    EMAIL_EXISTS("This email has already been used. Please try with different email address."),
    INVALID_INPUT_ERROR_MSG("Invalid input."),
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    CONNECTION_ERROR_MSG("Oops, something went wrong. Please try again after sometime"),
    OLD_PASSWORD_NOT_EXISTS("Invalid old password"),
    OLD_PASSWORD_AND_NEW_PASSWORD_NOT_SAME("Current Password and New Password cannot be same"),
    NEW_PASSWORD_NOT_SAME_LAST_PASSWORD(
        "New Password should not be the same as the last 10 passwords."),
    NEW_PASSWORD_IS_INVALID(
        "Your password does not meet the required criteria. Please refer to the password requirements provided on-screen."),
    CODE_EXPIRED("Code Expired"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCOUNT_LOCKED("As a security measure, this account has been locked."),
    ACCOUNT_TEMP_LOCKED("As a security measure, this account has been locked for 15 minutes."),
    EMAIL_NOT_VERIFIED(
        "Your account is not verified. Please verify your account by clicking on verification link which has been sent to your registered email. If not received, would you like to resend verification link?"),

    INVALID_USERNAME_PASSWORD_MSG("Invalid username or password"),

    PASSWORD_EXPIRED("Password has expired"),

    INVALID_EMAIL_ID("Invalid email id");

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
      if (status.equalsIgnoreCase(ErrorCodes.STATUS_104.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_111.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_115.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_116.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_121.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_124.getValue())
          || status.equalsIgnoreCase(ErrorCodes.STATUS_128.getValue())) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
      }
    } catch (Exception e) {
      logger.info("MyStudiesUserRegUtil - getFailureResponse() :: error ", e);
    }
  }

  public static Date addMinutes(String currentDate, int minutes) {
    Date futureDate = null;
    try {
      Date dt = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).parse(currentDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MINUTE, minutes);
      Date newDate = cal.getTime();
      futureDate =
          new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT)
              .parse(new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).format(newDate));
    } catch (Exception e) {
      logger.info("MyStudiesUserRegUtil - addMinutes() :: error ", e);
    }
    return futureDate;
  }

  public static Date getCurrentUtilDateTime() {
    Date date = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow =
        new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).format(currentDate.getTime());
    try {
      date = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).parse(dateNow);
    } catch (Exception e) {
      logger.info("MyStudiesUserRegUtil - getCurrentUtilDateTime() :: ERROR ", e);
    }
    return date;
  }

  public static String getEncryptedString12(String input) {
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
    logger.info("MyStudiesUserRegUtil - genarateEmailContent() start");
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
    logger.info("MyStudiesUserRegUtil - genarateEmailContent() end");
    return emailContent;
  }

  public static boolean isPasswordStrong(String password) throws SystemException {
    logger.info("MyStudiesUserRegUtil - isPasswordStrong() start");
    try {
      if (password != null) {
        return password.matches(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\\"#$%&'()*+,-.:;<=>?@\\\\[\\\\]^_`{|}~]).{8,64}$");
      } else return false;
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - isPasswordStrong() - error() ", e);
      throw new SystemException();
    }
  }

  public static boolean isValidEmailId(String emailId) throws SystemException {
    logger.info("MyStudiesUserRegUtil - isValidEmailId() called");
    try {
      if (emailId != null) {
        return emailId.matches("([A-Za-z0-9-_.+]+@[A-Za-z0-9-_]+(?:\\.[A-Za-z0-9]+)+)");
      } else {
        return false;
      }
    } catch (Exception e) {
      logger.error("MyStudiesUserRegUtil - isValidEmailId() - error() ", e);
      throw new SystemException();
    }
  }

  public static String getEncryptedString(String input, String rawSalt) {

    logger.info("MyStudiesUserRegUtil - getEncryptedString() - starts");
    StringBuilder sb = new StringBuilder();
    if (!StringUtils.isEmpty(input)) {
      try {
        byte[] salt = rawSalt.getBytes();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        // Pass salt to the digest
        messageDigest.update(salt);

        messageDigest.update(input.getBytes("UTF-8"));
        byte[] digestBytes = messageDigest.digest();

        for (int i = 0; i < digestBytes.length; i++) {
          String hex = Integer.toHexString(0xff & digestBytes[i]);
          if (hex.length() < 2) {
            sb.append('0');
          }
          sb.append(hex);
        }
      } catch (Exception e) {
        logger.error("MyStudiesUserRegUtil getEncryptedString() - error() ", e);
      }
    }
    logger.info("MyStudiesUserRegUtil - getEncryptedString() - ends");
    return sb.toString();
  }

  public static String getHashedValue(String passwordToHash) {
    String generatedHash = null;

    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(passwordToHash.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.info("MyStudiesUserRegUtil getHashedValue() - error() ", e);
    }
    logger.info("MyStudiesUserRegUtil - getHashedValue() - ends");
    return generatedHash;
  }

  public static String getCurrentDateTime() {
    String getToday = "";
    try {
      Date today = new Date();
      SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT);
      getToday = formatter.format(today.getTime());

    } catch (Exception e) {
      logger.info(" getCurrentDateTime: ", e);
    }
    return getToday;
  }

  public static Date addDays(String currentDate, int days) {
    Date futureDate = null;
    try {
      Date dt = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).parse(currentDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.DATE, days);
      Date newDate = cal.getTime();
      futureDate =
          new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT)
              .parse(new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).format(newDate));
    } catch (Exception e) {
      logger.info(" addDays : ", e);
    }
    return futureDate;
  }

  public static Date addHours(String currentDate, int hours) {
    Date futureDate = null;
    try {
      Date dt = new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).parse(currentDate);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.HOUR, hours);
      Date newDate = cal.getTime();
      futureDate =
          new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT)
              .parse(new SimpleDateFormat(AppConstants.SDF_DATE_TIME_FORMAT).format(newDate));
    } catch (Exception e) {
      logger.info(" addHours : ", e);
    }
    return futureDate;
  }
}
