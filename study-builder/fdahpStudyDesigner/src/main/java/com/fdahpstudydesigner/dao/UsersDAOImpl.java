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

import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPermissions;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UsersDAOImpl implements UsersDAO {

  private static Logger logger = Logger.getLogger(UsersDAOImpl.class);

  HibernateTemplate hibernateTemplate;

  private Transaction transaction = null;

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Override
  public String activateOrDeactivateUser(
      int userId, int userStatus, int loginUser, SessionObject userSession) {
    logger.info("UsersDAOImpl - activateOrDeactivateUser() - Starts");
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
    logger.info("UsersDAOImpl - activateOrDeactivateUser() - Ends");
    return msg;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String addOrUpdateUserDetails(
      UserBO userBO, String permissions, String selectedStudies, String permissionValues) {
    logger.info("UsersDAOImpl - addOrUpdateUserDetails() - Starts");
    Session session = null;
    Integer userId = 0;
    String msg = FdahpStudyDesignerConstants.FAILURE;
    Query query = null;
    UserBO userBO2 = null;
    Set<UserPermissions> permissionSet = null;
    StudyPermissionBO studyPermissionBO = null;
    String[] selectedStudy = null;
    String[] permissionValue = null;
    boolean updateFlag = false;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (null == userBO.getUserId()) {
        userId = (Integer) session.save(userBO);
      } else {
        session.update(userBO);
        userId = userBO.getUserId();
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
        userBO2.setAccessLevel(FdahpStudyDesignerUtil.getUserAccessLevel(permissionSet));
        session.update(userBO2);
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

      if (!"".equals(selectedStudies) && !"".equals(permissionValues)) {
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
                  .setParameter("studyId", Integer.valueOf(selectedStudy[i]));
          studyPermissionBO = (StudyPermissionBO) query.uniqueResult();
          if (null != studyPermissionBO) {
            studyPermissionBO.setViewPermission("1".equals(permissionValue[i]) ? true : false);
            session.update(studyPermissionBO);
          } else {
            studyPermissionBO = new StudyPermissionBO();
            studyPermissionBO.setStudyId(Integer.parseInt(selectedStudy[i]));
            studyPermissionBO.setViewPermission("1".equals(permissionValue[i]) ? true : false);
            studyPermissionBO.setUserId(userId);
            session.save(studyPermissionBO);
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
    logger.info("UsersDAOImpl - addOrUpdateUserDetails() - Ends");
    return msg;
  }

  @Override
  public String enforcePasswordChange(Integer userId, String email) {
    logger.info("UsersDAOImpl - enforcePasswordChange() - Starts");
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
    logger.info("UsersDAOImpl - enforcePasswordChange() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<String> getActiveUserEmailIds() {
    logger.info("UsersDAOImpl - getActiveUserEmailIds() - Starts");
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
    logger.info("UsersDAOImpl - getActiveUserEmailIds() - Ends");
    return emails;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> getPermissionsByUserId(Integer userId) {
    logger.info("UsersDAOImpl - getPermissionsByUserId() - Starts");
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
    logger.info("UsersDAOImpl - getPermissionsByUserId() - Ends");
    return permissions;
  }

  @Override
  public List<String> getSuperAdminList() {
    logger.info("UsersDAOImpl - getSuperAdminList() - Starts");
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
    logger.info("UsersDAOImpl - getSuperAdminList() - Ends");
    return userSuperAdminList;
  }

  @Override
  public UserBO getSuperAdminNameByEmailId(String emailId) {
    logger.info("UsersDAOImpl - getSuperAdminNameByEmailId() - Starts");
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
    logger.info("UsersDAOImpl - getSuperAdminNameByEmailId() - Ends");
    return userBo;
  }

  @Override
  public UserBO getUserDetails(int userId) {
    logger.info("UsersDAOImpl - getUserDetails() - Starts");
    Session session = null;
    UserBO userBO = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getUserById").setInteger("userId", userId);
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
    logger.info("UsersDAOImpl - getUserDetails() - Ends");
    return userBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UserBO> getUserList() {
    logger.info("UsersDAOImpl - getUserList() - Starts");
    Session session = null;
    List<UserBO> userList = null;
    List<Object[]> objList = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session.createSQLQuery(
              " SELECT u.user_id,u.first_name,u.last_name,u.email,r.role_name,u.status,"
                  + "u.password,u.email_changed,u.access_level FROM users u,roles r WHERE r.role_id = u.role_id and u.user_id "
                  + "not in (select upm.user_id from user_permission_mapping upm where "
                  + "upm.permission_id = (select up.permission_id from user_permissions up "
                  + "where up.permissions ='ROLE_SUPERADMIN')) ORDER BY u.user_id DESC ");
      objList = query.list();
      if ((null != objList) && !objList.isEmpty()) {
        userList = new ArrayList<>();
        for (Object[] obj : objList) {
          UserBO userBO = new UserBO();
          userBO.setUserId(null != obj[0] ? (Integer) obj[0] : 0);
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
    logger.info("UsersDAOImpl - getUserList() - Ends");
    return userList;
  }

  @Override
  public Integer getUserPermissionByUserId(Integer sessionUserId) {
    logger.info("UsersDAOImpl - getUserPermissionByUserId() - Starts");
    Session session = null;
    Integer userId = null;
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
      userId = (Integer) query.uniqueResult();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserPermissionByUserId() - ERROR", e);
    }
    logger.info("UsersDAOImpl - getUserPermissionByUserId() - Ends");
    return userId;
  }

  @Override
  public RoleBO getUserRole(int roleId) {
    logger.info("UsersDAOImpl - getUserRole() - Starts");
    Session session = null;
    RoleBO roleBO = null;
    Query query = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getUserRoleByRoleId").setInteger("roleId", roleId);
      roleBO = (RoleBO) query.uniqueResult();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserRole() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.info("UsersDAOImpl - getUserRole() - Ends");
    return roleBO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<RoleBO> getUserRoleList() {
    logger.info("UsersDAOImpl - getUserRoleList() - Starts");
    List<RoleBO> roleBOList = null;
    Query query = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.createQuery(" FROM RoleBO RBO ");
      roleBOList = query.list();
    } catch (Exception e) {
      logger.error("UsersDAOImpl - getUserRoleList() - ERROR", e);
    } finally {
      if (null != session) {
        session.close();
      }
    }
    logger.info("UsersDAOImpl - getUserRoleList() - Ends");
    return roleBOList;
  }
}
