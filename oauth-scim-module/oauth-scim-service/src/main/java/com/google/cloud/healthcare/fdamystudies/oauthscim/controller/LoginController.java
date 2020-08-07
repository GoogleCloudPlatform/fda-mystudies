/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.RequestParamValidator.validateRequiredParams;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.APP_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_APP_VERSION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.EMAIL;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PASSWORD;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_TO;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SIGNUP_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SKIP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_REG_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.CookieHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

@Controller
public class LoginController {

  private static final String AUTO_LOGIN = "autoLogin";

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  private static final String LOGIN = "login";

  @Autowired private OAuthService oauthService;

  @Autowired private UserService userService;

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private CookieHelper cookieHelper;

  /**
   * @param loginChallenge is optional. ORY Hydra sends this field as query param when login/consent
   *     flow is initiated.
   * @param code ORY Hydra redirects to this path again when the login/consent flow is completed.
   *     ORY Hydra sends authorization 'code' and no login challenge in query params.
   * @param request
   * @param response
   * @param model
   * @return
   */
  @GetMapping(value = "/login")
  public String login(
      @RequestParam(name = LOGIN_CHALLENGE, required = false) String loginChallenge,
      @RequestParam(required = false) String code,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model) {
    logger.entry(String.format("%s request", request.getRequestURI()));

    // login/consent flow completed
    if (StringUtils.isNotBlank(code)) {
      logger.exit(
          "login/consent flow completed, redirect to callbackUrl with auth code and userId");
      return redirectToCallbackUrl(request, code, response);
    }

    // login/consent flow initiated
    if (StringUtils.isEmpty(loginChallenge)) {
      return redirectConfig.getDefaultErrorUrl();
    }

    // show or skip login page
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add(LOGIN_CHALLENGE, loginChallenge);
    ResponseEntity<JsonNode> loginResponse = oauthService.requestLogin(paramMap);
    if (loginResponse.getStatusCode().is2xxSuccessful()) {
      JsonNode responseBody = loginResponse.getBody();
      if (skipLogin(responseBody)) {
        logger.exit("skip login, return to callback URL");
        return redirectToCallbackUrl(request, code, response);
      }
      return redirectToLoginOrSigninPage(response, responseBody, model, loginChallenge);
    }

    return redirectToError(request, response);
  }

  /**
   * @param email
   * @param password
   * @param tempRegId optional field. It enables auto sign-in after signup.
   * @param appId cookie value
   * @param loginChallenge cookie value
   * @param request
   * @param response
   * @param model
   * @return
   * @throws JsonProcessingException
   */
  @PostMapping(value = "/login")
  public String authenticate(
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String password,
      @CookieValue(name = TEMP_REG_ID, required = false) String tempRegId,
      @CookieValue(name = APP_ID) String appId,
      @CookieValue(name = LOGIN_CHALLENGE) String loginChallenge,
      @CookieValue(name = DEVICE_PLATFORM) String devicePlatform,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model)
      throws JsonProcessingException {
    logger.entry(String.format("%s request", request.getRequestURI()));

    if (StringUtils.isNotEmpty(tempRegId)) {
      return autoSignInOrReturnLoginPage(tempRegId, loginChallenge, request, response);
    }

    // validate user credentials
    ValidationErrorResponse errors = validateRequiredParams(request, EMAIL, PASSWORD);
    if (errors.hasErrors()) {
      logger.exit(String.format("validation errors=%s", errors));
      model.addAttribute(ERROR_DESCRIPTION, ErrorCode.INVALID_LOGIN_CREDENTIALS.getDescription());
      return LOGIN;
    }

    UserRequest user = new UserRequest();
    user.setEmail(email);
    user.setPassword(password);
    user.setAppId(appId);

    AuthenticationResponse authenticationResponse = userService.authenticate(user);

    if (ErrorCode.PENDING_CONFIRMATION
        .getDescription()
        .equalsIgnoreCase(authenticationResponse.getErrorDescription())) {
      String redirectUrl = redirectConfig.getAccountActivationUrl(devicePlatform);
      String url =
          String.format(
              "%s?userId=%s&accountStatus=%s",
              redirectUrl,
              authenticationResponse.getUserId(),
              authenticationResponse.getAccountStatus());
      return redirect(response, url);
    }

    if (authenticationResponse.is2xxSuccessful()) {
      logger.exit("authentication success, redirect to consent page");
      cookieHelper.addCookie(response, USER_ID, authenticationResponse.getUserId());
    } else {
      logger.error(
          String.format(
              "authentication failed with error %s", authenticationResponse.getErrorDescription()));

      model.addAttribute(ERROR_DESCRIPTION, authenticationResponse.getErrorDescription());
      return LOGIN;
    }

    return redirectToConsentPage(loginChallenge, email, request, response);
  }

