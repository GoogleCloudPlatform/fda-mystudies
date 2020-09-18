/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  @Transactional(readOnly = true)
  public UserDetailsEntity getUserInfoDetails(String userId) {
    logger.info("CommonServiceImpl getUserInfoDetails() - Starts ");
    UserDetailsEntity userDetailsEntity = null;
    if (!StringUtils.isEmpty(userId)) {
      userDetailsEntity = commonDao.getUserInfoDetails(userId);
    }
    logger.info("CommonServiceImpl getUserInfoDetails() - Ends ");
    return userDetailsEntity;
  }
}
