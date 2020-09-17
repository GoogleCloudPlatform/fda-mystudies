/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.DATA_RETENTION_SETTING_CAPTURED_ON_WITHDRAWAL;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.PARTICIPANT_DATA_DELETED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_FAILED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_ACCOUNT_DEACTIVATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_PROFILE_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.InfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.LoginBean;
import com.google.cloud.healthcare.fdamystudies.beans.SettingsRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class UserProfileControllerTest extends BaseMockIT {

  private static final String USER_PROFILE_PATH = "/myStudiesUserMgmtWS/userProfile";

  private static final String UPDATE_USER_PROFILE_PATH = "/myStudiesUserMgmtWS/updateUserProfile";

  private static final String DEACTIVATE_PATH = "/myStudiesUserMgmtWS/deactivate";

  private static final String RESEND_CONFIRMATION_PATH = "/myStudiesUserMgmtWS/resendConfirmation";

  @Autowired private UserProfileController profileController;

  @Autowired private UserManagementProfileService profileService;

  @Autowired private FdaEaUserDetailsServiceImpl service;

  @Autowired private EmailNotification emailNotification;

  @Autowired private ObjectMapper objectMapper;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawUrl;

  @Test
  public void contextLoads() {
    assertNotNull(profileController);
    assertNotNull(mockMvc);
    assertNotNull(profileService);
    assertNotNull(service);
  }

  @Test
  public void getUserProfileSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    mockMvc
        .perform(get(USER_PROFILE_PATH).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(content().string(containsString("cdash93@gmail.com")))
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE);
  }

  @Test
  public void getUserProfileBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    // Invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    mockMvc
        .perform(get(USER_PROFILE_PATH).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.INVALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(READ_OPERATION_FAILED_FOR_USER_PROFILE.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_FAILED_FOR_USER_PROFILE);
  }

  @Test
  public void updateUserProfileSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    SettingsRespBean settingRespBean = new SettingsRespBean(true, true, true, true, "", "");
    UserRequestBean userRequestBean = new UserRequestBean(settingRespBean, new InfoBean());
    String requestJson = getObjectMapper().writeValueAsString(userRequestBean);
    mockMvc
        .perform(
            post(UPDATE_USER_PROFILE_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(String.valueOf(HttpStatus.OK.value()))));

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_PROFILE_UPDATED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, USER_PROFILE_UPDATED);

    MvcResult result =
        mockMvc
            .perform(get(USER_PROFILE_PATH).headers(headers).contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    boolean remote =
        JsonPath.read(result.getResponse().getContentAsString(), "$.settings.remoteNotifications");
    assertTrue(remote);

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void deactivateAccountSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    StudyReqBean studyReqBean = new StudyReqBean(Constants.STUDY_ID, Constants.TRUE);
    List<StudyReqBean> list = new ArrayList<StudyReqBean>();
    list.add(studyReqBean);
    DeactivateAcctBean acctBean = new DeactivateAcctBean(list);
    String requestJson = getObjectMapper().writeValueAsString(acctBean);

    mockMvc
        .perform(
            delete(DEACTIVATE_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.SUCCESS)));

    verifyTokenIntrospectRequest(1);

    UserDetailsBO daoResp = service.loadUserDetailsByUserId(Constants.VALID_USER_ID);
    assertEquals(3, daoResp.getStatus());

    verify(1, putRequestedFor(urlEqualTo("/oauth-scim-service/users/" + Constants.VALID_USER_ID)));
    verify(
        1,
        postRequestedFor(
            urlEqualTo(
                "/mystudies-response-server/participant/withdraw?studyId=studyId1&participantId=1&deleteResponses=true")));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDY_ID);
    auditRequest.setParticipantId("1");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_ACCOUNT_DEACTIVATED.getEventCode(), auditRequest);
    auditEventMap.put(PARTICIPANT_DATA_DELETED.getEventCode(), auditRequest);
    auditEventMap.put(DATA_RETENTION_SETTING_CAPTURED_ON_WITHDRAWAL.getEventCode(), auditRequest);
    auditEventMap.put(WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_ACCOUNT_DEACTIVATED,
        PARTICIPANT_DATA_DELETED,
        DATA_RETENTION_SETTING_CAPTURED_ON_WITHDRAWAL,
        WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE);
  }

  @Test
  public void deactivateAccountBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.INVALID_USER_ID);

    // invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    DeactivateAcctBean acctBean = new DeactivateAcctBean();
    String requestJson = getObjectMapper().writeValueAsString(acctBean);
    mockMvc
        .perform(
            delete(DEACTIVATE_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(1);
  }

  @Test
  public void resendConfirmationBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    // without email
    String requestJson = getLoginBean("", Constants.PASSWORD);
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // invalid email
    requestJson = getLoginBean(Constants.INVALID_EMAIL, Constants.PASSWORD);
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // without appId
    headers.set(Constants.APP_ID_HEADER, "");
    requestJson = getLoginBean(Constants.EMAIL_ID, Constants.PASSWORD);
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
  }

  @Test
  public void resendConfirmationSuccess() throws Exception {

    Mockito.when(
            emailNotification.sendEmailNotification(
                Mockito.anyString(),
                Mockito.anyString(),
                eq(Constants.VALID_EMAIL),
                Mockito.any(),
                Mockito.any()))
        .thenReturn(true);

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    String requestJson = getLoginBean(Constants.VALID_EMAIL, Constants.PASSWORD);

    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.SUCCESS)));

    verify(emailNotification, times(1))
        .sendEmailNotification(
            Mockito.anyString(),
            Mockito.anyString(),
            eq(Constants.VALID_EMAIL),
            Mockito.any(),
            Mockito.any());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.USER_ID);
    auditRequest.setAppId(Constants.APP_ID_VALUE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED);
  }

  private String getLoginBean(String emailId, String password) throws JsonProcessingException {
    LoginBean loginBean = new LoginBean(emailId, password);
    return getObjectMapper().writeValueAsString(loginBean);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
