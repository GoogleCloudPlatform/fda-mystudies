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
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_STATUS_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.APP_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTO_LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CLIENT_APP_VERSION_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CORRELATION_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_DESCRIPTION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.FORGOT_PASSWORD_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_CHALLENGE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.PRIVACY_POLICY_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_TO;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SIGNUP_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SOURCE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SOURCE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_REG_ID;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TEMP_REG_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.TERMS_LINK;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ValidationErrorResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.JsonUtils;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.beans.LoginRequest;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.CookieHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
public class LoginController {

  private static final String MOBILE_APP = "mobileApp";

  private XLogger logger = XLoggerFactory.getXLogger(LoginController.class.getName());

  @Autowired private OAuthService oauthService;

  @Autowired private UserService userService;

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private CookieHelper cookieHelper;

  @Autowired private AuthScimAuditHelper auditHelper;

  @GetMapping(value = "/login")
  public String login(
      @RequestParam(name = LOGIN_CHALLENGE) String loginChallenge,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model)
      throws UnsupportedEncodingException {
    logger.entry(String.format("%s request", request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    model.addAttribute("loginRequest", new LoginRequest());

    // login/consent flow initiated
    if (StringUtils.isEmpty(loginChallenge)) {
      auditHelper.logEvent(SIGNIN_FAILED, auditRequest);
      return ERROR_VIEW_NAME;
    }

    // show or skip login page
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add(LOGIN_CHALLENGE, loginChallenge);
    ResponseEntity<JsonNode> loginResponse = oauthService.requestLogin(paramMap);
    if (loginResponse.getStatusCode().is2xxSuccessful()) {
      JsonNode responseBody = loginResponse.getBody();
      return redirectToLoginOrAutoLoginPage(response, responseBody, model, loginChallenge);
    }

    auditHelper.logEvent(SIGNIN_FAILED, auditRequest);
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
   * @throws UnsupportedEncodingException
   */
  @PostMapping(value = "/login")
  public String authenticate(
      @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
      BindingResult bindingResult,
      Model model,
      HttpServletRequest request,
      HttpServletResponse response)
      throws JsonProcessingException, UnsupportedEncodingException {
    logger.entry(String.format("%s request", request.getRequestURI()));
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    String tempRegId = cookieHelper.getCookieValue(request, TEMP_REG_ID_COOKIE);
    String appId = cookieHelper.getCookieValue(request, APP_ID_COOKIE);
    String loginChallenge = cookieHelper.getCookieValue(request, LOGIN_CHALLENGE_COOKIE);
    String mobilePlatform = cookieHelper.getCookieValue(request, MOBILE_PLATFORM_COOKIE);
    String source = cookieHelper.getCookieValue(request, SOURCE_COOKIE);

    boolean attrsAdded = addAttributesToModel(model, mobilePlatform, source);
    if (!attrsAdded) {
      return ERROR_VIEW_NAME;
    }

    if (StringUtils.isNotEmpty(tempRegId)) {
      return redirectToLoginOrConsentPage(tempRegId, loginChallenge, response);
    }

    // validate login credentials
    ValidationErrorResponse errors = validateRequiredParams(request, "email", "password");
    if (bindingResult.hasErrors() || errors.hasErrors()) {
      // don't log the binding errors, rejected value contains PII information such as email,
      // password
      logger.error(
          String.format(
              "hasBindingErrors=%b and missing required params=%b, error code=%s",
              bindingResult.hasErrors(), errors.hasErrors(), ErrorCode.INVALID_LOGIN_CREDENTIALS));
      model.addAttribute(ERROR_DESCRIPTION, ErrorCode.INVALID_LOGIN_CREDENTIALS.getDescription());
      return LOGIN_VIEW_NAME;
    }

    UserRequest user = new UserRequest();
    user.setEmail(loginRequest.getEmail());
    user.setPassword(loginRequest.getPassword());
    user.setAppId(appId);

    AuthenticationResponse authenticationResponse = userService.authenticate(user, auditRequest);

    if (UserAccountStatus.PENDING_CONFIRMATION.getStatus()
        == authenticationResponse.getAccountStatus()) {
      String redirectUrl = redirectConfig.getAccountActivationUrl(mobilePlatform, source);
      String url = String.format("%s?email=%s", redirectUrl, loginRequest.getEmail());
      return redirect(response, url);
    }

    if (authenticationResponse.is2xxSuccessful()) {
      logger.exit("authentication success, redirect to consent page");
      cookieHelper.addCookie(response, USER_ID_COOKIE, authenticationResponse.getUserId());
      cookieHelper.addCookie(
          response,
          ACCOUNT_STATUS_COOKIE,
          String.valueOf(authenticationResponse.getAccountStatus()));
    } else {
      return LOGIN_VIEW_NAME;
    }
    return redirectToConsentPage(loginChallenge, authenticationResponse.getUserId(), response);
  }

  private String redirectToLoginOrConsentPage(
      String tempRegId, String loginChallenge, HttpServletResponse response)
      throws UnsupportedEncodingException {
    Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
    if (!optUser.isPresent()) {
      logger.exit("tempRegId is invalid, return to login page");
      cookieHelper.deleteCookie(response, TEMP_REG_ID_COOKIE);
      return LOGIN_VIEW_NAME;
    } else {
      UserEntity user = optUser.get();
      logger.exit("tempRegId is valid, return to consent page");
      cookieHelper.addCookie(response, USER_ID_COOKIE, user.getUserId());
      cookieHelper.addCookie(response, ACCOUNT_STATUS_COOKIE, String.valueOf(user.getStatus()));
      userService.resetTempRegId(user.getUserId());
      return redirectToConsentPage(loginChallenge, user.getUserId(), response);
    }
  }

  private String redirectToConsentPage(
      String loginChallenge, String userId, HttpServletResponse response) {
    ResponseEntity<JsonNode> result = oauthService.loginAccept(userId, loginChallenge);
    if (result.getStatusCode().is2xxSuccessful()) {
      String redirectUrl = JsonUtils.getTextValue(result.getBody(), REDIRECT_TO);
      return redirect(response, redirectUrl);
    }
    return ERROR_VIEW_NAME;
  }

  private String redirectToLoginOrAutoLoginPage(
      HttpServletResponse response, JsonNode responseBody, Model model, String loginChallenge)
      throws UnsupportedEncodingException {

    String requestUrl = URLDecoder.decode(responseBody.get("request_url").textValue(), "UTF-8");
    MultiValueMap<String, String> qsParams =
        UriComponentsBuilder.fromUriString(requestUrl).build().getQueryParams();

    cookieHelper.addCookie(response, LOGIN_CHALLENGE_COOKIE, loginChallenge);
    cookieHelper.addCookies(
        response,
        qsParams,
        APP_ID_COOKIE,
        CORRELATION_ID_COOKIE,
        CLIENT_APP_VERSION_COOKIE,
        MOBILE_PLATFORM_COOKIE,
        SOURCE_COOKIE);

    String mobilePlatform = qsParams.getFirst(MOBILE_PLATFORM);
    String source = qsParams.getFirst(SOURCE);
    model.addAttribute(LOGIN_CHALLENGE, loginChallenge);
    boolean attrsAdded = addAttributesToModel(model, mobilePlatform, source);
    if (!attrsAdded) {
      return ERROR_VIEW_NAME;
    }

    // tempRegId for auto login after signup
    String tempRegId = qsParams.getFirst(TEMP_REG_ID);
    if (StringUtils.isNotEmpty(tempRegId)) {
      Optional<UserEntity> optUser = userService.findUserByTempRegId(tempRegId);
      if (optUser.isPresent()) {
        UserEntity user = optUser.get();
        logger.exit("tempRegId is valid, return to auto login page");
        cookieHelper.addCookie(response, USER_ID_COOKIE, user.getUserId());
        cookieHelper.addCookie(response, TEMP_REG_ID_COOKIE, tempRegId);
        cookieHelper.addCookie(response, ACCOUNT_STATUS_COOKIE, String.valueOf(user.getStatus()));
        return AUTO_LOGIN_VIEW_NAME;
      }
      logger.exit("tempRegId is invalid, return to login page");
      return LOGIN_VIEW_NAME;
    }
    return LOGIN_VIEW_NAME;
  }

  private boolean addAttributesToModel(Model model, String mobilePlatform, String source) {
    PlatformComponent platformComponent = PlatformComponent.fromValue(source);
    if (platformComponent == null) {
      logger.warn(
          String.format(
              "'%s' is invalid source value. Allowed values: MOBILE APPS or PARTICIPANT MANAGER",
              source));
      return false;
    }

    model.addAttribute(
        FORGOT_PASSWORD_LINK, redirectConfig.getForgotPasswordUrl(mobilePlatform, source));
    model.addAttribute(SIGNUP_LINK, redirectConfig.getSignupUrl(mobilePlatform));
    model.addAttribute(TERMS_LINK, redirectConfig.getTermsUrl(mobilePlatform, source));
    model.addAttribute(PRIVACY_POLICY_LINK, redirectConfig.getPrivacyPolicyUrl(mobilePlatform));
    model.addAttribute(ABOUT_LINK, redirectConfig.getAboutUrl(source));
    model.addAttribute(MOBILE_APP, platformComponent.equals(PlatformComponent.MOBILE_APPS));
    return true;
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
    logger.error(String.format("Request %s failed with an exception", req.getRequestURL()), ex);
    return displayErrorPage(req);
  }

  @ExceptionHandler(ErrorCodeException.class)
  public ModelAndView handleErrorCodeException(HttpServletRequest req, ErrorCodeException ex) {
    try {
      logger.error(
          String.format(
              "Request %s failed with an ErrorCode=%s", req.getRequestURL(), ex.getErrorCode()));
      String mobilePlatform = cookieHelper.getCookieValue(req, MOBILE_PLATFORM_COOKIE);
      mobilePlatform =
          StringUtils.defaultIfEmpty(mobilePlatform, MobilePlatform.UNKNOWN.getValue());
      String source = cookieHelper.getCookieValue(req, SOURCE_COOKIE);

      ModelAndView modelView = new ModelAndView();
      modelView.setViewName(LOGIN_VIEW_NAME);
      modelView.addObject("loginRequest", new LoginRequest());
      modelView.addObject(ERROR_DESCRIPTION, ex.getErrorCode().getDescription());
      modelView.addObject(
          FORGOT_PASSWORD_LINK, redirectConfig.getForgotPasswordUrl(mobilePlatform, source));
      modelView.addObject(SIGNUP_LINK, redirectConfig.getSignupUrl(mobilePlatform));
      modelView.addObject(TERMS_LINK, redirectConfig.getTermsUrl(mobilePlatform, source));
      modelView.addObject(PRIVACY_POLICY_LINK, redirectConfig.getPrivacyPolicyUrl(mobilePlatform));
      modelView.addObject(ABOUT_LINK, redirectConfig.getAboutUrl(mobilePlatform));

      PlatformComponent platformComponent = PlatformComponent.fromValue(source);
      modelView.addObject(MOBILE_APP, platformComponent.equals(PlatformComponent.MOBILE_APPS));

      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(req);
      auditHelper.logEvent(SIGNIN_FAILED, auditRequest);

      return modelView;
    } catch (Exception e) {
      logger.error("Exception in handleErrorCodeException()", e);
      return displayErrorPage(req);
    }
  }

  private ModelAndView displayErrorPage(HttpServletRequest req) {
    ModelAndView modelView = new ModelAndView();
    modelView.setViewName(ERROR_VIEW_NAME);

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(req);
    auditHelper.logEvent(SIGNIN_FAILED, auditRequest);

    return modelView;
  }
}
