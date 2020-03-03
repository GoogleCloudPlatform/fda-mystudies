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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminUserRepository;

/**
 * user-management-service-bundle
 *
 * @author Chiranjibi Dash
 */
@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserDaoImpl.class);
  @Autowired private UserRegAdminUserRepository adminUserRepository;

  @Override
  public UserRegAdminUser save(UserRegAdminUser adminUser) throws SystemException {
    logger.info("(Dao)...UserRegAdminUserDaoImpl.save()...Started");
    UserRegAdminUser userRegAdminUser = null;
    if (adminUser != null) {
      try {
        userRegAdminUser = adminUserRepository.save(adminUser);
        logger.info("(Dao)...UserRegAdminUserDaoImpl.save()...Ended: ");
        return userRegAdminUser;
      } catch (Exception e) {
        logger.error("(Dao)...UserRegAdminUserDaoImpl.save()...Ended: ", e);
        throw new SystemException();
      }
    }
    return userRegAdminUser;
  }
}
