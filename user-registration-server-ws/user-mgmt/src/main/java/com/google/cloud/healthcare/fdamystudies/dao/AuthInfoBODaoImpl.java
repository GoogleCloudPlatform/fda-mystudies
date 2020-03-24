/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoBORepository;

@Service
public class AuthInfoBODaoImpl implements AuthInfoBODao {

  private static final Logger logger = LoggerFactory.getLogger(AuthInfoBODaoImpl.class);
  @Autowired AuthInfoBORepository authInfoRepository;

  @Override
  public AuthInfoBO save(AuthInfoBO authInfo) throws SystemException {
    logger.info("AuthInfoBODaoImpl save() - starts");
    AuthInfoBO dbResponse = null;
    if (authInfo != null) {
      try {
        dbResponse = authInfoRepository.save(authInfo);
        logger.info("AuthInfoBODaoImpl save() - ends");
        return dbResponse;
      } catch (Exception e) {
        logger.error("AuthInfoBODaoImpl save(): ", e);
        throw new SystemException();
      }
    } else return null;
  }
}
