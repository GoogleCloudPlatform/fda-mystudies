/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.util;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.SIGNIN_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.SIGNIN_FAILED_UNREGISTERED_USER;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.SIGNIN_SUCCEEDED;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.dao.LoginDAOImpl;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

  private static XLogger logger =
      XLoggerFactory.getXLogger(LimitLoginAuthenticationProvider.class.getName());

  private LoginDAOImpl loginDAO;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();

  @Override
  public Authentication authenticate(Authentication authentication) {
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    final Integer MAX_ATTEMPTS = Integer.valueOf(propMap.get("max.login.attempts"));
    final Integer USER_LOCK_DURATION =
        Integer.valueOf(propMap.get("user.lock.duration.in.minutes"));
    final String lockMsg = propMap.get("user.lock.msg");
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    AuditLogEventRequest auditRequest =
        AuditEventMapper.fromHttpServletRequest(attributes.getRequest());
    String username = (String) authentication.getPrincipal();

    try {
      UserBO userBO =
          StringUtils.isNotEmpty(username)
              ? loginDAO.getValidUserByEmail(username.toLowerCase())
              : null;
      if (userBO == null) {
        auditRequest.setSource(SIGNIN_FAILED_UNREGISTERED_USER.getSource().getValue());
        auditRequest.setDestination(SIGNIN_FAILED_UNREGISTERED_USER.getDestination().getValue());
        auditLogEventHelper.logEvent(SIGNIN_FAILED_UNREGISTERED_USER, auditRequest);
      }

      UserAttemptsBo userAttempts =
          loginDAO.getUserAttempts(authentication.getName().toLowerCase());

      // Restricting the user to login for specified minutes if the user
      // has max fails attempts
      try {
        if ((userAttempts != null)
            && (userAttempts.getAttempts() >= MAX_ATTEMPTS)
            && new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                .parse(
                    FdahpStudyDesignerUtil.addMinutes(
                        userAttempts.getLastModified(), USER_LOCK_DURATION))
                .after(
                    new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                        .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))) {

          auditRequest.setSource(SIGNIN_FAILED.getSource().getValue());
          auditRequest.setDestination(SIGNIN_FAILED.getDestination().getValue());
          auditLogEventHelper.logEvent(SIGNIN_FAILED, auditRequest);
          throw new LockedException(lockMsg);
        }
      } catch (ParseException e) {
        logger.error("LimitLoginAuthenticationProvider - authenticate - ERROR", e);
      }

      UsernamePasswordAuthenticationToken token =
          new UsernamePasswordAuthenticationToken(
              authentication.getPrincipal(),
              authentication.getCredentials(),
              new ArrayList<GrantedAuthority>());

      // if reach here, means login success, else an exception will be thrown
      Authentication auth = super.authenticate(token);
      // reset the user_attempts
      loginDAO.resetFailAttempts(authentication.getName().toLowerCase());

      if (userBO != null) {
        userBO.setUserLastLoginDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
        loginDAO.updateUser(userBO);
        auditRequest.setUserId(userBO.getUserId().toString());
        auditRequest.setUserAccessLevel(userBO.getAccessLevel());
      }
      auditRequest.setSource(SIGNIN_SUCCEEDED.getSource().getValue());
      auditRequest.setDestination(SIGNIN_SUCCEEDED.getDestination().getValue());
      auditLogEventHelper.logEvent(SIGNIN_SUCCEEDED, auditRequest);
      return auth;

    } catch (BadCredentialsException e) {
      // invalid login, update to user_attempts
      loginDAO.updateFailAttempts(authentication.getName().toLowerCase(), auditRequest);
      auditRequest.setSource(SIGNIN_FAILED.getSource().getValue());
      auditRequest.setDestination(SIGNIN_FAILED.getDestination().getValue());
      auditLogEventHelper.logEvent(SIGNIN_FAILED, auditRequest);
      throw e;

    } catch (LockedException e) {
      logger.error(
          "LimitLoginAuthenticationProvider - authenticate - ERROR - this user is locked! ", e);
      String error;
      UserAttemptsBo userAttempts =
          loginDAO.getUserAttempts(authentication.getName().toLowerCase());

      if (userAttempts != null) {
        error = lockMsg;
      } else {
        error = e.getMessage();
      }

      throw new LockedException(error);
    }
  }

  @Autowired
  public void setLoginDAO(LoginDAOImpl loginDAO) {
    this.loginDAO = loginDAO;
  }
}
