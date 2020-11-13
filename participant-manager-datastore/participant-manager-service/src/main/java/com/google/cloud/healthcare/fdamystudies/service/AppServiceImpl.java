/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

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
import com.google.cloud.healthcare.fdamystudies.model.AppStudyInfo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
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

  @Autowired private ParticipantStudyRepository participantStudiesRepository;

  @Autowired private UserRegAdminRepository userRegAdminRepository;

  @Autowired private AppRepository appRepository;

  @Autowired private StudyRepository studyRepository;

  @Autowired private ParticipantManagerAuditLogHelper participantManagerHelper;

  @Override
  @Transactional(readOnly = true)
  public AppResponse getApps(String userId) {
    logger.entry("getApps(userId)");

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!(optUserRegAdminEntity.isPresent())) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      AppResponse appResponse = getAppsForSuperAdmin(optUserRegAdminEntity.get());
      logger.exit(String.format("total apps for superadmin=%d", appResponse.getApps().size()));
      return appResponse;
    }

    List<AppStudyInfo> appStudyInfoList = appRepository.findAppsByUserId(userId);
    if (CollectionUtils.isEmpty(appStudyInfoList)) {
      throw new ErrorCodeException(ErrorCode.APP_NOT_FOUND);
    }

    List<String> appIds =
        appStudyInfoList.stream().map(AppStudyInfo::getAppId).collect(Collectors.toList());

    Map<String, AppPermissionEntity> appPermissionsByAppInfoId =
        getAppPermissionsMap(userId, appIds);

    List<AppCount> appUserCount = userDetailsRepository.findAppUsersCount(appIds);

    Map<String, Long> appIdbyUsersCount =
        appUserCount.stream().collect(Collectors.toMap(AppCount::getAppId, AppCount::getCount));

    List<AppCount> appEnrolledCountList = appRepository.findEnrolledCountByAppId(userId);
    Map<String, AppCount> appEnrolledCountMap =
        appEnrolledCountList
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> appInvitedCountList = appRepository.findInvitedCountByAppId(userId);
    Map<String, AppCount> appInvitedCountMap =
        appInvitedCountList
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> appEnrolledWithoutTarget = appRepository.findEnrolledWithoutTarget(userId);
    Map<String, AppCount> appEnrolledWithoutTargetMap =
        appEnrolledWithoutTarget
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    return prepareAppResponse(
        appStudyInfoList,
        appPermissionsByAppInfoId,
        appIdbyUsersCount,
        appInvitedCountMap,
        appEnrolledCountMap,
        appEnrolledWithoutTargetMap,
        optUserRegAdminEntity.get());
  }

  private AppResponse getAppsForSuperAdmin(UserRegAdminEntity userRegAdminEntity) {
    List<AppCount> appUsersCountList = userDetailsRepository.findAppUsersCount();
    Map<String, AppCount> appUsersCountMap =
        appUsersCountList
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> studiesList = studyRepository.findAppStudiesCount();
    Map<String, AppCount> appStudiesCountMap =
        studiesList.stream().collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> appInvitedCountList = appRepository.findInvitedCountByAppId();
    Map<String, AppCount> appInvitedCountMap =
        appInvitedCountList
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> appEnrolledCountList = appRepository.findEnrolledCountByAppId();
    Map<String, AppCount> appEnrolledCountMap =
        appEnrolledCountList
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppCount> appEnrolledWithoutTarget = appRepository.findEnrolledWithoutTarget();
    Map<String, AppCount> appEnrolledWithoutTargetMap =
        appEnrolledWithoutTarget
            .stream()
            .collect(Collectors.toMap(AppCount::getAppId, Function.identity()));

    List<AppEntity> apps = appRepository.findAll();
    List<AppDetails> appDetailsList = new ArrayList<>();
    for (AppEntity app : apps) {
      AppDetails appDetails = AppMapper.toAppDetails(app);
      Long usersCount =
          appUsersCountMap.containsKey(app.getId())
              ? appUsersCountMap.get(app.getId()).getCount()
              : 0L;
      appDetails.setAppUsersCount(usersCount);

      Long studiesCount =
          appStudiesCountMap.containsKey(app.getId())
              ? appStudiesCountMap.get(app.getId()).getCount()
              : 0L;
      appDetails.setStudiesCount(studiesCount);
      appDetails.setPermission(Permission.EDIT.value());
      Long enrolledCount = getCount(appEnrolledCountMap, app.getId());
      Long invitedCount = getCount(appInvitedCountMap, app.getId());
      appDetails.setEnrolledCount(enrolledCount);
      appDetails.setInvitedCount(invitedCount);
      if (appEnrolledWithoutTargetMap.containsKey(appDetails.getId())) {
        enrolledCount =
            enrolledCount - appEnrolledWithoutTargetMap.get(appDetails.getId()).getCount();
      }

      if (invitedCount != 0) {
        double percentage = (Double.valueOf(enrolledCount) * 100) / Double.valueOf(invitedCount);
        appDetails.setEnrollmentPercentage(percentage);
      }
      appDetailsList.add(appDetails);
    }
    return new AppResponse(
        MessageCode.GET_APPS_SUCCESS, appDetailsList, userRegAdminEntity.isSuperAdmin());
  }

  private Long getCount(Map<String, AppCount> map, String appId) {
    if (map.containsKey(appId)) {
      return map.get(appId).getCount();
    }
    return 0L;
  }

  private AppResponse prepareAppResponse(
      List<AppStudyInfo> appStudyInfoList,
      Map<String, AppPermissionEntity> appPermissionsByAppInfoId,
      Map<String, Long> appIdbyUsersCount,
      Map<String, AppCount> siteWithInvitedParticipantCountMap,
      Map<String, AppCount> siteWithEnrolledParticipantCountMap,
      Map<String, AppCount> appEnrolledWithoutTargetMap,
      UserRegAdminEntity userRegAdminEntity) {
    List<AppDetails> apps = new ArrayList<>();
    for (AppStudyInfo appStudyInfo : appStudyInfoList) {
      AppDetails appDetails = new AppDetails();
      appDetails.setId(appStudyInfo.getAppId());
      appDetails.setCustomId(appStudyInfo.getCustomAppId());
      appDetails.setStudiesCount(appStudyInfo.getStudyCount());
      appDetails.setName(appStudyInfo.getAppName());
      appDetails.setAppUsersCount(appIdbyUsersCount.get(appStudyInfo.getAppId()));

      if (appPermissionsByAppInfoId.get(appStudyInfo.getAppId()) != null) {
        Integer appEditPermission =
            appPermissionsByAppInfoId.get(appStudyInfo.getAppId()).getEdit().value();
        appDetails.setPermission(
            appEditPermission == Permission.NO_PERMISSION.value()
                ? Permission.VIEW.value()
                : Permission.EDIT.value());
      }

      calculateEnrollmentPercentage(
          appDetails,
          siteWithInvitedParticipantCountMap,
          siteWithEnrolledParticipantCountMap,
          appEnrolledWithoutTargetMap);
      apps.add(appDetails);
    }

    LongSummaryStatistics studyPermissioinCount =
        appStudyInfoList.stream().mapToLong(AppStudyInfo::getStudyCount).summaryStatistics();

    AppResponse appResponse =
        new AppResponse(
            MessageCode.GET_APPS_SUCCESS,
            apps,
            studyPermissioinCount.getSum(),
            userRegAdminEntity.isSuperAdmin());
    logger.exit(String.format("total apps=%d", appResponse.getApps().size()));
    return appResponse;
  }

  private void calculateEnrollmentPercentage(
      AppDetails appDetails,
      Map<String, AppCount> siteWithInvitedParticipantCountMap,
      Map<String, AppCount> siteWithEnrolledParticipantCountMap,
      Map<String, AppCount> appEnrolledWithoutTargetMap) {
    long appInvitedCount = getCount(siteWithInvitedParticipantCountMap, appDetails.getId());
    long appEnrolledCount = getCount(siteWithEnrolledParticipantCountMap, appDetails.getId());
    appDetails.setEnrolledCount(appEnrolledCount);
    appDetails.setInvitedCount(appInvitedCount);

    if (appEnrolledWithoutTargetMap.containsKey(appDetails.getId())) {
      appEnrolledCount =
          appEnrolledCount - appEnrolledWithoutTargetMap.get(appDetails.getId()).getCount();
    }

    if (appInvitedCount != 0) {
      double percentage =
          (Double.valueOf(appEnrolledCount) * 100) / Double.valueOf(appInvitedCount);
      appDetails.setEnrollmentPercentage(percentage);
    }
  }

  private Map<String, AppPermissionEntity> getAppPermissionsMap(
      String userId, List<String> appIds) {
    Map<String, AppPermissionEntity> appPermissionsByAppInfoId = new HashMap<>();

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

  @Override
  @Transactional(readOnly = true)
  public AppResponse getAppsWithOptionalFields(String userId, String[] fields) {
    logger.entry("getAppsWithOptionalFields(userId,fields)");

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);

    if (!(optUserRegAdminEntity.isPresent() && optUserRegAdminEntity.get().isSuperAdmin())) {
      throw new ErrorCodeException(ErrorCode.NOT_SUPER_ADMIN_ACCESS);
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
      String appId, String adminId, AuditLogEventRequest auditRequest, String[] excludeSiteStatus) {
    logger.entry("getAppParticipants(appId, adminId)");
    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(adminId);
    if (!optUserRegAdminEntity.isPresent()) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    AppEntity app = null;
    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      Optional<AppEntity> optAppEntity = appRepository.findById(appId);
      app = optAppEntity.orElseThrow(() -> new ErrorCodeException(ErrorCode.APP_NOT_FOUND));
    } else {
      Optional<AppPermissionEntity> optAppPermissionEntity =
          appPermissionRepository.findByUserIdAndAppId(adminId, appId);
      app =
          optAppPermissionEntity
              .orElseThrow(() -> new ErrorCodeException(ErrorCode.APP_PERMISSION_ACCESS_DENIED))
              .getApp();
    }

    List<UserDetailsEntity> userDetails = userDetailsRepository.findByAppId(app.getId());
    List<StudyEntity> studyEntity = studyRepository.findByAppId(app.getId());
    List<ParticipantDetail> participants = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(userDetails)) {
      Map<String, Map<StudyEntity, List<ParticipantStudyEntity>>> participantsEnrolled =
          getEnrolledParticipants(userDetails, studyEntity);
      participants = prepareParticpantDetails(userDetails, participantsEnrolled, excludeSiteStatus);
    }

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

    List<String> studyIds =
        studyEntity.stream().distinct().map(StudyEntity::getId).collect(Collectors.toList());

    List<String> userIds =
        userDetails.stream().distinct().map(UserDetailsEntity::getId).collect(Collectors.toList());

    List<ParticipantStudyEntity> participantEnrollments =
        participantStudiesRepository.findByStudyIdsAndUserIds(studyIds, userIds);

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
          participantEnrollmentsByUserDetailsAndStudy,
      String[] excludeSiteStatus) {
    List<ParticipantDetail> participantList = new ArrayList<>();
    for (UserDetailsEntity userDetailsEntity : userDetails) {
      ParticipantDetail participant = ParticipantMapper.toParticipantDetails(userDetailsEntity);
      if (participantEnrollmentsByUserDetailsAndStudy.containsKey(userDetailsEntity.getId())) {
        Map<StudyEntity, List<ParticipantStudyEntity>> enrolledStudiesByStudyInfoId =
            participantEnrollmentsByUserDetailsAndStudy.get(userDetailsEntity.getId());
        List<AppStudyDetails> enrolledStudies =
            StudyMapper.toAppStudyDetailsList(
                enrolledStudiesByStudyInfoId, excludeSiteStatus, true);
        participant.getEnrolledStudies().addAll(enrolledStudies);
      }
      participantList.add(participant);
    }

    return participantList;
  }
}
