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
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;

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

  private String getStudyMetaDataJson(
      String studyId,
      String studyTitle,
      String studyVersion,
      String studyType,
      String studyStatus,
      String studyCategory,
      String studyTagline,
      String studySponser,
      String studyEnrolling,
      String appId,
      String appName,
      String appDescription,
      String orgId)
      throws JsonProcessingException {
    StudyMetadataBean study =
        new StudyMetadataBean(
            studyId,
            studyTitle,
            studyVersion,
            studyType,
            studyStatus,
            studyCategory,
            studyTagline,
            studySponser,
            studyEnrolling,
            appId,
            appName,
            appDescription,
            orgId);
    return getObjectMapper().writeValueAsString(study);
  }

  @Test
  public void addUpdateStudyMetadata() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();

    String requestJson =
        getStudyMetaDataJson(
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
    performPost(STUDY_METADATA_PATH, requestJson, headers, Constants.SUCCESS, OK);
  }

  @Test
  public void addUpdateStudyMetadataInvalidInput() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // without studyId
    String requestJson =
        getStudyMetaDataJson(
            "",
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
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without studyVersion
    requestJson =
        getStudyMetaDataJson(
            Constants.STUDY_ID,
            Constants.STUDY_TITLE,
            "",
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
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without appId
    requestJson =
        getStudyMetaDataJson(
            Constants.STUDY_ID,
            Constants.STUDY_TITLE,
            Constants.STUDY_VERSION,
            Constants.STUDY_TYPE,
            Constants.STUDY_STATUS,
            Constants.STUDY_CATEGORY,
            Constants.STUDY_TAGLINE,
            Constants.STUDY_SPONSOR,
            Constants.STUDY_ENROLLING,
            "",
            Constants.APP_NAME,
            Constants.APP_DESCRIPTION,
            Constants.ORG_ID_VALUE);
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);

    // without orgId
    requestJson =
        getStudyMetaDataJson(
            Constants.STUDY_ID,
            Constants.STUDY_TITLE,
            Constants.STUDY_VERSION,
            Constants.STUDY_TYPE,
            Constants.STUDY_STATUS,
            Constants.STUDY_CATEGORY,
            Constants.STUDY_TAGLINE,
            Constants.STUDY_SPONSOR,
            Constants.STUDY_ENROLLING,
            Constants.APP_ID_HEADER,
            Constants.APP_NAME,
            Constants.APP_DESCRIPTION,
            "");
    performPost(STUDY_METADATA_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void sendNotificationInvalidInput() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.CLIENT_ID_HEADER, Constants.SECRET_KEY_HEADER);

    // null body
    NotificationForm notificationForm = null;
    String requestJson = getObjectMapper().writeValueAsString(notificationForm);
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // empty appId and empty notificationType
    NotificationBean notificationBean =
        new NotificationBean(Constants.STUDY_ID, Constants.CUSTOM_STUDYID, null, null);
    List<NotificationBean> list = new ArrayList<NotificationBean>();
    list.add(notificationBean);
    notificationForm = new NotificationForm(list);
    requestJson = getObjectMapper().writeValueAsString(notificationForm);
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Test
  public void sendNotification() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.CLIENT_ID_HEADER, Constants.SECRET_KEY_HEADER);

    NotificationBean notificationBean =
        new NotificationBean(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDYID,
            Constants.APP_ID_HEADER,
            AppConstants.STUDY_LEVEL);
    List<NotificationBean> list = new ArrayList<NotificationBean>();
    list.add(notificationBean);
    NotificationForm notificationForm = new NotificationForm(list);
    String requestJson = getObjectMapper().writeValueAsString(notificationForm);
    performPost(SEND_NOTIFICATION_PATH, requestJson, headers, "", OK);
  }
}
