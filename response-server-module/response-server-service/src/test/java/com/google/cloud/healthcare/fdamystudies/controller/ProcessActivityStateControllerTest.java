/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantActivitiesBo;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantActivitiesRepository;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectMapper;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestMethodOrder(OrderAnnotation.class)
public class ProcessActivityStateControllerTest extends BaseMockIT {
  @Autowired ParticipantActivitiesRepository participantActivitiesRepository;

  @Test
  @Order(1)
  void shouldUpdateActivityState() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    String inputJsonContent = readJsonFile("/update_activity_state_runs_info_request.json");
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .content(inputJsonContent)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    ActivityStateRequestBean inputActivityStateBean =
        getObjectMapper().readValue(inputJsonContent, ActivityStateRequestBean.class);
    String studyId = inputActivityStateBean.getStudyId();
    String participantId = inputActivityStateBean.getParticipantId();

    List<ParticipantActivitiesBo> resultsList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(studyId, participantId);
    assertNotNull(resultsList);
    assertEquals(1, resultsList.size());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityId(),
        resultsList.get(0).getActivityId());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityVersion(),
        resultsList.get(0).getActivityVersion());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getBookmarked(),
        resultsList.get(0).getBookmark());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRunId(),
        resultsList.get(0).getActivityRunId());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityState(),
        resultsList.get(0).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getTotal(),
        resultsList.get(0).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getCompleted(),
        resultsList.get(0).getCompleted());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getMissed(),
        resultsList.get(0).getMissed());
  }

  @Test
  @Order(2)
  void shouldGetActivityStateValidParams() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    // Querying activity state with parameters based on how it has been saved in the get activity
    // state call above
    requestParams.add("studyId", "RT3");
    requestParams.add("participantId", "567");

    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.GET_ACTIVITY_STATE.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/get_activity_state_runs_info_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
    participantActivitiesRepository.deleteAll();
  }

  @Test
  @Order(3)
  void shouldUpdateActivityStateMultiple() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    String inputJsonContent =
        readJsonFile("/update_activity_state_runs_info__multiple_request.json");
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .content(inputJsonContent)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

    ActivityStateRequestBean inputActivityStateBean =
        getObjectMapper().readValue(inputJsonContent, ActivityStateRequestBean.class);
    String studyId = inputActivityStateBean.getStudyId();
    String participantId = inputActivityStateBean.getParticipantId();

    List<ParticipantActivitiesBo> resultsList =
        participantActivitiesRepository.findByStudyIdAndParticipantId(studyId, participantId);
    assertNotNull(resultsList);
    assertEquals(2, resultsList.size());

    // Validate 1st activity state
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityId(),
        resultsList.get(0).getActivityId());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityVersion(),
        resultsList.get(0).getActivityVersion());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getBookmarked(),
        resultsList.get(0).getBookmark());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRunId(),
        resultsList.get(0).getActivityRunId());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityState(),
        resultsList.get(0).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getTotal(),
        resultsList.get(0).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getCompleted(),
        resultsList.get(0).getCompleted());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getMissed(),
        resultsList.get(0).getMissed());

    // Validate 2nd activity state
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityId(),
        resultsList.get(1).getActivityId());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityVersion(),
        resultsList.get(1).getActivityVersion());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getBookmarked(),
        resultsList.get(1).getBookmark());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRunId(),
        resultsList.get(1).getActivityRunId());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityState(),
        resultsList.get(1).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRun().getTotal(),
        resultsList.get(1).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRun().getCompleted(),
        resultsList.get(1).getCompleted());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRun().getMissed(),
        resultsList.get(1).getMissed());
  }

  @Test
  void shouldGetActivityStateBadRequestNullStudyId() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("studyId", null);
    requestParams.add("participantId", "567");
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void shouldGetActivityStateBadRequestNullParticipantId() throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add("studyId", "RT3");
    requestParams.add("participantId", null);
    mockMvc
        .perform(
            get(ApiEndpoint.GET_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @ParameterizedTest
  @CsvSource({",567", "RT3,"})
  void shouldGetActivityStateBadRequestEmptyStudyId(ArgumentsAccessor argumentsAccessor)
      throws Exception {
    HttpHeaders headers = TestUtils.newHeadersUser();
    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    String studyIdArg =
        argumentsAccessor.getString(0) == null ? "" : argumentsAccessor.getString(0);
    String participantIdArg =
        argumentsAccessor.getString(1) == null ? "" : argumentsAccessor.getString(1);
    requestParams.add("studyId", studyIdArg);
    requestParams.add("participantId", participantIdArg);

    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.GET_ACTIVITY_STATE.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();
    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }
}
