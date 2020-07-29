/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.mapper;

import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import java.sql.Timestamp;
import java.time.Instant;

public final class AuditLogEventMapper {

  private AuditLogEventMapper() {}

  public static AuditLogEventEntity fromAuditLogEventRequest(AuditLogEventRequest auditRequest) {
    AuditLogEventEntity entity = new AuditLogEventEntity();
    entity.setCreated(new Timestamp(Instant.now().toEpochMilli()));
    entity.setAppId(auditRequest.getAppId());
    entity.setDestination(auditRequest.getDestination());
    entity.setAppVersion(auditRequest.getAppVersion());
    entity.setSource(auditRequest.getSource());
    entity.setResourceServer(auditRequest.getResourceServer());
    entity.setCorrelationId(auditRequest.getCorrelationId());
    entity.setUserAccessLevel(auditRequest.getUserAccessLevel());
    entity.setSourceApplicationVersion(auditRequest.getSourceApplicationVersion());
    entity.setDestinationApplicationVersion(auditRequest.getDestinationApplicationVersion());
    entity.setDescription(auditRequest.getDescription());
    entity.setMobilePlatform(auditRequest.getMobilePlatform());
    entity.setEventName(auditRequest.getEventName());
    entity.setEventCode(auditRequest.getEventCode());
    entity.setOccurred(auditRequest.getOccured());
    entity.setUserIp(auditRequest.getUserIp());
    entity.setUserId(auditRequest.getUserId());
    entity.setPlatformVersion(auditRequest.getPlatformVersion());
    entity.setParticipantId(auditRequest.getParticipantId());
    entity.setStudyId(auditRequest.getStudyId());
    entity.setStudyVersion(auditRequest.getStudyVersion());
    return entity;
  }

  public static AuditLogEventResponse toAuditLogEventResponse(AuditLogEventEntity eventEntity) {
    AuditLogEventResponse response = new AuditLogEventResponse();
    response.setEventId(eventEntity.getLogId());
    return response;
  }
}
