/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ParticipantStudyInformation;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface ParticipantStudyInfoService {
  public ParticipantStudyInformation getParticipantStudyInfo(
      String orgId, String applicationId, String studyId, String participantId)
      throws ProcessResponseException;
}
