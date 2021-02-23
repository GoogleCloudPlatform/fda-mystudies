/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ActivityResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStructureBean;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface ActivityResponseProcessorService {

  void saveActivityResponseDataForParticipant(
      QuestionnaireActivityStructureBean activityMetadatFromWcp,
      ActivityResponseBean questionnaireActivityResponseBean,
      AuditLogEventRequest auditRequest)
      throws ProcessResponseException, Exception;

  StoredResponseBean getActivityResponseDataForParticipant(
      String studyId, String siteId, String participantId, String activityId, String questionKey)
      throws ProcessResponseException;

  void updateWithdrawalStatusForParticipant(String studyId, String participantId)
      throws ProcessResponseException;
}
