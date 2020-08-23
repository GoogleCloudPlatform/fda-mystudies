package com.google.cloud.healthcare.fdamystudies.controller.tests;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.ParticipantInformationController;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    HttpHeaders headers = TestUtils.getCommonHeaderValues();
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .headers(headers)
                .param("participantId", Constants.PARTICIPANT_ID)
                .param("studyId", Constants.STUDY_ID_OF_PARTICIPANT)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setParticipantId(Constants.PARTICIPANT_ID);
    auditRequest.setStudyId(Constants.STUDY_ID_OF_PARTICIPANT);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS);
  }

  @Test
  public void getParticipantDetailsFailure() throws Exception {

    // participant id null
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .param("participantId", "")
                .param("studyId", Constants.STUDY_ID_OF_PARTICIPANT)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
    HttpHeaders headers = TestUtils.getCommonHeaderValues();

    // study id null
    mockMvc
        .perform(
            get(ApiEndpoint.PARTICIPANT_INFO.getPath())
                .param("participantId", Constants.PARTICIPANT_ID)
                .param("studyId", "")
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

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

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS.getEventCode(), auditRequest);

    verifyAuditEventCall(READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS);
  }
}
