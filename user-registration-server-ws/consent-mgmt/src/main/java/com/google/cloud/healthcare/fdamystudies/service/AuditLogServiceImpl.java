package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ACCESS_LEVEL;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ALERT;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.APPLICATION_COMPONENT_NAME;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.APPLICATION_VERSION;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.APP_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CLIENT_ACCESS_LEVEL;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CLIENT_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CLIENT_IP;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.EVENT_DETAIL;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.EVENT_INFO;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.EVENT_NAME;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.EVENT_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ORG_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PLACE_HOLDERS;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.RESOURCE_SERVER;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.ROLLBACK;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.SYSTEM_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.USER_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.RESOURCE_REQUESTING_ENTITY_SYSTM_ID;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEventStatus;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvents;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AuditLogEventRepository;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil;

@Service
public class AuditLogServiceImpl extends BaseServiceImpl implements AuditLogService {

  private static final Logger LOG = LoggerFactory.getLogger(AuditLogServiceImpl.class);

  @Value("${auditlog.events_endpoint}")
  private String eventsEndpoint;

  @Autowired RestTemplate restTemplate;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired AuditLogEventRepository auditLogEventRepository;

  @Override
  public ResponseEntity<JsonNode> logAuditEvent(AuditLogEvents eventEnum, JsonNode aleParams) {

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Authorization", getOAuthService().getAccessToken(aleParams));

    // prepare the request for POST method
    String userId = getTextValue(aleParams, USER_ID_HEADER);
    ObjectNode requestBody = prepareAuditLogEventRequest(eventEnum, aleParams, userId);

    ResponseEntity<JsonNode> aleResponse =
        exchangeForJson(eventsEndpoint, headers, requestBody, HttpMethod.POST);

    if (aleResponse.getStatusCode().is2xxSuccessful()) {
      return aleResponse;
    } else if (aleResponse.getStatusCode().is4xxClientError()) {
      if (LOG.isErrorEnabled()) {
        LOG.error(
            String.format(
                "%s event failed with status %d for user_id=%s, errors=%s",
                eventEnum.getEventName(),
                aleResponse.getStatusCodeValue(),
                userId,
                aleResponse.getBody()));
      }

      ((ObjectNode) aleResponse.getBody()).put(ROLLBACK, eventEnum.isRollback());
      return aleResponse;
    }

    // save the audit log event in application database, task scheduler will retry to send
    // the events to centralized audit log system.
    return saveAuditLogEvent(eventEnum, userId, requestBody, aleResponse);
  }

  private ResponseEntity<JsonNode> saveAuditLogEvent(
      AuditLogEvents eventEnum,
      String userId,
      ObjectNode requestBody,
      ResponseEntity<JsonNode> aleResponse) {
    ObjectNode errResponse = getObjectNode();
    ErrorCode errorCode = ErrorCode.EC_500;
    if (eventEnum.isRollback()) {
      errResponse.put(ROLLBACK, eventEnum.isRollback());
      errResponse.setAll((ObjectNode) aleResponse.getBody());
    } else {
      // save the event in app database
      long id = saveAuditLogEventEntity(requestBody, aleResponse.getStatusCodeValue());
      if (id > 0) {
        errorCode = ErrorCode.EC_202;
        if (LOG.isInfoEnabled()) {
          LOG.info(
              String.format(
                  "%s event for user_id=%s saved for task scheduler.",
                  eventEnum.getEventName(), userId));
        }
      } else {
        if (LOG.isErrorEnabled()) {
          LOG.error(
              String.format(
                  "%s event for user_id=%s not saved for task scheduler.",
                  eventEnum.getEventName(), userId));
        }
      }

      errResponse = (ObjectNode) getErrorResponse(errorCode);
    }

    return ResponseEntity.status(errorCode.statusCode()).body(errResponse);
  }

  private ObjectNode prepareAuditLogEventRequest(
      AuditLogEvents eventEnum, JsonNode params, String userId) {
    ObjectNode requestBody = getObjectNode();
    requestBody.put(USER_ID, userId);
    requestBody.put(ALERT, eventEnum.isAlert());
    requestBody.put(APP_ID, getTextValue(params, APP_ID));
    requestBody.put(ORG_ID, getTextValue(params, ORG_ID));
    requestBody.put(CORRELATION_ID, getTextValue(params, CORRELATION_ID));
    requestBody.put(EVENT_NAME, eventEnum.getEventName());
    requestBody.put(SYSTEM_ID, eventEnum.getSystemId());
    requestBody.put(EVENT_TIMESTAMP, MyStudiesUserRegUtil.getSystemDateTimestamp());

    ObjectNode eventInfo = getObjectNode();
    copyAll(
        params,
        eventInfo,
        DESCRIPTION,
        USER_ID,
        CORRELATION_ID,
        APP_ID,
        ORG_ID,
        EVENT_NAME,
        USER_ID_HEADER,
        CLIENT_IP,
        PLACE_HOLDERS,
        RESOURCE_REQUESTING_ENTITY_SYSTM_ID);
    eventInfo.put(ACCESS_LEVEL, eventEnum.getAccessLevel());
    eventInfo.put(CLIENT_ACCESS_LEVEL, eventEnum.getClientAccessLevel());
    eventInfo.put(CLIENT_ID, eventEnum.getClientId());
    eventInfo.put(RESOURCE_SERVER, eventEnum.getResourceServer());
    eventInfo.put(APPLICATION_COMPONENT_NAME, appConfig.getApplicationComponentName());
    eventInfo.put(EVENT_DETAIL, eventEnum.getEventDetail());
    eventInfo.put(APPLICATION_VERSION, appConfig.getApplicationVersion());

    // replace placeholders in description
    String description = eventEnum.getDescription();
    if (StringUtils.contains(description, "{") && StringUtils.contains(description, "}")) {
      description = StringUtils.defaultIfEmpty(getTextValue(params, DESCRIPTION), description);

      description = PlaceholderReplacer.replaceNamedPlaceholders(params, description);
    }

    eventInfo.put(DESCRIPTION, description);
    requestBody.set(EVENT_INFO, eventInfo);
    return requestBody;
  }

