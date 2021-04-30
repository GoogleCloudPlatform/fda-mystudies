/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.util;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.service.UsersService;
import java.text.SimpleDateFormat;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class FdahpStudyDesignerPreHandlerInterceptor extends HandlerInterceptorAdapter {

  private static XLogger logger =
      XLoggerFactory.getXLogger(FdahpStudyDesignerPreHandlerInterceptor.class.getName());

  @Autowired private UsersService usersService;

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    logger.entry("begin preHandle()");
    SessionObject session = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String defaultURL = propMap.get("action.default.redirect.url");
    final String excludeActions = propMap.get("interceptor.urls");
    String uri = request.getRequestURI();
    boolean flag = false;
    String passwordExpiredDateTime = null;
    int passwordExpirationInDay = Integer.parseInt(propMap.get("password.expiration.in.day"));
    String forceChangePasswordurl = propMap.get("action.force.changepassword.url");
    String updatePassword = propMap.get("action.force.updatepassword.url");
    String sessionOutUrl = propMap.get("action.logout.url");
    propMap.get("user.inactive.msg");
    String actionLoginbackUrl = propMap.get("action.loginback.url");
    String timeoutMsg = propMap.get("user.session.timeout");
    try {
      if (null != request.getSession()) {
        session =
            (SessionObject)
                request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      }
      // Allow some of the URL
      String list[] = excludeActions.split(",");
      for (String element : list) {
        if (uri.endsWith(element.trim())) {
          flag = true;
        }
      }

      int customSessionExpiredErrorCode = 901;
      boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
      if ((null == session)
          && (request.getParameter("error") != null)
          && request.getParameter("error").equals("timeout")
          && ajax) {
        response.sendError(customSessionExpiredErrorCode);
        logger.info("FdahpStudyDesignerPreHandlerInterceptor - Ajax preHandle(): " + uri + "");
        return false;
      }
      if (!flag) {
        if (null == session) {
          if (uri.contains(actionLoginbackUrl)) {
            request
                .getSession(false)
                .setAttribute(
                    "loginBackUrl",
                    request.getScheme()
                        + "://"
                        + // "http" + "://
                        request.getServerName()
                        + // "myhost"
                        ":"
                        + // ":"
                        request.getServerPort()
                        + // "8080"
                        request.getRequestURI()
                        + // "/people"
                        "?"
                        + // "?"
                        request.getQueryString());
          }
          response.sendRedirect(defaultURL);
          logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): " + uri);
          return false;
        } else if (!ajax && !uri.contains(sessionOutUrl)) {
          // Checking for password Expired Date Time from current
          // Session
          passwordExpiredDateTime = session.getPasswordExpiryDateTime();
          if (StringUtils.isNotBlank(passwordExpiredDateTime)
              && FdahpStudyDesignerUtil.addDaysToDate(
                      new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                          .parse(passwordExpiredDateTime),
                      passwordExpirationInDay)
                  .before(
                      new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                          .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))
              && !uri.contains(forceChangePasswordurl)
              && !uri.contains(updatePassword)) {
            response.sendRedirect(forceChangePasswordurl);
            logger.info(
                "FdahpStudyDesignerPreHandlerInterceptor -preHandle(): force change password");
          }
          // Checking for force logout for current user
          UserBO user = usersService.getUserDetails(session.getUserId());
          if (null != user) {
            if (user.isForceLogout()) {
              response.sendRedirect(sessionOutUrl + "?msg=" + timeoutMsg);
              logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): force logout");
              return false;
            } else if (user.getEmailChanged()) {
              response.sendRedirect(
                  sessionOutUrl + "?msg=" + propMap.get("email.not.varified.error"));
              logger.info("FdahpStudyDesignerPreHandlerInterceptor -preHandle(): email change");
              return false;
            }
          }
        }
      } else if (uri.contains(defaultURL) && (null != session)) {
        response.sendRedirect(session.getCurrentHomeUrl());
      }
    } catch (Exception e) {
      logger.error("FdahpStudyDesignerPreHandlerInterceptor - preHandle()", e);
    }
    logger.exit(
        "FdahpStudyDesignerPreHandlerInterceptor - End Point: preHandle() - "
            + " : "
            + FdahpStudyDesignerUtil.getCurrentDateTime()
            + " uri"
            + uri);
    return true;
  }
}
