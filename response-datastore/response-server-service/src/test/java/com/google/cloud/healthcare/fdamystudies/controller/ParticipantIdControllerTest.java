/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.PARTICIPANT_ID_GENERATED;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.APPLICATION_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.APPLICATION_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.STUDY_ID;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.bean.EnrollmentTokenIdentifierBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantInfoRepository;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class ParticipantIdControllerTest extends BaseMockIT {
  @Autowired private ParticipantInfoRepository repository;

  @Test
  void shouldAddParticipant() throws Exception {
    // Step-1 call API to details to add participant id
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBeanRequest =
        createValidEnrollmentTokenIdentifierBean();
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollmentTokenIdentifierBeanRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty())
            .andReturn();

    String participantId = result.getResponse().getContentAsString();
    assertNotNull(participantId);
    // Step-2 Find ParticipantBo by participantId and compare with input ParticipantBo object
    List<ParticipantInfoEntity> participantBoList = repository.findByParticipantId(participantId);
    assertNotNull(participantBoList);
    assertEquals(1, participantBoList.size());
    assertEquals(
        enrollmentTokenIdentifierBeanRequest.getCustomStudyId(),
        participantBoList.get(0).getStudyId());
    assertEquals(
        enrollmentTokenIdentifierBeanRequest.getTokenIdentifier(),
        participantBoList.get(0).getTokenId());

    // Step-3 cleanup - delete the record from database
    repository.deleteAll();

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(APPLICATION_ID_VALUE);
    auditRequest.setParticipantId(participantId);
    auditRequest.setStudyId(STUDY_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(PARTICIPANT_ID_GENERATED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, PARTICIPANT_ID_GENERATED);
  }

  @Test
  void shouldReturnBadRequestForMissingApplicationId() throws Exception {
    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.remove("appId");
    mockMvc
        .perform(
            post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(createValidEnrollmentTokenIdentifierBean()))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  void shouldReturnBadRequestForBlankStudyId() throws Exception {

    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollTokenIdentifierBeanInvalidStudyId =
        new EnrollmentTokenIdentifierBean();
    enrollTokenIdentifierBeanInvalidStudyId.setCustomStudyId(" ");
    enrollTokenIdentifierBeanInvalidStudyId.setTokenIdentifier(UUID.randomUUID().toString());
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollTokenIdentifierBeanInvalidStudyId))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  @Test
  void shouldReturnBadRequestForBlankEnrollmentToken() throws Exception {

    HttpHeaders headers = TestUtils.newCommonHeaders();
    headers.add(APPLICATION_ID_HEADER, APPLICATION_ID_VALUE);
    EnrollmentTokenIdentifierBean enrollTokenIdentifierBeanInvalidStudyId =
        new EnrollmentTokenIdentifierBean();
    enrollTokenIdentifierBeanInvalidStudyId.setCustomStudyId(STUDY_ID);
    enrollTokenIdentifierBeanInvalidStudyId.setTokenIdentifier(" ");
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.ADD_PARTICIPANT.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(enrollTokenIdentifierBeanInvalidStudyId))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  private EnrollmentTokenIdentifierBean createValidEnrollmentTokenIdentifierBean() {
    EnrollmentTokenIdentifierBean enrollmentTokenIdentifierBean =
        new EnrollmentTokenIdentifierBean();
    enrollmentTokenIdentifierBean.setCustomStudyId(STUDY_ID);
    enrollmentTokenIdentifierBean.setTokenIdentifier(UUID.randomUUID().toString());
    return enrollmentTokenIdentifierBean;
  }
}
