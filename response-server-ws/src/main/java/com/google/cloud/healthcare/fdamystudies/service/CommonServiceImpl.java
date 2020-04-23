/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.time.LocalDateTime;
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
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLog;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private CommonDao commonDao;

  @Autowired private ApplicationConfiguration appConfig;

  @Autowired private ActivityLogRepository activityLogRepository;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public boolean validateServerClientCredentials(String clientId, String clientSecret)
      throws SystemException, UnAuthorizedRequestException, InvalidRequestException {

    HttpHeaders headers = null;
    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {

      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_ID_PARAM, clientId);
      headers.set(AppConstants.CLIENT_SECRET_PARAM, clientSecret);
      requestBody = new HttpEntity<>(null, headers);
      logger.debug("CommonServiceImpl validateServerClientCredentials() Begin");
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

      logger.error("ERROR: " + e.getRawStatusCode());
      logger.error("Headers: " + e.getResponseHeaders());
      logger.error("Body: " + e.getResponseBodyAsString());
      logger.error("ERROR: ", e);

      if (e.getRawStatusCode() == 401) {
        logger.error("Invalid client credentials.");
        throw new UnAuthorizedRequestException();
      } else if (e.getRawStatusCode() == 400) {
        logger.error("Client credentials verification error.");
        throw new InvalidRequestException();
      } else {
        throw new SystemException();
      }
    }
    logger.error("Invalid client credentials.");
    throw new SystemException();
  }

  @Override
  public Integer validateAccessToken(String userId, String accessToken, String clientToken) {
    logger.info("CommonServiceImpl validateAccessToken() - starts ");
    Integer value = null;
    HttpHeaders headers = null;

    HttpEntity<BodyForProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.CLIENT_TOKEN_KEY, clientToken);
      headers.set(AppConstants.USER_ID_KEY, userId);
      headers.set(AppConstants.ACCESS_TOKEN_KEY, accessToken);
      requestBody = new HttpEntity<>(null, headers);
      logger.debug("CommonServiceImpl validateAccessToken() Begin");
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
    logger.debug("CommonServiceImpl validateAccessToken() - ends ");
    return value;
  }

  @Override
  public ParticipantBo getParticipantInfoDetails(String participantId) {
    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    ParticipantBo participantInfo = null;
    try {
      participantInfo = commonDao.getParticipantInfoDetails(participantId);
    } catch (Exception e) {
      logger.error("CommonServiceImpl getParticipantInfoDetails() - error ", e);
    }

    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    return participantInfo;
  }

  @Override
  public ActivityLog createActivityLog(
      String userId, String activityName, String activtyDesc, String clientId) {
    logger.info("CommonServiceImpl createActivityLog() - starts ");
    ActivityLog activityLog = new ActivityLog();
    try {
      activityLog.setAuthUserId(userId);
      activityLog.setActivityName(activityName);
      activityLog.setActivtyDesc(activtyDesc);
      activityLog.setActivityDateTime(LocalDateTime.now());
      activityLog.setServerClientId(clientId);
      activityLogRepository.save(activityLog);

    } catch (Exception e) {
      logger.error("CommonServiceImpl createActivityLog() - error ", e);
    }
    logger.info("CommonServiceImpl createActivityLog() - ends ");

    return activityLog;
  }
}
