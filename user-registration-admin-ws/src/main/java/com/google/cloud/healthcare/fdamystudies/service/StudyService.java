/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.ParticipantRegistryResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;

public interface StudyService {

  public ParticipantRegistryResponseBean getStudyParticipants(Integer userId, Integer studyId)
      throws SystemException, InvalidUserIdException;

  public DashboardBean getStudies(Integer userId);
}
