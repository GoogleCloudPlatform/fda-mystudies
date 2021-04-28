/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.AuthInfoBODao;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthInfoBOServiceImpl implements AuthInfoBOService {

  private XLogger logger = XLoggerFactory.getXLogger(AuthInfoBOServiceImpl.class.getName());
  @Autowired AuthInfoBODao authInfoDao;

  @Override
  @Transactional
  public AuthInfoEntity save(AuthInfoEntity authInfo) {

    logger.entry("Begin save()");
    AuthInfoEntity dbResponse = null;
    if (authInfo != null) {
      dbResponse = authInfoDao.save(authInfo);
    }
    logger.exit("save() - ends");
    return dbResponse;
  }
}
