/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ParticipantRegistryResponse;
import com.google.cloud.healthcare.fdamystudies.beans.StudyResponse;

public interface StudyService {

  public StudyResponse getStudies(String userId, Integer limit, Integer offset, String searchTerm);

  public ParticipantRegistryResponse getStudyParticipants(
      String userId,
      String studyId,
      String[] excludeParticipantStudyStatus,
      AuditLogEventRequest auditRequest,
      Integer page,
      Integer limit);
}
