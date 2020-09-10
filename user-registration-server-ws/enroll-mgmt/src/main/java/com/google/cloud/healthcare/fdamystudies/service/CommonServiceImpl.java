/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
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
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("CommonServiceImpl validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;
    BodyForProvider providerBody = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("clientToken", clientToken);
      headers.set("userId", userId);
      headers.set("accessToken", accessToken);

      requestBody = new HttpEntity<>(null, headers);
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerAccessTokenValidationUrl(),
              HttpMethod.POST,
              requestBody,
              Integer.class);
      value = (Integer) responseEntity.getBody();
    } catch (Exception e) {
      logger.error("CommonServiceImpl validateAccessToken() - error ", e);
    }
    logger.info("CommonServiceImpl validateAccessToken() - ends ");
    return value;
  }

  @Override
  public UserDetailsBO getUserInfoDetails(String userId) {
    logger.info("CommonServiceImpl getUserInfoDetails() - Starts ");
    UserDetailsBO userDetailsBO = null;
    try {
      if (!StringUtils.isEmpty(userId)) {
        userDetailsBO = commonDao.getUserInfoDetails(userId);
      }
    } catch (Exception e) {
      logger.error("CommonServiceImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonServiceImpl getUserInfoDetails() - Ends ");
    return userDetailsBO;
  }

  @Override
  public List<ActivityLogBO> createActivityLogList(
      String userId, String activityName, List<String> activityDescList) {
    logger.info("CommonServiceImpl createActivityLogList() - starts ");
    List<ActivityLogBO> activityLogBoList = new LinkedList<>();
    if (!StringUtils.isBlank(userId)
        && !StringUtils.isBlank(activityName)
        && !CollectionUtils.isEmpty(activityDescList)) {
      try {
        activityLogBoList = commonDao.createActivityLogList(userId, activityName, activityDescList);
      } catch (Exception e) {
        logger.error("CommonServiceImpl createActivityLogList() - error ", e);
      }
    }
    logger.info("CommonServiceImpl createActivityLogList() - ends ");
    return activityLogBoList;
  }

  @Override
  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("CommonServiceImpl createActivityLog() - starts ");
    ActivityLogBO activityLog = new ActivityLogBO();
    if (!StringUtils.isBlank(userId)
        && !StringUtils.isBlank(activityName)
        && !StringUtils.isBlank(activtyDesc)) {
      try {
        activityLog.setAuthUserId(userId);
        activityLog.setActivityName(activityName);
        activityLog.setActivtyDesc(activtyDesc);
        activityLog.setActivityDateTime(LocalDateTime.now());
        commonDao.createActivityLog(userId, activityName, activtyDesc);
      } catch (Exception e) {
        logger.error("CommonServiceImpl createActivityLog() - error ", e);
      }
    }
    logger.info("CommonServiceImpl createActivityLog() - ends ");
    return activityLog;
  }

  @Override
  public boolean validateServerClientCredentials(String clientId, String clientSecret)
      throws SystemException, UnAuthorizedRequestException, InvalidRequestException {

    HttpHeaders headers = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_ID, clientId);
      headers.set(AppConstants.SECRET_KEY, clientSecret);
      requestBody = new HttpEntity<>(null, headers);
      logger.debug("CommonServiceImpl validateServerClientCredentials(): starts");
      responseEntity =
          restTemplate.exchange(
              appConfig.getAuthServerClientValidationUrl(),
              HttpMethod.POST,
              requestBody,
              String.class);
      HttpStatus status = responseEntity.getStatusCode();
      if (status == HttpStatus.OK) {
        return true;
      }
    } catch (RestClientResponseException e) {

      if (e.getRawStatusCode() == 401) {
        logger.error("Invalid client Id or client secret. Client id is: " + clientId);
        throw new UnAuthorizedRequestException();
      } else if (e.getRawStatusCode() == 400) {
        logger.error("Client verification ended with Bad Request");
        throw new InvalidRequestException();
      } else {
        throw new SystemException();
      }
    } catch (Exception e) {
      logger.error("CommonServiceImpl validateServerClientCredentials - error ", e);
      throw new SystemException();
    }
    return false;
  }
}
