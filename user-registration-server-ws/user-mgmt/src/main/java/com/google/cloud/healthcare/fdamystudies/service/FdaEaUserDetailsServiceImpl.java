/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.dao.FdaEaUserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdOrEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash, Date: 03-Jan-2020, Time: 3:50:58 pm
 */
@Service
public class FdaEaUserDetailsServiceImpl implements FdaEaUserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(FdaEaUserDetailsServiceImpl.class);

  @Autowired private AuthInfoBOService authInfoService;

  @Autowired private UserAppDetailsService userAppDetailsService;

  @Autowired private FdaEaUserDetailsDao userDetailsDao;

  /*@Override
  public List<UserDetails> loadParticipantDetailsListByEmail(String emailId) {
    List<UserDetails> userDetails = null;
    if (emailId != null) {
      userDetails = userDetailsDao.loadParticipantDetailsListByEmail(emailId);
    }
    return userDetails;
  }*/

  @Override
  @Transactional
  public UserDetails saveUser(UserDetails userDetails) throws SystemException {
    // call dao layer to save the userDetails
    logger.info("(S)...FdaEaUserDetailsServiceImpl.saveUser()...STARTED");
    UserDetails daoResp = null;
    try {
      if (userDetails != null) {
        daoResp = userDetailsDao.saveUser(userDetails);

        // TODO: insert required records in auth_info table
        AuthInfoBO authInfo = new AuthInfoBO();
        authInfo.setAppId(daoResp.getAppInfoId());
        authInfo.setUserId(daoResp.getUserDetailsId());
        authInfo.setCreatedOn(LocalDateTime.now(ZoneId.systemDefault()));
        authInfo = authInfoService.save(authInfo);
        logger.info("(C)...authInfo: " + authInfo);

        // TODO: insert required records in user_app_details table
        UserAppDetailsBO userAppDetails = new UserAppDetailsBO();
        userAppDetails.setAppInfoId(daoResp.getAppInfoId());
        userAppDetails.setCreatedOn(LocalDateTime.now(ZoneId.systemDefault()));
        userAppDetails.setUserDetailsId(daoResp.getUserDetailsId());
        userAppDetails = userAppDetailsService.save(userAppDetails);
        logger.info("(C)...userAppDetails: " + userAppDetails);
      }
    } catch (Exception e) {
      logger.error("(S)...FdaEaUserDetailsServiceImpl.saveUser()...Ended", e);
      throw new SystemException();
    }
    return daoResp;
  }

  @Override
  public UserDetails loadUserDetailsByUserId(String userId) throws SystemException {
    // call dao layer to get the user details using userId
    logger.info("FdaEaUserDetailsServiceImpl.loadUserDetailsByUserId()...STARTED");
    UserDetails daoResp = null;
    if (userId != null) {
      daoResp = userDetailsDao.loadUserDetailsByUserId(userId);
    }
    return daoResp;
  }

  @Override
  public boolean verifyCode(String code, String userId)
      throws SystemException, InvalidUserIdOrEmailCodeException {

    UserDetails daoResopnse = null;
    if (userId != null) {
      daoResopnse = userDetailsDao.loadEmailCodeByUserId(userId);
      logger.info("(S)....daoResopnse: " + daoResopnse);

      if (daoResopnse != null
          && code.equals(daoResopnse.getEmailCode())
          && !LocalDateTime.now().isAfter(daoResopnse.getCodeExpireDate())) {
        logger.info("(S)......OTP CODE VERIFIED as true");

        // Calling User Registration Server to update Status == 1 and make emailCode = null

        UserDetails userDetails = userDetailsDao.loadUserDetailsByUserId(userId);

        userDetails.setStatus(1);
        userDetails.setEmailCode(null);
        userDetails.setCodeExpireDate(null);
        UserDetails updatedUserDetails = userDetailsDao.saveUser(userDetails);

        if (updatedUserDetails != null) {
          if (updatedUserDetails.getStatus() == 1) {
            return true;
          } else return false;
        } else throw new SystemException();
      } else {
        logger.info("No User Found Exception 2");
        throw new InvalidUserIdOrEmailCodeException();
      }
    }
    logger.info("(S)....daoResopnse: " + daoResopnse);
    return false;
  }
}
