/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.YET_TO_ENROLL;

import com.google.cloud.healthcare.fdamystudies.beans.Enrollment;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.Permission;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyAppDetails;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyParticipantDetails;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class ParticipantMapper {

  private ParticipantMapper() {}

  public static ParticipantDetail fromParticipantStudy(
      StudyParticipantDetails studyParticipantDetail) {
    ParticipantDetail participantDetail = new ParticipantDetail();
    participantDetail.setId(studyParticipantDetail.getParticipantId());

    participantDetail.setSiteId(studyParticipantDetail.getSiteId());
    participantDetail.setCustomLocationId(studyParticipantDetail.getLocationCustomId());
    participantDetail.setLocationName(studyParticipantDetail.getLocationName());
    participantDetail.setEmail(studyParticipantDetail.getEmail());

    String onboardingStatusCode = studyParticipantDetail.getOnboardingStatus();
    onboardingStatusCode =
        StringUtils.defaultString(onboardingStatusCode, OnboardingStatus.DISABLED.getCode());
    participantDetail.setOnboardingStatus(
        OnboardingStatus.fromCode(onboardingStatusCode).getStatus());

    String invitedDate = DateTimeUtils.format(studyParticipantDetail.getInvitedDate());
    participantDetail.setInvitedDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));

    if (studyParticipantDetail.getEnrolledStatus() != null) {
      if (EnrollmentStatus.WITHDRAWN
              .getStatus()
              .equalsIgnoreCase(studyParticipantDetail.getEnrolledStatus())
          && (OnboardingStatus.NEW.getCode().equals(onboardingStatusCode)
              || OnboardingStatus.INVITED.getCode().equals(onboardingStatusCode))) {
        participantDetail.setEnrollmentStatus(CommonConstants.YET_TO_ENROLL);
        participantDetail.setEnrollmentDate(null);
      } else {
        String enrollmentStatus =
            EnrollmentStatus.IN_PROGRESS
                    .getStatus()
                    .equalsIgnoreCase(studyParticipantDetail.getEnrolledStatus())
                ? EnrollmentStatus.ENROLLED.getStatus()
                : studyParticipantDetail.getEnrolledStatus();
        participantDetail.setEnrollmentStatus(enrollmentStatus);
        String enrollmentDate = DateTimeUtils.format(studyParticipantDetail.getEnrolledDate());
        participantDetail.setEnrollmentDate(
            StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));
      }

    } else {
      participantDetail.setEnrollmentStatus(YET_TO_ENROLL);
    }

    return participantDetail;
  }

  public static ParticipantRegistrySiteEntity fromParticipantRequest(
      ParticipantDetailRequest participantRequest, SiteEntity site) {
    ParticipantRegistrySiteEntity participantRegistrySite = new ParticipantRegistrySiteEntity();
    participantRegistrySite.setEmail(participantRequest.getEmail());
    participantRegistrySite.setSite(site);
    participantRegistrySite.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySite.setEnrollmentToken(RandomStringUtils.randomAlphanumeric(8));
    participantRegistrySite.setStudy(site.getStudy());
    return participantRegistrySite;
  }

  public static ParticipantRegistryDetail fromSite(
      SiteEntity site, Permission permission, String siteId) {
    ParticipantRegistryDetail participants = new ParticipantRegistryDetail();
    participants.setSiteStatus(site.getStatus());
    participants.setSiteId(siteId);
    if (site.getStudy() != null) {
      StudyEntity study = site.getStudy();
      participants.setStudyId(study.getId());
      participants.setStudyName(study.getName());
      participants.setCustomStudyId(study.getCustomId());
      participants.setSitePermission(permission.value());
      participants.setStudyStatus(study.getStatus());
      setParticipantRegistryAppInfo(participants, study);
      setParticipantRegistryLocation(site, participants);
    }
    return participants;
  }

  private static void setParticipantRegistryAppInfo(
      ParticipantRegistryDetail participants, StudyEntity study) {
    if (study.getApp() != null) {
      participants.setAppName(study.getApp().getAppName());
      participants.setCustomAppId(study.getApp().getAppId());
      participants.setAppId(study.getApp().getAppId());
    }
  }

  private static void setParticipantRegistryLocation(
      SiteEntity site, ParticipantRegistryDetail participants) {
    if (site.getLocation() != null) {
      participants.setLocationName(site.getLocation().getName());
      participants.setCustomLocationId(site.getLocation().getCustomId());
      participants.setLocationStatus(site.getLocation().getStatus());
    }
  }

  public static ParticipantDetail toParticipantDetails(
      List<ParticipantStudyEntity> participantStudies,
      ParticipantRegistrySiteEntity participantRegistrySite,
      ParticipantDetail participant) {

    participant.setId(participantRegistrySite.getId());
    participant.setEmail(participantRegistrySite.getEmail());
    String onboardingStatusCode = participantRegistrySite.getOnboardingStatus();
    participant.setOnboardingStatus(OnboardingStatus.fromCode(onboardingStatusCode).getStatus());
    Map<String, ParticipantStudyEntity> idMap = new HashMap<>();

    for (ParticipantStudyEntity participantStudy : participantStudies) {
      if (participantStudy.getParticipantRegistrySite() != null) {
        idMap.put(participantStudy.getParticipantRegistrySite().getId(), participantStudy);
      }
    }

    ParticipantStudyEntity participantStudy = idMap.get(participantRegistrySite.getId());
    if (participantStudy != null) {
      if (EnrollmentStatus.WITHDRAWN.getStatus().equalsIgnoreCase(participantStudy.getStatus())
          && (OnboardingStatus.NEW.getCode().equals(onboardingStatusCode)
              || OnboardingStatus.INVITED.getCode().equals(onboardingStatusCode))) {
        participant.setEnrollmentStatus(CommonConstants.YET_TO_ENROLL);
      } else {
        String enrollmentStatus =
            EnrollmentStatus.IN_PROGRESS.getStatus().equalsIgnoreCase(participantStudy.getStatus())
                ? EnrollmentStatus.ENROLLED.getStatus()
                : participantStudy.getStatus();
        participant.setEnrollmentStatus(enrollmentStatus);
        String enrollmentDate = DateTimeUtils.format(participantStudy.getEnrolledDate());
        participant.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));
      }
    } else {
      participant.setEnrollmentStatus(CommonConstants.YET_TO_ENROLL);
    }

    String invitedDate = DateTimeUtils.format(participantRegistrySite.getInvitationDate());
    participant.setInvitedDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));
    String disabledDate = DateTimeUtils.format(participantRegistrySite.getDisabledDate());
    participant.setDisabledDate(StringUtils.defaultIfEmpty(disabledDate, NOT_APPLICABLE));
    return participant;
  }

  public static ParticipantDetail toParticipantDetails(AppParticipantsInfo appParticipantInfo) {
    ParticipantDetail participant = new ParticipantDetail();
    participant.setUserDetailsId(appParticipantInfo.getUserDetailsId());
    participant.setEmail(appParticipantInfo.getEmail());
    UserStatus userStatus = UserStatus.fromValue(appParticipantInfo.getRegistrationStatus());
    participant.setRegistrationStatus(userStatus.getDescription());
    participant.setRegistrationDate(DateTimeUtils.format(appParticipantInfo.getRegistrationDate()));
    return participant;
  }

  public static void addEnrollments(
      ParticipantDetail participantDetail,
      List<ParticipantStudyEntity> participantsEnrollments,
      String onboardingStatus) {
    for (ParticipantStudyEntity participantsEnrollment : participantsEnrollments) {
      Enrollment enrollment = new Enrollment();
      if ((OnboardingStatus.INVITED.getStatus().equals(onboardingStatus)
              || OnboardingStatus.NEW.getStatus().equals(onboardingStatus))
          && EnrollmentStatus.WITHDRAWN.getStatus().equals(participantsEnrollment.getStatus())) {
        enrollment.setEnrollmentStatus(CommonConstants.YET_TO_ENROLL);
        enrollment.setParticipantId(participantsEnrollment.getParticipantId());
        enrollment.setEnrollmentDate(NOT_APPLICABLE);
        enrollment.setWithdrawalDate(NOT_APPLICABLE);
        participantDetail.getEnrollments().add(enrollment);
        // TODO: (Issue #1454) Separate records should be displayed for each time of the user
        // enrollment process
        // current implementation has single record, so added below return;
        return;
      }
      String enrollmentStatus =
          EnrollmentStatus.IN_PROGRESS.getStatus().equals(participantsEnrollment.getStatus())
              ? EnrollmentStatus.ENROLLED.getStatus()
              : participantsEnrollment.getStatus();
      enrollment.setEnrollmentStatus(enrollmentStatus);
      enrollment.setParticipantId(participantsEnrollment.getParticipantId());

      String enrollmentDate = DateTimeUtils.format(participantsEnrollment.getEnrolledDate());
      enrollment.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));

      String withdrawalDate = DateTimeUtils.format(participantsEnrollment.getWithdrawalDate());
      enrollment.setWithdrawalDate(StringUtils.defaultIfEmpty(withdrawalDate, NOT_APPLICABLE));
      participantDetail.getEnrollments().add(enrollment);
    }
  }

  public static ParticipantDetail toParticipantDetailsResponse(
      ParticipantRegistrySiteEntity participantRegistry) {
    ParticipantDetail participantDetail = new ParticipantDetail();
    participantDetail.setParticipantRegistrySiteid(participantRegistry.getId());
    participantDetail.setAppName(participantRegistry.getStudy().getApp().getAppName());
    participantDetail.setCustomAppId(participantRegistry.getStudy().getApp().getAppId());
    participantDetail.setStudyName(participantRegistry.getStudy().getName());
    participantDetail.setStudyType(participantRegistry.getStudy().getType());
    participantDetail.setStudyStatus(participantRegistry.getStudy().getStatus());
    participantDetail.setCustomStudyId(participantRegistry.getStudy().getCustomId());
    participantDetail.setLocationName(participantRegistry.getSite().getLocation().getName());
    participantDetail.setSiteId(participantRegistry.getSite().getId());
    participantDetail.setSiteStatus(participantRegistry.getSite().getStatus());
    participantDetail.setCustomLocationId(
        participantRegistry.getSite().getLocation().getCustomId());
    participantDetail.setEmail(participantRegistry.getEmail());

    String invitedDate = DateTimeUtils.format(participantRegistry.getInvitationDate());
    participantDetail.setInvitationDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));

    OnboardingStatus onboardingStatus =
        OnboardingStatus.fromCode(participantRegistry.getOnboardingStatus());

    String status =
        (OnboardingStatus.INVITED == onboardingStatus || OnboardingStatus.NEW == onboardingStatus)
            ? onboardingStatus.getStatus()
            : OnboardingStatus.DISABLED.getStatus();
    participantDetail.setOnboardingStatus(status);
    return participantDetail;
  }

  public static ParticipantRegistrySiteEntity fromParticipantDetail(
      ParticipantDetail participant, SiteEntity site) {
    ParticipantRegistrySiteEntity participantRegistrySite = new ParticipantRegistrySiteEntity();
    participantRegistrySite.setEmail(participant.getEmail());
    participantRegistrySite.setSite(site);
    participantRegistrySite.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySite.setEnrollmentToken(RandomStringUtils.randomAlphanumeric(8));
    participantRegistrySite.setStudy(site.getStudy());
    return participantRegistrySite;
  }

  public static ParticipantRegistryDetail fromStudyAppDetails(
      StudyAppDetails studyAppDetails, UserRegAdminEntity user) {
    ParticipantRegistryDetail participantRegistryDetail = new ParticipantRegistryDetail();
    participantRegistryDetail.setStudyId(studyAppDetails.getStudyId());
    participantRegistryDetail.setCustomStudyId(studyAppDetails.getCustomStudyId());
    participantRegistryDetail.setStudyName(studyAppDetails.getStudyName());
    participantRegistryDetail.setStudyType(studyAppDetails.getStudyType());
    participantRegistryDetail.setAppId(studyAppDetails.getAppId());
    participantRegistryDetail.setAppName(studyAppDetails.getAppName());
    participantRegistryDetail.setCustomAppId(studyAppDetails.getCustomAppId());
    participantRegistryDetail.setTargetEnrollment(studyAppDetails.getTargetEnrollment());
    Integer permission = user.isSuperAdmin() ? Permission.EDIT.value() : studyAppDetails.getEdit();

    participantRegistryDetail.setOpenStudySitePermission(
        studyAppDetails.getStudyType().equalsIgnoreCase(CommonConstants.OPEN_STUDY)
            ? permission
            : null);
    return participantRegistryDetail;
  }
}
