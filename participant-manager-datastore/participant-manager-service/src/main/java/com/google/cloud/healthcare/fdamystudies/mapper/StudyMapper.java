/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

  public static List<AppStudyDetails> toAppStudyDetailsList(
      Map<StudyEntity, List<ParticipantStudyEntity>> enrolledStudiesByStudyInfoId) {

    List<AppStudyDetails> appStudyDetailsList = new ArrayList<>();

    for (Entry<StudyEntity, List<ParticipantStudyEntity>> entry :
        enrolledStudiesByStudyInfoId.entrySet()) {
      AppStudyDetails appStudyDetails = new AppStudyDetails();
      StudyEntity study = entry.getKey();
      appStudyDetails.setCustomStudyId(study.getCustomId());
      appStudyDetails.setStudyName(study.getName());
      appStudyDetails.setStudyId(study.getId());
      List<AppSiteDetails> sites = SiteMapper.toParticipantSiteList(entry);
      appStudyDetails.setSites(sites);
      appStudyDetailsList.add(appStudyDetails);
    }
    return appStudyDetailsList;
  }

  public static StudyDetails toStudyDetails(StudyEntity study) {
    StudyDetails studyDetail = new StudyDetails();
    studyDetail.setId(study.getId());
    studyDetail.setCustomId(study.getCustomId());
    studyDetail.setName(study.getName());
    studyDetail.setType(study.getType());
    studyDetail.setAppId(study.getApp().getAppId());
    studyDetail.setAppInfoId(study.getApp().getId());
    studyDetail.setLogoImageUrl(study.getLogoImageUrl());

    return studyDetail;
  }
}
