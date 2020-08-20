/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.consent.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired CommonDao commonDao;

  @Autowired private ActivityLogRepository activityLogRepository;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public Integer getUserDetailsId(String userId) {
    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Starts ");
    Integer userDetailId = null;
    try {
      userDetailId = commonDao.getUserDetailsId(userId);
    } catch (Exception e) {
      logger.error("UserConsentManagementServiceImpl getStudyInfoId() - error ", e);
    }

    logger.info("UserConsentManagementServiceImpl getUserDetailsId() - Ends ");
    return userDetailId;
  }

  @Override
  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("CommonServiceImpl createActivityLog() - starts ");
    ActivityLogBO activityLog = new ActivityLogBO();
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
