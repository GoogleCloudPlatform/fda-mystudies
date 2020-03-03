/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;

/**
 * user-management-service-bundle
 *
 * @author Chiranjibi Dash
 */
@Service
public class UserRegAdminUserServiceImpl implements UserRegAdminUserService {

  private static final Logger logger = LoggerFactory.getLogger(UserRegAdminUserServiceImpl.class);
  @Autowired private UserRegAdminUserDao adminUserDao;

  @Override
  public UserRegAdminUser save(UserRegAdminUser adminUser) throws SystemException {

    logger.info("(S)...UserRegAdminUserServiceImpl.save()...Started");
    UserRegAdminUser userRegAdminUser = null;
    if (adminUser != null) {
      userRegAdminUser = adminUserDao.save(adminUser);
      logger.info("(S)...UserRegAdminUserServiceImpl.save()...Ended: ");
      return userRegAdminUser;
    }
    return userRegAdminUser;
  }
}
