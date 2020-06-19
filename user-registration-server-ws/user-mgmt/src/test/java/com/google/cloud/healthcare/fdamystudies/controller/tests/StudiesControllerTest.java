package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.StudiesController;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;

public class StudiesControllerTest extends BaseMockIT {

  private static final String STUDY_METADATA_PATH = "/studies/studymetadata";
  private static final String SEND_NOTIFICATION_PATH = "/studies/sendNotification";

  @Autowired StudiesController studiesController;
  @Autowired StudiesServices studiesServices;

  @Test
  public void contextLoads() {
    assertNotNull(studiesController);
    assertNotNull(mockMvc);
    assertNotNull(studiesServices);
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
    // sample response={"code":200,"message":"Success"}
    // expect actual response contains 200
    performPost(STUDY_METADATA_PATH, requestJson, headers, String.valueOf(200), OK);
  }

  @Test
  public void addUpdateStudyMetadataFailure() throws Exception {

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
  public void sendNotificationFailure() throws Exception {

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
  public void sendNotificationSuccess() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.CLIENT_ID_HEADER, Constants.SECRET_KEY_HEADER);

    String requestJson =
        getNotificationForm(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDY_ID,
            Constants.APP_ID_HEADER,
            Constants.STUDY_LEVEL);
    // sample response={"code":200,"message":"Success"}
    // expect actual response contains 200
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, String.valueOf(200), OK);
  }

  private String getNotificationForm(
      String studyId, String customStudyId, String appId, String notificationType)
      throws JsonProcessingException {
    NotificationBean bean = new NotificationBean(studyId, customStudyId, appId, notificationType);
    List<NotificationBean> list = new ArrayList<NotificationBean>();
    list.add(bean);
    NotificationForm notificationForm = new NotificationForm(list);
    return getObjectMapper().writeValueAsString(notificationForm);
  }
}
