/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.CONTACT_US_CONTENT_EMAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.FEEDBACK_CONTENT_EMAILED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.service.UserSupportService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

public class UserSupportControllerTest extends BaseMockIT {

  private static final String FEEDBACK_PATH = "/myStudiesUserMgmtWS/feedback";

  private static final String CONTACT_US_PATH = "/myStudiesUserMgmtWS/contactUs";

  @Autowired private UserSupportController controller;

  @Autowired private UserSupportService service;

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

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    String requestJson = getFeedBackDetails(Constants.SUBJECT, Constants.BODY);

    mockMvc
        .perform(
            post(FEEDBACK_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(Constants.SUCCESS)));

    String subject = appConfig.getFeedbackMailSubject() + Constants.SUBJECT;
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("body", Constants.BODY);
    templateArgs.put("orgName", appConfig.getOrgName());
    String body =
        PlaceholderReplacer.replaceNamedPlaceholders(appConfig.getFeedbackMailBody(), templateArgs);

    verifyMimeMessage(appConfig.getFeedbackToEmail(), appConfig.getFromEmail(), subject, body);

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(FEEDBACK_CONTENT_EMAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, FEEDBACK_CONTENT_EMAILED);
  }

  @Test
  public void shouldSendEmailForContactUs() throws Exception {
    appConfig.setContactusToEmail("contactus_app_test@grr.la");

    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.USER_ID_HEADER);

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

    String subject = appConfig.getContactusMailSubject() + Constants.SUBJECT;
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("firstName", Constants.FIRST_NAME);
    templateArgs.put("email", Constants.EMAIL_ID);
    templateArgs.put("subject", Constants.SUBJECT);
    templateArgs.put("body", Constants.BODY);
    templateArgs.put("orgName", appConfig.getOrgName());
    String body =
        PlaceholderReplacer.replaceNamedPlaceholders(
            appConfig.getContactusMailBody(), templateArgs);

    verifyMimeMessage(appConfig.getContactusToEmail(), appConfig.getFromEmail(), subject, body);

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(CONTACT_US_CONTENT_EMAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, CONTACT_US_CONTENT_EMAILED);
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
