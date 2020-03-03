package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;

public interface ParticipantInformationService {

  public ParticipantInfoRespBean getParticipantInfoDetails(String particpinatId, String studyId);
}
