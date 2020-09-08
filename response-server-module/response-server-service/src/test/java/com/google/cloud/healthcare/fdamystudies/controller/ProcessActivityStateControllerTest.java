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
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantActivitiesRepository;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantActivitiesBo;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantActivityStateResponseService;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.CsvSource;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProcessActivityStateControllerTest extends BaseMockIT {
  @Autowired ParticipantActivitiesRepository participantActivitiesRepository;
  @Autowired ParticipantActivityStateResponseService participantActivitiesResponseService;

  @Test
  void shouldGetActivityStateValidParams() throws Exception {
    // Step 1: Save the activity first
    String inputJsonContent = readJsonFile("/update_activity_state_runs_info_request.json");
    ActivityStateRequestBean activityStateBean =
        getObjectMapper().readValue(inputJsonContent, ActivityStateRequestBean.class);
    participantActivitiesResponseService.saveParticipantActivities(activityStateBean);

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    // Step 2: Querying activity state with parameters based on how it has been saved in the get
    // activity  state call above
    requestParams.add("studyId", "RT3");
    requestParams.add("participantId", "567");

    // Step 3: Call API to get activity state
    HttpHeaders headers = TestUtils.newHeadersUser();
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
  }

  @ParameterizedTest
  @CsvSource({"RT4,567", "RT3,568"})
  void shouldGetEmptyActivityStateParamsNotFound(ArgumentsAccessor argumentsAccessor)
      throws Exception {
    // Step 1: Save the activity first
    String inputJsonContent = readJsonFile("/update_activity_state_runs_info_request.json");
    ActivityStateRequestBean activityStateBean =
        getObjectMapper().readValue(inputJsonContent, ActivityStateRequestBean.class);
    participantActivitiesResponseService.saveParticipantActivities(activityStateBean);

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    // Step 2: Querying activity state with parameters, which are different from how it has been
    // saved in the save activity call above
    String studyIdArg = argumentsAccessor.getString(0);
    String participantIdArg = argumentsAccessor.getString(1);
    requestParams.add("studyId", studyIdArg);
    requestParams.add("participantId", participantIdArg);

    // Step 3: Call API to update activity state
    HttpHeaders headers = TestUtils.newHeadersUser();
    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.GET_ACTIVITY_STATE.getPath())
                    .contextPath(getContextPath())
                    .params(requestParams)
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.activities").isArray())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/get_empty_activity_state_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @ParameterizedTest
  @CsvSource({",567", "RT3,"})
  void shouldGetActivityStateBadRequestInvalidParams(ArgumentsAccessor argumentsAccessor)
      throws Exception {

    LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    String studyIdArg =
        argumentsAccessor.getString(0) == null ? "" : argumentsAccessor.getString(0);
    String participantIdArg =
        argumentsAccessor.getString(1) == null ? "" : argumentsAccessor.getString(1);
    requestParams.add("studyId", studyIdArg);
    requestParams.add("participantId", participantIdArg);

    HttpHeaders headers = TestUtils.newHeadersUser();
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

  @Test
  void shouldUpdateActivityState() throws Exception {

    String inputJsonContent = readJsonFile("/update_activity_state_runs_info_request.json");
    // Step 1: Call API to update activity state
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .content(inputJsonContent)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    ActivityStateRequestBean inputActivityStateBean =
        getObjectMapper().readValue(inputJsonContent, ActivityStateRequestBean.class);
    String studyId = inputActivityStateBean.getStudyId();
    String participantId = inputActivityStateBean.getParticipantId();

    // Step 2: verify updated values
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
        inputActivityStateBean.getActivity().get(0).getActivityState(),
        resultsList.get(0).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getTotal(),
        resultsList.get(0).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getCompleted(),
        resultsList.get(0).getCompleted());
  }

  @Test
  void shouldUpdateActivityStateMultiple() throws Exception {

    String inputJsonContent =
        readJsonFile("/update_activity_state_runs_info__multiple_request.json");
    // Step 1: Call API to update activity state
    HttpHeaders headers = TestUtils.newHeadersUser();
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ACTIVITY_STATE.getPath())
                .contextPath(getContextPath())
                .content(inputJsonContent)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    // Step 2: verify updated values
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
        inputActivityStateBean.getActivity().get(0).getActivityState(),
        resultsList.get(0).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getTotal(),
        resultsList.get(0).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(0).getActivityRun().getCompleted(),
        resultsList.get(0).getCompleted());

    // Validate 2nd activity state
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityId(),
        resultsList.get(1).getActivityId());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityVersion(),
        resultsList.get(1).getActivityVersion());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityState(),
        resultsList.get(1).getActivityState());

    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRun().getTotal(),
        resultsList.get(1).getTotal());
    assertEquals(
        inputActivityStateBean.getActivity().get(1).getActivityRun().getCompleted(),
        resultsList.get(1).getCompleted());
  }

  @AfterEach
  void cleanUp() {
    participantActivitiesRepository.deleteAll();
  }
}
