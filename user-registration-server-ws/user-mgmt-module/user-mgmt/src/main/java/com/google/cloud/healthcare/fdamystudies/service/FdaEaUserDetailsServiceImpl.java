/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfo;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.transaction.Transactional;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class FdaEaUserDetailsServiceImpl implements FdaEaUserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsServiceImpl.class);

  @Autowired private AuthInfoBOService authInfoService;

  @Autowired private UserAppDetailsService userAppDetailsService;

  @Autowired private FdaEaUserDetailsDao userDetailsDao;

  @Autowired private UserManagementUtil userManagementUtil;

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
  public boolean verifyCode(String code, UserDetailsBO participantDetails) {
    logger.info("FdaEaUserDetailsServiceImpl verifyCode() - starts");
    boolean result = code == null || participantDetails == null;
    if (result) {
      throw new IllegalArgumentException();
    }
    if (code.equals(participantDetails.getEmailCode())
        && LocalDateTime.now().isBefore(participantDetails.getCodeExpireDate())) {
      return true;
    } else {
      logger.info("FdaEaUserDetailsServiceImpl verifyCode() - ends");
      return false;
    }
  }

  @Override
  public boolean updateStatus(UserDetailsBO participantDetails) {
    logger.info("FdaEaUserDetailsServiceImpl updateStatus() - starts");
    UserDetailsBO userDetailsBO = SerializationUtils.clone(participantDetails);
    userDetailsBO.setUserDetailsId(participantDetails.getUserDetailsId());
    userDetailsBO.setEmailCode(null);
    userDetailsBO.setCodeExpireDate(null);
    userDetailsBO.setStatus(AppConstants.EMAILID_VERIFIED_STATUS);
    boolean status = userDetailsDao.updateStatus(userDetailsBO);

    if (status) {
      UpdateAccountInfo accountStatus = new UpdateAccountInfo();
      accountStatus.setEmailVerified(true);
      UpdateAccountInfoResponseBean value =
          userManagementUtil.updateUserInfoInAuthServer(
              accountStatus, participantDetails.getUserId());
      if (value.getHttpStatusCode() != HttpStatus.OK.value()) {
        status = false; // rolling back in registration server and returning false.
        boolean rollbackStatus = userDetailsDao.updateStatus(participantDetails);
        if (!rollbackStatus) {
          logger.error("Failed to rollback email status.");
        }
      }
    }
    return status;
  }
}
