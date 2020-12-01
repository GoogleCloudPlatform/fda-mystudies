/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.task;

import com.google.cloud.healthcare.fdamystudies.service.ManageUserService;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserAccountEmailSchedulerTask {

  private XLogger logger =
      XLoggerFactory.getXLogger(UserAccountEmailSchedulerTask.class.getName());

  @Autowired private ManageUserService manageUserService;

  // 5min fixed delay and 10s initial delay
  @Scheduled(
      fixedDelayString = "${invite.participant.fixed.delay.ms}",
      initialDelayString = "${invite.participant.initial.delay.ms}")
  public void processEmailRequests() {
    logger.entry("begin processEmailRequests()");
    manageUserService.sendUserEmail();
    logger.exit("processEmailRequests() completed");
  }
}
