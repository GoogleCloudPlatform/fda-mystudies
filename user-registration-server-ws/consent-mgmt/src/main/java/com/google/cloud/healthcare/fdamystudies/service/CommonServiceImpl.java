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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.healthcare.fdamystudies.bean.BodyForProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.AuditLogBo;
import com.google.cloud.healthcare.fdamystudies.repository.AuditLogRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired CommonDao commonDao;

  @Autowired private AuditLogRepository auditLogRepository;

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

      requestBody = new HttpEntity<BodyForProvider>(null, headers);
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
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Starts ");
    Integer userDetailId = null;
    try {
      userDetailId = commonDao.getUserDetailsId(userId);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyInfoId() - error ", e);
    }

    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Ends ");
    return userDetailId;
  }

  @Override
  public AuditLogBo createAuditLog(String userId, String event, String description) {
    logger.info("CommonServiceImpl createAuditLog() - starts ");
    AuditLogBo auditLog = new AuditLogBo();
    auditLog.setAuthUserId(StringUtils.isEmpty(userId) ? AppConstants.NOT_APPLICABLE : userId);
    auditLog.setActivityName(event);
    auditLog.setActivtyDesc(description);
    auditLog.setActivityDateTime(LocalDateTime.now());
    auditLog.setServerClientId(appConfig.getMobileAppClientId());
    try {
      auditLogRepository.save(auditLog);
    } catch (Exception e) {
      logger.error("CommonServiceImpl createAuditLog() - error ", e);
      return null;
    }
    logger.info("CommonServiceImpl createAuditLog() - ends ");

    return auditLog;
  }

  @Override
  public AuditLogBo createAuditLog(
      String userId,
      String event,
      String description,
      String accessLevel,
      String participantId,
      String studyId) {
    logger.info("CommonServiceImpl createAuditLog() - starts ");
    AuditLogBo auditLog = new AuditLogBo();
    auditLog.setAuthUserId(StringUtils.isEmpty(userId) ? AppConstants.NOT_APPLICABLE : userId);
    auditLog.setActivityName(event);
    auditLog.setActivtyDesc(description);
    auditLog.setActivityDateTime(LocalDateTime.now());
    auditLog.setAccessLevel(
        StringUtils.isEmpty(accessLevel) ? AppConstants.NOT_APPLICABLE : accessLevel);
    auditLog.setServerClientId(
        StringUtils.isEmpty(appConfig.getMobileAppClientId())
            ? AppConstants.NOT_APPLICABLE
            : appConfig.getMobileAppClientId());
    auditLog.setParticipantId(
        StringUtils.isEmpty(participantId) ? AppConstants.NOT_APPLICABLE : participantId);
    auditLog.setStudyId(StringUtils.isEmpty(studyId) ? AppConstants.NOT_APPLICABLE : studyId);
    try {
      auditLogRepository.save(auditLog);
    } catch (Exception e) {
      logger.error("CommonServiceImpl createAuditLog() - error ", e);
      return null;
    }
    logger.info("CommonServiceImpl createAuditLog() - ends ");

    return auditLog;
  }

  @Override
  public AuditLogBo createAuditLog(
      String userId, String event, String description, String accessLevel) {
    logger.info("CommonServiceImpl createAuditLog() - starts ");
    AuditLogBo auditLog =
        createAuditLog(
            userId,
            event,
            description,
            accessLevel,
            AppConstants.NOT_APPLICABLE,
            AppConstants.NOT_APPLICABLE);
    logger.info("CommonServiceImpl createAuditLog() - ends ");
    return auditLog;
  }
}
