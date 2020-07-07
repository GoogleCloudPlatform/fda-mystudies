/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.time.LocalDateTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.repository.ActivityLogRepository;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

  private static final Logger logger = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

  @Autowired private ActivityLogRepository activityLogRepository;

  @Override
  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("ActivityLogServiceImpl createActivityLog() - starts ");
    ActivityLogBO activityLog = new ActivityLogBO();
    if (!StringUtils.isBlank(userId)
        && !StringUtils.isBlank(activityName)
        && !StringUtils.isBlank(activtyDesc)) {
      try {
        activityLog.setAuthUserId(userId);
        activityLog.setActivityName(activityName);
        activityLog.setActivtyDesc(activtyDesc);
        activityLog.setActivityDateTime(LocalDateTime.now());
        activityLogRepository.save(activityLog);
      } catch (Exception e) {
        logger.error("ActivityLogServiceImpl createActivityLog() - error ", e);
      }
    }
    logger.info("ActivityLogServiceImpl createActivityLog() - ends ");
    return activityLog;
  }
}
