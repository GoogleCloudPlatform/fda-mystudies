package com.google.cloud.healthcare.fdamystudies.task;

import com.google.cloud.healthcare.fdamystudies.service.UserManagementProfileService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeactivateAccountScheduledTask {
  private XLogger logger =
      XLoggerFactory.getXLogger(DeactivateAccountScheduledTask.class.getName());

  @Autowired UserManagementProfileService userManagementProfService;

  @Scheduled(
      fixedDelayString = "${fixed.delay.milliseconds}",
      initialDelayString = "${initial.delay.milliseconds}")
  public void processDeactivatePendingRequests() {
    logger.entry("begin processDeactivatePendingRequests()");

    userManagementProfService.processDeactivatePendingRequests();

    logger.exit("processDeactivatePendingRequests() completed");
  }
}
