/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_STATUS_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.SOURCE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_SUCCEEDED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_WITH_TEMPORARY_PASSWORD_SUCCEEDED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimAuditHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.CookieHelper;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CallbackController {

  private XLogger logger = XLoggerFactory.getXLogger(CallbackController.class.getName());

  @Autowired private RedirectConfig redirectConfig;

  @Autowired private AuthScimAuditHelper auditHelper;

  @Autowired private UserService userService;

  @Autowired private CookieHelper cookieHelper;

  @GetMapping(value = "/callback")
  public String login(
      @RequestParam String code,
      HttpServletRequest request,
      HttpServletResponse response,
      Model model)
      throws UnsupportedEncodingException {

    try {


    logger.info(String.format("%s request(GET /callback) ======= start =========", request.getRequestURI()));
    logger.info("referer --> [ " + request.getHeader("REFERER") + " ]");
    //logger.info(String.format("%s request", request.getRequestURI()));

    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

    logger.info("code is Empty?? --> [ " + StringUtils.isEmpty(code) + " ]");
    if (StringUtils.isEmpty(code)) {
      logger.error("authorization code is empty, return error view");
      auditHelper.logEvent(SIGNIN_FAILED, auditRequest);
      return ERROR_VIEW_NAME;
    }

    String userId = cookieHelper.getCookieValue(request, USER_ID_COOKIE);
    logger.info("userId from cookie --> [ " + userId + " ]");
    if (StringUtils.isEmpty(userId)) {
      logger.error("userId cookie value is empty, return error view");
      auditHelper.logEvent(SIGNIN_FAILED, auditRequest);
      return ERROR_VIEW_NAME;
    }

    String mobilePlatform = cookieHelper.getCookieValue(request, MOBILE_PLATFORM_COOKIE);
    String accountStatus = cookieHelper.getCookieValue(request, ACCOUNT_STATUS_COOKIE);
    String source = cookieHelper.getCookieValue(request, SOURCE_COOKIE);
    String callbackUrl = redirectConfig.getCallbackUrl(mobilePlatform, source);

    logger.info("mobilePlatform --> [ " + mobilePlatform + " ]");
    logger.info("source         --> [ " + source + " ]");
    logger.info("callbackUrl    --> [ " + callbackUrl + " ]");
    logger.info("accountStatus  --> [ " + accountStatus + " ]");

    logger.info("Status list ---> [ 0:ACTIVE / 1:PENDING CONF / 2:LOCKED / 3:PWD RESET / 4:DEACTIVATED ");
    String redirectUrl = null;
    if (StringUtils.equals(
        accountStatus, String.valueOf(UserAccountStatus.PASSWORD_RESET.getStatus()))) {
      logger.info("パスワードリセットの場合");
      Optional<UserEntity> optUserEntity = userService.findByUserId(userId);
      if (optUserEntity.isPresent()) {
        UserEntity user = optUserEntity.get();
        redirectUrl =
            String.format(
                "%s?code=%s&userId=%s&accountStatus=%s&email=%s",
                callbackUrl, code, userId, accountStatus, user.getEmail());
      }
    } else {
      logger.info("パスワードリセット以外の場合");
      redirectUrl =
          String.format(
              "%s?code=%s&userId=%s&accountStatus=%s", callbackUrl, code, userId, accountStatus);
    }
    logger.info("---------------");
    logger.info("redirectUrl --> \n" + redirectUrl);
    logger.info("---------------");

    if (UserAccountStatus.ACTIVE.getStatus() == Integer.parseInt(accountStatus)) {
      auditHelper.logEvent(SIGNIN_SUCCEEDED, auditRequest);
      logger.info("SIGNIN SUCCEEDED!!!!!");
    } else {
      auditHelper.logEvent(SIGNIN_WITH_TEMPORARY_PASSWORD_SUCCEEDED, auditRequest);
    }

    logger.info(String.format("redirect to %s from /login", callbackUrl));
    return redirect(response, redirectUrl);

    } finally {
      logger.info(String.format("%s request(GET /callback) ======= end =========", request.getRequestURI()));
    }
  }

  private String redirect(HttpServletResponse response, String redirectUrl) {
    response.setHeader("Location", redirectUrl);
    response.setStatus(HttpStatus.FOUND.value());
    return "redirect:" + redirectUrl;
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
