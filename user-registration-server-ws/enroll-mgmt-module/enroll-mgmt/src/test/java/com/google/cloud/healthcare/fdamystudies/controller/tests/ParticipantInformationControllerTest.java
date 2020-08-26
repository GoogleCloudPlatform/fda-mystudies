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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.ParticipantInformationController;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class ParticipantInformationControllerTest extends BaseMockIT {

  @Autowired private ParticipantInformationController controller;

  @Autowired private ParticipantInformationService participantInfoService;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(participantInfoService);
  }

  @Test
  public void getParticipantDetailsSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", Constants.PARTICIPANT_ID)
                .param("studyId", Constants.STUDY_ID_OF_PARTICIPANT)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest();
  }

  @Test
  public void getParticipantDetailsFailure() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // participant id null
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", "")
                .param("studyId", Constants.STUDY_ID_OF_PARTICIPANT)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // study id null
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", Constants.PARTICIPANT_ID)
                .param("studyId", "")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // participant id not exists
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", Constants.PARTICIPANT_ID_NOT_EXISTS)
                .param("studyId", Constants.STUDY_ID_OF_PARTICIPANT)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(3);
  }
}
