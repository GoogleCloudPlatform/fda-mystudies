/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRegAdminUserDaoImpl implements UserRegAdminUserDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(UserRegAdminUserDaoImpl.class.getName());

  @Autowired private UserRegAdminRepository adminUserRepository;

  @Override
  public UserRegAdminEntity save(UserRegAdminEntity adminUser) {
    logger.entry("Begin save()");
    if (adminUser != null) {
      UserRegAdminEntity userRegAdminUser = adminUserRepository.save(adminUser);
      logger.exit("save() - ends");
      return userRegAdminUser;
    }
    return null;
  }
}
