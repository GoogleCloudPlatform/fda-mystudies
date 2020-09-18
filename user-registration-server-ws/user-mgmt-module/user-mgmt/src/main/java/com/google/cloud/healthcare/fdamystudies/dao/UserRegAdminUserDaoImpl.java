/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserDaoImpl.class);

  @Autowired private UserRegAdminRepository adminUserRepository;

  @Override
  public UserRegAdminEntity save(UserRegAdminEntity adminUser) throws SystemException {
    logger.info("UserRegAdminUserDaoImpl save() - starts");
    UserRegAdminEntity userRegAdminUser = null;
    if (adminUser != null) {
      try {
        userRegAdminUser = adminUserRepository.save(adminUser);
        logger.info("UserRegAdminUserDaoImpl save() - ends");
        return userRegAdminUser;
      } catch (Exception e) {
        logger.error("UserRegAdminUserDaoImpl save(): ", e);
        throw new SystemException();
      }
    }
    return userRegAdminUser;
  }
}
