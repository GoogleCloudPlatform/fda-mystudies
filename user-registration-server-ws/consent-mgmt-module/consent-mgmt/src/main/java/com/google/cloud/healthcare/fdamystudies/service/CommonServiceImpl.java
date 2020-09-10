/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Starts ");

    Integer userDetailId = commonDao.getUserDetailsId(userId);

    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Ends ");
    return userDetailId;
  }
}
