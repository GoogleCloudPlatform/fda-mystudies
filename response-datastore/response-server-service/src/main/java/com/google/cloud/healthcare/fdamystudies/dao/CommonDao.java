/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;

public interface CommonDao {
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId);

  public StudyEntity getStudyDetails(String customStudyId);
}
