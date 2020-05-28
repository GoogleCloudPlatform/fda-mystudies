/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

@Service
public class FdaEaUserDetailsServiceImpl implements FdaEaUserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsServiceImpl.class);

  @Autowired private AuthInfoBOService authInfoService;

  @Autowired private UserAppDetailsService userAppDetailsService;

  @Autowired private FdaEaUserDetailsDao userDetailsDao;

  @Override
  @Transactional
  public UserDetailsBO saveUser(UserDetailsBO userDetailsBO) throws SystemException {
    logger.info("FdaEaUserDetailsServiceImpl saveUser() - starts");
    UserDetailsBO daoResp = null;
    try {
      if (userDetailsBO != null) {
        daoResp = userDetailsDao.saveUser(userDetailsBO);

        AuthInfoBO authInfo = new AuthInfoBO();
        authInfo.setAppId(daoResp.getAppInfoId());
        authInfo.setUserId(daoResp.getUserDetailsId());
        authInfo.setCreatedOn(LocalDateTime.now(ZoneId.systemDefault()));
        authInfoService.save(authInfo);

        UserAppDetailsBO userAppDetails = new UserAppDetailsBO();
        userAppDetails.setAppInfoId(daoResp.getAppInfoId());
        userAppDetails.setCreatedOn(LocalDateTime.now(ZoneId.systemDefault()));
        userAppDetails.setUserDetailsId(daoResp.getUserDetailsId());
        userAppDetailsService.save(userAppDetails);
      }
      logger.info("FdaEaUserDetailsServiceImpl saveUser() - ends");
    } catch (Exception e) {
      logger.error("FdaEaUserDetailsServiceImpl saveUser(): ", e);
      throw new SystemException();
    }
    return daoResp;
  }

  @Override
  public UserDetailsBO loadUserDetailsByUserId(String userId) throws SystemException {
    // call dao layer to get the user details using userId
    logger.info("FdaEaUserDetailsServiceImpl loadUserDetailsByUserId() - starts");
    UserDetailsBO daoResp = null;
    if (userId != null) {
      daoResp = userDetailsDao.loadUserDetailsByUserId(userId);
    }
    logger.info("FdaEaUserDetailsServiceImpl loadUserDetailsByUserId() - ends");
    return daoResp;
  }

  @Override
  public boolean verifyCode(String code, String userId)
      throws InvalidUserIdException, InvalidEmailCodeException, SystemException {

    logger.info("FdaEaUserDetailsServiceImpl verifyCode() - startes");
    boolean response = false;
    UserDetailsBO daoResopnse = null;
    if (userId != null) {
      daoResopnse = userDetailsDao.loadEmailCodeByUserId(userId);

      if (daoResopnse != null) {
        if (code.equals(daoResopnse.getEmailCode())
            && LocalDateTime.now().isBefore(daoResopnse.getCodeExpireDate())) {
          logger.info("(S)......OTP CODE VERIFIED as true");
          return true;
        } else {
          logger.info("FdaEaUserDetailsServiceImpl verifyCode() - ends");
          throw new InvalidEmailCodeException();
        }
      } else {
        logger.info("FdaEaUserDetailsServiceImpl verifyCode() - ends");
        throw new InvalidUserIdException();
      }
    }
    return response;
  }

  @Override
  public boolean updateStatus(UserDetailsBO participantDetails)
      throws InvalidRequestException, SystemException {
    logger.info("FdaEaUserDetailsServiceImpl updateStatus() - starts");
    if (participantDetails != null) {
      return userDetailsDao.updateStatus(participantDetails);
    } else {
      logger.info("FdaEaUserDetailsServiceImpl updateStatus() - ends");
      return false;
    }
  }
}
