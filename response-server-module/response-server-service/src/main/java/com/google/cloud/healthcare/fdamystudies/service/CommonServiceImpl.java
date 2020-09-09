/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantInfoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired private RestTemplate restTemplate;

  @Autowired private CommonDao commonDao;

  @Autowired private ApplicationConfiguration appConfig;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public ParticipantInfoEntity getParticipantInfoDetails(String participantId) {
    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    ParticipantInfoEntity participantInfo = null;
    try {
      participantInfo = commonDao.getParticipantInfoDetails(participantId);
    } catch (Exception e) {
      logger.error("CommonServiceImpl getParticipantInfoDetails() - error ", e);
    }

    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    return participantInfo;
  }
}
