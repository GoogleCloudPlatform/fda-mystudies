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
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.repository.UserAppDetailsRepository;

@Repository
public class UserAppDetailsDaoImpl implements UserAppDetailsDao {

  private static final Logger logger = LoggerFactory.getLogger(UserAppDetailsDaoImpl.class);
  @Autowired private UserAppDetailsRepository userAppDetailsRepository;

  @Override
  public UserAppDetailsBO save(UserAppDetailsBO userAppDetails) throws SystemException {
    logger.info("UserAppDetailsDaoImpl loadEmailCodeByUserId() - starts");
    if (userAppDetails != null) {
      UserAppDetailsBO dbResponse = null;
      try {
        dbResponse = userAppDetailsRepository.save(userAppDetails);
        logger.info("UserAppDetailsDaoImpl loadEmailCodeByUserId() - ends");
        return dbResponse;
      } catch (Exception e) {
        logger.error("UserAppDetailsDaoImpl loadEmailCodeByUserId(): ", e);
        throw new SystemException();
      }
    } else return null;
  }
}
