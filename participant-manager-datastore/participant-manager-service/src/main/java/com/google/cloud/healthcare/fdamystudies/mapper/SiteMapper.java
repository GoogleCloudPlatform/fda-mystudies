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
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppSiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
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

  public static AppSiteDetails toAppSiteDetails(
      AppSiteInfo appSiteInfo, AppParticipantsInfo appParticipantsInfo) {
    AppSiteDetails appSiteDetails = new AppSiteDetails();
    appSiteDetails.setSiteId(appSiteInfo.getSiteId());
    appSiteDetails.setCustomSiteId(appSiteInfo.getLocationCustomId());
    appSiteDetails.setSiteName(appSiteInfo.getLocationName());
    appSiteDetails.setSiteStatus(appParticipantsInfo.getParticipantStudyStatus());

    String withdrawalDate = DateTimeUtils.format(appParticipantsInfo.getWithdrawalTime());
    appSiteDetails.setWithdrawlDate(StringUtils.defaultIfEmpty(withdrawalDate, NOT_APPLICABLE));

    String enrollmentDate = DateTimeUtils.format(appParticipantsInfo.getEnrolledTime());
    appSiteDetails.setEnrollmentDate(StringUtils.defaultIfEmpty(enrollmentDate, NOT_APPLICABLE));

    return appSiteDetails;
  }
}
