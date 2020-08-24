/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AppUtil {

  private static final Logger logger = LoggerFactory.getLogger(AppUtil.class);

  private static Map<String, String> appProperties;

  public static void setAppProperties(Map<String, String> appProperties) {
    AppUtil.appProperties = appProperties;
  }

  public static Map<String, String> getAppProperties() {
    return appProperties;
  }

  public static ResponseEntity<Object> httpResponseForInternalServerError() {
    logger.info("INFO: AppUtil - httpResponseForInternalServerError() :: starts");
    ErrorBean errorBean = null;
    try {
      errorBean =
          new ErrorBean()
              .setCode(ErrorCode.EC_500.code())
              .setMessage(ErrorCode.EC_500.errorMessage());
    } catch (Exception e) {
      logger.error("ERROR: AppUtil - httpResponseForInternalServerError()", e);
    }
    logger.info("INFO: AppUtil - httpResponseForInternalServerError() :: ends");
    return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static ErrorBean dynamicResponse(int code, String message) {
    ErrorBean error = null;
    try {
      error = new ErrorBean(code, message);
    } catch (Exception e) {
      logger.error("ERROR: AppUtil - dynamicResponse() - error()", e);
    }
    return error;
  }
}
