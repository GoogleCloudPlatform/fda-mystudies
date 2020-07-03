/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.auditlog.repository.AuditLogEventRepository;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.jayway.jsonpath.JsonPath;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

@TestMethodOrder(OrderAnnotation.class)
public class AuditLogEventControllerTest extends BaseMockIT {

  @Autowired private AuditLogEventRepository repository;

  @Test
  @Order(1)
  public void shouldSaveAuditLogEvent() throws Exception {
    // Step-1 call API to post the audit log event
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    AuditLogEventRequest request = createAuditLogEventRequest();

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.EVENTS.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(request))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.eventId").isNotEmpty())
            .andReturn();

    // Step-2 Find AuditLogEventEntity by Id and compare with AuditLogEventRequest object
    String eventId = JsonPath.read(result.getResponse().getContentAsString(), "$.eventId");
    AuditLogEventEntity aleEntity = repository.findById(eventId).get();
    assertNotNull(aleEntity);
    assertEquals(request.getEventName(), aleEntity.getEventName());
    assertEquals(request.getCorrelationId(), aleEntity.getCorrelationId());

    // Step-3 cleanup - delete the event from database
    repository.deleteById(eventId);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/v1/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  @Test
  @Order(2)
  public void shouldReturnUnauthorized() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", INVALID_BEARER_TOKEN);

    performPost(
        ApiEndpoint.EVENTS.getPath(),
        asJsonString(createAuditLogEventRequest()),
        headers,
        "Invalid token",
        UNAUTHORIZED);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/v1/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(INVALID_TOKEN)));
  }

  @Test
  @Order(3)
  public void shouldReturnNotFoundForRestClientErrorException() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    String token = UUID.randomUUID().toString();
    headers.add("Authorization", "Bearer " + token);

    performPost(
        ApiEndpoint.EVENTS.getPath(),
        asJsonString(createAuditLogEventRequest()),
        headers,
        "Not Found",
        NOT_FOUND);

    verify(
        1,
        postRequestedFor(urlEqualTo("/oauth-scim-service/v1/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(token)));
  }

  @Test
  @Order(4)
  public void shouldReturnBadRequestForInvalidContent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    AuditLogEventRequest aleRequest = new AuditLogEventRequest();
    aleRequest.setSystemId(RandomStringUtils.randomAlphanumeric(40));
    aleRequest.setUserId(RandomStringUtils.randomAlphanumeric(101));
    aleRequest.setSystemIp("0.0.0.");
    aleRequest.setClientIp(getRandomSystemIp());

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.EVENTS.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(aleRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(
        2,
        postRequestedFor(urlEqualTo("/oauth-scim-service/v1/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private AuditLogEventRequest createAuditLogEventRequest() {
    AuditLogEventRequest aleRequest = new AuditLogEventRequest();
    aleRequest.setUserId(UUID.randomUUID().toString());
    aleRequest.setAccessLevel(null);
    aleRequest.setAlert(false);
    aleRequest.setAppId("MyStudies");
    aleRequest.setApplicationComponentName("Auth Server");
    aleRequest.setApplicationVersion("v1.0");
    aleRequest.setClientId("FMSGCPARDTST");
    aleRequest.setClientAccessLevel("System-level");
    aleRequest.setClientAppVersion("v1.1");
    aleRequest.setCorrelationId(UUID.randomUUID().toString());
    aleRequest.setDescription(
        String.format(
            "App user registration successful for username %s and user ID %s returned to Resource Server",
            "mock_ale@grr.la", aleRequest.getUserId()));
    aleRequest.setDevicePlatform("Android");
    aleRequest.setDeviceType("MOBILE");
    aleRequest.setEventDetail("App user registration success");
    aleRequest.setEventName("REGISTRATION_SUCCESS");
    aleRequest.setOccured(Instant.now().toEpochMilli());
    aleRequest.setOrgId("FDA");
    aleRequest.setRequestUri(null);
    aleRequest.setResourceServer("Participant Datastore");
    aleRequest.setSystemId("FMSGCAUTHSVR");
    aleRequest.setSystemIp(getRandomSystemIp());
    aleRequest.setClientIp(getRandomSystemIp());

    return aleRequest;
  }

  private String getRandomSystemIp() {
    StringBuilder sb = new StringBuilder();
    sb.append(RandomStringUtils.randomNumeric(4))
        .append(".")
        .append(RandomStringUtils.randomNumeric(4))
        .append(".")
        .append(RandomStringUtils.randomNumeric(4))
        .append(".")
        .append(RandomStringUtils.randomNumeric(4));
    return sb.toString();
  }
}
