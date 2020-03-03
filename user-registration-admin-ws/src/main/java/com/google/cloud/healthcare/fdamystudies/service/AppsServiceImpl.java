/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.AppBean;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.EnrolledStudies;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppParticipantRegistryServiceResponse;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppsDetailsServiceResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.Participants;
import com.google.cloud.healthcare.fdamystudies.bean.SiteDetails;
import com.google.cloud.healthcare.fdamystudies.bean.SitesResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudiesResponseBean;
import com.google.cloud.healthcare.fdamystudies.dao.AppPermissionDao;
import com.google.cloud.healthcare.fdamystudies.dao.AppsDao;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.SitePermissionDAO;
import com.google.cloud.healthcare.fdamystudies.dao.StudyInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyPermissionDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserDetailsDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserRegAdminUserDao;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AppPermission;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@Service
public class AppsServiceImpl implements AppsService {
  private static Logger logger = LoggerFactory.getLogger(AppsServiceImpl.class);

  @Autowired private AppsDao appsDao;

  @Autowired private CommonServiceImpl commonServiceImpl;

  @Autowired private CommonDao commonDao;

  // @Autowired private AppDetailsDao appDetailsDao;

  @Autowired private AppPermissionDao appPermissionDao;

  @Autowired private StudyInfoDao studyInfoDao;

  @Autowired private StudyPermissionDao studyPermissionDao;

  @Autowired private SitePermissionDAO sitePermissionDAO;

  @Autowired private SiteDao siteDao;

  @Autowired private UserDetailsDao userDetailsDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Autowired private UserRegAdminUserDao userRegAdminUserDao;

  String responseType = "error";

