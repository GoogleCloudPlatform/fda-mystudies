/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
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

import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;
import com.fdahpstudydesigner.service.LoginService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Repository;

@Repository
public class LoginDAOImpl implements LoginDAO {

  private static Logger logger = Logger.getLogger(LoginDAOImpl.class.getName());

  @Autowired private AuditLogDAO auditLogDAO;

  @Autowired private LoginService loginService;

  HibernateTemplate hibernateTemplate;
  private Query query = null;
  private Transaction transaction = null;

  public LoginDAOImpl() {}

  @Override
  public String changePassword(Integer userId, String newPassword, String oldPassword) {
    logger.info("LoginDAOImpl - changePassword() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    UserBO adminUserBO = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String encrNewPass = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      query = session.getNamedQuery("getUserById").setInteger("userId", userId);
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
        adminUserBO.setPasswordExpairdedDateTime(
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
    logger.info("LoginDAOImpl - changePassword() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<UserPasswordHistory> getPasswordHistory(Integer userId) {
    logger.info("LoginDAOImpl - updatePasswordHistory() - Starts");
    List<UserPasswordHistory> passwordHistories = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((userId != null) && (userId != 0)) {
        passwordHistories =
            session.getNamedQuery("getPaswordHistoryByUserId").setInteger("userId", userId).list();
      }

    } catch (Exception e) {
      logger.error("LoginDAOImpl - updatePasswordHistory() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("LoginDAOImpl - updatePasswordHistory() - Ends");
    return passwordHistories;
  }

  @Override
  public UserAttemptsBo getUserAttempts(String userEmailId) {
    logger.info("LoginDAOImpl - getUserAttempts() - Starts");
    Session session = null;
    UserAttemptsBo attemptsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      SQLQuery query =
          session.createSQLQuery(
              "select * from user_attempts where BINARY email_id='" + userEmailId + "'");
      query.addEntity(UserAttemptsBo.class);
      attemptsBo = (UserAttemptsBo) query.uniqueResult();
    } catch (Exception e) {
      logger.error("LoginDAOImpl - getUserAttempts() - ERROR ", e);
    } finally {
      if ((session != null) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("LoginDAOImpl - getUserAttempts() - Ends");
    return attemptsBo;
  }

  @Override
  public UserBO getUserBySecurityToken(String securityToken) {
    logger.info("LoginDAOImpl - getUserBySecurityToken() - Starts");
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
    logger.info("LoginDAOImpl - getUserBySecurityToken() - Ends");
    return userBO;
  }

  @Override
  public UserBO getValidUserByEmail(String email) {
    logger.info("LoginDAOImpl - getValidUserByEmail() - Starts");
    UserBO userBo = null;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      SQLQuery query =
          session.createSQLQuery(
              "select * from users UBO where BINARY lower(UBO.email) = '"
                  + email.toLowerCase()
                  + "'");
      query.addEntity(UserBO.class);
      userBo = (UserBO) query.uniqueResult();
      if (userBo != null) {
        userBo.setUserLastLoginDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
        if (userBo.getRoleId() != null) {
          String role =
              (String)
                  session
                      .createSQLQuery(
                          "select role_name from roles where role_id=" + userBo.getRoleId())
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
    logger.info("LoginDAOImpl - getValidUserByEmail() - Ends");
    return userBo;
  }

  @Override
  public Boolean isFrocelyLogOutUser(Integer userId) {
    logger.info("LoginDAOImpl - isFrocelyLogOutUser() - Starts");
    UserBO userBo = null;
    boolean result = false;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((userId != null) && (userId != 0)) {
        userBo =
            (UserBO)
                session.getNamedQuery("getUserById").setInteger("userId", userId).uniqueResult();
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
    logger.info("LoginDAOImpl - isFrocelyLogOutUser() - Ends");
    return result;
  }

  @Override
  public Boolean isUserEnabled(Integer userId) {
    logger.info("LoginDAOImpl - isUserExists() - Starts");
    UserBO userBo = null;
    boolean result = false;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((userId != null) && (userId != 0)) {
        userBo =
            (UserBO)
                session.getNamedQuery("getUserById").setInteger("userId", userId).uniqueResult();
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
    logger.info("LoginDAOImpl - isUserExists() - Ends");
    return result;
  }

  private boolean isUserExists(String userEmail) {
    logger.info("LoginDAOImpl - isUserExists() - Starts");
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
    logger.info("LoginDAOImpl - isUserExists() - Ends");
    return result;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void passwordLoginBlocked() {
    logger.info("LoginDAOImpl - passwordLoginBlocked() - Starts");
    Session session = null;
    List<Integer> userBOList = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    int lastLoginExpirationInDay =
        Integer.parseInt("-" + propMap.get("lastlogin.expiration.in.day"));
    String lastLoginDateTime;
    StringBuilder sb = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      lastLoginDateTime =
          new SimpleDateFormat(FdahpStudyDesignerConstants.DB_SDF_DATE_TIME)
              .format(
                  FdahpStudyDesignerUtil.addDaysToDate(new Date(), lastLoginExpirationInDay + 1));
      sb = new StringBuilder();
      sb.append(
              "SELECT u.user_id FROM users u,roles r WHERE r.role_id = u.role_id and u.user_id not in ")
          .append("(select upm.user_id from user_permission_mapping upm where upm.permission_id = ")
          .append(
              "(select up.permission_id from user_permissions up where up.permissions ='ROLE_SUPERADMIN')) ")
          .append("AND u.user_login_datetime < '")
          .append(lastLoginDateTime)
          .append("' AND u.status=1");
      query = session.createSQLQuery(sb.toString());
      userBOList = query.list();
      if ((userBOList != null) && !userBOList.isEmpty()) {
        session
            .createSQLQuery(
                "Update users set status = 0 WHERE user_id in("
                    + StringUtils.join(userBOList, ",")
                    + ")")
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
    logger.info("LoginDAOImpl - passwordLoginBlocked() - Ends");
  }

  @Override
  public void resetFailAttempts(String userEmailId) {
    logger.info("LoginDAOImpl - resetFailAttempts() - Starts");
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
    logger.info("LoginDAOImpl - resetFailAttempts() - Ends");
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  @Override
  public void updateFailAttempts(String userEmailId) {
    logger.info("LoginDAOImpl - updateUser() - Starts");
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
          session.createSQLQuery(
              "select * from users UBO where BINARY UBO.email = '" + userEmailId + "'");
      query.addEntity(UserBO.class);
      userBO = (UserBO) query.uniqueResult();
      if (userBO != null) {
        if (isAcountLocked) {
          SessionObject sessionObject = new SessionObject();
          sessionObject.setUserId(userBO.getUserId());
          String activityDetails =
              FdahpStudyDesignerConstants.USER_LOCKED_ACTIVITY_DEATILS_MESSAGE.replace(
                  "&name", userEmailId);
          auditLogDAO.saveToAuditLog(
              session,
              transaction,
              sessionObject,
              FdahpStudyDesignerConstants.USER_LOCKED_ACTIVITY_MESSAGE,
              activityDetails,
              "LoginDAOImpl - updateUser()");
        } else {
          SessionObject sessionObject = new SessionObject();
          sessionObject.setUserId(userBO.getUserId());
          auditLogDAO.saveToAuditLog(
              session,
              transaction,
              sessionObject,
              FdahpStudyDesignerConstants.PASS_FAIL_ACTIVITY_MESSAGE,
              FdahpStudyDesignerConstants.PASS_FAIL_ACTIVITY_DEATILS_MESSAGE,
              "LoginDAOImpl - updateUser()");
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
      loginService.sendLockedAccountPasswordResetLinkToMail(userEmailId);
      throw new LockedException(propMap.get("account.lock.msg"));
    }
    logger.info("LoginDAOImpl - updateUser() - Ends");
  }

  @SuppressWarnings("unchecked")
  @Override
  public String updatePasswordHistory(Integer userId, String userPassword) {
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
      if ((userId != null) && (userId != 0)) {
        passwordHistories =
            session.getNamedQuery("getPaswordHistoryByUserId").setInteger("userId", userId).list();
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
    logger.info("LoginDAOImpl - updatePasswordHistory() - Ends");
    return result;
  }

  @Override
  public String updateUser(UserBO userBO) {
    logger.info("LoginDAOImpl - updateUser() - Starts");
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
    logger.info("LoginDAOImpl - updateUser() - Ends");
    return result;
  }

  @Override
  // Reset the user details as part of account locking flow
  public String updateUserForResetPassword(UserBO userBO) {
    logger.info("LoginDAOImpl - updateUserForResetPassword() - Starts");
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
    logger.info("LoginDAOImpl - updateUserForResetPassword() - Ends");
    return result;
  }
}