  private String autoSignInOrReturnLoginPage(
      String tempRegId,
      String loginChallenge,
      HttpServletRequest request,
      HttpServletResponse response) {
    Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
    if (!optUser.isPresent()) {
      logger.exit("tempRegId is invalid, return to login page");
      cookieHelper.deleteCookie(response, TEMP_REG_ID);
      return LOGIN;
    } else {
      UserEntity user = optUser.get();
      logger.exit("tempRegId is valid, return to consent page");
      cookieHelper.addCookie(response, USER_ID, user.getUserId());
      userService.resetTempRegId(user.getUserId());
      return redirectToConsentPage(loginChallenge, user.getEmail(), request, response);
    }
  }

  private boolean skipLogin(JsonNode responseBody) {
    return responseBody.has(SKIP) && responseBody.get(SKIP).booleanValue();
  }

  private String redirectToConsentPage(
      String loginChallenge,
      String email,
      HttpServletRequest request,
      HttpServletResponse response) {
    ResponseEntity<JsonNode> result = oauthService.loginAccept(email, loginChallenge);
    if (result.getStatusCode().is2xxSuccessful()) {
      String redirectUrl = JsonUtils.getTextValue(result.getBody(), REDIRECT_TO);
      return redirect(response, redirectUrl);
    }
    return redirectToError(request, response);
  }

  private String redirectToLoginOrSigninPage(
      HttpServletResponse response, JsonNode responseBody, Model model, String loginChallenge) {

    String requestUrl = responseBody.get("request_url").textValue();
    MultiValueMap<String, String> qsParams =
        UriComponentsBuilder.fromUriString(requestUrl).build().getQueryParams();

    cookieHelper.addCookie(response, LOGIN_CHALLENGE, loginChallenge);
    cookieHelper.addCookies(
        response,
        qsParams,
        APP_ID,
        CORRELATION_ID,
        CLIENT_APP_VERSION,
        DEVICE_TYPE,
        DEVICE_PLATFORM,
        TEMP_REG_ID);

    String tempRegId = qsParams.getFirst(TEMP_REG_ID);
    String devicePlatform = qsParams.getFirst(DEVICE_PLATFORM);
    model.addAttribute(LOGIN_CHALLENGE, loginChallenge);
    model.addAttribute(FORGOT_PASSWORD_LINK, redirectConfig.getForgotPasswordUrl(devicePlatform));
    model.addAttribute(SIGNUP_LINK, redirectConfig.getSignupUrl(devicePlatform));

    // tempRegId for auto signin after signup
    if (StringUtils.isNotEmpty(tempRegId)) {
      Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
      if (optUser.isPresent()) {
        UserEntity user = optUser.get();
        logger.exit("tempRegId is valid, return to auto signin page");
        cookieHelper.addCookie(response, USER_ID, user.getUserId());
        return AUTO_LOGIN;
      }

      logger.exit("tempRegId is invalid, return to login page");
      cookieHelper.deleteCookie(response, TEMP_REG_ID);
      return LOGIN;
    }
    return LOGIN;
  }

  private String redirectToCallbackUrl(
      HttpServletRequest request, String code, HttpServletResponse response) {
    String userId = WebUtils.getCookie(request, USER_ID).getValue();
    String devicePlatform = WebUtils.getCookie(request, DEVICE_PLATFORM).getValue();
    String callbackUrl = redirectConfig.getCallbackUrl(devicePlatform);

    String redirectUrl = String.format("%s?code=%s&userId=%s", callbackUrl, code, userId);

    logger.exit(String.format("redirect to %s from /login", callbackUrl));
    return redirect(response, redirectUrl);
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  private String redirectToError(HttpServletRequest request, HttpServletResponse response) {
    String devicePlatform = WebUtils.getCookie(request, DEVICE_PLATFORM).getValue();
    String redirectUrl = redirectConfig.getErrorUrl(devicePlatform);
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }
}
