package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.ParticipantInformationController;
import com.google.cloud.healthcare.fdamystudies.service.ParticipantInformationService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;

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

    String path =
        String.format(
            "/participantInfo?participantId=%s&studyId=%s",
            Constants.PARTICIPANT_ID, Constants.STUDY_ID_OF_PARTICIPANT);

    performGet(path, new HttpHeaders(), Constants.SUCCESS, OK);
  }

  @Test
  public void getParticipantDetailsFailure() throws Exception {

    // participant id null
    String path =
        String.format(
            "/participantInfo?participantId=%s&studyId=%s", "", Constants.STUDY_ID_OF_PARTICIPANT);

    performGet(path, new HttpHeaders(), "", BAD_REQUEST);

    // study id null
    path =
        String.format("/participantInfo?participantId=%s&studyId=%s", Constants.PARTICIPANT_ID, "");

    performGet(path, new HttpHeaders(), "", BAD_REQUEST);

    // participant id not exists
    path =
        String.format(
            "/participantInfo?participantId=%s&studyId=%s",
            Constants.PARTICIPANT_ID_NOT_EXISTS, Constants.STUDY_ID_OF_PARTICIPANT);

    performGet(path, new HttpHeaders(), "", BAD_REQUEST);
  }
}
