/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_FAILED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_DELETED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_PROFILE_UPDATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.testutils.Constants.DEVICE_TOKEN;
import static com.google.cloud.healthcare.fdamystudies.testutils.Constants.IOS;
import static com.google.cloud.healthcare.fdamystudies.testutils.Constants.IOS_APP_VERSION;
import static com.google.cloud.healthcare.fdamystudies.testutils.Constants.UPDATED_IOS_APP_VERSION;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.InfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordBean;
import com.google.cloud.healthcare.fdamystudies.beans.SettingsRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class UserProfileControllerTest extends BaseMockIT {

  private static final String USER_PROFILE_PATH = "/participant-user-datastore/userProfile";

  private static final String STUDY_VERSION = "3.6";

  private static final String UPDATE_USER_PROFILE_PATH =
      "/participant-user-datastore/updateUserProfile";

  private static final String DEACTIVATE_PATH = "/participant-user-datastore/deactivate";

  private static final String RESEND_CONFIRMATION_PATH =
      "/participant-user-datastore/resendConfirmation";

  @Autowired private UserProfileController profileController;

  @Autowired private UserManagementProfileService profileService;

  @Autowired private FdaEaUserDetailsServiceImpl service;

  @Autowired private ObjectMapper objectMapper;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawUrl;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private AuthInfoRepository authInfoRepository;

  @Autowired private AppRepository appRepository;

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

    Optional<AppEntity> optApp = appRepository.findById("1");
    Optional<UserDetailsEntity> optUserDetails = userDetailsRepository.findById("45");

    // insert new record having same device token in auth_info table
    AuthInfoEntity newAuthInfo = new AuthInfoEntity();
    newAuthInfo.setApp(optApp.get());
    newAuthInfo.setDeviceToken(DEVICE_TOKEN);
    newAuthInfo.setIosAppVersion(IOS_APP_VERSION);
    newAuthInfo.setUserDetails(optUserDetails.get());
    newAuthInfo = authInfoRepository.saveAndFlush(newAuthInfo);

    Optional<AuthInfoEntity> optAuthInfo = authInfoRepository.findById("222");
    optAuthInfo.get().setDeviceToken(DEVICE_TOKEN);
    authInfoRepository.saveAndFlush(optAuthInfo.get());

    List<AuthInfoEntity> authInfoListOnBeforeUpdate =
        authInfoRepository.findByDeviceToken(DEVICE_TOKEN);
    assertEquals(2, authInfoListOnBeforeUpdate.size());

    InfoBean infoBean = new InfoBean(IOS, UPDATED_IOS_APP_VERSION, DEVICE_TOKEN);

    SettingsRespBean settingRespBean = new SettingsRespBean(true, true, true, true, "", "");
    UserRequestBean userRequestBean = new UserRequestBean(settingRespBean, infoBean);
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

    // Fetching data using same device token and expecting only one record
    List<AuthInfoEntity> authInfoListOnSuccessUpdate =
        authInfoRepository.findByDeviceToken(DEVICE_TOKEN);

    assertEquals(1, authInfoListOnSuccessUpdate.size());

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
  public void updateUserProfileWithoutSettingsBeanSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    UserRequestBean userRequestBean = new UserRequestBean(null, new InfoBean());
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

    String emailId = JsonPath.read(result.getResponse().getContentAsString(), "$.profile.emailId");
    assertEquals(Constants.USER_EMAIL, emailId);

    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void shouldReturnUnauthorizedForUpdateUserProfile() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.set(Constants.USER_ID_HEADER, IdGenerator.id());
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
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.USER_NOT_EXISTS.getDescription())));

    verifyTokenIntrospectRequest(1);
  }

  @Test
  public void deactivateAccountSuccess() throws Exception {
    String veryLongEmail = RandomStringUtils.randomAlphabetic(300) + "@grr.la";
    Optional<UserDetailsEntity> optUserDetails =
        userDetailsRepository.findByUserId(Constants.USER_ID);
    UserDetailsEntity userDetails = optUserDetails.get();
    userDetails.setEmail(veryLongEmail);
    userDetailsRepository.saveAndFlush(userDetails);

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    headers.set(Constants.USER_ID_HEADER, Constants.USER_ID);

    Optional<StudyEntity> optStudyEntity = studyRepository.findByCustomStudyId(Constants.STUDY_ID);
    if (optStudyEntity.isPresent()) {
      StudyEntity studyEntity = optStudyEntity.get();
      studyEntity.setVersion(Float.valueOf(STUDY_VERSION));
      studyRepository.saveAndFlush(studyEntity);
    }

    StudyReqBean studyReqBean = new StudyReqBean(Constants.STUDY_ID);
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
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest(1);

    UserDetailsEntity daoResp = service.loadUserDetailsByUserId(Constants.USER_ID);
    assertNotNull(daoResp);
    assertTrue(daoResp.getEmail().length() == CommonConstants.EMAIL_LENGTH);
    assertTrue(daoResp.getEmail().contains("_DEACTIVATED_"));

    Optional<ParticipantStudyEntity> participant =
        participantStudyRepository.findByStudyIdAndUserId(Constants.STUDY_INFO_ID, daoResp.getId());
    assertNotNull(participant.get().getWithdrawalDate());

    assertTrue(
        participant
            .get()
            .getParticipantRegistrySite()
            .getOnboardingStatus()
            .equals(OnboardingStatus.DISABLED.getCode()));
    assertNotNull(participant.get().getParticipantRegistrySite().getDisabledDate());

    verify(1, deleteRequestedFor(urlEqualTo("/auth-server/users/" + Constants.USER_ID)));
    verify(
        1,
        postRequestedFor(
            urlEqualTo(
                "/response-datastore/participant/withdraw?studyId=studyId1&studyVersion=3.6"
                    + "&participantId=4")));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.USER_ID);
    auditRequest.setStudyId(Constants.STUDY_ID);
    auditRequest.setStudyVersion(STUDY_VERSION);
    auditRequest.setParticipantId("4");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_DELETED.getEventCode(), auditRequest);
    auditEventMap.put(WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, USER_DELETED, WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE);
  }

  @Test
  public void deactivateAccountIsNotFound() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    // invalid userId, expect user not found error from auth server
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
        .andExpect(status().isNotFound());

    verifyTokenIntrospectRequest(1);
  }

  @Test
  public void resendConfirmationBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    // without email
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(asJsonString(new ResetPasswordBean("")))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // invalid email
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(asJsonString(new ResetPasswordBean(Constants.INVALID_EMAIL)))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // without appId
    headers.set(Constants.APP_ID_HEADER, "");
    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(asJsonString(new ResetPasswordBean(Constants.VALID_EMAIL)))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error_description", is(ErrorCode.APP_NOT_FOUND.getDescription())));
  }

  @Test
  public void resendConfirmationSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.APP_ID_HEADER);

    mockMvc
        .perform(
            post(RESEND_CONFIRMATION_PATH)
                .content(asJsonString(new ResetPasswordBean(Constants.VALID_EMAIL)))
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.SUCCESS)));

    List<UserDetailsEntity> listOfUserDetails =
        userDetailsRepository.findByEmail(Constants.VALID_EMAIL);
    String subject = appConfig.getConfirmationMailSubject();
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("securitytoken", listOfUserDetails.get(0).getEmailCode());
    templateArgs.put("orgName", appConfig.getOrgName());
    templateArgs.put("contactEmail", appConfig.getContactEmail());
    String body =
        PlaceholderReplacer.replaceNamedPlaceholders(appConfig.getConfirmationMail(), templateArgs);

    verifyMimeMessage(Constants.VALID_EMAIL, appConfig.getFromEmail(), subject, body);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.USER_ID);
    auditRequest.setAppId(Constants.APP_ID_VALUE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
