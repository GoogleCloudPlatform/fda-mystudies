/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.repository.UserAppDetailsRepository;

/**
 * user-management-service-bundle
 *
 * @author Chiranjibi Dash
 */
@Repository
public class UserAppDetailsDaoImpl implements UserAppDetailsDao {

  private static final Logger logger = LoggerFactory.getLogger(UserAppDetailsDaoImpl.class);
  @Autowired private UserAppDetailsRepository userAppDetailsRepository;

  @Override
  public UserAppDetailsBO save(UserAppDetailsBO userAppDetails) throws SystemException {
    logger.info("UserAppDetailsDaoImpl.save()...Started");
    if (userAppDetails != null) {
      UserAppDetailsBO dbResponse = null;
      try {
        dbResponse = userAppDetailsRepository.save(userAppDetails);
        return dbResponse;
      } catch (Exception e) {
        logger.error("UserAppDetailsDaoImpl.save()...Ended");
        throw new SystemException();
      }
    } else return null;
  }
}
