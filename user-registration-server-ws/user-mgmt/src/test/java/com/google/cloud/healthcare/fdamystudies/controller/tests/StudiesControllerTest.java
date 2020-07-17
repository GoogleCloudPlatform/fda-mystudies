package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.StudiesController;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDaoImpl;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;

public class StudiesControllerTest extends BaseMockIT {

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
