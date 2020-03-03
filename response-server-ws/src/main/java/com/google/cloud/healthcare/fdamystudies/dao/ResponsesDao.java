/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.Map;
import com.google.cloud.healthcare.fdamystudies.bean.StoredResponseBean;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface ResponsesDao {
  void saveStudyMetadata(String studyCollectionName, String studyId,
      Map<String, Object> dataToStore) throws ProcessResponseException;

  void saveActivityResponseData(String studyCollectionName, String studyId,
      String participantCollectionName, String activitiesCollectionName,
      Map<String, Object> dataToStoreParticipantCollectionMap,
      Map<String, Object> dataToStoreActivityResults) throws ProcessResponseException;

  StoredResponseBean getActivityResponseDataForParticipant(String studyCollectionName,
      String studyId, String siteId, String participantId, String activityId)
      throws ProcessResponseException;
}
