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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.auditlog.config.AppPropConfig;
import com.google.cloud.healthcare.fdamystudies.auditlog.model.AuditLogEventEntity;
import com.google.cloud.healthcare.fdamystudies.auditlog.repository.AuditLogEventRepository;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserAccessLevel;
import com.jayway.jsonpath.JsonPath;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class AuditLogEventControllerTest extends BaseMockIT {

  @Autowired private AuditLogEventRepository repository;

  @Autowired private AppPropConfig appPropConfig;

  @BeforeEach
  public void setUp() {
    WireMock.resetAllRequests();
    appPropConfig.setAuditStorage("database");
  }

  @Test
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
    assertEquals(request.getCorrelationId(), aleEntity.getCorrelationId());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldPostAuditEventToStackDriver() throws Exception {
    // Step-1 call API to post the audit event to stackdriver
    appPropConfig.setAuditStorage("stackdriver");

    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    AuditLogEventRequest auditRequest = createAuditLogEventRequest();

    mockMvc
        .perform(
            post(ApiEndpoint.EVENTS.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(auditRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    ArgumentCaptor<AuditLogEventRequest> argument =
        ArgumentCaptor.forClass(AuditLogEventRequest.class);
    verify(mockAuditService, times(1)).postAuditLogEvent(argument.capture());
    assertEquals(auditRequest.getEventCode(), argument.getValue().getEventCode());

    verifyTokenIntrospectRequest();
  }

  @Test
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
        postRequestedFor(urlEqualTo("/oauth-scim-service/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(INVALID_TOKEN)));
  }

  @Test
  public void shouldReturnBadRequestForInvalidContent() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(RandomStringUtils.randomAlphanumeric(101));
    auditRequest.setUserIp("0.0.0.");

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.EVENTS.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(auditRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verifyTokenIntrospectRequest();
  }

  @AfterEach
  public void cleanUp() {
    repository.deleteAll();
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }

  private AuditLogEventRequest createAuditLogEventRequest() {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(UUID.randomUUID().toString());
    auditRequest.setUserAccessLevel(UserAccessLevel.APP_STUDY_ADMIN.getValue());
    auditRequest.setAppId("MyStudies");
    auditRequest.setSource(PlatformComponent.PARTICIPANT_DATASTORE.getValue());
    auditRequest.setResourceServer(PlatformComponent.PARTICIPANT_DATASTORE.getValue());
    auditRequest.setAppVersion("v1.0");
    auditRequest.setDestination(PlatformComponent.SCIM_AUTH_SERVER.getValue());
    auditRequest.setCorrelationId(UUID.randomUUID().toString());
    auditRequest.setDescription(
        String.format(
            "Password reset for User ID ${user_id} was successful.", auditRequest.getUserId()));
    auditRequest.setMobilePlatform(MobilePlatform.ANDROID.getValue());
    auditRequest.setEventCode("REGISTRATION_SUCCESS");
    auditRequest.setOccured(new Timestamp(Instant.now().toEpochMilli()));
    auditRequest.setDestination(PlatformComponent.AUTH_SERVER.getValue());
    auditRequest.setUserIp(getRandomSystemIp());
    auditRequest.setPlatformVersion("1.0");
    auditRequest.setSourceApplicationVersion("1.0");
    auditRequest.setDestinationApplicationVersion("1.0");
    return auditRequest;
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
