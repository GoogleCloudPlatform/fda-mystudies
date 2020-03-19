package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;

public interface ParticipantInformationDao {

  public ParticipantInfoRespBean getParticipantInfoDetails(String particpinatId, String studyId);
}
