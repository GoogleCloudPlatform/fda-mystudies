/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.util;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.transaction.SystemException;
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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

@Component
public class UserManagementUtil {

  private static final Logger logger = LoggerFactory.getLogger(UserManagementUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  /*
   * This method is used to validate the user and user's current session through a REST API call to
   * AuthServer.
   */
  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("UserManagementUtil validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    // BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("clientToken", clientToken);
      headers.set("userId", userId);
      headers.set("accessToken", accessToken);

      /*providerBody = new BodyForProvider();
      providerBody.setUserId(userId);
      providerBody.setAccessToken(accessToken);*/

      requestBody = new HttpEntity<BodyForProvider>(null, headers);

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

  /*
   * This method is used to validate the user and user's current session through a REST API call to
   * AuthServer.
   */

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
      headers.set("clientToken", clientToken);
      headers.set("userId", userId);
      providerBody = new ChangePasswordBean();

      providerBody.setCurrentPassword(oldPassword);
      providerBody.setNewPassword(newPassword);

      requestBody = new HttpEntity<ChangePasswordBean>(providerBody, headers);

      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUrl() + "/changePassword",
              HttpMethod.POST,
              requestBody,
              Integer.class);
      value = (Integer) responseEntity.getBody();

      if (value == 1) {
        responseBean.setMessage("Success");
      } else {
        responseBean.setMessage("Failure");
      }
      /*if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
        ResponseBean responseBean = (ResponseBean) responseEntity.getBody();
        if (responseBean != null) {
          respMessage = responseBean.getMessage();
        }
      }*/
    } catch (Exception e) {
      logger.error("UserManagementUtil changePassword() - error ", e);
    }
    logger.info("UserManagementUtil changePassword() - ends ");
    return respMessage;
  }

  /**
   * @author Chiranjibi Dash
   * @param accountInfo
   * @param userId
   * @param accessToken
   * @param appId
   * @param orgId
   * @param clientId
   * @param secretKey
   * @return UpdateAccountInfoResponseBean
   * @throws SystemException
   */
  public UpdateAccountInfoResponseBean updateUserInfoInAuthServer(
      UpdateAccountInfo accountInfo, String userId, String accessToken, String clientToken)
      throws SystemException {
    logger.info("(Util)....UserManagementUtil.updateUserInfoInAuthServer()......STARTED");

    UpdateAccountInfoResponseBean authResponse = null;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    headers.set("accessToken", accessToken);
    headers.set("userId", userId);
    headers.set("clientToken", clientToken);

    HttpEntity<UpdateAccountInfo> request = new HttpEntity<UpdateAccountInfo>(accountInfo, headers);

    ObjectMapper objectMapper = null;
    try {

      logger.info("AUTH SERVER CALL WITH REQUEST: " + request);
      ResponseEntity<?> responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUpdateStatusUrl(), HttpMethod.POST, request, String.class);
      logger.info("StatusCode: " + responseEntity.getStatusCode());
      logger.info("responseEntity: " + responseEntity);

      if (responseEntity.getStatusCode() == HttpStatus.OK) {

        String body = (String) responseEntity.getBody();
        logger.info(body);

        objectMapper = new ObjectMapper();

        try {
          authResponse = objectMapper.readValue(body, UpdateAccountInfoResponseBean.class);
          logger.info("authResponse: " + authResponse);
          logger.info("(Util)....UserManagementUtil.updateUserInfoInAuthServer()......ENDED");
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

      logger.error("Headers: " + e.getResponseHeaders());
      if (e.getRawStatusCode() == 401) {
        Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
        authResponse = new UpdateAccountInfoResponseBean();
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
        logger.error("Internal Server Error: 500");
        authResponse = new UpdateAccountInfoResponseBean();
        authResponse.setHttpStatusCode(500 + "");

      } else {
        Set<Entry<String, List<String>>> headerSet = e.getResponseHeaders().entrySet();
        authResponse = new UpdateAccountInfoResponseBean();
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
      logger.error("update user information authResponse: " + authResponse);
      return authResponse;
    }
  }

  /**
   * @author Chiranjibi Dash
   * @param userId
   * @param appId
   * @param orgId
   * @param clientId
   * @param secretKey
   * @return DeleteAccountInfoResponseBean
   * @throws SystemException
   */
  public DeleteAccountInfoResponseBean deleteUserInfoInAuthServer(
      String userId, String clientToken, String accessToken) throws SystemException {
    logger.info("(Util)....UserRegistrationController.deleteUserInfoInAuthServer()......STARTED");

    DeleteAccountInfoResponseBean authResponse = null;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("userId", userId);
    headers.set("clientToken", clientToken);
    headers.set("accessToken", accessToken);

    HttpEntity<?> request = new HttpEntity<>(null, headers);
    ObjectMapper objectMapper = null;

    try {
      logger.info("AUTH SERVER CALL WITH REQUEST: " + request);
      logger.info("URL is:" + appConfig.getAuthServerDeleteStatusUrl());
      ResponseEntity<?> responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerDeleteStatusUrl(), HttpMethod.DELETE, request, String.class);

      if (responseEntity.getStatusCode() == HttpStatus.OK) {

        String body = (String) responseEntity.getBody();
        logger.info(body);

        objectMapper = new ObjectMapper();

        try {
          authResponse = objectMapper.readValue(body, DeleteAccountInfoResponseBean.class);
          logger.info("authResponse: " + authResponse);
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
          if ("status".equals(entry.getKey())) {
            authResponse.setCode(entry.getValue().get(0));
          }
          if ("StatusMessage".equals(entry.getKey())) {
            authResponse.setMessage(entry.getValue().get(0));
          }
        }
        authResponse.setHttpStatusCode(401 + "");

      } else if (e.getRawStatusCode() == 500) {
        logger.error("Internal Server Error: 500");
        authResponse = new DeleteAccountInfoResponseBean();
        authResponse.setHttpStatusCode(500 + "");

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

      logger.error("authResponse: " + authResponse);
      return authResponse;
    }
  }

  /**
   * @author Chiranjibi Dash
   * @param userForm
   * @param appId
   * @param orgId
   * @param clientId
   * @param secretKey
   * @return AuthRegistrationResponseBean
   * @throws SystemException
   */
  public AuthRegistrationResponseBean registerUserInAuthServer(
      UserRegistrationForm userForm, String appId, String orgId, String clientId, String secretKey)
      throws SystemException {
    logger.info("(Util)....UserRegistrationController.registerUserInAuthServer......STARTED");
    // String url = "http://localhost:8000/authServer/register";
    AuthRegistrationResponseBean authServerResponse = null;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("appId", appId);
    headers.set("orgId", orgId);
    headers.set("clientId", clientId);
    headers.set("secretKey", secretKey);

    AuthServerRegistrationBody providerBody = new AuthServerRegistrationBody();
    providerBody.setEmailId(userForm.getEmailId());
    providerBody.setPassword(userForm.getPassword());
    /*
     * if (userForm != null) { providerBody.setEmailId(userForm.getEmailId());
     * providerBody.setPassword(userForm.getPassword()); } else { authServerResponse = new
     * AuthRegistrationResponseBean(); authServerResponse.setCode(ErrorCode.EC_128.code() + "");
     * authServerResponse.setMessage(ErrorCode.EC_128.errorMessage()); return authServerResponse; }
     */

    logger.info("userForm: " + userForm);
    HttpEntity<AuthServerRegistrationBody> request =
        new HttpEntity<AuthServerRegistrationBody>(providerBody, headers);
    logger.info("AUTH SERVER CALL WITH REQUEST: " + request);
    ObjectMapper objectMapper = null;
    try {
      logger.info("URL is: " + appConfig.getAuthServerRegisterStatusUrl());
      RestTemplate template = new RestTemplate();
      ResponseEntity<?> responseEntity =
          template.exchange(
              appConfig.getAuthServerRegisterStatusUrl(), HttpMethod.POST, request, String.class);

      if (responseEntity.getStatusCode() == HttpStatus.OK) {
        String body = (String) responseEntity.getBody();
        objectMapper = new ObjectMapper();
        try {
          authServerResponse = objectMapper.readValue(body, AuthRegistrationResponseBean.class);
          logger.info("authResponse: " + authServerResponse);
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
        logger.error("Internal Server Error: 500");
        authServerResponse = new AuthRegistrationResponseBean();
        authServerResponse.setHttpStatusCode(500 + "");

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
      logger.error("authServerResponse: " + authServerResponse);
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
      headers.set("userId", userId);
      headers.set("accessToken", accessToken);
      headers.set("clientToken", clientToken);
      requestBody = new HttpEntity<BodyForProvider>(bodyProvider, headers);
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerUrl() + "/deactivate",
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
}
