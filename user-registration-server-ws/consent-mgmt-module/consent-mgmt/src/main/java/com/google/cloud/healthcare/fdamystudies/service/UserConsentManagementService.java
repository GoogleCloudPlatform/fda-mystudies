/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import java.util.List;

public interface UserConsentManagementService {

  public ParticipantStudiesBO getParticipantStudies(Integer studyId, String userId);

  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList);

  public StudyConsentBO getStudyConsent(String userId, Integer studyId, String consentVersion);

  public String saveStudyConsent(StudyConsentBO studyConsent);

  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, Integer studyId, String consentVersion, AuditLogEventRequest auditRequest);

  public StudyInfoBean getStudyInfoId(String customStudyId);

  public Integer getUserDetailsId(String userId);
}
