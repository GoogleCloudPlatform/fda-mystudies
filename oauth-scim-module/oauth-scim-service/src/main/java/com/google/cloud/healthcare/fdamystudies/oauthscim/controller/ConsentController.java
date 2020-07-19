/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.DEVICE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_TO;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SKIP;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import javax.servlet.http.Cookie;
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
import org.springframework.web.util.WebUtils;

@Controller
public class ConsentController {

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  @Autowired private OAuthService oauthService;

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private AppPropertyConfig appConfig;

  @GetMapping(value = "/consent")
  public String authorize(
      @RequestParam(required = false, name = CONSENT_CHALLENGE) String consentChallenge,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model) {
    logger.entry(String.format("%s request", request.getRequestURI()));

    if (StringUtils.isNotBlank(consentChallenge)) {
      addCookie(response, CONSENT_CHALLENGE, consentChallenge);
      // show or skip consent page
      MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
      paramMap.add(CONSENT_CHALLENGE, consentChallenge);
      ResponseEntity<JsonNode> consentResponse = oauthService.requestConsent(paramMap);
      if (consentResponse.getStatusCode().is2xxSuccessful()) {
        JsonNode responseBody = consentResponse.getBody();
        return skipConsent(responseBody)
            ? redirectToCallbackUrl(request, true, response)
            : "consent";
      }
    }

    return redirectToError(request, response);
  }

  private boolean skipConsent(JsonNode responseBody) {
    return responseBody.has(SKIP) && responseBody.get(SKIP).booleanValue();
  }

  private String redirectToCallbackUrl(
      HttpServletRequest request, boolean skipConsent, HttpServletResponse response) {
    String userId = WebUtils.getCookie(request, USER_ID).getValue();
    String devicePlatform = WebUtils.getCookie(request, DEVICE_PLATFORM).getValue();
    String callbackUrl = redirectConfig.getCallbackUrl(devicePlatform);

    String redirectUrl =
        String.format("%s?skip_consent=%b&userId=%s", callbackUrl, skipConsent, userId);

    logger.exit(String.format("redirect to %s from /consent", callbackUrl));
    return redirect(response, redirectUrl);
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  @PostMapping(value = "/consent")
  public String authenticate(
      @CookieValue(name = CONSENT_CHALLENGE) String consentChallenge,
      @CookieValue(name = USER_ID) String userId,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model) {
    logger.entry(String.format("%s request", request.getRequestURI()));

    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add(CONSENT_CHALLENGE, consentChallenge);
    ResponseEntity<JsonNode> consentResponse = oauthService.consentAccept(paramMap);

    if (consentResponse.getStatusCode().is2xxSuccessful()) {
      String redirectUrl = getTextValue(consentResponse.getBody(), REDIRECT_TO);
      return redirect(response, redirectUrl);
    }

    return redirectToError(request, response);
  }

  private String redirectToError(HttpServletRequest request, HttpServletResponse response) {
    String devicePlatform = WebUtils.getCookie(request, DEVICE_PLATFORM).getValue();
    String redirectUrl = redirectConfig.getErrorUrl(devicePlatform);
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  public void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
    Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(600);
    cookie.setSecure(appConfig.isSecureCookie());
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
