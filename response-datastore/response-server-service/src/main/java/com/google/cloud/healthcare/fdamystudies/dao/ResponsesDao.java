/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;
import java.util.Map;

public interface ResponsesDao {
  void saveStudyMetadata(
      String studyCollectionName, String studyId, Map<String, Object> dataToStore)
      throws ProcessResponseException;

  StoredResponseBean getActivityResponseDataForParticipant(
      String studyCollectionName,
      String studyId,
      String siteId,
      String participantId,
      String activityId,
      String questionKey)
      throws ProcessResponseException;

  void saveActivityResponseData(
      String studyId,
      String studyCollectionName,
      String activitiesCollectionName,
      Map<String, Object> dataToStoreActivityResults)
      throws ProcessResponseException;

  void deleteActivityResponseDataForParticipant(
      String studyCollectionName,
      String studyId,
      String activitiesCollectionName,
      String participantId)
      throws ProcessResponseException;

  void updateWithdrawalStatusForParticipant(
      String studyCollectionName, String studyId, String participantId)
      throws ProcessResponseException;
}
