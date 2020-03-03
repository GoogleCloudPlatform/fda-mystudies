/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;

@Component
public class SchedulerServiceImpl implements SchedulerService {

  private static Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

  @Autowired private CommonDao commonDao;

  @Scheduled(cron = "0 0/2 * * * ?") // every 2 minutes everyday
  public void processEmail() {
    logger.info("SchedularServiceImpl processEmail() start ");
    try {
      commonDao.processEmail();
    } catch (Exception e) {
      logger.error("SchedularServiceImpl processEmail() error ", e);
    }
    logger.info("SchedularServiceImpl processEmail() end ");
  }
}
