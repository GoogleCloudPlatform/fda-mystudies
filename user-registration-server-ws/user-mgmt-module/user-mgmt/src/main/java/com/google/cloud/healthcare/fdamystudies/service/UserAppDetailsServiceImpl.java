/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.UserAppDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAppDetailsServiceImpl implements UserAppDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(UserAppDetailsServiceImpl.class);
  @Autowired private UserAppDetailsDao userAppDetailsDao;

  @Override
  @Transactional()
  public UserAppDetailsEntity save(UserAppDetailsEntity userAppDetails) throws SystemException {

    logger.info("UserAppDetailsServiceImpl save() - starts");

    UserAppDetailsEntity dbResponse = null;

    if (userAppDetails != null) {
      dbResponse = userAppDetailsDao.save(userAppDetails);
      logger.info("UserAppDetailsServiceImpl save() - ends");
      return dbResponse;
    } else {
      logger.info("UserAppDetailsServiceImpl save() - ends");
      return dbResponse;
    }
  }
}
