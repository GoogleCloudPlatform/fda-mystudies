package com.google.cloud.healthcare.fdamystudies.common;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ParticipantManagerAuditLogHelper {

  @Autowired AuditEventService aleService;

  public AuditLogEventResponse logEvent(AuditLogEvent eventEnum, AuditLogEventRequest aleRequest) {
    return aleService.postAuditLogEvent(eventEnum, aleRequest);
  }

  public AuditLogEventResponse logEvent(
      AuditLogEvent eventEnum, AuditLogEventRequest aleRequest, Map<String, String> values) {
    values.put("user_id", aleRequest.getUserId());
    String description =
        PlaceholderReplacer.replaceNamedPlaceholders(eventEnum.getDescription(), values);
    aleRequest.setDescription(description);
    return aleService.postAuditLogEvent(eventEnum, aleRequest);
  }
}
