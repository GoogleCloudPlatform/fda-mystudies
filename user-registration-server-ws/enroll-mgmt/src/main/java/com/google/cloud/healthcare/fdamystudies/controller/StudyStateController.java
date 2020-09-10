/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateResponse;
import com.google.cloud.healthcare.fdamystudies.beans.WithDrawFromStudyRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
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

@RestController
public class StudyStateController {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateController.class);

  @Autowired StudyStateService studyStateService;

  @Autowired CommonService commonService;

  @PostMapping(
      value = "/updateStudyState",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateStudyState(
      @RequestHeader("userId") String userId,
      @RequestBody StudyStateReqBean studyStateReqBean,
      @Context HttpServletResponse response) {
    logger.info("StudyStateController updateStudyState() - Starts ");
    StudyStateRespBean studyStateRespBean = null;
    try {
      if (studyStateReqBean != null && userId != null && !StringUtils.isEmpty(userId)) {
        if (studyStateReqBean.getStudies() != null && !studyStateReqBean.getStudies().isEmpty()) {
          List<StudiesBean> studiesBeenList = studyStateReqBean.getStudies();
          UserDetailsBO user = commonService.getUserInfoDetails(userId);
          if (user != null) {
            List<ParticipantStudiesBO> existParticipantStudies =
                studyStateService.getParticipantStudiesList(user);
            studyStateRespBean =
                studyStateService.saveParticipantStudies(
                    studiesBeenList, existParticipantStudies, userId);
            if (studyStateRespBean != null
                && studyStateRespBean
                    .getMessage()
                    .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
              studyStateRespBean.setCode(HttpStatus.OK.value());
            }
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
                response);
            return null;
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          return null;
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }
    } catch (Exception e) {
      logger.error("StudyStateController updateStudyState() - error ", e);
    }
    logger.info("StudyStateController updateStudyState() - Ends ");
    return new ResponseEntity<>(studyStateRespBean, HttpStatus.OK);
  }

  @GetMapping(value = "/studyState", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyState(
      @RequestHeader("userId") String userId, @Context HttpServletResponse response) {

    logger.info("(C)...StudyStateController.getStudyState()...Started");
    if (((userId.length() != 0) || StringUtils.isNotEmpty(userId))) {
      StudyStateResponse studyStateResponse = BeanUtil.getBean(StudyStateResponse.class);
      try {
        List<StudyStateBean> studies = studyStateService.getStudiesState(userId);
        studyStateResponse.setStudies(studies);
        studyStateResponse.setMessage(AppConstants.SUCCESS);
        return new ResponseEntity<>(studyStateResponse, HttpStatus.OK);
      } catch (InvalidUserIdException e) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_128.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
            response);
        logger.info("(C)...StudyStateController.getStudyState()...Ended with INVALID_USER_ID");
        return null;
      } catch (Exception e) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.EC_500.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
            response);
        logger.info(
            "(C)...StudyStateController.getStudyState()...Ended with Internal Server Error");
        return null;
      }
    } else {
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      logger.info("(C)...StudyStateController.getStudyState()...Ended with INVALID_INPUT");
      return null;
    }
  }

  @PostMapping(
      value = "/withdrawfromstudy",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> withdrawFromStudy(
      @RequestBody WithdrawFromStudyBean withdrawFromStudyBean,
      @Context HttpServletResponse response) {
    logger.info("StudyStateController withdrawFromStudy() - Starts ");
    WithDrawFromStudyRespBean respBean = null;
    try {
      if (withdrawFromStudyBean != null) {
        if (withdrawFromStudyBean.getParticipantId() != null
            && !withdrawFromStudyBean.getParticipantId().isEmpty()
            && withdrawFromStudyBean.getStudyId() != null
            && !withdrawFromStudyBean.getStudyId().isEmpty()) {
          respBean =
              studyStateService.withdrawFromStudy(
                  withdrawFromStudyBean.getParticipantId(),
                  withdrawFromStudyBean.getStudyId(),
                  withdrawFromStudyBean.isDelete());
          if (respBean != null) {
            logger.info("StudyStateController withdrawFromStudy() - Ends ");
            respBean.setCode(ErrorCode.EC_200.code());
            respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue());

            return new ResponseEntity<>(respBean, HttpStatus.OK);
          } else {
            MyStudiesUserRegUtil.getFailureResponse(
                MyStudiesUserRegUtil.ErrorCodes.STATUS_104.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
                MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue(),
                response);
            return null;
          }
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          return null;
        }
      } else {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
            response);
        return null;
      }
    } catch (UnAuthorizedRequestException e) {
      logger.error("StudyStateController withdrawFromStudy() - error ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_108.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNAUTHORIZED.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_CLIENTID_OR_SECRET_KEY.getValue(),
          response);
      return null;
    } catch (InvalidRequestException e) {
      logger.error("StudyStateController withdrawFromStudy() - error ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
          response);
      return null;
    } catch (Exception e) {
      logger.error("StudyStateController withdrawFromStudy() - error ", e);
      MyStudiesUserRegUtil.getFailureResponse(
          MyStudiesUserRegUtil.ErrorCodes.EC_500.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.UNKNOWN.getValue(),
          MyStudiesUserRegUtil.ErrorCodes.CONNECTION_ERROR_MSG.getValue(),
          response);
      return null;
    }
  }
}
