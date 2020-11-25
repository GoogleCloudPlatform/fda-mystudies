/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudySiteInfo;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

public final class StudyMapper {

  private StudyMapper() {}

  public static AppStudyResponse toAppStudyResponse(
      StudyEntity study, List<SiteEntity> sites, String[] fields) {
    AppStudyResponse appStudyResponse = new AppStudyResponse();
    appStudyResponse.setStudyId(study.getId());
    appStudyResponse.setCustomStudyId(study.getCustomId());
    appStudyResponse.setStudyName(study.getName());
    if (ArrayUtils.contains(fields, "sites")) {
      List<AppSiteResponse> appSiteResponsesList =
          CollectionUtils.emptyIfNull(sites)
              .stream()
              .map(SiteMapper::toAppSiteResponse)
              .collect(Collectors.toList());
      appStudyResponse.getSites().addAll(appSiteResponsesList);
    }
    int totalSiteCountPerStudy = appStudyResponse.getSites().size();
    appStudyResponse.setTotalSitesCount(totalSiteCountPerStudy);
    return appStudyResponse;
  }

  public static AppStudyDetails toAppStudyDetailsList(AppParticipantsInfo appParticipantsInfo) {
    AppStudyDetails appStudyDetails = new AppStudyDetails();
    appStudyDetails.setCustomStudyId(appParticipantsInfo.getCustomStudyId());
    appStudyDetails.setStudyName(appParticipantsInfo.getStudyName());
    appStudyDetails.setStudyId(appParticipantsInfo.getStudyId());
    appStudyDetails.setStudyType(appParticipantsInfo.getStudyType());
    return appStudyDetails;
  }

  public static StudyDetails toStudyDetails(StudySiteInfo studySiteInfo) {
    StudyDetails studyDetail = new StudyDetails();
    studyDetail.setId(studySiteInfo.getStudyId());
    studyDetail.setCustomId(studySiteInfo.getCustomId());
    studyDetail.setName(studySiteInfo.getStudyName());
    studyDetail.setType(studySiteInfo.getStudyType());
    studyDetail.setAppId(studySiteInfo.getCustomAppId());
    studyDetail.setAppInfoId(studySiteInfo.getAppId());
    studyDetail.setAppName(studySiteInfo.getAppName());
    studyDetail.setLogoImageUrl(studySiteInfo.getLogoImageUrl());
    studyDetail.setStudyStatus(studySiteInfo.getStudyStatus());
    return studyDetail;
  }
}
