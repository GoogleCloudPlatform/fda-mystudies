/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
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
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.util.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.util.EnrollmentManagementUtil;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.transaction.SystemException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyStateServiceImpl implements StudyStateService {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(StudyStateServiceImpl.class.getName());

  @Autowired StudyStateDao studyStateDao;

  @Autowired CommonDao commonDao;

  @Autowired EnrollmentManagementUtil enrollUtil;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantEnrollmentHistoryRepository participantEnrollmentHistoryRepository;

  @Autowired private StudyRepository studyRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ParticipantStudyEntity> getParticipantStudiesList(
      UserDetailsEntity user, List<StudiesBean> studiesBeenList) {
    logger.entry("Begin getParticipantStudiesList()");

    List<ParticipantStudyEntity> participantStudies = new ArrayList<>();
    List<String> participantStudyIds = new ArrayList<>();

    List<String> customStudyIds =
        studiesBeenList
            .stream()
            .map(StudiesBean::getStudyId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    List<String> siteIds =
        studiesBeenList
            .stream()
            .map(StudiesBean::getSiteId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    Optional<StudyEntity> optStudy = studyRepository.findByCustomIds(customStudyIds);

    if (optStudy.isPresent() && optStudy.get().getType().equals(OPEN_STUDY)) {
      participantStudyIds =
          participantStudyRepository.findByStudyIdAndUserDetailId(
              optStudy.get().getId(), user.getUserId());
    } else if (CollectionUtils.isEmpty(siteIds) && optStudy.get().getType().equals(CLOSE_STUDY)) {
      participantStudyIds =
          participantStudyRepository.findByEmailAndStudyCustomIds(user.getEmail(), customStudyIds);
    } else {
      participantStudyIds =
          participantStudyRepository.findByEmailAndSiteIds(user.getEmail(), siteIds);
    }

    if (CollectionUtils.isNotEmpty(participantStudyIds)) {
      participantStudies = participantStudyRepository.findAllById(participantStudyIds);
    }
    logger.exit("getParticipantStudiesList() - Ends ");
    return participantStudies;
  }

  @Override
  @Transactional
  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudyEntity> existParticipantStudies,
      AuditLogEventRequest auditRequest,
      UserDetailsEntity user) {
    logger.entry("Begin saveParticipantStudies()");
    StudyStateRespBean studyStateRespBean = null;
    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();
    List<ParticipantStudyEntity> participantStudies = new ArrayList<ParticipantStudyEntity>();
    Map<String, String> placeHolder = new HashMap<>();
    auditRequest.setUserId(user.getUserId());
    Map<String, ParticipantStudyEntity> studyParticipantbyIdMap =
        existParticipantStudies
            .stream()
            .collect(
                Collectors.toMap(
                    ParticipantStudyEntity::getStudyId,
                    Function.identity(),
                    (existing, replacement) -> existing));

    try {
      for (StudiesBean studyBean : studiesBeenList) {
        String participantId =
            studyBean.getParticipantId() != null ? studyBean.getParticipantId() : NOT_APPLICABLE;
        auditRequest.setParticipantId(participantId);

        ParticipantStudyEntity participantStudyEntity = null;

        StudyEntity studyEntity = commonDao.getStudyDetails(studyBean.getStudyId().trim());
        auditRequest.setStudyId(studyEntity.getCustomId());
        auditRequest.setStudyVersion(String.valueOf(studyEntity.getVersion()));
        if (studyParticipantbyIdMap.containsKey(studyBean.getStudyId().trim())) {
          participantStudyEntity = studyParticipantbyIdMap.get(studyBean.getStudyId().trim());
        } else {
          participantStudyEntity = new ParticipantStudyEntity();
          participantStudyEntity.setStudy(studyEntity);
          participantStudyEntity.setStatus(EnrollmentStatus.YET_TO_ENROLL.getStatus());
        }

        if (StringUtils.isNotEmpty(studyBean.getStatus())) {
          participantStudyEntity.setStatus(studyBean.getStatus());
          if (EnrollmentStatus.ENROLLED.getStatus().equalsIgnoreCase(studyBean.getStatus())) {
            participantStudyEntity.setEnrolledDate(Timestamp.from(Instant.now()));
          }
        }

        participantStudyEntity.setBookmark(studyBean.getBookmarked());
        participantStudyEntity.setCompletion(studyBean.getCompletion());
        participantStudyEntity.setAdherence(studyBean.getAdherence());
        participantStudyEntity.setUserDetails(user);

        placeHolder.put("study_state_value", participantStudyEntity.getStatus());
        participantStudies.add(participantStudyEntity);
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
    logger.exit("saveParticipantStudies() - Ends ");
    return studyStateRespBean;
  }

  @Override
  @Transactional(readOnly = true)
  public List<StudyStateBean> getStudiesState(String userId) throws SystemException {
    logger.entry("Begin getStudiesState()");

    List<StudyStateBean> serviceResponseList = new ArrayList<>();

    UserDetailsEntity userDetailsEntity = userRegAdminUserDao.getRecord(userId);
    if (userDetailsEntity == null) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    List<ParticipantStudyEntity> participantStudiesList =
        participantStudiesInfoDao.getParticipantStudiesInfo(userDetailsEntity.getUserId());
    if (participantStudiesList != null && !participantStudiesList.isEmpty()) {
      for (ParticipantStudyEntity participantStudy : participantStudiesList) {
        StudyStateBean studyStateBean = BeanUtil.getBean(StudyStateBean.class);
        if (participantStudy.getParticipantRegistrySite() != null) {
          String enrolledTokenVal =
              studyStateDao.getEnrollTokenForParticipant(
                  participantStudy.getParticipantRegistrySite().getId());
          studyStateBean.setHashedToken(
              EnrollmentManagementUtil.getHashedValue(enrolledTokenVal.toUpperCase()));
        }

        if (participantStudy.getStudy() != null) {
          studyStateBean.setStudyId(participantStudy.getStudy().getCustomId());
        }
        if (participantStudy.getParticipantId() != null) {
          studyStateBean.setParticipantId(participantStudy.getParticipantId());
        }
        studyStateBean.setCompletion(participantStudy.getCompletion());
        studyStateBean.setBookmarked(participantStudy.getBookmark());
        studyStateBean.setAdherence(participantStudy.getAdherence());
        if (participantStudy.getEnrolledDate() != null) {
          studyStateBean.setEnrolledDate(
              MyStudiesUserRegUtil.getIsoDateFormat(participantStudy.getEnrolledDate()));
        }
        if (participantStudy.getSite() != null) {
          studyStateBean.setSiteId(participantStudy.getSite().getId().toString());
        }
        String enrollmentHistoryStatus = null;
        if (StringUtils.isNotEmpty(studyStateBean.getSiteId())
            && StringUtils.isNotEmpty(participantStudy.getParticipantRegistrySite().getId())) {
          enrollmentHistoryStatus =
              participantEnrollmentHistoryRepository.findBySiteIdAndParticipantRegistryId(
                  studyStateBean.getSiteId(),
                  participantStudy.getParticipantRegistrySite().getId());
        }
        String enrollmentStatus =
            StringUtils.isNotEmpty(enrollmentHistoryStatus)
                    && EnrollmentStatus.WITHDRAWN.getStatus().equals(enrollmentHistoryStatus)
                    && EnrollmentStatus.YET_TO_ENROLL
                        .getStatus()
                        .equals(participantStudy.getStatus())
                ? EnrollmentStatus.WITHDRAWN.getStatus()
                : participantStudy.getStatus();
        studyStateBean.setStatus(enrollmentStatus);
        serviceResponseList.add(studyStateBean);
      }
    }

    return serviceResponseList;
  }

  @Override
  @Transactional
  public WithDrawFromStudyRespBean withdrawFromStudy(
      String participantId, String studyId, AuditLogEventRequest auditRequest) {
    logger.entry("Begin withdrawFromStudy()");
    WithDrawFromStudyRespBean respBean = null;

    String message = studyStateDao.withdrawFromStudy(participantId, studyId);
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

      enrollUtil.withDrawParticipantFromStudy(
          participantId, participantStudy.get().getStudy().getVersion(), studyId, auditRequest);
      respBean = new WithDrawFromStudyRespBean();
      respBean.setCode(HttpStatus.OK.value());
      respBean.setMessage(MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
    }

    logger.exit("withdrawFromStudy() - Ends ");
    return respBean;
  }

  @Override
  public String getSiteId(String userId, String token) {
    logger.entry("Begin getSiteId()");
    String siteId = null;
    if (StringUtils.isNotEmpty(token)) {
      siteId = participantStudyRepository.getSiteId(userId, token.toUpperCase());
    }

    logger.exit("getSiteId() - Ends ");
    return StringUtils.defaultString(siteId);
  }
}
