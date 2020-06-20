/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.auditlog.validator.AuditLogEventValidator;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.jayway.jsonpath.JsonPath;

public class AuditLogEventControllerTest extends BaseMockIT {

  @Autowired AuditLogEventValidator validator;

  private static String validAuditLogEvent;

  private static String invalidAuditLogEvent;

  private static ObjectMapper objMapper = new ObjectMapper();

  @BeforeAll
  public static void loadEvents() throws JsonParseException, JsonMappingException, IOException {
    invalidAuditLogEvent =
        objMapper
            .readValue(
                AuditLogEventControllerTest.class.getResourceAsStream(
                    "/invalid_audit_log_event.json"),
                JsonNode.class)
            .toString();

    validAuditLogEvent =
        objMapper
            .readValue(
                AuditLogEventControllerTest.class.getResourceAsStream(
                    "/valid_audit_log_event.json"),
                JsonNode.class)
            .toString();
  }

  @Test
  public void shouldSaveAuditLogEvent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    MvcResult result =
        performPost(
            ApiEndpoint.EVENTS.getPath(), validAuditLogEvent, headers, StringUtils.EMPTY, CREATED);
    int eventId = JsonPath.read(result.getResponse().getContentAsString(), "$.event_id");
    assertTrue(eventId > 0);
  }

  @Test
  public void shouldReturnUnauthorized() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", INVALID_BEARER_TOKEN);

    performPost(
        ApiEndpoint.EVENTS.getPath(), validAuditLogEvent, headers, "Invalid token", UNAUTHORIZED);
  }

  @Test
  public void shouldReturnNotFoundForRestClientErrorException() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", "Bearer " + UUID.randomUUID().toString());

    // expect FilterChainExceptionHandler extracts status code and error message from
    // RestClientErrorException
    performPost(
        ApiEndpoint.EVENTS.getPath(), validAuditLogEvent, headers, "404 Not Found", NOT_FOUND);
  }

  @Test
  public void shouldReturnBadRequestForInvalidContent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    performPost(
        ApiEndpoint.EVENTS.getPath(),
        invalidAuditLogEvent,
        headers,
        "extraneous key [user_id] is not permitted",
        BAD_REQUEST);
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
