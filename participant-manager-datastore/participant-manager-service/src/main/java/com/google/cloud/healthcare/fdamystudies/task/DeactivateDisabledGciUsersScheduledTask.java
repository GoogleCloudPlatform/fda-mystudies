package com.google.cloud.healthcare.fdamystudies.task;

import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeactivateDisabledGciUsersScheduledTask {

  private XLogger logger =
      XLoggerFactory.getXLogger(DeactivateDisabledGciUsersScheduledTask.class.getName());

  @Autowired ManageUserService manageUserService;

  // 1min fixed delay and 10s initial delay
  @Scheduled(
      fixedDelayString = "${gci.participant.fixed.delay.ms}",
      initialDelayString = "${invite.participant.initial.delay.ms}")
  public void deactivateDeletedOrDisbledGciUsers() {
    logger.entry("begin deactivateDeletedOrDisbledGciUsers()");
    manageUserService.updateGciUsers();
    logger.exit("deactivateDeletedOrDisbledGciUsers() completed");
  }
}
