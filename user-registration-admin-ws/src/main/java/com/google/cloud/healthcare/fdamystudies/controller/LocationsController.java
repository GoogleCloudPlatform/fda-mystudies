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

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationBean;
import com.google.cloud.healthcare.fdamystudies.bean.LocationResponseBean;
import com.google.cloud.healthcare.fdamystudies.service.LocationService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@RestController
public class LocationsController {

  @Autowired private LocationService locationService;

  private static final String ERROR_BEAN_TYPE = "error";

  @GetMapping(value = {"/locations", "/locations/{locationId}"})
  public ResponseEntity<?> getLocations(
      @RequestHeader("userId") String authUserId,
      @PathVariable(value = "locationId", required = false) Integer locationId) {
    try {
      LocationResponseBean locationRespBean = locationService.getLocations(authUserId, locationId);
      if (locationRespBean.getErrorBean() != null) {
        ErrorBean errorBean = locationRespBean.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_882.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_881.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.NOT_FOUND;
        } else if (ErrorCode.EC_861.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.UNAUTHORIZED;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(errorBean, httpStatus);
      } else {
        return new ResponseEntity<>(locationRespBean.getLocations(), HttpStatus.OK);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/locations")
  public ResponseEntity<?> addNewLocation(
      @RequestHeader("userId") String authUserId, @RequestBody LocationBean locationBean) {
    try {
      if (StringUtils.isBlank(locationBean.getName())
          || StringUtils.isBlank(locationBean.getDescription())
          || StringUtils.isBlank(locationBean.getCustomId())) {

        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_812.code(),
                ErrorCode.EC_812.errorMessage(),
                ERROR_BEAN_TYPE,
                ErrorCode.EC_812.errorMessage());
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
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      locationService.addNewLocation(authUserId, locationBean);
      if (locationBean.getErrorBean() != null) {
        ErrorBean errorBean = locationBean.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_882.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_861.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ErrorCode.EC_883.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(errorBean, httpStatus);
      } else {
        return new ResponseEntity<>(locationBean, HttpStatus.CREATED);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/locations/{locationId}")
  public ResponseEntity<?> updateLocation(
      @RequestHeader("userId") String authUserId,
      @RequestBody LocationBean locationBean,
      @PathVariable("locationId") Integer locationId) {
    try {

      if (!StringUtils.isBlank(locationBean.getCustomId())) {

        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_884.code(),
                ErrorCode.EC_884.errorMessage(),
                ERROR_BEAN_TYPE,
                ErrorCode.EC_884.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      } else if (locationBean.getName().length() > 200
          || locationBean.getDescription().length() > 500
          || (!StringUtils.isBlank(locationBean.getStatus())
              && !"0".equals(locationBean.getStatus())
              && !"1".equals(locationBean.getStatus()))) {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_813.code(),
                ErrorCode.EC_813.errorMessage(),
                ERROR_BEAN_TYPE,
                ErrorCode.EC_813.errorMessage());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
      locationBean.setId(locationId);
      locationService.updateLocation(authUserId, locationBean);
      if (locationBean.getErrorBean() != null) {
        ErrorBean errorBean = locationBean.getErrorBean();
        HttpStatus httpStatus = null;
        if (ErrorCode.EC_882.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.FORBIDDEN;
        } else if (ErrorCode.EC_861.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ErrorCode.EC_881.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.NOT_FOUND;
        } else if (ErrorCode.EC_885.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ErrorCode.EC_886.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else if (ErrorCode.EC_887.code() == errorBean.getApp_error_code()) {
          httpStatus = HttpStatus.BAD_REQUEST;
        } else {
          httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(errorBean, httpStatus);
      } else {
        return new ResponseEntity<>(locationBean, HttpStatus.OK);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(locationBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
