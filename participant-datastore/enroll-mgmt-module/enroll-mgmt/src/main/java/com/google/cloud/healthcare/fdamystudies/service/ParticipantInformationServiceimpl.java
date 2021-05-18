/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.ParticipantInfoRespBean;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantInformationDao;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipantInformationServiceimpl implements ParticipantInformationService {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(ParticipantInformationServiceimpl.class.getName());

  @Autowired ParticipantInformationDao participantInfoDao;
  @Autowired CommonDao commonDao;

  @Override
  @Transactional(readOnly = true)
  public ParticipantInfoRespBean getParticipantInfoDetails(String particpinatId, String studyId) {
    logger.entry("Begin getParticipantDetails()");
    ParticipantInfoRespBean participantInforespBean = null;
    String studyInfoId = String.valueOf(0);
    studyInfoId = commonDao.getStudyId(studyId);
    participantInforespBean =
        participantInfoDao.getParticipantInfoDetails(particpinatId, studyInfoId);

    logger.exit("getParticipantDetails() - ends ");
    return participantInforespBean;
  }
}
