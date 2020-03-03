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

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessBean;
import com.google.cloud.healthcare.fdamystudies.bean.User;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateEntryFoundException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidEmailIdException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotInvited;
import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@RestController
public class ManageUsers {

  private static Logger logger = LoggerFactory.getLogger(ManageUsers.class);

  @Autowired private ManageUserService manageUserService;

  @PostMapping(
      value = "/manageusers/",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> addNewUserSiteCoordinate(
      @RequestBody RegisterUser user, @RequestHeader("userId") String userId) {

    logger.info("ManageUsers - addNewUserSiteCoordinate(): starts");
    ErrorBean errorBean = null;
    if (((userId.length() != 0) || StringUtils.isNotEmpty(userId))
        && ((user.getEmail() != null) || (!StringUtils.isEmpty(user.getEmail())))
        && ((user.getFirstName() != null) || (!StringUtils.isEmpty(user.getFirstName())))
        && ((user.getLastName() != null) || (!StringUtils.isEmpty(user.getLastName())))
    /*&& user.getAppsPermission().isEmpty()*/ ) {

      try {

        //        ((studyId.length() != 0) || (!StringUtils.isEmpty(studyId))
        // TODO: save Details in user admin table
        String message = manageUserService.saveUser(userId, user);
        if ("SUCCESS".equals(message)) {
          logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended");
          return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        } else {
          errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_777.code(),
                  ErrorCode.EC_777.errorMessage(),
                  URWebAppWSConstants.ERROR,
                  ErrorCode.EC_777.errorMessage());
          logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended");
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      } catch (InvalidUserIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_817.code(),
                ErrorCode.EC_817.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_817.errorMessage());
        logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
      } catch (SystemException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended");
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (DuplicateEntryFoundException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_979.code(),
                ErrorCode.EC_979.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_979.errorMessage());
        logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("(C)...ManageUsers.addNewUserSite_Coordinate()...Ended");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping(
      value = "/manageusers",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> manageUsers(@RequestHeader("userId") String userId) {

    logger.info("(C)...ManageUsers.manageUsers()...Started with " + userId);
    ErrorBean errorBean = null;

    if (((userId.length() != 0) || StringUtils.isNotEmpty(userId))) {
      try {

        // TODO: save Details in user admin table
        List<User> manageUsers = manageUserService.getUsers(userId);
        if (manageUsers != null && !manageUsers.isEmpty()) {
          logger.info("(C)...ManageUsers.manageUsers()...Ended");
          return new ResponseEntity<>(manageUsers, HttpStatus.OK);
        } else {
          errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_777.code(),
                  ErrorCode.EC_777.errorMessage(),
                  "error",
                  ErrorCode.EC_777.errorMessage());
          logger.info("(C)...ManageUsers.manageUsers()...Ended");
          return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
        }
      } catch (InvalidUserIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_817.code(),
                ErrorCode.EC_817.errorMessage(),
                "error",
                ErrorCode.EC_817.errorMessage());
        logger.error("(C)...ManageUsers.manageUsers()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
      } catch (SystemException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                "error",
                ErrorCode.EC_500.errorMessage());
        logger.error("(C)...ManageUsers.manageUsers()...Ended");
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                "error",
                ErrorCode.EC_500.errorMessage());
        logger.error("(C)...ManageUsers.manageUsers()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              "error",
              ErrorCode.EC_777.errorMessage());
      logger.info("(C)...ManageUsers.manageUsers()...Ended");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @PostMapping(
      value = "/users/",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> setUpAccount(@RequestBody SetUpAccountRequest request) {

    logger.info("ManageUsers - setUpAccount() : starts");
    ErrorBean errorBean = null;

    if (((request.getEmail().length() != 0) || StringUtils.isNotEmpty(request.getEmail()))
        && ((request.getPassword().length() != 0) || StringUtils.isNotEmpty(request.getPassword()))
        && ((request.getFirstName().length() != 0)
            || StringUtils.isNotEmpty(request.getFirstName()))
        && ((request.getLastName().length() != 0)
            || StringUtils.isNotEmpty(request.getLastName()))) {

      try {
        SetUpAccountResponse serviceResponse = manageUserService.saveUser(request);

        if ("SUCCESS".equals(serviceResponse.getMessage())) {
          SuccessBean sucessBean = new SuccessBean("SUCCESS");
          logger.info("ManageUsers - setUpAccount() : ends successfully");
          return new ResponseEntity<>(sucessBean, HttpStatus.OK);
        } else {
          if ("400".equals(serviceResponse.getStatusCode())) {
            errorBean =
                AppUtil.dynamicResponse(
                    ErrorCode.EC_400.code(),
                    serviceResponse.getMessage(),
                    URWebAppWSConstants.ERROR,
                    ErrorCode.EC_44.errorMessage());
            logger.info("ManageUsers - setUpAccount() : ends");
            return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
          } else {
            errorBean =
                AppUtil.dynamicResponse(
                    ErrorCode.EC_401.code(),
                    ErrorCode.EC_401.errorMessage(),
                    URWebAppWSConstants.ERROR,
                    serviceResponse.getMessage());
            logger.info("ManageUsers - setUpAccount() : ends");
            return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
          }
        }
      } catch (UserNotInvited e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_861.code(),
                ErrorCode.EC_861.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_861.errorMessage());
        logger.error("ManageUsers - setUpAccount() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (InvalidEmailIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_867.code(),
                ErrorCode.EC_44.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_867.errorMessage());
        logger.error("ManageUsers - setUpAccount() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.error("ManageUsers - setUpAccount() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("ManageUsers - setUpAccount() : ends");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }
}
