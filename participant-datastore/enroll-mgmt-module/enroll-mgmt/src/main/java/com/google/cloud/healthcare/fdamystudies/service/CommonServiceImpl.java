/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired CommonDao commonDao;

  private static final XLogger logger =
      XLoggerFactory.getXLogger(CommonServiceImpl.class.getName());

  @Override
  @Transactional(readOnly = true)
  public UserDetailsEntity getUserInfoDetails(String userId) {
    logger.entry("Begin getUserInfoDetails()");
    UserDetailsEntity userDetailsEntity = null;

    userDetailsEntity = commonDao.getUserInfoDetails(userId);

    logger.exit("getUserInfoDetails() - Ends ");
    return userDetailsEntity;
  }
}
