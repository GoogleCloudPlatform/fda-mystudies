/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

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

    return new ResponseEntity<>(
        new ErrorBean()
            .setCode(ErrorCode.EC_500.code())
            .setMessage(ErrorCode.EC_500.errorMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  public static long isValidSession(String sesionExpiryTime) {
    DateTimeFormatter baseFormatter = null;
    DateTimeFormatter formatter = null;
    LocalDateTime lastReqTime = null;
    long finalValue = 0;
    long expiryTime = 0;
    long currentTime = 0;
    if (!StringUtils.isEmpty(sesionExpiryTime)) {
      baseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
      lastReqTime = LocalDateTime.parse(sesionExpiryTime, baseFormatter);
      expiryTime = Long.parseLong(formatter.format(lastReqTime));
      LocalDateTime timeNow = LocalDateTime.now(ZoneId.of(AppConstants.SERVER_TIMEZONE));
      currentTime = Long.parseLong(formatter.format(timeNow));
      if (currentTime > expiryTime) {
        finalValue = -1;
      }
    }
    return finalValue;
  }
}
