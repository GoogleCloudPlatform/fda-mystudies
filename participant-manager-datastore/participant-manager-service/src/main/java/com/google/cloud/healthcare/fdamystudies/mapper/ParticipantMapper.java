/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

import com.google.cloud.healthcare.fdamystudies.beans.Enrollment;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.EnrollmentStatus;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public final class ParticipantMapper {

  private ParticipantMapper() {}

  public static ParticipantDetail fromParticipantStudy(ParticipantStudyEntity participantStudy) {
    ParticipantDetail participantDetail = new ParticipantDetail();
    participantDetail.setId(participantStudy.getId());

    if (participantStudy.getStatus().equalsIgnoreCase(EnrollmentStatus.IN_PROGRESS.getStatus())) {
      participantDetail.setEnrollmentStatus(EnrollmentStatus.ENROLLED.getStatus());
    } else {
      participantDetail.setEnrollmentStatus(participantStudy.getStatus());
    }

    if (participantStudy.getSite() != null) {
      participantDetail.setSiteId(participantStudy.getSite().getId());
      participantDetail.setCustomLocationId(participantStudy.getSite().getLocation().getCustomId());
      participantDetail.setLocationName(participantStudy.getSite().getLocation().getName());
    }
    if (participantStudy.getParticipantRegistrySite() != null) {
      if (participantStudy.getParticipantRegistrySite().getEmail() != null) {
        participantDetail.setEmail(participantStudy.getParticipantRegistrySite().getEmail());
      }

      String onboardingStatusCode =
          participantStudy.getParticipantRegistrySite().getOnboardingStatus();
      onboardingStatusCode =
          StringUtils.defaultString(onboardingStatusCode, OnboardingStatus.DISABLED.getCode());
      participantDetail.setOnboardingStatus(
          OnboardingStatus.fromCode(onboardingStatusCode).getStatus());

      String invitedDate =
          DateTimeUtils.format(participantStudy.getParticipantRegistrySite().getInvitationDate());
      participantDetail.setInvitedDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));
    }

    String enrollmentDate = DateTimeUtils.format(participantStudy.getEnrolledDate());
    participantDetail.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));
    return participantDetail;
  }

  public static ParticipantRegistryDetail fromStudyAndApp(StudyEntity study, AppEntity app) {
    ParticipantRegistryDetail participantRegistryDetail = new ParticipantRegistryDetail();
    participantRegistryDetail.setStudyId(study.getId());
    participantRegistryDetail.setCustomStudyId(study.getCustomId());
    participantRegistryDetail.setStudyName(study.getName());
    participantRegistryDetail.setStudyType(study.getType());
    participantRegistryDetail.setAppId(app.getId());
    participantRegistryDetail.setAppName(app.getAppName());
    participantRegistryDetail.setCustomAppId(app.getAppId());
    return participantRegistryDetail;
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
      SiteEntity site, SitePermissionEntity sitePermission, String siteId) {
    ParticipantRegistryDetail participants = new ParticipantRegistryDetail();
    participants.setSiteStatus(site.getStatus());
    participants.setSiteId(siteId);
    if (site.getStudy() != null) {
      StudyEntity study = site.getStudy();
      participants.setStudyId(study.getId());
      participants.setStudyName(study.getName());
      participants.setCustomStudyId(study.getCustomId());
      participants.setSitePermission(sitePermission.getCanEdit().value());
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
      String enrollmentStatus =
          EnrollmentStatus.IN_PROGRESS.getStatus().equals(participantStudy.getStatus())
              ? EnrollmentStatus.ENROLLED.getStatus()
              : participantStudy.getStatus();
      participant.setEnrollmentStatus(enrollmentStatus);
      String enrollmentDate = DateTimeUtils.format(participantStudy.getEnrolledDate());
      participant.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));
    } else {
      if (OnboardingStatus.NEW.getCode().equals(onboardingStatusCode)
          || OnboardingStatus.INVITED.getCode().equals(onboardingStatusCode)) {
        participant.setEnrollmentStatus(CommonConstants.YET_TO_ENROLL);
      }
    }
    String invitedDate = DateTimeUtils.format(participantRegistrySite.getInvitationDate());
    participant.setInvitedDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));
    return participant;
  }

  public static ParticipantDetail toParticipantDetails(UserDetailsEntity userDetailsEntity) {
    ParticipantDetail participant = new ParticipantDetail();
    participant.setUserDetailsId(userDetailsEntity.getId());
    participant.setEmail(userDetailsEntity.getEmail());
    UserStatus userStatus = UserStatus.fromValue(userDetailsEntity.getStatus());
    participant.setRegistrationStatus(userStatus.getDescription());
    participant.setRegistrationDate(DateTimeUtils.format(userDetailsEntity.getVerificationDate()));
    return participant;
  }

  public static void addEnrollments(
      ParticipantDetail participantDetail, List<ParticipantStudyEntity> participantsEnrollments) {
    for (ParticipantStudyEntity participantsEnrollment : participantsEnrollments) {
      Enrollment enrollment = new Enrollment();
      enrollment.setEnrollmentStatus(participantsEnrollment.getStatus());
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
    participantDetail.setCustomStudyId(participantRegistry.getStudy().getCustomId());
    participantDetail.setLocationName(participantRegistry.getSite().getLocation().getName());
    participantDetail.setSiteId(participantRegistry.getSite().getId());
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
    participantDetail.setOnboardringStatus(status);
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
}
