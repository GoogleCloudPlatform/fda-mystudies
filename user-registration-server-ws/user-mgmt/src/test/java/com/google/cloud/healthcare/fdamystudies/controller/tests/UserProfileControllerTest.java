package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.bean.StudyReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.InfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.LoginBean;
import com.google.cloud.healthcare.fdamystudies.beans.SettingsRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.config.AppConfig;
import com.google.cloud.healthcare.fdamystudies.controller.UserProfileController;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;

@TestMethodOrder(OrderAnnotation.class)
public class UserProfileControllerTest extends BaseMockIT {

  private static final String PING_PATH = "/ping";
  private static final String USER_PROFILE_PATH = "/userProfile";
  private static final String UPDATE_USER_PROFILE_PATH = "/updateUserProfile";
  private static final String DEACTIVATE_PATH = "/deactivate";
  private static final String RESEND_CONFIRMATION_PATH = "/resendConfirmation";

  @Autowired private UserProfileController profileController;

  @Autowired private UserManagementProfileService profileService;

  @Autowired private FdaEaUserDetailsServiceImpl service;

  @Autowired private AppConfig appconfig;

  @Value("${auth.server.deactivateUrl}")
  private String deactivateUrl;

  @Value("${response.server.url.participant.withdraw}")
  private String withdrawUrl;

  @Order(1)
  @Test
  public void contextLoads() {
    assertNotNull(profileController);
    assertNotNull(mockMvc);
    assertNotNull(profileService);
    assertNotNull(service);
  }

  @Order(2)
  @Test
  public void ping() throws Exception {
    performGet(PING_PATH, TestUtils.getCommonHeaders(Constants.USER_ID_HEADER), OK);
  }

  @Order(3)
  @Test
  public void getUserProfileSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    performGet(USER_PROFILE_PATH, headers, "cdash93@gmail.com", OK);
  }

  @Order(4)
  @Test
  public void getUserProfileBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    // Invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    performGet(USER_PROFILE_PATH, headers, BAD_REQUEST);
  }

  @Order(5)
  @Test
  public void updateUserProfileSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    SettingsRespBean settingRespBean = new SettingsRespBean(true, true, true, true, "", "");
    UserRequestBean userRequestBean = new UserRequestBean(settingRespBean, new InfoBean());
    String requestJson = getObjectMapper().writeValueAsString(userRequestBean);
    performPost(
        UPDATE_USER_PROFILE_PATH, requestJson, headers, String.valueOf(HttpStatus.OK.value()), OK);
    MvcResult result = performGet(USER_PROFILE_PATH, headers, OK);
    boolean remote =
        JsonPath.read(result.getResponse().getContentAsString(), "$.settings.remoteNotifications");
    assertTrue(remote);
  }

  @Order(6)
  @Test
  public void deactivateAccountSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    StudyReqBean studyReqBean = new StudyReqBean(Constants.STUDY_ID, Constants.DELETE);
    List<StudyReqBean> list = new ArrayList<StudyReqBean>();
    list.add(studyReqBean);
    DeactivateAcctBean acctBean = new DeactivateAcctBean(list);
    String requestJson = getObjectMapper().writeValueAsString(acctBean);
    performDelete(DEACTIVATE_PATH, requestJson, headers, Constants.SUCCESS, OK);

    UserDetailsBO daoResp = service.loadUserDetailsByUserId(Constants.VALID_USER_ID);
    assertEquals(3, daoResp.getStatus());

    verify(1, postRequestedFor(urlEqualTo("/AuthServer/deactivate")));
    verify(
        1,
        postRequestedFor(
            urlEqualTo(
                "/mystudies-response-server/participant/withdraw?studyId=studyId1&participantId=1&deleteResponses=delete")));
  }

  @Order(7)
  @Test
  public void deactivateAccountBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    // invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    DeactivateAcctBean acctBean = new DeactivateAcctBean();
    String requestJson = getObjectMapper().writeValueAsString(acctBean);
    performDelete(DEACTIVATE_PATH, requestJson, headers, "", BAD_REQUEST);

    verify(2, postRequestedFor(urlEqualTo("/AuthServer/deactivate")));
  }

  @Order(8)
  @Test
  public void resendConfirmationBadRequest() throws Exception {

    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    // without email
    String requestJson = getLoginBean("", Constants.PASSWORD);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // invalid email
    requestJson = getLoginBean(Constants.INVALID_EMAIL, Constants.PASSWORD);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);

    // without appId
    headers.set(Constants.APP_ID_HEADER, "");
    requestJson = getLoginBean(Constants.EMAIL_ID, Constants.PASSWORD);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, "", BAD_REQUEST);
  }

  @Order(9)
  @Test
  public void resendConfirmationSuccess() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(Constants.APP_ID_HEADER, Constants.ORG_ID_HEADER);

    String requestJson = getLoginBean(Constants.VALID_EMAIL, Constants.PASSWORD);
    performPost(RESEND_CONFIRMATION_PATH, requestJson, headers, Constants.SUCCESS, OK);

    verify(appconfig.emailNotification(), times(1))
        .sendEmailNotification(
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.anyString(),
            Mockito.any(),
            Mockito.any());
  }

  private String getLoginBean(String emailId, String password) throws JsonProcessingException {
    LoginBean loginBean = new LoginBean(emailId, password);
    return getObjectMapper().writeValueAsString(loginBean);
  }
}
