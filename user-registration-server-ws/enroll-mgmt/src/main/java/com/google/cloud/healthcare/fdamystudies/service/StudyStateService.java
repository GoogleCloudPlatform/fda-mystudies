package com.google.cloud.healthcare.fdamystudies.service;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.beans.StudiesBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateBean;
import com.google.cloud.healthcare.fdamystudies.beans.StudyStateRespBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.NoStudyEnrolledException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;

public interface StudyStateService {

  public List<ParticipantStudiesBO> getParticipantStudiesList(String userId);

  public StudyStateRespBean saveParticipantStudies(
      List<StudiesBean> studiesBeenList,
      List<ParticipantStudiesBO> existParticipantStudies,
      String userId);

  public List<StudyStateBean> getStudiesState(String userId)
      throws SystemException, InvalidUserIdException, NoStudyEnrolledException;
}
