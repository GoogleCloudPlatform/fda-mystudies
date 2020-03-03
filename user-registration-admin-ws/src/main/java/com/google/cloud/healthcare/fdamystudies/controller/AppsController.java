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

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppParticipantRegistryServiceResponse;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppsDetailsControllerResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppsDetailsServiceResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.service.AppsService;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@RestController
@RequestMapping("/apps")
public class AppsController {
  private static Logger logger = LoggerFactory.getLogger(AppsController.class);

  @Autowired AppsService appsService;

  @GetMapping(value = "")
  public ResponseEntity<?> getApps(@RequestHeader("userId") Integer userId) {
    logger.info("SiteController - getApps() : starts");
    DashboardBean dashboardBean;
    ErrorBean errorBean = null;
    if (null != userId && userId != 0) {
      try {
        dashboardBean = appsService.getApps(userId);
        if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_200.code()) {
          return new ResponseEntity<>(dashboardBean.getApps(), HttpStatus.OK);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_817.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.BAD_REQUEST);
        } else if (dashboardBean.getError().getApp_error_code() == ErrorCode.EC_500.code()) {
          return new ResponseEntity<>(dashboardBean.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
    logger.info("SiteController - getApps() : ends");

    return new ResponseEntity<>(dashboardBean.getApps(), HttpStatus.OK);
  }

  @GetMapping("/{app}/participants")
  public ResponseEntity<?> getAppParticipantRegistry(
      @PathVariable("app") String appId, @RequestHeader("userId") String userId) {

    ErrorBean errorBean = null;

    if (((appId.length() != 0) || (!StringUtils.isEmpty(appId)))
        && ((userId.length() != 0) || (!StringUtils.isEmpty(userId)))) {
      try {
        GetAppParticipantRegistryServiceResponse serviceResponse =
            appsService.getAppParticipantRegistry(
                Integer.parseInt(appId), Integer.parseInt(userId));
        if (serviceResponse != null) {
          GetAppParticipantRegistryResponse controllerResponse =
              BeanUtil.getBean(GetAppParticipantRegistryResponse.class);
          controllerResponse.setAppId(serviceResponse.getAppId());
          controllerResponse.setCustomAppId(serviceResponse.getCustomAppId());
          controllerResponse.setAppName(serviceResponse.getAppName());
          controllerResponse.setParticipants(serviceResponse.getParticipants());
          logger.info("(C)...AppsController.getAppParticipantRegistry()...Ended");
          return new ResponseEntity<>(controllerResponse, HttpStatus.OK);
        } else {
          errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_817.code(),
                  ErrorCode.EC_817.errorMessage(),
                  URWebAppWSConstants.ERROR,
                  ErrorCode.EC_817.errorMessage());
          logger.info("(C)...AppsController.getAppParticipantRegistry()...Ended");
          return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
        }
      } catch (InvalidUserIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_817.code(),
                ErrorCode.EC_817.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_817.errorMessage());
        logger.error("(C)...AppsController.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
      } catch (SystemException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.info("(C)...AppsController.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.info("(C)...AppsController.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("(C)...AppsController.getAppParticipantRegistry()...Ended");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }

  @GetMapping("/details")
  public ResponseEntity<?> getAppsDetails(@RequestHeader("userId") String userId) {

    logger.info("AppsController - getAppsDetails() : starts");
    ErrorBean errorBean = null;
    List<GetAppsDetailsControllerResponseBean> apps = null;

    if ((userId.length() != 0) || (StringUtils.isNotEmpty(userId))) {
      try {
        List<GetAppsDetailsServiceResponseBean> serviceResponseBean;
        serviceResponseBean = appsService.getAppsDetails(Integer.parseInt(userId));

        if (serviceResponseBean != null && !serviceResponseBean.isEmpty()) {
          apps = new ArrayList<>();
          for (GetAppsDetailsServiceResponseBean serviceResponse : serviceResponseBean) {
            if (serviceResponse != null) {
              GetAppsDetailsControllerResponseBean app =
                  BeanUtil.getBean(GetAppsDetailsControllerResponseBean.class);
              app.setId(serviceResponse.getId());
              app.setCustomId(serviceResponse.getCustomId());
              app.setName(serviceResponse.getName());
              app.setStudies(serviceResponse.getStudies());
              apps.add(app);
            }
          }
        } else {
          errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_817.code(),
                  ErrorCode.EC_817.errorMessage(),
                  URWebAppWSConstants.ERROR,
                  ErrorCode.EC_817.errorMessage());
          logger.info("AppsController - getAppsDetails() : ends with UNAUTHORIZED request");
          return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
        }
        logger.info("AppsController - getAppsDetails() : ends");
        return new ResponseEntity<>(apps, HttpStatus.OK);
      } catch (InvalidUserIdException e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_408.code(),
                ErrorCode.EC_408.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_408.errorMessage());
        logger.error("AppsController - getAppsDetails() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.UNAUTHORIZED);
      } catch (Exception e) {
        errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_500.code(),
                ErrorCode.EC_500.errorMessage(),
                URWebAppWSConstants.ERROR,
                ErrorCode.EC_500.errorMessage());
        logger.error("AppsController - getAppsDetails() : error ", e);
        return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } else {
      errorBean =
          AppUtil.dynamicResponse(
              ErrorCode.EC_777.code(),
              ErrorCode.EC_777.errorMessage(),
              URWebAppWSConstants.ERROR,
              ErrorCode.EC_777.errorMessage());
      logger.info("AppsController - getAppsDetails() : ends");
      return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
    }
  }
}
