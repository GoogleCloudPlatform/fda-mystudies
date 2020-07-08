/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;

public class AppUtil {

  private static final Logger logger = LogManager.getLogger(AppUtil.class);

  public static ErrorBean dynamicResponse(
      int code, String userMessage, String type, String detailMessage) {
    ErrorBean error = null;
    try {
      error = new ErrorBean(code, userMessage, type, detailMessage);
    } catch (Exception e) {
      logger.error("ERROR: AppUtil - dynamicResponse() - error()", e);
    }
    return error;
  }
}
