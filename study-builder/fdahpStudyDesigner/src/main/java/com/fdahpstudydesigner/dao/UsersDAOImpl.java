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

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.UserIdAccessLevelInfo;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsersDAOImpl implements UsersDAO {

  private static XLogger logger = XLoggerFactory.getXLogger(UsersDAOImpl.class.getName());

  HibernateTemplate hibernateTemplate;

  private Transaction transaction = null;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Override
  public String activateOrDeactivateUser(
      String userId, int userStatus, String loginUser, SessionObject userSession) {
    logger.entry("begin activateOrDeactivateUser()");
    String msg = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int count = 0;
    Query query = null;
    Boolean forceLogout = false;
    UserBO userBO = null;
    boolean userStatusNew;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      userBO = getUserDetails(userId);
      transaction = session.beginTransaction();
      if (userStatus == 0) {
        userStatusNew = true;
        forceLogout = false;

      } else {
        userStatusNew = false;
        forceLogout = true;
      }
      query =
          session
              .createQuery(
                  " UPDATE UserBO SET enabled =:userStatusNew "
                      + ", modifiedOn = now(), modifiedBy =:loginUser "
                      + ",forceLogout =:forceLogout "
                      + " WHERE userId =:userId ")
              .setParameter("userStatusNew", userStatusNew)
              .setParameter("loginUser", loginUser)
              .setParameter("forceLogout", forceLogout)
              .setParameter("userId", userId);
      count = query.executeUpdate();
      if (count > 0) {

        msg = FdahpStudyDesignerConstants.SUCCESS;
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("UsersDAOImpl - activateOrDeactivateUser() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("activateOrDeactivateUser() - Ends");
    return msg;
  }

  @SuppressWarnings("unchecked")
  @Override
  public UserIdAccessLevelInfo addOrUpdateUserDetails(
      UserBO userBO, String permissions, String selectedStudies, String permissionValues) {
    logger.entry("begin addOrUpdateUserDetails()");
    Session session = null;
    String userId = null;
    String msg = FdahpStudyDesignerConstants.FAILURE;
    Query query = null;
    UserBO userBO2 = null;
    Set<UserPermissions> permissionSet = null;
    StudyPermissionBO studyPermissionBO = null;
    String[] selectedStudy = null;
    String[] permissionValue = null;
    boolean updateFlag = false;
    UserIdAccessLevelInfo userIdAccessLevelInfo = null;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      userIdAccessLevelInfo = new UserIdAccessLevelInfo();
      if (null == userBO.getUserId()) {
        userId = (String) session.save(userBO);
        userIdAccessLevelInfo.setUserId(userId);
      } else {
        session.update(userBO);
        userId = userBO.getUserId();
        userIdAccessLevelInfo.setUserId(userId);
        updateFlag = true;
      }

      query =
          session
              .createQuery(" FROM UserBO UBO where UBO.userId =:userId ")
              .setParameter("userId", userId);
      userBO2 = (UserBO) query.uniqueResult();
      if (!permissions.isEmpty()) {
        List<String> permissionList = Arrays.asList(permissions.split(","));
        permissionSet =
            new HashSet<UserPermissions>(
                session
                    .createQuery(
                        "FROM UserPermissions UPBO WHERE UPBO.permissions IN ( :permissions )")
                    .setParameterList("permissions", permissionList)
                    .list());
        userBO2.setPermissionList(permissionSet);
        userBO2.setAccessLevel(userBO2.getRoleId().equals("1") ? "SUPERADMIN" : "STUDY ADMIN");
        session.update(userBO2);
        userIdAccessLevelInfo.setAccessLevel(userBO2.getAccessLevel());
      } else {
        userBO2.setPermissionList(null);
        session.update(userBO2);
      }

      if (updateFlag && "".equals(selectedStudies)) {
        query =
            session
                .createSQLQuery(" delete from study_permission where user_id =:userId ")
                .setParameter("userId", userId);
        query.executeUpdate();
      }

      if (!"".equals(selectedStudies)
          && !"".equals(permissionValues)
          && userBO2.getRoleId().equals("2")) {
        selectedStudy = selectedStudies.split(",");
        permissionValue = permissionValues.split(",");
        List<String> selectedStudiesList = Arrays.asList(selectedStudies.split(","));
        if (updateFlag) {
          query =
              session
                  .createSQLQuery(
                      " delete from study_permission where study_id not in (:selectedStudies) and user_id =:userId")
                  .setParameterList("selectedStudies", selectedStudiesList)
                  .setParameter("userId", userId);
          query.executeUpdate();
        }
        for (int i = 0; i < selectedStudy.length; i++) {
          query =
              session
                  .createQuery(
                      " FROM StudyPermissionBO UBO where UBO.studyId=:studyId"
                          + " AND UBO.userId=:userId")
                  .setParameter("userId", userId)
                  .setParameter("studyId", selectedStudy[i]);
          studyPermissionBO = (StudyPermissionBO) query.uniqueResult();
          if (null != studyPermissionBO) {
            studyPermissionBO.setViewPermission("1".equals(permissionValue[i]) ? true : false);
            session.update(studyPermissionBO);
          } else {
            studyPermissionBO = new StudyPermissionBO();
            studyPermissionBO.setStudyId(selectedStudy[i]);
            studyPermissionBO.setViewPermission("1".equals(permissionValue[i]) ? true : false);
            studyPermissionBO.setUserId(userId);
            session.save(studyPermissionBO);
          }
        }

      } else if (userBO2.getRoleId().equals("1")) {
        query = session.createQuery(" FROM StudyBo SBO WHERE SBO.version = 0");
        List<StudyBo> studyBOList = query.list();
        if (CollectionUtils.isNotEmpty(studyBOList)) {
          for (int i = 0; i < studyBOList.size(); i++) {
            query =
                session
                    .createQuery(
                        " FROM StudyPermissionBO UBO where UBO.studyId=:studyId"
                            + " AND UBO.userId=:userId")
                    .setParameter("userId", userId)
                    .setParameter("studyId", studyBOList.get(i).getId());
            studyPermissionBO = (StudyPermissionBO) query.uniqueResult();
            if (null != studyPermissionBO) {
              studyPermissionBO.setViewPermission(true);
              session.update(studyPermissionBO);
            } else {
              studyPermissionBO = new StudyPermissionBO();
              studyPermissionBO.setStudyId(studyBOList.get(i).getId());
              studyPermissionBO.setViewPermission(true);
              studyPermissionBO.setUserId(userId);
              session.save(studyPermissionBO);
            }
          }
        }
      }
      transaction.commit();
      msg = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      transaction.rollback();
      logger.error("UsersDAOImpl - addOrUpdateUserDetails() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("addOrUpdateUserDetails() - Ends");
    if (msg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
      return userIdAccessLevelInfo;
    } else return null;
  }

  @Override
  public String enforcePasswordChange(String userId, String email) {
    logger.entry("begin enforcePasswordChange()");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      // sending activationLink to all active users and send the
      // deactivate users when they active
      List<String> SAEmailIdList = getSuperAdminList();
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if ((userId != null) && StringUtils.isNotEmpty(email)) {
        int count =
            session
                .createSQLQuery(
                    "Update users set force_logout='Y', credentialsNonExpired=false WHERE user_id =:userId")
                .setParameter("userId", userId)
                .executeUpdate();
        if (count > 0) {
          session
              .createSQLQuery("update user_attempts set attempts = 0 WHERE email_id =:email")
              .setParameter("email", email)
              .executeUpdate();
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      } else {
        int count =
            session
                .createSQLQuery(
                    "Update users set force_logout='Y' WHERE status=true AND email NOT IN(:emailIds)")
                .setParameterList("emailIds", SAEmailIdList)
                .executeUpdate();
        if (count > 0) {
          int result =
              session
                  .createSQLQuery(
                      "Update users set credentialsNonExpired=false WHERE email NOT IN(:emailIds)")
                  .setParameterList("emailIds", SAEmailIdList)
                  .executeUpdate();
          if (result > 0) {
            session
                .createSQLQuery(
                    "update user_attempts set attempts = 0 WHERE email_id NOT IN(:emailIds)")
                .setParameterList("emailIds", SAEmailIdList)
                .executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }

      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("UsersDAOImpl - enforcePasswordChange() - ERROR", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("enforcePasswordChange() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getActiveUserEmailIds() {
    logger.entry("begin getActiveUserEmailIds()");
    Session session = null;
    List<String> emails = null;
    try {
      List<String> SAEmailIdList = getSuperAdminList();
      session = hibernateTemplate.getSessionFactory().openSession();
      // sending activationLink to all active users and send the
      // deactivate users when they active
      Query query =
          session.createSQLQuery(
              " SELECT u.email "
                  + "FROM users u,roles r WHERE r.role_id = u.role_id and u.status=1 AND email NOT IN(:emailIds)");
      query.setParameterList("emailIds", SAEmailIdList);
      emails = query.list();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getActiveUserEmailIds() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getActiveUserEmailIds() - Ends");
    return emails;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> getPermissionsByUserId(String userId) {
    logger.entry("begin getPermissionsByUserId()");
    Session session = null;
    Query query = null;
    List<Integer> permissions = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .createSQLQuery(
                  " SELECT UPM.permission_id FROM user_permission_mapping UPM WHERE UPM.user_id =:userId ")
              .setParameter("userId", userId);
      permissions = query.list();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getPermissionsByUserId() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getPermissionsByUserId() - Ends");
    return permissions;
  }

  @Override
  public List<String> getSuperAdminList() {
    logger.entry("begin getSuperAdminList()");
    Session session = null;
    List<String> userSuperAdminList = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createSQLQuery(
              "Select u.email from users u where u.user_id in (select upm.user_id from user_permission_mapping upm where upm.permission_id = (select up.permission_id from user_permissions up where up.permissions = 'ROLE_SUPERADMIN'))");
      userSuperAdminList = query.list();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getSuperAdminList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getSuperAdminList() - Ends");
    return userSuperAdminList;
  }

  @Override
  public UserBO getSuperAdminNameByEmailId(String emailId) {
    logger.entry("begin getSuperAdminNameByEmailId()");
    Session session = null;
    UserBO userBo = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .createQuery(" from UserBO where userEmail = :emailId")
              .setParameter("emailId", emailId);
      userBo = (UserBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getSuperAdminNameByEmailId() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getSuperAdminNameByEmailId() - Ends");
    return userBo;
  }

  @Override
  public UserBO getUserDetails(String userId) {
    logger.entry("begin getUserDetails()");
    Session session = null;
    UserBO userBO = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getUserById").setString("userId", userId);
      userBO = (UserBO) query.uniqueResult();
      if ((userBO != null) && (userBO.getRoleId() != null)) {
        String roleName =
            (String)
                session
                    .createSQLQuery("select role_name from roles where role_id=:roleId")
                    .setParameter("roleId", userBO.getRoleId())
                    .uniqueResult();
        if (StringUtils.isNotEmpty(roleName)) {
          userBO.setRoleName(roleName);
        }
      }
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserDetails() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getUserDetails() - Ends");
    return userBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UserBO> getUserList() {
    logger.entry("begin getUserList()");
    Session session = null;
    List<UserBO> userList = null;
    List<Object[]> objList = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createSQLQuery(
              " SELECT u.user_id,u.first_name,u.last_name,u.email,r.role_name,u.status,"
                  + "u.password,u.email_changed,u.access_level FROM users u,roles r WHERE r.role_id = u.role_id  "
                  + " ORDER BY u.user_id DESC ");
      objList = query.list();
      if ((null != objList) && !objList.isEmpty()) {
        userList = new ArrayList<>();
        for (Object[] obj : objList) {
          UserBO userBO = new UserBO();
          userBO.setUserId(null != obj[0] ? (String) obj[0] : null);
          userBO.setFirstName(null != obj[1] ? String.valueOf(obj[1]) : "");
          userBO.setLastName(null != obj[2] ? String.valueOf(obj[2]) : "");
          userBO.setUserEmail(null != obj[3] ? String.valueOf(obj[3]) : "");
          userBO.setRoleName(null != obj[4] ? String.valueOf(obj[4]) : "");
          userBO.setEnabled(null != obj[5] ? (Boolean) obj[5] : false);
          userBO.setUserPassword(null != obj[6] ? String.valueOf(obj[6]) : "");
          userBO.setEmailChanged(null != obj[7] ? (Boolean) obj[7] : false);
          userBO.setAccessLevel(null != obj[8] ? String.valueOf(obj[8]) : "");
          userBO.setUserFullName(userBO.getFirstName() + " " + userBO.getLastName());
          userList.add(userBO);
        }
      }
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getUserList() - Ends");
    return userList;
  }

  @Override
  public String getUserPermissionByUserId(String sessionUserId) {
    logger.entry("begin getUserPermissionByUserId()");
    Session session = null;
    String userId = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .createSQLQuery(
                  "Select u.user_id from users u where u.user_id in "
                      + "(select upm.user_id from user_permission_mapping upm where upm.permission_id "
                      + "= (select up.permission_id from user_permissions up where "
                      + "up.permissions = 'ROLE_SUPERADMIN')) and u.user_id =:sessionUserId ")
              .setParameter("sessionUserId", sessionUserId);
      userId = (String) query.uniqueResult();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserPermissionByUserId() - ERROR", e);
    }
    logger.exit("getUserPermissionByUserId() - Ends");
    return userId;
  }

  @Override
  public RoleBO getUserRole(String roleId) {
    logger.entry("begin getUserRole()");
    Session session = null;
    RoleBO roleBO = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getUserRoleByRoleId").setString("roleId", roleId);
      roleBO = (RoleBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserRole() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getUserRole() - Ends");
    return roleBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<RoleBO> getUserRoleList() {
    logger.entry("begin getUserRoleList()");
    List<RoleBO> roleBOList = null;
    Query query = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery(" FROM RoleBO RBO");
      roleBOList = query.list();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserRoleList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.exit("getUserRoleList() - Ends");
    return roleBOList;
  }
}
