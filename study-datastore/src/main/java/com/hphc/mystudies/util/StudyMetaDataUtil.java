/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.util;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.hphc.mystudies.bean.FailureResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class StudyMetaDataUtil {

  private static final XLogger LOGGER =
      XLoggerFactory.getXLogger(StudyMetaDataUtil.class.getName());

  @SuppressWarnings("rawtypes")
  protected static final HashMap configMap = StudyMetaDataUtil.getAppProperties();

  @SuppressWarnings("rawtypes")
  protected static final HashMap authConfigMap = StudyMetaDataUtil.getAuthorizationProperties();

  @SuppressWarnings("unchecked")
  private static final HashMap<String, String> authPropMap = StudyMetaDataUtil.authConfigMap;

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static HashMap getAppProperties() {
    LOGGER.entry("begin getAppProperties()");
    HashMap hm = new HashMap<String, String>();
    Enumeration<String> keys = null;
    Enumeration<Object> objectKeys = null;
    try {
      ResourceBundle rb = ResourceBundle.getBundle("messageResource");
      keys = rb.getKeys();
      while (keys.hasMoreElements()) {
        String key = keys.nextElement();
        String value = rb.getString(key);
        hm.put(key, value);
      }
      ServletContext context = ServletContextHolder.getServletContext();

      Properties prop =
          PropertiesUtil.makePropertiesWithEnvironmentVariables("application.properties");
      objectKeys = prop.keys();
      while (objectKeys.hasMoreElements()) {
        String key = (String) objectKeys.nextElement();
        String value = prop.getProperty(key);
        hm.put(key, value);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getAppProperties() - ERROR ", e);
    }
    LOGGER.exit("getAppProperties() :: ends");
    return hm;
  }

  public static FailureResponse getFailureResponse(String status, String title, String detail) {
    LOGGER.entry("begin getFailureResponse()");
    FailureResponse failureResponse = new FailureResponse();
    try {
      failureResponse.setResultType(StudyMetaDataConstants.FAILURE);
      failureResponse.getErrors().setStatus(status);
      failureResponse.getErrors().setTitle(title);
      failureResponse.getErrors().setDetail(detail);
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - getFailureResponse() ", e);
    }
    LOGGER.exit("getFailureResponse() :: Ends");
    return failureResponse;
  }

  public static void getFailureResponse(
      String status, String title, String detail, HttpServletResponse response) {
    LOGGER.entry("begin getFailureResponse()");
    try {
      response.setHeader("status", status);
      response.setHeader("title", title);
      response.setHeader("StatusMessage", detail);
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - getFailureResponse() ", e);
    }
    LOGGER.exit("getFailureResponse() :: Ends");
  }

  public static int noOfDaysForMonthYear(int month, int year) {
    LOGGER.entry("begin noOfDaysForMonthYear()");
    int numDays = 30;
    try {
      Calendar calendar = Calendar.getInstance();
      calendar.set(Calendar.YEAR, year);
      calendar.set(Calendar.MONTH, month - 1);
      numDays = calendar.getActualMaximum(Calendar.DATE);
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - noOfDaysForMonthYear() " + e);
    }
    LOGGER.exit("noOfDaysForMonthYear() :: Ends");
    return numDays;
  }

  public static int noOfDaysBetweenTwoDates(String startDate, String endDate) {
    LOGGER.entry("begin noOfDaysBetweenTwoDates()");
    int daysdiff = 0;
    try {
      long diff =
          StudyMetaDataConstants.SDF_DATE.parse(endDate).getTime()
              - StudyMetaDataConstants.SDF_DATE.parse(startDate).getTime();
      long diffDays = (diff / (24 * 60 * 60 * 1000)) + 1;
      daysdiff = (int) diffDays;
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - noOfDaysBetweenTwoDates() " + e);
    }
    LOGGER.exit("noOfDaysBetweenTwoDates() :: Ends");
    return daysdiff;
  }

  public static String getCurrentDate() {
    LOGGER.entry("begin getCurrentDate()");
    String dateNow = "";
    try {
      Calendar currentDate = Calendar.getInstance();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
      dateNow = formatter.format(currentDate.getTime());
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - getCurrentDate() " + e);
    }
    LOGGER.exit("getCurrentDate() :: Ends");
    return dateNow;
  }

  public static String getCurrentDateTime() {
    LOGGER.entry("StudyMetaDataUtil: getCurrentDateTime() - Starts ");
    String getToday = "";
    try {
      Date today = new Date();
      SimpleDateFormat formatter =
          new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
      formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
      getToday = formatter.format(today.getTime());
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getCurrentDateTime() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getCurrentDateTime() - Ends ");
    return getToday;
  }

  public static String getFormattedDate1(
      String inputDate, String inputFormat, String outputFormat) {
    LOGGER.entry("StudyMetaDataUtil: getFormattedDate1() - Starts ");
    String finalDate = "";
    java.sql.Date formattedDate = null;
    if ((inputDate != null) && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)) {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
        formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());

        formatter = new SimpleDateFormat(outputFormat);
        finalDate = formatter.format(formattedDate);
      } catch (Exception e) {
        LOGGER.error("StudyMetaDataUtil: getFormattedDate1() - ERROR", e);
      }
    }
    LOGGER.exit("StudyMetaDataUtil: getFormattedDate1() - Ends ");
    return finalDate;
  }

  public static String getTimeDiffInDaysHoursMins(Date dateOne, Date dateTwo) {
    LOGGER.entry("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Starts ");
    String diff = "";
    try {
      long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
      diff =
          String.format(
              "%d Day(s) %d hour(s) %d min(s)",
              TimeUnit.MILLISECONDS.toDays(timeDiff),
              TimeUnit.MILLISECONDS.toHours(timeDiff)
                  - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(timeDiff)),
              TimeUnit.MILLISECONDS.toMinutes(timeDiff)
                  - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeDiff)));
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getTimeDiffInDaysHoursMins() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Ends ");
    return diff;
  }

  public static String getTimeDiffInDaysHoursMins(String dateOne1, String dateTwo2) {
    LOGGER.entry("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Starts ");
    String diff = "";
    try {
      Date dateOne = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateOne1);
      Date dateTwo = StudyMetaDataConstants.SDF_DATE_TIME.parse(dateTwo2);
      long timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
      diff = String.format("%d", TimeUnit.MILLISECONDS.toMinutes(timeDiff));
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - getTimeDiffInDaysHoursMins() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getTimeDiffInDaysHoursMins() - Ends ");
    return diff;
  }

  public static String getEncodedStringByBase64(String plainText) {
    LOGGER.entry("StudyMetaDataUtil: getEncodedStringByBase64() - Starts ");
    if (StringUtils.isEmpty(plainText)) {
      return "";
    }
    try {
      byte[] bytesEncoded = Base64.encode(plainText.getBytes());
      return new String(bytesEncoded);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getEncodedStringByBase64() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getEncodedStringByBase64() - Ends ");
    return "";
  }

  public static String getDecodedStringByBase64(byte[] encodedText) {
    LOGGER.entry("StudyMetaDataUtil: getDecodedStringByBase64() - Starts ");
    if (StringUtils.isEmpty(encodedText.toString())) {
      return "";
    }
    try {
      byte[] valueDecoded = Base64.decode(encodedText);
      return new String(valueDecoded);
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getDecodedStringByBase64() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getDecodedStringByBase64() - Ends ");
    return "";
  }

  public static String getEncryptedString(String input) {
    LOGGER.entry("getEncryptedString");
    StringBuilder sb = new StringBuilder();
    String encryptValue = "";
    if (StringUtils.isNotEmpty(input)) {
      encryptValue = input + StudyMetaDataConstants.PASS_SALT;
      try {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
        messageDigest.update(encryptValue.getBytes("UTF-8"));
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
        LOGGER.error("ERROR: getEncryptedString ", e);
      }
    }
    LOGGER.exit("getEncryptedString :: Ends");
    return sb.toString();
  }

  public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
    LOGGER.entry("StudyMetaDataUtil: getFormattedDate() - Starts ");
    String finalDate = "";
    java.sql.Date formattedDate = null;
    if ((inputDate != null) && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)) {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
        formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());

        formatter = new SimpleDateFormat(outputFormat);
        finalDate = formatter.format(formattedDate);
      } catch (Exception e) {
        LOGGER.error("ERROR: getFormattedDate ", e);
      }
    }
    LOGGER.exit("StudyMetaDataUtil: getFormattedDate() - Ends ");
    return finalDate;
  }

  public static String addMinutes(String dtStr, int minutes) {
    LOGGER.entry("StudyMetaDataUtil: addMinutes() - Starts ");
    String newdateStr = "";
    try {
      SimpleDateFormat date = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
      Date dt = date.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MINUTE, minutes);
      Date newDate = cal.getTime();
      newdateStr = date.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addMinutes() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addMinutes() - Ends ");
    return newdateStr;
  }

  public static String addDays(String dtStr, int days) {
    LOGGER.entry("StudyMetaDataUtil: addDays() - Starts ");
    String newdateStr = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE_TIME.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.DATE, days);
      Date newDate = cal.getTime();
      newdateStr = StudyMetaDataConstants.SDF_DATE_TIME.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addDays() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addDays() - Ends ");
    return newdateStr;
  }

  public static String addMonth(String dtStr, int months) {
    LOGGER.entry("StudyMetaDataUtil: addMonth() - Starts ");
    String newdateStr = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MONTH, months);
      Date newDate = cal.getTime();
      newdateStr = StudyMetaDataConstants.SDF_DATE.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addMonth() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addMonth() - Ends ");
    return newdateStr;
  }

  public static String addYear(String dtStr, int years) {
    LOGGER.entry("StudyMetaDataUtil: addYear() - Starts ");
    String newdateStr = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.YEAR, years);
      Date newDate = cal.getTime();
      newdateStr = StudyMetaDataConstants.SDF_DATE.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addYear() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addYear() - Ends ");
    return newdateStr;
  }

  public static Long getDateToSeconds(String getCurrentDate) {
    LOGGER.entry("StudyMetaDataUtil: getDateToSeconds() - Starts ");
    Long getInSeconds = null;
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(getCurrentDate);
      getInSeconds = dt.getTime();
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - getDateToSeconds() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getDateToSeconds() - Ends ");
    return getInSeconds;
  }

  public static String getSecondsToDate(String value) {
    LOGGER.entry("StudyMetaDataUtil: getSecondsToDate() - Starts ");
    String dateText;
    long getLongValue = Long.parseLong(value);
    Date date = new Date(getLongValue);
    SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yyyy"); // yyyy-MM-dd
    df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    dateText = df2.format(date);
    LOGGER.exit("StudyMetaDataUtil: getSecondsToDate() - Ends ");
    return dateText;
  }

  public static String getToDate(String value) {
    LOGGER.entry("StudyMetaDataUtil: getToDate() - Starts ");
    String dateText;
    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd"); // yyyy-MM-dd
    df2.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    dateText = df2.format(value);
    LOGGER.exit("StudyMetaDataUtil: getToDate() - Ends ");
    return dateText;
  }

  public static String getCurrentDateTimeInUTC() {
    LOGGER.entry("StudyMetaDataUtil: getCurrentDateTimeInUTC() - Starts ");
    String dateNow = null;
    final SimpleDateFormat sdf = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
    String timeZone = "UTC";
    try {
      String strDate = new Date() + "";
      if (strDate.indexOf("IST") != -1) {
        timeZone = "IST";
      }
      sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
      dateNow = sdf.format(new Date());
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil: getCurrentDateTimeInUTC(): ERROR " + e);
    }
    LOGGER.exit("StudyMetaDataUtil: getCurrentDateTimeInUTC() - Ends ");
    return dateNow;
  }

  public static String platformType(String authCredentials, String type) {
    LOGGER.entry("begin platformType() - Starts");
    String bundleIdAndAppToken = null;
    String platform = "";
    try {
      if (StringUtils.isNotEmpty(authCredentials) && authCredentials.contains("Basic")) {
        final byte[] encodedUserPassword =
            authCredentials.replaceFirst("Basic" + " ", "").getBytes();
        byte[] decodedBytes = Base64.decode(encodedUserPassword);
        bundleIdAndAppToken = new String(decodedBytes, "UTF-8");

        if (bundleIdAndAppToken.contains(":")) {
          final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
          final String bundleId = tokenizer.nextToken();
          final String appToken = tokenizer.nextToken();
          if (authPropMap.containsValue(bundleId) && authPropMap.containsValue(appToken)) {
            String appBundleId = "";
            String appTokenId = "";
            for (Map.Entry<String, String> map : authPropMap.entrySet()) {
              if (map.getValue().equals(appToken)) {
                appTokenId = map.getKey();
              }

              if (map.getValue().equals(bundleId)) {
                appBundleId = map.getValue();
              }
            }

            if (StringUtils.isNotEmpty(appBundleId) && StringUtils.isNotEmpty(appTokenId)) {
              final StringTokenizer authTokenizer = new StringTokenizer(appTokenId, ".");
              final String platformType = authTokenizer.nextToken();

              if (platformType.equals(StudyMetaDataConstants.STUDY_PLATFORM_ANDROID)) {
                switch (type) {
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM:
                    platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_ANDROID;
                    break;
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS:
                    platform = StudyMetaDataConstants.STUDY_PLATFORM_ANDROID;
                    break;
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID:
                    platform = bundleId;
                    break;
                }
              } else {
                switch (type) {
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_PLATFORM:
                    platform = StudyMetaDataConstants.STUDY_PLATFORM_TYPE_IOS;
                    break;
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_OS:
                    platform = StudyMetaDataConstants.STUDY_PLATFORM_IOS;
                    break;
                  case StudyMetaDataConstants.STUDY_AUTH_TYPE_BUNDLE_ID:
                    platform = bundleId;
                    break;
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - platformType() :: ERROR", e);
    }
    LOGGER.exit("platformType() - Ends");
    return platform;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static HashMap getAuthorizationProperties() {
    LOGGER.entry("begin getAuthorizationProperties()");
    HashMap hashMap = new HashMap<String, String>();
    Enumeration<String> keys = null;
    Enumeration<Object> objectKeys = null;
    try {
      Properties prop =
          PropertiesUtil.makePropertiesWithEnvironmentVariables("authorizationResource.properties");
      objectKeys = prop.keys();
      while (objectKeys.hasMoreElements()) {
        String key = (String) objectKeys.nextElement();
        String value = prop.getProperty(key);
        hashMap.put(key, value);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getAuthorizationProperties() - ERROR ", e);
    }
    LOGGER.exit("getAuthorizationProperties() :: Ends");
    return hashMap;
  }

  public static String getDayByDate(String input) {
    LOGGER.entry("StudyMetaDataUtil: getDayByDate() - Starts ");
    String actualDay = "";
    try {
      if (StringUtils.isNotEmpty(input)) {
        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date MyDate = newDateFormat.parse(input);
        newDateFormat.applyPattern(StudyMetaDataConstants.SDF_DAY);
        actualDay = newDateFormat.format(MyDate);
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getDayByDate() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getDayByDate() - Ends ");
    return actualDay;
  }

  public static String addDaysToDate(String input, int days) {
    LOGGER.entry("StudyMetaDataUtil: addDaysToDate() - Starts ");
    String output = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.DATE, days);
      Date newDate = cal.getTime();
      output = StudyMetaDataConstants.SDF_DATE.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addDaysToDate() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addDaysToDate() - Ends ");
    return output;
  }

  public static String addWeeksToDate(String input, int weeks) {
    LOGGER.entry("StudyMetaDataUtil: addWeeksToDate() - Starts ");
    String output = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.WEEK_OF_MONTH, weeks);
      Date newDate = cal.getTime();
      output = StudyMetaDataConstants.SDF_DATE.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addWeeksToDate() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addWeeksToDate() - Ends ");
    return output;
  }

  public static String addMonthsToDate(String input, int months) {
    LOGGER.entry("StudyMetaDataUtil: addMonthsToDate() - Starts ");
    String output = "";
    try {
      Date dt = StudyMetaDataConstants.SDF_DATE.parse(input);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MONTH, months);
      Date newDate = cal.getTime();
      output = StudyMetaDataConstants.SDF_DATE.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("StudyMetaDataUtil - addMonthsToDate() - ERROR ", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addMonthsToDate() - Ends ");
    return output;
  }

  public static String getFormattedDateTimeZone(
      String input, String inputFormat, String outputFormat) {
    LOGGER.entry("StudyMetaDataUtil: getFormattedDateTimeZone() - Starts ");
    String output = "";
    try {
      if (StringUtils.isNotEmpty(input)) {
        SimpleDateFormat inputSDF = new SimpleDateFormat(inputFormat);
        Date inputDate = inputSDF.parse(input);
        SimpleDateFormat outputSDF = new SimpleDateFormat(outputFormat);
        output = outputSDF.format(inputDate);
      }
    } catch (Exception e) {
      LOGGER.error("AuthenticationService - getFormattedDateTimeZone() :: ERROR", e);
    }
    LOGGER.exit("StudyMetaDataUtil: getFormattedDateTimeZone() - Ends ");
    return output;
  }

  public static String addSeconds(String dtStr, int seconds) {
    LOGGER.entry("StudyMetaDataUtil: addSeconds() - Starts ");
    String newdateStr = "";
    try {
      SimpleDateFormat date = new SimpleDateFormat(StudyMetaDataConstants.SDF_DATE_TIME_PATTERN);
      Date dt = date.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.SECOND, seconds);
      Date newDate = cal.getTime();
      newdateStr = date.format(newDate);
    } catch (ParseException e) {
      LOGGER.error("AuthenticationService - addSeconds() :: ERROR", e);
    }
    LOGGER.exit("StudyMetaDataUtil: addSeconds() - Ends ");
    return newdateStr;
  }

  public static String getMilliSecondsForImagePath() {
    LOGGER.entry("StudyMetaDataUtil: getMilliSecondsForImagePath() - Starts ");
    String milliSeconds;
    Calendar cal = Calendar.getInstance();
    milliSeconds = "?v=" + cal.getTimeInMillis();
    LOGGER.exit("StudyMetaDataUtil: getMilliSecondsForImagePath() - Ends ");
    return milliSeconds;
  }

  public static String getBundleIdFromAuthorization(String authCredentials) {
    LOGGER.entry("begin getBundleIdFromAuthorization() - Starts");
    String bundleIdAndAppToken = null;
    String appBundleId = "";
    try {
      if (StringUtils.isNotEmpty(authCredentials) && authCredentials.contains("Basic")) {
        final byte[] encodedUserPassword =
            authCredentials.replaceFirst("Basic" + " ", "").getBytes();
        byte[] decodedBytes = Base64.decode(encodedUserPassword);
        bundleIdAndAppToken = new String(decodedBytes, "UTF-8");
        if (bundleIdAndAppToken.contains(":")) {
          final StringTokenizer tokenizer = new StringTokenizer(bundleIdAndAppToken, ":");
          final String bundleId = tokenizer.nextToken();
          final String appToken = tokenizer.nextToken();
          if (authPropMap.containsKey(bundleId) && authPropMap.containsKey(appToken)) {
            appBundleId = bundleId;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("StudyMetaDataUtil - getBundleIdFromAuthorization() :: ERROR", e);
    }
    LOGGER.exit("getBundleIdFromAuthorization() - Ends");
    return appBundleId;
  }

  public static String replaceSingleQuotes(String activityId) {
    LOGGER.entry("begin replaceSingleQuotes() - Starts");
    String newActivityId = activityId;
    if (activityId.indexOf("'") > -1) {
      newActivityId = activityId.replaceAll("'", "''");
    }
    LOGGER.exit("replaceSingleQuotes() - Ends");
    return newActivityId;
  }

  // Get the day for the WeekName
  public static int getDayName(String dayName) {
    int day = 0;
    switch (dayName) {
      case "Sunday":
        day = 1;
        break;
      case "Monday":
        day = 2;
        break;
      case "Tuesday":
        day = 3;
        break;
      case "Wednesday":
        day = 4;
        break;
      case "Thursday":
        day = 5;
        break;
      case "Friday":
        day = 6;
        break;
      case "Saturday":
        day = 7;
        break;
    }

    return day;
  }

  public static String saveResponsesActivityDocument(
      String jsonData,
      String activityId,
      String studyId,
      String activityRunId,
      String participantId,
      String version) {
    LOGGER.entry("begin saveResponsesActivityDocument()");
    File serverFile;
    String consentFileName = null;
    try {
      if (StringUtils.isNotEmpty(jsonData)) {
        // byte[] bytes = Base64.decode(content.replaceAll("\n", ""));
        byte[] bytes = jsonData.getBytes();
        String currentPath =
            System.getProperty((String) getAppProperties().get("fda.current.path"));
        // String currentPath =
        // getAppProperties().get("fda.docs.responses.path").toString();
        String rootPath =
            currentPath.replace('\\', '/') + getAppProperties().get("fda.docs.responses.path");
        // String rootPath = currentPath.replace('\\', '/');
        File directory = new File(rootPath + File.separator);
        if (!directory.exists()) {
          directory.mkdirs();
        }
        consentFileName =
            getStandardFileNameForResponses(
                activityId, studyId, activityRunId, participantId, version);
        LOGGER.warn(
            "WARN: StudyMetaDataUtil - saveResponsesActivityDocument() :: CONSENT FILE NAME ==> "
                + consentFileName);
        serverFile =
            new File("/var/www/html/dataResponses/data" + File.separator + consentFileName);

        serverFile.setReadable(true);
        serverFile.setExecutable(true);
        serverFile.setWritable(true);

        serverFile.getAbsolutePath();

        saveFileInPath(serverFile, bytes);
      }
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - saveResponsesActivityDocument()", e);
    }
    LOGGER.exit("saveResponsesActivityDocument() :: ends");
    return consentFileName;
  }

  public static void saveFileInPath(File serverFile, byte[] bytes) {
    LOGGER.entry("begin saveFileInPath()");
    try (FileOutputStream fileOutputStream = new FileOutputStream(serverFile);
        BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream); ) {
      stream.write(bytes);
      LOGGER.warn(
          "WARN: StudyMetaDataUtil - saveFileInPath() :: CONSENT FILE PATH ==> "
              + serverFile.getAbsolutePath());
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - saveFileInPath()", e);
    }
    LOGGER.exit("saveFileInPath() :: ends");
  }

  public static String getStandardFileNameForResponses(
      String activityId,
      String studyId,
      String activityRunId,
      String participantId,
      String version) {
    LOGGER.entry("begin getStandardFileNameForResponses()");
    String fileName = null;
    try {
      fileName =
          new StringBuilder()
              .append("FDAHPHCI_")
              .append(new SimpleDateFormat("MMddyyyyHHmmss").format(new Date()))
              .append("_")
              .append(studyId)
              .append("_")
              .append(activityId)
              .append("_")
              .append(activityRunId)
              .append("_")
              .append(participantId)
              .append("_")
              .append(version)
              .append(".json")
              .toString();
    } catch (Exception e) {
      LOGGER.error("ERROR: StudyMetaDataUtil - getStandardFileNameForResponses()", e);
    }
    LOGGER.exit("getStandardFileNameForResponses() :: ends");
    return fileName;
  }

  public static String getResources(String bucketName, String filepath, String dataFormat) {
    try {
      if (StringUtils.isNotBlank(filepath)) {
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(BlobId.of(bucketName, filepath));
        if (blob != null) {
          ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
          blob.downloadTo(outputStream);
          return dataFormat + java.util.Base64.getEncoder().encodeToString(blob.getContent());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Unable to getResources", e);
    }
    return null;
  }
}
