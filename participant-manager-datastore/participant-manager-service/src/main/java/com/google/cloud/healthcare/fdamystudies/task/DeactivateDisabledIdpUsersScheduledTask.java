package com.google.cloud.healthcare.fdamystudies.task;

import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DeactivateDisabledIdpUsersScheduledTask {

  private XLogger logger =
      XLoggerFactory.getXLogger(DeactivateDisabledIdpUsersScheduledTask.class.getName());

  @Autowired ManageUserService manageUserService;

  // 1min fixed delay and 10s initial delay
  @Scheduled(
      fixedDelayString = "${idp.participant.fixed.delay.ms}",
      initialDelayString = "${invite.participant.initial.delay.ms}")
  public void deactivateDeletedOrDisbledIdPUsers() {
    logger.entry("begin deactivateDeletedOrDisbledIdPUsers()");
    manageUserService.updateIdpUsers();
    logger.exit("deactivateDeletedOrDisbledIdPUsers() completed");
  }
}
