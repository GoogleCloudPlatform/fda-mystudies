/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.google.cloud.healthcare.fdamystudies.beans.AppSiteResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;

public final class StudyMapper {

  private StudyMapper() {}

  public static List<AppStudyResponse> toAppDetailsResponseList(
      List<StudyEntity> studies,
      Map<String, List<SiteEntity>> groupByStudyIdSiteMap,
      String[] fields) {
    List<AppStudyResponse> studyResponseList = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(studies)) {
      for (StudyEntity study : studies) {
        AppStudyResponse appStudyResponse = new AppStudyResponse();
        appStudyResponse.setStudyId(study.getId());
        appStudyResponse.setCustomStudyId(study.getCustomId());
        appStudyResponse.setStudyName(study.getName());
        if (ArrayUtils.contains(fields, "sites")) {
          List<AppSiteResponse> appSiteResponsesList =
              SiteMapper.toAppDetailsResponseList(groupByStudyIdSiteMap.get(study.getId()));
          appStudyResponse.getSites().addAll(appSiteResponsesList);
        }
        studyResponseList.add(appStudyResponse);
      }
    }
    return studyResponseList;
  }
}
