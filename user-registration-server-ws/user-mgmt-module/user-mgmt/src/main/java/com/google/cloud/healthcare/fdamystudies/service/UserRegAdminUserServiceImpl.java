/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRegAdminUserServiceImpl implements UserRegAdminUserService {

  private static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserServiceImpl.class);
  @Autowired private UserRegAdminUserDao adminUserDao;

  @Override
  public UserRegAdminEntity save(UserRegAdminEntity adminUser) throws SystemException {

    logger.info("UserRegAdminUserServiceImpl save() - starts");
    UserRegAdminEntity userRegAdminUser = null;
    if (adminUser != null) {
      userRegAdminUser = adminUserDao.save(adminUser);
    }
    logger.info("UserRegAdminUserServiceImpl save() - ends");
    return userRegAdminUser;
  }
}