  public DashboardBean getApps(Integer userId) {
    DashboardBean dashboardBean = new DashboardBean();
    logger.info("SiteServiceImpl - getApps(): starts");
    List<AppBean> apps = new ArrayList<>();
    List<SitePermission> sitePermissions = new ArrayList<>();
    Map<StudyInfoBO, List<SitePermission>> studyPermissionMap = new HashMap<>();
    Map<Object, Long> siteWithInvitedParticipantCountMap = new HashMap<>();
    Map<Object, Long> siteWithEnrolledParticipantCountMap = new HashMap<>();
    Map<Object, AppPermission> appPermissionsByAppInfoId = new HashMap<>();
    Map<Integer, Long> appIdbyUsersCount = new HashMap<>();
    try {
      sitePermissions = commonServiceImpl.getSitePermissionsOfUser(userId);

      if (sitePermissions != null && !sitePermissions.isEmpty()) {

        studyPermissionMap =
            sitePermissions.stream().collect(Collectors.groupingBy(SitePermission::getStudyInfo));

        if (studyPermissionMap.size() >= 2) {
          List<Integer> usersAppsIds =
              sitePermissions
                  .stream()
                  .distinct()
                  .map(appInfoDetailsbo -> appInfoDetailsbo.getId())
                  .collect(Collectors.toList());

          List<AppPermission> appPermissions =
              appsDao.getAppPermissionsOfUserByAppIds(usersAppsIds, userId);

          if (appPermissions != null && !appPermissions.isEmpty()) {
            appPermissionsByAppInfoId =
                appPermissions
                    .stream()
                    .collect(
                        Collectors.toMap(e -> e.getAppInfo().getAppInfoId(), Function.identity()));

            appIdbyUsersCount = appsDao.getAppUsersCount(usersAppsIds);
          }
          Map<AppInfoDetailsBO, Map<StudyInfoBO, List<SitePermission>>>
              sitePermissionByAppInfoAndStudyInfo =
                  sitePermissions
                      .stream()
                      .collect(
                          Collectors.groupingBy(
                              SitePermission::getAppInfo,
                              Collectors.groupingBy(SitePermission::getStudyInfo)));

          List<Integer> usersSiteIds =
              sitePermissions
                  .stream()
                  .map(s -> s.getSiteBo().getId())
                  .distinct()
                  .collect(Collectors.toList());

          List<ParticipantRegistrySite> participantRegistry =
              commonDao.getParticipantRegistryOfSites(usersSiteIds);

          siteWithInvitedParticipantCountMap =
              participantRegistry
                  .stream()
                  .collect(
                      Collectors.groupingBy(
                          e -> e.getSites().getId(),
                          Collectors.summingLong(ParticipantRegistrySite::getInvitationCount)));

          List<ParticipantStudiesBO> participantsEnrollments =
              commonDao.getParticipantsEnrollmentsOfSites(usersSiteIds);

          siteWithEnrolledParticipantCountMap =
              participantsEnrollments
                  .stream()
                  .collect(
                      Collectors.groupingBy(e -> e.getSiteBo().getId(), Collectors.counting()));

          for (Map.Entry<AppInfoDetailsBO, Map<StudyInfoBO, List<SitePermission>>> entry :
              sitePermissionByAppInfoAndStudyInfo.entrySet()) {
            AppBean appBean = new AppBean();
            Long appInvitedCount = 0l;
            Long appEnrolledCount = 0l;
            Double percentage;
            appBean.setId(entry.getKey().getAppInfoId());
            appBean.setCustomId(entry.getKey().getAppId());
            appBean.setTotalStudiesCount((long) entry.getValue().size());
            appBean.setName(entry.getKey().getAppName());
            if (appIdbyUsersCount.get(entry.getKey().getAppInfoId()) != null)
              appBean.setAppUsersCount(appIdbyUsersCount.get(entry.getKey().getAppInfoId()));

            if (appPermissionsByAppInfoId.get(entry.getKey().getAppInfoId()) != null) {
              Integer appEditPermission =
                  appPermissionsByAppInfoId.get(entry.getKey().getAppInfoId()).getEdit();
              appBean.setAppPermission(
                  appEditPermission == 0
                      ? URWebAppWSConstants.READ_PERMISSION
                      : URWebAppWSConstants.READ_AND_EDIT_PERMISSION);
              appBean.setAppUsersCount(0L);
            }

            for (Map.Entry<StudyInfoBO, List<SitePermission>> studyentry :
                entry.getValue().entrySet()) {
              for (SitePermission sitePermission : studyentry.getValue()) {

                if (siteWithInvitedParticipantCountMap.get(sitePermission.getSiteBo().getId())
                        != null
                    && studyentry.getKey().getType().equals(URWebAppWSConstants.CLOSE_STUDY))
                  appInvitedCount =
                      appInvitedCount
                          + siteWithInvitedParticipantCountMap.get(
                              sitePermission.getSiteBo().getId());

                if (studyentry.getKey().getType().equals(URWebAppWSConstants.OPEN_STUDY))
                  appInvitedCount =
                      appInvitedCount + sitePermission.getSiteBo().getTargetEnrollment();

                if (siteWithEnrolledParticipantCountMap.get(sitePermission.getSiteBo().getId())
                    != null)
                  appEnrolledCount =
                      appEnrolledCount
                          + siteWithEnrolledParticipantCountMap.get(
                              sitePermission.getSiteBo().getId());
              }
            }
            appBean.setEnrolledCount(appEnrolledCount);
            appBean.setInvitedCount(appInvitedCount);
            if (appBean.getInvitedCount() != 0
                && appBean.getInvitedCount() >= appBean.getEnrolledCount()) {
              percentage =
                  (Double.valueOf(appBean.getEnrolledCount()) * 100)
                      / Double.valueOf(appBean.getInvitedCount());
              appBean.setEnrollmentPercentage(percentage);
            }
            apps.add(appBean);
          }

          dashboardBean.setApps(apps);
          dashboardBean.setError(
              AppUtil.dynamicResponse(
                  ErrorCode.EC_200.code(),
                  ErrorCode.EC_200.errorMessage(),
                  responseType,
                  ErrorCode.EC_200.errorMessage()));
        } else {
          dashboardBean.setError(
              AppUtil.dynamicResponse(
                  ErrorCode.EC_817.code(),
                  ErrorCode.EC_817.errorMessage(),
                  responseType,
                  ErrorCode.EC_817.errorMessage()));
        }
      } else {
        dashboardBean.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_817.code(),
                ErrorCode.EC_817.errorMessage(),
                responseType,
                ErrorCode.EC_817.errorMessage()));
      }
    } catch (Exception e) {
      logger.info("SiteServiceImpl - getApps() : error", e);
      dashboardBean.setError(
          AppUtil.dynamicResponse(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              responseType,
              ErrorCode.EC_500.errorMessage()));
    }

    logger.info("SiteServiceImpl - getApps() : ends");

    return dashboardBean;
  }

  @Override
  public List<GetAppsDetailsServiceResponseBean> getAppsDetails(Integer userId)
      throws SystemException, InvalidUserIdException {

    logger.info("AppsServiceImpl - getAppsDetails() : starts");
    try {
      List<GetAppsDetailsServiceResponseBean> serviceResponseList = new ArrayList<>();

      if (userId != null) {
        UserRegAdminUser userRegAdminUser = userRegAdminUserDao.checkPermission(userId);

        if (userRegAdminUser != null && Boolean.TRUE.equals(userRegAdminUser.getSuperAdmin())) {
          List<AppInfoDetailsBO> appDetails = appsDao.getAllApps();
          List<Integer> appInfoIdList = new ArrayList<>();
          if (appDetails != null && !appDetails.isEmpty()) {
            appDetails
                .stream()
                .map((app) -> appInfoIdList.add(app.getAppInfoId()))
                .collect(Collectors.toList());
          }

          List<StudyInfoBO> studyList = studyInfoDao.getStudies(appInfoIdList);
          List<Integer> studyIdList = new ArrayList<>();
          if (studyList != null && !studyList.isEmpty()) {
            studyList
                .stream()
                .map((study) -> studyIdList.add(study.getId()))
                .collect(Collectors.toList());
          }

          List<SiteBo> siteList = siteDao.getSites(studyIdList);

          Map<Integer, List<StudyInfoBO>> appWithStudyMap =
              studyList.stream().collect(Collectors.groupingBy(e -> e.getAppInfo().getAppInfoId()));

          Map<Integer, List<SiteBo>> siteWithStudyMap =
              siteList.stream().collect(Collectors.groupingBy(e -> e.getStudyInfo().getId()));

          for (AppInfoDetailsBO app : appDetails) {
            GetAppsDetailsServiceResponseBean appInfo =
                BeanUtil.getBean(GetAppsDetailsServiceResponseBean.class);
            appInfo.setCustomId(app.getAppId());
            appInfo.setId(app.getAppInfoId());
            appInfo.setName(app.getAppName());

            for (Map.Entry<Integer, List<StudyInfoBO>> studyEntry : appWithStudyMap.entrySet()) {

              List<StudyInfoBO> studyListFromDao = studyEntry.getValue();
              List<StudiesResponseBean> studies = new ArrayList<>();

              for (StudyInfoBO study : studyListFromDao) {
                if (app.getAppInfoId() == study.getAppInfo().getAppInfoId()) {
                  StudiesResponseBean studyResponse = BeanUtil.getBean(StudiesResponseBean.class);
                  studyResponse.setStudyId(study.getId());
                  studyResponse.setCustomStudyId(study.getCustomId());
                  studyResponse.setStudyName(study.getName());

                  List<SitesResponseBean> sites = new ArrayList<>();
                  for (Map.Entry<Integer, List<SiteBo>> siteEntry : siteWithStudyMap.entrySet()) {

                    List<SiteBo> siteListFromDao = siteEntry.getValue();

                    for (SiteBo site : siteListFromDao) {
                      if (study.getId() == site.getStudyInfo().getId()) {
                        SitesResponseBean siteResponse = BeanUtil.getBean(SitesResponseBean.class);
                        siteResponse.setSiteId(site.getId());
                        siteResponse.setCustomLocationId(site.getLocations().getCustomId());
                        siteResponse.setLocationDescription(site.getLocations().getDescription());
                        siteResponse.setLocationId(site.getLocations().getId());
                        siteResponse.setLocationName(site.getLocations().getName());

                        sites.add(siteResponse);
                      }
                    }
                  }
                  studyResponse.setSites(sites);
                  studies.add(studyResponse);
                  appInfo.setStudies(studies);
                }
              }
            }
            serviceResponseList.add(appInfo);
          }
        } else {
          throw new InvalidUserIdException();
        }
        logger.info("AppsServiceImpl - getAppsDetails() : ends");
        return serviceResponseList;
      } else {
        throw new InvalidUserIdException();
      }
    } catch (InvalidUserIdException e) {
      logger.error("AppsServiceImpl - getAppsDetails() : error ", e);
      throw e;
    } catch (Exception e) {
      logger.error("AppsServiceImpl - getAppsDetails() : error ", e);
      throw new SystemException();
    }
  }

  @Override
  public GetAppParticipantRegistryServiceResponse getAppParticipantRegistry(
      Integer appId, Integer adminId) throws SystemException, InvalidUserIdException {

    logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Started");
    GetAppParticipantRegistryServiceResponse serviceResponse = null;

    if (appId != null && adminId != null) {
      try {
        // TODO: check the userId has app level permission or not
        AppPermission appPermission = appPermissionDao.checkPermission(adminId, appId);
        if (appPermission != null) {
          serviceResponse = BeanUtil.getBean(GetAppParticipantRegistryServiceResponse.class);
          serviceResponse.setAppId(appPermission.getAppInfo().getAppInfoId());
          serviceResponse.setCustomAppId(appPermission.getAppInfo().getAppId());
          serviceResponse.setAppName(appPermission.getAppInfo().getAppName());

          List<Participants> participants = new ArrayList<>();
          List<UserDetails> participantDetails =
              userDetailsDao.getUserDetais(appPermission.getAppInfo().getAppInfoId());

          List<StudyInfoBO> appsStudies =
              appsDao.getAppsStudies(appPermission.getAppInfo().getAppInfoId());

          List<Integer> appsStudyInfoIds =
              appsStudies.stream().distinct().map(s -> s.getId()).collect(Collectors.toList());

          List<Integer> userDetailsIds =
              participantDetails
                  .stream()
                  .distinct()
                  .map(a -> a.getUserDetailsId())
                  .collect(Collectors.toList());

          List<ParticipantStudiesBO> participantEnrollments =
              appsDao.getParticipantEnrollments(appsStudyInfoIds, userDetailsIds);

          Map<Object, Map<StudyInfoBO, List<ParticipantStudiesBO>>>
              participantEnrollmentsByUserDetailsAndStudyInfo =
                  participantEnrollments
                      .stream()
                      .collect(
                          Collectors.groupingBy(
                              s -> s.getUserDetails().getUserDetailsId(),
                              Collectors.groupingBy(s -> s.getStudyInfo())));
          if (participantDetails != null && participantDetails.size() > 0) {
            Participants participant = null;
            for (UserDetails userDetails : participantDetails) {
              List<EnrolledStudies> enrolledStudies = new ArrayList<>();
              participant = BeanUtil.getBean(Participants.class);
              participant.setId(userDetails.getUserDetailsId());
              participant.setEmail(userDetails.getEmail());
              if (userDetails.getStatus() == 1) {
                participant.setRegistrationStatus("Active");
              } else if (userDetails.getStatus() == 2) {
                participant.setRegistrationStatus("Pending");
              } else {
                participant.setRegistrationStatus("Inactive");
              }
              // TODO: participant.setStudiesEnrolled(2); //  (Pending)
              participant.setRegistrationDate(
                  URWebAppWSConstants.SDF_DATE_TIME.format(userDetails.getVerificationDate()));
              if (participantEnrollmentsByUserDetailsAndStudyInfo.get(
                      userDetails.getUserDetailsId())
                  != null) {
                Map<StudyInfoBO, List<ParticipantStudiesBO>> enrolledStudiesByStudyInfoId =
                    participantEnrollmentsByUserDetailsAndStudyInfo.get(
                        userDetails.getUserDetailsId());
                for (Entry<StudyInfoBO, List<ParticipantStudiesBO>> entry :
                    enrolledStudiesByStudyInfoId.entrySet()) {
                  EnrolledStudies enrolledStudy = new EnrolledStudies();
                  List<SiteDetails> sites = new ArrayList<>();
                  enrolledStudy.setCustomStudyId(entry.getKey().getCustomId());
                  enrolledStudy.setStudyName(entry.getKey().getName());
                  enrolledStudy.setStudyId(entry.getKey().getId());
                  for (ParticipantStudiesBO enrollment : entry.getValue()) {
                    SiteDetails studiesEnrollment = new SiteDetails();
                    studiesEnrollment.setCustomSiteId(
                        enrollment.getSiteBo().getLocations().getCustomId());
                    studiesEnrollment.setSiteId(enrollment.getSiteBo().getId());
                    studiesEnrollment.setSiteName(enrollment.getSiteBo().getLocations().getName());
                    studiesEnrollment.setSiteStatus(enrollment.getStatus());
                    studiesEnrollment.setWithdrawlDate(
                        enrollment.getWithdrawalDate() != null
                            ? enrollment
                                .getWithdrawalDate()
                                .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                            : "NA");
                    studiesEnrollment.setEnrollmentDate(
                        enrollment.getEnrolledDate() != null
                            ? URWebAppWSConstants.SDF_DATE_TIME.format(enrollment.getEnrolledDate())
                            : "NA");
                    sites.add(studiesEnrollment);
                  }
                  enrolledStudy.setSites(sites);
                  enrolledStudies.add(enrolledStudy);
                }
              }

              participant.setEnrolledStudies(enrolledStudies);
              participants.add(participant);
            }
          }
          serviceResponse.setParticipants(participants);
          logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended");
          return serviceResponse;
        } else {
          logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended");
          throw new InvalidUserIdException();
        }
      } catch (InvalidUserIdException e) {
        logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        throw e;
      } catch (SystemException e) {
        logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        throw e;
      } catch (Exception e) {
        logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended: (ERROR) ", e);
        throw new SystemException();
      }
    } else {
      logger.info("(Service)...AppsServiceImpl.getAppParticipantRegistry()...Ended with null");
      return null;
    }
  }
}
