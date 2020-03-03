/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  public static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserDaoImpl.class);

  @Autowired private EntityManagerFactory entityManagerFactory;

  @Autowired private SiteDao siteDao;

  @Override
  public String saveDetails(RegisterUser user, String userId) throws SystemException {
    logger.info("(Service)...UserRegAdminUserDaoImpl.save()...Started");
    String message = "FAILURE";
    if (user != null) {
      Transaction transaction = null;
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        transaction = session.beginTransaction();

        UserRegAdminUser admin = BeanUtil.getBean(UserRegAdminUser.class);
        admin.setEmail(user.getEmail());
        admin.setFirstName(user.getFirstName());
        admin.setLastName(user.getLastName());
        admin.setCreatedBy(Integer.valueOf(userId));
        admin.setCreated(LocalDateTime.now(ZoneId.systemDefault()));
        if (user.getSuperAdmin() != null && Boolean.TRUE.equals(user.getSuperAdmin())) {
          admin.setSuperAdmin(true);
        }

        admin.setManageLocations(user.getManageLocations());
        Integer id = (Integer) session.save(admin);

        logger.info("(Service)...UserRegAdminUserDaoImpl.save()...Ended");
        return "SUCCESS";
      } catch (Exception e) {
        logger.info("(Service)...UserRegAdminUserDaoImpl.save()...Ended ", e);
        if (transaction != null) {
          transaction.rollback();
        }
        throw new SystemException();
      }
    } else {
      logger.info("(Service)...UserRegAdminUserDaoImpl.save()...Ended with null");
      return message;
    }
  }

  /*@Override
  @SuppressWarnings("unchecked")
  public List<UserRegAdminUser> checkPermission(Integer adminId) throws SystemException {

    logger.info("(Service)...UserRegAdminUserDaoImpl.checkPermission()...Started");
    List<UserRegAdminUser> userRegAdminUserList = null;
    if (adminId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<UserRegAdminUser> query = session.createQuery("from UserRegAdminUser where id = :id");
        query.setParameter("id", adminId);
        userRegAdminUserList = query.getResultList();

        logger.info(
            "(DAO)...UserRegAdminUserDaoImpl.checkPermission()...Ended " + userRegAdminUserList);
        return userRegAdminUserList;
      } catch (Exception e) {
        logger.error("(DAO)...StudyInfoDaoImpl - checkPermission: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info(
          "(DAO)...UserRegAdminUserDaoImpl.checkPermission()...Ended " + userRegAdminUserList);
      return null;
    }
  }*/

  @Override
  @SuppressWarnings("unchecked")
  public UserRegAdminUser checkPermission(Integer adminId) throws SystemException {

    logger.info("UserRegAdminUserDaoImpl - checkPermission() : starts");
    List<UserRegAdminUser> userRegAdminUserList = null;
    UserRegAdminUser userRegAdminUser = null;
    if (adminId != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query<UserRegAdminUser> query = session.createQuery("from UserRegAdminUser where id = :id");
        query.setParameter("id", adminId);
        userRegAdminUserList = query.getResultList();

        if (userRegAdminUserList != null && !userRegAdminUserList.isEmpty()) {
          userRegAdminUser = userRegAdminUserList.get(0);
        }
        logger.info("UserRegAdminUserDaoImpl - checkPermission() : ends");
        return userRegAdminUser;
      } catch (Exception e) {
        logger.error("UserRegAdminUserDaoImpl - checkPermission() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("UserRegAdminUserDaoImpl - checkPermission() : ends");
      return userRegAdminUser;
    }
  }

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public Boolean checkDuplicateEntryUsingEmailId(String email) throws SystemException {
    logger.info("(DAO)...UserRegAdminUserDaoImpl.checkDuplicateEntryUsingEmailId()...Started");

    Boolean result = false;
    Long count = null;
    if (email != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query query =
            session.createQuery("select count(*) from UserRegAdminUser where email = :emailId");
        query.setParameter("emailId", email);
        count = (Long) query.uniqueResult();

        if (count != null && count > 0) {
          result = true;
        }
        return result;
      } catch (Exception e) {
        logger.error("(DAO)...StudyInfoDaoImpl - checkPermission: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(DAO)...UserRegAdminUserDaoImpl.checkPermission()...Ended false");
      return false;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<UserRegAdminUser> getAllRecords() throws SystemException {
    logger.info("(DAO)...UserRegAdminUserDaoImpl.getAllRecords()...Started");
    List<UserRegAdminUser> userRegAdminUserList = null;
    try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
      Query<UserRegAdminUser> query = session.createQuery("from UserRegAdminUser");
      userRegAdminUserList = query.getResultList();

      logger.info(
          "(DAO)...UserRegAdminUserDaoImpl.getAllRecords()...Ended " + userRegAdminUserList);
      return userRegAdminUserList;
    } catch (Exception e) {
      logger.error("(DAO)...StudyInfoDaoImpl - getAllRecords: (ERROR) ", e);
      throw new SystemException();
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Boolean checkEmailIdExists(String email) throws SystemException {
    logger.info("UserRegAdminUserDaoImpl - checkEmailIdExists() : starts");

    Boolean result = false;
    Long count = null;
    if (email != null) {
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        Query query =
            session.createQuery("select count(*) from UserRegAdminUser where email = :emailId");
        query.setParameter("emailId", email);

        count = (Long) query.uniqueResult();
        if (count != null && count == 1) {
          return true;
        } else return result;

      } catch (Exception e) {
        logger.error("UserRegAdminUserDaoImpl - checkEmailIdExists() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("UserRegAdminUserDaoImpl - checkEmailIdExists() : ends with false");
      return result;
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public String updateUser(SetUpAccountRequest request, String authUserId) throws SystemException {
    logger.info("UserRegAdminUserDaoImpl - updateUser() : starts");
    List<UserRegAdminUser> userRegAdminUserList = null;
    Transaction transaction = null;
    String message = "FAILURE";

    if (request != null) {

      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        transaction = session.beginTransaction();
        Query<UserRegAdminUser> query =
            session.createQuery("from UserRegAdminUser where email = :email");
        query.setParameter("email", request.getEmail());
        userRegAdminUserList = query.getResultList();

        if (userRegAdminUserList != null && !userRegAdminUserList.isEmpty()) {
          UserRegAdminUser userRegAdminUser = userRegAdminUserList.get(0);
          userRegAdminUser.setUrAdminAuthId(authUserId);
          userRegAdminUser.setFirstName(request.getFirstName());
          userRegAdminUser.setLastName(request.getLastName());
          session.update(userRegAdminUser);
          transaction.commit();
          session.close();
          message = "SUCCESS";
        }

        logger.info("UserRegAdminUserDaoImpl - updateUser() : ends");
        return message;

      } catch (Exception e) {
        logger.error("UserRegAdminUserDaoImpl - updateUser() : error ", e);
        throw new SystemException();
      }

    } else {
      logger.info("UserRegAdminUserDaoImpl - updateUser() : ends");
      return message;
    }
  }
}
