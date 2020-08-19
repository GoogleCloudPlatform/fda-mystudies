/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ABOUT_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_STATUS_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.APP_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTO_LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EMAIL;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PRIVACY_POLICY_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SIGNUP_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TERMS_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.net.MalformedURLException;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class LoginControllerTest extends BaseMockIT {

  private static final int MAX_LOGIN_ATTEMPTS = 5;

  private static final String PASSWORD_VALUE = "0Auth_scim_service_mock!t";

  private static final String LOGIN_CHALLENGE_VALUE = "d9d3ff8a-0c93-466a-bc4f-bf8b0d3d5453";

  private static final String LOGIN_CHALLENGE_VALUE_FOR_ANDROID =
      "0fac4201-6c0a-4776-b745-ab07d428248c";

  private static final String AUTO_LOGIN_LOGIN_CHALLENGE_VALUE =
      "117eb076-23cf-4653-a76d-14ec1ead4317";

  private static final String USER_ID_VALUE = "4e626d41-7f42-43a6-b749-ee4b6635ac66";

  private static final String TEMP_REG_ID_VALUE = "ec2045a1-0cd3-4998-b515-7f9703dff5bf";

  private static final String APP_ID_VALUE = "MyStudies";

  private static final String EMAIL_VALUE = "mockit_oauth_scim_user@grr.la";

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private UserRepository userRepository;

  @Autowired private UserService userService;

  @Test
  public void shouldReturnLoginPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE);

    String forgotPasswordRedirectUrl =
        redirectConfig.getForgotPasswordUrl(MobilePlatform.UNKNOWN.getValue());
    String termsRedirectUrl = redirectConfig.getTermsUrl(MobilePlatform.UNKNOWN.getValue());
    String aboutRedirectUrl = redirectConfig.getAboutUrl(MobilePlatform.UNKNOWN.getValue());

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute(FORGOT_PASSWORD_LINK, forgotPasswordRedirectUrl))
        .andExpect(model().attribute(SIGNUP_LINK, nullValue()))
        .andExpect(model().attribute(PRIVACY_POLICY_LINK, nullValue()))
        .andExpect(model().attribute(TERMS_LINK, termsRedirectUrl))
        .andExpect(model().attribute(ABOUT_LINK, aboutRedirectUrl))
        .andExpect(content().string(containsString("<title>Login</title>")))
        .andReturn();
  }

  @Test
  public void shouldReturnLoginPageForAndroid() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE_FOR_ANDROID);
    String forgotPasswordRedirectUrl =
        redirectConfig.getForgotPasswordUrl(MobilePlatform.ANDROID.getValue());
    String signupRedirectUrl = redirectConfig.getSignupUrl(MobilePlatform.ANDROID.getValue());
    String termsRedirectUrl = redirectConfig.getTermsUrl(MobilePlatform.ANDROID.getValue());
    String privacyPolicyRedirectUrl =
        redirectConfig.getPrivacyPolicyUrl(MobilePlatform.ANDROID.getValue());
    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute(ABOUT_LINK, nullValue()))
        .andExpect(model().attribute(FORGOT_PASSWORD_LINK, forgotPasswordRedirectUrl))
        .andExpect(model().attribute(SIGNUP_LINK, signupRedirectUrl))
        .andExpect(model().attribute(TERMS_LINK, termsRedirectUrl))
        .andExpect(model().attribute(PRIVACY_POLICY_LINK, privacyPolicyRedirectUrl))
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
        .andExpect(view().name(ERROR_VIEW_NAME));
  }

  @Test
  public void shouldRedirectToCallbackUrl() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("code", AUTH_CODE_VALUE);

    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie userIdCookie = new Cookie(USER_ID_COOKIE, USER_ID_VALUE);
    Cookie accountStatusCookie = new Cookie(ACCOUNT_STATUS_COOKIE, "0");

    String callbackUrl = redirectConfig.getCallbackUrl(MobilePlatform.UNKNOWN.getValue());
    String expectedRedirectUrl =
        String.format(
            "%s?code=%s&userId=%s&accountStatus=0", callbackUrl, AUTH_CODE_VALUE, USER_ID_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams)
                .cookie(mobilePlatformCookie, userIdCookie, accountStatusCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(expectedRedirectUrl))
        .andReturn();
  }

  @Test
  public void shouldReturnAutoLoginPage() throws Exception {
    // Step-1 user registration
    UserEntity user = new UserEntity();
    user.setEmail("mockit_email@grr.la");
    user.setAppId("MyStudies");
    user.setStatus(UserAccountStatus.ACTIVE.getStatus());
    user.setTempRegId(TEMP_REG_ID_VALUE);
    // UserInfo JSON contains password hash & salt, password history etc
    ObjectNode userInfo = JsonUtils.getObjectNode().put("password", PasswordGenerator.generate(12));
    user.setUserInfo(userInfo);
    userRepository.saveAndFlush(user);

    // Step-2 redirect to auto login page after signup
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, AUTO_LOGIN_LOGIN_CHALLENGE_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name(AUTO_LOGIN_VIEW_NAME))
        .andExpect(content().string(containsString("<title>Please wait</title>")))
        .andReturn();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "test@b0ston",
        "TEST@B0STON",
        "TEST@BoSTON",
        "TEST@0a",
        "T3stB0ston",
        "Test @b0ston"
      })
  public void shouldReturnInvalidLoginCredentials(String password)
      throws MalformedURLException, JsonProcessingException, Exception {

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, password);

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie))
        .andDo(print())
        .andExpect(view().name(LOGIN_VIEW_NAME))
        .andExpect(
            content().string(containsString(ErrorCode.INVALID_LOGIN_CREDENTIALS.getDescription())));
  }

  @Test
  public void shouldAuthenticateTheUserAndRedirectToActivationPage() throws Exception {
    // Step-1 create a user account with PENDING_CONFIRMATION status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    userEntity = userRepository.saveAndFlush(userEntity);

    // Step-2 call API with login credentials
    String activationUrl =
        redirectConfig.getAccountActivationUrl(MobilePlatform.UNKNOWN.getValue());
    String expectedViedName =
        String.format("redirect:%s?email=%s", activationUrl, userEntity.getEmail());

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(expectedViedName));

    // Step-3 delete user account
    userRepository.delete(userEntity);
  }

  @Test
  public void shouldAuthenticateTheUserAndRedirectToConsentPage() throws Exception {
    // Step-1 create a user account with ACTIVE status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
    userRepository.saveAndFlush(userEntity);

    // Step-2 call API with login credentials
    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ApiEndpoint.CONSENT_PAGE.getUrl()));
  }

  @Test
  public void shouldReturnInvalidLoginCredentials() throws Exception {
    // Step-1 create a user account with ACTIVE status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
    userRepository.saveAndFlush(userEntity);

    // Step-2 call API with invalid login credentials
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie))
        .andDo(print())
        .andExpect(
            content().string(containsString(ErrorCode.INVALID_LOGIN_CREDENTIALS.getDescription())));
  }

  @Test
  public void shouldSendAccountLockedEmail() throws Exception {
    // Step-1 create a user account with ACTIVE status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());
    userEntity = userRepository.saveAndFlush(userEntity);

    // Step-2 call API for 5 times with invalid login credentials to lock the account
    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE + 1);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());

    ErrorCode expectedErrorCode = ErrorCode.INVALID_LOGIN_CREDENTIALS;
    for (int loginAttempts = 1; loginAttempts <= MAX_LOGIN_ATTEMPTS; loginAttempts++) {
      if (loginAttempts == MAX_LOGIN_ATTEMPTS) {
        expectedErrorCode = ErrorCode.ACCOUNT_LOCKED;
      }
      mockMvc
          .perform(
              post(ApiEndpoint.LOGIN_PAGE.getPath())
                  .contextPath(getContextPath())
                  .params(requestParams)
                  .cookie(appIdCookie, loginChallenge, mobilePlatformCookie))
          .andDo(print())
          .andExpect(content().string(containsString(expectedErrorCode.getDescription())));
    }

    // Step-3 expect account status changed to ACCOUNT_LOCKED
    userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    assertTrue(
        UserAccountStatus.ACCOUNT_LOCKED.equals(UserAccountStatus.valueOf(userEntity.getStatus())));
  }

  @AfterEach
  public void cleanUp() {
    userRepository.deleteAll();
  }

  private UserRequest newUserRequest() {
    UserRequest userRequest = new UserRequest();
    userRequest.setAppId(APP_ID_VALUE);
    userRequest.setEmail(EMAIL_VALUE);
    userRequest.setPassword(PASSWORD_VALUE);
    userRequest.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    return userRequest;
  }

  private MultiValueMap<String, String> getLoginRequestParamsMap() {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(EMAIL, EMAIL_VALUE);
    requestParams.add(PASSWORD, PASSWORD_VALUE);
    return requestParams;
  }
}