  private long saveAuditLogEventEntity(JsonNode eventDetails, int httpStatusCode) {
    LOG.info("--- BEGIN saveAuditLogEvent()");
    AuditLogEventEntity auditLogEventEntity = new AuditLogEventEntity();
    auditLogEventEntity.setEventDetails(eventDetails.toString());
    auditLogEventEntity.setRetryCount(0);
    auditLogEventEntity.setEventStatus(
        AuditLogEventStatus.NOT_RECORDED_AT_AUDIT_LOG_SERVICE.getStatus());
    auditLogEventEntity.setHttpStatusCode(httpStatusCode);
    auditLogEventEntity.setCreatedTimestamp(MyStudiesUserRegUtil.getSystemDateTimestamp());

    auditLogEventEntity = auditLogEventRepository.saveAndFlush(auditLogEventEntity);

    return auditLogEventEntity.getId();
  }

  private boolean updateAuditLogEventStatus(
      long aleId, AuditLogEventStatus aleStatus, int httpStatusCode) {
    LOG.info("--- BEGIN updateAuditLogEventStatus()");
    Optional<AuditLogEventEntity> record = auditLogEventRepository.findById(aleId);
    if (record.isPresent()) {
      AuditLogEventEntity auditLogEventEntity = record.get();
      auditLogEventEntity.setEventStatus(aleStatus.getStatus());
      auditLogEventEntity.setHttpStatusCode(httpStatusCode);
      auditLogEventEntity.setLastModifiedTimestamp(MyStudiesUserRegUtil.getSystemDateTimestamp());
      auditLogEventEntity.setRetryCount(auditLogEventEntity.getRetryCount() + 1);
      auditLogEventEntity = auditLogEventRepository.saveAndFlush(auditLogEventEntity);
      return (auditLogEventEntity.getEventStatus()
              == AuditLogEventStatus.RECORDED_AT_AUDIT_LOG_SERVICE.getStatus())
          ? Boolean.TRUE
          : Boolean.FALSE;
    }

    return false;
  }

  @Override
  public void resendLogAuditEvents() {
    LOG.info("--- BEGIN resendLogAuditEvents()");

    try {
      List<AuditLogEventEntity> events =
          auditLogEventRepository.findByEventStatus(
              AuditLogEventStatus.NOT_RECORDED_AT_AUDIT_LOG_SERVICE.getStatus());
      if (LOG.isInfoEnabled()) {
        LOG.info(String.format("resend %d events to centralized audit log service", events.size()));
      }

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

      for (AuditLogEventEntity auditLogEventEntity : events) {
        resendAuditLogEvent(headers, auditLogEventEntity);
      }
    } catch (Exception e) {
      LOG.error("error in retryLogAuditEvent", e);
    }
  }

  private void resendAuditLogEvent(HttpHeaders headers, AuditLogEventEntity auditLogEventEntity) {
    try {
      if (HttpStatus.valueOf(auditLogEventEntity.getHttpStatusCode()).is4xxClientError()) {
        return;
      }

      headers.set("Authorization", getOAuthService().getAccessToken(getObjectNode()));
      ResponseEntity<JsonNode> aleResponse =
          exchangeForJson(
              eventsEndpoint,
              headers,
              toJson(auditLogEventEntity.getEventDetails()),
              HttpMethod.POST);

      AuditLogEventStatus aleStatus =
          aleResponse.getStatusCode().is2xxSuccessful()
              ? AuditLogEventStatus.RECORDED_AT_AUDIT_LOG_SERVICE
              : AuditLogEventStatus.NOT_RECORDED_AT_AUDIT_LOG_SERVICE;

      updateAuditLogEventStatus(
          auditLogEventEntity.getId(), aleStatus, aleResponse.getStatusCodeValue());
    } catch (Exception e) {
      LOG.error("error in retryLogAuditEvent for event id: {}", auditLogEventEntity.getId());
    }
  }
}
