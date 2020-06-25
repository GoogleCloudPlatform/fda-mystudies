/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.dao.AuthInfoBODao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

@Service
public class AuthInfoBOServiceImpl implements AuthInfoBOService {

  private static final Logger logger = LoggerFactory.getLogger(AuthInfoBOServiceImpl.class);
  @Autowired AuthInfoBODao authInfoDao;

  @Override
  public AuthInfoBO save(AuthInfoBO authInfo) throws SystemException {
    logger.info("AuthInfoBOServiceImpl save() - starts");
    AuthInfoBO dbResponse = null;
    if (authInfo != null) {
      dbResponse = authInfoDao.save(authInfo);
    }
    logger.info("AuthInfoBOServiceImpl save() - ends");
    return dbResponse;
  }
}
