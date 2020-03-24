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
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.beans.VerifyCodeResponse;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
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
        authInfo = authInfoService.save(authInfo);

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
  public VerifyCodeResponse verifyCode(String code, String userId)
      throws SystemException, InvalidUserIdException, InvalidEmailCodeException {

    logger.info("FdaEaUserDetailsServiceImpl verifyCode() - calls");
    VerifyCodeResponse response = null;
    UserDetailsBO daoResopnse = null;
    if (userId != null) {
      daoResopnse = userDetailsDao.loadEmailCodeByUserId(userId);

      if (daoResopnse != null) {
        if (code.equals(daoResopnse.getEmailCode())
            && !LocalDateTime.now().isAfter(daoResopnse.getCodeExpireDate())) {
          logger.info("(S)......OTP CODE VERIFIED as true");

          daoResopnse.setStatus(1);
          daoResopnse.setEmailCode(null);
          daoResopnse.setCodeExpireDate(null);
          UserDetailsBO updatedUserDetails = userDetailsDao.saveUser(daoResopnse);

          if (updatedUserDetails != null) {
            if (updatedUserDetails.getStatus() == 1) {
              response = new VerifyCodeResponse();
              response.setEmailId(updatedUserDetails.getEmail());
              response.setIsCodeVerified(true);
              return response;
            } else return response;
          } else throw new SystemException();
        } else throw new InvalidEmailCodeException();
      } else {
        logger.info("No User Found Exception");
        throw new InvalidUserIdException();
      }
    }
    return response;
  }
}
