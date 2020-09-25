package com.google.cloud.healthcare.fdamystudies.common;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import java.util.Map;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResponseServerAuditLogHelper {

  private XLogger logger = XLoggerFactory.getXLogger(ResponseServerAuditLogHelper.class.getName());

  @Autowired AuditEventService auditService;

  @Autowired private CommonApplicationPropertyConfig commonPropConfig;

  public void logEvent(AuditLogEvent eventEnum, AuditLogEventRequest auditRequest) {
    logEvent(eventEnum, auditRequest, null);
  }

  public void logEvent(
      AuditLogEvent eventEnum, AuditLogEventRequest auditRequest, Map<String, String> values) {
    logger.entry(
        String.format("call post audit log event for eventCode=%s", eventEnum.getEventCode()));

    String description = eventEnum.getDescription();
    if (values != null) {
      description = PlaceholderReplacer.replaceNamedPlaceholders(description, values);
    }
    auditRequest.setDescription(description);
    auditRequest =
        AuditEventMapper.fromAuditLogEventEnumAndCommonPropConfig(
            eventEnum, commonPropConfig, auditRequest);
    auditService.postAuditLogEvent(auditRequest);

    logger.exit(String.format("%s event successfully logged", eventEnum.getEventCode()));
  }
}
