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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.cloud.healthcare.fdamystudies.beans.AppDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;

@Service
public class AppServiceImpl implements AppService {
  private XLogger logger = XLoggerFactory.getXLogger(AppServiceImpl.class.getName());

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantStudyRepository participantStudiesRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Override
  @Transactional(readOnly = true)
  public AppResponse getApps(String userId) {
    logger.entry("getApps(userId)");

    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);
    if (CollectionUtils.isEmpty(sitePermissions)) {
      logger.exit(ErrorCode.APP_NOT_FOUND);
      return new AppResponse(ErrorCode.APP_NOT_FOUND);
    }

    List<String> appIds = getAppIds(sitePermissions);

    Map<String, AppPermissionEntity> appPermissionsByAppInfoId =
        getAppPermissionsMap(userId, appIds);

    Map<String, Long> appIdbyUsersCount = userDetailsRepository.findAppUsersCount(appIds);

    Map<AppEntity, Map<StudyEntity, List<SitePermissionEntity>>>
        sitePermissionByAppInfoAndStudyInfo = getPermissionByAppInfoAndStudyInfo(sitePermissions);

    List<String> usersSiteIds = getUserSiteIds(sitePermissions);

    List<ParticipantRegistrySiteEntity> participantRegistry =
        participantRegistrySiteRepository.findBySiteIds(usersSiteIds);

    Map<String, Long> siteWithInvitedParticipantCountMap =
        getSiteWithInvitedParticipantCountMap(participantRegistry);

    List<ParticipantStudyEntity> participantsEnrollments =
        participantStudiesRepository.findParticipantEnrollmentsBySiteIds(usersSiteIds);

    Map<String, Long> siteWithEnrolledParticipantCountMap =
        participantsEnrollments
            .stream()
            .collect(Collectors.groupingBy(e -> e.getSite().getId(), Collectors.counting()));

