/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.config.CommonModuleConfiguration;
import com.google.cloud.healthcare.fdamystudies.config.WireMockInitializer;
import com.google.cloud.healthcare.fdamystudies.service.AuditEventService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("mockit")
@TestPropertySource({
  "classpath:application-mockit.properties",
  "classpath:application-mockit-common.properties"
})
@ComponentScan(basePackages = {"com.google.cloud.healthcare.fdamystudies"})
public class BaseMockIT {
  private XLogger logger = XLoggerFactory.getXLogger(BaseMockIT.class.getName());

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String VALID_TOKEN = "7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String INVALID_BEARER_TOKEN =
      "Bearer cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final String INVALID_TOKEN = "cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final String AUTH_CODE_VALUE = "28889b79-d7c6-4fe3-990c-bd239c6ce199";

  protected static final ResultMatcher OK = status().isOk();

  protected static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();

  protected static final ResultMatcher CREATED = status().isCreated();

  protected static final ResultMatcher NOT_FOUND = status().isNotFound();

  @Autowired private WireMockServer wireMockServer;

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ServletContext servletContext;

  @Autowired protected AuditEventService mockAuditService;

  @Autowired protected JavaMailSender emailSender;

  protected List<AuditLogEventRequest> auditRequests = new ArrayList<>();

  @LocalServerPort int randomServerPort;

  @Autowired private TestRestTemplate restTemplate;

  @PostConstruct
  public void logServerPort() {
    logger.debug(String.format("server port=%d", randomServerPort));
  }

  protected WireMockServer getWireMockServer() {
    return wireMockServer;
  }

  protected String getContextPath() {
    return servletContext.getContextPath();
  }

