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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyBean;
import com.google.cloud.healthcare.fdamystudies.dao.AppDetailsDao;
import com.google.cloud.healthcare.fdamystudies.dao.AppsDao;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.StudyInfoDao;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.BeanUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;

@Service
public class StudyServiceImpl implements StudyService {
  private static Logger logger = LoggerFactory.getLogger(StudyServiceImpl.class);

  @Autowired private AppsDao appsDao;

  @Autowired private SiteDao siteDao;

  @Autowired private CommonDao commonDao;

  @Autowired private CommonService commonService;

  @Autowired private StudyInfoDao studyInfoDao;

  @Autowired private AppDetailsDao appDetailsDao;

  @Autowired private StudyPermissionService studyPermissionService;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  public DashboardBean getStudies(Integer userId) {
    DashboardBean dashboardBean = new DashboardBean();
    logger.info(" SiteServiceImpl - getStudies():starts");
    List<StudyBean> studies = new ArrayList<>();
    List<SitePermission> sitePermissions = new ArrayList<>();
    Map<StudyInfoBO, List<SitePermission>> studyPermissionMap = new HashMap<>();
    Map<Object, Long> siteWithInvitedParticipantCountMap = new HashMap<>();
    Map<Object, Long> siteWithEnrolledParticipantCountMap = new HashMap<>();
    Map<Integer, StudyPermission> studyPermissionsByStudyInfoId = new HashMap<>();
    try {
      sitePermissions = commonService.getSitePermissionsOfUser(userId);

      if (sitePermissions != null && !sitePermissions.isEmpty() && sitePermissions.size() >= 2) {

        studyPermissionMap =
            sitePermissions.stream().collect(Collectors.groupingBy(SitePermission::getStudyInfo));

        List<Integer> usersStudyIds =
            sitePermissions
                .stream()
                .distinct()
                .map(studyInfobo -> studyInfobo.getId())
                .collect(Collectors.toList());

        List<StudyPermission> studyPermissions =
            siteDao.getStudyPermissionsOfUserByStudyIds(usersStudyIds, userId);

        if (studyPermissions != null && !studyPermissions.isEmpty()) {
          studyPermissionsByStudyInfoId =
              studyPermissions
                  .stream()
                  .collect(Collectors.toMap(e -> e.getStudyInfo().getId(), Function.identity()));
        }
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
                .collect(Collectors.groupingBy(e -> e.getSiteBo().getId(), Collectors.counting()));

        for (Map.Entry<StudyInfoBO, List<SitePermission>> entry : studyPermissionMap.entrySet()) {
          StudyBean studyBean = new StudyBean();
          Long studyInvitedCount = 0l;
          Long studyEnrolledCount = 0l;
          Double percentage;
          studyBean.setId(entry.getKey().getId());
          studyBean.setCustomId(entry.getKey().getCustomId());
          studyBean.setName(entry.getKey().getName());
          studyBean.setType(entry.getKey().getType());
          studyBean.setTotalSitesCount((long) entry.getValue().size());
          if (studyPermissionsByStudyInfoId.get(entry.getKey().getId()) != null) {
            Integer studyEditPermission =
                studyPermissionsByStudyInfoId.get(entry.getKey().getId()).getEdit();
            studyBean.setStudyPermission(
                studyEditPermission == 0
                    ? URWebAppWSConstants.READ_PERMISSION
                    : URWebAppWSConstants.READ_AND_EDIT_PERMISSION);
          }
          for (SitePermission sitePermission : entry.getValue()) {
            if (siteWithInvitedParticipantCountMap.get(sitePermission.getSiteBo().getId()) != null
                && entry.getKey().getType().equals(URWebAppWSConstants.CLOSE_STUDY))
              studyInvitedCount =
                  studyInvitedCount
                      + siteWithInvitedParticipantCountMap.get(sitePermission.getSiteBo().getId());

            if (entry.getKey().getType().equals(URWebAppWSConstants.OPEN_STUDY))
              studyInvitedCount =
                  studyInvitedCount + sitePermission.getSiteBo().getTargetEnrollment();

            if (siteWithEnrolledParticipantCountMap.get(sitePermission.getSiteBo().getId()) != null)
              studyEnrolledCount =
                  studyEnrolledCount
                      + siteWithEnrolledParticipantCountMap.get(sitePermission.getSiteBo().getId());
          }

          studyBean.setEnrolledCount(studyEnrolledCount);
          studyBean.setInvitedCount(studyInvitedCount);
          if (studyBean.getInvitedCount() != 0
              && studyBean.getInvitedCount() >= studyBean.getEnrolledCount()) {
            percentage =
                (Double.valueOf(studyBean.getEnrolledCount()) * 100)
                    / Double.valueOf(studyBean.getInvitedCount());
            studyBean.setEnrollmentPercentage(percentage);
          }
          studies.add(studyBean);
        }

        dashboardBean.setStudies(studies);
        dashboardBean.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_200.code(),
                ErrorCode.EC_200.errorMessage(),
                "error",
                ErrorCode.EC_200.errorMessage()));
      } else {
        dashboardBean.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_816.code(),
                ErrorCode.EC_816.errorMessage(),
                "error",
                ErrorCode.EC_816.errorMessage()));
      }
    } catch (Exception e) {
      logger.info("SiteServiceImpl - getSites() : error", e);
      dashboardBean.setError(
          AppUtil.dynamicResponse(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }

    logger.info("SiteServiceImpl - getSites() : ends");

    return dashboardBean;
  }

  @Override
  public ParticipantRegistryResponseBean getStudyParticipants(Integer userId, Integer studyId)
      throws SystemException, InvalidUserIdException {

    logger.info("StudyServiceImpl - getStudyParticipants() : starts");

    ParticipantRegistryResponseBean respBean = null;
    StudyPermission studyPermission = null;

    if (userId != null && studyId != null) {
      try {
        studyPermission = studyPermissionService.getStudyPermissionForUser(studyId, userId);

        if (studyPermission != null) {
          StudyInfoBO studyInfo = studyInfoDao.getStudyInfoDetails(studyId);
          AppInfoDetailsBO appDetails =
              appsDao.getAppInfoDetails(studyPermission.getAppInfo().getAppInfoId());

          if (studyInfo != null || appDetails != null) {
            respBean = BeanUtil.getBean(ParticipantRegistryResponseBean.class);
            if (studyInfo != null) {
              respBean.setStudyId(studyInfo.getId());
              respBean.setCustomStudyId(studyInfo.getCustomId());
              respBean.setStudyName(studyInfo.getName());
            }
            if (appDetails != null) {
              respBean.setAppId(appDetails.getAppInfoId());
              respBean.setAppName(appDetails.getAppName());
              respBean.setCustomAppId(appDetails.getAppId());
            }

            List<ParticipantBean> registryParticipants = new ArrayList<>();
            List<ParticipantStudiesBO> participantStudiesList =
                participantStudiesInfoDao.getParticipantStudiesDetails(studyId);

            if (participantStudiesList != null && !participantStudiesList.isEmpty()) {

              for (ParticipantStudiesBO participantStudy : participantStudiesList) {
                ParticipantBean participantBean = BeanUtil.getBean(ParticipantBean.class);
                participantBean.setId(participantStudy.getParticipantStudyInfoId());
                participantBean.setEnrollmentStatus(participantStudy.getStatus());
                participantBean.setEmail(participantStudy.getParticipantRegistrySite().getEmail());
                participantBean.setSiteId(participantStudy.getSiteBo().getId());
                participantBean.setCustomLocationId(
                    participantStudy.getSiteBo().getLocations().getCustomId());
                participantBean.setLocationName(
                    participantStudy.getSiteBo().getLocations().getName());
                if ("I"
                    .equalsIgnoreCase(
                        participantStudy.getParticipantRegistrySite().getOnboardingStatus())) {
                  participantBean.setOnboardingStatus("Invited");
                } else if ("N"
                    .equalsIgnoreCase(
                        participantStudy.getParticipantRegistrySite().getOnboardingStatus())) {
                  participantBean.setOnboardingStatus("New");
                } else {
                  participantBean.setOnboardingStatus("Disabled");
                }
                participantBean.setInvitedDate(
                    participantStudy.getParticipantRegistrySite().getInvitationDate() != null
                        ? URWebAppWSConstants.SDF_DATE_TIME.format(
                            participantStudy.getParticipantRegistrySite().getInvitationDate())
                        : "NA");
                participantBean.setEnrollmentDate(
                    participantStudy.getEnrolledDate() != null
                        ? URWebAppWSConstants.SDF_DATE_TIME.format(
                            participantStudy.getEnrolledDate())
                        : "NA");
                registryParticipants.add(participantBean);
              }
            }
            respBean.setRegistryParticipants(registryParticipants);
            logger.info("StudyServiceImpl - getStudyParticipants() : ends");
            return respBean;
          }
          return null;
        } else throw new InvalidUserIdException();
      } catch (InvalidUserIdException | SystemException e) {
        logger.error("StudyServiceImpl - getStudyParticipants() : error ", e);
        throw e;
      } catch (Exception e) {
        logger.error("StudyServiceImpl - getStudyParticipants() : error ", e);
        throw new SystemException();
      }
    } else {
      logger.info("StudyServiceImpl - getStudyParticipants() : ends with null");
      return null;
    }
  }
}