    return prepareAppResponse(
        sitePermissions,
        appPermissionsByAppInfoId,
        appIdbyUsersCount,
        sitePermissionByAppInfoAndStudyInfo,
        siteWithInvitedParticipantCountMap,
        siteWithEnrolledParticipantCountMap);
  }

  private AppResponse prepareAppResponse(
      List<SitePermissionEntity> sitePermissions,
      Map<String, AppPermissionEntity> appPermissionsByAppInfoId,
      Map<String, Long> appIdbyUsersCount,
      Map<AppEntity, Map<StudyEntity, List<SitePermissionEntity>>>
          sitePermissionByAppInfoAndStudyInfo,
      Map<String, Long> siteWithInvitedParticipantCountMap,
      Map<String, Long> siteWithEnrolledParticipantCountMap) {
    List<AppDetails> apps = new ArrayList<>();
    for (Map.Entry<AppEntity, Map<StudyEntity, List<SitePermissionEntity>>> entry :
        sitePermissionByAppInfoAndStudyInfo.entrySet()) {
      AppEntity app = entry.getKey();
      AppDetails appDetails = new AppDetails();
      appDetails.setId(app.getId());
      appDetails.setCustomId(app.getAppId());
      appDetails.setStudiesCount((long) entry.getValue().size());
      appDetails.setName(app.getAppName());
      appDetails.setAppUsersCount(appIdbyUsersCount.get(app.getId()));
      appDetails.setAppUsersCount(0L);

      if (appPermissionsByAppInfoId.get(app.getId()) != null) {
        Integer appEditPermission = appPermissionsByAppInfoId.get(app.getId()).getEdit();
        appDetails.setAppPermission(
            appEditPermission == VIEW_VALUE ? READ_PERMISSION : READ_AND_EDIT_PERMISSION);
      }

      calculateEnrollmentPercentage(
          appDetails,
          siteWithInvitedParticipantCountMap,
          siteWithEnrolledParticipantCountMap,
          entry);
      apps.add(appDetails);
    }

    Map<StudyEntity, List<SitePermissionEntity>> studyPermissionMap =
        sitePermissions.stream().collect(Collectors.groupingBy(SitePermissionEntity::getStudy));

    AppResponse appResponse =
        new AppResponse(MessageCode.GET_APPS_SUCCESS, apps, studyPermissionMap.size());
    logger.exit(String.format("total apps=%d", appResponse.getApps().size()));
    return appResponse;
  }

  private void calculateEnrollmentPercentage(
      AppDetails appDetails,
      Map<String, Long> siteWithInvitedParticipantCountMap,
      Map<String, Long> siteWithEnrolledParticipantCountMap,
      Map.Entry<AppEntity, Map<StudyEntity, List<SitePermissionEntity>>> entry) {
    long appInvitedCount = 0L;
    long appEnrolledCount = 0L;
    for (Map.Entry<StudyEntity, List<SitePermissionEntity>> studyEntry :
        entry.getValue().entrySet()) {
      String studyType = studyEntry.getKey().getType();
      for (SitePermissionEntity sitePermission : studyEntry.getValue()) {
        String siteId = sitePermission.getSite().getId();
        if (siteWithInvitedParticipantCountMap.get(siteId) != null
            && CLOSE_STUDY.equals(studyType)) {
          appInvitedCount += siteWithInvitedParticipantCountMap.get(siteId);
        }

        if (OPEN_STUDY.equals(studyType)) {
          appInvitedCount += sitePermission.getSite().getTargetEnrollment();
        }

        if (siteWithEnrolledParticipantCountMap.get(siteId) != null) {
          appEnrolledCount += siteWithEnrolledParticipantCountMap.get(siteId);
        }
      }
    }
    appDetails.setEnrolledCount(appEnrolledCount);
    appDetails.setInvitedCount(appInvitedCount);
    double percentage = 0;
    if (appDetails.getInvitedCount() != 0
        && appDetails.getInvitedCount() >= appDetails.getEnrolledCount()) {
      percentage =
          (Double.valueOf(appDetails.getEnrolledCount()) * 100)
              / Double.valueOf(appDetails.getInvitedCount());
      appDetails.setEnrollmentPercentage(percentage);
    }
  }

  private Map<String, Long> getSiteWithInvitedParticipantCountMap(
      List<ParticipantRegistrySiteEntity> participantRegistry) {
    return participantRegistry
        .stream()
        .collect(
            Collectors.groupingBy(
                e -> e.getSite().getId(),
                Collectors.summingLong(ParticipantRegistrySiteEntity::getInvitationCount)));
  }

  private List<String> getUserSiteIds(List<SitePermissionEntity> sitePermissions) {
    return sitePermissions
        .stream()
        .map(s -> s.getSite().getId())
        .distinct()
        .collect(Collectors.toList());
  }

  private Map<AppEntity, Map<StudyEntity, List<SitePermissionEntity>>>
      getPermissionByAppInfoAndStudyInfo(List<SitePermissionEntity> sitePermissions) {
    return sitePermissions
        .stream()
        .collect(
            Collectors.groupingBy(
                SitePermissionEntity::getAppInfo,
                Collectors.groupingBy(SitePermissionEntity::getStudy)));
  }

  private Map<String, AppPermissionEntity> getAppPermissionsMap(
      String userId, List<String> appIds) {
    Map<String, AppPermissionEntity> appPermissionsByAppInfoId = null;

    List<AppPermissionEntity> appPermissions =
        appPermissionRepository.findAppPermissionsOfUserByAppIds(appIds, userId);

    if (CollectionUtils.isNotEmpty(appPermissions)) {
      appPermissionsByAppInfoId =
          appPermissions
              .stream()
              .collect(Collectors.toMap(e -> e.getAppInfo().getId(), Function.identity()));
    }
    return appPermissionsByAppInfoId;
  }

  private List<String> getAppIds(List<SitePermissionEntity> sitePermissions) {
    return sitePermissions
        .stream()
        .map(appInfoDetailsbo -> appInfoDetailsbo.getAppInfo().getId())
        .distinct()
        .collect(Collectors.toList());
  }
}
