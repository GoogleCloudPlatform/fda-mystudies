/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.ENROLLMENT_TOKEN_FOUND_INVALID;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.PARTICIPANT_ID_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.USER_FOUND_ELIGIBLE_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.TOKEN_EXPIRED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.controller.EnrollmentTokenController;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.ErrorResponseUtil.ErrorCodes;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class EnrollmentTokenControllerTest extends BaseMockIT {

  @Autowired private EnrollmentTokenController controller;
  @Autowired private EnrollmentTokenService enrollmentTokenService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(enrollmentTokenService);
  }

  @Test
  public void validateEnrollmentTokenSuccess() throws Exception {
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById("30");
    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setEnrollmentToken(Constants.TOKEN);
    participantRegistrySite.setEnrollmentTokenUsed(false);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void validateEnrollmentTokenBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // without study id
    String requestJson = getEnrollmentJson(Constants.TOKEN, null);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // without token
    requestJson = getEnrollmentJson(null, Constants.STUDYOF_HEALTH_CLOSE);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(3);

    // unknown token id
    requestJson = getEnrollmentJson(Constants.UNKOWN_TOKEN, Constants.STUDYOF_HEALTH);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);
    auditRequest.setStudyVersion("3.1");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ENROLLMENT_TOKEN_FOUND_INVALID.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, ENROLLMENT_TOKEN_FOUND_INVALID);

    verifyTokenIntrospectRequest(4);
  }

  @Test
  public void validateEnrollmentTokenAlreadyUsedBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // study id not exists
    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYID_NOT_EXIST);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());

    verifyTokenIntrospectRequest();

    // token already use
    requestJson = getEnrollmentJson(Constants.TOKEN_ALREADY_USED, Constants.STUDYOF_HEALTH_1);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void validateEnrollmentTokenBadRequest() throws Exception {
    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void validateEnrollmentForExpiredToken() throws Exception {
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById("30");

    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setEnrollmentToken(Constants.TOKEN);
    participantRegistrySite.setEnrollmentTokenUsed(false);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()));

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error_description", is(TOKEN_EXPIRED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantForExpiredToken() throws Exception {
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findById("30");

    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setEnrollmentToken(Constants.TOKEN);
    participantRegistrySite.setEnrollmentTokenUsed(false);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().minus(1, ChronoUnit.DAYS).toEpochMilli()));

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error_description", is(TOKEN_EXPIRED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantSuccessStudyTypeClose() throws Exception {
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findByEnrollmentToken(Constants.TOKEN_NEW);
    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setOnboardingStatus(OnboardingStatus.INVITED.getCode());
    participantRegistrySite.setEnrollmentTokenUsed(false);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH_CLOSE);
    auditRequest.setStudyVersion("3.3");
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setParticipantId("i4ts7dsf50c6me154sfsdfdv");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_ID_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_ID_RECEIVED);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldNotAllowUserForEnrollment() throws Exception {
    List<ParticipantRegistrySiteEntity> participantRegistrySiteList =
        participantRegistrySiteRepository.findByStudyIdAndEmail("3", "cdash936@gmail.com");

    ParticipantRegistrySiteEntity participantRegistrySite = participantRegistrySiteList.get(0);
    // Set onboarding status to Disabled (D)
    participantRegistrySite.setOnboardingStatus("D");
    participantRegistrySite.setEnrollmentTokenUsed(false);

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isGone())
        .andExpect(jsonPath("$.error_description", is(TOKEN_EXPIRED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnUnknownTokenForEnrollment() throws Exception {
    List<ParticipantRegistrySiteEntity> participantRegistrySiteList =
        participantRegistrySiteRepository.findByStudyIdAndEmail("3", "cdash936@gmail.com");

    ParticipantRegistrySiteEntity participantRegistrySite = participantRegistrySiteList.get(0);
    // Set onboarding status to New (N)
    participantRegistrySite.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(ErrorCodes.INVALID_TOKEN.getValue())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantSuccessForCaseInsensitiveToken() throws Exception {
    Optional<ParticipantStudyEntity> optParticipantStudy =
        participantStudyRepository.findByParticipantId("i4ts7dsf50c6me154sfsdfdv");

    ParticipantStudyEntity participantStudy = optParticipantStudy.get();
    participantStudy.setParticipantId("1");
    participantStudyRepository.saveAndFlush(participantStudy);

    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findByEnrollmentToken(Constants.TOKEN_NEW);
    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

    // study type close
    String requestJson = getEnrollmentJson("6DL0pOqf", Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH_CLOSE);
    auditRequest.setStudyVersion("3.3");
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setParticipantId("i4ts7dsf50c6me154sfsdfdv");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_ID_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_ID_RECEIVED);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldFailEnrollmentForExistingParticipant() throws Exception {
    Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
        participantRegistrySiteRepository.findByEnrollmentToken(Constants.TOKEN_NEW);
    ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
    participantRegistrySite.setEnrollmentTokenUsed(true);
    participantRegistrySite.setEnrollmentTokenExpiry(
        new Timestamp(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli()));

    participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);
    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void enrollParticipantSuccessStudyTypeOpen() throws Exception {
    // study type open
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);
    auditRequest.setStudyVersion("3.1");
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setParticipantId("i4ts7dsf50c6me154sfsdfdv");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_FOUND_ELIGIBLE_FOR_STUDY.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, USER_FOUND_ELIGIBLE_FOR_STUDY);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantSuccessNewUser() throws Exception {
    // new user id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // unknown token id
    String requestJson = getEnrollmentJson(Constants.UNKOWN_TOKEN, Constants.STUDYOF_HEALTH_CLOSE);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // without token
    requestJson = getEnrollmentJson(null, Constants.STUDYOF_HEALTH_CLOSE);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // without study id
    requestJson = getEnrollmentJson(Constants.TOKEN, null);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void enrollParticipantBadRequest() throws Exception {

    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantForbidden() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // token already use
    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYID_NOT_EXIST);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());

    verifyTokenIntrospectRequest();

    // study id not exists
    requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYID_NOT_EXIST);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());

    verifyTokenIntrospectRequest(2);
  }

  private String getEnrollmentJson(String tokenId, String studyId) throws JsonProcessingException {
    EnrollmentBean enrollmentBean = new EnrollmentBean(tokenId, studyId);
    return getObjectMapper().writeValueAsString(enrollmentBean);
  }
}
