/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.NOT_APPLICABLE;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.SiteResponse;
import com.google.cloud.healthcare.fdamystudies.common.DateTimeUtils;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.StringUtils;

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
      Entry<StudyEntity, List<ParticipantStudyEntity>> entry) {
    List<AppSiteDetails> sites = new ArrayList<>();
    for (ParticipantStudyEntity enrollment : entry.getValue()) {
      AppSiteDetails studiesEnrollment = new AppSiteDetails();
      studiesEnrollment.setCustomSiteId(enrollment.getSite().getLocation().getCustomId());
      studiesEnrollment.setSiteId(enrollment.getSite().getId());
      studiesEnrollment.setSiteName(enrollment.getSite().getLocation().getName());
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
}
