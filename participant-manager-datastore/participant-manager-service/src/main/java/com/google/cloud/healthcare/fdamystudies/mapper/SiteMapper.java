/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.model.InviteParticipantEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;

import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;

public class SiteMapper {

  private SiteMapper() {}

  public static SiteResponse toSiteResponse(SiteEntity site) {
    SiteResponse response = new SiteResponse();
    response.setSiteId(site.getId());
    return response;
  }

  public static AppSiteResponse toAppSiteResponse(SiteEntity site) {
    AppSiteResponse appSiteResponse = new AppSiteResponse();
    appSiteResponse.setSiteId(site.getId());
    appSiteResponse.setCustomLocationId(site.getLocation().getCustomId());
    appSiteResponse.setLocationDescription(site.getLocation().getDescription());
    appSiteResponse.setLocationId(site.getLocation().getId());
    appSiteResponse.setLocationName(site.getLocation().getName());
    return appSiteResponse;
  }

  public static List<AppSiteDetails> toParticipantSiteList(
      Entry<StudyEntity, List<ParticipantStudyEntity>> entry, String[] excludeSiteStatus) {
    List<AppSiteDetails> sites = new ArrayList<>();
    for (ParticipantStudyEntity enrollment : entry.getValue()) {
      if (ArrayUtils.contains(excludeSiteStatus, enrollment.getStatus())) {
        continue;
      }

      AppSiteDetails studiesEnrollment = new AppSiteDetails();

      if (enrollment.getSite() != null) {
        studiesEnrollment.setCustomSiteId(enrollment.getSite().getLocation().getCustomId());
        studiesEnrollment.setSiteId(enrollment.getSite().getId());
        studiesEnrollment.setSiteName(enrollment.getSite().getLocation().getName());
      }
      studiesEnrollment.setSiteStatus(enrollment.getStatus());

      String withdrawalDate = DateTimeUtils.format(enrollment.getWithdrawalDate());
      studiesEnrollment.setWithdrawlDate(
          StringUtils.defaultIfEmpty(withdrawalDate, NOT_APPLICABLE));

      String enrollmentDate = DateTimeUtils.format(enrollment.getEnrolledDate());
      studiesEnrollment.setEnrollmentDate(
          StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));

      sites.add(studiesEnrollment);
    }
    return sites;
  }

  public static AuditLogEventRequest prepareAuditlogRequest(
      InviteParticipantEntity inviteParticipantDetails) {
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setAppId(inviteParticipantDetails.getAppId());
    auditRequest.setAppVersion(inviteParticipantDetails.getAppVersion());
    auditRequest.setCorrelationId(inviteParticipantDetails.getCorrelationId());
    auditRequest.setSource(inviteParticipantDetails.getSource());
    auditRequest.setMobilePlatform(inviteParticipantDetails.getMobilePlatform());
    auditRequest.setUserId(inviteParticipantDetails.getUserId());
    return auditRequest;
  }

  public static InviteParticipantEntity toInviteParticipantEntity(
      AuditLogEventRequest auditRequest) {
    InviteParticipantEntity inviteParticipantsEmail = new InviteParticipantEntity();
    inviteParticipantsEmail.setStudy(auditRequest.getStudyId());
    inviteParticipantsEmail.setAppVersion(auditRequest.getAppVersion());
    inviteParticipantsEmail.setCorrelationId(auditRequest.getCorrelationId());
    inviteParticipantsEmail.setSource(auditRequest.getSource());
    inviteParticipantsEmail.setMobilePlatform(auditRequest.getMobilePlatform());
    inviteParticipantsEmail.setUserId(auditRequest.getUserId());
    return inviteParticipantsEmail;
  }

}
