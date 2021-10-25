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

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_DETAILS_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_CREATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_USER_INVITATION_RESENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCED_FOR_USER;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.PASSWORD_ENFORCEMENT_EMAIL_SENT;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_ACCOUNT_UPDATED_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_RECORD_VIEWED;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.GciAdminList;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.common.StudyBuilderConstants;
import com.fdahpstudydesigner.dao.UsersDAO;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.service.UsersService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import com.google.firebase.auth.ExportedUserRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UsersController {

  private static XLogger logger = XLoggerFactory.getXLogger(UsersController.class.getName());

  @Autowired private LoginService loginService;

  @Autowired private StudyService studyService;

  @Autowired private UsersService usersService;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private UsersDAO usersDAO;

  Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
  String gciEnabled = configMap.get("gciEnabled");

  @RequestMapping("/adminUsersEdit/activateOrDeactivateUser.do")
  public void activateOrDeactivateUser(
      HttpServletRequest request, HttpServletResponse response, String userId, String userStatus)
      throws IOException {
    logger.entry("begin activateOrDeactivateUser()");
    String msg = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (null != userSession) {
        msg =
            usersService.activateOrDeactivateUser(
                userId, Integer.valueOf(userStatus), userSession.getUserId(), userSession, request);
      }
    } catch (Exception e) {
      logger.error("UsersController - activateOrDeactivateUser() - ERROR", e);
    }
    logger.exit("activateOrDeactivateUser() - Ends");
    jsonobject.put("message", msg);
    response.setContentType("application/json");
    out = response.getWriter();
    out.print(jsonobject);
  }

  @RequestMapping("/adminUsersEdit/addOrEditUserDetails.do")
  public ModelAndView addOrEditUserDetails(HttpServletRequest request) {
    logger.entry("begin addOrEditUserDetails()");
    ModelAndView mav = new ModelAndView();
    ModelMap map = new ModelMap();
    UserBO userBO = null;
    List<StudyListBean> studyBOs = null;
    List<RoleBO> roleBOList = null;
    List<StudyBo> studyBOList = null;
    String actionPage = "";
    List<Integer> permissions = null;
    String usrId = null;
    List<UserBO> userList = null;
    List<GciAdminList> gciAdminList = new ArrayList<GciAdminList>();
    Set<String> sbUserList = new HashSet<>();
    Set<String> gciUserList = new HashSet<>();
    List<String> adminList = new ArrayList<String>();

    try {
      if (FdahpStudyDesignerUtil.isSession(request)) {
        String userId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("userId"))
                ? ""
                : request.getParameter("userId");
        String checkRefreshFlag =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("checkRefreshFlag"))
                ? ""
                : request.getParameter("checkRefreshFlag");
        if (!"".equalsIgnoreCase(checkRefreshFlag)) {

          if (!"".equals(userId)) {
            userBO = usersService.getUserDetails(userId);
          }
          if (Boolean.parseBoolean(gciEnabled)) {
            userList = usersService.getUserList();

            List<GciAdminList> users = new ArrayList<GciAdminList>();
            Map<String, GciAdminList> userMap = new HashMap<>();
            for (UserBO user : userList) {
              GciAdminList sbUser = new GciAdminList();
              // Study builder user email list
              sbUserList.add(user.getUserEmail());
              sbUser.setEmailId(user.getUserEmail());
              users.add(sbUser);
              userMap.put(user.getUserEmail(), sbUser);
            }

            // Start listing users from the beginning, 1000 at a time.
            ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);

            for (ExportedUserRecord exportedUserRecord : page.iterateAll()) {
              // Google identity platform user email list
              gciUserList.add(exportedUserRecord.getEmail());
              GciAdminList admin = new GciAdminList();
              if (!exportedUserRecord.isDisabled()) {
                admin.setEmailId(exportedUserRecord.getEmail());
                admin.setUid(exportedUserRecord.getUid());
                gciAdminList.add(admin);
              }
            }

            for (GciAdminList a1 : gciAdminList) {
              if (!userMap.containsKey(a1.getEmailId())) {
                adminList.add(a1.getEmailId());
              }
            }
          }

          if (!"".equals(userId)) {
            actionPage = FdahpStudyDesignerConstants.EDIT_PAGE;
            if (null != userBO) {
              studyBOs = studyService.getStudyListByUserId(userBO.getUserId());
              permissions = usersService.getPermissionsByUserId(userBO.getUserId());
            }
          } else {
            actionPage = FdahpStudyDesignerConstants.ADD_PAGE;
            if (!adminList.isEmpty()) {
              map.addAttribute("adminList", adminList);
            }
          }

          roleBOList = usersService.getUserRoleList();
          studyBOList = studyService.getAllStudyList();
          map.addAttribute("actionPage", actionPage);
          map.addAttribute("userBO", userBO);
          map.addAttribute("permissions", permissions);
          map.addAttribute("roleBOList", roleBOList);
          map.addAttribute("studyBOList", studyBOList);
          map.addAttribute("studyBOs", studyBOs);
          mav = new ModelAndView("addOrEditUserPage", map);
        } else {
          mav = new ModelAndView("redirect:/adminUsersView/getUserList.do");
        }
      }
    } catch (Exception e) {
      logger.error("UsersController - addOrEditUserDetails() - ERROR", e);
    }
    logger.exit("addOrEditUserDetails() - Ends");
    return mav;
  }

  @RequestMapping("/adminUsersEdit/addOrUpdateUserDetails.do")
  public ModelAndView addOrUpdateUserDetails(
      HttpServletRequest request, UserBO userBO, BindingResult result) {
    logger.entry("begin addOrUpdateUserDetails()");
    ModelAndView mav = new ModelAndView();
    String msg = "";
    String permissions = "";
    int count = 1;
    List<Integer> permissionList = new ArrayList<>();
    boolean addFlag = false;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (null != userSession) {

        String manageNotifications =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("manageNotifications"))
                ? ""
                : request.getParameter("manageNotifications");
        String manageStudies =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("manageStudies"))
                ? ""
                : request.getParameter("manageStudies");
        String addingNewStudy =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("addingNewStudy"))
                ? ""
                : request.getParameter("addingNewStudy");
        String selectedStudies =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("selectedStudies"))
                ? ""
                : request.getParameter("selectedStudies");
        String permissionValues =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("permissionValues"))
                ? ""
                : request.getParameter("permissionValues");
        String ownUser =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("ownUser"))
                ? ""
                : request.getParameter("ownUser");
        if (StringUtils.isEmpty(userBO.getUserId())) {
          addFlag = true;
          userBO.setCreatedBy(userSession.getUserId());
          userBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        } else {
          addFlag = false;
          userBO.setModifiedBy(userSession.getUserId());
          userBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
        }

        if (userBO.getRoleId().equals("1")) {
          // Superadmin flow
          permissions = FdahpStudyDesignerConstants.SUPER_ADMIN_PERMISSIONS;
        } else {
          // Study admin flow
          if (!"".equals(manageNotifications)) {
            if ("0".equals(manageNotifications)) {
              permissions +=
                  count > 1
                      ? ",ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW"
                      : "ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW";
              count++;
              permissionList.add(
                  FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW);
            } else if ("1".equals(manageNotifications)) {
              permissions +=
                  count > 1
                      ? ",ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW"
                      : "ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW";
              count++;
              permissionList.add(
                  FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW);
              permissions +=
                  count > 1
                      ? ",ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT"
                      : "ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT";
              permissionList.add(
                  FdahpStudyDesignerConstants.ROLE_MANAGE_APP_WIDE_NOTIFICATION_EDIT);
            }
          }
          if (!"".equals(manageStudies)) {
            if ("1".equals(manageStudies)) {
              permissions += count > 1 ? ",ROLE_MANAGE_STUDIES" : "ROLE_MANAGE_STUDIES";
              count++;
              permissionList.add(FdahpStudyDesignerConstants.ROLE_MANAGE_STUDIES);
              if (!"".equals(addingNewStudy) && "1".equals(addingNewStudy)) {
                permissions +=
                    count > 1 ? ",ROLE_CREATE_MANAGE_STUDIES" : "ROLE_CREATE_MANAGE_STUDIES";
                permissionList.add(FdahpStudyDesignerConstants.ROLE_CREATE_MANAGE_STUDIES);
              }
            } else {
              selectedStudies = "";
              permissionValues = "";
            }
          } else {
            selectedStudies = "";
            permissionValues = "";
          }
        }
        AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
        msg =
            usersService.addOrUpdateUserDetails(
                request,
                userBO,
                permissions,
                selectedStudies,
                permissionValues,
                userSession,
                auditRequest);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(msg)) {
          if (addFlag) {
            request
                .getSession()
                .setAttribute(
                    FdahpStudyDesignerConstants.SUC_MSG, propMap.get("add.user.success.message"));
          } else {
            request.getSession().setAttribute("ownUser", ownUser);
            request
                .getSession()
                .setAttribute(
                    FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get("update.user.success.message"));
          }
        } else {
          request.getSession().setAttribute(FdahpStudyDesignerConstants.ERR_MSG, msg);
          if (addFlag) {
            auditLogEventHelper.logEvent(NEW_USER_CREATION_FAILED, auditRequest);
          } else {
            auditLogEventHelper.logEvent(USER_ACCOUNT_UPDATED_FAILED, auditRequest);
          }
        }
        mav = new ModelAndView("redirect:/adminUsersView/getUserList.do");
      }
    } catch (Exception e) {
      logger.error("UsersController - addOrUpdateUserDetails() - ERROR", e);
    }
    logger.exit("addOrUpdateUserDetails() - Ends");
    return mav;
  }

  @RequestMapping("/adminUsersEdit/enforcePasswordChange.do")
  public ModelAndView enforcePasswordChange(HttpServletRequest request) {
    logger.entry("begin enforcePasswordChange()");
    ModelAndView mav = new ModelAndView();
    String msg = "";
    List<String> emails = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      String changePassworduserId =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter("changePassworduserId"))
              ? ""
              : request.getParameter("changePassworduserId");
      String emailId =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter("emailId"))
              ? ""
              : request.getParameter("emailId");
      if (null != userSession) {
        if (StringUtils.isNotEmpty(emailId) && StringUtils.isNotEmpty(changePassworduserId)) {
          msg = usersService.enforcePasswordChange(changePassworduserId, emailId);
          if (StringUtils.isNotEmpty(msg)
              && msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            Map<String, String> values = new HashMap<>();
            values.put(StudyBuilderConstants.EDITED_USER_ID, changePassworduserId);
            auditLogEventHelper.logEvent(PASSWORD_CHANGE_ENFORCED_FOR_USER, auditRequest, values);

            String sent =
                loginService.sendPasswordResetLinkToMail(
                    request, emailId, "", "enforcePasswordChange", auditRequest);
            if (FdahpStudyDesignerConstants.SUCCESS.equals(sent)) {
              auditLogEventHelper.logEvent(PASSWORD_ENFORCEMENT_EMAIL_SENT, auditRequest, values);
            } else {
              auditLogEventHelper.logEvent(
                  PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED, auditRequest, values);
            }
          }
        } else {
          msg = usersService.enforcePasswordChange(null, "");
          if (StringUtils.isNotEmpty(msg)
              && msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            auditLogEventHelper.logEvent(PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS, auditRequest);
            emails = usersService.getActiveUserEmailIds();
            if ((emails != null) && !emails.isEmpty()) {
              int failedCount = 0;
              int successCount = 0;
              for (String email : emails) {
                String sent =
                    loginService.sendPasswordResetLinkToMail(
                        request, email, "", "enforcePasswordChange", auditRequest);
                if (FdahpStudyDesignerConstants.SUCCESS.equals(sent)) {
                  successCount++;
                } else {
                  failedCount++;
                }
              }
              if (successCount == emails.size()) {
                auditLogEventHelper.logEvent(
                    PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_SENT, auditRequest);
              }

              if (failedCount > 0) {
                auditLogEventHelper.logEvent(
                    PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_FAILED, auditRequest);
              }
            }
          }
        }
        if (StringUtils.isNotEmpty(msg)
            && msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          request
              .getSession()
              .setAttribute(
                  FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get("password.enforce.link.success.message"));
        } else {
          request
              .getSession()
              .setAttribute(
                  FdahpStudyDesignerConstants.ERR_MSG,
                  propMap.get("password.enforce.failure.message"));
        }
        mav = new ModelAndView("redirect:/adminUsersView/getUserList.do");
      }
    } catch (Exception e) {
      logger.error("UsersController - enforcePasswordChange() - ERROR", e);
    }
    logger.exit("enforcePasswordChange() - Ends");
    return mav;
  }

  @RequestMapping("/adminUsersView/getUserList.do")
  public ModelAndView getUserList(HttpServletRequest request) {
    logger.entry("begin getUserList()");
    ModelAndView mav = new ModelAndView();
    ModelMap map = new ModelMap();
    List<UserBO> userList = null;
    String sucMsg = "";
    String errMsg = "";
    String ownUser = "";
    List<RoleBO> roleList = null;
    try {
      if (FdahpStudyDesignerUtil.isSession(request)) {
        if (null != request.getSession().getAttribute(FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null != request.getSession().getAttribute(FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.ERR_MSG);
        }

        ownUser = (String) request.getSession().getAttribute("ownUser");
        userList = usersService.getUserList();
        List<UserBO> gciUserList = usersDAO.getGciUserList();
        getGciUserInfo(userList, gciUserList);

        roleList = usersService.getUserRoleList();
        map.addAttribute("roleList", roleList);
        map.addAttribute("userList", userList);
        map.addAttribute("ownUser", ownUser);
        mav = new ModelAndView("userListPage", map);
      }
    } catch (Exception e) {
      logger.error("UsersController - getUserList() - ERROR", e);
    }
    logger.exit("getUserList() - Ends");
    return mav;
  }

  private void getGciUserInfo(List<UserBO> userList, List<UserBO> gciUserList)
      throws FirebaseAuthException {
    List<String> userEmail = new ArrayList<>();
    List<String> gciEmail = new ArrayList<>();
    List<String> gciDisabledEmail = new ArrayList<>();
    if (Boolean.parseBoolean(gciEnabled)) {
      for (UserBO user : gciUserList) {
        userEmail.add(user.getUserEmail());
      }
      // Start listing users from the beginning, 1000 at a time.
      ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
      for (ExportedUserRecord exportedUserRecord : page.iterateAll()) {
        if (exportedUserRecord.isDisabled()) {
          gciDisabledEmail.add(exportedUserRecord.getEmail());
        }
        gciEmail.add(exportedUserRecord.getEmail());
      }

      List<String> deletedGciUser = ListUtils.removeAll(userEmail, gciEmail);
      List<String> disableUsers = new ArrayList<>();
      disableUsers.addAll(deletedGciUser);
      disableUsers.addAll(gciDisabledEmail);

      for (UserBO user : userList) {
        for (String disableUser : disableUsers) {
          if (user.getUserEmail().equals(disableUser) && user.isGciUser()) {
            user.setDisableGciUser("Y");
          }
        }
      }
    } else {
      if (!gciUserList.isEmpty()) {
        for (UserBO user : userList) {
          if (user.isGciUser()) {
            user.setDisableGciUser("Y");
          }
        }
      }
    }
  }

  @RequestMapping("/adminUsersEdit/resendActivateDetailsLink.do")
  public ModelAndView resendActivateDetailsLink(HttpServletRequest request) {
    logger.entry("begin resendActivateDetailsLink()");
    ModelAndView mav = new ModelAndView();
    String msg = "";
    UserBO userBo = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (null != userSession) {
        String userId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("userId"))
                ? ""
                : request.getParameter("userId");
        if (StringUtils.isNotEmpty(userId)) {
          userBo = usersService.getUserDetails(userId);
          if (userBo != null) {
            msg =
                loginService.sendPasswordResetLinkToMail(
                    request, userBo.getUserEmail(), "", "USER", auditRequest);
          }
          if (msg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            request
                .getSession()
                .setAttribute(
                    FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get("resent.link.success.message"));
            Map<String, String> values = new HashMap<>();
            values.put(StudyBuilderConstants.USER_ID, String.valueOf(userId));
            auditLogEventHelper.logEvent(NEW_USER_INVITATION_RESENT, auditRequest, values);
          } else {
            request.getSession().setAttribute(FdahpStudyDesignerConstants.ERR_MSG, msg);
          }
        }
        mav = new ModelAndView("redirect:/adminUsersView/getUserList.do");
      }
    } catch (Exception e) {
      logger.error("UsersController - resendActivateDetailsLink() - ERROR", e);
    }
    logger.exit("resendActivateDetailsLink() - Ends");
    return mav;
  }

  @RequestMapping("/adminUsersView/viewUserDetails.do")
  public ModelAndView viewUserDetails(HttpServletRequest request) {
    logger.entry("begin viewUserDetails()");
    ModelAndView mav = new ModelAndView();
    ModelMap map = new ModelMap();
    UserBO userBO = null;
    List<StudyListBean> studyBOs = null;
    List<RoleBO> roleBOList = null;
    List<StudyBo> studyBOList = null;
    String actionPage = FdahpStudyDesignerConstants.VIEW_PAGE;
    List<Integer> permissions = null;
    Map<String, String> values = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);

      if (FdahpStudyDesignerUtil.isSession(request)) {
        String userId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("userId"))
                ? ""
                : request.getParameter("userId");
        String checkViewRefreshFlag =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("checkViewRefreshFlag"))
                ? ""
                : request.getParameter("checkViewRefreshFlag");
        if (!"".equalsIgnoreCase(checkViewRefreshFlag)) {
          if (!"".equals(userId)) {
            userBO = usersService.getUserDetails(userId);
            if (null != userBO) {
              if (Boolean.parseBoolean(gciEnabled)) {
                if (userBO.isGciUser()) {
                  try {
                    UserRecord userRecord =
                        FirebaseAuth.getInstance().getUserByEmail(userBO.getUserEmail());
                    if (userRecord.isDisabled()) {
                      map.addAttribute("gciDisableUser", "Y");
                    }
                  } catch (Exception e) {
                    map.addAttribute("gciDisableUser", "Y");
                  }
                }
              } else {
                if (userBO.isGciUser()) {
                  map.addAttribute("gciDisableUser", "Y");
                }
              }
              studyBOs = studyService.getStudyListByUserId(userBO.getUserId());
              permissions = usersService.getPermissionsByUserId(userBO.getUserId());

              HttpSession session = request.getSession();
              SessionObject sesObj =
                  (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
              if (sesObj.getUserId().equals(userBO.getUserId())) {
                auditLogEventHelper.logEvent(ACCOUNT_DETAILS_VIEWED, auditRequest);
              } else {
                values.put("viewed_user_id", userId);
                auditLogEventHelper.logEvent(USER_RECORD_VIEWED, auditRequest, values);
              }
            }
          }
          roleBOList = usersService.getUserRoleList();
          studyBOList = studyService.getAllStudyList();
          map.addAttribute("actionPage", actionPage);
          map.addAttribute("userBO", userBO);
          map.addAttribute("permissions", permissions);
          map.addAttribute("roleBOList", roleBOList);
          map.addAttribute("studyBOList", studyBOList);
          map.addAttribute("studyBOs", studyBOs);
          mav = new ModelAndView("addOrEditUserPage", map);
        } else {
          mav = new ModelAndView("redirect:getUserList.do");
        }
      }
    } catch (Exception e) {
      logger.error("UsersController - viewUserDetails() - ERROR", e);
    }
    logger.exit("viewUserDetails() - Ends");
    return mav;
  }
}
