/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.AppStudyDetails;
import com.google.cloud.healthcare.fdamystudies.beans.AppStudyResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyDetails;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppStudySiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.StudySiteInfo;

public final class StudyMapper {

  private StudyMapper() {}

  public static AppStudyResponse toAppStudyResponse(AppStudySiteInfo study) {
    AppStudyResponse appStudyResponse = new AppStudyResponse();
    appStudyResponse.setStudyId(study.getStudyId());
    appStudyResponse.setCustomStudyId(study.getCustomStudyId());
    appStudyResponse.setStudyName(study.getStudyName());
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
    studyDetail.setStudyStatus(studySiteInfo.getStudyStatus());
    return studyDetail;
  }
}
