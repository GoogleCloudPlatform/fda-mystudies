/*
 * Copyright 2020 Google LLC
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
import com.google.cloud.healthcare.fdamystudies.enroll.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.UserDetailsBO;
import java.util.List;

public interface StudyStateService {

  public List<ParticipantStudiesBO> getParticipantStudiesList(UserDetailsBO user);

  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudiesBO> existParticipantStudies,
      String userId,
      AuditLogEventRequest auditRequest);

  public List<StudyStateBean> getStudiesState(String userId);

  public WithDrawFromStudyRespBean withdrawFromStudy(
      String participantId, String studyId, boolean delete);
}
