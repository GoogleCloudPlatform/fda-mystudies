package com.google.cloud.healthcare.fdamystudies.auditlog.service;

import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.ALERT;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.APP_ID;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.EVENT_INFO;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.EVENT_NAME;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.EVENT_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.ORG_ID;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.PLATFORM_VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.SYSTEM_ID;
import static com.google.cloud.healthcare.fdamystudies.common.AuditLogConstants.USER_ID;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.auditlog.repository.AuditLogEventRepository;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;

@Service
public class AuditLogEventServiceImpl extends BaseServiceImpl implements AuditLogEventService {

  private XLogger logger = XLoggerFactory.getXLogger(AuditLogEventServiceImpl.class.getName());

  @Autowired private AuditLogEventRepository repository;

  @Value("${auditlog.platform-version}")
  private String platformVersion;

  @Override
  public long saveAuditLogEvent(JsonNode eventParams) {
    logger.entry(String.format("begin saveAuditLogEvent() with eventParams=%s", eventParams));
    AuditLogEventEntity event = new AuditLogEventEntity();

    event.setCorrelationId(getTextValue(eventParams, CORRELATION_ID));
    event.setEventName(getTextValue(eventParams, EVENT_NAME));
    event.setSystemId(getTextValue(eventParams, SYSTEM_ID));
    event.setAppId(getTextValue(eventParams, APP_ID));
    event.setOrgId(getTextValue(eventParams, ORG_ID));
    event.setUserId(getTextValue(eventParams, USER_ID));
    event.setCreatedTimestamp(DateTimeUtils.getSystemDateTimestamp());
    event.setEventTimestamp(eventParams.get(EVENT_TIMESTAMP).longValue());
    event.setAlert(eventParams.get(ALERT).booleanValue());

    ObjectNode eventInfo = getObjectNode();
    eventInfo.put(PLATFORM_VERSION, platformVersion);
    eventInfo.setAll((ObjectNode) eventParams.get(EVENT_INFO));
    event.setEventInfo(eventInfo.toString());

    event = repository.saveAndFlush(event);
    logger.exit(String.format("event_id=%d", event.getId()));

    return event.getId();
  }
}
