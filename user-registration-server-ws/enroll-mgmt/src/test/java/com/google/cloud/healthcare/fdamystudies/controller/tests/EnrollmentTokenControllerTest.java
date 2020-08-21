package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.EnrollmentTokenController;
import com.google.cloud.healthcare.fdamystudies.service.EnrollmentTokenService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class EnrollmentTokenControllerTest extends BaseMockIT {

  private static final String PING_PATH = "/ping";
  private static final String VALIDATE_ENROLLMENT_TOKEN_PATH = "/validateEnrollmentToken";
  private static final String ENROLL_PATH = "/enroll";

  @Autowired private EnrollmentTokenController controller;
  @Autowired private EnrollmentTokenService enrollmentTokenService;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(enrollmentTokenService);
  }

  @Test
  public void ping() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    performGet(PING_PATH, headers, "", OK);
  }

  @Test
  public void validateEnrollmentTokenSuccess() throws Exception {

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, Constants.SUCCESS, OK);
  }

  @Test
  public void validateEnrollmentTokenBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // without study id
    String requestJson = getEnrollmentJson(Constants.TOKEN, null);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", BAD_REQUEST);

    // without token
    requestJson = getEnrollmentJson(null, Constants.STUDYOF_HEALTH_CLOSE);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", BAD_REQUEST);

    // unknown token id
    requestJson = getEnrollmentJson(Constants.UNKOWN_TOKEN, Constants.STUDYOF_HEALTH);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void validateEnrollmentTokenForbidden() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);

    // study id not exists
    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYID_NOT_EXIST);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", FORBIDDEN);

    // token already use
    requestJson = getEnrollmentJson(Constants.TOKEN_ALREADY_USED, Constants.STUDYOF_HEALTH_1);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", FORBIDDEN);
  }

  @Test
  public void validateEnrollmentTokenUnAuthorised() throws Exception {
    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson = getEnrollmentJson(Constants.TOKEN, Constants.STUDYOF_HEALTH);
    performPost(VALIDATE_ENROLLMENT_TOKEN_PATH, requestJson, headers, "", UNAUTHORIZED);
  }

  @Test
  public void enrollParticipantSuccessStudyTypeClose() throws Exception {

    // study type close
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH_CLOSE);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    performPost(ENROLL_PATH, requestJson, headers, Constants.SUCCESS, OK);
  }

  @Test
  public void enrollParticipantSuccessStudyTypeOpen() throws Exception {
    // study type open
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    performPost(ENROLL_PATH, requestJson, headers, Constants.SUCCESS, OK);
  }

  @Test
  public void enrollParticipantSuccessNewUser() throws Exception {
    // new user id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.NEW_USER_ID);

    performPost(ENROLL_PATH, requestJson, headers, Constants.SUCCESS, OK);
  }

  @Test
  public void enrollParticipantBadRequests() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // without study id
    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, null);
    performPost(ENROLL_PATH, requestJson, headers, "", BAD_REQUEST);

    // without token
    requestJson = getEnrollmentJson(null, Constants.STUDYOF_HEALTH_CLOSE);
    performPost(ENROLL_PATH, requestJson, headers, "", BAD_REQUEST);

    // unknown token id

    requestJson = getEnrollmentJson(Constants.UNKOWN_TOKEN, Constants.STUDYOF_HEALTH_CLOSE);
    performPost(ENROLL_PATH, requestJson, headers, Constants.UNKOWN_TOKEN_MESSAGE, BAD_REQUEST);
  }

  @Test
  public void enrollParticipantUnauthorized() throws Exception {

    // without userId header
    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYOF_HEALTH);
    performPost(ENROLL_PATH, requestJson, headers, "", UNAUTHORIZED);
  }

  @Test
  public void enrollParticipantForbidden() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    // token already use
    String requestJson =
        getEnrollmentJson(Constants.ENROLL_TOKEN_ALREADY_USED, Constants.STUDYOF_HEALTH_CLOSE);
    performPost(ENROLL_PATH, requestJson, headers, Constants.TOKEN_USED_MESSAGE, FORBIDDEN);

    // study id not exists
    requestJson = getEnrollmentJson(Constants.TOKEN_NEW, Constants.STUDYID_NOT_EXIST);
    performPost(ENROLL_PATH, requestJson, headers, "", FORBIDDEN);
  }

  private String getEnrollmentJson(String tokenId, String studyId) throws JsonProcessingException {
    EnrollmentBean enrollmentBean = new EnrollmentBean(tokenId, studyId);
    return getObjectMapper().writeValueAsString(enrollmentBean);
  }
}
