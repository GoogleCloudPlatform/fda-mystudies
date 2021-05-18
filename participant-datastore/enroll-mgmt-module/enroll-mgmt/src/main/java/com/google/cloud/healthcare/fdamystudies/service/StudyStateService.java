/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.WithDrawFromStudyRespBean;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;

public interface StudyStateService {

  public List<ParticipantStudyEntity> getParticipantStudiesList(
      UserDetailsEntity user, List<StudiesBean> studiesBeenList);

  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudyEntity> existParticipantStudies,
      AuditLogEventRequest auditRequest,
      UserDetailsEntity user);

  public List<StudyStateBean> getStudiesState(String userId)
      throws javax.transaction.SystemException;

  public WithDrawFromStudyRespBean withdrawFromStudy(
      String participantId, String studyId, AuditLogEventRequest auditRequest);

  public String getSiteId(String userId, String token);
}
