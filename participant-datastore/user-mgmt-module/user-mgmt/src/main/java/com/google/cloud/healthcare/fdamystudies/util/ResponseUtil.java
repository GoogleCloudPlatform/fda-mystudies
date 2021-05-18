/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil.ErrorCodes;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpStatus;

public final class ResponseUtil {

  private ResponseUtil() {}

  private static final XLogger LOG = XLoggerFactory.getXLogger(ResponseUtil.class.getName());

  public static ResponseBean prepareBadRequestResponse(
      HttpServletResponse response, String... errorTypes) {
    String errorType = errorTypes[0];
    ResponseBean responseBean = new ResponseBean();
    // Default error code for missing required parameter and InvalidRequestException
    ErrorCodes errorMsg = MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG;

    switch (errorType) {
      case AppConstants.INVALID_EMAIL_CODE_EXCEPTION:
        errorMsg = MyStudiesUserRegUtil.ErrorCodes.INVALID_EMAIL_CODE;
        break;
      case AppConstants.INVALID_USERID_EXCEPTION:
        errorMsg = MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID;
        break;
      case AppConstants.EMAIL_NOT_EXISTS:
        errorMsg = MyStudiesUserRegUtil.ErrorCodes.EMAIL_NOT_EXISTS;
        break;
      case AppConstants.MISSING_REQUIRED_PARAMETER:
        errorMsg = MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG;
        break;
    }

    MyStudiesUserRegUtil.getFailureResponse(
        HttpStatus.BAD_REQUEST.toString(),
        MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
        errorMsg.getValue(),
        response);
    responseBean.setCode(HttpStatus.BAD_REQUEST.value());
    responseBean.setMessage(errorMsg.getValue());
    LOG.exit("Bad Request Response: " + responseBean);
    return responseBean;
  }

  public static ResponseBean prepareSystemExceptionResponse(HttpServletResponse response) {
    MyStudiesUserRegUtil.getFailureResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
        MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
        MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
        response);
    ResponseBean responseBean = new ResponseBean();
    responseBean.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue());
    return responseBean;
  }

  public static ResponseBean prepareSuccessResponse(HttpServletResponse response) {
    MyStudiesUserRegUtil.getFailureResponse(
        HttpStatus.OK.toString(),
        MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
        MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue(),
        response);
    ResponseBean responseBean = new ResponseBean();
    responseBean.setCode(HttpStatus.OK.value());
    responseBean.setMessage(ErrorCode.EC_200.errorMessage());
    return responseBean;
  }
}
