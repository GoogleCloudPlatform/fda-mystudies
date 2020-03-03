/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.ChangePasswordBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.ProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.service.UserProfileService;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSUtil;
import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/")
public class UserProfileController {

  private static final Logger logger = LogManager.getLogger(UserLoginController.class);

  private static final Gson gson = new Gson();

  @Autowired private UserProfileService userProfileService;

  @Autowired private URWebAppWSUtil urWebAppWSUtil;

  @Autowired private ApplicationConfiguratation appConfig;

  @RequestMapping(value = "ping")
  public ResponseEntity<?> ping() {
    logger.info(" UserProfileController - ping()");
    return new ResponseEntity<>(gson.toJson("UR  web app ws API works!"), HttpStatus.OK);
  }

  @GetMapping(value = "/users/{user}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getUserProfile(@PathVariable("user") String authUserId) {
    logger.info("UserProfileController getUserProfile() - Starts ");
    UserProfileRespBean userProfileRespBean = new UserProfileRespBean();
    ProfileRespBean profileRespBean = new ProfileRespBean();
    ErrorBean errorBean = null;
    UserDetailsResponseBean userDetailsResBean = new UserDetailsResponseBean();
    try {
      if ((authUserId != null) && !StringUtils.isEmpty(authUserId)) {
        userDetailsResBean = userProfileService.getUserProfile(authUserId);
        if (userDetailsResBean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {

          /*userProfileRespBean = userDetailsResBean.getUserProfileRespBean();*/
          profileRespBean = userDetailsResBean.getProfileRespBean();
        } else {
          if (userDetailsResBean.getError().getApp_error_code() == ErrorCode.EC_61.code()) {
            return new ResponseEntity<>(userDetailsResBean.getError(), HttpStatus.BAD_REQUEST);
          } else if (userDetailsResBean.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
            return new ResponseEntity<>(
                userDetailsResBean.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
          }
        }
      } else {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_777.code(),
                ErrorCode.EC_777.errorMessage(),
                "error",
                ErrorCode.EC_777.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }

    } catch (Exception e) {
      logger.error("UserProfileController getUserProfile() - error ", e);
    }
    logger.info("UserProfileController getUserProfile() - Ends ");
    return new ResponseEntity<>(profileRespBean, HttpStatus.OK);
  }

  //  @PutMapping(value = "/users/{user}", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
  // MediaType.APPLICATION_JSON_VALUE)
  @PostMapping(
      value = "/updateUserProfile",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUserProfile(@RequestBody UserRequestBean userDetails) {
    logger.info("UserProfileController updateUserProfile() - Starts ");
    ErrorBean errorBean = null;
    String clientId = "";
    String secretKey = "";
    String respMessage = "";
    ChangePasswordBean changePasswordBean = new ChangePasswordBean();
    try {
      clientId = appConfig.getClientId();
      secretKey = appConfig.getSecretKey();
      if (!StringUtils.isEmpty(userDetails.getUserId())
          && !StringUtils.isEmpty(userDetails.getUserInfo())) {
        errorBean =
            userProfileService.updateUserProfile(
                userDetails.getUserId(), userDetails.getUserInfo());
        if (errorBean.getApp_error_code() == ErrorCode.EC_200.code()) {
          if (!StringUtils.isEmpty(userDetails.getCurrentPswd())
              && !StringUtils.isEmpty(userDetails.getNewPswd())
              && !StringUtils.isEmpty(userDetails.getConfirmPswd())) {
            changePasswordBean.setCurrentPassword(userDetails.getCurrentPswd());
            changePasswordBean.setNewPassword(userDetails.getNewPswd());
            respMessage =
                urWebAppWSUtil.changePassword(
                    userDetails.getUserId(), 0, 0, clientId, secretKey, changePasswordBean);
            if (respMessage.equalsIgnoreCase("SUCCESS")) {
              errorBean = new ErrorBean(ErrorCode.EC_30.code(), ErrorCode.EC_30.errorMessage());
            }
          }
        } else {
          return new ResponseEntity<>(errorBean, HttpStatus.CONFLICT);
        }
      } else {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_789.code(),
                ErrorCode.EC_789.errorMessage(),
                "error",
                ErrorCode.EC_789.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.NOT_FOUND);
      }

    } catch (Exception e) {
      logger.error("UserProfileController updateUserProfile() - error ", e);
    }
    logger.info("UserProfileController updateUserProfile() - Ends ");
    return new ResponseEntity<>(errorBean, HttpStatus.OK);
  }
}
