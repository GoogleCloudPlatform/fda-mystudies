/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.enroll.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.UserDetailsBO;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class CommonServiceImpl implements CommonService {

  @Autowired CommonDao commonDao;

  private static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);

  @Override
  public UserDetailsBO getUserInfoDetails(String userId) {
    logger.info("CommonServiceImpl getUserInfoDetails() - Starts ");
    UserDetailsBO userDetailsBO = null;
    try {
      if (!StringUtils.isEmpty(userId)) {
        userDetailsBO = commonDao.getUserInfoDetails(userId);
      }
    } catch (Exception e) {
      logger.error("CommonServiceImpl getUserInfoDetails() - error ", e);
    }
    logger.info("CommonServiceImpl getUserInfoDetails() - Ends ");
    return userDetailsBO;
  }

  @Override
  public List<ActivityLogBO> createActivityLogList(
      String userId, String activityName, List<String> activityDescList) {
    logger.info("CommonServiceImpl createActivityLogList() - starts ");
    List<ActivityLogBO> activityLogBoList = new LinkedList<>();
    if (!StringUtils.isBlank(userId)
        && !StringUtils.isBlank(activityName)
        && !CollectionUtils.isEmpty(activityDescList)) {
      try {
        activityLogBoList = commonDao.createActivityLogList(userId, activityName, activityDescList);
      } catch (Exception e) {
        logger.error("CommonServiceImpl createActivityLogList() - error ", e);
      }
    }
    logger.info("CommonServiceImpl createActivityLogList() - ends ");
    return activityLogBoList;
  }

  @Override
  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc) {
    logger.info("CommonServiceImpl createActivityLog() - starts ");
    ActivityLogBO activityLog = new ActivityLogBO();
    if (!StringUtils.isBlank(userId)
        && !StringUtils.isBlank(activityName)
        && !StringUtils.isBlank(activtyDesc)) {
      try {
        activityLog.setAuthUserId(userId);
        activityLog.setActivityName(activityName);
        activityLog.setActivtyDesc(activtyDesc);
        activityLog.setActivityDateTime(LocalDateTime.now());
        commonDao.createActivityLog(userId, activityName, activtyDesc);
      } catch (Exception e) {
        logger.error("CommonServiceImpl createActivityLog() - error ", e);
      }
    }
    logger.info("CommonServiceImpl createActivityLog() - ends ");
    return activityLog;
  }
}
