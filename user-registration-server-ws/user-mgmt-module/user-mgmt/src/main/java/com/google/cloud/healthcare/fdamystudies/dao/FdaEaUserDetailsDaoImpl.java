/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import java.util.Optional;
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

  @Autowired private UserDetailsRepository repository;

  @Autowired private EntityManagerFactory entityManagerFactory;

  @PersistenceContext private EntityManager entityManager;

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsDaoImpl.class);

  @Override
  @Transactional
  public UserDetailsEntity loadUserDetailsByUserId(String userId) {
    logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - starts");

    UserDetailsEntity userDetails = null;
    if (userId != null) {
      Optional<UserDetailsEntity> optUserDetails = repository.findByUserId(userId);
      if (optUserDetails.isPresent()) {
        userDetails = optUserDetails.get();
      }
    }
    logger.info("FdaEaUserDetailsDaoImpl loadUserDetailsByUserId() - ends");
    return userDetails;
  }

  @Override
  public UserDetailsEntity saveUser(UserDetailsEntity userDetails) {
    return repository.save(userDetails);
  }

  @Override
  public UserDetailsEntity loadEmailCodeByUserId(String userId) {
    logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() - starts");

    UserDetailsEntity dbResponse = null;
    if (userId != null) {
      Optional<UserDetailsEntity> optUserDetails = repository.findByUserId(userId);
      if (optUserDetails.isPresent()) {
        dbResponse = optUserDetails.get();
      }
      logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
      return dbResponse;
    } else {
      logger.info("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
      return dbResponse;
    }
  }

  @Override
  @Transactional
  public boolean updateStatus(UserDetailsEntity participantDetails) {

    logger.info("FdaEaUserDetailsDaoImpl updateStatus() - starts");
    if (participantDetails == null) {
      throw new IllegalArgumentException();
    }
    entityManager.merge(participantDetails);
    return true;
  }

  @Override
  public boolean saveAllRecords(
      UserDetailsEntity userDetails, AuthInfoEntity authInfo, UserAppDetailsEntity userAppDetails) {

    logger.info("FdaEaUserDetailsDaoImpl saveAllRecords() - starts");
    if (userDetails != null && authInfo != null && userAppDetails != null) {
      Transaction transaction = null;
      try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
        transaction = session.beginTransaction();

        String userDetailsId = (String) session.save(userDetails);

        Optional<UserDetailsEntity> optUserDetail = repository.findById(userDetailsId);
        if (optUserDetail.isPresent()) {
          authInfo.setUserDetails(optUserDetail.get());
          userAppDetails.setUserDetails(optUserDetail.get());
        }
        session.save(authInfo);
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
