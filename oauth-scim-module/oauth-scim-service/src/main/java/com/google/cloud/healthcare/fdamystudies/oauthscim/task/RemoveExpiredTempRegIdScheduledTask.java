package com.google.cloud.healthcare.fdamystudies.oauthscim.task;

import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RemoveExpiredTempRegIdScheduledTask {

  private static final int INITIAL_DELAY_MILLI_SEC = 10000;

  private static final int FIXED_DELAY_MILLI_SEC = 1800000;

  private XLogger logger =
      XLoggerFactory.getXLogger(RemoveExpiredTempRegIdScheduledTask.class.getName());

  @Autowired private UserService userService;

  // 30min fixed delay and 10s initial delay
  @Scheduled(fixedDelay = FIXED_DELAY_MILLI_SEC, initialDelay = INITIAL_DELAY_MILLI_SEC)
  public void removeExpiredTempRegIds() {
    logger.info("begin removeExpiredTempRegIds()");
    userService.removeExpiredTempRegIds();
  }
}
