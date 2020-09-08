/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantActivitiesBo;
import java.util.List;

public interface ParticipantActivitiesDao {

  void saveParticipantActivities(List<ParticipantActivitiesBo> participantActivitiesList)
      throws ProcessActivityStateException;

  List<ParticipantActivitiesBo> getParticipantActivities(String studyId, String participantId)
      throws ProcessActivityStateException;

  void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException;
}
