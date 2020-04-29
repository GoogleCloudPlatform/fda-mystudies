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
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.model.AuditLogBo;
import com.google.cloud.healthcare.fdamystudies.repository.AuditLogRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Service
public class AuditLogServiceImpl implements AuditLogService {

  private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

  @Autowired private AuditLogRepository auditLogRepository;

  @Override
  public AuditLogBo createAuditLog(
      String userId,
      String activityName,
      String activtyDesc,
      String clientId,
      String participantId,
      String studyId,
      String accessLevel) {
    logger.info("CommonServiceImpl createActivityLog() - starts");
    AuditLogBo activityLog = new AuditLogBo();
    try {
      activityLog.setAuthUserId(
          (userId == null || userId.isEmpty()) ? AppConstants.NOT_APPLICABLE : userId);
      activityLog.setServerClientId(
          (clientId == null || clientId.isEmpty()) ? AppConstants.NOT_APPLICABLE : clientId);
      activityLog.setAccessLevel(
          (accessLevel == null || accessLevel.isEmpty())
              ? AppConstants.NOT_APPLICABLE
              : accessLevel);
      activityLog.setParticipantId(
          (participantId == null || participantId.isEmpty())
              ? AppConstants.NOT_APPLICABLE
              : participantId);
      activityLog.setStudyId(
          (studyId == null || studyId.isEmpty()) ? AppConstants.NOT_APPLICABLE : studyId);
      activityLog.setActivityName(activityName);
      activityLog.setActivtyDesc(activtyDesc);
      activityLog.setActivityDateTime(LocalDateTime.now());
      auditLogRepository.save(activityLog);
    } catch (Exception e) {
      logger.error("CommonServiceImpl createActivityLog() - error ", e);
    }
    logger.info("CommonServiceImpl createActivityLog() - ends");
    return activityLog;
  }
}
