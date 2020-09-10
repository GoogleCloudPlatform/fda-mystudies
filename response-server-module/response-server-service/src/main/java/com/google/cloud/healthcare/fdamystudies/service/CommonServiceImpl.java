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
import com.google.cloud.healthcare.fdamystudies.model.ActivityLog;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;
import java.time.LocalDateTime;
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

  @Autowired private ActivityLogRepository activityLogRepository;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public ParticipantBo getParticipantInfoDetails(String participantId) {
    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    ParticipantBo participantInfo = null;
    try {
      participantInfo = commonDao.getParticipantInfoDetails(participantId);
    } catch (Exception e) {
      logger.error("CommonServiceImpl getParticipantInfoDetails() - error ", e);
    }

    logger.info("CommonServiceImpl getParticipantInfoDetails() - starts ");
    return participantInfo;
  }

  @Override
  public ActivityLog createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("CommonServiceImpl createActivityLog() - starts ");
    ActivityLog activityLog = new ActivityLog();
    try {
      activityLog.setAuthUserId(userId);
      activityLog.setActivityName(activityName);
      activityLog.setActivtyDesc(activtyDesc);
      activityLog.setActivityDateTime(LocalDateTime.now());
      activityLogRepository.save(activityLog);

    } catch (Exception e) {
      logger.error("CommonServiceImpl createActivityLog() - error ", e);
    }
    logger.info("CommonServiceImpl createActivityLog() - ends ");

    return activityLog;
  }
}
