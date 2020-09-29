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
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_TO;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.CookieHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.OAuthService;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ConsentController {

  private XLogger logger = XLoggerFactory.getXLogger(UserController.class.getName());

  @Autowired private OAuthService oauthService;

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private CookieHelper cookieHelper;

  @Autowired private AuthScimAuditHelper auditHelper;

  @GetMapping(value = "/consent")
  public String authorize(
      @RequestParam(name = CONSENT_CHALLENGE) String consentChallenge,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model)
      throws UnsupportedEncodingException {
    logger.entry(String.format("%s request", request.getRequestURI()));

    cookieHelper.addCookie(response, CONSENT_CHALLENGE_COOKIE, consentChallenge);
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add(CONSENT_CHALLENGE, consentChallenge);
    ResponseEntity<JsonNode> consentResponse = oauthService.requestConsent(paramMap);
    if (consentResponse.getStatusCode().is2xxSuccessful()) {
      return "consent";
    }

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditHelper.logEvent(SIGNIN_FAILED, auditRequest);

    return ERROR_VIEW_NAME;
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
  }

  @PostMapping(value = "/consent")
  public String authenticate(HttpServletRequest request, HttpServletResponse response, Model model)
      throws UnsupportedEncodingException {
    logger.entry(String.format("%s request", request.getRequestURI()));

    String consentChallenge = cookieHelper.getCookieValue(request, CONSENT_CHALLENGE_COOKIE);

    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add(CONSENT_CHALLENGE, consentChallenge);
    ResponseEntity<JsonNode> consentResponse = oauthService.consentAccept(paramMap);

    if (consentResponse.getStatusCode().is2xxSuccessful()) {
      String redirectUrl = getTextValue(consentResponse.getBody(), REDIRECT_TO);
      return redirect(response, redirectUrl);
    }

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    auditHelper.logEvent(SIGNIN_FAILED, auditRequest);

    return ERROR_VIEW_NAME;
  }

  @ExceptionHandler(Exception.class)
  public ModelAndView handleError(HttpServletRequest req, Exception ex) {
    logger.error(String.format("Request %s failed with an exception", req.getRequestURL()), ex);
    ModelAndView modelView = new ModelAndView();
    modelView.setViewName(ERROR_VIEW_NAME);

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(req);
    auditHelper.logEvent(SIGNIN_FAILED, auditRequest);

    return modelView;
  }
}
