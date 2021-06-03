/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_RE_ACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_DEACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.EDITED_USER_ID;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.UserIdAccessLevelInfo;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.common.StudyBuilderConstants;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.EmailNotification;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

  private static XLogger logger = XLoggerFactory.getXLogger(UsersServiceImpl.class.getName());

  @Autowired private StudyBuilderAuditEventHelper auditLogHelper;

  @Autowired LoginService loginService;

  @Autowired private UsersDAO usersDAO;

  @Autowired private EmailNotification emailNotification;

  @Override
  public String activateOrDeactivateUser(
      String userId,
      int userStatus,
      String loginUser,
      SessionObject userSession,
      HttpServletRequest request) {
    logger.entry("begin activateOrDeactivateUser()");
    String msg = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> keyValueForSubject = null;
    String dynamicContent = "";
    UserBO userBo = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customerCareMail = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      msg = usersDAO.activateOrDeactivateUser(userId, userStatus, loginUser, userSession);
      userBo = usersDAO.getUserDetails(userId);
      if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        StudyBuilderAuditEvent auditLogEvent =
            userStatus == 1 ? USER_RECORD_DEACTIVATED : USER_ACCOUNT_RE_ACTIVATED;
        Map<String, String> values = new HashMap<>();
        values.put(EDITED_USER_ID, String.valueOf(userId));
        auditLogHelper.logEvent(auditLogEvent, auditRequest, values);
        keyValueForSubject = new HashMap<String, String>();
        if ((userBo != null) && Integer.valueOf(userStatus).equals(0)) {
          if (!userBo.isCredentialsNonExpired()) {
            loginService.sendPasswordResetLinkToMail(
                request,
                userBo.getUserEmail(),
                "",
                "ReactivateMailAfterEnforcePassChange",
                auditRequest);
          } else {
            customerCareMail = propMap.get("email.address.customer.service");
            keyValueForSubject.put("$userFirstName", userBo.getFirstName());
            keyValueForSubject.put("$customerCareMail", customerCareMail);
            keyValueForSubject.put("$orgName", propMap.get("orgName"));
            dynamicContent =
                FdahpStudyDesignerUtil.genarateEmailContent(
                    "mailForReactivatingUserContent", keyValueForSubject);
            emailNotification.sendEmailNotification(
                "mailForReactivatingUserSubject",
                dynamicContent,
                userBo.getUserEmail(),
                null,
                null);
          }
        }
      }
    } catch (Exception e) {
      logger.error("UsersServiceImpl - activateOrDeactivateUser() - ERROR", e);
    }
    logger.exit("activateOrDeactivateUser() - Ends");
    return msg;
  }

  @Override
  public String addOrUpdateUserDetails(
      HttpServletRequest request,
      UserBO userBO,
      String permissions,
      String selectedStudies,
      String permissionValues,
      SessionObject userSession,
      AuditLogEventRequest auditRequest) {
    logger.entry("begin addOrUpdateUserDetails()");
    UserBO userBO2 = null;
    String msg = FdahpStudyDesignerConstants.FAILURE;
    boolean addFlag = false;
    List<StudyBuilderAuditEvent> auditLogEvents = new LinkedList<>();
    Map<String, String> values = new HashMap<>();
    boolean emailIdChange = false;
    UserBO userBO3 = null;

    try {
      if (StringUtils.isEmpty(userBO.getUserId())) {
        addFlag = true;
        userBO2 = new UserBO();
        userBO2.setFirstName(null != userBO.getFirstName() ? userBO.getFirstName().trim() : "");
        userBO2.setLastName(null != userBO.getLastName() ? userBO.getLastName().trim() : "");
        userBO2.setUserEmail(
            (null != userBO.getUserEmail() ? userBO.getUserEmail().trim() : "").toLowerCase());
        userBO2.setPhoneNumber(
            null != userBO.getPhoneNumber() ? userBO.getPhoneNumber().trim() : "");
        userBO2.setRoleId(userBO.getRoleId());
        userBO2.setCreatedBy(userBO.getCreatedBy());
        userBO2.setCreatedOn(userBO.getCreatedOn());
        userBO2.setEnabled(false);
        userBO2.setCredentialsNonExpired(true);
        userBO2.setAccountNonExpired(true);
        userBO2.setAccountNonLocked(true);
      } else {
        addFlag = false;
        userBO2 = usersDAO.getUserDetails(userBO.getUserId());
        userBO3 = usersDAO.getUserDetails(userBO.getUserId());
        userBO2.setFirstName(null != userBO.getFirstName() ? userBO.getFirstName().trim() : "");
        userBO2.setLastName(null != userBO.getLastName() ? userBO.getLastName().trim() : "");
        if (!userBO2.getUserEmail().equals(userBO.getUserEmail())) {
          emailIdChange = true;
          userBO2.setEmailChanged(true);
        }
        userBO2.setUserEmail(
            (null != userBO.getUserEmail() ? userBO.getUserEmail().trim() : "").toLowerCase());
        userBO2.setPhoneNumber(
            null != userBO.getPhoneNumber() ? userBO.getPhoneNumber().trim() : "");
        userBO2.setRoleId(userBO.getRoleId());
        userBO2.setModifiedBy(userBO.getModifiedBy());
        userBO2.setModifiedOn(userBO.getModifiedOn());
        userBO2.setEnabled(userBO.isEnabled());
        if (!userSession.getUserId().equals(userBO.getUserId())) {
          userBO2.setForceLogout(true);
        }
      }
      UserIdAccessLevelInfo userIdAccessLevelInfo =
          usersDAO.addOrUpdateUserDetails(userBO2, permissions, selectedStudies, permissionValues);
      if (userIdAccessLevelInfo != null) {
        if (addFlag) {
          values.put(
              StudyBuilderConstants.USER_ID, String.valueOf(userIdAccessLevelInfo.getUserId()));
          values.put(StudyBuilderConstants.ACCESS_LEVEL, userIdAccessLevelInfo.getAccessLevel());
          msg =
              loginService.sendPasswordResetLinkToMail(
                  request, userBO2.getUserEmail(), "", "USER", auditRequest);
          auditLogEvents.add(NEW_USER_CREATED);
          if (FdahpStudyDesignerConstants.SUCCESS.equals(msg)) {
            auditLogEvents.add(NEW_USER_INVITATION_EMAIL_SENT);
          }
        }
        if (!addFlag) {
          values.put(
              StudyBuilderConstants.EDITED_USER_ID,
              String.valueOf(userIdAccessLevelInfo.getUserId()));
          values.put(
              StudyBuilderConstants.EDITED_USER_ACCESS_LEVEL,
              userIdAccessLevelInfo.getAccessLevel());
          auditLogEvents.add(USER_RECORD_UPDATED);

          if (emailIdChange) {
            msg =
                loginService.sendPasswordResetLinkToMail(
                    request,
                    userBO2.getUserEmail(),
                    userBO3.getUserEmail(),
                    "USER_EMAIL_UPDATE",
                    auditRequest);
          } else {
            msg =
                loginService.sendPasswordResetLinkToMail(
                    request, userBO2.getUserEmail(), "", "USER_UPDATE", auditRequest);
          }
        }
        auditLogHelper.logEvent(auditLogEvents, auditRequest, values);

      } else {
        auditLogHelper.logEvent(NEW_USER_CREATION_FAILED, auditRequest);
      }
    } catch (Exception e) {
      logger.error("UsersServiceImpl - addOrUpdateUserDetails() - ERROR", e);
    }
    logger.exit("addOrUpdateUserDetails() - Ends");
    return msg;
  }

  @Override
  public String enforcePasswordChange(String userId, String email) {
    logger.entry("begin enforcePasswordChange()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = usersDAO.enforcePasswordChange(userId, email);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - enforcePasswordChange() - ERROR", e);
    }
    logger.exit("enforcePasswordChange() - Ends");
    return message;
  }

  @Override
  public List<String> getActiveUserEmailIds() {
    logger.entry("begin getActiveUserEmailIds()");
    List<String> emails = null;
    try {
      emails = usersDAO.getActiveUserEmailIds();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getActiveUserEmailIds() - ERROR", e);
    }
    logger.exit("getActiveUserEmailIds() - Ends");
    return emails;
  }

  @Override
  public List<Integer> getPermissionsByUserId(String userId) {
    logger.entry("begin permissionsByUserId()");
    List<Integer> permissions = null;
    try {
      permissions = usersDAO.getPermissionsByUserId(userId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - permissionsByUserId() - ERROR", e);
    }
    logger.exit("permissionsByUserId() - Ends");
    return permissions;
  }

  @Override
  public UserBO getUserDetails(String userId) {
    logger.entry("begin getUserDetails()");
    UserBO userBO = null;
    try {
      userBO = usersDAO.getUserDetails(userId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserDetails() - ERROR", e);
    }
    logger.exit("getUserDetails() - Ends");
    return userBO;
  }

  @Override
  public List<UserBO> getUserList() {
    logger.entry("begin getUserList()");
    List<UserBO> userList = null;
    try {
      userList = usersDAO.getUserList();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserList() - ERROR", e);
    }
    logger.exit("getUserList() - Ends");
    return userList;
  }

  @Override
  public String getUserPermissionByUserId(String sessionUserId) {
    String userId = null;
    logger.entry("begin getUserPermissionByUserId()");
    try {
      userId = usersDAO.getUserPermissionByUserId(sessionUserId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserPermissionByUserId() - ERROR", e);
    }
    logger.exit("getUserPermissionByUserId() - Ends");
    return userId;
  }

  @Override
  public RoleBO getUserRole(String roleId) {
    logger.entry("begin getUserRole()");
    RoleBO roleBO = null;
    try {
      roleBO = usersDAO.getUserRole(roleId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserRole() - ERROR", e);
    }
    logger.exit("getUserRole() - Ends");
    return roleBO;
  }

  @Override
  public List<RoleBO> getUserRoleList() {
    logger.entry("begin getUserRoleList()");
    List<RoleBO> roleBOList = null;
    try {
      roleBOList = usersDAO.getUserRoleList();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserRoleList() - ERROR", e);
    }
    logger.exit("getUserRoleList() - Ends");
    return roleBOList;
  }
}
