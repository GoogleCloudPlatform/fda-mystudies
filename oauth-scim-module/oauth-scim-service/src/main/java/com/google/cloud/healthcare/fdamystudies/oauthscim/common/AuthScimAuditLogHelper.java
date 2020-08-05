package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.CommonApplicationPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthScimAuditLogHelper {

  @Autowired AuditEventService auditService;

  @Autowired private CommonApplicationPropertyConfig commonPropConfig;

  public void logEvent(AuditLogEvent eventEnum, AuditLogEventRequest auditRequest) {
    Map<String, String> values = new HashMap<>();
    values.put("user_id", auditRequest.getUserId());
    String description =
        PlaceholderReplacer.replaceNamedPlaceholders(eventEnum.getDescription(), values);
    auditRequest.setDescription(description);

    auditRequest =
        AuditEventMapper.fromAuditLogEventEnumAndCommonPropConfig(
            eventEnum, commonPropConfig, auditRequest);

    auditService.postAuditLogEvent(auditRequest);
  }
}
