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

public final class AuditLogEventMapper {

  private AuditLogEventMapper() {}

  public static AuditLogEventEntity fromAuditLogEventRequest(AuditLogEventRequest auditRequest) {
    AuditLogEventEntity entity = new AuditLogEventEntity();
    entity.setAccessLevel(auditRequest.getAccessLevel());
    entity.setAlert(auditRequest.getAlert());
    entity.setAppId(auditRequest.getAppId());
    entity.setApplicationComponentName(auditRequest.getApplicationComponentName());
    entity.setApplicationVersion(auditRequest.getApplicationVersion());
    entity.setClientId(auditRequest.getClientId());
    entity.setCorrelationId(auditRequest.getCorrelationId());
    entity.setClientAccessLevel(auditRequest.getClientAccessLevel());
    entity.setClientAppVersion(auditRequest.getClientAppVersion());
    entity.setDescription(auditRequest.getDescription());
    entity.setDevicePlatform(auditRequest.getDevicePlatform());
    entity.setDeviceType(auditRequest.getDeviceType());
    entity.setEventDetail(auditRequest.getEventDetail());
    entity.setEventName(auditRequest.getEventName());
    entity.setOccurred(new Timestamp(auditRequest.getOccured()));
    entity.setOrgId(auditRequest.getOrgId());
    entity.setRequestUri(auditRequest.getRequestUri());
    entity.setResourceServer(auditRequest.getResourceServer());
    entity.setSystemId(auditRequest.getSystemId());
    entity.setSystemIp(auditRequest.getSystemIp());
    entity.setUserId(auditRequest.getUserId());
    return entity;
  }

  public static AuditLogEventResponse toAuditLogEventResponse(AuditLogEventEntity eventEntity) {
    AuditLogEventResponse response = new AuditLogEventResponse();
    response.setEventId(eventEntity.getId());
    return response;
  }
}
