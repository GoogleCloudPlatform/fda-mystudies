/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import javax.persistence.EntityManagerFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfo;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsBORepository;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;

@Repository
public class FdaEaUserDetailsDaoImpl implements FdaEaUserDetailsDao {

  @Autowired private UserDetailsBORepository repository;

  @Autowired private UserManagementUtil userManagementUtil;

  @Autowired private EntityManagerFactory entityManagerFactory;

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsDaoImpl.class);

  @Override
  @Transactional
  public UserDetailsBO loadUserDetailsByUserId(String userId) throws SystemException {
    logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - starts");
    try {
      UserDetailsBO userDetailsBO = null;
      if (userId != null) {
        userDetailsBO = repository.findByUserId(userId);
      }
      logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - ends");
      return userDetailsBO;
    } catch (Exception e) {
      logger.error("FdaEaUserDetailsDaoImpl.loadUserDetailsByUserId(): ", e);
      throw new SystemException();
    }
  }

  @Override
  public UserDetailsBO saveUser(UserDetailsBO userDetailsBO) throws SystemException {
    logger.info("FdaEaUserDetailsDaoImpl saveUser() - starts");
    try {
      UserDetailsBO savedUserDetails = null;
      if (userDetailsBO != null) {
        savedUserDetails = repository.save(userDetailsBO);
      }
      logger.info("FdaEaUserDetailsDaoImpl saveUser() - ends");
      return savedUserDetails;
    } catch (Exception e) {
      logger.error("FdaEaUserDetailsDaoImpl.saveUser(): ", e);
      throw new SystemException();
    }
  }

  @Override
  public UserDetailsBO loadEmailCodeByUserId(String userId) throws SystemException {
    logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() - starts");
    try {
      UserDetailsBO dbResponse = null;
      if (userId != null) {
        dbResponse = repository.findByUserId(userId);
        logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
        return dbResponse;
      } else {
        logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
        return dbResponse;
      }
    } catch (Exception e) {
      logger.error("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId(): ", e);
      throw new SystemException();
    }
  }

  @Override
  public boolean updateStatus(UserDetailsBO participantDetails)
      throws InvalidRequestException, SystemException {

    logger.info("FdaEaUserDetailsDaoImpl updateStatus() - starts");
    if (participantDetails != null) {
      Transaction transaction = null;
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        transaction = session.beginTransaction();

        participantDetails.setEmailCode(null);
        participantDetails.setCodeExpireDate(null);
        participantDetails.setStatus(1); // status 1--->> user's emailId verified
        session.update(participantDetails);

        UpdateAccountInfo accountStatus = new UpdateAccountInfo();
        accountStatus.setEmailVerified(true);
        userManagementUtil.updateUserInfoInAuthServer(
            accountStatus, participantDetails.getUserId());

        transaction.commit();
        return true;
      } catch (Exception e) {
        logger.error("FdaEaUserDetailsDaoImpl updateStatus(): ", e);
        if (transaction != null) {
          try {
            transaction.rollback();
          } catch (Exception e1) {
            logger.error("FdaEaUserDetailsDaoImpl updateStatus(): ", e);
          }
        }
        if (e instanceof InvalidRequestException) {
          throw new InvalidRequestException();
        } else {
          throw new SystemException();
        }
      }
    } else {
      logger.info("FdaEaUserDetailsDaoImpl updateStatus() - ends");
      return false;
    }
  }

  @Override
  public boolean saveAllRecords(
      UserDetailsBO userDetailsBO, AuthInfoBO authInfo, UserAppDetailsBO userAppDetails)
      throws SystemException {

    logger.info("FdaEaUserDetailsDaoImpl saveAllRecords() - starts");
    if (userDetailsBO != null && authInfo != null && userAppDetails != null) {
      Transaction transaction = null;
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        transaction = session.beginTransaction();

        Integer userDetailsId = (Integer) session.save(userDetailsBO);
        authInfo.setUserId(userDetailsId);
        session.save(authInfo);
        userAppDetails.setUserDetailsId(userDetailsId);
        session.save(userAppDetails);

        transaction.commit();
        return true;
      } catch (Exception e) {
        logger.error("FdaEaUserDetailsDaoImpl saveAllRecords(): ", e);
        if (transaction != null) {
          try {
            transaction.rollback();
          } catch (Exception e1) {
            logger.error("FdaEaUserDetailsDaoImpl saveAllRecords(): ", e);
          }
        }
        throw new SystemException();
      }
    } else {
      logger.info("FdaEaUserDetailsDaoImpl saveAllRecords() - ends");
      return false;
    }
  }
}
