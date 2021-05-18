/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.UserAppDetailsRepository;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserAppDetailsDaoImpl implements UserAppDetailsDao {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(UserAppDetailsDaoImpl.class.getName());
  @Autowired private UserAppDetailsRepository userAppDetailsRepository;

  @Override
  public UserAppDetailsEntity save(UserAppDetailsEntity userAppDetails) {
    logger.entry("Begin loadEmailCodeByUserId()");
    if (userAppDetails != null) {

      UserAppDetailsEntity dbResponse = userAppDetailsRepository.save(userAppDetails);
      logger.exit("loadEmailCodeByUserId() - ends");
      return dbResponse;
    }
    return null;
  }
}
