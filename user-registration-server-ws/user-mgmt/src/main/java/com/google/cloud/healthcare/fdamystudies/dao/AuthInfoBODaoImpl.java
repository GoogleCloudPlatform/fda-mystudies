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
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.repository.AuthInfoBORepository;

/**
 * user-management-service-bundle
 *
 * @author Chiranjibi Dash
 */
@Service
public class AuthInfoBODaoImpl implements AuthInfoBODao {

  private static final Logger logger = LoggerFactory.getLogger(AuthInfoBODaoImpl.class);
  @Autowired AuthInfoBORepository authInfoRepository;

  @Override
  public AuthInfoBO save(AuthInfoBO authInfo) throws SystemException {
    logger.info("AuthInfoBODaoImpl.save()...Started");
    if (authInfo != null) {
      try {
        AuthInfoBO dbResponse = null;
        if (authInfo != null) {
          dbResponse = authInfoRepository.save(authInfo);
        }
        return dbResponse;
      } catch (Exception e) {
        logger.error("AuthInfoBODaoImpl.save()...Ended");
        throw new SystemException();
      }
    } else return null;
  }
}
