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

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventResponse;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.repository.AuditEventRepository;
import java.time.Instant;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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

    AuditLogEventResponse auditResponse = callEventsApi(requestBody);

    logger.exit(String.format("status=%d", auditResponse.getHttpStatusCode()));
    return auditResponse;
  }

  private AuditLogEventResponse callEventsApi(JsonNode requestBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + oauthService.getAccessToken());

    ResponseEntity<JsonNode> response =
        exchangeForJson(eventsEndpoint, headers, requestBody, HttpMethod.POST);

    AuditLogEventResponse auditResponse = null;
    if (response.getStatusCode().is2xxSuccessful()) {
      auditResponse = getObjectMapper().convertValue(response.getBody(), AuditLogEventResponse.class);
      auditResponse.setHttpStatusCode(response.getStatusCodeValue());
      return auditResponse;
    } else if (response.getStatusCode().is4xxClientError()) {
      logger.error(
          String.format(
              "%s failed with status=%d, response=%s",
              eventsEndpoint, response.getStatusCodeValue(), response.getBody()));

      String eventName = getTextValue(requestBody, "eventName");
      auditResponse =
          new AuditLogEventResponse(
              response.getStatusCode(),
              String.format(
                  "%s event not received/processed by the central audit log system.", eventName));
    }
    return auditResponse;
  }
}
