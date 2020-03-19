/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantActivitiesBo;

public interface ParticipantActivitiesDao {

  public void saveParticipantActivities(List<ParticipantActivitiesBo> participantActivitiesList)
      throws ProcessActivityStateException;

  List<ParticipantActivitiesBo> getParticipantActivities(String studyId, String participantId)
      throws ProcessActivityStateException;
}