  protected String getEncodedAuthorization(String clientId, String clientSecret) {
    String credentials = clientId + ":" + clientSecret;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  protected MvcResult performGet(
      String path,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        get(path).contextPath(servletContext.getContextPath()).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  protected MvcResult performPost(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        post(path)
            .contextPath(servletContext.getContextPath())
            .content(requestBody)
            .headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  /**
   * @param assertOptionalFieldsForEvent is a {@link Map} collection that contains {@link eventCode}
   *     as key and {@link AuditLogEventRequest} with optional field values as value.
   * @param auditEvents audit event enums
   */
  protected void verifyAuditEventCall(
      Map<String, AuditLogEventRequest> assertOptionalFieldsForEvent,
      AuditLogEvent... auditEvents) {

    verifyAuditEventCall(auditEvents);

    Map<String, AuditLogEventRequest> auditRequestByEventCode =
        auditRequests
            .stream()
            .collect(Collectors.toMap(AuditLogEventRequest::getEventCode, Function.identity()));

    assertOptionalFieldsForEvent.forEach(
        (eventCode, expectedAuditRequest) -> {
          AuditLogEventRequest auditRequest = auditRequestByEventCode.get(eventCode);
          assertEquals(expectedAuditRequest.getUserId(), auditRequest.getUserId());
          assertEquals(expectedAuditRequest.getParticipantId(), auditRequest.getParticipantId());
          assertEquals(expectedAuditRequest.getStudyId(), auditRequest.getStudyId());
          assertEquals(expectedAuditRequest.getStudyVersion(), auditRequest.getStudyVersion());
        });
  }

  protected void verifyAuditEventCall(AuditLogEvent... auditEvents) {
    ArgumentCaptor<AuditLogEventRequest> argument =
        ArgumentCaptor.forClass(AuditLogEventRequest.class);
    verify(mockAuditService, atLeastOnce()).postAuditLogEvent(argument.capture());

    Map<String, AuditLogEventRequest> auditRequestByEventCode =
        auditRequests
            .stream()
            .collect(Collectors.toMap(AuditLogEventRequest::getEventCode, Function.identity()));

    for (AuditLogEvent auditEvent : auditEvents) {
      AuditLogEventRequest auditRequest = auditRequestByEventCode.get(auditEvent.getEventCode());

      assertEquals(auditEvent.getEventCode(), auditRequest.getEventCode());

      if (auditEvent.getDestination() != null) {
        assertEquals(auditEvent.getDestination().getValue(), auditRequest.getDestination());
      }

      // Use enum value where specified, otherwise, use 'source' header value.
      if (auditEvent.getSource().isPresent()) {
        assertEquals(auditEvent.getSource().get().getValue(), auditRequest.getSource());
      } else if (StringUtils.isNotEmpty(auditRequest.getSource())) {
        PlatformComponent platformComponent = PlatformComponent.fromValue(auditRequest.getSource());
        assertNotNull(platformComponent);
      }

      if (auditEvent.getResourceServer().isPresent()) {
        assertEquals(
            auditEvent.getResourceServer().get().getValue(), auditRequest.getResourceServer());
      }

      if (auditEvent.getUserAccessLevel().isPresent()) {
        assertEquals(
            auditEvent.getUserAccessLevel().get().getValue(), auditRequest.getUserAccessLevel());
      }

      assertFalse(
          StringUtils.contains(auditRequest.getDescription(), "{")
              && StringUtils.contains(auditRequest.getDescription(), "}"));
      assertNotNull(auditRequest.getCorrelationId());
      assertNotNull(auditRequest.getOccurred());
      assertNotNull(auditRequest.getPlatformVersion());
      assertNotNull(auditRequest.getAppId());
      assertNotNull(auditRequest.getAppVersion());
      assertNotNull(auditRequest.getMobilePlatform());
    }
  }

  protected void clearAuditRequests() {
    auditRequests.clear();
  }

  protected void verifyDoesNotContain(String text, String... searchValues) {
    for (String value : searchValues) {
      assertFalse(StringUtils.contains(text, value));
    }
  }

  @BeforeEach
  void setUp(TestInfo testInfo) {
    logger.entry(String.format("TEST STARTED: %s", testInfo.getDisplayName()));

    WireMock.resetAllRequests();

    Mockito.reset(mockAuditService);
    auditRequests.clear();
    doAnswer(
            invocation ->
                auditRequests.add(
                    SerializationUtils.clone((AuditLogEventRequest) invocation.getArguments()[0])))
        .when(mockAuditService)
        .postAuditLogEvent(Mockito.any(AuditLogEventRequest.class));
    WireMock.resetAllRequests();
  }

  @AfterEach
  void tearDown(TestInfo testInfo) {
    logger.exit(String.format("TEST FINISHED: %s", testInfo.getDisplayName()));
  }

  @TestConfiguration
  @Import(CommonModuleConfiguration.class)
  static class BaseMockITConfiguration {}

  protected void verifyTokenIntrospectRequest() {
    verifyTokenIntrospectRequest(1);
  }

  protected void verifyTokenIntrospectRequest(int times) {
    verify(
        times,
        postRequestedFor(urlEqualTo("/auth-server/oauth2/introspect"))
            .withRequestBody(new ContainsPattern(VALID_TOKEN)));
  }

  protected MimeMessage verifyMimeMessage(
      String toEmail, String fromEmail, String subject, String body)
      throws MessagingException, IOException {
    ArgumentCaptor<MimeMessage> mailCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(emailSender, atLeastOnce()).send(mailCaptor.capture());

    MimeMessage mail = mailCaptor.getValue();

    assertThat(mail.getFrom()).containsExactly(new InternetAddress(fromEmail));
    assertThat(mail.getRecipients(Message.RecipientType.TO))
        .containsExactly(new InternetAddress(toEmail));
    assertThat(mail.getRecipients(Message.RecipientType.CC)).isNull();

    assertThat(mail.getSubject()).isEqualToIgnoringCase(subject);
    assertThat(mail.getContent().toString()).contains(body);

    assertThat(mail.getDataHandler().getContentType())
        .isEqualToIgnoringCase("text/html; charset=utf-8");
    return mail;
  }

  protected String generateApiDocs() throws IOException {
    // get swagger json
    String apiDocs = this.restTemplate.getForObject("/v2/api-docs", String.class);

    // format the json
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    ObjectNode jsonObjNode = mapper.readValue(apiDocs, ObjectNode.class);
    jsonObjNode.put("host", "localhost:8080");

    // prepare the filepath
    String documentPath = Paths.get("").toAbsolutePath().toString();
    documentPath =
        documentPath.substring(0, documentPath.indexOf("fda-mystudies"))
            + "fda-mystudies/documentation/API"
            + servletContext.getContextPath()
            + "/openapi.json";
    documentPath = documentPath.replace(" ", "_");

    // write api-docs json to a file
    FileUtils.write(new File(documentPath), jsonObjNode.toPrettyString(), Charset.defaultCharset());
    logger.info(String.format("Open API documentation created at %s", documentPath));
    return documentPath;
  }
}
