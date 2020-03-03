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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentDocumentBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentHistory;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.EnableDisableParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.Enrollments;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.InviteParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantDetailsBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.SiteBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyBean;
import com.google.cloud.healthcare.fdamystudies.bean.SuccessBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantRegistrySiteDAO;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantStudiesInfoDao;
import com.google.cloud.healthcare.fdamystudies.dao.SiteDao;
import com.google.cloud.healthcare.fdamystudies.dao.UserProfileServiceDao;
import com.google.cloud.healthcare.fdamystudies.model.MailMessages;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySite;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.SiteBo;
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import com.google.cloud.healthcare.fdamystudies.utils.AppUtil;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.utils.SendEmailNotification;
import com.google.cloud.healthcare.fdamystudies.utils.TokenUtil;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSConstants;
import com.google.cloud.healthcare.fdamystudies.utils.URWebAppWSUtil;

@Service
public class SitesServiceImpl implements SitesService {
  private static Logger logger = LoggerFactory.getLogger(SitesServiceImpl.class);

  @Autowired private ParticipantRegistrySiteDAO participantRegistrySiteDAO;

  @Autowired private SitePermissionService sitePermissionService;

  @Autowired private UserProfileServiceDao userProfileServiceDao;

  @Autowired private SiteDao siteDao;

  @Autowired private ParticipantStudiesInfoDao participantStudiesInfoDao;

  @Autowired CommonService commonService;

  @Autowired CommonDao commonDao;

  @Autowired SendEmailNotification emailNotification;

  @Autowired ApplicationConfiguratation applicationConfiguration;

  @Autowired FileStorageService cloudStorageService;

  @Override
  public ParticipantRegistryResponseBean getParticipants(
      String userId, Integer siteId, String onboardingStatus) {
    logger.error("SitesServiceImpl - getParticipants() : starts");
    ParticipantRegistryResponseBean respBean = new ParticipantRegistryResponseBean();
    List<ParticipantBean> participants = null;
    try {
      UserDetailsResponseBean userDetails =
          userProfileServiceDao.getUserProfileById(Integer.valueOf(userId));
      SitePermission sitePermission =
          sitePermissionService.getSitePermissionForUser(
              siteId, userDetails.getProfileRespBean().getUserId());
      if (sitePermission != null && sitePermission.getEdit() >= 0) {
        SiteBo site = siteDao.getSiteDetails(siteId);
        if (site.getStudyInfo() != null) {
          StudyInfoBO studyInfo = site.getStudyInfo();
          respBean.setStudyId(studyInfo.getId());
          respBean.setStudyName(studyInfo.getName());
          respBean.setCustomStudyId(studyInfo.getCustomId());
          respBean.setSitePermission(sitePermission.getEdit());
          if (studyInfo.getAppInfo() != null) {
            respBean.setAppName(studyInfo.getAppInfo().getAppName());
            respBean.setCustomAppId(studyInfo.getAppInfo().getAppId());
          }
          if (site.getLocations() != null) {
            respBean.setLocationName(site.getLocations().getName());
            respBean.setCustomLocationId(site.getLocations().getCustomId());
            respBean.setLocationStatus(site.getLocations().getStatus());
          }
        }

        List<ParticipantRegistrySite> registryParticipants =
            participantRegistrySiteDAO.getParticipantRegistryForSite(onboardingStatus, siteId);
        if (URWebAppWSConstants.ONBOARDING_STATUS_ALL.equalsIgnoreCase(onboardingStatus)) {
          Map<String, Integer> counts =
              participantRegistrySiteDAO.getParticipantCountByOnboardingStatus(siteId);
          respBean.setCountByStatus(counts);
        }
        if (!CollectionUtils.isEmpty(registryParticipants)) {
          participants = new LinkedList<ParticipantBean>();
          List<Integer> registryIds =
              registryParticipants
                  .stream()
                  .map(participantRegistry -> participantRegistry.getId())
                  .collect(Collectors.toList());
          List<ParticipantStudiesBO> participantStudies =
              participantStudiesInfoDao.getParticipantStudiesByRegistryId(registryIds);
          Map<Integer, ParticipantStudiesBO> idMap = new HashMap<>();
          for (ParticipantStudiesBO participantStudy : participantStudies) {
            if (participantStudy.getParticipantRegistrySite() != null) {
              idMap.put(participantStudy.getParticipantRegistrySite().getId(), participantStudy);
            }
          }

          Integer newlyCreatedSince = applicationConfiguration.getNewlyCreatedTimeframeMinutes();
          Map<String, String> dbToAPIValueMap = URWebAppWSUtil.onboardingStatusDBToAPIMapping();
          for (ParticipantRegistrySite participantRegistrySite : registryParticipants) {
            ParticipantBean participant = new ParticipantBean();
            participant.setId(participantRegistrySite.getId());
            participant.setEmail(participantRegistrySite.getEmail());
            participant.setOnboardingStatus(
                dbToAPIValueMap.get(participantRegistrySite.getOnboardingStatus()));

            SimpleDateFormat sdf = new SimpleDateFormat(URWebAppWSConstants.UI_DATE);
            ParticipantStudiesBO participantStudy = idMap.get(participantRegistrySite.getId());
            if (participantStudy != null) {
              participant.setEnrollmentStatus(participantStudy.getStatus());
              if (participantStudy.getEnrolledDate() != null) {
                participant.setEnrollmentDate(sdf.format(participantStudy.getEnrolledDate()));
              }
            }

            Date newCreatedDate =
                new Date(System.currentTimeMillis() - newlyCreatedSince * 60 * 1000L);
            if (participantRegistrySite.getInvitationDate() != null) {
              participant.setInvitedDate(sdf.format(participantRegistrySite.getInvitationDate()));
            }
            if (participantRegistrySite.getCreated().after(newCreatedDate)) {
              participant.setNewlyCreatedUser(true);
            } else {
              participant.setNewlyCreatedUser(false);
            }
            participants.add(participant);
          }
          respBean.setRegistryParticipants(participants);
        }
      }
    } catch (Exception e) {
      logger.error("SitesServiceImpl - getParticipants() : error");
      throw e;
    }
    logger.info("SitesServiceImpl - getParticipants() : ends");
    return respBean;
  }

