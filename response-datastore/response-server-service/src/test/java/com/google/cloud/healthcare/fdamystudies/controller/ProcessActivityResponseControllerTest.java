/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_RESPONSE_SAVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.ACTIVTY_METADATA_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.DATA_SHARING_CONSENT_VALUE_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ACTIVITY_DATA_DELETED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ID_INVALID;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_RETRIEVED;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.WITHDRAWAL_INFORMATION_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PARTICIPANT_ID_KEY;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACTIVITY_COLLECTION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACTIVITY_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.PARTICIPANT_ID_NOT_EXISTS_MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.QUESTION_KEY_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.SITE_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_COLLECTION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_VERSION;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.SUCCESS;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.VALID_USER_ID;
import static com.google.cloud.healthcare.fdamystudies.utils.ErrorCode.EC_701;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.dao.CloudFirestoreResponsesDaoImpl;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantActivitiesRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantInfoRepository;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantActivitiesEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.utils.Constants;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class ProcessActivityResponseControllerTest extends BaseMockIT {

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private ParticipantInfoRepository participantBoRepository;

  private ParticipantInfoEntity participantBo;

  @MockBean private CloudFirestoreResponsesDaoImpl responsesDaoMock;

  @Captor ArgumentCaptor<String> studyCollectionNameCaptor;
  @Captor ArgumentCaptor<String> studyIdCaptor;
  @Captor ArgumentCaptor<String> siteIdCaptor;
  @Captor ArgumentCaptor<String> participantIdCaptor;
  @Captor ArgumentCaptor<String> activityIdCaptor;
  @Captor ArgumentCaptor<String> questionKeyCaptor;
  @Captor ArgumentCaptor<String> activityCollectionNameCaptor;
  @Captor ArgumentCaptor<Map<String, Object>> dataToStoreCaptor;

  @Autowired private ParticipantActivitiesRepository participantActivitiesRepository;

  @BeforeEach
  public void setUp() {
    participantBo = testDataHelper.saveParticipant();
  }

  @Test
  public void shouldSaveProcessActivityResponse() throws Exception {
    Map<String, Object> dataToStore = new HashMap<>();
    dataToStore.put(PARTICIPANT_ID_KEY, participantBo.getParticipantId());

    // Step-1 saveActivityResponseData
    doNothing()
        .when(responsesDaoMock)
        .saveActivityResponseData(
            STUDY_ID_VALUE,
            STUDY_COLLECTION_NAME_VALUE,
            ACTIVITY_COLLECTION_NAME_VALUE,
            dataToStore);

    // Step-2 call API to details to save participant activities
    ActivityResponseBean activityResponseBean = setActivityResponseBean();
    activityResponseBean.getMetadata().setStudyVersion(STUDY_VERSION);
    activityResponseBean.getMetadata().setActivityRunId("2");

    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            post(ApiEndpoint.PROCESS_ACTIVITY_RESPONSE.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(SUCCESS)));

    // Step-3: verify saved values
    List<ParticipantActivitiesEntity> participantActivitiesList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(
            STUDY_ID_VALUE, participantBo.getParticipantId());

    assertNotNull(participantActivitiesList);
    assertEquals(1, participantActivitiesList.size());

    verify(responsesDaoMock)
        .saveActivityResponseData(
            studyIdCaptor.capture(),
            studyCollectionNameCaptor.capture(),
            activityCollectionNameCaptor.capture(),
            dataToStoreCaptor.capture());

    verify(
        1,
        getRequestedFor(
            urlEqualTo(
                "/participant-enroll-datastore/participantInfo?studyId=ASignature01&participantId="
                    + participantBo.getParticipantId())));

    verify(
        1,
        getRequestedFor(
            urlEqualTo(
                "/study-datastore/activity?studyId=ASignature01"
                    + "&activityId=Activity&activityVersion=1.0")));

    // Step-4: assert argument capture
    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(ACTIVITY_COLLECTION_NAME_VALUE, activityCollectionNameCaptor.getValue());
    assertEquals(
        participantBo.getParticipantId(), dataToStoreCaptor.getValue().get(PARTICIPANT_ID_KEY));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(activityResponseBean.getMetadata().getStudyId());
    auditRequest.setStudyVersion(activityResponseBean.getMetadata().getStudyVersion());
    auditRequest.setParticipantId(activityResponseBean.getParticipantId());
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(ACTIVITY_RESPONSE_RECEIVED.getEventCode(), auditRequest);
    auditEventMap.put(ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA.getEventCode(), auditRequest);
    auditEventMap.put(
        DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA.getEventCode(),
        auditRequest);
    auditEventMap.put(ACTIVITY_RESPONSE_SAVED.getEventCode(), auditRequest);
    auditEventMap.put(DATA_SHARING_CONSENT_VALUE_RETRIEVED.getEventCode(), auditRequest);
    auditEventMap.put(ACTIVTY_METADATA_RETRIEVED.getEventCode(), auditRequest);
    auditEventMap.put(WITHDRAWAL_INFORMATION_RETRIEVED.getEventCode(), auditRequest);
    auditEventMap.put(
        ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        ACTIVITY_RESPONSE_RECEIVED,
        ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA,
        DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA,
        ACTIVITY_RESPONSE_SAVED,
        DATA_SHARING_CONSENT_VALUE_RETRIEVED,
        ACTIVTY_METADATA_RETRIEVED,
        WITHDRAWAL_INFORMATION_RETRIEVED,
        ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION);
  }

  @Test
  public void shouldReturnBadRequestForEmptyInputsOfProccessActivityResponse() throws Exception {
    ActivityResponseBean activityResponseBean = new ActivityResponseBean();
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            post(ApiEndpoint.PROCESS_ACTIVITY_RESPONSE.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.userMessage", is(EC_701.errorMessage())));
  }

  @Test
  public void shouldReturnBadRequestForInvalidParticipant() throws Exception {
    ActivityResponseBean activityResponseBean = setActivityResponseBean();
    activityResponseBean.getMetadata().setStudyVersion(STUDY_VERSION);
    activityResponseBean.setParticipantId(IdGenerator.id());
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            post(ApiEndpoint.PROCESS_ACTIVITY_RESPONSE.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.detailMessage", is(PARTICIPANT_ID_NOT_EXISTS_MESSAGE)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(VALID_USER_ID);
    auditRequest.setStudyId(activityResponseBean.getMetadata().getStudyId());
    auditRequest.setStudyVersion(activityResponseBean.getMetadata().getStudyVersion());
    auditRequest.setParticipantId(activityResponseBean.getParticipantId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_ID_INVALID.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_ID_INVALID);
  }

  @Test
  public void shouldGetActivityResponse() throws Exception {
    String inputJsonContent = readJsonFile("/get_activity_response_data_for_participant.json");
    StoredResponseBean storedResponseBean =
        getObjectMapper().readValue(inputJsonContent, StoredResponseBean.class);

    // Step-1 getActivityResponseDataForParticipant
    when(responsesDaoMock.getActivityResponseDataForParticipant(
            STUDY_COLLECTION_NAME_VALUE,
            STUDY_ID_VALUE,
            SITE_ID_VALUE,
            participantBo.getParticipantId(),
            ACTIVITY_ID_VALUE,
            QUESTION_KEY_VALUE))
        .thenReturn(storedResponseBean);

    // Step-2 call API to details to Get process activity response
    HttpHeaders headers = TestUtils.newHeadersUser();
    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.GET_PROCESS_ACTIVITY_RESPONSE.getPath())
                    .contextPath(getContextPath())
                    .headers(headers)
                    .queryParam("appId", "appId")
                    .queryParam("studyId", STUDY_ID_VALUE)
                    .queryParam("siteId", SITE_ID_VALUE)
                    .queryParam("participantId", participantBo.getParticipantId())
                    .queryParam(PARTICIPANT_TOKEN_IDENTIFIER_KEY, participantBo.getTokenId())
                    .queryParam("activityId", ACTIVITY_ID_VALUE)
                    .queryParam("questionKey", QUESTION_KEY_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    JSONAssert.assertEquals(inputJsonContent, actualResponse, JSONCompareMode.NON_EXTENSIBLE);

    verify(responsesDaoMock)
        .getActivityResponseDataForParticipant(
            studyCollectionNameCaptor.capture(),
            studyIdCaptor.capture(),
            siteIdCaptor.capture(),
            participantIdCaptor.capture(),
            activityIdCaptor.capture(),
            questionKeyCaptor.capture());

    // Step 3: assert argument capture
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(SITE_ID_VALUE, siteIdCaptor.getValue());
    assertEquals(participantBo.getParticipantId(), participantIdCaptor.getValue());
    assertEquals(ACTIVITY_ID_VALUE, activityIdCaptor.getValue());
    assertEquals(QUESTION_KEY_VALUE, questionKeyCaptor.getValue());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setAppId("appId");
    auditRequest.setSiteId(SITE_ID_VALUE);
    auditRequest.setStudyId(STUDY_ID_VALUE);
    auditRequest.setParticipantId(participantBo.getParticipantId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED);
  }

  @Test
  public void shouldReturnBadRequestsGetActivityResponse() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            get(ApiEndpoint.GET_PROCESS_ACTIVITY_RESPONSE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .queryParam("appId", "")
                .queryParam("studyId", "")
                .queryParam("siteId", "")
                .queryParam("participantId", "")
                .queryParam(PARTICIPANT_TOKEN_IDENTIFIER_KEY, "")
                .queryParam("activityId", "")
                .queryParam(QUESTION_KEY_VALUE, ""))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.userMessage", is(EC_701.errorMessage())));
  }

  @Test
  public void shouldUpdateWithdrawStatusParticipantFromStudy() throws Exception {
    // Step-1 updateWithdrawalStatusForParticipant
    doNothing()
        .when(responsesDaoMock)
        .updateWithdrawalStatusForParticipant(
            STUDY_COLLECTION_NAME_VALUE, STUDY_ID_VALUE, participantBo.getParticipantId());

    // Step-2 call API to update withdraw status of participant from study
    ActivityResponseBean activityResponseBean = setActivityResponseBean();
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers)
                .queryParam("deleteResponses", "")
                .queryParam("participantId", participantBo.getParticipantId())
                .queryParam("studyId", STUDY_ID_VALUE)
                .queryParam("studyVersion", STUDY_VERSION))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(SUCCESS)));

    // Step 3: verify deleted values
    List<ParticipantActivitiesEntity> participantActivitiesList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(
            STUDY_ID_VALUE, participantBo.getParticipantId());
    assertTrue(participantActivitiesList.isEmpty());

    verify(responsesDaoMock)
        .updateWithdrawalStatusForParticipant(
            studyCollectionNameCaptor.capture(),
            studyIdCaptor.capture(),
            participantIdCaptor.capture());

    // Step 4: assert argument capture
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(participantBo.getParticipantId(), participantIdCaptor.getValue());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(VALID_USER_ID);
    auditRequest.setStudyId(STUDY_ID_VALUE);
    auditRequest.setStudyVersion(STUDY_VERSION);
    auditRequest.setParticipantId(participantBo.getParticipantId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE.getEventCode(), auditRequest);
    auditEventMap.put(WITHDRAWAL_INFORMATION_UPDATED.getEventCode(), auditRequest);
    auditEventMap.put(PARTICIPANT_ACTIVITY_DATA_DELETED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE,
        WITHDRAWAL_INFORMATION_UPDATED,
        PARTICIPANT_ACTIVITY_DATA_DELETED);
  }

  @Test
  public void shouldReturnBadRequestForEmptyInputsOfWithdraw() throws Exception {
    ActivityResponseBean activityResponseBean = setActivityResponseBean();
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers)
                .queryParam("deleteResponses", "")
                .queryParam("participantId", "")
                .queryParam("studyId", "")
                .queryParam("studyVersion", ""))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.userMessage", is(EC_701.errorMessage())));
  }

  @AfterEach
  public void clean() {
    participantBoRepository.deleteAll();
  }

  private ActivityResponseBean setActivityResponseBean() {
    ActivityResponseBean activityResponseBean = new ActivityResponseBean();
    activityResponseBean.setApplicationId("UNCSTAND001");
    activityResponseBean.setParticipantId(participantBo.getParticipantId());
    activityResponseBean.getMetadata().setActivityId(ACTIVITY_ID_VALUE);
    activityResponseBean.getMetadata().setVersion("1.0");
    activityResponseBean.getMetadata().setStudyId(STUDY_ID_VALUE);
    activityResponseBean.setType("questionnaire");
    activityResponseBean.setTokenIdentifier(participantBo.getTokenId());
    return activityResponseBean;
  }
}
