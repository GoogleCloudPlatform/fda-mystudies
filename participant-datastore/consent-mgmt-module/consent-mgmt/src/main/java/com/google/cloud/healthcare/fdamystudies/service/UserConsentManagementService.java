/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.List;

public interface UserConsentManagementService {

  public ParticipantStudyEntity getParticipantStudies(String string, String userId);

  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList);

  public StudyConsentEntity getStudyConsent(String userId, String studyId, String consentVersion);

  public String saveStudyConsent(StudyConsentEntity studyConsent);

  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, String studyId, String consentVersion, AuditLogEventRequest auditRequest);

  public StudyEntity getStudyInfo(String customStudyId);

  public String getUserDetailsId(String userId);
}
