/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public ParticipantBo getParticipantInfoDetails(String participantId) {
    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    ParticipantBo participantInfo = null;
    participantInfo = commonDao.getParticipantInfoDetails(participantId);

    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    return participantInfo;
  }
}
