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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
}
