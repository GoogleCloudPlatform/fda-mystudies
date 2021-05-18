/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.UserAppDetailsDao;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAppDetailsServiceImpl implements UserAppDetailsService {

  private XLogger logger = XLoggerFactory.getXLogger(UserAppDetailsServiceImpl.class.getName());
  @Autowired private UserAppDetailsDao userAppDetailsDao;

  @Override
  @Transactional()
  public UserAppDetailsEntity save(UserAppDetailsEntity userAppDetails) {
    logger.entry("Begin save()");

    UserAppDetailsEntity dbResponse = null;

    if (userAppDetails != null) {
      dbResponse = userAppDetailsDao.save(userAppDetails);
      logger.exit("save() - ends");
      return dbResponse;
    } else {
      logger.exit("save() - ends");
      return dbResponse;
    }
  }
}
