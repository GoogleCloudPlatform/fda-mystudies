package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.StudiesController;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDaoImpl;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;

public class StudiesControllerTest extends BaseMockIT {

  private static final String STUDY_METADATA_PATH = "/studies/studymetadata";

  private static final String SEND_NOTIFICATION_PATH = "/studies/sendNotification";

  @Autowired private StudiesController studiesController;

  @Autowired private StudiesServices studiesServices;

  @Autowired private CommonDaoImpl commonDao;

  @Test
  public void contextLoads() {
    assertNotNull(studiesController);
    assertNotNull(mockMvc);
    assertNotNull(studiesServices);
    assertNotNull(commonDao);
  }

  public StudyMetadataBean createStudyMetadataBean() {
    return new StudyMetadataBean(
        Constants.STUDY_ID,
        Constants.STUDY_TITLE,
        Constants.STUDY_VERSION,
        Constants.STUDY_TYPE,
        Constants.STUDY_STATUS,
        Constants.STUDY_CATEGORY,
        Constants.STUDY_TAGLINE,
        Constants.STUDY_SPONSOR,
        Constants.STUDY_ENROLLING,
        Constants.APP_ID_VALUE,
        Constants.APP_NAME,
        Constants.APP_DESCRIPTION,
        Constants.ORG_ID_VALUE);
  }

  @Test
  public void addUpdateStudyMetadataSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    String requestJson = getObjectMapper().writeValueAsString(createStudyMetadataBean());
    performPost(
        STUDY_METADATA_PATH, requestJson, headers, String.valueOf(HttpStatus.OK.value()), OK);
    HashSet<String> set = new HashSet<>();
    set.add(Constants.STUDY_ID);
    List<StudyInfoBO> list = commonDao.getStudyInfoSet(set);
    StudyInfoBO studyInfoBo =
        list.stream()
            .filter(x -> x.getCustomId().equals(Constants.STUDY_ID))
            .findFirst()
            .orElse(null);
    assertNotNull(studyInfoBo);
    assertEquals(Constants.STUDY_SPONSOR, studyInfoBo.getSponsor());
    assertEquals(Constants.STUDY_TAGLINE, studyInfoBo.getTagline());
  }

  @Test
  public void addUpdateStudyMetadataBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // without studyId
    StudyMetadataBean metadataBean = createStudyMetadataBean();
    metadataBean.setStudyId("");
    String requestJson = getObjectMapper().writeValueAsString(metadataBean);
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without studyVersion
    metadataBean = createStudyMetadataBean();
    metadataBean.setStudyVersion("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without appId
    metadataBean = createStudyMetadataBean();
    metadataBean.setAppId("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without orgId
    metadataBean = createStudyMetadataBean();
    metadataBean.setOrgId("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void sendNotificationBadRequest() throws Exception {

    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.CLIENT_ID_HEADER, Constants.SECRET_KEY_HEADER);

    // null body
    NotificationForm notificationForm = null;
    String requestJson = getObjectMapper().writeValueAsString(notificationForm);
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty notificationType
    requestJson =
        getNotificationForm(
            Constants.STUDY_ID, Constants.CUSTOM_STUDY_ID, Constants.APP_ID_HEADER, "");
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  @Disabled
  // TODO(#668) Remove @Disabled when Github test case failed issue fix
  public void sendNotificationSuccess() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.CLIENT_ID_HEADER, Constants.SECRET_KEY_HEADER);

    // StudyLevel notificationType
    String requestJson =
        getNotificationForm(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDY_ID,
            Constants.APP_ID_VALUE,
            Constants.STUDY_LEVEL);

    mockMvc
        .perform(post(SEND_NOTIFICATION_PATH).content(requestJson).headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(ErrorCode.EC_200.errorMessage())))
        .andExpect(jsonPath("$.code", is(ErrorCode.EC_200.code())))
        .andExpect(jsonPath("$.response.multicast_id", greaterThan(0L)))
        .andExpect(
            jsonPath(
                "$.response.results[0].message_id", is("0:1491324495516461%31bd1c9631bd1c96")));

    // GatewayLevel notificationType
    requestJson =
        getNotificationForm(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDY_ID,
            Constants.APP_ID_VALUE,
            Constants.GATEWAY_LEVEL);

    mockMvc
        .perform(post(SEND_NOTIFICATION_PATH).content(requestJson).headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(ErrorCode.EC_200.errorMessage())))
        .andExpect(jsonPath("$.code", is(ErrorCode.EC_200.code())))
        .andExpect(jsonPath("$.response.multicast_id", greaterThan(0L)))
        .andExpect(
            jsonPath(
                "$.response.results[0].message_id", is("0:1491324495516461%31bd1c9631bd1c96")));
  }

  private String getNotificationForm(
      String studyId, String customStudyId, String appId, String notificationType)
      throws JsonProcessingException {

    NotificationBean notificationBean = null;
    notificationBean = new NotificationBean(studyId, customStudyId, appId, notificationType);
    List<NotificationBean> list = new ArrayList<NotificationBean>();
    list.add(notificationBean);
    NotificationForm notificationForm = new NotificationForm(list);
    return getObjectMapper().writeValueAsString(notificationForm);
  }
}
