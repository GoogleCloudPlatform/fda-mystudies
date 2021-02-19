/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.REGISTRATION_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_ACCOUNT_ACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailIdVerificationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileManagementDaoImpl;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class VerifyEmailIdControllerTest extends BaseMockIT {

  private static final int VERIFIED_STATUS = 1;

  private static final String VERIFY_EMAIL_ID_PATH = "/participant-user-datastore/verifyEmailId";

  @Autowired private VerifyEmailIdController controller;

  @Autowired private CommonService commonService;

  @Autowired private UserDetailsRepository repository;

  @Autowired private UserProfileManagementDaoImpl userProfileDao;

  @Autowired UserManagementProfileService userManagementProfService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(commonService);
  }

  @Test
  public void shouldReturnBadRequestForInvalidCode() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);
    // invalid code
    String requestJson = getEmailIdVerificationForm(Constants.INVALID_CODE, "abc@gmail.com");
    mockMvc
        .perform(
            post(VERIFY_EMAIL_ID_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.message", is(Constants.INVALID_EMAIL_CODE)));

    UserDetailsEntity user =
        userManagementProfService.getParticipantDetailsByEmail(
            "abc@gmail.com", Constants.APP_ID_VALUE);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);
    auditRequest.setUserId(user.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap, ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE);
  }

  @Test
  public void shouldReturnBadRequestForInvalidEmailId() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);
    // expired code
    String requestJson =
        getEmailIdVerificationForm(Constants.VALID_CODE, Constants.INVALID_EMAIL_ID);
    mockMvc
        .perform(
            post(VERIFY_EMAIL_ID_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.message", is(Constants.INVALID_EMAIL_CODE)));

    UserDetailsEntity user =
        userManagementProfService.getParticipantDetailsByEmail(
            Constants.INVALID_EMAIL_ID, Constants.APP_ID_VALUE);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);
    auditRequest.setUserId(user.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE.getEventCode(),
        auditRequest);

    verifyAuditEventCall(
        auditEventMap, ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE);
  }

  @Test
  public void shouldReturnBadRequestForEmailNotExists() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);
    // invalid emailId
    String requestJson = getEmailIdVerificationForm(Constants.CODE, Constants.INVALID_EMAIL);
    mockMvc
        .perform(
            post(VERIFY_EMAIL_ID_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.message", is(Constants.EMAIL_NOT_EXIST)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED);
  }

  @Test
  public void shouldUpdateEmailStatusToVerified() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    String requestJson = getEmailIdVerificationForm(Constants.CODE, Constants.VERIFY_CODE_EMAIL);

    mockMvc
        .perform(
            post(VERIFY_EMAIL_ID_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.verified").value(Boolean.TRUE))
        .andExpect(jsonPath("$.tempRegId").isNotEmpty());

    // get list of userDetails by emailId
    List<UserDetailsEntity> userDetailsList = repository.findByEmail(Constants.VERIFY_CODE_EMAIL);
    UserDetailsEntity userDetails =
        userDetailsList
            .stream()
            .filter(
                user -> {
                  AppEntity appDetail =
                      userProfileDao.getAppPropertiesDetailsByAppId(user.getApp().getId());
                  return StringUtils.equals(user.getEmail(), Constants.VERIFY_CODE_EMAIL)
                      && StringUtils.equals(appDetail.getAppId(), Constants.APP_ID_VALUE);
                })
            .findAny()
            .orElse(null);
    assertNotNull(userDetails);
    assertTrue(VERIFIED_STATUS == userDetails.getStatus());

    verify(1, putRequestedFor(urlEqualTo("/auth-server/users/" + Constants.VALID_USER_ID)));

    UserDetailsEntity user =
        userManagementProfService.getParticipantDetailsByEmail(
            Constants.VERIFY_CODE_EMAIL, Constants.APP_ID_VALUE);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(Constants.APP_ID_VALUE);
    auditRequest.setUserId(user.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_ACCOUNT_ACTIVATED.getEventCode(), auditRequest);
    auditEventMap.put(USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION.getEventCode(), auditRequest);
    auditEventMap.put(REGISTRATION_SUCCEEDED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_ACCOUNT_ACTIVATED,
        USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION,
        REGISTRATION_SUCCEEDED);
  }

  private String getEmailIdVerificationForm(String code, String emailId)
      throws JsonProcessingException {
    EmailIdVerificationForm emailIdVerificationForm = new EmailIdVerificationForm(code, emailId);
    return getObjectMapper().writeValueAsString(emailIdVerificationForm);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
