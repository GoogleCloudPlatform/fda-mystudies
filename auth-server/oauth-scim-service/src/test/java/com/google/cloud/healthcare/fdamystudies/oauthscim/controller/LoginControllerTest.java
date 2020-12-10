/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.INVALID_LOGIN_CREDENTIALS;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.PASSWORD_EXPIRED;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.TEMP_PASSWORD_EXPIRED;
import static com.google.cloud.healthcare.fdamystudies.common.ErrorCode.USER_NOT_FOUND;
import static com.google.cloud.healthcare.fdamystudies.common.HashUtils.hash;
import static com.google.cloud.healthcare.fdamystudies.common.HashUtils.salt;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ABOUT_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_LOCKED_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_STATUS_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.APP_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTO_LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EMAIL;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EXPIRE_TIMESTAMP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.HASH;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.OTP_USED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PRIVACY_POLICY_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SALT;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SIGNUP_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SOURCE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_REG_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TERMS_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.ACCOUNT_LOCKED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_EXPIRED_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_INVALID_PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED_UNREGISTERED_USER;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.common.PasswordGenerator;
import com.google.cloud.healthcare.fdamystudies.common.PlaceholderReplacer;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
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

  protected static final String VALID_CORRELATION_ID = "8a56d20c-d755-4487-b80d-22d5fa383046";

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private UserRepository userRepository;

  @Autowired private UserService userService;

  @Autowired private AppPropertyConfig appPropertyConfig;

  @Test
  public void shouldReturnLoginPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE);

    HttpHeaders headers = getCommonHeaders();

    String forgotPasswordRedirectUrl =
        redirectConfig.getForgotPasswordUrl(
            MobilePlatform.UNKNOWN.getValue(), PlatformComponent.PARTICIPANT_MANAGER.getValue());
    String termsRedirectUrl =
        redirectConfig.getTermsUrl(
            MobilePlatform.UNKNOWN.getValue(), PlatformComponent.PARTICIPANT_MANAGER.getValue());
    String aboutRedirectUrl =
        redirectConfig.getAboutUrl(PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute(FORGOT_PASSWORD_LINK, forgotPasswordRedirectUrl))
        .andExpect(model().attribute(SIGNUP_LINK, nullValue()))
        .andExpect(model().attribute(PRIVACY_POLICY_LINK, nullValue()))
        .andExpect(model().attribute(TERMS_LINK, termsRedirectUrl))
        .andExpect(model().attribute(ABOUT_LINK, aboutRedirectUrl))
        .andExpect(content().string(containsString("<title>Sign in</title>")))
        .andReturn();
  }

  @Test
  public void shouldReturnLoginPageForAndroid() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE_FOR_ANDROID);

    HttpHeaders headers = getCommonHeaders();

    String forgotPasswordRedirectUrl =
        redirectConfig.getForgotPasswordUrl(
            MobilePlatform.ANDROID.getValue(), PlatformComponent.MOBILE_APPS.getValue());
    String signupRedirectUrl = redirectConfig.getSignupUrl(MobilePlatform.ANDROID.getValue());
    String termsRedirectUrl =
        redirectConfig.getTermsUrl(
            MobilePlatform.ANDROID.getValue(), PlatformComponent.MOBILE_APPS.getValue());
    String privacyPolicyRedirectUrl =
        redirectConfig.getPrivacyPolicyUrl(MobilePlatform.ANDROID.getValue());
    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(queryParams)
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(model().attribute(ABOUT_LINK, nullValue()))
        .andExpect(model().attribute(FORGOT_PASSWORD_LINK, forgotPasswordRedirectUrl))
        .andExpect(model().attribute(SIGNUP_LINK, signupRedirectUrl))
        .andExpect(model().attribute(TERMS_LINK, termsRedirectUrl))
        .andExpect(model().attribute(PRIVACY_POLICY_LINK, privacyPolicyRedirectUrl))
        .andExpect(content().string(containsString("<title>Sign in</title>")))
        .andReturn();
  }

  @Test
  public void shouldReturnErrorPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .headers(getCommonHeaders())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(view().name(ERROR_VIEW_NAME));

    verifyAuditEventCall(SIGNIN_FAILED);
  }

  @Test
  public void shouldAutoLoginPageFailedInvalidRegId() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(LOGIN_CHALLENGE, LOGIN_CHALLENGE_VALUE_FOR_ANDROID);

    HttpHeaders headers = getCommonHeaders();

    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie userIdCookie = new Cookie(USER_ID_COOKIE, USER_ID_VALUE);
    Cookie accountStatusCookie = new Cookie(ACCOUNT_STATUS_COOKIE, "0");
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            get(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .queryParams(queryParams)
                .cookie(mobilePlatformCookie, userIdCookie, accountStatusCookie, sourceCookie))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
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

    HttpHeaders headers = getCommonHeaders();

    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.LOGIN_PAGE.getPath())
                    .contextPath(getContextPath())
                    .headers(headers)
                    .queryParams(queryParams))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name(AUTO_LOGIN_VIEW_NAME))
            .andExpect(content().string(containsString("<title>Please wait</title>")))
            .andReturn();

    String accountStatus = result.getResponse().getCookie(ACCOUNT_STATUS_COOKIE).getValue();
    assertTrue(UserAccountStatus.ACTIVE.getStatus() == Integer.parseInt(accountStatus));
  }

  @Test
  public void shouldRedirectToConsentPageForAutoSignIn() throws Exception {
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

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie tempRegId = new Cookie(TEMP_REG_ID_COOKIE, TEMP_REG_ID_VALUE);
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();

    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.LOGIN_PAGE.getPath())
                    .contextPath(getContextPath())
                    .headers(headers)
                    .params(queryParams)
                    .cookie(
                        appIdCookie, loginChallenge, mobilePlatformCookie, tempRegId, sourceCookie))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl(ApiEndpoint.CONSENT_PAGE.getUrl()))
            .andReturn();

    String accountStatus = result.getResponse().getCookie(ACCOUNT_STATUS_COOKIE).getValue();
    assertTrue(UserAccountStatus.ACTIVE.getStatus() == Integer.parseInt(accountStatus));
  }

  @Test
  public void shouldRedirectToLoginPageForInvalidTempRegIdForAutoSignIn() throws Exception {
    // Step-1 user registration
    UserEntity user = new UserEntity();
    user.setEmail("mockit_email@grr.la");
    user.setAppId("MyStudies");
    user.setStatus(UserAccountStatus.ACTIVE.getStatus());
    user.setTempRegId(IdGenerator.id());
    // UserInfo JSON contains password hash & salt, password history etc
    ObjectNode userInfo = JsonUtils.getObjectNode().put("password", PasswordGenerator.generate(12));
    user.setUserInfo(userInfo);
    userRepository.saveAndFlush(user);

    // Step-2 redirect to auto login page after signup
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie tempRegId = new Cookie(TEMP_REG_ID_COOKIE, TEMP_REG_ID_VALUE);
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .params(queryParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, tempRegId, sourceCookie))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name(LOGIN_VIEW_NAME));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "testb0ston",
        "TEST/B0STON",
        "TEST.BoSTON",
        "TEST$0a",
        "T3stB0ston",
        "Test @b0ston"
      })
  public void shouldReturnInvalidLoginCredentials(String email)
      throws MalformedURLException, JsonProcessingException, Exception {

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(EMAIL, email);

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    String forgotPasswordRedirectUrl =
        redirectConfig.getForgotPasswordUrl(
            MobilePlatform.UNKNOWN.getValue(), PlatformComponent.PARTICIPANT_MANAGER.getValue());
    String termsRedirectUrl =
        redirectConfig.getTermsUrl(
            MobilePlatform.UNKNOWN.getValue(), PlatformComponent.PARTICIPANT_MANAGER.getValue());
    String aboutRedirectUrl =
        redirectConfig.getAboutUrl(PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(view().name(LOGIN_VIEW_NAME))
        .andExpect(content().string(containsString(forgotPasswordRedirectUrl)))
        .andExpect(content().string(containsString(termsRedirectUrl)))
        .andExpect(content().string(containsString(aboutRedirectUrl)))
        .andExpect(content().string(containsString(INVALID_LOGIN_CREDENTIALS.getDescription())));
  }

  @Test
  public void shouldAuthenticateTheUserAndRedirectToActivationPage() throws Exception {
    // Step-1 create a user account with PENDING_CONFIRMATION status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.PENDING_CONFIRMATION.getStatus());
    userEntity = userRepository.saveAndFlush(userEntity);

    HttpHeaders headers = getCommonHeaders();

    // Step-2 call API with login credentials
    String activationUrl =
        redirectConfig.getAccountActivationUrl(
            MobilePlatform.UNKNOWN.getValue(), PlatformComponent.PARTICIPANT_MANAGER.getValue());
    String expectedViedName =
        String.format("redirect:%s?email=%s", activationUrl, userEntity.getEmail());

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(view().name(expectedViedName));

    // Step-3 delete user account
    userRepository.delete(userEntity);
  }

  @Test
  public void shouldAuthenticateTempPassAndRedirectToActivationPage() throws Exception {
    // Step-1 create a user account with PENDING_CONFIRMATION status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity = userRepository.saveAndFlush(userEntity);

    HttpHeaders headers = getCommonHeaders();

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection());

    // Step-3 delete user account
    userRepository.delete(userEntity);
  }

  @Test
  public void shouldAuthenticateTempPassAndRedirectToLoginPage() throws Exception {
    // Step-1 create a user account with ACCOUNT_LOCKED status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());

    JsonNode userInfo = userEntity.getUserInfo();
    String rawSalt = salt();
    String hashValue = hash(PASSWORD_VALUE, rawSalt);
    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hashValue);
    passwordNode.put(SALT, rawSalt);
    passwordNode.put(EXPIRE_TIMESTAMP, Instant.now().plus(Duration.ofMinutes(15)).toEpochMilli());
    ((ObjectNode) userInfo).set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    userEntity.setUserInfo(userInfo);

    userEntity = userRepository.saveAndFlush(userEntity);

    HttpHeaders headers = getCommonHeaders();

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(EMAIL, EMAIL_VALUE);
    requestParams.add(PASSWORD, PASSWORD_VALUE);

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection());

    // Step-3 delete user account
    userRepository.delete(userEntity);
  }

  @Test
  public void shouldAuthenticateResetPasswordAndRedirectToLoginPage() throws Exception {
    // Step-1 create a user account with PASSWORD_RESET status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.PASSWORD_RESET.getStatus());

    JsonNode userInfo = userEntity.getUserInfo();
    String rawSalt = salt();
    String hashValue = hash(PASSWORD_VALUE, rawSalt);
    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hashValue);
    passwordNode.put(SALT, rawSalt);
    passwordNode.put(EXPIRE_TIMESTAMP, Instant.now().plus(Duration.ofMinutes(15)).toEpochMilli());
    ((ObjectNode) userInfo).set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    userEntity.setUserInfo(userInfo);

    userEntity = userRepository.saveAndFlush(userEntity);

    HttpHeaders headers = getCommonHeaders();

    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(EMAIL, EMAIL_VALUE);
    requestParams.add(PASSWORD, PASSWORD_VALUE);

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection());

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

    HttpHeaders headers = getCommonHeaders();

    // Step-2 call API with login credentials
    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();

    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
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
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .params(requestParams)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(INVALID_LOGIN_CREDENTIALS.getDescription())));
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
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();
    headers.add("userId", userEntity.getUserId());

    for (int loginAttempts = 1; loginAttempts <= MAX_LOGIN_ATTEMPTS; loginAttempts++) {

      mockMvc
          .perform(
              post(ApiEndpoint.LOGIN_PAGE.getPath())
                  .contextPath(getContextPath())
                  .params(requestParams)
                  .headers(headers)
                  .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
          .andDo(print())
          .andExpect(view().name(LOGIN_VIEW_NAME));

      AuditLogEventRequest auditRequest = new AuditLogEventRequest();
      auditRequest.setUserId(userEntity.getUserId());

      Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
      auditEventMap.put(SIGNIN_FAILED_INVALID_PASSWORD.getEventCode(), auditRequest);
      verifyAuditEventCall(auditEventMap, SIGNIN_FAILED_INVALID_PASSWORD);

      if (loginAttempts == MAX_LOGIN_ATTEMPTS) {
        verifyAuditEventCall(ACCOUNT_LOCKED);
      }
      // Reset Audit Event calls
      clearAuditRequests();
      auditEventMap.clear();
    }

    // Step-3 expect account status changed to ACCOUNT_LOCKED
    userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    assertTrue(
        UserAccountStatus.ACCOUNT_LOCKED.equals(UserAccountStatus.valueOf(userEntity.getStatus())));

    String subject = getMailAccountLockedSubject();
    String body =
        String.join(
            "<br/>",
            "This is to inform you that, as a security measure, your user account for MyStudies app",
            "has been temporarily locked for a period of 15 minutes, due to multiple consecutive failed sign-in",
            "attempts with incorrect password.");

    MimeMessage mail =
        verifyMimeMessage(EMAIL_VALUE, appPropertyConfig.getFromEmail(), subject, body);
    verifyDoesNotContain(mail.getContent().toString(), "@tempPassword@", "@appId");
  }

  private String getMailAccountLockedSubject() {
    Map<String, String> templateArgs = new HashMap<>();
    return PlaceholderReplacer.replaceNamedPlaceholders(
        appPropertyConfig.getMailAccountLockedSubject(), templateArgs);
  }

  @Test
  public void shouldRespondPasswordExpiredForLockedAccount() throws Exception {
    // Step-1 create a user account with ACCOUNT_LOCKED status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACCOUNT_LOCKED.getStatus());

    // Attempt to login within 15 min. of Account locked
    JsonNode userInfo = userEntity.getUserInfo();
    String rawSalt = salt();
    String hashValue = hash(ACCOUNT_LOCKED_PASSWORD, rawSalt);
    ObjectNode passwordNode = getObjectNode();
    passwordNode.put(HASH, hashValue);
    passwordNode.put(SALT, rawSalt);
    passwordNode.put(EXPIRE_TIMESTAMP, Instant.now().plus(Duration.ofMinutes(15)).toEpochMilli());
    passwordNode.put(OTP_USED, true);
    ((ObjectNode) userInfo).set(ACCOUNT_LOCKED_PASSWORD, passwordNode);
    userEntity.setUserInfo(userInfo);
    userEntity = userRepository.saveAndFlush(userEntity);

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();
    headers.add("userId", userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(TEMP_PASSWORD_EXPIRED.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD);
  }

  @Test
  public void checkLoginFailedInvalidCredentials() throws Exception {
    // Step-1 create a user account with ACTIVE status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.PASSWORD_RESET.getStatus());
    userEntity = userRepository.saveAndFlush(userEntity);

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE + 1);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();
    headers.add("userId", userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(INVALID_LOGIN_CREDENTIALS.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED.getEventCode(), auditRequest);
    auditEventMap.put(SIGNIN_FAILED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED, SIGNIN_FAILED);
  }

  @Test
  public void shouldReturnPasswordExpiredErrorCode() throws Exception {
    // Step-1 create a user account with ACTIVE status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.ACTIVE.getStatus());

    JsonNode userInfo = userEntity.getUserInfo();
    ObjectNode passwordNode = (ObjectNode) userInfo.get(PASSWORD);
    passwordNode.put("expire_timestamp", Instant.now().minus(Duration.ofDays(1)).toEpochMilli());
    passwordNode.put("otp_used", false);
    userEntity.setUserInfo(userInfo);
    userEntity = userRepository.saveAndFlush(userEntity);

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();
    headers.add("userId", userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(PASSWORD_EXPIRED.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_FAILED_EXPIRED_PASSWORD.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_FAILED_EXPIRED_PASSWORD);
  }

  @Test
  public void shouldReturnTempPasswordExpiredErrorCode() throws Exception {
    // Step-1 create a user account with PASSWORD_RESET status
    UserResponse userResponse = userService.createUser(newUserRequest());
    UserEntity userEntity = userRepository.findByUserId(userResponse.getUserId()).get();
    userEntity.setStatus(UserAccountStatus.PASSWORD_RESET.getStatus());

    JsonNode userInfo = userEntity.getUserInfo();
    ObjectNode nameNode = (ObjectNode) userInfo.get(PASSWORD);
    nameNode.put("expire_timestamp", Instant.now().minus(Duration.ofDays(1)).toEpochMilli());
    nameNode.put("otp_used", false);
    userEntity.setUserInfo(userInfo);
    userEntity = userRepository.saveAndFlush(userEntity);

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();
    headers.add("userId", userEntity.getUserId());

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(TEMP_PASSWORD_EXPIRED.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(userEntity.getUserId());
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD);
  }

  @Test
  public void checkSigninFailureUnregisteredUser() throws Exception {

    MultiValueMap<String, String> requestParams = getLoginRequestParamsMap();
    requestParams.set(PASSWORD, PASSWORD_VALUE + 1);
    Cookie appIdCookie = new Cookie(APP_ID_COOKIE, "MyStudies");
    Cookie loginChallenge = new Cookie(LOGIN_CHALLENGE_COOKIE, LOGIN_CHALLENGE_VALUE);
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie sourceCookie =
        new Cookie(SOURCE_COOKIE, PlatformComponent.PARTICIPANT_MANAGER.getValue());

    HttpHeaders headers = getCommonHeaders();

    mockMvc
        .perform(
            post(ApiEndpoint.LOGIN_PAGE.getPath())
                .contextPath(getContextPath())
                .params(requestParams)
                .headers(headers)
                .cookie(appIdCookie, loginChallenge, mobilePlatformCookie, sourceCookie))
        .andDo(print())
        .andExpect(content().string(containsString(USER_NOT_FOUND.getDescription())));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_FAILED_UNREGISTERED_USER.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_FAILED_UNREGISTERED_USER);
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

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add("appVersion", "1.0");
    headers.add("appId", PlatformComponent.PARTICIPANT_MANAGER.getValue());
    headers.add("studyId", "MyStudies");
    headers.add("source", PlatformComponent.PARTICIPANT_MANAGER.getValue());
    headers.add("correlationId", IdGenerator.id());
    return headers;
  }
}
