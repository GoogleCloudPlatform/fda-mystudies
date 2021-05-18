/*
 * Copyright 2020-2021 Google LLC
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FdaEaUserDetailsDaoImpl implements FdaEaUserDetailsDao {

  @Autowired private UserDetailsRepository repository;

  @Autowired private SessionFactory sessionFactory;

  private static final XLogger logger =
      XLoggerFactory.getXLogger(FdaEaUserDetailsDaoImpl.class.getName());

  @Override
  @Transactional
  public UserDetailsEntity loadUserDetailsByUserId(String userId) {
    logger.entry("Begin loadUserDetailsByUserId()");

    UserDetailsEntity userDetails = null;
    if (userId != null) {
      Optional<UserDetailsEntity> optUserDetails = repository.findByUserId(userId);
      if (optUserDetails.isPresent()) {
        userDetails = optUserDetails.get();
      }
    }
    logger.exit("loadUserDetailsByUserId() - ends");
    return userDetails;
  }

  @Override
  public UserDetailsEntity saveUser(UserDetailsEntity userDetails) {
    return repository.save(userDetails);
  }

  @Override
  public UserDetailsEntity loadEmailCodeByUserId(String userId) {
    logger.entry("Begin loadEmailCodeByUserId()");

    UserDetailsEntity dbResponse = null;
    if (userId != null) {
      Optional<UserDetailsEntity> optUserDetails = repository.findByUserId(userId);
      if (optUserDetails.isPresent()) {
        dbResponse = optUserDetails.get();
      }
      logger.exit("FdaEaUserDetailsDaoImpl loadEmailCodeByUserId() -ends");
      return dbResponse;
    } else {
      logger.exit("loadEmailCodeByUserId() -ends");
      return dbResponse;
    }
  }

  @Override
  @Transactional
  public boolean updateStatus(UserDetailsEntity participantDetails) {

    logger.entry("Begin updateStatus()");
    Session session = this.sessionFactory.getCurrentSession();

    if (participantDetails == null) {
      throw new IllegalArgumentException();
    }
    session.merge(participantDetails);
    return true;
  }

  @Override
  public boolean saveAllRecords(
      UserDetailsEntity userDetails, AuthInfoEntity authInfo, UserAppDetailsEntity userAppDetails) {

    logger.entry("Begin saveAllRecords()");
    if (userDetails != null && authInfo != null && userAppDetails != null) {
      Session session = this.sessionFactory.getCurrentSession();

      String userDetailsId = (String) session.save(userDetails);

      Optional<UserDetailsEntity> optUserDetail = repository.findById(userDetailsId);

      if (optUserDetail.isPresent()) {
        authInfo.setUserDetails(optUserDetail.get());
        userAppDetails.setUserDetails(optUserDetail.get());
      }
      session.save(authInfo);
      session.save(userAppDetails);
      return true;

    } else {
      logger.exit("saveAllRecords() - ends");
      return false;
    }
  }
}
