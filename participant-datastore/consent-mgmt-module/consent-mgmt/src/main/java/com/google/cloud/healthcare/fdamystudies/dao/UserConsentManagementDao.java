/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.List;

public interface UserConsentManagementDao {

  public ParticipantStudyEntity getParticipantStudies(String studyInfoId, String userId);

  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList);

  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion);

  public String saveStudyConsent(StudyConsentEntity studyConsent);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId);

  public StudyEntity getStudyInfo(String customStudyId);

  public String getUserDetailsId(String userId);
}
