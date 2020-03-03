/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.AuthRegistrationResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.AuthServerRegistrationBody;
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.bean.ChangePasswordBean;
import com.google.cloud.healthcare.fdamystudies.bean.DeleteAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;

@Component
public class URWebAppWSUtil {

  private URWebAppWSUtil() {}

  private static final Logger logger = LogManager.getLogger(URWebAppWSUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationConfiguratation appConfig;

  public static String getEncryptedPassword(String input) {
    String hashedPassword = null;
    logger.info("getEncryptedPassword(): start");
    if (input != null) {
      try {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        hashedPassword = passwordEncoder.encode(input);
      } catch (Exception e) {
        logger.error(" getEncryptedPassword() - error() ", e);
      }
    }
    logger.info("getEncryptedPassword() : end");
    return hashedPassword;
  }

  /**
   * @param input
   * @return {@link BCryptPasswordEncoder}
   */
  /* getEncodedString(String test) method returns Encoded String */
  public static Boolean compairEncryptedPassword(String dbEncryptPassword, String uiPassword) {
    Boolean isMatch = false;
    logger.info(" compairEncryptedPassword() : start");
    try {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

      isMatch = passwordEncoder.matches(uiPassword, dbEncryptPassword);
    } catch (Exception e) {
      logger.error(" compairEncryptedPassword() - error ", e);
    }
    logger.info(" compairEncryptedPassword() : end");
    return isMatch;
  }

  public static String getCurrentDateTime() {
    Calendar currentDate = Calendar.getInstance();
    SimpleDateFormat formatter = URWebAppWSConstants.DB_SDF_DATE_TIME;
    formatter.setTimeZone(TimeZone.getTimeZone(AppConstants.KEY_NEW_YORK_TIME_ZONE));
    return formatter.format(currentDate.getTime());
  }

  public static String addHours(String dtStr, int hours) {
    String newdateStr = "";
    try {
      Date dt = URWebAppWSConstants.DB_SDF_DATE_TIME.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.HOUR, hours);
      Date newDate = cal.getTime();
      newdateStr = URWebAppWSConstants.DB_SDF_DATE_TIME.format(newDate);
    } catch (ParseException e) {
      logger.error("ERROR :: URWebAppWSUtil - addHours", e);
    }
    return newdateStr;
  }

  public static String genarateEmailContent(String emailContentName, Map<String, String> keyValue) {
    logger.info("URWebAppWSUtil - genarateEmailContent() start");
    try {
      if (!StringUtils.isEmpty(emailContentName)) {
        for (Map.Entry<String, String> entry : keyValue.entrySet()) {
          emailContentName =
              emailContentName.replace(
                  entry.getKey(), StringUtils.isEmpty(entry.getValue()) ? "" : entry.getValue());
        }
      }
    } catch (Exception e) {
      logger.error("URWebAppWSUtil - genarateEmailContent() - error() ", e);
    }
    logger.info("URWebAppWSUtil - genarateEmailContent() end");
    return emailContentName;
  }

  public static boolean compareDateWithCurrentDateTime(String inputDate, String inputFormat) {
    boolean flag = false;
    final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
    TimeZone.setDefault(TimeZone.getTimeZone(AppConstants.KEY_NEW_YORK_TIME_ZONE));
    sdf.setTimeZone(TimeZone.getTimeZone(AppConstants.KEY_NEW_YORK_TIME_ZONE));
    try {
      if (new Date().before(sdf.parse(inputDate))) {
        flag = true;
      }
      String date = getCurrentDateTime();
      if (new SimpleDateFormat(inputFormat)
          .parse(inputDate)
          .equals(new SimpleDateFormat(inputFormat).parse(date))) {
        flag = true;
      }
    } catch (ParseException e) {
      logger.error("URWebAppWSUtil - compareDateWithCurrentDateTime() - error() ", e);
    }
    return flag;
  }

  public static String addMinutes(String dtStr, int minutes) {
    String newdateStr = "";
    try {
      SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date dt = date.parse(dtStr);
      Calendar cal = Calendar.getInstance();
      cal.setTime(dt);
      cal.add(Calendar.MINUTE, minutes);
      Date newDate = cal.getTime();
      newdateStr = date.format(newDate);
    } catch (ParseException e) {
      logger.error("URWebAppWSUtil - compareDateWithCurrentDateTime() - error() ", e);
    }
    return newdateStr;
  }

