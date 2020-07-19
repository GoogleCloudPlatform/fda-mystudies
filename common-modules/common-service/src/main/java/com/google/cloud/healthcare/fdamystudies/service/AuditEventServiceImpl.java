/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.toJsonNode;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEventStatus;
import com.google.cloud.healthcare.fdamystudies.model.AuditEventEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AuditEventRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;

@Service
@ConditionalOnProperty(
    value = "commonservice.auditlogevent.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class AuditEventServiceImpl extends BaseServiceImpl implements AuditEventService {

  private XLogger logger = XLoggerFactory.getXLogger(AuditEventServiceImpl.class.getName());

  @Value("${auditlog.events_endpoint:}")
  private String eventsEndpoint;

  @Value("${auditlog.app.component.name:}")
  private String appComponentName;

  @Value("${auditlog.application.version:}")
  private String applicationVersion;

  @Autowired AuditEventRepository auditEventRepository;

  @Autowired private OAuthService oauthService;

  @Override
  @Transactional
  public AuditLogEventResponse postAuditLogEvent(
      AuditLogEvent eventEnum, AuditLogEventRequest aleRequest) {
    logger.entry(String.format("begin postAuditLogEvent() for %s event", eventEnum.getEventName()));

    // prepare the request for POST method
    aleRequest.setEventName(eventEnum.getEventName());
    aleRequest.setAlert(eventEnum.isAlert());
    aleRequest.setSystemId(eventEnum.getSystemId());
    aleRequest.setAccessLevel(eventEnum.getAccessLevel());
    aleRequest.setClientId(eventEnum.getClientId());
    aleRequest.setClientAccessLevel(eventEnum.getClientAccessLevel());
    aleRequest.setResourceServer(eventEnum.getResourceServer());
    aleRequest.setEventDetail(eventEnum.getEventDetail());
    aleRequest.setOccured(Instant.now().toEpochMilli());

    JsonNode requestBody = getObjectMapper().convertValue(aleRequest, JsonNode.class);

    AuditLogEventResponse aleResponse = callEventsApi(requestBody);

    // save the event in the application database if fallback is true
    if (eventEnum.isFallback()
        && !HttpStatus.valueOf(aleResponse.getHttpStatusCode()).is2xxSuccessful()) {
      aleResponse = saveAuditLogEvent(requestBody, aleResponse.getHttpStatusCode());
    }
    logger.exit(String.format("status=%d", aleResponse.getHttpStatusCode()));
    return aleResponse;
  }

  private AuditLogEventResponse callEventsApi(JsonNode requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + oauthService.getAccessToken());

    AuditLogEventResponse aleResponse;
    int httpStatusCode = 0;
    try {
      ResponseEntity<JsonNode> response =
          exchangeForJson(eventsEndpoint, headers, requestBody, HttpMethod.POST);
      httpStatusCode = response.getStatusCodeValue();

      if (response.getStatusCode().is2xxSuccessful()) {
        aleResponse =
            getObjectMapper().convertValue(response.getBody(), AuditLogEventResponse.class);
        aleResponse.setHttpStatusCode(response.getStatusCodeValue());
        return aleResponse;
      } else if (response.getStatusCode().is4xxClientError()) {
        logger.error(
            String.format(
                "%s failed with status=%d, response=%s",
                eventsEndpoint, httpStatusCode, response.getBody()));
      }
    } catch (RestClientResponseException e) {
      httpStatusCode = e.getRawStatusCode();
      logger.error(String.format("%s failed with status=%d", eventsEndpoint, httpStatusCode), e);
    } catch (Exception e) {
      httpStatusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
      logger.error(String.format("%s failed with status=%d", eventsEndpoint, httpStatusCode), e);
    }

    String eventName = getTextValue(requestBody, "eventName");
    return new AuditLogEventResponse(
        HttpStatus.valueOf(httpStatusCode),
        String.format(
            "%s event not received/processed by the central audit log system.", eventName));
  }

  @Transactional
  public AuditLogEventResponse saveAuditLogEvent(JsonNode requestBody, int httpStatus) {
    AuditEventEntity auditLogEventEntity = new AuditEventEntity();
    auditLogEventEntity.setEventRequest(requestBody.toString());
    auditLogEventEntity.setRetryCount(0);
    auditLogEventEntity.setStatus(
        AuditLogEventStatus.NOT_RECORDED_AT_CENTRAL_AUDIT_LOG.getStatus());
    auditLogEventEntity.setHttpStatusCode(httpStatus);
    auditEventRepository.saveAndFlush(auditLogEventEntity);
    return new AuditLogEventResponse(HttpStatus.ACCEPTED, "event saved for task scheduler");
  }

  @Transactional
  public void updateAuditLogEventStatus(
      String id, AuditLogEventStatus aleStatus, int httpStatusCode) {
    Optional<AuditEventEntity> record = auditEventRepository.findById(id);
    if (record.isPresent()) {
      AuditEventEntity aleEntity = record.get();
      aleEntity.setStatus(aleStatus.getStatus());
      aleEntity.setHttpStatusCode(httpStatusCode);
      aleEntity.setModified(new Timestamp(Instant.now().toEpochMilli()));
      aleEntity.setRetryCount(aleEntity.getRetryCount() + 1);
      auditEventRepository.saveAndFlush(aleEntity);
    }
  }

  @Override
  @Transactional
  public void resendAuditLogEvents() {
    logger.entry("begin resendLogAuditEvents() with no args");
    try {
      List<AuditEventEntity> events =
          auditEventRepository.findByStatus(
              AuditLogEventStatus.NOT_RECORDED_AT_CENTRAL_AUDIT_LOG.getStatus());

      logger.info(String.format("%d events found for scheduler task", events.size()));

      for (AuditEventEntity auditLogEventEntity : events) {

        // ignore bad events
        if (HttpStatus.BAD_REQUEST.value() == auditLogEventEntity.getHttpStatusCode()) {
          continue;
        }

        AuditLogEventResponse aleResponse =
            callEventsApi(toJsonNode(auditLogEventEntity.getEventRequest()));

        AuditLogEventStatus aleStatus =
            HttpStatus.valueOf(aleResponse.getHttpStatusCode()).is2xxSuccessful()
                ? AuditLogEventStatus.RECORDED_AT_CENTRAL_AUDIT_LOG
                : AuditLogEventStatus.NOT_RECORDED_AT_CENTRAL_AUDIT_LOG;

        updateAuditLogEventStatus(
            auditLogEventEntity.getId(), aleStatus, aleResponse.getHttpStatusCode());
      }

    } catch (Exception e) {
      logger.error("resendLogAuditEvents() failed with an exception", e);
    }
  }
}
