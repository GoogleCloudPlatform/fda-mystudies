/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.FHIRresponseEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import java.util.List;

public interface CommonDao {
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId);

  public StudyEntity getStudyDetails(String customStudyId);

  public void saveToFHIREntity(String getFhirJson, String studyId);

  public void updateDidStatus(String questionnaireReference);

  public List<FHIRresponseEntity> getFhirDetails(Boolean didStatus);
}
