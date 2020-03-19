package com.google.cloud.healthcare.fdamystudies.controller;

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
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.NoStudyEnrolledException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.service.StudyStateService;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@RestController
public class StudyStateController {
  private static final Logger logger = LoggerFactory.getLogger(StudyStateController.class);

  @Autowired StudyStateService studyStateService;

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
          List<ParticipantStudiesBO> existParticipantStudies =
              studyStateService.getParticipantStudiesList(userId);
          if (existParticipantStudies != null
              && !existParticipantStudies.isEmpty()
              && studiesBeenList != null
              && !studiesBeenList.isEmpty()) {
            studyStateRespBean =
                studyStateService.saveParticipantStudies(
                    studiesBeenList, existParticipantStudies, userId);
            if (studyStateRespBean != null
                && studyStateRespBean
                    .getMessage()
                    .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
              studyStateRespBean.setCode(HttpStatus.OK.value());
            }
          }
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

  @GetMapping(
      value = "/studyState",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getStudyState(
      @RequestHeader("userId") String userId, @Context HttpServletResponse response) {

    logger.info("(C)...StudyStateController.getStudyState()...Started");
    if (((userId.length() != 0) || StringUtils.isNotEmpty(userId))) {
      List<StudyStateBean> studyStateList = null;
      try {
        studyStateList = studyStateService.getStudiesState(userId);
        if (studyStateList != null && studyStateList.size() > 0) {
          return new ResponseEntity<>(studyStateList, HttpStatus.OK);
        } else {
          MyStudiesUserRegUtil.getFailureResponse(
              MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
              MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT_ERROR_MSG.getValue(),
              response);
          logger.info("(C)...StudyStateController.getStudyState()...Ended with INVALID_INPUT");
          return null;
        }
      } catch (InvalidUserIdException e) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_128.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_USER_ID.getValue(),
            response);
        logger.info("(C)...StudyStateController.getStudyState()...Ended with INVALID_USER_ID");
        return null;
      } catch (NoStudyEnrolledException e) {
        MyStudiesUserRegUtil.getFailureResponse(
            MyStudiesUserRegUtil.ErrorCodes.STATUS_102.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.INVALID_INPUT.getValue(),
            MyStudiesUserRegUtil.ErrorCodes.NO_STUDIES_FOUND.getValue(),
            response);
        logger.info("(C)...StudyStateController.getStudyState()...Ended with NO_STUDIES_FOUND");
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
}
