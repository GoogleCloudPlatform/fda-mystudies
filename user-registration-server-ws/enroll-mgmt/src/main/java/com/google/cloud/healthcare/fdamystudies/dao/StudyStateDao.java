/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import java.util.List;

public interface StudyStateDao {

  public List<ParticipantStudiesBO> getParticipantStudiesList(UserDetailsBO user);

  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList);

  public String getEnrollTokenForParticipant(Integer participantRegistryId);

  public String withdrawFromStudy(String participantId, String studyId, boolean delete);
}
