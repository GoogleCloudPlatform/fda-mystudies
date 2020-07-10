/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.DevicePlatform;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@TestMethodOrder(OrderAnnotation.class)
public class LoginControllerTest extends BaseMockIT {

  private static final String LOGIN_CHALLENGE_VALUE = "d9d3ff8a-0c93-466a-bc4f-bf8b0d3d5453";

  private static final String AUTO_SIGNIN_LOGIN_CHALLENGE_VALUE =
      "117eb076-23cf-4653-a76d-14ec1ead4317";

  protected static final String USER_ID_VALUE = "4e626d41-7f42-43a6-b749-ee4b6635ac66";

  protected static final String TEMP_REG_ID_VALUE = "ec2045a1-0cd3-4998-b515-7f9703dff5bf";

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private UserRepository userRepository;

  @Test
  public void shouldReturnLoginPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Login</title>")))
        .andReturn();
  }

  @Test
  public void shouldReturnErrorPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Error</title>")))
        .andReturn();
  }

  @Test
  public void shouldRedirectToCallbackUrl() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("code", AUTH_CODE_VALUE);

    Cookie devicePlatformCookie = new Cookie(DEVICE_PLATFORM, DevicePlatform.UNKNOWN.getValue());
    Cookie userIdCookie = new Cookie(USER_ID, USER_ID_VALUE);

    String callbackUrl = redirectConfig.getCallbackUrl(DevicePlatform.UNKNOWN.getValue());
    String expectedRedirectUrl =
        String.format("%s?code=%s&userId=%s", callbackUrl, AUTH_CODE_VALUE, USER_ID_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams)
                .cookie(devicePlatformCookie, userIdCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(expectedRedirectUrl))
        .andReturn();
  }

  @Test
  public void shouldReturnAutoSigninPage() throws Exception {
    // Step-1 user registration
    UserEntity user = new UserEntity();
    user.setEmail("mockit_email@grr.la");
    user.setOrgId("FDA");
    user.setAppId("MyStudies");
    user.setStatus(UserAccountStatus.ACTIVE.getStatus());
    user.setTempRegId(TEMP_REG_ID_VALUE);
    // UserInfo JSON contains password hash & salt, password history etc
    ObjectNode userInfo = JsonUtils.getObjectNode().put("password", PasswordGenerator.generate(12));
    user.setUserInfo(userInfo.toString());
    userRepository.saveAndFlush(user);

    // Step-2 redirect to auto signin page after signup
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, AUTO_SIGNIN_LOGIN_CHALLENGE_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Please wait</title>")))
        .andReturn();
  }
}
