/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsBORepository;

/**
 * Project Name: MyStudies-UserReg-WS
 *
 * @author Chiranjibi Dash, Date: Dec 18, 2019, Time: 5:40:27 PM
 */
@Repository
public class FdaEaUserDetailsDaoImpl implements FdaEaUserDetailsDao {

  @Autowired private UserDetailsBORepository repository;

  //    @Autowired private EntityManagerFactory entityManagerFactory;

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsDaoImpl.class);

  @Override
  @Transactional
  public UserDetails loadUserDetailsByUserId(String userId) throws SystemException {

    try {
      logger.info("(DAO)....FdaEaUserDetailsDaoImpl.loadUserDetailsByUserId()....Started");
      UserDetails userDetails = null;
      // get the user details using the userId
      if (userId != null) {
        userDetails = repository.findByUserId(userId);
      }
      logger.info("(DAO)....FdaEaUserDetailsDaoImpl.loadUserDetailsByUserId()....Ended");
      return userDetails;
    } catch (Exception e) {
      logger.error("(DAO)....FdaEaUserDetailsDaoImpl.loadUserDetailsByUserId()....Ended");
      throw new SystemException();
    }
  }

  @Override
  public UserDetails saveUser(UserDetails userDetails) throws SystemException {

    try {
      logger.info("(DAO)....FdaEaUserDetailsDaoImpl.saveUser()....Started");
      // save user here, using repository
      UserDetails savedUserDetails = null;
      if (userDetails != null) {
        savedUserDetails = repository.save(userDetails);
      }
      logger.info("(DAO)....FdaEaUserDetailsDaoImpl.saveUser()....Ended");
      return savedUserDetails;
    } catch (Exception e) {
      logger.error("(DAO)....FdaEaUserDetailsDaoImpl.saveUser()....Ended");
      throw new SystemException();
    }
  }

  @Override
  public UserDetails loadEmailCodeByUserId(String userId) throws SystemException {
    try {
      logger.info("(DAO)....FdaEaUserDetailsDaoImpl.loadEmailCodeByUserId()....Started");
      UserDetails dbResponse = null;
      if (userId != null) {
        dbResponse = repository.findByUserId(userId);
        return dbResponse;
      } else {
        logger.info("(DAO)....FdaEaUserDetailsDaoImpl.loadEmailCodeByUserId()....Ended");
        return dbResponse;
      }
    } catch (Exception e) {
      logger.error("(DAO)....FdaEaUserDetailsDaoImpl.loadEmailCodeByUserId()....Ended");
      throw new SystemException();
    }
  }
}
