/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.EnrollmentTokenController;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

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

    verifyTokenIntrospectRequest(4);
  }

  @Test
  public void validateEnrollmentTokenForbidden() throws Exception {
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
        .andExpect(status().isForbidden());

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
  public void enrollParticipantSuccessStudyTypeClose() throws Exception {

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

    verifyTokenIntrospectRequest();
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

    verifyTokenIntrospectRequest();
  }

  @Test
  public void enrollParticipantSuccessNewUser() throws Exception {
    // new user id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);
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
