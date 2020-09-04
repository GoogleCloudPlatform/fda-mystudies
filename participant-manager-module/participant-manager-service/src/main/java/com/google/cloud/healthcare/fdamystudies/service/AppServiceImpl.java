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
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.APP_PARTICIPANT_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AppDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppParticipantsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AppMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.StudyMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantRegistrySiteRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppServiceImpl implements AppService {
  private XLogger logger = XLoggerFactory.getXLogger(AppServiceImpl.class.getName());

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private UserDetailsRepository userDetailsRepository;

  @Autowired private ParticipantRegistrySiteRepository participantRegistrySiteRepository;

  @Autowired private ParticipantStudyRepository participantStudiesRepository;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional(readOnly = true)
  public AppResponse getApps(String userId) {
    logger.entry("getApps(userId)");

    List<SitePermissionEntity> sitePermissions =
        sitePermissionRepository.findSitePermissionByUserId(userId);
    if (CollectionUtils.isEmpty(sitePermissions)) {
      throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
    }

    List<String> appIds = getAppIds(sitePermissions);

    Map<String, AppPermissionEntity> appPermissionsByAppInfoId =
        getAppPermissionsMap(userId, appIds);

    List<AppCount> appUserCount = userDetailsRepository.findAppUsersCount(appIds);

    Map<String, Long> appIdbyUsersCount =
        appUserCount.stream().collect(Collectors.toMap(AppCount::getAppId, AppCount::getCount));

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

      if (appPermissionsByAppInfoId.get(app.getId()) != null) {
        Integer appEditPermission = appPermissionsByAppInfoId.get(app.getId()).getEdit().value();
        appDetails.setPermission(
            appEditPermission == Permission.NO_PERMISSION.value()
                ? Permission.VIEW.value()
                : Permission.EDIT.value());
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

        if (sitePermission.getSite().getTargetEnrollment() != null
            && OPEN_STUDY.equals(studyType)) {
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
                SitePermissionEntity::getApp,
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
              .collect(Collectors.toMap(e -> e.getApp().getId(), Function.identity()));
    }
    return appPermissionsByAppInfoId;
  }

  private List<String> getAppIds(List<SitePermissionEntity> sitePermissions) {
    return sitePermissions
        .stream()
        .map(appInfoDetailsbo -> appInfoDetailsbo.getApp().getId())
        .distinct()
        .collect(Collectors.toList());
  }

  @Override
  @Transactional(readOnly = true)
  public AppResponse getAppsWithOptionalFields(String userId, String[] fields) {
    logger.entry("getAppsWithOptionalFields(userId,fields)");

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);

    if (!(optUserRegAdminEntity.isPresent() && optUserRegAdminEntity.get().isSuperAdmin())) {
      throw new ErrorCodeException(ErrorCode.USER_ADMIN_ACCESS_DENIED);
    }

    List<AppEntity> apps = appRepository.findAll();

    List<StudyEntity> studies = new ArrayList<>();
    apps.stream().map(AppEntity::getStudies).forEach(studies::addAll);

    List<SiteEntity> sites = new ArrayList<>();
    studies.stream().map(StudyEntity::getSites).forEach(sites::addAll);

    AppResponse appResponse = prepareAppResponse(apps, studies, sites, fields);

    logger.exit(String.format("total apps=%d", appResponse.getApps().size()));
    return appResponse;
  }

  private AppResponse prepareAppResponse(
      List<AppEntity> apps, List<StudyEntity> studies, List<SiteEntity> sites, String[] fields) {
    Map<String, List<StudyEntity>> groupByAppIdStudyMap =
        studies.stream().collect(Collectors.groupingBy(StudyEntity::getAppId));

    Map<String, List<SiteEntity>> groupByStudyIdSiteMap =
        sites.stream().collect(Collectors.groupingBy(SiteEntity::getStudyId));

    List<AppDetails> appsList = new ArrayList<>();
    for (AppEntity app : apps) {
      AppDetails appDetails = AppMapper.toAppDetails(app);
      if (ArrayUtils.contains(fields, "studies")) {
        List<StudyEntity> appStudies = groupByAppIdStudyMap.get(app.getId());
        List<AppStudyResponse> appStudyResponses =
            CollectionUtils.emptyIfNull(appStudies)
                .stream()
                .map(
                    study ->
                        StudyMapper.toAppStudyResponse(
                            study, groupByStudyIdSiteMap.get(study.getId()), fields))
                .collect(Collectors.toList());

        appDetails.getStudies().addAll(appStudyResponses);
      }
      int totalSitesCount =
          appDetails
              .getStudies()
              .stream()
              .map(study -> study.getSites().size())
              .reduce(0, Integer::sum);
      appDetails.setTotalSitesCount(totalSitesCount);

      appsList.add(appDetails);
    }

    return new AppResponse(MessageCode.GET_APPS_DETAILS_SUCCESS, appsList);
  }

  @Override
  @Transactional(readOnly = true)
  public AppParticipantsResponse getAppParticipants(
      String appId, String adminId, AuditLogEventRequest auditRequest) {
    logger.entry("getAppParticipants(appId, adminId)");
    Optional<AppPermissionEntity> optAppPermissionEntity =
        appPermissionRepository.findByUserIdAndAppId(adminId, appId);

    if (!optAppPermissionEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
    }

    AppPermissionEntity appPermission = optAppPermissionEntity.get();

    AppEntity app = appPermission.getApp();

    List<UserDetailsEntity> userDetails = userDetailsRepository.findByAppId(app.getId());
    List<StudyEntity> studyEntity = studyRepository.findByAppId(app.getId());

    Map<String, Map<StudyEntity, List<ParticipantStudyEntity>>>
        participantEnrollmentsByUserDetailsAndStudy =
            getEnrolledParticipants(userDetails, studyEntity);

    List<ParticipantDetail> participants =
        prepareParticpantDetails(userDetails, participantEnrollmentsByUserDetailsAndStudy);

    AppParticipantsResponse appParticipantsResponse =
        new AppParticipantsResponse(
            MessageCode.GET_APP_PARTICIPANTS_SUCCESS,
            app.getId(),
            app.getAppId(),
            app.getAppName());
    appParticipantsResponse.getParticipants().addAll(participants);

    auditRequest.setAppId(appId);
    auditRequest.setUserId(adminId);
    participantManagerHelper.logEvent(APP_PARTICIPANT_REGISTRY_VIEWED, auditRequest);

    logger.exit(String.format("%d participant found for appId=%s", participants.size(), appId));
    return appParticipantsResponse;
  }

  private Map<String, Map<StudyEntity, List<ParticipantStudyEntity>>> getEnrolledParticipants(
      List<UserDetailsEntity> userDetails, List<StudyEntity> studyEntity) {

    List<String> appsStudyIds =
        studyEntity.stream().distinct().map(StudyEntity::getId).collect(Collectors.toList());

    List<String> userDetailsIds =
        userDetails.stream().distinct().map(UserDetailsEntity::getId).collect(Collectors.toList());

    List<ParticipantStudyEntity> participantEnrollments =
        participantStudiesRepository.findByAppIdAndUserId(appsStudyIds, userDetailsIds);

    return participantEnrollments
        .stream()
        .collect(
            Collectors.groupingBy(
                ParticipantStudyEntity::getUserDetailsId,
                Collectors.groupingBy(ParticipantStudyEntity::getStudy)));
  }

  private List<ParticipantDetail> prepareParticpantDetails(
      List<UserDetailsEntity> userDetails,
      Map<String, Map<StudyEntity, List<ParticipantStudyEntity>>>
          participantEnrollmentsByUserDetailsAndStudy) {
    List<ParticipantDetail> participantList = new ArrayList<>();
    for (UserDetailsEntity userDetailsEntity : userDetails) {
      ParticipantDetail participant = ParticipantMapper.toParticipantDetails(userDetailsEntity);
      if (participantEnrollmentsByUserDetailsAndStudy.containsKey(userDetailsEntity.getId())) {
        Map<StudyEntity, List<ParticipantStudyEntity>> enrolledStudiesByStudyInfoId =
            participantEnrollmentsByUserDetailsAndStudy.get(userDetailsEntity.getId());
        AppStudyDetails enrolledStudy = StudyMapper.toAppStudyDetails(enrolledStudiesByStudyInfoId);
        participant.getEnrolledStudies().add(enrolledStudy);
      }
      participantList.add(participant);
    }

    return participantList;
  }
}
