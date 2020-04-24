/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.service.UserSupportService;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@RestController
public class UserSupportController {

  private static final Logger logger = LoggerFactory.getLogger(UserSupportController.class);

  @Autowired UserSupportService supportService;

  @PostMapping(
      value = "/feedback",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> feedbackDetails(
      @RequestBody FeedbackReqBean reqBean, @Context HttpServletResponse response) {
    logger.info("INFO: StudyMetaDataService - feedbackDetails() :: Starts");
    int isSent = 0;
    ResponseBean responseBean = new ResponseBean();
    ErrorBean errorBean = null;
    try {
      if (StringUtils.isNotEmpty(reqBean.getSubject())
          && StringUtils.isNotEmpty(reqBean.getBody())) {
        isSent = supportService.feedback(reqBean.getSubject(), reqBean.getBody());
        if (isSent == 2) {
          responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        errorBean = new ErrorBean();
        errorBean.setCode(HttpStatus.BAD_REQUEST.value());
        errorBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("StudyMetaDataService - feedbackDetails() :: ERROR", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
          response);
      errorBean = new ErrorBean();
      errorBean.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue());
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("INFO: StudyMetaDataService - feedbackDetails() :: Ends");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }

  @PostMapping(
      value = "/contactUs",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> contactUsDetails(
      @RequestBody ContactUsReqBean reqBean, @Context HttpServletResponse response) {
    logger.info("INFO: StudyMetaDataService - contactUsDetails() :: Starts");
    int isSent = 0;
    ResponseBean responseBean = new ResponseBean();
    ErrorBean errorBean = null;
    try {
      if (StringUtils.isNotEmpty(reqBean.getSubject())
          && StringUtils.isNotEmpty(reqBean.getBody())
          && StringUtils.isNotEmpty(reqBean.getFirstName())
          && StringUtils.isNotEmpty(reqBean.getEmail())) {
        isSent =
            supportService.contactUsDetails(
                reqBean.getSubject(),
                reqBean.getBody(),
                reqBean.getFirstName(),
                reqBean.getEmail());
        if (isSent == 2) {
          responseBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        errorBean = new ErrorBean();
        errorBean.setCode(HttpStatus.BAD_REQUEST.value());
        errorBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue());
        return new ResponseEntity<>(errorBean, HttpStatus.BAD_REQUEST);
      }
    } catch (Exception e) {
      logger.error("StudyMetaDataService - contactUsDetails() :: ERROR", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
          response);
      errorBean = new ErrorBean();
      errorBean.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
      errorBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue());
      return new ResponseEntity<>(errorBean, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    logger.info("INFO: StudyMetaDataService - contactUsDetails() :: Ends");
    return new ResponseEntity<>(responseBean, HttpStatus.OK);
  }
}
