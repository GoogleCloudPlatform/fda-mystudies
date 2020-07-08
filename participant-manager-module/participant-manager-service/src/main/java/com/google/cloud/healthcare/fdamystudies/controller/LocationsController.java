/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@CrossOrigin
@RestController
public class LocationsController {

  @Autowired private LocationService locationService;

  private static final String ERROR_BEAN_TYPE = "error";

  private static final Logger logger = LoggerFactory.getLogger(LocationsController.class);

  @PostMapping("/locations")
  public ResponseEntity<?> addNewLocation(
      @RequestHeader("userId") Integer userId, @RequestBody LocationBean locationBean) {
    try {
      logger.info("LocationsController - addNewLocation() - starts");
      if (StringUtils.isBlank(locationBean.getName())
          || StringUtils.isBlank(locationBean.getDescription())
          || StringUtils.isBlank(locationBean.getCustomId())) {

        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_812.code(),
                ErrorCode.EC_812.errorMessage(),
                ERROR_BEAN_TYPE,
                ErrorCode.EC_812.errorMessage());
        logger.info("LocationsController - addNewLocation() - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } else if (!Pattern.matches(
              AppConstants.ALPHA_NUMERIC_REGEX_MAX15, locationBean.getCustomId())
          || locationBean.getName().length() > AppConstants.LOCATION_NAME_MAX_LENGTH
          || locationBean.getDescription().length()
              > AppConstants.LOCATION_DESCRIPTION_MAX_LENGTH) {

        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_813.code(),
                ErrorCode.EC_813.errorMessage(),
                ERROR_BEAN_TYPE,
                ErrorCode.EC_813.errorMessage());
        logger.info("LocationsController - addNewLocation() - ends");
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      locationService.addNewLocation(userId, locationBean);
      if (locationBean.getErrorBean() != null) {
        ErrorBean errorBean = locationBean.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_882.code() == errorBean.getAppErrorCode()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_861.code() == errorBean.getAppErrorCode()) {
          httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ErrorCode.EC_883.code() == errorBean.getAppErrorCode()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        logger.info("LocationsController - addNewLocation() - ends");
        return new ResponseEntity<>(errorBean, httpStatus);
      } else {
        logger.info("LocationsController - addNewLocation() - ends");
        return new ResponseEntity<>(locationBean, HttpStatus.CREATED);
      }
    } catch (Exception e) {
      logger.info("LocationsController - addNewLocation() - error", e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
