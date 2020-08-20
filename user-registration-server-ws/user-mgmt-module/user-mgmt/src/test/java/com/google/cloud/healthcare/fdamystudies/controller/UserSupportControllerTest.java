package com.google.cloud.healthcare.fdamystudies.controller;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.service.UserSupportService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class UserSupportControllerTest extends BaseMockIT {

  private static final String FEEDBACK_PATH = "/myStudiesUserMgmtWS/feedback";

  private static final String CONTACT_US_PATH = "/myStudiesUserMgmtWS/contactUs";

  @Autowired private UserSupportController controller;

  @Autowired private UserSupportService service;

  @Autowired private EmailNotification emailNotification;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(service);
  }

  @Test
  public void shouldSendFeedbackEmail() throws Exception {
    appConfig.setFeedbackToEmail("feedback_app_test@grr.la");
    Mockito.when(
            emailNotification.sendEmailNotification(
                Mockito.anyString(),
                Mockito.anyString(),
                eq(appConfig.getFeedbackToEmail()),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(true);

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    String requestJson = getFeedBackDetails(Constants.SUBJECT, Constants.BODY);

    mockMvc
        .perform(
            post(FEEDBACK_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(Constants.SUCCESS)));

    verify(emailNotification, times(1))
        .sendEmailNotification(
            Mockito.anyString(),
            Mockito.anyString(),
            eq(appConfig.getFeedbackToEmail()),
            Mockito.any(),
            Mockito.any());
  }

  @Test
  public void shouldSendEmailForContactUs() throws Exception {
    appConfig.setContactusToEmail("contactus_app_test@grr.la");
    Mockito.when(
            emailNotification.sendEmailNotification(
                Mockito.anyString(),
                Mockito.anyString(),
                eq(appConfig.getContactusToEmail()),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(true);

    HttpHeaders headers =
        TestUtils.getCommonHeaders(
            Constants.APP_ID_HEADER,
            Constants.ORG_ID_HEADER,
            Constants.CLIENT_ID_HEADER,
            Constants.SECRET_KEY_HEADER,
            Constants.USER_ID_HEADER);

    String requestJson =
        getContactUsRequest(
            Constants.SUBJECT, Constants.BODY, Constants.FIRST_NAME, Constants.EMAIL_ID);

    mockMvc
        .perform(
            post(CONTACT_US_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(Constants.SUCCESS)));

    verify(emailNotification, times(1))
        .sendEmailNotification(
            Mockito.anyString(),
            Mockito.anyString(),
            eq(appConfig.getContactusToEmail()),
            Mockito.any(),
            Mockito.any());
  }

  private String getContactUsRequest(String subject, String body, String firstName, String email)
      throws JsonProcessingException {
    ContactUsReqBean contactUsReqBean = new ContactUsReqBean(subject, body, firstName, email);
    return getObjectMapper().writeValueAsString(contactUsReqBean);
  }

  private String getFeedBackDetails(String subject, String body) throws JsonProcessingException {
    FeedbackReqBean feedbackReqBean = new FeedbackReqBean(subject, body);
    return getObjectMapper().writeValueAsString(feedbackReqBean);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
