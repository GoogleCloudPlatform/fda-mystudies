package com.google.cloud.healthcare.fdamystudies.task;

import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeactivateAccountScheduledTask {

  // TODO (#1104) move initial delay and fixed delay to application*.property
  private static final int INITIAL_DELAY_MILLI_SEC = 10000;

  private static final int FIXED_DELAY_MILLI_SEC = 1800000;

  private XLogger logger =
      XLoggerFactory.getXLogger(DeactivateAccountScheduledTask.class.getName());

  @Autowired UserManagementProfileService userManagementProfService;

  // 30min fixed delay and 10s initial delay
  @Scheduled(fixedDelay = FIXED_DELAY_MILLI_SEC, initialDelay = INITIAL_DELAY_MILLI_SEC)
  public void processDeactivatePendingRequests() {
    logger.entry("begin processDeactivatePendingRequests()");

    userManagementProfService.processDeactivatePendingRequests();

    logger.exit("processDeactivatePendingRequests() completed");
  }
}
