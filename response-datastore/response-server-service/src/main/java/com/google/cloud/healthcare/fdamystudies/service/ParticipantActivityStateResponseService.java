/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.ActivitiesBean;
import com.google.cloud.healthcare.fdamystudies.bean.ActivityStateRequestBean;
import com.google.cloud.healthcare.fdamystudies.exception.ProcessActivityStateException;

public interface ParticipantActivityStateResponseService {

  void saveParticipantActivities(ActivityStateRequestBean activityStateBean)
      throws ProcessActivityStateException;

  ActivitiesBean getParticipantActivities(String studyId, String participantId)
      throws ProcessActivityStateException;

  void deleteParticipantActivites(String studyId, String participantId)
      throws ProcessActivityStateException;
}
