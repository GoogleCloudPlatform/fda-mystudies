/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_FAILED_FOR_STUDY_INFO;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.WITHDRAWAL_FROM_STUDY_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.WITHDRAWAL_FROM_STUDY_SUCCEEDED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateResponse;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.controller.StudyStateController;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class StudyStateControllerTest extends BaseMockIT {

  @Autowired private StudyStateController controller;

  @Autowired private StudyStateService studyStateService;

  @Autowired private ObjectMapper objectMapper;

  @Autowired protected MockMvc mockMvc;

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(studyStateService);
  }

  @Test
  public void updateStudyStateSuccess() throws Exception {
    StudiesBean studiesBean =
        new StudiesBean(
            "StudyofHealthClose",
            Constants.BOOKMARKED,
            Constants.COMPLETION,
            Constants.ADHERENCE,
            Constants.SITE_ID);

    List<StudiesBean> listStudies = new ArrayList<StudiesBean>();
    listStudies.add(studiesBean);

    String requestJson = getStudyStateJson(listStudies);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_STUDY_STATE_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId("StudyofHealthClose");
    auditRequest.setStudyVersion("3.3");
    auditRequest.setParticipantId(NOT_APPLICABLE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT);

    verifyTokenIntrospectRequest();

    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.STUDY_STATE_PATH.getPath())
                    .content(JsonUtils.asJsonString(requestJson))
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    verifyTokenIntrospectRequest(2);

    StudyStateResponse response =
        getObjectMapper()
            .readValue(result.getResponse().getContentAsString(), StudyStateResponse.class);

    Optional<StudyStateBean> study =
        response
            .getStudies()
            .stream()
            .filter(s -> s.getStudyId().equals("StudyofHealthClose"))
            .findFirst();

    assertTrue(study.isPresent());
    assertTrue(study.get().getBookmarked());
    assertEquals(35, study.get().getCompletion());
    assertEquals(55, study.get().getAdherence());
  }

  @Test
  public void updateStudyStateFailure() throws Exception {

    StudiesBean studiesBean =
        new StudiesBean(
            Constants.STUDYOF_HEALTH,
            Constants.BOOKMARKED,
            Constants.COMPLETION,
            Constants.ADHERENCE,
            Constants.SITE_ID);

    List<StudiesBean> listStudies = new ArrayList<StudiesBean>();
    listStudies.add(studiesBean);

    String requestJson = getStudyStateJson(listStudies);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // not valid user id
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_STUDY_STATE_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // empty studylist
    listStudies = new ArrayList<StudiesBean>();
    requestJson = getStudyStateJson(listStudies);
    headers.set(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_STUDY_STATE_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void getStudyStateSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            get(ApiEndpoint.STUDY_STATE_PATH.getPath())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void getStudyStateUnauthorizedUserId() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            get(ApiEndpoint.STUDY_STATE_PATH.getPath())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound());

    verifyAuditEventCall(READ_OPERATION_FAILED_FOR_STUDY_INFO);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void withdrawFromStudySuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    String requestJson =
        getWithDrawJson(Constants.PARTICIPANT_ID, Constants.STUDY_ID_OF_PARTICIPANT);

    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW_FROM_STUDY_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDY_ID_OF_PARTICIPANT);
    auditRequest.setStudyVersion("3.5");
    auditRequest.setParticipantId(Constants.PARTICIPANT_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(WITHDRAWAL_FROM_STUDY_SUCCEEDED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, WITHDRAWAL_FROM_STUDY_SUCCEEDED);

    verifyTokenIntrospectRequest();

    mockMvc
        .perform(
            get(ApiEndpoint.STUDY_STATE_PATH.getPath())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.studies[0].participantId").isEmpty())
        .andExpect(jsonPath("$.studies[0].status", is(EnrollmentStatus.WITHDRAWN.getStatus())))
        .andReturn();

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void withdrawFromStudyFailure() throws Exception {

    // empty participant Id
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    String requestJson = getWithDrawJson("", Constants.STUDY_ID_OF_PARTICIPANT);

    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW_FROM_STUDY_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // empty study Id
    requestJson = getWithDrawJson(Constants.PARTICIPANT_ID, "");

    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW_FROM_STUDY_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // study Id not exists
    requestJson = getWithDrawJson(Constants.PARTICIPANT_ID, Constants.STUDYID_NOT_EXIST);

    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW_FROM_STUDY_PATH.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setParticipantId(Constants.PARTICIPANT_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(WITHDRAWAL_FROM_STUDY_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(WITHDRAWAL_FROM_STUDY_FAILED);

    verifyTokenIntrospectRequest(3);
  }

  private String getWithDrawJson(String participatId, String studyId)
      throws JsonProcessingException {
    WithdrawFromStudyBean withdrawFromStudyBean = new WithdrawFromStudyBean(participatId, studyId);
    return getObjectMapper().writeValueAsString(withdrawFromStudyBean);
  }

  private String getStudyStateJson(List<StudiesBean> listStudies) throws JsonProcessingException {
    StudyStateReqBean studyStateReqBean = new StudyStateReqBean(listStudies);
    return getObjectMapper().writeValueAsString(studyStateReqBean);
  }
}
