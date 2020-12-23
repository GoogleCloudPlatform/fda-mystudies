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
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyStateDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantEnrollmentHistoryRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyStateServiceImpl implements StudyStateService {

  private static final Logger logger = LoggerFactory.getLogger(StudyStateServiceImpl.class);

  @Autowired StudyStateDao studyStateDao;

  @Autowired CommonDao commonDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantEnrollmentHistoryRepository participantEnrollmentHistoryRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ParticipantStudyEntity> getParticipantStudiesList(UserDetailsEntity user) {
    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Starts ");
    List<ParticipantStudyEntity> participantStudiesList = null;
    participantStudiesList = studyStateDao.getParticipantStudiesList(user);

    logger.info("StudyStateServiceImpl getParticipantStudiesList() - Ends ");
    return participantStudiesList;
  }

  @Override
  @Transactional
  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudyEntity> existParticipantStudies,
      String userId,
      AuditLogEventRequest auditRequest) {
    logger.info("StudyStateServiceImpl saveParticipantStudies() - Starts ");
    StudyStateRespBean studyStateRespBean = null;
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    List<ParticipantStudyEntity> participantStudies = new ArrayList<ParticipantStudyEntity>();
    Map<String, String> placeHolder = new HashMap<>();
    auditRequest.setUserId(userId);
    Map<String, ParticipantStudyEntity> studyParticipantbyIdMap =
        existParticipantStudies
            .stream()
            .collect(Collectors.toMap(ParticipantStudyEntity::getStudyId, Function.identity()));

    try {
      for (StudiesBean studyBean : studiesBeenList) {
        auditRequest.setStudyId(studyBean.getStudyId());
        auditRequest.setParticipantId(studyBean.getParticipantId());
        ParticipantStudyEntity participantStudyEntity1 = null;
        if (studyParticipantbyIdMap.containsKey(studyBean.getStudyId().trim())) {
          participantStudyEntity1 = studyParticipantbyIdMap.get(studyBean.getStudyId().trim());
        } else {
          StudyEntity studyEntity = commonDao.getStudyDetails(studyBean.getStudyId().trim());
          participantStudyEntity1 = new ParticipantStudyEntity();
          participantStudyEntity1.setStudy(studyEntity);
        }

        participantStudyEntity1.setStatus(studyBean.getStatus());
        if (EnrollmentStatus.ENROLLED.getStatus().equalsIgnoreCase(studyBean.getStatus())) {
          participantStudyEntity1.setEnrolledDate(Timestamp.from(Instant.now()));
          participantStudyEntity1.setParticipantId(studyBean.getParticipantId());
        }

        participantStudyEntity1.setBookmark(studyBean.getBookmarked());
        participantStudyEntity1.setCompletion(studyBean.getCompletion());
        participantStudyEntity1.setAdherence(studyBean.getAdherence());

        placeHolder.put("study_state_value", participantStudyEntity1.getStatus());
        participantStudies.add(participantStudyEntity1);
      }
      message = studyStateDao.saveParticipantStudies(participantStudies);
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
      throw e;
    }

    logger.info("StudyStateServiceImpl saveParticipantStudies() - Ends ");
    return studyStateRespBean;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StudyStateBean> getStudiesState(String userId) throws SystemException {
    logger.info("(Service)...StudyStateServiceImpl.getStudiesState()...Started");

    List<StudyStateBean> serviceResponseList = new ArrayList<>();

    if (userId != null) {
      UserDetailsEntity userDetailsEntity = userRegAdminUserDao.getRecord(userId);
      if (userDetailsEntity == null) {
        throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
      }

      List<ParticipantStudyEntity> participantStudiesList =
          participantStudiesInfoDao.getParticipantStudiesInfo(userDetailsEntity.getUserId());
      if (participantStudiesList != null && !participantStudiesList.isEmpty()) {
        for (ParticipantStudyEntity participantStudiesBO : participantStudiesList) {
          StudyStateBean studyStateBean = BeanUtil.getBean(StudyStateBean.class);
          if (participantStudiesBO.getParticipantRegistrySite() != null) {
            String enrolledTokenVal =
                studyStateDao.getEnrollTokenForParticipant(
                    participantStudiesBO.getParticipantRegistrySite().getId());
            studyStateBean.setHashedToken(
                EnrollmentManagementUtil.getHashedValue(enrolledTokenVal.toUpperCase()));
          }
          if (participantStudiesBO.getStudy() != null) {
            studyStateBean.setStudyId(participantStudiesBO.getStudy().getCustomId());
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
          if (participantStudiesBO.getSite() != null) {
            studyStateBean.setSiteId(participantStudiesBO.getSite().getId().toString());
          }
          serviceResponseList.add(studyStateBean);
        }
      }
    }

    return serviceResponseList;
  }

  @Override
  @Transactional
  public WithDrawFromStudyRespBean withdrawFromStudy(
      String participantId, String studyId, boolean delete, AuditLogEventRequest auditRequest) {
    logger.info("StudyStateServiceImpl withdrawFromStudy() - Starts ");
    WithDrawFromStudyRespBean respBean = null;

    String message = studyStateDao.withdrawFromStudy(participantId, studyId, delete);
    if (message.equalsIgnoreCase(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue())) {
      Optional<ParticipantStudyEntity> participantStudy =
          participantStudyRepository.findByParticipantId(participantId);

      Optional<ParticipantRegistrySiteEntity> optParticipantRegistrySite =
          participantRegistrySiteRepository.findById(
              participantStudy.get().getParticipantRegistrySite().getId());
      ParticipantRegistrySiteEntity participantRegistrySite = optParticipantRegistrySite.get();
      participantRegistrySite.setOnboardingStatus(OnboardingStatus.DISABLED.getCode());
      participantRegistrySite.setDisabledDate(new Timestamp(Instant.now().toEpochMilli()));
      participantRegistrySiteRepository.saveAndFlush(participantRegistrySite);

      participantEnrollmentHistoryRepository.updateWithdrawalDateAndStatus(
          participantStudy.get().getUserDetails().getId(),
          participantStudy.get().getStudy().getId(),
          EnrollmentStatus.WITHDRAWN.getStatus());

      participantStudy.get().setParticipantId(null);
      participantStudyRepository.saveAndFlush(participantStudy.get());

      enrollUtil.withDrawParticipantFromStudy(participantId, studyId, delete, auditRequest);
      respBean = new WithDrawFromStudyRespBean();
      respBean.setCode(HttpStatus.OK.value());
      respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    }

    logger.info("StudyStateServiceImpl withdrawFromStudy() - Ends ");
    return respBean;
  }
}
