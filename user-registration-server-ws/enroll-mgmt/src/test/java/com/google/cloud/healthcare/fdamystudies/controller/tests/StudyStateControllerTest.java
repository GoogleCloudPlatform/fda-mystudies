package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.StudyStateController;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.jayway.jsonpath.JsonPath;

public class StudyStateControllerTest extends BaseMockIT {

  private static final String WITHDRAW_FROM_STUDY_PATH = "/withdrawfromstudy";
  private static final String STUDY_STATE_PATH = "/studyState";
  private static final String UPDATE_STUDY_STATE_PATH = "/updateStudyState";
  @Autowired private StudyStateController controller;
  @Autowired private StudyStateService studyStateService;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(studyStateService);
  }

  @Test
  public void updateStudyStateSuccess() throws Exception {

    StudiesBean studiesBean =
        new StudiesBean(
            Constants.STUDYOF_HEALTH,
            Constants.BOOKMARKED,
            Constants.COMPLETION,
            Constants.ADHERENCE);

    List<StudiesBean> listStudies = new ArrayList<StudiesBean>();
    listStudies.add(studiesBean);

    String requestJson = getStudyStateJson(listStudies);

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    performPost(UPDATE_STUDY_STATE_PATH, requestJson, headers, Constants.SUCCESS, OK);

    MvcResult result = performGet(STUDY_STATE_PATH, headers, Constants.SUCCESS.toUpperCase(), OK);
    List<Map<String, Object>> studyList =
        JsonPath.read(result.getResponse().getContentAsString(), "$.studies[*]");
    for (Map<String, Object> studyMap : studyList) {
      if (studyMap.get("studyId").equals(Constants.STUDYOF_HEALTH)) {
        assertTrue((boolean) studyMap.get("bookmarked").equals(Constants.BOOKMARKED));
        assertTrue((int) studyMap.get("completion") == Constants.COMPLETION);
        assertTrue((int) studyMap.get("adherence") == Constants.ADHERENCE);
        break;
      }
    }
  }

  @Test
  public void updateStudyStateFailure() throws Exception {

    StudiesBean studiesBean =
        new StudiesBean(
            Constants.STUDYOF_HEALTH,
            Constants.BOOKMARKED,
            Constants.COMPLETION,
            Constants.ADHERENCE);

    List<StudiesBean> listStudies = new ArrayList<StudiesBean>();
    listStudies.add(studiesBean);

    String requestJson = getStudyStateJson(listStudies);

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // not valid user id
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    performPost(UPDATE_STUDY_STATE_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty studylist
    listStudies = new ArrayList<StudiesBean>();
    requestJson = getStudyStateJson(listStudies);
    headers.set(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    performPost(UPDATE_STUDY_STATE_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void getStudyStateSuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    performGet(STUDY_STATE_PATH, headers, Constants.SUCCESS.toUpperCase(), OK);
  }

  @Test
  public void getStudyStateUnauthorizedUserId() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    performGet(STUDY_STATE_PATH, headers, "", UNAUTHORIZED);
  }

  @Test
  public void withdrawFromStudySuccess() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    String requestJson =
        getWithDrawJson(
            Constants.PARTICIPANT_ID, Constants.STUDY_ID_OF_PARTICIPANT, Constants.DELETE);
    performPost(
        WITHDRAW_FROM_STUDY_PATH, requestJson, headers, Constants.SUCCESS.toUpperCase(), OK);

    MvcResult result = performGet(STUDY_STATE_PATH, headers, Constants.SUCCESS.toUpperCase(), OK);
    List<Map<String, String>> studyList =
        JsonPath.read(result.getResponse().getContentAsString(), "$.studies[*]");
    for (Map<String, String> studyMap : studyList) {
      if (studyMap.get("participantId").equals(Constants.PARTICIPANT_ID)) {
        assertTrue(studyMap.get("status").equals(AppConstants.WITHDRAWN));
        break;
      }
    }
  }

  @Test
  public void withdrawFromStudyFailure() throws Exception {

    // empty participant Id
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);

    String requestJson = getWithDrawJson("", Constants.STUDY_ID_OF_PARTICIPANT, Constants.DELETE);
    performPost(WITHDRAW_FROM_STUDY_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty study Id
    requestJson = getWithDrawJson(Constants.PARTICIPANT_ID, "", Constants.DELETE);
    performPost(WITHDRAW_FROM_STUDY_PATH, requestJson, headers, "", BAD_REQUEST);

    // study Id not exists
    requestJson =
        getWithDrawJson(Constants.PARTICIPANT_ID, Constants.STUDYID_NOT_EXIST, Constants.DELETE);
    performPost(WITHDRAW_FROM_STUDY_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  private String getWithDrawJson(String participatId, String studyId, boolean delete)
      throws JsonProcessingException {
    WithdrawFromStudyBean withdrawFromStudyBean =
        new WithdrawFromStudyBean(participatId, studyId, delete);
    return getObjectMapper().writeValueAsString(withdrawFromStudyBean);
  }

  private String getStudyStateJson(List<StudiesBean> listStudies) throws JsonProcessingException {
    StudyStateReqBean studyStateReqBean = new StudyStateReqBean(listStudies);
    return getObjectMapper().writeValueAsString(studyStateReqBean);
  }
}
