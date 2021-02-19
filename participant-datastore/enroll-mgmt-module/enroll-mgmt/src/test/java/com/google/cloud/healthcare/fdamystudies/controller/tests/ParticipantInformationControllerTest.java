/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.ParticipantInformationController;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class ParticipantInformationControllerTest extends BaseMockIT {

  private static final String PARTICIPANT_ID = "3";

  @Autowired private ParticipantInformationController controller;

  @Autowired private ParticipantInformationService participantInfoService;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(participantInfoService);
  }

  @Test
  public void getParticipantDetailsSuccess() throws Exception {
    Optional<ParticipantStudyEntity> optParticipantStudy =
        participantStudyRepository.findByParticipantId("i4ts7dsf50c6me154sfsdfdv");

    if (optParticipantStudy.isPresent()) {
      ParticipantStudyEntity participantStudy = optParticipantStudy.get();
      participantStudy.setParticipantId(PARTICIPANT_ID);
      participantStudyRepository.saveAndFlush(participantStudy);
    }

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", PARTICIPANT_ID)
                .param("studyId", Constants.STUDYOF_HEALTH_CLOSE)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setParticipantId(PARTICIPANT_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH_CLOSE);
    auditRequest.setStudyVersion("3.3");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS);

    verifyTokenIntrospectRequest();
  }

  @Test
  public void getParticipantDetailsFailure() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);

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

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(Constants.STUDY_ID_OF_PARTICIPANT);
    auditRequest.setStudyVersion("3.5");
    auditRequest.setParticipantId(Constants.PARTICIPANT_ID_NOT_EXISTS);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS);

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

    verifyTokenIntrospectRequest(3);
  }
}
