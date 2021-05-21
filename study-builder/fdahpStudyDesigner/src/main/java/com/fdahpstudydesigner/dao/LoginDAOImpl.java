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

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ACCOUNT_LOCKED;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.FAILED_ATTEMPT;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.LOCK_TIME;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDAOImpl implements LoginDAO {

  private static XLogger logger = XLoggerFactory.getXLogger(LoginDAOImpl.class.getName());

  @Autowired private LoginService loginService;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  HibernateTemplate hibernateTemplate;
  private Query query = null;
  private Transaction transaction = null;

  public LoginDAOImpl() {}

  @Override
  public String changePassword(String userId, String newPassword, String oldPassword) {
    logger.info("LoginDAOImpl - changePassword() - Starts");

    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    UserBO adminUserBO = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String encrNewPass = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      query = session.getNamedQuery("getUserById").setString("userId", userId);
      adminUserBO = (UserBO) query.uniqueResult();
      if ((null != adminUserBO)
          && FdahpStudyDesignerUtil.compareEncryptedPassword(
              adminUserBO.getUserPassword(), oldPassword)) {
        encrNewPass = FdahpStudyDesignerUtil.getEncryptedPassword(newPassword);
        if ((null != encrNewPass) && !"".equals(encrNewPass)) {
          adminUserBO.setUserPassword(encrNewPass);
        }
        adminUserBO.setModifiedBy(userId);
        adminUserBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDate());
        adminUserBO.setPasswordExpiryDateTime(
            new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME).format(new Date()));
        session.update(adminUserBO);
        message = FdahpStudyDesignerConstants.SUCCESS;
      } else {
        message = propMap.get("invalid.oldpassword.msg");
      }
      transaction.commit();
    } catch (Exception e) {
      logger.error("LoginDAOImpl - changePassword() - ERROR ", e);
      if (transaction != null) {
        transaction.rollback();
      }
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("changePassword() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UserPasswordHistory> getPasswordHistory(String userId) {
    logger.info("LoginDAOImpl - updatePasswordHistory() - Starts");

    List<UserPasswordHistory> passwordHistories = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(userId)) {
        passwordHistories =
            session.getNamedQuery("getPaswordHistoryByUserId").setString("userId", userId).list();
      }

    } catch (Exception e) {
      logger.error("LoginDAOImpl - updatePasswordHistory() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.exit("updatePasswordHistory() - Ends");
    return passwordHistories;
  }

  @Override
  public UserAttemptsBo getUserAttempts(String userEmailId) {
    logger.entry("begin getUserAttempts()");
    Session session = null;
    UserAttemptsBo attemptsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      Query query = session.createQuery(" from UserAttemptsBo where userEmailId =:userEmailId");
      query.setParameter("userEmailId", userEmailId);
      attemptsBo = (UserAttemptsBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("LoginDAOImpl - getUserAttempts() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getUserAttempts() - Ends");
    return attemptsBo;
  }

  @Override
  public UserBO getUserBySecurityToken(String securityToken) {
    logger.entry("begin getUserBySecurityToken()");
    Session session = null;
    UserBO userBO = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      userBO =
          (UserBO)
              session
                  .getNamedQuery("getUserBySecurityToken")
                  .setString("securityToken", securityToken)
                  .uniqueResult();
      if ((null != userBO) && !userBO.getSecurityToken().equals(securityToken)) {
        userBO = null;
      }
    } catch (Exception e) {
      logger.error("LoginDAOImpl - getUserBySecurityToken() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getUserBySecurityToken() - Ends");
    return userBO;
  }

  @Override
  public UserBO getValidUserByEmail(String email) {
    logger.entry("begin getValidUserByEmail()");
    UserBO userBo = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      Query query = session.createQuery("from UserBO UBO where lower(userEmail)=:email");

      query.setParameter("email", email.toLowerCase());
      userBo = (UserBO) query.uniqueResult();
      if (userBo != null) {
        userBo.setUserLastLoginDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
        if (userBo.getRoleId() != null) {
          String role =
              (String)
                  session
                      .createSQLQuery("select role_name from roles where role_id=:roleId")
                      .setParameter("roleId", userBo.getRoleId())
                      .uniqueResult();
          if (StringUtils.isNotEmpty(role)) {
            userBo.setRoleName(role);
          }
        }
      }
    } catch (Exception e) {
      userBo = null;
      logger.error("LoginDAOImpl - getValidUserByEmail() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("getValidUserByEmail() - Ends");
    return userBo;
  }

  @Override
  public Boolean isFrocelyLogOutUser(String userId) {
    logger.info("LoginDAOImpl - isFrocelyLogOutUser() - Starts");

    UserBO userBo = null;
    boolean result = false;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(userId)) {
        userBo =
            (UserBO)
                session.getNamedQuery("getUserById").setString("userId", userId).uniqueResult();
        if (userBo != null) {
          result = userBo.isForceLogout();
        }
      }

    } catch (Exception e) {
      logger.error("LoginDAOImpl - isFrocelyLogOutUser() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("isFrocelyLogOutUser() - Ends");
    return result;
  }

  @Override
  public Boolean isUserEnabled(String userId) {
    logger.info("LoginDAOImpl - isUserExists() - Starts");
    UserBO userBo = null;
    boolean result = false;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(userId)) {
        userBo =
            (UserBO)
                session.getNamedQuery("getUserById").setString("userId", userId).uniqueResult();
        if (userBo != null) {
          result = userBo.isEnabled();
        }
      }

    } catch (Exception e) {
      logger.error("LoginDAOImpl - isUserExists() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("isUserExists() - Ends");
    return result;
  }

  private boolean isUserExists(String userEmail) {
    logger.entry("begin isUserExists()");
    UserBO userBo = null;
    boolean result = false;
    try {
      userBo = this.getValidUserByEmail(userEmail);
      if (userBo != null) {
        result = true;
      }
    } catch (Exception e) {
      logger.error("LoginDAOImpl - isUserExists() - ERROR ", e);
    }
    logger.exit("isUserExists() - Ends");
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void passwordLoginBlocked() {
    logger.entry("begin passwordLoginBlocked()");
    Session session = null;
    List<Integer> userBOList = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    int lastLoginExpirationInDay =
        Integer.parseInt("-" + propMap.get("lastlogin.expiration.in.day"));
    String lastLoginDateTime;
    String sb = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      lastLoginDateTime =
          new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
              .format(
                  FdahpStudyDesignerUtil.addDaysToDate(new Date(), lastLoginExpirationInDay + 1));
      sb =
          "SELECT u.user_id FROM users u,roles r WHERE r.role_id = u.role_id and u.user_id not in "
              + "(select upm.user_id from user_permission_mapping upm where upm.permission_id = "
              + "(select up.permission_id from user_permissions up where up.permissions ='ROLE_SUPERADMIN')) "
              + "AND u.user_login_datetime < :lastLoginDateTime AND u.status=1 ";
      query = session.createSQLQuery(sb).setParameter("lastLoginDateTime", lastLoginDateTime);
      userBOList = query.list();
      if ((userBOList != null) && !userBOList.isEmpty()) {
        session
            .createSQLQuery("Update users set status = 0 WHERE user_id in( :userBOList )")
            .setParameterList("userBOList", userBOList)
            .executeUpdate();
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - passwordLoginBlocked() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("passwordLoginBlocked() - Ends");
  }

  @Override
  public void resetFailAttempts(String userEmailId) {
    logger.entry("begin resetFailAttempts()");
    Session session = null;
    UserAttemptsBo attemptsBo = null;
    try {
      attemptsBo = this.getUserAttempts(userEmailId);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (attemptsBo != null) {
        session.delete(attemptsBo);
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - resetFailAttempts() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("resetFailAttempts() - Ends");
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Override
  public void updateFailAttempts(String userEmailId, AuditLogEventRequest auditRequest) {
    logger.entry("begin updateUser()");
    Session session = null;
    UserAttemptsBo attemptsBo = null;
    boolean isAcountLocked = false;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    final Integer MAX_ATTEMPTS = Integer.valueOf(propMap.get("max.login.attempts"));
    UserBO userBO = new UserBO();
    final Integer USER_LOCK_DURATION =
        Integer.valueOf(propMap.get("user.lock.duration.in.minutes"));
    try {
      attemptsBo = this.getUserAttempts(userEmailId);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (attemptsBo == null) {
        if (this.isUserExists(userEmailId)) {
          attemptsBo = new UserAttemptsBo();
          attemptsBo.setAttempts(1);
          attemptsBo.setUserEmail(userEmailId);
          attemptsBo.setLastModified(FdahpStudyDesignerUtil.getCurrentDateTime());
          session.save(attemptsBo);
        }
      } else {
        if ((attemptsBo.getAttempts() + 1) >= MAX_ATTEMPTS) {
          // locked user
          isAcountLocked = true;
        }
        if (this.isUserExists(userEmailId)) {
          if ((attemptsBo.getAttempts() >= MAX_ATTEMPTS)
              && new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                  .parse(
                      FdahpStudyDesignerUtil.addMinutes(
                          attemptsBo.getLastModified(), USER_LOCK_DURATION))
                  .before(
                      new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
                          .parse(FdahpStudyDesignerUtil.getCurrentDateTime()))) {
            attemptsBo.setAttempts(1);
            attemptsBo.setUserEmail(userEmailId);
            attemptsBo.setLastModified(FdahpStudyDesignerUtil.getCurrentDateTime());
            session.update(attemptsBo);
            isAcountLocked = false;
          } else if (attemptsBo.getAttempts() < (MAX_ATTEMPTS + 1)) {
            attemptsBo.setAttempts(attemptsBo.getAttempts() + 1);
            attemptsBo.setUserEmail(userEmailId);
            attemptsBo.setLastModified(FdahpStudyDesignerUtil.getCurrentDateTime());
            session.update(attemptsBo);
          }
        }
      }
      SQLQuery query =
          session.createSQLQuery("select * from users UBO where BINARY UBO.email = :userEmailId");
      query.addEntity(UserBO.class).setParameter("userEmailId", userEmailId);
      userBO = (UserBO) query.uniqueResult();
      if (userBO != null) {
        if (isAcountLocked) {
          SessionObject sessionObject = new SessionObject();
          sessionObject.setUserId(userBO.getUserId());

          Map<String, String> values = new HashMap<>();
          values.put(LOCK_TIME, String.valueOf(USER_LOCK_DURATION));
          values.put(FAILED_ATTEMPT, String.valueOf(MAX_ATTEMPTS));
          auditRequest.setUserId(userBO.getUserId().toString());
          auditRequest.setUserAccessLevel(userBO.getAccessLevel());
          auditRequest.setSource(ACCOUNT_LOCKED.getSource().getValue());
          auditRequest.setDestination(ACCOUNT_LOCKED.getDestination().getValue());
          auditLogEventHelper.logEvent(ACCOUNT_LOCKED, auditRequest, values);
        } else {
          SessionObject sessionObject = new SessionObject();
          sessionObject.setUserId(userBO.getUserId());
        }
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - updateUser() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    if (isAcountLocked) {
      loginService.sendLockedAccountPasswordResetLinkToMail(userEmailId, auditRequest);
      throw new LockedException(propMap.get("account.lock.msg"));
    }
    logger.exit("updateUser() - Ends");
  }

  @SuppressWarnings("unchecked")
  @Override
  public String updatePasswordHistory(String userId, String userPassword) {
    logger.info("LoginDAOImpl - updatePasswordHistory() - Starts");

    List<UserPasswordHistory> passwordHistories = null;
    UserPasswordHistory savePasswordHistory = null;
    String result = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    Integer passwordHistoryCount = Integer.parseInt(propMap.get("password.history.count"));
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (StringUtils.isNotEmpty(userId)) {
        passwordHistories =
            session.getNamedQuery("getPaswordHistoryByUserId").setString("userId", userId).list();
        if ((passwordHistories != null)
            && (passwordHistories.size() > (passwordHistoryCount - 1))) {
          for (int i = 0; i < ((passwordHistories.size() - passwordHistoryCount) + 1); i++) {
            session.delete(passwordHistories.get(i));
          }
        }
        savePasswordHistory = new UserPasswordHistory();
        savePasswordHistory.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
        savePasswordHistory.setUserId(userId);
        savePasswordHistory.setUserPassword(userPassword);
        session.save(savePasswordHistory);
        result = FdahpStudyDesignerConstants.SUCCESS;
      }
      transaction.commit();
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - updatePasswordHistory() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("updatePasswordHistory() - Ends");
    return result;
  }

  @Override
  public String updateUser(UserBO userBO) {
    logger.entry("begin updateUser()");
    Session session = null;
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(userBO);
      transaction.commit();
      this.resetFailAttempts(userBO.getUserEmail());
      result = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - updateUser() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("updateUser() - Ends");
    return result;
  }

  @Override
  // Reset the user details as part of account locking flow
  public String updateUserForResetPassword(UserBO userBO) {
    logger.entry("begin updateUserForResetPassword()");
    Session session = null;
    String result = FdahpStudyDesignerConstants.FAILURE;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(userBO);
      transaction.commit();
      result = FdahpStudyDesignerConstants.SUCCESS;
    } catch (Exception e) {
      if (transaction != null) {
        transaction.rollback();
      }
      logger.error("LoginDAOImpl - updateUserForResetPassword() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.exit("updateUserForResetPassword() - Ends");
    return result;
  }
}
