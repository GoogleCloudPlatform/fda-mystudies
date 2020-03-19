/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private CommonDao commonDao;

  @Autowired private ApplicationConfiguration appConfig;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public boolean validateServerClientCredentials(
      String applicationId, String clientId, String clientSecret) {
    boolean isAuthenticated = false;
    try {
      isAuthenticated =
          commonDao.validateServerClientCredentials(applicationId, clientId, clientSecret);

    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl validatedAuthKey() - error ", e);
    }
    return isAuthenticated;
  }

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
      headers.set(AppConstants.CLIENT_TOKEN_KEY, clientToken);
      headers.set(AppConstants.USER_ID_KEY, userId);
      headers.set(AppConstants.ACCESS_TOKEN_KEY, accessToken);
      requestBody = new HttpEntity<BodyForProvider>(null, headers);
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
}
