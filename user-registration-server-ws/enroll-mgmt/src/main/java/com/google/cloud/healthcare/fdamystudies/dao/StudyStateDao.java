package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;

public interface StudyStateDao {

  public List<ParticipantStudiesBO> getParticipantStudiesList(String userId);

  public String saveParticipantStudies(List<ParticipantStudiesBO> participantStudiesList);
}
