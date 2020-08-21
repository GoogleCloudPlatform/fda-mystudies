package com.google.cloud.healthcare.fdamystudies.controller.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent;
import com.google.cloud.healthcare.fdamystudies.controller.EnrollmentTokenController;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EnrollmentTokenControllerTest extends BaseMockIT {

  @Autowired private EnrollmentTokenController controller;
  @Autowired private EnrollmentTokenService enrollmentTokenService;

  @Autowired private ObjectMapper objectMapper;

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
  public void ping() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    mockMvc
        .perform(
            get(ApiEndpoint.PING_PATH.getPath()).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void validateEnrollmentTokenSuccess() throws Exception {
    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void validateEnrollmentTokenBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

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

    verifyAuditEventCall(EnrollAuditEvent.ENROLLMENT_TOKEN_FOUND_INVALID);
  }

  @Test
  public void validateEnrollmentTokenForbidden() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

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

    // token already use
    requestJson = getEnrollmentJson(Constants.TOKEN_ALREADY_USED, Constants.STUDYOF_HEALTH_1);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void validateEnrollmentTokenUnAuthorised() throws Exception {
    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);
    mockMvc
        .perform(
            post(ApiEndpoint.VALIDATE_ENROLLMENT_TOKEN_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void enrollParticipantSuccessStudyTypeClose() throws Exception {

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void enrollParticipantSuccessStudyTypeOpen() throws Exception {
    // study type open
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void enrollParticipantSuccessNewUser() throws Exception {
    // new user id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());
  }

  @Test
  public void enrollParticipantBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // without study id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, null);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // without token
    requestJson = getEnrollmentJson(null, Constants.STUDYOF_HEALTH_CLOSE);
    // performPost(ENROLL_PATH, requestJson, headers, "", BAD_REQUEST);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // unknown token id
    requestJson = getEnrollmentJson(Constants.UNKOWN_TOKEN, Constants.STUDYOF_HEALTH_CLOSE);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void enrollParticipantUnauthorized() throws Exception {

    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void enrollParticipantForbidden() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // token already use
    String requestJson =
        getEnrollmentJson(Constants.ENROLL_TOKEN_ALREADY_USED, Constants.STUDYOF_HEALTH_CLOSE);

    mockMvc
        .perform(
            post(ApiEndpoint.ENROLL_PATH.getPath())
                .headers(headers)
                .content(requestJson)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden());

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
  }

  private String getEnrollmentJson(String tokenId, String studyId) throws JsonProcessingException {
    EnrollmentBean enrollmentBean = new EnrollmentBean(tokenId, studyId);
    return getObjectMapper().writeValueAsString(enrollmentBean);
  }

  public HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    return headers;
  }
}
