/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.cloud.healthcare.fdamystudies.controller.bean.LoginResponse;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
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
    STATUS_400("400"),
    STATUS_401("401"),
    STATUS_403("403"),
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
        "Sorry, an error has occurred and your request could not be processed. "
            + "Please try again later."),
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
        "Your password does not meet the required criteria. "
            + "Please refer to the password requirements provided on-screen."),
    CODE_EXPIRED("Code Expired"),
    INVALID_CREDENTIALS("Invalid credentials"),
    ACCOUNT_LOCKED(
        "Due to consecutive failed sign-in attempts with incorrect password, your account has been"
            + " locked for a period of 15 minutes."
            + " Please check your registered email inbox for assistance to reset your password "
            + "in this period or wait until the lock period is over to sign in again."),
    ACCOUNT_TEMP_LOCKED("Your account has been temporarily locked. Please try after some time."),
    EMAIL_NOT_VERIFIED(
        "Your account is not verified. Please verify your account by clicking on verification link"
            + " which has been sent to your registered email. "
            + "If not received, would you like to resend verification link?"),

    INVALID_USERNAME_PASSWORD_MSG(
        "Email or password is invalid. If you have not registered, please Sign Up"),

    PASSWORD_EXPIRED("Your Password is expired. Please use forgot password to reset your password"),

    INVALID_EMAIL_ID("Invalid email id"),

    UNAUTHORIZED_CLIENT_FOR_REGISTER("This client is not authorized to register User");

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
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\\"#$%&'()*+,"
                + "-.:;<=>?@\\\\[\\\\]^_`{|}~]).{8,64}$");
      } else {
        return false;
      }
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

  public static String genarateEmailContent(String emailContentName, Map<String, String> keyValue) {
    logger.info("MyStudiesUserRegUtil - genarateEmailContent() :: Starts");

    if (StringUtils.isNotBlank(emailContentName)) {
      for (Map.Entry<String, String> entry : keyValue.entrySet()) {
        emailContentName =
            emailContentName.replace(
                entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
      }
    }
    logger.info("MyStudiesUserRegUtil - genarateEmailContent() :: Ends");
    return emailContentName;
  }

  public static void registrationResponse(HttpServletResponse response, String flag) {
    logger.info("MyStudiesUserRegUtil - registrationResponse() :: Starts");
    switch (flag) {
      case AppConstants.INVALID_EMAIL_ID:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_ID.getValue(),
            response);
        break;
      case AppConstants.PASSWORD_IS_INVALID:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NEW_PASSWORD_IS_INVALID.getValue(),
            response);
        break;
      case AppConstants.UNAUTHORIZED_CLIENT_FOR_REGISTER:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_401.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED_CLIENT_FOR_REGISTER.getValue(),
            response);
        break;
      case AppConstants.EMAIL_EXISTS:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.EMAIL_EXISTS.getValue(),
            response);
        break;

      default:
        logger.error(
            "MyStudiesUserRegUtil - registrationResponse() :: No matches found for the given flag!!");
        throw new IllegalArgumentException();
    }
  }

  public static LoginResponse loginResponse(HttpServletResponse response, String flag) {
    logger.info("MyStudiesUserRegUtil - loginResponse() :: Starts");
    LoginResponse loginResp = new LoginResponse();
    switch (flag) {
      case AppConstants.INVALID_USERNAME_PASSWORD:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            AppConstants.INVALID_USERNAME_PASSWORD_MSG,
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USERNAME_PASSWORD_MSG.getValue(),
            response);
        loginResp.setCode(HttpStatus.UNAUTHORIZED.value());
        loginResp.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USERNAME_PASSWORD_MSG.getValue());
        return loginResp;
      case AppConstants.ACCOUNT_LOCKED:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.name(),
            MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue(),
            response);
        loginResp.setCode(ErrorCode.EC_102.code());
        loginResp.setMessage(MyStudiesUserRegUtil.ErrorCodes.ACCOUNT_LOCKED.getValue());
        return loginResp;
      case AppConstants.PASSWORD_EXPIRED:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_401.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.PASSWORD_EXPIRED.getValue(),
            response);
        return null;
      case AppConstants.CODE_EXPIRED:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_103.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.CODE_EXPIRED.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.CODE_EXPIRED.getValue(),
            response);
        return null;

      default:
        logger.error(
            "MyStudiesUserRegUtil - loginResponse() :: No matches found for the given flag!!");
        throw new IllegalArgumentException();
    }
  }

  public static void commonErrorResponse(HttpServletResponse response, String flag) {
    logger.info("MyStudiesUserRegUtil - commonErrorResponse() :: Starts");
    switch (flag) {
      case AppConstants.MISSING_REQUIRED_PARAMETER:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_400.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        break;
      case AppConstants.INVALID_CLIENTID_OR_SECRET_KEY:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_401.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
            response);
        break;
      case AppConstants.SYSTEM_EXCEPTION:
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_500.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.SYSTEM_ERROR_FOUND.getValue(),
            response);
        break;
      default:
        logger.error(
            "MyStudiesUserRegUtil - commonErrorResponse() :: No matches found for the given flag!!");
        throw new IllegalArgumentException();
    }
  }
}
