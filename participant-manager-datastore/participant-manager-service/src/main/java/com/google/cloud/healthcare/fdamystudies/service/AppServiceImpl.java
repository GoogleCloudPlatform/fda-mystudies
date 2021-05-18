/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.APP_PARTICIPANT_REGISTRY_VIEWED;

import com.google.cloud.healthcare.fdamystudies.beans.AppDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppParticipantsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppSiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerAuditLogHelper;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.mapper.AppMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.ParticipantMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.SiteMapper;
import com.google.cloud.healthcare.fdamystudies.mapper.StudyMapper;
import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppStudyInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppStudySiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistory;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantEnrollmentHistoryRepository;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserDetailsRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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

  @Autowired private ParticipantEnrollmentHistoryRepository participantEnrollmentHistoryRepository;

  @Override
  @Transactional(readOnly = true)
  public AppResponse getApps(String userId, Integer limit, Integer offset, String searchTerm) {
    logger.entry("getApps(userId)");

    Optional<UserRegAdminEntity> optUserRegAdminEntity = userRegAdminRepository.findById(userId);
    if (!(optUserRegAdminEntity.isPresent())) {
      throw new ErrorCodeException(ErrorCode.USER_NOT_FOUND);
    }

    if (optUserRegAdminEntity.get().isSuperAdmin()) {
      AppResponse appResponse =
          getAppsForSuperAdmin(
              optUserRegAdminEntity.get(), limit, offset, StringUtils.defaultString(searchTerm));
      logger.exit(String.format("total apps for superadmin=%d", appResponse.getApps().size()));
      return appResponse;
    }

    List<AppStudyInfo> appStudyInfoList =
        appRepository.findAppsByUserId(
            userId, limit, offset, StringUtils.defaultString(searchTerm));
    if (CollectionUtils.isEmpty(appStudyInfoList)) {
      return new AppResponse(
          MessageCode.GET_APPS_SUCCESS,
          new ArrayList<>(),
          optUserRegAdminEntity.get().isSuperAdmin());
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

  private AppResponse getAppsForSuperAdmin(
      UserRegAdminEntity userRegAdminEntity, Integer limit, Integer offset, String searchTerm) {
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

    List<AppEntity> apps = appRepository.findAll(limit, offset, searchTerm);

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

      if (invitedCount != null && invitedCount != 0) {
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

    List<AppStudySiteInfo> appStudySiteInfoList = appRepository.findAppsStudiesSites();

    Map<String, AppDetails> appsMap = new HashMap<>();
    Map<String, AppStudyResponse> studiesMap = new HashMap<>();
    Map<String, AppSiteResponse> sitesMap = new HashMap<>();

    AppDetails appDetails = null;
    for (AppStudySiteInfo appStudySiteInfo : appStudySiteInfoList) {
      if (!appsMap.containsKey(appStudySiteInfo.getAppId())) {
        appDetails = AppMapper.toAppDetails(appStudySiteInfo);
        appsMap.put(appStudySiteInfo.getAppId(), appDetails);
      }
      appDetails = appsMap.get(appStudySiteInfo.getAppId());

      AppStudyResponse appStudyResponse = null;
      if (!studiesMap.containsKey(appStudySiteInfo.getAppStudyIdKey())
          && ArrayUtils.contains(fields, "studies")) {
        appStudyResponse = StudyMapper.toAppStudyResponse(appStudySiteInfo);
        studiesMap.put(appStudySiteInfo.getAppStudyIdKey(), appStudyResponse);
        appDetails.getStudies().add(appStudyResponse);
      }
      appStudyResponse = studiesMap.get(appStudySiteInfo.getAppStudyIdKey());

      if (StringUtils.isNotEmpty(appStudySiteInfo.getSiteId())
          && ArrayUtils.contains(fields, "sites")
          && appStudyResponse != null
          && !sitesMap.containsKey(appStudySiteInfo.getAppStudySiteIdKey())) {
        AppSiteResponse appSiteResponse = SiteMapper.toAppSiteResponse(appStudySiteInfo);
        sitesMap.put(appStudySiteInfo.getAppStudySiteIdKey(), appSiteResponse);

        appStudyResponse.getSites().add(appSiteResponse);
        appStudyResponse.setTotalSitesCount(appStudyResponse.getSites().size());
        appDetails.setTotalSitesCount(appDetails.getTotalSitesCount() + 1);
        sortSites(appStudyResponse);
      }
      sortStudies(appDetails);
    }

    List<AppDetails> apps = appsMap.values().stream().collect(Collectors.toList());
    List<AppDetails> sortedApps =
        apps.stream()
            .sorted(Comparator.comparing(AppDetails::getName))
            .collect(Collectors.toList());

    AppResponse appResponse = new AppResponse(MessageCode.GET_APPS_DETAILS_SUCCESS, sortedApps);

    logger.exit(String.format("total apps=%d", appResponse.getApps().size()));
    return appResponse;
  }

  private void sortStudies(AppDetails appDetails) {
    List<AppStudyResponse> sortedStudies =
        appDetails
            .getStudies()
            .stream()
            .sorted(Comparator.comparing(AppStudyResponse::getStudyName))
            .collect(Collectors.toList());
    appDetails.getStudies().clear();
    appDetails.getStudies().addAll(sortedStudies);
  }

  private void sortSites(AppStudyResponse appStudyResponse) {
    List<AppSiteResponse> sortedSites =
        appStudyResponse
            .getSites()
            .stream()
            .sorted(Comparator.comparing(AppSiteResponse::getLocationName))
            .collect(Collectors.toList());
    appStudyResponse.getSites().clear();
    appStudyResponse.getSites().addAll(sortedSites);
  }

  @Override
  @Transactional(readOnly = true)
  public AppParticipantsResponse getAppParticipants(
      String appId,
      String adminId,
      AuditLogEventRequest auditRequest,
      Integer limit,
      Integer offset,
      String orderByCondition,
      String searchTerm) {
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

    List<String> userIds =
        appRepository.findUserDetailIds(
            app.getId(),
            UserStatus.DEACTIVATED.getValue(),
            limit,
            offset,
            orderByCondition,
            StringUtils.defaultString(searchTerm));

    if (CollectionUtils.isEmpty(userIds)) {
      AppParticipantsResponse appParticipantsResponse =
          prepareAppParticipantResponse(appId, adminId, auditRequest, app, new ArrayList<>());

      logger.exit(String.format("No participants found for appId=%s", appId));
      return appParticipantsResponse;
    }

    List<AppParticipantsInfo> appParticipantsInfoList =
        appRepository.findUserDetailsByAppId(app.getId(), userIds, orderByCondition);

    List<ParticipantEnrollmentHistory> enrollmentHistoryEntities =
        participantEnrollmentHistoryRepository.findParticipantEnrollmentHistoryByAppId(
            app.getId(), userIds);

    Map<String, List<AppSiteDetails>> sitesByUserIdStudyIdMap = new HashMap<>();

    List<AppSiteDetails> appSites = null;
    AppSiteDetails appSiteDetails = null;
    for (ParticipantEnrollmentHistory enrollmentHistory : enrollmentHistoryEntities) {
      if (!sitesByUserIdStudyIdMap.containsKey(enrollmentHistory.getUserIdStudyIdKey())) {
        appSites = new ArrayList<>();
        sitesByUserIdStudyIdMap.put(enrollmentHistory.getUserIdStudyIdKey(), appSites);
      }
      appSites = sitesByUserIdStudyIdMap.get(enrollmentHistory.getUserIdStudyIdKey());
      appSiteDetails = prepareAppSiteDetails(enrollmentHistory);
      appSites.add(appSiteDetails);
    }

    Set<String> uniqueUserStudyIds = new HashSet<>();
    Map<String, ParticipantDetail> participantsMap = new LinkedHashMap<>();
    for (AppParticipantsInfo appParticipantsInfo : appParticipantsInfoList) {
      ParticipantDetail participantDetail =
          participantsMap.containsKey(appParticipantsInfo.getUserDetailsId())
              ? participantsMap.get(appParticipantsInfo.getUserDetailsId())
              : ParticipantMapper.toParticipantDetails(appParticipantsInfo);
      participantsMap.put(appParticipantsInfo.getUserDetailsId(), participantDetail);
      if (StringUtils.isEmpty(appParticipantsInfo.getStudyId())
          || !uniqueUserStudyIds.add(appParticipantsInfo.getUserIdStudyIdKey())) {
        continue;
      }

      AppStudyDetails appStudyDetails = StudyMapper.toAppStudyDetailsList(appParticipantsInfo);

      String userIdStudyIdKey =
          appParticipantsInfo.getUserDetailsId() + "-" + appParticipantsInfo.getStudyId();
      if (sitesByUserIdStudyIdMap.containsKey(userIdStudyIdKey)) {
        appStudyDetails.getSites().addAll(sitesByUserIdStudyIdMap.get(userIdStudyIdKey));
      }

      participantDetail.getEnrolledStudies().add(appStudyDetails);
    }

    List<ParticipantDetail> participants =
        participantsMap.values().stream().collect(Collectors.toList());

    Long participantCount =
        appRepository.countParticipantByAppIdAndSearchTerm(
            app.getId(), UserStatus.DEACTIVATED.getValue(), StringUtils.defaultString(searchTerm));

    AppParticipantsResponse appParticipantsResponse =
        prepareAppParticipantResponse(appId, adminId, auditRequest, app, participants);
    appParticipantsResponse.setTotalParticipantCount(participantCount);

    logger.exit(String.format("%d participant found for appId=%s", participantsMap.size(), appId));
    return appParticipantsResponse;
  }

  private AppSiteDetails prepareAppSiteDetails(ParticipantEnrollmentHistory enrollmentHistory) {
    AppSiteDetails appSiteDetails;
    appSiteDetails = new AppSiteDetails();
    appSiteDetails.setSiteId(enrollmentHistory.getSiteId());
    appSiteDetails.setCustomLocationId(enrollmentHistory.getLocationCustomId());
    appSiteDetails.setLocationName(enrollmentHistory.getLocationName());
    appSiteDetails.setParticipantStudyStatus(enrollmentHistory.getEnrollmentStatus());

    String withdrawalDate = DateTimeUtils.format(enrollmentHistory.getWithdrawalDate());
    String enrolledDate = DateTimeUtils.format(enrollmentHistory.getEnrolledDate());
    appSiteDetails.setWithdrawlDate(StringUtils.defaultIfEmpty(withdrawalDate, NOT_APPLICABLE));
    appSiteDetails.setEnrollmentDate(StringUtils.defaultIfEmpty(enrolledDate, NOT_APPLICABLE));
    return appSiteDetails;
  }

  private AppParticipantsResponse prepareAppParticipantResponse(
      String appId,
      String adminId,
      AuditLogEventRequest auditRequest,
      AppEntity app,
      List<ParticipantDetail> participants) {
    AppParticipantsResponse appParticipantsResponse =
        new AppParticipantsResponse(
            MessageCode.GET_APP_PARTICIPANTS_SUCCESS,
            app.getId(),
            app.getAppId(),
            app.getAppName());
    appParticipantsResponse.getParticipants().addAll(participants);

    auditRequest.setAppId(app.getAppId());
    auditRequest.setUserId(adminId);
    participantManagerHelper.logEvent(APP_PARTICIPANT_REGISTRY_VIEWED, auditRequest);
    return appParticipantsResponse;
  }
}