  @SuppressWarnings("unchecked")
  public DashboardBean getSites(Integer userId) {
    DashboardBean dashboardBean = new DashboardBean();
    logger.info(" SiteServiceImpl - getSites():starts");
    List<StudyBean> studies = new ArrayList<>();
    List<SitePermission> sitePermissions = new ArrayList<>();
    Map<StudyInfoBO, List<SitePermission>> sitePermissionsByStudyInfoId = new HashMap<>();
    Map<Object, Long> siteWithInvitedParticipantCountMap = new HashMap<>();
    Map<Object, Long> siteWithEnrolledParticipantCountMap = new HashMap<>();
    Map<Integer, StudyPermission> studyPermissionsByStudyInfoId = new HashMap<>();
    try {
      sitePermissions = commonService.getSitePermissionsOfUser(userId);

      if (sitePermissions != null && !sitePermissions.isEmpty()) {

        sitePermissionsByStudyInfoId =
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

        for (Map.Entry<StudyInfoBO, List<SitePermission>> entry :
            sitePermissionsByStudyInfoId.entrySet()) {
          StudyBean studyBean = new StudyBean();
          studyBean.setId(entry.getKey().getId());
          studyBean.setCustomId(entry.getKey().getCustomId());
          studyBean.setName(entry.getKey().getName());
          studyBean.setType(entry.getKey().getType());
          studyBean.setAppId(entry.getKey().getAppInfo().getAppId());
          studyBean.setAppInfoId(entry.getKey().getAppInfo().getAppInfoId());
          if (studyPermissionsByStudyInfoId.get(entry.getKey().getId()) != null) {
            Integer studyEditPermission =
                studyPermissionsByStudyInfoId.get(entry.getKey().getId()).getEdit();
            studyBean.setStudyPermission(
                studyEditPermission == 0
                    ? URWebAppWSConstants.READ_PERMISSION
                    : URWebAppWSConstants.READ_AND_EDIT_PERMISSION);
          }
          List<SiteBean> siteBeans = new ArrayList<>();
          for (SitePermission sitePermission : entry.getValue()) {
            Double percentage;
            SiteBean siteBean = new SiteBean();
            siteBean.setId(sitePermission.getSiteBo().getId());
            siteBean.setName(sitePermission.getSiteBo().getName());
            siteBean.setEdit(sitePermission.getEdit());
            if (entry.getKey().getType().equals(URWebAppWSConstants.OPEN_STUDY)) {
              siteBean.setInvitedCount(
                  Long.valueOf(sitePermission.getSiteBo().getTargetEnrollment()));
            } else if (entry.getKey().getType().equals(URWebAppWSConstants.CLOSE_STUDY)
                && siteWithInvitedParticipantCountMap.get(sitePermission.getSiteBo().getId())
                    != null) {
              siteBean.setInvitedCount(
                  siteWithInvitedParticipantCountMap.get(sitePermission.getSiteBo().getId()));
            }
            if (siteWithEnrolledParticipantCountMap.get(sitePermission.getSiteBo().getId()) != null)
              siteBean.setEnrolledCount(
                  siteWithEnrolledParticipantCountMap.get(sitePermission.getSiteBo().getId()));
            if (siteBean.getInvitedCount() != 0
                && siteBean.getInvitedCount() >= siteBean.getEnrolledCount()) {
              percentage =
                  (Double.valueOf(siteBean.getEnrolledCount()) * 100)
                      / Double.valueOf(siteBean.getInvitedCount());
              siteBean.setEnrollmentPercentage(percentage);
            }
            siteBeans.add(siteBean);
          }
          studyBean.setSites(siteBeans);
          studies.add(studyBean);
        }
        dashboardBean.setStudies(studies);
        dashboardBean.setError(
            new ErrorBean(ErrorCode.EC_200.code(), ErrorCode.EC_200.errorMessage()));
      } else {
        dashboardBean.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_814.code(),
                ErrorCode.EC_814.errorMessage(),
                "error",
                ErrorCode.EC_814.errorMessage()));
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
  public ParticipantDetailsBean getParticipantDetails(
      Integer participantRegistrySiteId, Integer userId) {
    logger.info(" SiteServiceImpl - getParticipantDetails():starts");
    List<Integer> participantStudyIds = new ArrayList<>();
    List<ConsentHistory> consentHistories = new ArrayList<>();
    List<Enrollments> enrollments = new ArrayList<>();
    ParticipantDetailsBean participantDetails = new ParticipantDetailsBean();
    try {
      ParticipantRegistrySite participantRegistry =
          siteDao.getParticipantSiteRegistry(participantRegistrySiteId);
      if (participantRegistry != null) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(
                participantRegistry.getSites().getId(), userId);
        if (sitePermission != null) {
          participantDetails.setAppName(
              participantRegistry.getStudyInfo().getAppInfo().getAppName());
          participantDetails.setCustomAppId(
              participantRegistry.getStudyInfo().getAppInfo().getAppId());
          participantDetails.setStudyName(participantRegistry.getStudyInfo().getName());
          participantDetails.setCustomStudyId(participantRegistry.getStudyInfo().getCustomId());
          participantDetails.setLocationName(
              participantRegistry.getSites().getLocations().getName());
          participantDetails.setCustomLocationId(
              participantRegistry.getSites().getLocations().getCustomId());
          participantDetails.setEmail(participantRegistry.getEmail());
          participantDetails.setInvitationDate(
              participantRegistry.getInvitationDate() != null
                  ? URWebAppWSConstants.SDF_DATE_TIME.format(
                      participantRegistry.getInvitationDate())
                  : "NA");
          participantDetails.setOnboardringStatus(
              participantRegistry.getOnboardingStatus().equals("I")
                  ? URWebAppWSConstants.ONBOARDING_STATUS_INVITED_CAPS
                  : (participantRegistry.getOnboardingStatus().equals("N")
                      ? URWebAppWSConstants.ONBOARDING_STATUS_NEW_CAPS
                      : URWebAppWSConstants.ONBOARDING_STATUS_DISABLED_CAPS));

          participantDetails.setParticipantRegistrySiteid(participantRegistry.getId());
          List<ParticipantStudiesBO> participantsEnrollments =
              siteDao.getparticipantsEnrollment(participantRegistrySiteId);
          if (participantsEnrollments != null && participantsEnrollments.isEmpty()) {
            for (ParticipantStudiesBO participantsEnrollment : participantsEnrollments) {
              participantStudyIds.add(participantsEnrollment.getParticipantStudyInfoId());
              Enrollments enrollment = new Enrollments();
              enrollment.setEnrollmentStatus(participantsEnrollment.getStatus());
              enrollment.setParticipantId(participantsEnrollment.getParticipantId());
              enrollment.setEnrollmentDate(
                  participantsEnrollment.getEnrolledDate() != null
                      ? URWebAppWSConstants.SDF_DATE_TIME.format(
                          participantsEnrollment.getEnrolledDate())
                      : "NA");
              enrollment.setWithdrawalDate(
                  participantsEnrollment.getWithdrawalDate() != null
                      ? participantsEnrollment
                          .getWithdrawalDate()
                          .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
                      : "NA");
              enrollments.add(enrollment);
            }
            participantDetails.setEnrollments(enrollments);

            List<StudyConsentBO> studyCosents =
                siteDao.getStudyConsentsOfParticipantStudyIds(participantStudyIds);

            if (studyCosents != null && studyCosents.isEmpty()) {
              for (StudyConsentBO studyCosent : studyCosents) {
                ConsentHistory consentHistory = new ConsentHistory();
                consentHistory.setConsentDocumentPath(studyCosent.getPdfPath());
                consentHistory.setConsentVersion(studyCosent.getVersion());
                consentHistory.setConsentedDate(
                    studyCosent.getParticipantStudiesBO().getEnrolledDate() != null
                        ? URWebAppWSConstants.SDF_DATE_TIME.format(
                            studyCosent.getParticipantStudiesBO().getEnrolledDate())
                        : "");
                consentHistory.setDataSharingPermissions(
                    studyCosent.getParticipantStudiesBO().getSharing());
                consentHistories.add(consentHistory);
              }
            }
            participantDetails.setConsentHistory(consentHistories);
          }
          participantDetails.setError(
              AppUtil.dynamicResponse(
                  ErrorCode.EC_200.code(),
                  ErrorCode.EC_200.errorMessage(),
                  "error",
                  ErrorCode.EC_200.errorMessage()));

        } else {

          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          participantDetails.setError(errorBean);
          return participantDetails;
        }
      } else {
        participantDetails.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_963.code(),
                ErrorCode.EC_963.errorMessage(),
                "error",
                ErrorCode.EC_963.errorMessage()));
      }
    } catch (Exception e) {
      logger.info(" SiteServiceImpl - getParticipantDetails():error", e);
      participantDetails.setError(
          AppUtil.dynamicResponse(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info(" SiteServiceImpl - getParticipantDetails():ends");

    return participantDetails;
  }

  @Override
  public void addNewParticipant(ParticipantBean participant, Integer siteId, Integer userId) {
    logger.info("SitesServiceImpl - addNewParticipant() : starts");
    try {

      SiteBo siteBo = siteDao.getSiteDetails(siteId);
      if (siteBo != null && siteBo.getStatus() == 1) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(siteId, userId);
        if (sitePermission != null && sitePermission.getEdit() == 1) {

          List<ParticipantRegistrySite> registry =
              commonService.getParticipantRegistry(
                  siteBo.getStudyInfo().getId(), participant.getEmail());
          if (CollectionUtils.isEmpty(registry)) {
            ParticipantRegistrySite participantRegistrySite = new ParticipantRegistrySite();
            participantRegistrySite.setEmail(participant.getEmail());
            participantRegistrySite.setSites(siteBo);
            participantRegistrySite.setOnboardingStatus("N");
            participantRegistrySite.setEnrollmentToken(TokenUtil.randomString(8));
            participantRegistrySite.setCreated(new Date());
            participantRegistrySite.setStudyInfo(siteBo.getStudyInfo());
            participantRegistrySite.setCreatedBy(userId);
            participantRegistrySiteDAO.saveParticipantRegistry(participantRegistrySite);
            participant.setSuccessBean(new SuccessBean(SuccessBean.ADD_PARTICIPANT_SUCCESS));
          } else {
            ParticipantRegistrySite participantRegistrySite = registry.get(0);
            ParticipantStudiesBO participantStudies =
                commonService.getParticipantStudiesBOs(participantRegistrySite.getId());

            if (participantStudies != null) {
              if ("Withdrawn".equals(participantStudies.getStatus())
                  && "Disabled".equals(participantRegistrySite.getOnboardingStatus())) {
              } else if ("Enrolled".equals(participantStudies.getStatus())) {
                ErrorBean errorBean =
                    AppUtil.dynamicResponse(
                        ErrorCode.EC_862.code(),
                        ErrorCode.EC_862.errorMessage(),
                        "error",
                        ErrorCode.EC_862.errorMessage());
                participant.setErrorBean(errorBean);
                return;
              }
            } else {
              ErrorBean errorBean =
                  AppUtil.dynamicResponse(
                      ErrorCode.EC_864.code(),
                      ErrorCode.EC_864.errorMessage(),
                      "error",
                      ErrorCode.EC_864.errorMessage());
              participant.setErrorBean(errorBean);
              return;
            }
          }
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          participant.setErrorBean(errorBean);
          return;
        }

      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_865.code(),
                ErrorCode.EC_865.errorMessage(),
                "error",
                ErrorCode.EC_865.errorMessage());
        participant.setErrorBean(errorBean);
      }
    } catch (Exception e) {
      logger.error("SitesServiceImpl - addNewParticipant() : error");
      throw e;
    }
    logger.info("SitesServiceImpl - addNewParticipant() : ends");
  }

  @Override
  public void addNewParticipant(
      ParticipantResponseBean participantRespBean, Integer siteId, Integer userId) {
    logger.info("SitesServiceImpl - addNewParticipant() : starts");
    try {

      SiteBo siteBo = siteDao.getSiteDetails(siteId);
      if (siteBo != null && siteBo.getStatus() == 1) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(siteId, userId);
        if (sitePermission != null && sitePermission.getEdit() == 1) {
          Set<String> duplicateEmails = new HashSet<>();
          for (ParticipantBean participant : participantRespBean.getParticipants()) {

            List<ParticipantRegistrySite> registry =
                commonService.getParticipantRegistry(
                    siteBo.getStudyInfo().getId(), participant.getEmail());
            if (CollectionUtils.isEmpty(registry)) {
              ParticipantRegistrySite participantRegistrySite = new ParticipantRegistrySite();
              participantRegistrySite.setEmail(participant.getEmail());
              participantRegistrySite.setSites(siteBo);
              participantRegistrySite.setOnboardingStatus("N");
              participantRegistrySite.setEnrollmentToken(TokenUtil.randomString(8));
              participantRegistrySite.setCreated(new Date());
              participantRegistrySite.setStudyInfo(siteBo.getStudyInfo());
              participantRegistrySite.setCreatedBy(userId);
              participantRegistrySiteDAO.saveParticipantRegistry(participantRegistrySite);
              participant.setSuccessBean(new SuccessBean(SuccessBean.IMPORT_PARTICIPANT_SUCCESS));
            } else {
              duplicateEmails.add(participant.getEmail());
              continue;
            }
          }
          if (!CollectionUtils.isEmpty(duplicateEmails)) {
            participantRespBean.setDuplicateEmails(duplicateEmails);
          }
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          participantRespBean.setErrorBean(errorBean);
          return;
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_865.code(),
                ErrorCode.EC_865.errorMessage(),
                "error",
                ErrorCode.EC_865.errorMessage());
        participantRespBean.setErrorBean(errorBean);
        return;
      }
    } catch (Exception e) {
      logger.error("SitesServiceImpl - addNewParticipant() : error");
      throw e;
    }
    logger.info("SitesServiceImpl - addNewParticipant() : ends");
  }

  @Override
  public void inviteParticipants(
      InviteParticipantBean inviteParticipantBean, Integer siteId, Integer userId) {
    logger.info("SitesServiceImpl - inviteParticipants - starts");
    try {
      SiteBo siteBo = siteDao.getSiteDetails(siteId);
      if (siteBo != null && siteBo.getStatus() == 1) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(siteId, userId);
        if (sitePermission != null && sitePermission.getEdit() == 1) {
          for (int id : inviteParticipantBean.getId()) {
            ParticipantRegistrySite part = siteDao.getParticipantSiteRegistry(id);
            if (part != null
                && ("I".equals(part.getOnboardingStatus())
                    || "N".equals(part.getOnboardingStatus()))) {
              String token = TokenUtil.randomString(AppConstants.TOKEN_SIZE);
              siteDao.updateEnrollment(token, id, part.getOnboardingStatus());
              String subject = applicationConfiguration.getParticipantInviteSubject();
              subject = subject.replaceAll("\\$study name\\$", siteBo.getStudyInfo().getName());
              String body = applicationConfiguration.getParticipantInviteBody();
              body = body.replaceAll("\\$study name\\$", siteBo.getStudyInfo().getName());
              body = body.replaceAll("\\$org name\\$", "");
              body = body.replaceAll("\\$enrolment token\\$", token);

              MailMessages mailMessages =
                  new MailMessages(part.getEmail(), "", subject, body, null, null, null);
              List<MailMessages> messages = new LinkedList<MailMessages>();
              messages.add(mailMessages);
              String message = commonDao.addToMailMessages(messages);
              if (AppConstants.SUCCESS.equals(message)) {
                logger.info("SitesServiceImpl - Successfully added to mail_messages");
              } else {
                logger.info("SitesServiceImpl - error while adding to mail_messages");
              }
            } else {
              continue;
            }
          }
          inviteParticipantBean.setSuccessBean(new SuccessBean(SuccessBean.PARTICIPANTS_INVITED));
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          inviteParticipantBean.setErrorBean(errorBean);
          return;
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_865.code(),
                ErrorCode.EC_865.errorMessage(),
                "error",
                ErrorCode.EC_865.errorMessage());
        inviteParticipantBean.setErrorBean(errorBean);
        return;
      }
    } catch (Exception e) {
      logger.error("SitesServiceImpl - inviteParticipants - error");
      throw e;
    }
    logger.info("SitesServiceImpl - inviteParticipants - ends");
  }

  public void updateOnboardingStatus(
      EnableDisableParticipantBean bean, Integer siteId, Integer userId) {
    logger.info("SitesServiceImpl - updateOnboardingStatus - starts");
    try {
      SiteBo siteBo = siteDao.getSiteDetails(siteId);
      if (siteBo != null && siteBo.getStatus() == 1) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(siteId, userId);
        if (sitePermission != null && sitePermission.getEdit() == 1) {
          List<ParticipantRegistrySite> list =
              participantRegistrySiteDAO.getParticipantRegistry(bean.getId());
          List<Integer> ids = new LinkedList<Integer>();
          if ("1".equals(bean.getStatus())) {
            for (ParticipantRegistrySite part : list) {
              List<ParticipantRegistrySite> existing =
                  commonService.getParticipantRegistry(
                      siteBo.getStudyInfo().getId(), part.getEmail());
              if (CollectionUtils.isEmpty(existing)) {
                ids.add(part.getId());
              } else {
                boolean existingNewInvited = false;
                for (ParticipantRegistrySite exist : existing) {
                  if ("N".equals(exist.getOnboardingStatus())
                      || "I".equals(exist.getOnboardingStatus())) {
                    existingNewInvited = true;
                    break;
                  }
                }
                if (!existingNewInvited) {
                  ids.add(part.getId());
                }
              }
            }
            String mesg = participantRegistrySiteDAO.updateOnboardingStatus(ids, "N");
            if (AppConstants.SUCCESS.equals(mesg)) {
              bean.setSuccessBean(new SuccessBean(SuccessBean.PARTICIPANT_ENABLED));
            }
          } else {
            for (ParticipantRegistrySite part : list) {
              ids.add(part.getId());
            }
            String mesg = participantRegistrySiteDAO.updateOnboardingStatus(ids, "D");
            if (AppConstants.SUCCESS.equals(mesg)) {
              bean.setSuccessBean(new SuccessBean(SuccessBean.PARTICIPANT_DISABLED));
            }
          }
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          bean.setErrorBean(errorBean);
          logger.info("SitesServiceImpl - updateOnboardingStatus - ends");
          return;
        }
      } else {
        ErrorBean errorBean =
            AppUtil.dynamicResponse(
                ErrorCode.EC_865.code(),
                ErrorCode.EC_865.errorMessage(),
                "error",
                ErrorCode.EC_865.errorMessage());
        bean.setErrorBean(errorBean);
        logger.info("SitesServiceImpl - updateOnboardingStatus - ends");
        return;
      }
    } catch (Exception e) {
      logger.error("SitesServiceImpl - updateOnboardingStatus - error");
      throw e;
    }
    logger.info("SitesServiceImpl - updateOnboardingStatus - ends");
  }

  @Override
  public ConsentDocumentBean getConsentDocument(Integer consentId, Integer userId) {
    logger.info(" SiteServiceImpl - getConsentDocument():starts");

    ConsentDocumentBean consentDocumentBean = new ConsentDocumentBean();
    try {
      StudyConsentBO studyConsentBO = siteDao.getstudyConsentBO(consentId);

      if (studyConsentBO != null) {
        SitePermission sitePermission =
            sitePermissionService.getSitePermissionForUser(
                studyConsentBO.getParticipantStudiesBO().getSiteBo().getId(), userId);
        if (sitePermission != null) {
          if (studyConsentBO.getPdfStorage() == 1) {
            String path = studyConsentBO.getPdfPath();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            cloudStorageService.downloadFileTo(path, baos);
            consentDocumentBean.setContent(new String(baos.toByteArray()));
          }
          consentDocumentBean.setType("application/pdf");
          consentDocumentBean.setError(
              AppUtil.dynamicResponse(
                  ErrorCode.EC_200.code(),
                  ErrorCode.EC_200.errorMessage(),
                  "error",
                  ErrorCode.EC_200.errorMessage()));
        } else {
          ErrorBean errorBean =
              AppUtil.dynamicResponse(
                  ErrorCode.EC_863.code(),
                  ErrorCode.EC_863.errorMessage(),
                  "error",
                  ErrorCode.EC_863.errorMessage());
          consentDocumentBean.setError(errorBean);
        }
      } else {
        consentDocumentBean.setError(
            AppUtil.dynamicResponse(
                ErrorCode.EC_964.code(),
                ErrorCode.EC_964.errorMessage(),
                "error",
                ErrorCode.EC_964.errorMessage()));
      }
    } catch (Exception e) {
      logger.info(" SiteServiceImpl - getConsentDocument():error", e);
      consentDocumentBean.setError(
          AppUtil.dynamicResponse(
              ErrorCode.EC_500.code(),
              ErrorCode.EC_500.errorMessage(),
              "error",
              ErrorCode.EC_500.errorMessage()));
    }
    logger.info(" SiteServiceImpl - getConsentDocument():ends");

    return consentDocumentBean;
  }
}
