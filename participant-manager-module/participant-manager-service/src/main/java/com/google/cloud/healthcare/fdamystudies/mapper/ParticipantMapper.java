/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.Enrollment;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetails;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.common.CommonConstants;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
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

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

public final class ParticipantMapper {

  private ParticipantMapper() {}

  public static ParticipantDetail fromParticipantStudy(ParticipantStudyEntity participantStudy) {
    ParticipantDetail participantDetail = new ParticipantDetail();
    participantDetail.setId(participantStudy.getParticipantId());
    participantDetail.setEnrollmentStatus(participantStudy.getStatus());
    participantDetail.setEmail(participantStudy.getParticipantRegistrySite().getEmail());
    participantDetail.setSiteId(participantStudy.getSite().getId());
    participantDetail.setCustomLocationId(participantStudy.getSite().getLocation().getCustomId());
    participantDetail.setLocationName(participantStudy.getSite().getLocation().getName());

    String invitedDate =
        DateTimeUtils.format(participantStudy.getParticipantRegistrySite().getInvitationDate());
    participantDetail.setInvitedDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));

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
      participants.setSitePermission(sitePermission.getCanEdit());
      fromAppInfo(participants, study);
      fromLocation(site, participants);
    }
    return participants;
  }

  private static void fromAppInfo(ParticipantRegistryDetail participants, StudyEntity study) {
    if (study.getAppInfo() != null) {
      participants.setAppName(study.getAppInfo().getAppName());
      participants.setCustomAppId(study.getAppInfo().getAppId());
      participants.setAppId(study.getAppInfo().getAppId());
    }
  }

  private static void fromLocation(SiteEntity site, ParticipantRegistryDetail participants) {
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
      participant.setEnrollmentStatus(participantStudy.getStatus());
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

  public static ParticipantDetails toParticipantDetails(UserDetailsEntity userDetailsEntity) {
    ParticipantDetails participant = new ParticipantDetails();
    participant.setUserDetailsId(userDetailsEntity.getId());
    participant.setEmail(userDetailsEntity.getEmail());
    UserStatus userStatus = UserStatus.fromValue(userDetailsEntity.getStatus());
    participant.setRegistrationStatus(userStatus.getDescription());
    participant.setRegistrationDate(DateTimeUtils.format(userDetailsEntity.getVerificationDate()));
    return participant;
  }

  public static void addEnrollments(
      ParticipantDetails participantDetails, List<ParticipantStudyEntity> participantsEnrollments) {
    for (ParticipantStudyEntity participantsEnrollment : participantsEnrollments) {
      Enrollment enrollment = new Enrollment();
      enrollment.setEnrollmentStatus(participantsEnrollment.getStatus());
      enrollment.setParticipantId(participantsEnrollment.getParticipantId());

      String enrollmentDate = DateTimeUtils.format(participantsEnrollment.getEnrolledDate());
      enrollment.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));

      String withdrawalDate = DateTimeUtils.format(participantsEnrollment.getWithdrawalDate());
      enrollment.setWithdrawalDate(StringUtils.defaultIfEmpty(withdrawalDate, NOT_APPLICABLE));
      participantDetails.getEnrollments().add(enrollment);
    }
  }

  public static ParticipantDetails toParticipantDetailsResponse(
      ParticipantRegistrySiteEntity participantRegistry) {
    ParticipantDetails participantDetails = new ParticipantDetails();
    participantDetails.setParticipantRegistrySiteid(participantRegistry.getId());
    participantDetails.setAppName(participantRegistry.getStudy().getAppInfo().getAppName());
    participantDetails.setCustomAppId(participantRegistry.getStudy().getAppInfo().getAppId());
    participantDetails.setStudyName(participantRegistry.getStudy().getName());
    participantDetails.setCustomStudyId(participantRegistry.getStudy().getCustomId());
    participantDetails.setLocationName(participantRegistry.getSite().getLocation().getName());
    participantDetails.setCustomLocationId(
        participantRegistry.getSite().getLocation().getCustomId());
    participantDetails.setEmail(participantRegistry.getEmail());

    String invitedDate = DateTimeUtils.format(participantRegistry.getInvitationDate());
    participantDetails.setInvitationDate(StringUtils.defaultIfEmpty(invitedDate, NOT_APPLICABLE));

    OnboardingStatus onboardingStatus =
        OnboardingStatus.fromCode(participantRegistry.getOnboardingStatus());

    String status =
        (OnboardingStatus.INVITED == onboardingStatus || OnboardingStatus.NEW == onboardingStatus)
            ? onboardingStatus.getStatus()
            : OnboardingStatus.DISABLED.getStatus();
    participantDetails.setOnboardringStatus(status);
    return participantDetails;
  }
}
