/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantDetail;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryDetail;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;

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
      ParticipantDetail participantRequest, SiteEntity site) {
    ParticipantRegistrySiteEntity participantRegistrySite = new ParticipantRegistrySiteEntity();
    participantRegistrySite.setEmail(participantRequest.getEmail());
    participantRegistrySite.setSite(site);
    participantRegistrySite.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    participantRegistrySite.setEnrollmentToken(RandomStringUtils.randomAlphanumeric(8));
    participantRegistrySite.setStudy(site.getStudy());
    return participantRegistrySite;
  }
}
