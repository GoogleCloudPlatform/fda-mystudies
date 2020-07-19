package com.google.cloud.healthcare.fdamystudies.oauthscim.task;

import com.google.cloud.healthcare.fdamystudies.oauthscim.service.UserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RemoveExpiredTempRegIdScheduledTask {

  private XLogger logger =
      XLoggerFactory.getXLogger(RemoveExpiredTempRegIdScheduledTask.class.getName());

  @Autowired private UserService userService;

  // 30min fixed delay and 10s initial delay
  @Scheduled(fixedDelay = 1800000, initialDelay = 10000)
  public void removeExpiredTempRegIds() {
    logger.info("begin removeExpiredTempRegIds()");
    userService.removeExpiredTempRegIds();
  }
}
