package com.google.cloud.healthcare.fdamystudies.common;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EnrollAuditEventHelper {

  @Autowired AuditEventService auditService;

  @Autowired private CommonApplicationPropertyConfig commonPropConfig;

  public void logEvent(AuditLogEvent eventEnum, AuditLogEventRequest auditRequest) {
    logEvent(eventEnum, auditRequest, null);
  }

  public void logEvent(
      AuditLogEvent eventEnum, AuditLogEventRequest auditRequest, Map<String, String> values) {
    String description = eventEnum.getDescription();
    if (values != null) {
      description = PlaceholderReplacer.replaceNamedPlaceholders(description, values);
    }
    auditRequest.setDescription(description);

    auditRequest =
        AuditEventMapper.fromAuditLogEventEnumAndCommonPropConfig(
            eventEnum, commonPropConfig, auditRequest);
    auditService.postAuditLogEvent(auditRequest);
  }
}
