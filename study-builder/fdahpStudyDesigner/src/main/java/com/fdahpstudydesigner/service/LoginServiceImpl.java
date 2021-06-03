/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_ACCOUNT_ACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_ACCOUNT_ACTIVATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_SUCCEEDED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_HELP_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_HELP_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_HELP_REQUESTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_RESET_SUCCEEDED;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.common.StudyBuilderConstants;
import com.fdahpstudydesigner.common.UserAccessLevel;
import com.fdahpstudydesigner.dao.LoginDAOImpl;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.EmailNotification;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService, UserDetailsService {

  private static XLogger logger = XLoggerFactory.getXLogger(LoginServiceImpl.class.getName());

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private HttpServletRequest request;

  @Autowired private EmailNotification emailNotification;

  private LoginDAOImpl loginDAO;

  @Override
  public String authAndAddPassword(
      String securityToken, String password, UserBO userBO2, SessionObject sesObj) {
    UserBO userBO = null;
    logger.entry("begin checkSecurityToken()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    boolean isValid = false;
    boolean isIntialPasswordSetUp = false;
    Map<String, String> keyValueForSubject = null;
    String dynamicContent = "";
    String result = FdahpStudyDesignerConstants.FAILURE;
    String oldPasswordError = propMap.get("old.password.error.msg");
    String passwordCount = propMap.get("password.history.count");
    List<UserPasswordHistory> passwordHistories = null;
    Boolean isValidPassword = true;
    Map<String, String> values = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      auditRequest.setUserId(String.valueOf(userBO2.getUserId()));
      userBO = loginDAO.getUserBySecurityToken(securityToken);
      if (null != userBO) {
        if (StringUtils.isBlank(userBO.getUserPassword())) {
          isIntialPasswordSetUp = true;
        }
        if ((password != null)
            && (password.contains(
                    FdahpStudyDesignerUtil.isNotEmpty(userBO2.getFirstName())
                        ? userBO2.getFirstName()
                        : userBO.getFirstName())
                || password.contains(
                    FdahpStudyDesignerUtil.isNotEmpty(userBO2.getLastName())
                        ? userBO2.getLastName()
                        : userBO.getLastName()))) {
          isValidPassword = false;
        }
        if (isValidPassword) {
          passwordHistories = loginDAO.getPasswordHistory(userBO.getUserId());
          if ((passwordHistories != null) && !passwordHistories.isEmpty()) {
            for (UserPasswordHistory userPasswordHistory : passwordHistories) {
              if (FdahpStudyDesignerUtil.compareEncryptedPassword(
                  userPasswordHistory.getUserPassword(), password)) {
                isValidPassword = false;
                break;
              }
            }
          }
          if (isValidPassword) {
            userBO.setFirstName(
                null != userBO2.getFirstName()
                    ? userBO2.getFirstName().trim()
                    : userBO.getFirstName());
            userBO.setLastName(
                null != userBO2.getLastName()
                    ? userBO2.getLastName().trim()
                    : userBO.getLastName());
            userBO.setPhoneNumber(
                null != userBO2.getPhoneNumber()
                    ? userBO2.getPhoneNumber().trim()
                    : userBO.getPhoneNumber());
            userBO.setUserPassword(FdahpStudyDesignerUtil.getEncryptedPassword(password));
            userBO.setTokenUsed(true);
            userBO.setEnabled(true);
            userBO.setAccountNonExpired(true);
            userBO.setAccountNonLocked(true);
            userBO.setCredentialsNonExpired(true);
            userBO.setPasswordExpiryDateTime(
                new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                    .format(new Date()));
            result = loginDAO.updateUser(userBO);
            if (result.equals(FdahpStudyDesignerConstants.SUCCESS)) {
              loginDAO.updatePasswordHistory(userBO.getUserId(), userBO.getUserPassword());
              isValid = true;
              SessionObject sessionObject = new SessionObject();
              sessionObject.setUserId(userBO.getUserId());

              auditRequest.setUserId(String.valueOf(userBO.getUserId()));

              auditLogEventHelper.logEvent(NEW_USER_ACCOUNT_ACTIVATED, auditRequest);
              auditLogEventHelper.logEvent(PASSWORD_RESET_SUCCEEDED, auditRequest);
            } else {
              if (userBO2 != null) {
                values.put(StudyBuilderConstants.USER_ID, String.valueOf(userBO.getUserId()));
                values.put(
                    StudyBuilderConstants.ACCESS_LEVEL,
                    UserAccessLevel.STUDY_BUILDER_ADMIN.getValue());
                auditLogEventHelper.logEvent(
                    NEW_USER_ACCOUNT_ACTIVATION_FAILED, auditRequest, values);
                auditLogEventHelper.logEvent(PASSWORD_RESET_FAILED, auditRequest, values);
              } else {
                auditLogEventHelper.logEvent(PASSWORD_RESET_FAILED, auditRequest, values);
              }
            }
          } else {
            result = oldPasswordError.replace("$countPass", passwordCount);
          }
        } else {
          result = propMap.get("password.name.contains.error.msg");
        }
        if (isIntialPasswordSetUp && isValid) {
          List<String> cc = new ArrayList<>();
          cc.add(propMap.get("email.address.cc"));
          keyValueForSubject = new HashMap<>();
          dynamicContent =
              FdahpStudyDesignerUtil.genarateEmailContent(
                  "newASPInitialPasswordSetupContent", keyValueForSubject);
          emailNotification.sendEmailNotification(
              "newASPInitialPasswordSetupSubject",
              dynamicContent,
              propMap.get("email.address.to"),
              cc,
              null);
        }
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
    }
    logger.exit("checkSecurityToken() - Ends");
    return result;
  }

  @Override
  public String changePassword(
      String userId, String newPassword, String oldPassword, SessionObject sesObj) {
    logger.entry("begin changePassword()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String message = FdahpStudyDesignerConstants.FAILURE;
    String oldPasswordError = propMap.get("old.password.error.msg");
    String passwordMaxCharMatchError = propMap.get("password.max.char.match.error.msg");
    String passwordCount = propMap.get("password.history.count");
    Integer passwordMaxCharMatchCount =
        Integer.parseInt(propMap.get("password.max.char.match.count"));
    List<UserPasswordHistory> passwordHistories = null;
    Boolean isValidPassword = false;
    int countPassChar = 0;
    StudyBuilderAuditEvent eventEnum = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      if ((newPassword != null)
          && (newPassword.contains(sesObj.getFirstName())
              || newPassword.contains(sesObj.getLastName()))) {
        isValidPassword = true;
      }
      if (!isValidPassword) {
        if ((null != newPassword) && StringUtils.isNotBlank(newPassword)) {
          char[] newPassChar = newPassword.toCharArray();
          List<String> countList = new ArrayList<>();
          for (char c : newPassChar) {
            if ((oldPassword != null)
                && (!oldPassword.contains(Character.toString(c)))
                && !countList.contains(Character.toString(c))) {
              countPassChar++;
              countList.add(Character.toString(c));
            }
            if ((passwordMaxCharMatchCount != null)
                && (countPassChar > passwordMaxCharMatchCount)) {
              isValidPassword = true;
              break;
            }
          }
        }
        if (isValidPassword) {
          passwordHistories = loginDAO.getPasswordHistory(userId);
          if ((passwordHistories != null) && !passwordHistories.isEmpty()) {
            for (UserPasswordHistory userPasswordHistory : passwordHistories) {
              if (FdahpStudyDesignerUtil.compareEncryptedPassword(
                  userPasswordHistory.getUserPassword(), newPassword)) {
                isValidPassword = false;
                break;
              }
            }
          }
          if (isValidPassword) {
            message = loginDAO.changePassword(userId, newPassword, oldPassword);
            if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
              loginDAO.updatePasswordHistory(
                  userId, FdahpStudyDesignerUtil.getEncryptedPassword(newPassword));
              eventEnum = PASSWORD_CHANGE_SUCCEEDED;

            } else {
              eventEnum = PASSWORD_CHANGE_FAILED;
            }
            auditLogEventHelper.logEvent(eventEnum, auditRequest);
          } else {
            message = oldPasswordError.replace("$countPass", passwordCount);
          }
        } else {
          message =
              passwordMaxCharMatchError.replace("$countMatch", passwordMaxCharMatchCount + "");
        }
      } else {
        message = propMap.get("password.name.contains.error.msg");
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - changePassword() - ERROR ", e);
    }
    logger.exit("changePassword() - Ends");
    return message;
  }

  @Override
  public UserBO checkSecurityToken(String securityToken) {
    UserBO userBO = null;
    logger.entry("begin checkSecurityToken()");
    Date securityTokenExpiredDate = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    UserBO chkBO = null;
    final Integer MAX_ATTEMPTS = Integer.valueOf(propMap.get("max.login.attempts"));
    final Integer USER_LOCK_DURATION =
        Integer.valueOf(propMap.get("user.lock.duration.in.minutes"));
    try {
      userBO = loginDAO.getUserBySecurityToken(securityToken);
      if ((null != userBO) && !userBO.getTokenUsed()) {
        UserAttemptsBo userAttempts = loginDAO.getUserAttempts(userBO.getUserEmail());
        if ((userAttempts == null)
            || (userAttempts.getAttempts() < MAX_ATTEMPTS)
            || new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                .parse(
                    FdahpStudyDesignerUtil.addMinutes(
                        userAttempts.getLastModified(), USER_LOCK_DURATION))
                .after(
                    new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                        .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))) {
          securityTokenExpiredDate =
              new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                  .parse(userBO.getTokenExpiryDate());
          if (securityTokenExpiredDate.after(
              new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                  .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))) {
            chkBO = userBO;
          }
        }
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
    }
    logger.exit("checkSecurityToken() - Ends");
    return chkBO;
  }

  @Override
  public Boolean isFrocelyLogOutUser(SessionObject sessionObject) {
    logger.entry("begin isFrocelyLogOutUser()");
    Boolean isFrocelyLogOut = false;
    try {
      isFrocelyLogOut = loginDAO.isFrocelyLogOutUser(sessionObject.getUserId());
    } catch (Exception e) {
      logger.error("LoginServiceImpl - isFrocelyLogOutUser() - ERROR ", e);
    }
    logger.exit("isFrocelyLogOutUser() - Ends");
    return isFrocelyLogOut;
  }

  @Override
  public Boolean isUserEnabled(SessionObject sessionObject) {
    logger.entry("begin isUserEnabled()");
    Boolean isUserEnabled = true;
    try {
      if (sessionObject.isSuperAdmin()) {
        isUserEnabled = loginDAO.isUserEnabled(sessionObject.getUserId());
      } else if (!sessionObject.isSuperAdmin() && (sessionObject.getSuperAdminId() != null)) {
        if (!(loginDAO.isUserEnabled(sessionObject.getUserId()))
            || !(loginDAO.isUserEnabled(sessionObject.getSuperAdminId()))) {
          isUserEnabled = false;
        }
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - isUserEnabled() - ERROR ", e);
    }
    logger.exit("isUserEnabled() - Ends");
    return isUserEnabled;
  }

  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    UserBO user = loginDAO.getValidUserByEmail(userEmail);

    List<GrantedAuthority> authorities =
        FdahpStudyDesignerUtil.buildUserAuthority(user.getPermissions());

    return FdahpStudyDesignerUtil.buildUserForAuthentication(user, authorities);
  }

  @Override
  public Boolean logUserLogOut(SessionObject sessionObject) {
    logger.entry("begin isFrocelyLogOutUser()");
    Boolean isLogged = false;
    try {

      isLogged = true;
    } catch (Exception e) {
      logger.error("LoginServiceImpl - isFrocelyLogOutUser() - ERROR ", e);
    }
    logger.exit("isFrocelyLogOutUser() - Ends");
    return isLogged;
  }

  @Override
  public String sendPasswordResetLinkToMail(
      HttpServletRequest request,
      String email,
      String oldEmail,
      String type,
      AuditLogEventRequest auditRequest) {
    logger.entry("begin sendPasswordResetLinkToMail");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String passwordResetToken = null;
    String message = propMap.get("user.forgot.error.msg");
    boolean flag = false;
    UserBO userdetails = null;
    Map<String, String> keyValueForSubject = null;
    Map<String, String> keyValueForSubject2 = null;
    String dynamicContent = "";
    String anotherdynamicContent = "";
    String acceptLinkMail = "";
    int passwordResetLinkExpirationInDay =
        Integer.parseInt(propMap.get("password.resetLink.expiration.in.hour"));
    String customerCareMail = "";
    String contact = "";
    final Integer MAX_ATTEMPTS = Integer.valueOf(propMap.get("max.login.attempts"));
    final Integer USER_LOCK_DURATION =
        Integer.valueOf(propMap.get("user.lock.duration.in.minutes"));
    final String lockMsg = propMap.get("user.lock.msg");
    try {
      passwordResetToken = RandomStringUtils.randomAlphanumeric(10);
      if (!StringUtils.isEmpty(passwordResetToken)) {
        userdetails = loginDAO.getValidUserByEmail(email);
        if ("".equals(type) && userdetails != null && userdetails.isEnabled()) {
          auditRequest.setUserAccessLevel(
              StringUtils.defaultIfBlank(userdetails.getAccessLevel(), ""));
          auditRequest.setUserId(String.valueOf(userdetails.getUserId()));
          auditLogEventHelper.logEvent(PASSWORD_HELP_REQUESTED, auditRequest);
        }
        if ("".equals(type) && userdetails.getEmailChanged()) {
          userdetails = null;
        }
        UserAttemptsBo userAttempts = loginDAO.getUserAttempts(email);
        // Restricting the user to login for specified minutes if the
        // user has max fails attempts
        if ((type != null)
            && "".equals(type)
            && (userAttempts != null)
            && (userAttempts.getAttempts() >= MAX_ATTEMPTS)
            && new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                .parse(
                    FdahpStudyDesignerUtil.addMinutes(
                        userAttempts.getLastModified(), USER_LOCK_DURATION))
                .after(
                    new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                        .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))) {
          message = lockMsg;
          flag = false;
        } else {
          flag = true;
        }

        if (flag) {
          flag = false;
          if (null != userdetails) {
            userdetails.setSecurityToken(passwordResetToken);
            userdetails.setTokenUsed(false);
            userdetails.setTokenExpiryDate(
                FdahpStudyDesignerUtil.addHours(
                    FdahpStudyDesignerUtil.getCurrentDateTime(), passwordResetLinkExpirationInDay));

            if (!"USER_UPDATE".equals(type)) {
              message = loginDAO.updateUser(userdetails);
            } else {
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
            if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
              if ("USER_EMAIL_UPDATE".equalsIgnoreCase(type)) {
                acceptLinkMail = propMap.get("emailChangeLink").trim();
              } else {
                acceptLinkMail = propMap.get("acceptLinkMail").trim();
              }
              keyValueForSubject = new HashMap<String, String>();
              keyValueForSubject2 = new HashMap<String, String>();
              keyValueForSubject.put("$firstName", userdetails.getFirstName());
              keyValueForSubject2.put("$firstName", userdetails.getFirstName());
              keyValueForSubject.put("$lastName", userdetails.getLastName());
              keyValueForSubject.put(
                  "$passwordResetLinkExpirationInDay",
                  String.valueOf(passwordResetLinkExpirationInDay));
              keyValueForSubject2.put(
                  "$passwordResetLinkExpirationInDay",
                  String.valueOf(passwordResetLinkExpirationInDay));
              keyValueForSubject.put("$passwordResetLink", acceptLinkMail + passwordResetToken);

              customerCareMail = propMap.get("email.address.customer.service");
              keyValueForSubject.put("$customerCareMail", customerCareMail);
              keyValueForSubject2.put("$customerCareMail", customerCareMail);
              keyValueForSubject.put("$newUpdatedMail", userdetails.getUserEmail());
              keyValueForSubject2.put("$newUpdatedMail", userdetails.getUserEmail());
              keyValueForSubject.put("$oldMail", oldEmail);
              contact = propMap.get("phone.number.to");
              keyValueForSubject.put("$contact", contact);
              keyValueForSubject.put("$orgName", propMap.get("orgName"));
              keyValueForSubject2.put("$orgName", propMap.get("orgName"));
              if ("USER".equals(type) && !userdetails.isEnabled()) {
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "userRegistrationContent", keyValueForSubject);
                flag =
                    emailNotification.sendEmailNotification(
                        "userRegistrationSubject", dynamicContent, email, null, null);

                Map<String, String> values = new HashMap<>();
                values.put(StudyBuilderConstants.USER_ID, String.valueOf(userdetails.getUserId()));
                if (!flag) {
                  auditLogEventHelper.logEvent(
                      NEW_USER_INVITATION_EMAIL_FAILED, auditRequest, values);
                }

              } else if ("USER_UPDATE".equals(type) && userdetails.isEnabled()) {
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "mailForUserUpdateContent", keyValueForSubject2);
                flag =
                    emailNotification.sendEmailNotification(
                        "mailForUserUpdateSubject", dynamicContent, email, null, null);
              } else if ("USER_EMAIL_UPDATE".equals(type)) {
                // Email to old email address
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "mailToOldEmailForUserEmailUpdateContent", keyValueForSubject2);
                flag =
                    emailNotification.sendEmailNotification(
                        "mailToOldEmailForUserEmailUpdateSubject",
                        dynamicContent,
                        oldEmail,
                        null,
                        null);
                // Email to new email address
                anotherdynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "mailToNewEmailForUserEmailUpdateContent", keyValueForSubject);
                flag =
                    emailNotification.sendEmailNotification(
                        "mailToNewEmailForUserEmailUpdateSubject",
                        anotherdynamicContent,
                        email,
                        null,
                        null);
              } else if ("enforcePasswordChange".equals(type)) {
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "mailForEnforcePasswordChangeContent", keyValueForSubject);
                flag =
                    emailNotification.sendEmailNotification(
                        "mailForEnforcePasswordChangeSubject", dynamicContent, email, null, null);
              } else if ("ReactivateMailAfterEnforcePassChange".equals(type)
                  && userdetails.isEnabled()) {
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "mailForReactivatingUserAfterEnforcePassChangeContent", keyValueForSubject);
                flag =
                    emailNotification.sendEmailNotification(
                        "mailForReactivatingUserAfterEnforcePassChangeSubject",
                        dynamicContent,
                        email,
                        null,
                        null);
              } else if ("".equals(type) && userdetails.isEnabled()) {
                dynamicContent =
                    FdahpStudyDesignerUtil.genarateEmailContent(
                        "passwordResetLinkContent", keyValueForSubject);
                flag =
                    emailNotification.sendEmailNotification(
                        "passwordResetLinkSubject", dynamicContent, email, null, null);
                StudyBuilderAuditEvent auditLogEvent =
                    flag ? PASSWORD_HELP_EMAIL_SENT : PASSWORD_HELP_EMAIL_FAILED;
                auditLogEventHelper.logEvent(auditLogEvent, auditRequest);
              } else if ("USER_UPDATE".equals(type) && !userdetails.isEnabled()) {
                flag = true;
              }

              message =
                  flag ? FdahpStudyDesignerConstants.SUCCESS : FdahpStudyDesignerConstants.FAILURE;

              if ("".equals(type) && (!userdetails.isEnabled())) {
                message = propMap.get("user.inactive.msg");
              }
              if ("".equals(type) && StringUtils.isEmpty(userdetails.getUserPassword())) {
                message = propMap.get("user.not.found.msg");
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - sendPasswordResetLinkToMail - ERROR ", e);
      auditLogEventHelper.logEvent(PASSWORD_HELP_EMAIL_FAILED, auditRequest);
    }
    logger.exit("sendPasswordResetLinkToMail - Ends");
    return message;
  }

  @Autowired
  public void setLoginDAO(LoginDAOImpl loginDAO) {
    this.loginDAO = loginDAO;
  }

  public String validateEmailChangeVerification(String securityToken) {
    UserBO userBO = null;
    logger.entry("begin checkSecurityToken()");
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      userBO = loginDAO.getUserBySecurityToken(securityToken);
      if (null != userBO) {
        userBO.setEmailChanged(false);
        userBO.setTokenUsed(true);
        result = loginDAO.updateUser(userBO);
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - checkSecurityToken() - ERROR ", e);
    }
    logger.exit("checkSecurityToken() - Ends");
    return result;
  }

  @Override
  // Send mail to user when account locked due to invalid login credentials
  public void sendLockedAccountPasswordResetLinkToMail(
      String email, AuditLogEventRequest auditRequest) {
    logger.entry("begin sendLockedAccountPasswordResetLinkToMail");
    try {
      Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
      String acceptLinkMail = propMap.get("acceptLinkMail").trim();
      int passwordResetLinkExpirationInHour =
          Integer.parseInt(propMap.get("accountlocked.resetLink.expiration.in.hour"));
      String passwordResetToken = RandomStringUtils.randomAlphanumeric(10);
      UserBO userdetails = loginDAO.getValidUserByEmail(email);
      if (null != userdetails && !userdetails.getEmailChanged()) {
        userdetails.setSecurityToken(passwordResetToken);
        userdetails.setTokenUsed(false);
        userdetails.setTokenExpiryDate(
            FdahpStudyDesignerUtil.addHours(
                FdahpStudyDesignerUtil.getCurrentDateTime(), passwordResetLinkExpirationInHour));

        String message = loginDAO.updateUserForResetPassword(userdetails);

        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          Map<String, String> keyValueForSubject = new HashMap<String, String>();
          keyValueForSubject.put("$firstName", userdetails.getFirstName());
          keyValueForSubject.put("$passwordResetLink", acceptLinkMail + passwordResetToken);
          String customerCareMail = propMap.get("email.address.customer.service");
          keyValueForSubject.put("$customerCareMail", customerCareMail);
          keyValueForSubject.put("$orgName", propMap.get("orgName"));
          String dynamicContent =
              FdahpStudyDesignerUtil.genarateEmailContent(
                  "accountLockedContent", keyValueForSubject);

          boolean response =
              emailNotification.sendEmailNotification(
                  "accountLockedSubject", dynamicContent, email, null, null);
          StudyBuilderAuditEvent auditEvent =
              response
                  ? PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT
                  : PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT;
          auditLogEventHelper.logEvent(auditEvent, auditRequest);
        }
      }
    } catch (Exception e) {
      logger.error("LoginServiceImpl - sendLockedAccountPasswordResetLinkToMail - ERROR ", e);
      auditLogEventHelper.logEvent(PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT, auditRequest);
    }
    logger.exit("sendLockedAccountPasswordResetLinkToMail - Ends");
  }

  @Override
  public boolean isInactiveUser(String securityToken) {
    logger.entry("begin isActiveUser()");
    UserBO user = loginDAO.getUserBySecurityToken(securityToken);
    boolean isInactiveUser = user != null && !user.isEnabled();
    logger.exit("isActiveUser() - Ends");
    return isInactiveUser;
  }

  @Override
  public boolean isIntialPasswordSetUp(String securityToken) {
    logger.entry("begin isIntialPasswordSetUp()");
    UserBO user = loginDAO.getUserBySecurityToken(securityToken);
    boolean isIntialPasswordSetUp = StringUtils.isBlank(user.getUserPassword());
    logger.exit("isIntialPasswordSetUp() - Ends");
    return isIntialPasswordSetUp;
  }
}
