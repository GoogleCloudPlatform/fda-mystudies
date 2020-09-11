/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsBORepository;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FdaEaUserDetailsDaoImpl implements FdaEaUserDetailsDao {

  @Autowired private UserDetailsBORepository repository;

  @Autowired private EntityManagerFactory entityManagerFactory;

  @PersistenceContext private EntityManager entityManager;

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsDaoImpl.class);

  @Override
  @Transactional
  public UserDetailsBO loadUserDetailsByUserId(String userId) {
    logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - starts");

    UserDetailsBO userDetailsBO = null;
    if (userId != null) {
      userDetailsBO = repository.findByUserId(userId);
    }
    logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - ends");
    return userDetailsBO;
  }

  @Override
  public UserDetailsBO saveUser(UserDetailsBO userDetailsBO) {
    return repository.save(userDetailsBO);
  }

  @Override
  public UserDetailsBO loadEmailCodeByUserId(String userId) {
    logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() - starts");

    UserDetailsBO dbResponse = null;
    if (userId != null) {
      dbResponse = repository.findByUserId(userId);
      logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
      return dbResponse;
    } else {
      logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
      return dbResponse;
    }
  }

  @Override
  @Transactional
  public boolean updateStatus(UserDetailsBO participantDetails) {

    logger.info("FdaEaUserDetailsDaoImpl updateStatus() - starts");
    if (participantDetails == null) {
      throw new IllegalArgumentException();
    }
    entityManager.merge(participantDetails);
    return true;
  }

  @Override
  public boolean saveAllRecords(
      UserDetailsBO userDetailsBO, AuthInfoBO authInfo, UserAppDetailsBO userAppDetails) {

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
        throw e;
      }
    } else {
      logger.info("FdaEaUserDetailsDaoImpl saveAllRecords() - ends");
      return false;
    }
  }
}
