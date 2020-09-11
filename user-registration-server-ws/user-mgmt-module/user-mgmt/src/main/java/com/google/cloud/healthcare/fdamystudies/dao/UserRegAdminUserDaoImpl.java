/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminUserRepository;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserRegAdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserDaoImpl.class);

  @Autowired private UserRegAdminUserRepository adminUserRepository;

  @Override
  public UserRegAdminUser save(UserRegAdminUser adminUser) {
    logger.info("UserRegAdminUserDaoImpl save() - starts");
    if (adminUser != null) {
      UserRegAdminUser userRegAdminUser = adminUserRepository.save(adminUser);
      logger.info("UserRegAdminUserDaoImpl save() - ends");
      return userRegAdminUser;
    }
    return null;
  }
}
