/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_EMAIL_FAILED;
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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl implements UsersService {

  private static Logger logger = Logger.getLogger(UsersServiceImpl.class);

  @Autowired private StudyBuilderAuditEventHelper auditLogHelper;

  @Autowired LoginService loginService;

  @Autowired private UsersDAO usersDAO;

  @Override
  public String activateOrDeactivateUser(
      int userId,
      int userStatus,
      int loginUser,
      SessionObject userSession,
      HttpServletRequest request) {
    logger.info("UsersServiceImpl - activateOrDeactivateUser() - Starts");
    String msg = FdahpStudyDesignerConstants.FAILURE;
    List<String> superAdminEmailList = null;
    Map<String, String> keyValueForSubject = null;
    String dynamicContent = "";
    UserBO userBo = null;
    UserBO adminFullNameIfSizeOne = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customerCareMail = "";
    String status = "";

    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      msg = usersDAO.activateOrDeactivateUser(userId, userStatus, loginUser, userSession);
      superAdminEmailList = usersDAO.getSuperAdminList();
      userBo = usersDAO.getUserDetails(userId);
      if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        StudyBuilderAuditEvent auditLogEvent =
            userStatus == 1 ? USER_RECORD_DEACTIVATED : USER_ACCOUNT_RE_ACTIVATED;
        Map<String, String> values = new HashMap<>();
        values.put(EDITED_USER_ID, String.valueOf(userId));
        auditLogHelper.logEvent(auditLogEvent, auditRequest, values);
        keyValueForSubject = new HashMap<String, String>();
        if ((superAdminEmailList != null) && !superAdminEmailList.isEmpty()) {
          if (userStatus == 1) {
            status = "Deactivated";
          } else {
            status = "Active";
          }
          if (superAdminEmailList.size() == 1) {
            for (String email : superAdminEmailList) {
              adminFullNameIfSizeOne = usersDAO.getSuperAdminNameByEmailId(email);
              keyValueForSubject.put(
                  "$admin",
                  adminFullNameIfSizeOne.getFirstName()
                      + " "
                      + adminFullNameIfSizeOne.getLastName());
            }
          } else {
            keyValueForSubject.put("$admin", "Admin");
          }
          keyValueForSubject.put("$userStatus", status);
          keyValueForSubject.put(
              "$sessionAdminFullName",
              userSession.getFirstName() + " " + userSession.getLastName());
          keyValueForSubject.put("$userEmail", userBo.getUserEmail());
          keyValueForSubject.put("$orgName", propMap.get("orgName"));
          dynamicContent =
              FdahpStudyDesignerUtil.genarateEmailContent(
                  "mailForAdminUserUpdateContent", keyValueForSubject);
          EmailNotification.sendEmailNotification(
              "mailForAdminUserUpdateSubject", dynamicContent, null, superAdminEmailList, null);
        }
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
            EmailNotification.sendEmailNotification(
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
    logger.info("UsersServiceImpl - activateOrDeactivateUser() - Ends");
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
    logger.info("UsersServiceImpl - addOrUpdateUserDetails() - Starts");
    UserBO userBO2 = null;
    String msg = FdahpStudyDesignerConstants.FAILURE;
    boolean addFlag = false;
    List<StudyBuilderAuditEvent> auditLogEvents = new LinkedList<>();
    Map<String, String> values = new HashMap<>();
    boolean emailIdChange = false;
    UserBO userBO3 = null;

    try {
      if (null == userBO.getUserId()) {
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
          } else {
            auditLogEvents.add(NEW_USER_INVITATION_EMAIL_FAILED);
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
    logger.info("UsersServiceImpl - addOrUpdateUserDetails() - Ends");
    return msg;
  }

  @Override
  public String enforcePasswordChange(Integer userId, String email) {
    logger.info("UsersServiceImpl - enforcePasswordChange() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message = usersDAO.enforcePasswordChange(userId, email);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - enforcePasswordChange() - ERROR", e);
    }
    logger.info("UsersServiceImpl - enforcePasswordChange() - Ends");
    return message;
  }

  @Override
  public List<String> getActiveUserEmailIds() {
    logger.info("UsersServiceImpl - getActiveUserEmailIds() - Starts");
    List<String> emails = null;
    try {
      emails = usersDAO.getActiveUserEmailIds();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getActiveUserEmailIds() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getActiveUserEmailIds() - Ends");
    return emails;
  }

  @Override
  public List<Integer> getPermissionsByUserId(Integer userId) {
    logger.info("UsersServiceImpl - permissionsByUserId() - Starts");
    List<Integer> permissions = null;
    try {
      permissions = usersDAO.getPermissionsByUserId(userId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - permissionsByUserId() - ERROR", e);
    }
    logger.info("UsersServiceImpl - permissionsByUserId() - Ends");
    return permissions;
  }

  @Override
  public UserBO getUserDetails(Integer userId) {
    logger.info("UsersServiceImpl - getUserDetails() - Starts");
    UserBO userBO = null;
    try {
      userBO = usersDAO.getUserDetails(userId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserDetails() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getUserDetails() - Ends");
    return userBO;
  }

  @Override
  public List<UserBO> getUserList() {
    logger.info("UsersServiceImpl - getUserList() - Starts");
    List<UserBO> userList = null;
    try {
      userList = usersDAO.getUserList();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserList() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getUserList() - Ends");
    return userList;
  }

  @Override
  public Integer getUserPermissionByUserId(Integer sessionUserId) {
    logger.info("UsersServiceImpl - getUserPermissionByUserId() - Starts");
    Integer userId = null;
    try {
      userId = usersDAO.getUserPermissionByUserId(sessionUserId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserPermissionByUserId() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getUserPermissionByUserId() - Ends");
    return userId;
  }

  @Override
  public RoleBO getUserRole(int roleId) {
    logger.info("UsersServiceImpl - getUserRole() - Starts");
    RoleBO roleBO = null;
    try {
      roleBO = usersDAO.getUserRole(roleId);
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserRole() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getUserRole() - Ends");
    return roleBO;
  }

  @Override
  public List<RoleBO> getUserRoleList() {
    logger.info("UsersServiceImpl - getUserRoleList() - Starts");
    List<RoleBO> roleBOList = null;
    try {
      roleBOList = usersDAO.getUserRoleList();
    } catch (Exception e) {
      logger.error("UsersServiceImpl - getUserRoleList() - ERROR", e);
    }
    logger.info("UsersServiceImpl - getUserRoleList() - Ends");
    return roleBOList;
  }
}
