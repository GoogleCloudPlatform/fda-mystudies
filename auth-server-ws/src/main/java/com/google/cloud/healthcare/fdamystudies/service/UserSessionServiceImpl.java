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
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotFoundException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.repository.SessionRepository;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Service
public class UserSessionServiceImpl implements UserSessionService {

  private static final Logger logger = LoggerFactory.getLogger(UserSessionServiceImpl.class);

  @Autowired private SessionRepository session;

  public String deleteTokenExpireDateByUserId(String userId)
      throws UserNotFoundException, SystemException {

    logger.info("UserSessionServiceImpl deleteTokenExpireDateByUserId() - starts");
    try {
      long result = session.deleteByUserId(userId);
      if (result > 0 && result == 1) {
        logger.info("UserSessionService deleteTokenExpireDateByUserId() - ends");
        return AppConstants.SUCCESS;
      } else {
        throw new UserNotFoundException();
      }
    } catch (UserNotFoundException e) {
      logger.error("UserSessionServiceImpl deleteTokenExpireDateByUserId() - error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("UserSessionServiceImpl deleteTokenExpireDateByUserId() - error ", e);
      throw new SystemException();
    }
  }

  public AuthInfoBO loadSessionByUserId(String userId) throws SystemException {
    logger.info("UserSessionServiceImpl loadSessionByUserId() - starts");
    AuthInfoBO sessionDetails = null;
    try {
      sessionDetails = session.findByUserId(userId);
      logger.info("UserSessionServiceImpl loadSessionByUserId() - ends");
      return sessionDetails;
    } catch (Exception e) {
      logger.error("UserSessionServiceImpl loadSessionByUserId() - error ", e);
      throw new SystemException();
    }
  }

  public AuthInfoBO save(AuthInfoBO authInfo) throws SystemException {

    logger.info("UserSessionServiceImpl save() - starts");
    try {
      AuthInfoBO sessionDetails = session.findByUserId(authInfo.getUserId());

      if (sessionDetails == null) {
        AuthInfoBO newUser = session.save(authInfo);
        logger.info("UserSessionServiceImpl save() - ends");
        return newUser;
      } else {
        sessionDetails.setExpireDate(authInfo.getExpireDate());
        logger.info("UserSessionServiceImpl save() - ends");
        return session.save(sessionDetails);
      }
    } catch (Exception e) {
      logger.error("UserSessionServiceImpl save() - error ", e);
      throw new SystemException();
    }
  }
}
