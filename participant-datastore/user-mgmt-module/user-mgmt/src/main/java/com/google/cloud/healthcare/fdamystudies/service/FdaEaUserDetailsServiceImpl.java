/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.sql.Timestamp;
import java.time.Instant;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FdaEaUserDetailsServiceImpl implements FdaEaUserDetailsService {

  private XLogger logger = XLoggerFactory.getXLogger(FdaEaUserDetailsServiceImpl.class.getName());

  @Autowired private AuthInfoBOService authInfoService;

  @Autowired private UserAppDetailsService userAppDetailsService;

  @Autowired private FdaEaUserDetailsDao userDetailsDao;

  @Autowired private UserManagementUtil userManagementUtil;

  @Override
  @Transactional
  public UserDetailsEntity saveUser(UserDetailsEntity userDetails) {
    logger.entry("Begin saveUser()");
    UserDetailsEntity daoResp = null;
    if (userDetails != null) {
      daoResp = userDetailsDao.saveUser(userDetails);
      AuthInfoEntity authInfo = new AuthInfoEntity();
      authInfo.setApp(daoResp.getApp());
      authInfo.setUserDetails(daoResp);
      authInfo.setCreated(Timestamp.from(Instant.now()));
      authInfoService.save(authInfo);
      UserAppDetailsEntity userAppDetails = new UserAppDetailsEntity();
      userAppDetails.setApp(daoResp.getApp());
      userAppDetails.setCreated(Timestamp.from(Instant.now()));
      userAppDetails.setUserDetails(daoResp);
      userAppDetailsService.save(userAppDetails);
    }
    logger.exit("saveUser() - ends");

    return daoResp;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetailsEntity loadUserDetailsByUserId(String userId) {
    // call dao layer to get the user details using userId
    logger.entry("Begin loadUserDetailsByUserId()");
    UserDetailsEntity daoResp = null;
    if (userId != null) {
      daoResp = userDetailsDao.loadUserDetailsByUserId(userId);
    }
    logger.exit("loadUserDetailsByUserId()");
    return daoResp;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean verifyCode(String code, UserDetailsEntity participantDetails) {
    logger.entry("Begin verifyCode()");
    boolean result = code == null || participantDetails == null;
    if (result) {
      throw new IllegalArgumentException();
    }
    if (code.equals(participantDetails.getEmailCode())
        && Timestamp.from(Instant.now()).before(participantDetails.getCodeExpireDate())) {
      return true;
    } else {
      logger.exit("verifyCode() - ends");
      return false;
    }
  }

  @Override
  @Transactional()
  public String updateStatus(
      UserDetailsEntity participantDetails, AuditLogEventRequest auditRequest) {
    logger.entry("Begin updateStatus()");

    UpdateEmailStatusRequest updateEmailStatusRequest = new UpdateEmailStatusRequest();
    updateEmailStatusRequest.setStatus(UserAccountStatus.ACTIVE.getStatus());
    UpdateEmailStatusResponse updateStatusResponse =
        userManagementUtil.updateUserInfoInAuthServer(
            updateEmailStatusRequest, participantDetails.getUserId(), auditRequest);

    UserDetailsEntity userDetails = SerializationUtils.clone(participantDetails);
    userDetails.setId(participantDetails.getId());
    userDetails.setEmailCode(null);
    userDetails.setCodeExpireDate(null);
    userDetails.setStatus(AppConstants.EMAILID_VERIFIED_STATUS);
    userDetailsDao.updateStatus(userDetails);

    return updateStatusResponse.getTempRegId();
  }
}
