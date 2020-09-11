/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PARTICIPANT_ID_KEY;
import static com.google.cloud.healthcare.fdamystudies.utils.AppConstants.PARTICIPANT_TOKEN_IDENTIFIER_KEY;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACTIVITY_COLLECTION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACTIVITY_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.PARTICIPANT_ID_NOT_EXISTS_MESSAGE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.QUESTION_KEY_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.SITE_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_COLLECTION_NAME_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_ID_VALUE;
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
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.dao.CloudFirestoreResponsesDaoImpl;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantActivitiesBo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantActivitiesRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantBoRepository;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProcessActivityResponseControllerTest extends BaseMockIT {

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private ParticipantBoRepository participantBoRepository;

  private ParticipantBo participantBo;

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
    dataToStore.put(PARTICIPANT_ID_KEY, participantBo.getParticipantIdentifier());

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
    List<ParticipantActivitiesBo> participantActivitiesList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(
            STUDY_ID_VALUE, participantBo.getParticipantIdentifier());

    assertNotNull(participantActivitiesList);
    assertEquals(1, participantActivitiesList.size());

    verify(responsesDaoMock)
        .saveActivityResponseData(
            studyIdCaptor.capture(),
            studyCollectionNameCaptor.capture(),
            activityCollectionNameCaptor.capture(),
            dataToStoreCaptor.capture());

    // Step-4: assert argument capture

    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(ACTIVITY_COLLECTION_NAME_VALUE, activityCollectionNameCaptor.getValue());
    assertEquals(
        participantBo.getParticipantIdentifier(),
        dataToStoreCaptor.getValue().get(PARTICIPANT_ID_KEY));
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
            participantBo.getParticipantIdentifier(),
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
                    .queryParam("orgId", "orgId")
                    .queryParam("appId", "appId")
                    .queryParam("studyId", STUDY_ID_VALUE)
                    .queryParam("siteId", SITE_ID_VALUE)
                    .queryParam("participantId", participantBo.getParticipantIdentifier())
                    .queryParam(
                        PARTICIPANT_TOKEN_IDENTIFIER_KEY, participantBo.getTokenIdentifier())
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
    assertEquals(participantBo.getParticipantIdentifier(), participantIdCaptor.getValue());
    assertEquals(ACTIVITY_ID_VALUE, activityIdCaptor.getValue());
    assertEquals(QUESTION_KEY_VALUE, questionKeyCaptor.getValue());
  }

  @Test
  public void shouldReturnBadRequestsGetActivityResponse() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            get(ApiEndpoint.GET_PROCESS_ACTIVITY_RESPONSE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .queryParam("orgId", "")
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
            STUDY_COLLECTION_NAME_VALUE, STUDY_ID_VALUE, participantBo.getParticipantIdentifier());

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
                .queryParam("participantId", participantBo.getParticipantIdentifier())
                .queryParam("studyId", STUDY_ID_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(SUCCESS)));

    // Step 3: verify deleted values
    List<ParticipantActivitiesBo> participantActivitiesList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(
            STUDY_ID_VALUE, participantBo.getParticipantIdentifier());
    assertTrue(participantActivitiesList.isEmpty());

    verify(responsesDaoMock)
        .updateWithdrawalStatusForParticipant(
            studyCollectionNameCaptor.capture(),
            studyIdCaptor.capture(),
            participantIdCaptor.capture());

    // Step 4: assert argument capture
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(participantBo.getParticipantIdentifier(), participantIdCaptor.getValue());
  }

  @Test
  public void shouldDeleteParticipantFromStudy() throws Exception {
    // Step-1 deleteActivityResponseDataForParticipant
    doNothing()
        .when(responsesDaoMock)
        .deleteActivityResponseDataForParticipant(
            STUDY_COLLECTION_NAME_VALUE,
            STUDY_ID_VALUE,
            ACTIVITY_COLLECTION_NAME_VALUE,
            participantBo.getParticipantIdentifier());

    // Step-2 call API to delete participant from study
    ActivityResponseBean activityResponseBean = setActivityResponseBean();
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    mockMvc
        .perform(
            post(ApiEndpoint.WITHDRAW.getPath())
                .contextPath(getContextPath())
                .content(JsonUtils.asJsonString(activityResponseBean))
                .headers(headers)
                .queryParam("deleteResponses", "true")
                .queryParam("participantId", participantBo.getParticipantIdentifier())
                .queryParam("studyId", STUDY_ID_VALUE))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(SUCCESS)));

    // Step 3: verify deleted values
    List<ParticipantActivitiesBo> participantActivitiesList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(
            STUDY_ID_VALUE, participantBo.getParticipantIdentifier());
    assertTrue(participantActivitiesList.isEmpty());

    verify(responsesDaoMock)
        .deleteActivityResponseDataForParticipant(
            studyCollectionNameCaptor.capture(),
            studyIdCaptor.capture(),
            activityCollectionNameCaptor.capture(),
            participantIdCaptor.capture());

    // Step 4: assert argument capture
    assertEquals(STUDY_COLLECTION_NAME_VALUE, studyCollectionNameCaptor.getValue());
    assertEquals(STUDY_ID_VALUE, studyIdCaptor.getValue());
    assertEquals(ACTIVITY_COLLECTION_NAME_VALUE, activityCollectionNameCaptor.getValue());
    assertEquals(participantBo.getParticipantIdentifier(), participantIdCaptor.getValue());
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
                .queryParam("studyId", ""))
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
    activityResponseBean.setOrgId("OrgName");
    activityResponseBean.setApplicationId("UNCSTAND001");
    activityResponseBean.setParticipantId(participantBo.getParticipantIdentifier());
    activityResponseBean.getMetadata().setActivityId(ACTIVITY_ID_VALUE);
    activityResponseBean.getMetadata().setVersion("1.0");
    activityResponseBean.getMetadata().setStudyId(STUDY_ID_VALUE);
    activityResponseBean.setTokenIdentifier(participantBo.getTokenIdentifier());
    return activityResponseBean;
  }
}
