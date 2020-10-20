/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantActivitiesEntity;
import java.util.List;

public interface ParticipantActivitiesDao {

  void saveParticipantActivities(List<ParticipantActivitiesEntity> participantActivitiesList)
      throws ProcessActivityStateException;

  List<ParticipantActivitiesEntity> getParticipantActivities(String studyId, String participantId)
      throws ProcessActivityStateException;

  void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException;
}
