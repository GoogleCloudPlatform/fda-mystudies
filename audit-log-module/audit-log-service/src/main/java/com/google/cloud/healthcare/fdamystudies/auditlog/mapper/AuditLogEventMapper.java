package com.google.cloud.healthcare.fdamystudies.auditlog.mapper;

import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import java.time.Instant;

public final class AuditLogEventMapper {

  private AuditLogEventMapper() {}

  public static AuditLogEventEntity fromAuditLogEventRequest(AuditLogEventRequest aleRequest) {
    AuditLogEventEntity entity = new AuditLogEventEntity();
    entity.setAccessLevel(aleRequest.getAccessLevel());
    entity.setAlert(aleRequest.getAlert());
    entity.setAppId(aleRequest.getAppId());
    entity.setApplicationComponentName(aleRequest.getApplicationComponentName());
    entity.setApplicationVersion(aleRequest.getApplicationVersion());
    entity.setClientId(aleRequest.getClientId());
    entity.setCorrelationId(aleRequest.getCorrelationId());
    entity.setClientAccessLevel(aleRequest.getClientAccessLevel());
    entity.setClientAppVersion(aleRequest.getClientAppVersion());
    entity.setCreatedTimestamp(Instant.now().toEpochMilli());
    entity.setDescription(aleRequest.getDescription());
    entity.setDevicePlatform(aleRequest.getDevicePlatform());
    entity.setDeviceType(aleRequest.getDeviceType());
    entity.setEventDetail(aleRequest.getEventDetail());
    entity.setEventName(aleRequest.getEventName());
    entity.setEventTimestamp(aleRequest.getEventTimestamp());
    entity.setOrgId(aleRequest.getOrgId());
    entity.setRequestUri(aleRequest.getRequestUri());
    entity.setResourceServer(aleRequest.getResourceServer());
    entity.setSystemId(aleRequest.getSystemId());
    entity.setSystemIp(aleRequest.getSystemIp());
    entity.setUserId(aleRequest.getUserId());
    return entity;
  }

  public static AuditLogEventResponse toAuditLogEventResponse(AuditLogEventEntity eventEntity) {
    AuditLogEventResponse response = new AuditLogEventResponse();
    response.setEventId(eventEntity.getId());
    return response;
  }
}
