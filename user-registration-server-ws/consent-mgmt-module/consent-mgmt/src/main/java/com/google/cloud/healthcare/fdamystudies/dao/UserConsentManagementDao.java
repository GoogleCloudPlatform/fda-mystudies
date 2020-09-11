/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import java.util.List;

public interface UserConsentManagementDao {

  public ParticipantStudiesBO getParticipantStudies(Integer studyInfoId, String userId);

  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList);

  public StudyConsentBO getStudyConsent(String userId, Integer studyId, String consentVersion);

  public String saveStudyConsent(StudyConsentBO studyConsent);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId);

  public StudyInfoBean getStudyInfoId(String customStudyId);

  public Integer getUserDetailsId(String userId);
}
