/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import java.util.List;

public interface StudyStateDao {

  public String saveParticipantStudies(List<ParticipantStudyEntity> participantStudiesList);

  public String getEnrollTokenForParticipant(String participantRegistryId);

  public String withdrawFromStudy(String participantId, String studyId);
}
