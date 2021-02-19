/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_REGISTRATION_REQUEST_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_SENT;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserAppDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class UserRegistrationControllerTest extends BaseMockIT {

  private static final String REGISTER_PATH = "/participant-user-datastore/register";

  @Autowired private UserRegistrationController controller;

  @Autowired private CommonService service;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private UserAppDetailsRepository userAppDetailsRepository;

  @Autowired private AuthInfoRepository authInfoRepository;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Value("${register.url}")
  private String authRegisterUrl;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(service);
  }

  @Test
  public void healthCheck() throws Exception {
    mockMvc.perform(get("/healthCheck")).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void shouldReturnBadRequestForRegisterUser() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    UserRegistrationForm userRegistrationForm = new UserRegistrationForm();
    MvcResult result =
        mockMvc
            .perform(
                post(REGISTER_PATH)
                    .content(asJsonString(userRegistrationForm))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.violations").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/responses/register_account_bad_request.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  public void shouldReturnBadRequestForInvalidPassword() throws Exception {

    // invalid  password
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    // invalid  password
    String requestJson = getRegisterUser("mockito123@gmail.com", Constants.INVALID_PASSWORD);
    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isInternalServerError());

    verify(
        1,
        postRequestedFor(urlEqualTo("/auth-server/users"))
            .withRequestBody(new ContainsPattern(Constants.INVALID_PASSWORD)));
  }

  @Test
  public void shouldReturnBadRequestForEmailExists() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    // user exists
    String requestJson = getRegisterUser(Constants.EMAIL_ID, Constants.PASSWORD);
    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error_description", is(USER_ALREADY_EXISTS.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME.getEventCode(), auditRequest);
    auditEventMap.put(ACCOUNT_REGISTRATION_REQUEST_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME,
        ACCOUNT_REGISTRATION_REQUEST_RECEIVED);
  }

  @Test
  public void shouldRegisterUser() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    String requestJson = getRegisterUser(Constants.EMAIL, Constants.PASSWORD);
    MvcResult result =
        mockMvc
            .perform(
                post(REGISTER_PATH)
                    .content(requestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    // find userDetails by userId and assert email
    Optional<UserDetailsEntity> optUserDetails = userDetailsRepository.findByUserId(userId);
    UserDetailsEntity userDetails = null;
    if (optUserDetails.isPresent()) {
      userDetails = optUserDetails.get();
    }

    assertEquals(Constants.EMAIL, userDetails.getEmail());

    String subject = appConfig.getConfirmationMailSubject();
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("securitytoken", userDetails.getEmailCode());
    templateArgs.put("orgName", appConfig.getOrgName());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    String body =
        PlaceholderReplacer.replaceNamedPlaceholders(appConfig.getConfirmationMail(), templateArgs);

    verifyMimeMessage(Constants.EMAIL, appConfig.getFromEmail(), subject, body);

    verify(
        1,
        postRequestedFor(urlEqualTo("/auth-server/users"))
            .withRequestBody(new ContainsPattern(Constants.PASSWORD)));

    Optional<UserAppDetailsEntity> optUserAppDetails =
        userAppDetailsRepository.findByUserDetails(userDetails);
    Optional<AuthInfoEntity> optAuthInfo = authInfoRepository.findByUserDetails(userDetails);
    assertNotNull(optAuthInfo);
    assertNotNull(optUserAppDetails);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);
    auditRequest.setUserId(userDetails.getUserId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();

    auditEventMap.put(VERIFICATION_EMAIL_SENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, VERIFICATION_EMAIL_SENT);
  }

  private String getRegisterUser(String emailId, String password) throws JsonProcessingException {
    UserRegistrationForm userRegistrationForm = new UserRegistrationForm(emailId, password);
    return getObjectMapper().writeValueAsString(userRegistrationForm);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
