package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

import com.google.cloud.healthcare.fdamystudies.beans.InfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.SettingsRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.config.AppConfig;
import com.google.cloud.healthcare.fdamystudies.controller.UserProfileController;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;

public class UserProfileControllerTest extends BaseMockIT {

  private static final String PING_PATH = "/ping";

  private static final String USER_PROFILE_PATH = "/userProfile";

  private static final String UPDATE_USER_PROFILE_PATH = "/updateUserProfile";

  @Autowired private UserProfileController profileController;

  @Autowired private UserManagementProfileService profileService;

  @Autowired private FdaEaUserDetailsServiceImpl service;

  @Autowired private AppConfig appconfig;

  @Test
  public void contextLoads() {
    assertNotNull(profileController);
    assertNotNull(mockMvc);
    assertNotNull(profileService);
    assertNotNull(service);
  }

  @Test
  public void ping() throws Exception {
    performGet(PING_PATH, TestUtils.getCommonHeaders(Constants.USER_ID_HEADER), OK);
  }

  @Test
  public void getUserProfileSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);
    performGet(USER_PROFILE_PATH, headers, "cdash93@gmail.com", OK);
  }

  @Test
  public void getUserProfileBadRequest() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders(Constants.USER_ID_HEADER);

    // Invalid userId
    headers.set(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);
    performGet(USER_PROFILE_PATH, headers, BAD_REQUEST);
  }

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
}
