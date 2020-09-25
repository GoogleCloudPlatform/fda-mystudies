/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.STUDY_STATE_SAVE_OR_UPDATE_FAILED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithDrawFromStudyRespBean;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyStateDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.enroll.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class StudyStateServiceImpl implements StudyStateService {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateServiceImpl.class);

  @Autowired StudyStateDao studyStateDao;

  @Autowired CommonDao commonDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Autowired private CommonService commonService;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @Override
  public List<ParticipantStudiesBO> getParticipantStudiesList(UserDetailsBO user) {
    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Starts ");
    List<ParticipantStudiesBO> participantStudiesList = null;
    try {
      participantStudiesList = studyStateDao.getParticipantStudiesList(user);
    } catch (Exception e) {
      logger.error("StudyStateServiceImpl getParticipantStudiesList() - error ", e);
    }
    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Ends ");
    return participantStudiesList;
  }

  @Override
  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudiesBO> existParticipantStudies,
      String userId,
      AuditLogEventRequest auditRequest) {
    logger.info("StudyStateServiceImpl saveParticipantStudies() - Starts ");
    StudyStateRespBean studyStateRespBean = null;
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    boolean isExists = false;
    StudyInfoBO studyInfo = null;
    List<ParticipantStudiesBO> addParticipantStudiesList = new ArrayList<ParticipantStudiesBO>();
    List<String> customStudyIdList = new LinkedList<>();
    ParticipantStudiesBO participantStudyBo = new ParticipantStudiesBO();

    Map<String, String> placeHolder = new HashMap<>();
    auditRequest.setUserId(userId);
    try {
      for (int i = 0; i < studiesBeenList.size(); i++) {
        StudiesBean studiesBean = studiesBeenList.get(i);

        auditRequest.setStudyId(studiesBean.getStudyId());
        auditRequest.setParticipantId(studiesBean.getParticipantId());
        studyInfo = commonDao.getStudyDetails(studiesBean.getStudyId().trim());
        if (existParticipantStudies != null && !existParticipantStudies.isEmpty()) {
          for (ParticipantStudiesBO participantStudies : existParticipantStudies) {
            if (studyInfo != null) {
              if (studyInfo.getId().equals(participantStudies.getStudyInfo().getId())) {
                isExists = true;
                if (participantStudies.getStatus() != null
                    && participantStudies
                        .getStatus()
                        .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.YET_TO_JOIN.getValue())) {
                  participantStudies.setEnrolledDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
                }
                if (studiesBean.getStatus() != null
                    && !StringUtils.isEmpty(studiesBean.getStatus())) {
                  participantStudies.setStatus(studiesBean.getStatus());

                  if (studiesBean
                      .getStatus()
                      .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.IN_PROGRESS.getValue())) {
                    participantStudies.setEnrolledDate(
                        MyStudiesUserRegUtil.getCurrentUtilDateTime());
                  }
                }
                if (studiesBean.getBookmarked() != null) {
                  participantStudies.setBookmark(studiesBean.getBookmarked());
                }
                if (studiesBean.getCompletion() != null) {
                  participantStudies.setCompletion(studiesBean.getCompletion());
                }
                if (studiesBean.getAdherence() != null) {
                  participantStudies.setAdherence(studiesBean.getAdherence());
                }
                if (studiesBean.getParticipantId() != null
                    && StringUtils.isNotEmpty(studiesBean.getParticipantId())) {
                  participantStudies.setParticipantId(studiesBean.getParticipantId());
                }
                placeHolder.put("study_state_value", participantStudies.getStatus());
                addParticipantStudiesList.add(participantStudies);
              }
            }
          }
        }
        if (!isExists) {
          if (studiesBean.getStudyId() != null
              && StringUtils.isNotEmpty(studiesBean.getStudyId())
              && studyInfo != null) {
            participantStudyBo.setStudyInfo(studyInfo);
          }
          if (studiesBean.getStatus() != null && StringUtils.isNotEmpty(studiesBean.getStatus())) {
            participantStudyBo.setStatus(studiesBean.getStatus());
            if (studiesBean
                .getStatus()
                .equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.IN_PROGRESS.getValue())) {
              participantStudyBo.setEnrolledDate(MyStudiesUserRegUtil.getCurrentUtilDateTime());
            }
          } else {
            participantStudyBo.setStatus(MyStudiesUserRegUtil.ErrorCodes.YET_TO_JOIN.getValue());
          }
          if (studiesBean.getBookmarked() != null) {
            participantStudyBo.setBookmark(studiesBean.getBookmarked());
          }
          if (userId != null && StringUtils.isNotEmpty(userId)) {
            participantStudyBo.setUserDetails(commonDao.getUserInfoDetails(userId));
          }
          if (studiesBean.getCompletion() != null) {
            participantStudyBo.setCompletion(studiesBean.getCompletion());
          }
          if (studiesBean.getAdherence() != null) {
            participantStudyBo.setAdherence(studiesBean.getAdherence());
          }
          if (studiesBean.getParticipantId() != null
              && StringUtils.isNotEmpty(studiesBean.getParticipantId())) {
            participantStudyBo.setParticipantId(studiesBean.getParticipantId());
          }
          placeHolder.put("study_state_value", participantStudyBo.getStatus());
          addParticipantStudiesList.add(participantStudyBo);
          customStudyIdList.add(studiesBean.getStudyId());
        }
      }
      message = studyStateDao.saveParticipantStudies(addParticipantStudiesList);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        studyStateRespBean = new StudyStateRespBean();
        studyStateRespBean.setMessage(
            MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());

        enrollAuditEventHelper.logEvent(
            STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT, auditRequest, placeHolder);
      }
    } catch (Exception e) {
      enrollAuditEventHelper.logEvent(STUDY_STATE_SAVE_OR_UPDATE_FAILED, auditRequest, placeHolder);
      logger.error("StudyStateServiceImpl saveParticipantStudies() - error ", e);
    }

    logger.info("StudyStateServiceImpl saveParticipantStudies() - Ends ");
    return studyStateRespBean;
  }

  @Override
  public List<StudyStateBean> getStudiesState(String userId)
      throws SystemException, InvalidUserIdException /*, NoStudyEnrolledException*/ {
    logger.info("(Service)...StudyStateServiceImpl.getStudiesState()...Started");

    List<StudyStateBean> serviceResponseList = new ArrayList<>();

    if (userId != null) {
      try {
        UserDetailsBO userDetailsBO = userRegAdminUserDao.getRecord(userId);
        if (userDetailsBO != null) {

          List<ParticipantStudiesBO> participantStudiesList =
              participantStudiesInfoDao.getParticipantStudiesInfo(userDetailsBO.getUserDetailsId());
          if (participantStudiesList != null && !participantStudiesList.isEmpty()) {
            for (ParticipantStudiesBO participantStudiesBO : participantStudiesList) {
              StudyStateBean studyStateBean = BeanUtil.getBean(StudyStateBean.class);
              if (participantStudiesBO.getParticipantRegistrySite() != null) {
                String enrolledTokenVal =
                    studyStateDao.getEnrollTokenForParticipant(
                        participantStudiesBO.getParticipantRegistrySite().getId());
                studyStateBean.setHashedToken(
                    EnrollmentManagementUtil.getHashedValue(enrolledTokenVal));
              }
              if (participantStudiesBO.getStudyInfo() != null) {
                studyStateBean.setStudyId(participantStudiesBO.getStudyInfo().getCustomId());
              }
              studyStateBean.setStatus(participantStudiesBO.getStatus());
              if (participantStudiesBO.getParticipantId() != null) {
                studyStateBean.setParticipantId(participantStudiesBO.getParticipantId());
              }
              studyStateBean.setCompletion(participantStudiesBO.getCompletion());
              studyStateBean.setBookmarked(participantStudiesBO.getBookmark());
              studyStateBean.setAdherence(participantStudiesBO.getAdherence());
              if (participantStudiesBO.getEnrolledDate() != null) {
                studyStateBean.setEnrolledDate(
                    MyStudiesUserRegUtil.getIsoDateFormat(participantStudiesBO.getEnrolledDate()));
              }
              if (participantStudiesBO.getSiteBo() != null) {
                studyStateBean.setSiteId(participantStudiesBO.getSiteBo().getId().toString());
              }
              serviceResponseList.add(studyStateBean);
            }
          }
        } else {
          throw new InvalidUserIdException();
        }
      } catch (InvalidUserIdException | SystemException e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw e;
      } catch (Exception e) {
        logger.error("(Service)...StudyStateServiceImpl.getStudiesState(): (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(Service)...StudyStateServiceImpl.getStudiesState()...Ended");
      return null;
    }
    return serviceResponseList;
  }

  @Override
  public WithDrawFromStudyRespBean withdrawFromStudy(
      String participantId, String studyId, boolean delete)
      throws UnAuthorizedRequestException, InvalidRequestException, SystemException {
    logger.info("StudyStateServiceImpl withdrawFromStudy() - Starts ");
    WithDrawFromStudyRespBean respBean = null;
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    try {
      message = studyStateDao.withdrawFromStudy(participantId, studyId, delete);
      if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
        enrollUtil.withDrawParticipantFromStudy(participantId, studyId, delete);
        respBean = new WithDrawFromStudyRespBean();
        respBean.setCode(HttpStatus.OK.value());
        respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
      }
    } catch (UnAuthorizedRequestException | InvalidRequestException e) {
      logger.error("StudyStateServiceImpl withdrawFromStudy() - error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("StudyStateServiceImpl withdrawFromStudy() - error ", e);
      throw new SystemException();
    }
    logger.info("StudyStateServiceImpl withdrawFromStudy() - Ends ");
    return respBean;
  }
}
