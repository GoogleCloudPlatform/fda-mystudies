/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private CommonDao commonDao;

  private XLogger logger = XLoggerFactory.getXLogger(CommonServiceImpl.class.getName());

  @Override
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId) {
    logger.entry("Begin getParticipantInfoDetails()");
    ParticipantInfoEntity participantInfo = null;
    participantInfo = commonDao.getParticipantInfoDetails(participantId);

    logger.exit("getParticipantInfoDetails() - ends ");
    return participantInfo;
  }
}
