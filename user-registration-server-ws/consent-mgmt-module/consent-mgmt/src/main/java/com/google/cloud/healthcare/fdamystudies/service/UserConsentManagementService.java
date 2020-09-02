/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.consent.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.consent.model.StudyConsentBO;
import java.util.List;

public interface UserConsentManagementService {

  public ConsentStudyResponseBean getStudyConsentDetails(
      String userId, Integer studyId, String consentVersion);

  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList);

  public String saveStudyConsent(StudyConsentBO studyConsent);
}
