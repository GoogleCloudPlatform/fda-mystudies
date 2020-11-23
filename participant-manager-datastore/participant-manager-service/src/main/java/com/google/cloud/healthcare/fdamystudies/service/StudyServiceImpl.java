/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.STUDY_PARTICIPANT_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.StudyResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCountForStudy;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyCount;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfo;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudyServiceImpl implements StudyService {
  private XLogger logger = XLoggerFactory.getXLogger(StudyServiceImpl.class.getName());

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private SiteRepository siteRepository;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Override
  @Transactional(readOnly = true)
  public StudyResponse getStudies(String userId) {
    logger.entry("getStudies(String userId)");

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!(optUserRegAdminEntity.isPresent())) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      StudyResponse studyResponse = getStudiesForSuperAdmin(optUserRegAdminEntity.get());
      logger.exit(
          String.format("total studies for superadmin=%d", studyResponse.getStudies().size()));
      return studyResponse;
    }

    List<StudyInfo> studyDetails = studyRepository.getStudyDetails(userId);

    if (CollectionUtils.isEmpty(studyDetails)) {
      throw new ErrorCodeException(ErrorCode.NO_STUDIES_FOUND);
    }

    List<EnrolledInvitedCountForStudy> enrolledInvitedCountList =
        studyRepository.getEnrolledInvitedCountByUserId(userId);
    Map<String, EnrolledInvitedCountForStudy> enrolledInvitedCountMap =
        CollectionUtils.emptyIfNull(enrolledInvitedCountList)
            .stream()
            .collect(
                Collectors.toMap(EnrolledInvitedCountForStudy::getStudyId, Function.identity()));

    List<EnrolledInvitedCountForStudy> enrolledInvitedCountListForOpenStudy =
        studyRepository.getInvitedEnrolledCountForOpenStudyForStudies(userId);
    Map<String, EnrolledInvitedCountForStudy> enrolledInvitedCountMapOfOpenStudy =
        CollectionUtils.emptyIfNull(enrolledInvitedCountListForOpenStudy)
            .stream()
            .collect(
                Collectors.toMap(EnrolledInvitedCountForStudy::getStudyId, Function.identity()));
    enrolledInvitedCountMap.putAll(enrolledInvitedCountMapOfOpenStudy);

    List<StudyCount> siteCounts = studyRepository.getSiteCount(userId);
    Map<String, StudyCount> sitesCountMap =
        siteCounts.stream().collect(Collectors.toMap(StudyCount::getStudyId, Function.identity()));

    return prepareStudyResponse(
        studyDetails, sitesCountMap, enrolledInvitedCountMap, optUserRegAdminEntity.get());
  }

  private StudyResponse getStudiesForSuperAdmin(UserRegAdminEntity userRegAdminEntity) {

    List<StudyCount> studyInvitedCountList = studyRepository.findInvitedCountByStudyId();
    Map<String, StudyCount> studyInvitedCountMap =
        studyInvitedCountList
            .stream()
            .collect(Collectors.toMap(StudyCount::getStudyId, Function.identity()));

    List<StudyCount> studyEnrolledCountList = studyRepository.findEnrolledCountByStudyId();
    Map<String, StudyCount> studyEnrolledCountMap =
        studyEnrolledCountList
            .stream()
            .collect(Collectors.toMap(StudyCount::getStudyId, Function.identity()));

    List<SiteCount> sitesList = siteRepository.findStudySitesCount();
    Map<String, SiteCount> sitesPerStudyMap =
        sitesList.stream().collect(Collectors.toMap(SiteCount::getStudyId, Function.identity()));

    List<StudyEntity> studies = studyRepository.findAll();
    List<StudyDetails> studyDetailsList = new ArrayList<>();
    for (StudyEntity study : studies) {
      if (sitesPerStudyMap.containsKey(study.getId())) {
        StudyDetails studyDetail = new StudyDetails();
        studyDetail.setId(study.getId());
        studyDetail.setCustomId(study.getCustomId());
        studyDetail.setName(study.getName());
        studyDetail.setType(study.getType());
        studyDetail.setLogoImageUrl(study.getLogoImageUrl());
        SiteCount siteCount = sitesPerStudyMap.get(study.getId());
        if (siteCount != null && siteCount.getCount() != null) {
          studyDetail.setSitesCount(siteCount.getCount());
        }
        studyDetail.setStudyPermission(Permission.EDIT.value());
        Long enrolledCount = getCount(studyEnrolledCountMap, study.getId());
        Long invitedCount = getCount(studyInvitedCountMap, study.getId());
        studyDetail.setEnrolled(enrolledCount);
        studyDetail.setInvited(invitedCount);
        if (studyDetail.getInvited() != 0
            && (studyDetail.getType().equals(OPEN_STUDY)
                || studyDetail.getInvited() >= studyDetail.getEnrolled())) {
          Double percentage =
              (Double.valueOf(studyDetail.getEnrolled()) * 100)
                  / Double.valueOf(studyDetail.getInvited());
          studyDetail.setEnrollmentPercentage(percentage);
        }
        studyDetailsList.add(studyDetail);
      }
    }
    return new StudyResponse(
        MessageCode.GET_STUDIES_SUCCESS, studyDetailsList, userRegAdminEntity.isSuperAdmin());
  }

  private Long getCount(Map<String, StudyCount> map, String studyId) {
    if (map.containsKey(studyId)) {
      return map.get(studyId).getCount();
    }
    return 0L;
  }

  private StudyResponse prepareStudyResponse(
      List<StudyInfo> studyList,
      Map<String, StudyCount> sitesCountMap,
      Map<String, EnrolledInvitedCountForStudy> enrolledInvitedCountMap,
      UserRegAdminEntity userRegAdminEntity) {
    List<StudyDetails> studies = new ArrayList<>();
    for (StudyInfo study : studyList) {
      StudyDetails studyDetail = new StudyDetails();
      studyDetail.setId(study.getStudyId());
      studyDetail.setCustomId(study.getCustomId());
      studyDetail.setName(study.getStudyName());
      studyDetail.setType(study.getType());
      studyDetail.setLogoImageUrl(study.getLogoImageUrl());
      studyDetail.setStudyPermission(study.getEdit());
      studyDetail.setSitesCount(
          sitesCountMap.containsKey(study.getStudyId())
              ? sitesCountMap.get(study.getStudyId()).getCount()
              : 0L);

      calculateEnrollmentPercentage(enrolledInvitedCountMap, study, studyDetail);
      studies.add(studyDetail);
    }

    LongSummaryStatistics totalSitePermission =
        sitesCountMap.values().stream().mapToLong(StudyCount::getCount).summaryStatistics();

    StudyResponse studyResponse =
        new StudyResponse(
            MessageCode.GET_STUDIES_SUCCESS,
            studies,
            totalSitePermission.getSum(),
            userRegAdminEntity.isSuperAdmin());
    logger.exit(String.format("total studies=%d", studyResponse.getStudies().size()));
    return studyResponse;
  }

  private void calculateEnrollmentPercentage(
      Map<String, EnrolledInvitedCountForStudy> enrolledInvitedCountMap,
      StudyInfo study,
      StudyDetails studyDetail) {
    long studyInvitedCount = 0L;
    Long studyEnrolledCount = 0L;

    EnrolledInvitedCountForStudy enrolledInvitedCount =
        enrolledInvitedCountMap.get(study.getStudyId());

    if (enrolledInvitedCount != null) {
      studyInvitedCount = enrolledInvitedCount.getInvitedCount();
      studyEnrolledCount = enrolledInvitedCount.getEnrolledCount();
    }

    studyDetail.setEnrolled(studyEnrolledCount);
    studyDetail.setInvited(studyInvitedCount);
    if (studyDetail.getInvited() != 0
        && (studyDetail.getType().equals(OPEN_STUDY)
            || studyDetail.getInvited() >= studyDetail.getEnrolled())) {
      Double percentage =
          (Double.valueOf(studyDetail.getEnrolled()) * 100)
              / Double.valueOf(studyDetail.getInvited());
      studyDetail.setEnrollmentPercentage(percentage);
    }
  }

  @Override
  public ParticipantRegistryResponse getStudyParticipants(
      String userId,
      String studyId,
      AuditLogEventRequest auditRequest,
      Integer page,
      Integer limit) {
    logger.entry("getStudyParticipants(String userId, String studyId)");
    auditRequest.setUserId(userId);

    // validations
    Optional<StudyEntity> optStudy = studyRepository.findById(studyId);
    if (!optStudy.isPresent()) {
      throw new ErrorCodeException(ErrorCode.STUDY_NOT_FOUND);
    }

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    StudyPermissionEntity studyPermissionEntity = null;
    AppEntity app = null;
    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      StudyEntity study = optStudy.get();
      Optional<AppEntity> optApp = appRepository.findById(study.getApp().getId());
      app = optApp.orElseThrow(() -> new ErrorCodeException(ErrorCode.APP_NOT_FOUND));
    } else {
      Optional<StudyPermissionEntity> optStudyPermission =
          studyPermissionRepository.findByStudyIdAndUserId(studyId, userId);
      StudyEntity study = optStudy.get();
      if (study.getType().equals(OPEN_STUDY) && !optStudyPermission.isPresent()) {
        List<SitePermissionEntity> sitePermissions =
            sitePermissionRepository.findByUserIdAndStudyId(userId, studyId);
        if (CollectionUtils.isEmpty(sitePermissions)) {
          throw new ErrorCodeException(ErrorCode.SITE_PERMISSION_ACCESS_DENIED);
        }
        app = study.getApp();
      } else {
        app =
            optStudyPermission
                .orElseThrow(() -> new ErrorCodeException(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED))
                .getApp();
        studyPermissionEntity = optStudyPermission.get();
      }

      if (app == null) {
        throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
      }
    }

    return prepareRegistryParticipantResponse(
        optStudy.get(),
        app,
        studyPermissionEntity,
        optUserRegAdminEntity.get(),
        auditRequest,
        page,
        limit);
  }

  private ParticipantRegistryResponse prepareRegistryParticipantResponse(
      StudyEntity study,
      AppEntity app,
      StudyPermissionEntity studyPermissionEntity,
      UserRegAdminEntity user,
      AuditLogEventRequest auditRequest,
      Integer page,
      Integer limit) {
    ParticipantRegistryDetail participantRegistryDetail =
        ParticipantMapper.fromStudyAndApp(study, app);

    if (OPEN_STUDY.equalsIgnoreCase(study.getType())) {
      Optional<SiteEntity> optSiteEntity =
          siteRepository.findByStudyIdAndType(study.getId(), study.getType());
      if (optSiteEntity.isPresent()) {
        participantRegistryDetail.setTargetEnrollment(optSiteEntity.get().getTargetEnrollment());
      }

      if (user.isSuperAdmin()) {
        participantRegistryDetail.setOpenStudySitePermission(Permission.EDIT.value());
      } else if (studyPermissionEntity != null) {
        participantRegistryDetail.setOpenStudySitePermission(
            studyPermissionEntity.getEdit().value());
      }
    }

    List<ParticipantRegistrySiteEntity> participantSiteList = null;
    if (page != null && limit != null) {
      Page<ParticipantRegistrySiteEntity> participantSitePage =
          participantRegistrySiteRepository.findByStudyIdForPagination(
              study.getId(), PageRequest.of(page, limit, Sort.by("created").descending()));
      participantSiteList = participantSitePage.getContent();
    } else {
      participantSiteList = participantRegistrySiteRepository.findByStudyId(study.getId());
    }

    List<ParticipantDetail> registryParticipants = new ArrayList<>();

    List<String> registryIds =
        CollectionUtils.emptyIfNull(participantSiteList)
            .stream()
            .map(ParticipantRegistrySiteEntity::getId)
            .collect(Collectors.toList());

    List<ParticipantStudyEntity> participantStudies = new ArrayList<>();
    // Check not empty for Ids to avoid SQLSyntaxErrorException
    if (CollectionUtils.isNotEmpty(registryIds)) {
      participantStudies =
          (List<ParticipantStudyEntity>)
              CollectionUtils.emptyIfNull(
                  participantStudyRepository.findParticipantsByParticipantRegistrySite(
                      registryIds));
    }

    if (CollectionUtils.isNotEmpty(participantSiteList)) {
      for (ParticipantRegistrySiteEntity participantSite : participantSiteList) {
        ParticipantDetail participantDetail =
            ParticipantMapper.fromParticipantStudy(participantSite, participantStudies);

        registryParticipants.add(participantDetail);
      }
    }

    participantRegistryDetail.setRegistryParticipants(registryParticipants);

    ParticipantRegistryResponse participantRegistryResponse =
        new ParticipantRegistryResponse(
            MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS, participantRegistryDetail);
    Long totalParticipantStudyCount = participantStudyRepository.countbyStudyId(study.getId());
    participantRegistryResponse.setTotalParticipantCount(totalParticipantStudyCount);

    auditRequest.setStudyId(study.getId());
    auditRequest.setAppId(app.getId());
    participantManagerHelper.logEvent(STUDY_PARTICIPANT_REGISTRY_VIEWED, auditRequest);

    logger.exit(String.format("message=%s", participantRegistryResponse.getMessage()));
    return participantRegistryResponse;
  }
}