  /*Coverting DB Date Format (yyyy-MM-dd HH:mm:ss)  to UI Date Format (MM/dd/yyyy)*/
  public static String getFormattedDate(String inputDate, String inputFormat, String outputFormat) {
    String finalDate = "";
    java.sql.Date formattedDate;
    if (inputDate != null && !"".equals(inputDate) && !"null".equalsIgnoreCase(inputDate)) {
      try {
        SimpleDateFormat formatter = new SimpleDateFormat(inputFormat);
        formattedDate = new java.sql.Date(formatter.parse(inputDate).getTime());
        formatter = new SimpleDateFormat(outputFormat);
        finalDate = formatter.format(formattedDate);
      } catch (Exception e) {
        logger.error("Exception in getFormattedDate(): ", e);
      }
    }
    return finalDate;
  }

  public static String getCurrentDateTime(String pattern) {
    logger.info("INFO: URWebAppWSUtil - getCurrentDateTime() :: starts");
    String currentDateTime = "";
    try {
      LocalDateTime now = LocalDateTime.now(ZoneId.of(AppConstants.KEY_NEW_YORK_TIME_ZONE));
      DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(pattern);
      currentDateTime = now.format(datePattern);
    } catch (Exception e) {
      logger.error("ERROR: URWebAppWSUtil - getCurrentDateTime()", e);
    }
    logger.info("INFO: URWebAppWSUtil - getCurrentDateTime() :: ends");
    return currentDateTime;
  }

