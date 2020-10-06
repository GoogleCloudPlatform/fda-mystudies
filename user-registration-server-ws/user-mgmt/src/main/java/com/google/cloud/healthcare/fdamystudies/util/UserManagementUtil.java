/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuthRegistrationResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuthServerRegistrationBody;
import com.google.cloud.healthcare.fdamystudies.beans.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeleteAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfo;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBodyProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.exceptions.UnAuthorizedRequestException;

@Component
public class UserManagementUtil {

  private static final Logger logger = LoggerFactory.getLogger(UserManagementUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("UserManagementUtil validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_TOKEN, clientToken);
      headers.set(AppConstants.USER_ID, userId);
      headers.set(AppConstants.ACCESS_TOKEN, accessToken);

      requestBody = new HttpEntity<>(null, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerAccessTokenValidationUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);

      value = (Integer) responseEntity.getBody();
    } catch (Exception e) {
      logger.error("UserManagementUtil validateAccessToken() - error ", e);
    }
    logger.info("UserManagementUtil validateAccessToken() - ends ");
    return value;
  }

  public String changePassword(
      String userId, String clientToken, String oldPassword, String newPassword) {
    logger.info("UserManagementUtil changePassword() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    ChangePasswordBean providerBody = null;
    HttpEntity<ChangePasswordBean> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    String respMessage = "";
    ResponseBean responseBean = new ResponseBean();
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_TOKEN, clientToken);
      headers.set(AppConstants.USER_ID, userId);

      providerBody = new ChangePasswordBean();
      providerBody.setCurrentPassword(oldPassword);
      providerBody.setNewPassword(newPassword);

      requestBody = new HttpEntity<>(providerBody, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUrl() + "/changePassword",
              HttpMethod.POST,
              requestBody,
              Integer.class);
      value = (Integer) responseEntity.getBody();

      if (value == 1) {
        responseBean.setMessage(AppConstants.SUCCESS);
      } else {
        responseBean.setMessage(AppConstants.FAILURE);
      }
    } catch (Exception e) {
      logger.error("UserManagementUtil changePassword() - error ", e);
    }
    logger.info("UserManagementUtil changePassword() - ends ");
    return respMessage;
  }

  public UpdateAccountInfoResponseBean updateUserInfoInAuthServer(
      UpdateAccountInfo accountInfo, String userId) {
    logger.info("(Util)....UserManagementUtil.updateUserInfoInAuthServer()......STARTED");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AppConstants.USER_ID, userId);

    HttpEntity<UpdateAccountInfo> request = new HttpEntity<>(accountInfo, headers);
    ResponseEntity<?> responseEntity = null;

    try {
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUpdateStatusUrl(), HttpMethod.POST, request, String.class);
      return new UpdateAccountInfoResponseBean(
          responseEntity.getStatusCodeValue(), responseEntity.getStatusCode().getReasonPhrase());

    } catch (RestClientResponseException e) {
      logger.info("(Util)....UserRegistrationController.updateUserInfoInAuthServer()......ENDED");
      return new UpdateAccountInfoResponseBean(
          HttpStatus.INTERNAL_SERVER_ERROR.value(),
          HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
  }

  public DeleteAccountInfoResponseBean deleteUserInfoInAuthServer(
      String userId, String clientToken, String accessToken) {
    logger.info("(Util)....UserRegistrationController.deleteUserInfoInAuthServer()......STARTED");

    DeleteAccountInfoResponseBean authResponse = null;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AppConstants.CLIENT_TOKEN, clientToken);
    headers.set(AppConstants.USER_ID, userId);
    headers.set(AppConstants.ACCESS_TOKEN, accessToken);

    HttpEntity<?> request = new HttpEntity<>(null, headers);
    ObjectMapper objectMapper = null;

    try {
      ResponseEntity<?> responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerDeleteStatusUrl(), HttpMethod.DELETE, request, String.class);

      if (responseEntity.getStatusCode() == HttpStatus.OK) {
        String body = (String) responseEntity.getBody();
        objectMapper = new ObjectMapper();
        try {
          authResponse = objectMapper.readValue(body, DeleteAccountInfoResponseBean.class);
          return authResponse;
        } catch (JsonParseException e) {
          return authResponse;
        } catch (JsonMappingException e) {
          return authResponse;
        } catch (IOException e) {
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
          if (AppConstants.STATUS.equals(entry.getKey())) {
            authResponse.setCode(entry.getValue().get(0));
          }
          if (AppConstants.STATUS_MESSAGE.equals(entry.getKey())) {
            authResponse.setMessage(entry.getValue().get(0));
          }
        }
        authResponse.setHttpStatusCode(401 + "");

      } else if (e.getRawStatusCode() == 500) {
        authResponse = new DeleteAccountInfoResponseBean();
        authResponse.setHttpStatusCode(500 + "");

      } else {
        Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
        authResponse = new DeleteAccountInfoResponseBean();
        for (Entry<String, List<String>> entry : headerSet) {
          if (AppConstants.STATUS.equals(entry.getKey())) {
            authResponse.setCode(entry.getValue().get(0));
          }
          if (AppConstants.STATUS_MESSAGE.equals(entry.getKey())) {
            authResponse.setMessage(entry.getValue().get(0));
          }
        }
        authResponse.setHttpStatusCode(400 + "");
      }

      return authResponse;
    }
  }

  public AuthRegistrationResponseBean registerUserInAuthServer(
      UserRegistrationForm userForm,
      String appId,
      String orgId,
      String clientId,
      String secretKey) {
    logger.info("UserManagementUtil.registerUserInAuthServer......Starts");
    AuthRegistrationResponseBean authServerResponse = null;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AppConstants.APP_ID, appId);
    headers.set(AppConstants.ORGANIZATION_ID, orgId);
    headers.set(AppConstants.CLIENT_ID, clientId);
    headers.set(AppConstants.SECRET_KEY, secretKey);

    AuthServerRegistrationBody providerBody = new AuthServerRegistrationBody();
    providerBody.setEmailId(userForm.getEmailId());
    providerBody.setPassword(userForm.getPassword());

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
        } catch (JsonParseException e) {
          return authServerResponse;
        } catch (JsonMappingException e) {
          return authServerResponse;
        } catch (IOException e) {
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

          if (AppConstants.STATUS.equals(entry.getKey())) {
            authServerResponse.setCode(entry.getValue().get(0));
          }

          if (AppConstants.TITLE.equals(entry.getKey())) {
            authServerResponse.setTitle(entry.getValue().get(0));
          }
          if (AppConstants.STATUS_MESSAGE.equals(entry.getKey())) {
            authServerResponse.setMessage(entry.getValue().get(0));
          }
        }
        authServerResponse.setHttpStatusCode(401 + "");

      } else if (e.getRawStatusCode() == 500) {
        authServerResponse = new AuthRegistrationResponseBean();
        authServerResponse.setHttpStatusCode(500 + "");

      } else {
        Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
        authServerResponse = new AuthRegistrationResponseBean();
        for (Entry<String, List<String>> entry : headerSet) {

          if (AppConstants.STATUS.equals(entry.getKey())) {
            authServerResponse.setCode(entry.getValue().get(0));
          }

          if (AppConstants.TITLE.equals(entry.getKey())) {
            authServerResponse.setTitle(entry.getValue().get(0));
          }
          if (AppConstants.STATUS_MESSAGE.equals(entry.getKey())) {
            authServerResponse.setMessage(entry.getValue().get(0));
          }
        }
        authServerResponse.setHttpStatusCode(400 + "");
      }
      return authServerResponse;
    }
  }

  public String deactivateAcct(String userId, String accessToken, String clientToken) {
    logger.info("UserManagementUtil deactivateAcct() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider bodyProvider = new BodyForProvider();
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    String respMessage = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_TOKEN, clientToken);
      headers.set(AppConstants.USER_ID, userId);
      headers.set(AppConstants.ACCESS_TOKEN, accessToken);

      requestBody = new HttpEntity<>(null, headers);
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerDeactivateUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);
      value = (Integer) responseEntity.getBody();
      if (value == 1) {
        respMessage = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }

    } catch (Exception e) {
      logger.error("UserManagementUtil deactivateAcct() - error ", e);
    }
    logger.info("UserManagementUtil deactivateAcct() - Ends ");
    return respMessage;
  }

  public static Date getCurrentUtilDateTime() {
    Date date = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate.getTime());
    try {
      date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateNow);
    } catch (Exception e) {
      logger.info("UserManagementUtil - getCurrentUtilDateTime() :: ERROR ", e);
    }
    return date;
  }

  public static String getHashedValue(String input) {
    String generatedHash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(input.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.error("No Such Algorithm Exception: ", e);
    }
    return generatedHash;
  }

  public String withdrawParticipantFromStudy(String participantId, String studyId, String delete)
      throws UnAuthorizedRequestException, InvalidRequestException, SystemException {
    logger.info("UserManagementUtil withDrawParticipantFromStudy() - starts ");
    HttpHeaders headers = null;
    HttpEntity<WithdrawFromStudyBodyProvider> request = null;

    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.APPLICATION_ID, null);
      headers.set(AppConstants.CLIENT_ID, appConfig.getClientId());
      headers.set(AppConstants.SECRET_KEY, getHashedValue(appConfig.getSecretKey()));

      request = new HttpEntity<>(null, headers);

      String url =
          appConfig.getWithdrawStudyUrl()
              + "?studyId="
              + studyId
              + "&participantId="
              + participantId
              + "&deleteResponses="
              + String.valueOf(delete);

      ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
      }

    } catch (RestClientResponseException e) {
      message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
      if (e.getRawStatusCode() == 401) {
        logger.error("Invalid client Id or client secret.");
        throw new UnAuthorizedRequestException();
      } else if (e.getRawStatusCode() == 400) {
        logger.error("Client verification ended with Bad Request");
        throw new InvalidRequestException();
      } else {
        logger.error("Client verification ended with Internal Server Error");
        throw new SystemException();
      }
    }
    logger.info("UserManagementUtil withDrawParticipantFromStudy() - Ends ");
    return message;
  }

  public static String genarateEmailContent(String emailContentName, Map<String, String> keyValue) {
    logger.info("UserManagementUtil - genarateEmailContent() :: Starts");
    if (StringUtils.isNotEmpty(emailContentName)) {
      for (Map.Entry<String, String> entry : keyValue.entrySet()) {
        emailContentName =
            emailContentName.replace(
                entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
      }
    }
    logger.info("UserManagementUtil - genarateEmailContent() :: Ends");
    return emailContentName;
  }
}
