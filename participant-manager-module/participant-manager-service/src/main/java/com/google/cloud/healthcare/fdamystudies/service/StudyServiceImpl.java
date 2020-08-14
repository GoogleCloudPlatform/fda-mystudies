/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.CLOSE_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.OPEN_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.READ_AND_EDIT_PERMISSION;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.READ_PERMISSION;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.VIEW_VALUE;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.StudyResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Override
  @Transactional(readOnly = true)
  public StudyResponse getStudies(String userId) {
    logger.entry("getStudies(String userId)");

    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);

    if (CollectionUtils.isEmpty(sitePermissions)) {
      logger.exit(ErrorCode.STUDY_NOT_FOUND);
      return new StudyResponse(ErrorCode.STUDY_NOT_FOUND);
    }

    Map<StudyEntity, List<SitePermissionEntity>> studyPermissionMap =
        sitePermissions.stream().collect(Collectors.groupingBy(SitePermissionEntity::getStudy));

    List<String> usersStudyIds =
        sitePermissions
            .stream()
            .distinct()
            .map(studyEntity -> studyEntity.getStudy().getId())
            .collect(Collectors.toList());

    Map<String, StudyPermissionEntity> studyPermissionsByStudyInfoId =
        getStudyPermissionsByStudyInfoId(userId, usersStudyIds);

    List<String> usersSiteIds =
        sitePermissions
            .stream()
            .map(s -> s.getSite().getId())
            .distinct()
            .collect(Collectors.toList());

    Map<String, Long> siteWithInvitedParticipantCountMap =
        getSiteWithInvitedParticipantCountMap(usersSiteIds);

    Map<String, Long> siteWithEnrolledParticipantCountMap =
        getSiteWithEnrolledParticipantCountMap(usersSiteIds);

    return prepareStudyResponse(
        sitePermissions,
        studyPermissionsByStudyInfoId,
        studyPermissionMap,
        siteWithInvitedParticipantCountMap,
        siteWithEnrolledParticipantCountMap);
  }

  private Map<String, StudyPermissionEntity> getStudyPermissionsByStudyInfoId(
      String userId, List<String> usersStudyIds) {
    List<StudyPermissionEntity> studyPermissions =
        studyPermissionRepository.findStudyPermissionsOfUserByStudyIds(usersStudyIds, userId);

    Map<String, StudyPermissionEntity> studyPermissionsByStudyInfoId = new HashMap<>();
    if (CollectionUtils.isNotEmpty(studyPermissions)) {
      studyPermissionsByStudyInfoId =
          studyPermissions
              .stream()
              .collect(Collectors.toMap(e -> e.getStudy().getId(), Function.identity()));
    }
    return studyPermissionsByStudyInfoId;
  }

  private StudyResponse prepareStudyResponse(
      List<SitePermissionEntity> sitePermissions,
      Map<String, StudyPermissionEntity> studyPermissionsByStudyInfoId,
      Map<StudyEntity, List<SitePermissionEntity>> studyPermissionMap,
      Map<String, Long> siteWithInvitedParticipantCountMap,
      Map<String, Long> siteWithEnrolledParticipantCountMap) {
    List<StudyDetails> studies = new ArrayList<>();
    for (Map.Entry<StudyEntity, List<SitePermissionEntity>> entry : studyPermissionMap.entrySet()) {
      StudyDetails studyDetail = new StudyDetails();
      StudyEntity study = entry.getKey();
      String studyId = study.getId();
      studyDetail.setId(studyId);
      studyDetail.setCustomId(study.getCustomId());
      studyDetail.setName(study.getName());
      studyDetail.setType(study.getType());
      List<SitePermissionEntity> permissions = entry.getValue();
      studyDetail.setSitesCount((long) permissions.size());

      if (studyPermissionsByStudyInfoId.get(studyId) != null) {
        Integer studyEditPermission =
            studyPermissionsByStudyInfoId.get(study.getId()).getEditPermission();
        studyDetail.setStudyPermission(
            studyEditPermission == VIEW_VALUE ? READ_PERMISSION : READ_AND_EDIT_PERMISSION);
        studyDetail.setStudyPermission(studyEditPermission);
      }

      calculateEnrollmentPercentage(
          siteWithInvitedParticipantCountMap,
          siteWithEnrolledParticipantCountMap,
          entry,
          studyDetail);
      studies.add(studyDetail);
    }

    StudyResponse studyResponse =
        new StudyResponse(MessageCode.GET_STUDIES_SUCCESS, studies, sitePermissions.size());
    logger.exit(String.format("total studies=%d", studyResponse.getStudies().size()));
    return studyResponse;
  }

  private void calculateEnrollmentPercentage(
      Map<String, Long> siteWithInvitedParticipantCountMap,
      Map<String, Long> siteWithEnrolledParticipantCountMap,
      Map.Entry<StudyEntity, List<SitePermissionEntity>> entry,
      StudyDetails studyDetail) {
    Long studyInvitedCount = 0L;
    Long studyEnrolledCount = 0L;
    for (SitePermissionEntity sitePermission : entry.getValue()) {
      studyInvitedCount =
          getStudyInvitedCount(
              siteWithInvitedParticipantCountMap, entry, studyInvitedCount, sitePermission);

      studyEnrolledCount +=
          siteWithEnrolledParticipantCountMap.get(sitePermission.getSite().getId());
    }

    studyDetail.setEnrolled(studyEnrolledCount);
    studyDetail.setInvited(studyInvitedCount);
    if (studyDetail.getInvited() != 0 && studyDetail.getInvited() >= studyDetail.getEnrolled()) {
      Double percentage =
          (Double.valueOf(studyDetail.getEnrolled()) * 100)
              / Double.valueOf(studyDetail.getInvited());
      studyDetail.setEnrollmentPercentage(percentage);
    }
  }

  private Long getStudyInvitedCount(
      Map<String, Long> siteWithInvitedParticipantCountMap,
      Map.Entry<StudyEntity, List<SitePermissionEntity>> entry,
      Long studyInvitedCount,
      SitePermissionEntity sitePermission) {
    String siteId = sitePermission.getSite().getId();
    String studyType = entry.getKey().getType();
    if (siteWithInvitedParticipantCountMap.get(siteId) != null && studyType.equals(CLOSE_STUDY)) {
      studyInvitedCount += siteWithInvitedParticipantCountMap.get(siteId);
    }

    if (sitePermission.getSite().getTargetEnrollment() != null && studyType.equals(OPEN_STUDY)) {
      studyInvitedCount += sitePermission.getSite().getTargetEnrollment();
    }
    return studyInvitedCount;
  }

  private Map<String, Long> getSiteWithEnrolledParticipantCountMap(List<String> usersSiteIds) {
    List<ParticipantStudyEntity> participantsEnrollments =
        participantStudyRepository.findParticipantEnrollmentsBySiteIds(usersSiteIds);

    return participantsEnrollments
        .stream()
        .collect(Collectors.groupingBy(e -> e.getSite().getId(), Collectors.counting()));
  }

  private Map<String, Long> getSiteWithInvitedParticipantCountMap(List<String> usersSiteIds) {
    List<ParticipantRegistrySiteEntity> participantRegistry =
        participantRegistrySiteRepository.findBySiteIds(usersSiteIds);

    return participantRegistry
        .stream()
        .collect(
            Collectors.groupingBy(
                e -> e.getSite().getId(),
                Collectors.summingLong(ParticipantRegistrySiteEntity::getInvitationCount)));
  }

  @Override
  public ParticipantRegistryResponse getStudyParticipants(String userId, String studyId) {
    logger.entry("getStudyParticipants(String userId, String studyId)");
    // validations
    Optional<StudyEntity> optStudy = studyRepository.findById(studyId);
    if (!optStudy.isPresent()) {
      logger.exit(ErrorCode.STUDY_NOT_FOUND);
      return new ParticipantRegistryResponse(ErrorCode.STUDY_NOT_FOUND);
    }

    Optional<StudyPermissionEntity> optStudyPermission =
        studyPermissionRepository.findByStudyIdAndUserId(studyId, userId);

    if (!optStudyPermission.isPresent()) {
      logger.exit(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED);
      return new ParticipantRegistryResponse(ErrorCode.STUDY_PERMISSION_ACCESS_DENIED);
    }

    StudyPermissionEntity studyPermission = optStudyPermission.get();

    if (studyPermission.getAppInfo() == null) {
      logger.exit(ErrorCode.APP_NOT_FOUND);
      return new ParticipantRegistryResponse(ErrorCode.APP_NOT_FOUND);
    }

    Optional<AppEntity> optApp =
        appRepository.findById(optStudyPermission.get().getAppInfo().getId());

    return prepareRegistryParticipantResponse(optStudy.get(), optApp.get());
  }

  private ParticipantRegistryResponse prepareRegistryParticipantResponse(
      StudyEntity study, AppEntity app) {
    ParticipantRegistryDetail participantRegistryDetail =
        ParticipantMapper.fromStudyAndApp(study, app);

    if (OPEN_STUDY.equalsIgnoreCase(study.getType())) {
      Optional<SiteEntity> optSiteEntity =
          siteRepository.findByStudyIdAndType(study.getId(), study.getType());
      if (optSiteEntity.isPresent()) {
        participantRegistryDetail.setTargetEnrollment(optSiteEntity.get().getTargetEnrollment());
      }
    }

    List<ParticipantStudyEntity> participantStudiesList =
        participantStudyRepository.findParticipantsByStudy(study.getId());
    List<ParticipantDetail> registryParticipants = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(participantStudiesList)) {
      for (ParticipantStudyEntity participantStudy : participantStudiesList) {
        ParticipantDetail participantDetail =
            ParticipantMapper.fromParticipantStudy(participantStudy);

        String onboardingStatusCode =
            participantStudy.getParticipantRegistrySite().getOnboardingStatus();
        onboardingStatusCode =
            StringUtils.defaultString(onboardingStatusCode, OnboardingStatus.DISABLED.getCode());
        participantDetail.setOnboardingStatus(
            OnboardingStatus.fromCode(onboardingStatusCode).getStatus());

        registryParticipants.add(participantDetail);
      }
    }

    participantRegistryDetail.setRegistryParticipants(registryParticipants);

    ParticipantRegistryResponse participantRegistryResponse =
        new ParticipantRegistryResponse(
            MessageCode.GET_PARTICIPANT_REGISTRY_SUCCESS, participantRegistryDetail);

    logger.exit(String.format("message=%s", participantRegistryResponse.getMessage()));
    return participantRegistryResponse;
  }
}
