/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.RequestParamValidator.validateRequiredParams;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ABOUT_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.APP_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTO_LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_APP_VERSION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CORRELATION_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_TYPE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PRIVACY_POLICY_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_TO;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SIGNUP_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SKIP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_REG_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TERMS_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.oauthscim.beans.LoginRequest;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.CookieHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.WebUtils;

@Controller
public class LoginController {

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  @Autowired private OAuthService oauthService;

  @Autowired private UserService userService;

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private CookieHelper cookieHelper;

  @Autowired private AppPropertyConfig appConfig;

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

    model.addAttribute("loginRequest", new LoginRequest());

    // login/consent flow completed
    if (StringUtils.isNotBlank(code)) {
      logger.exit(
          "login/consent flow completed, redirect to callbackUrl with auth code and userId");
      return redirectToCallbackUrl(request, code, response);
    }

    // login/consent flow initiated
    if (StringUtils.isEmpty(loginChallenge)) {
      return ERROR_VIEW_NAME;
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
      return redirectToLoginOrAutoLoginPage(response, responseBody, model, loginChallenge);
    }

    return ERROR_VIEW_NAME;
  }

  /**
   * @param LoginRequest email and password requests params binded to loginRequest object
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
      @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
      BindingResult bindingResult,
      Model model,
      @CookieValue(name = TEMP_REG_ID, required = false) String tempRegId,
      @CookieValue(name = APP_ID) String appId,
      @CookieValue(name = LOGIN_CHALLENGE) String loginChallenge,
      @CookieValue(name = DEVICE_PLATFORM) String mobilePlatform,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException {
    logger.entry(String.format("%s request", request.getRequestURI()));

    if (StringUtils.isNotEmpty(tempRegId)) {
      return autoLoginOrReturnLoginPage(tempRegId, loginChallenge, request, response);
    }

    // validate login credentials
    ValidationErrorResponse errors = validateRequiredParams(request, "email", "password");
    if (bindingResult.hasErrors() || errors.hasErrors()) {
      logger.exit(String.format("login validation errors=%s", ErrorCode.INVALID_LOGIN_CREDENTIALS));
      model.addAttribute(ERROR_DESCRIPTION, ErrorCode.INVALID_LOGIN_CREDENTIALS.getDescription());
      return LOGIN_VIEW_NAME;
    }

    UserRequest user = new UserRequest();
    user.setEmail(loginRequest.getEmail());
    user.setPassword(loginRequest.getPassword());
    user.setAppId(appId);

    AuthenticationResponse authenticationResponse = userService.authenticate(user);

    if (ErrorCode.PENDING_CONFIRMATION
        .getDescription()
        .equalsIgnoreCase(authenticationResponse.getErrorDescription())) {
      String redirectUrl = redirectConfig.getAccountActivationUrl(mobilePlatform);
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
      return LOGIN_VIEW_NAME;
    }
    return redirectToConsentPage(
        loginChallenge, authenticationResponse.getUserId(), request, response);
  }

  private String autoLoginOrReturnLoginPage(
      String tempRegId,
      String loginChallenge,
      HttpServletRequest request,
      HttpServletResponse response) {
    Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
    if (!optUser.isPresent()) {
      logger.exit("tempRegId is invalid, return to login page");
      cookieHelper.deleteCookie(response, TEMP_REG_ID);
      return LOGIN_VIEW_NAME;
    } else {
      UserEntity user = optUser.get();
      logger.exit("tempRegId is valid, return to consent page");
      cookieHelper.addCookie(response, USER_ID, user.getUserId());
      userService.resetTempRegId(user.getUserId());
      return redirectToConsentPage(loginChallenge, user.getUserId(), request, response);
    }
  }

  private boolean skipLogin(JsonNode responseBody) {
    return responseBody.has(SKIP) && responseBody.get(SKIP).booleanValue();
  }

  private String redirectToConsentPage(
      String loginChallenge,
      String userId,
      HttpServletRequest request,
      HttpServletResponse response) {
    ResponseEntity<JsonNode> result = oauthService.loginAccept(userId, loginChallenge);
    if (result.getStatusCode().is2xxSuccessful()) {
      String redirectUrl = JsonUtils.getTextValue(result.getBody(), REDIRECT_TO);
      return redirect(response, redirectUrl);
    }
    return ERROR_VIEW_NAME;
  }

  private String redirectToLoginOrAutoLoginPage(
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
    String mobilePlatform = qsParams.getFirst(DEVICE_PLATFORM);
    model.addAttribute(LOGIN_CHALLENGE, loginChallenge);
    model.addAttribute(FORGOT_PASSWORD_LINK, redirectConfig.getForgotPasswordUrl(mobilePlatform));
    model.addAttribute(SIGNUP_LINK, redirectConfig.getSignupUrl(mobilePlatform));
    model.addAttribute(TERMS_LINK, redirectConfig.getTermsUrl(mobilePlatform));
    model.addAttribute(PRIVACY_POLICY_LINK, redirectConfig.getPrivacyPolicyUrl(mobilePlatform));
    model.addAttribute(ABOUT_LINK, redirectConfig.getAboutUrl(mobilePlatform));

    // tempRegId for auto login after signup
    if (StringUtils.isNotEmpty(tempRegId)) {
      Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
      if (optUser.isPresent()) {
        UserEntity user = optUser.get();
        logger.exit("tempRegId is valid, return to auto login page");
        cookieHelper.addCookie(response, USER_ID, user.getUserId());
        return AUTO_LOGIN_VIEW_NAME;
      }

      logger.exit("tempRegId is invalid, return to login page");
      cookieHelper.deleteCookie(response, TEMP_REG_ID);
      return LOGIN_VIEW_NAME;
    }
    return LOGIN_VIEW_NAME;
  }

  private String redirectToCallbackUrl(
      HttpServletRequest request, String code, HttpServletResponse response) {
    String userId = WebUtils.getCookie(request, USER_ID).getValue();
    String mobilePlatform = WebUtils.getCookie(request, DEVICE_PLATFORM).getValue();
    String callbackUrl = redirectConfig.getCallbackUrl(mobilePlatform);

    String redirectUrl = String.format("%s?code=%s&userId=%s", callbackUrl, code, userId);

    logger.exit(String.format("redirect to %s from /login", callbackUrl));
    return redirect(response, redirectUrl);
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  public void addCookies(
      HttpServletResponse response, MultiValueMap<String, String> params, String... cookieNames) {
    for (String cookieName : cookieNames) {
      addCookie(response, cookieName, params.getFirst(cookieName));
    }
  }

  public void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
    Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(600);
    cookie.setSecure(appConfig.isSecureCookie());
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
    logger.error(String.format("Request %s failed with an exception", req.getRequestURL()), ex);
    ModelAndView modelView = new ModelAndView();
    modelView.setViewName(ERROR_VIEW_NAME);
    return modelView;
  }
}