  public String changePassword(
      String userId,
      int appId,
      int orgId,
      String clientId,
      String secretkey,
      ChangePasswordBean changePasswordBean) {
    logger.info("UserManagementUtil changePassword() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    String respMessage = "";
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("appId", String.valueOf(appId));
      headers.set("orgId", String.valueOf(orgId));
      headers.set("userId", userId);
      headers.set("clientId", clientId);
      headers.set("secretKey", secretkey);

      providerBody = new BodyForProvider();
      providerBody.setChangePasswordBean(changePasswordBean);

      requestBody = new HttpEntity<>(providerBody, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUrl() + "/changePassword",
              HttpMethod.POST,
              requestBody,
              Integer.class);

      if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        ResponseBean responseBean = (ResponseBean) responseEntity.getBody();
        if (responseBean != null) {
          respMessage = responseBean.getMessage();
        }
      }
    } catch (Exception e) {
      logger.error("UserManagementUtil changePassword() - error ", e);
    }
    logger.info("UserManagementUtil changePassword() - ends ");
    return respMessage;
  }

  public AuthRegistrationResponseBean registerUserInAuthServer(SetUpAccountRequest user)
      throws SystemException {
    logger.info("URWebAppWSUtil - registerUserInAuthServer() : starts");
    AuthRegistrationResponseBean authServerResponse = null;

    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("appId", null);
      headers.set("orgId", null);
      headers.set("clientId", appConfig.getClientId());
      headers.set("secretKey", appConfig.getSecretKey());

      AuthServerRegistrationBody providerBody = new AuthServerRegistrationBody();
      providerBody.setEmailId(user.getEmail());
      providerBody.setPassword(user.getPassword());

      HttpEntity<AuthServerRegistrationBody> request = new HttpEntity<>(providerBody, headers);
      ObjectMapper objectMapper = null;
      try {
        RestTemplate template = new RestTemplate();
        ResponseEntity<?> responseEntity =
            template.exchange(
                appConfig.getAuthServerRegisterStatusUrl(), HttpMethod.POST, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
          String body = (String) responseEntity.getBody();
          objectMapper = new ObjectMapper();
          try {
            authServerResponse = objectMapper.readValue(body, AuthRegistrationResponseBean.class);
            return authServerResponse;
          } catch (IOException e) {
            logger.error("URWebAppWSUtil - registerUserInAuthServer() : error ", e);
            return authServerResponse;
          }
        } else {
          return authServerResponse;
        }
      } catch (RestClientResponseException e) {
        if (e.getRawStatusCode() == 401) {
          Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
          authServerResponse = new AuthRegistrationResponseBean();
          for (Entry<String, List<String>> entry : headerSet) {

            if ("status".equals(entry.getKey())) {
              authServerResponse.setCode(entry.getValue().get(0));
            }

            if ("title".equals(entry.getKey())) {
              authServerResponse.setTitle(entry.getValue().get(0));
            }
            if ("StatusMessage".equals(entry.getKey())) {
              authServerResponse.setMessage(entry.getValue().get(0));
            }
          }
          authServerResponse.setHttpStatusCode(401 + "");

        } else if (e.getRawStatusCode() == 500) {
          throw new SystemException();
        } else {
          Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
          authServerResponse = new AuthRegistrationResponseBean();
          for (Entry<String, List<String>> entry : headerSet) {

            if ("status".equals(entry.getKey())) {
              authServerResponse.setCode(entry.getValue().get(0));
            }

            if ("title".equals(entry.getKey())) {
              authServerResponse.setTitle(entry.getValue().get(0));
            }
            if ("StatusMessage".equals(entry.getKey())) {
              authServerResponse.setMessage(entry.getValue().get(0));
            }
          }
          authServerResponse.setHttpStatusCode(400 + "");
        }
        logger.error("URWebAppWSUtil - registerUserInAuthServer() " + authServerResponse);
        return authServerResponse;
      }
    } catch (SystemException e) {
      logger.error("URWebAppWSUtil - registerUserInAuthServer() : error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("URWebAppWSUtil - registerUserInAuthServer() : error ", e);
      throw new SystemException();
    }
  }

  public DeleteAccountInfoResponseBean deleteUserInfoInAuthServer(
      String userId, String clientToken, String accessToken) throws SystemException {
    logger.info("URWebAppWSUtil - deleteUserInfoInAuthServer() : starts");

    DeleteAccountInfoResponseBean authResponse = null;
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("userId", userId);
      headers.set("accessToken", accessToken);
      headers.set("clientToken", clientToken);

      HttpEntity<?> request = new HttpEntity<>(null, headers);
      ObjectMapper objectMapper = null;

      try {
        ResponseEntity<?> responseEntity =
            restTemplate.exchange(
                appConfig.getAuthServerDeleteStatusUrl(), HttpMethod.DELETE, request, String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {

          String body = (String) responseEntity.getBody();
          logger.info(body);

          objectMapper = new ObjectMapper();

          try {
            authResponse = objectMapper.readValue(body, DeleteAccountInfoResponseBean.class);
            return authResponse;
          } catch (IOException e) {
            logger.error("URWebAppWSUtil - deleteUserInfoInAuthServer() : error ", e);
            return authResponse;
          }
        } else {
          return authResponse;
        }
      } catch (RestClientResponseException e) {
        if (e.getRawStatusCode() == 401) {
          Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
          authResponse = new DeleteAccountInfoResponseBean();
          for (Entry<String, List<String>> entry : headerSet) {
            if ("status".equals(entry.getKey())) {
              authResponse.setCode(entry.getValue().get(0));
            }
            if ("StatusMessage".equals(entry.getKey())) {
              authResponse.setMessage(entry.getValue().get(0));
            }
          }
          authResponse.setHttpStatusCode(401 + "");

        } else if (e.getRawStatusCode() == 500) {
          throw new SystemException();
        } else {
          Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
          authResponse = new DeleteAccountInfoResponseBean();
          for (Entry<String, List<String>> entry : headerSet) {
            if ("status".equals(entry.getKey())) {
              authResponse.setCode(entry.getValue().get(0));
            }
            if ("StatusMessage".equals(entry.getKey())) {
              authResponse.setMessage(entry.getValue().get(0));
            }
          }
          authResponse.setHttpStatusCode(400 + "");
        }

        logger.error("URWebAppWSUtil.authServerResponse: : " + authResponse);
        return authResponse;
      }
    } catch (SystemException e) {
      logger.error("URWebAppWSUtil - deleteUserInfoInAuthServer() : error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("URWebAppWSUtil - deleteUserInfoInAuthServer() : error ", e);
      throw new SystemException();
    }
  }

  public static Map<String, Integer> getDefaultOnboardingStatusCountMap() {
    Map<String, Integer> map = new HashMap<>();
    for (String onboardingStatus : URWebAppWSConstants.ONBOARDING_STATUS_VALUES) {
      map.put(onboardingStatus, 0);
    }
    return map;
  }

  public static Map<String, String> onboardingStatusDBToAPIMapping() {
    Map<String, String> map = new HashMap<>();
    map.put("N", "New");
    map.put("I", "Invited");
    map.put("D", "Disabled");
    return map;
  }
}
