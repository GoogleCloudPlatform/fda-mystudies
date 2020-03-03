/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserProfileUpdateBean;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileServiceDao;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@Service
public class UserProfileServiceImpl implements UserProfileService {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

  @Autowired private UserProfileServiceDao userProfileServiceDao;

  @Override
  public UserDetailsResponseBean getUserProfile(String authUserId) {
    logger.info("UserProfileServiceImpl - getUserProfile() - Starts");
    UserDetailsResponseBean userResponsebean = new UserDetailsResponseBean();

    try {
      userResponsebean = userProfileServiceDao.getUserProfile(authUserId);
    } catch (Exception e) {
      logger.error("UserProfileServiceImpl - getUserProfile() - Error", e);
      userResponsebean.setError(
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info("UserProfileServiceImpl - getUserProfile() - end");
    return userResponsebean;
  }

  @Override
  public ErrorBean updateUserProfile(String userId, UserProfileUpdateBean userReqBean) {
    logger.info("UserProfileServiceImpl - updateUserProfile() - Starts");
    ErrorBean errorBean = null;
    try {
      errorBean = userProfileServiceDao.updateUserProfile(userId, userReqBean);
    } catch (Exception e) {
      logger.error("UserProfileServiceImpl - updateUserProfile() - Error", e);
      errorBean =
          new ErrorBean(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage());
    }
    logger.info("SafePassageUserLoginServiceImpl - updateUserProfile() - end");
    return errorBean;
  }
}
