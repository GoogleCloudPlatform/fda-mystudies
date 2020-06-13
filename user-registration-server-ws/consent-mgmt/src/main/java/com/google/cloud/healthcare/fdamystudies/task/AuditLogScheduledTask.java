package com.google.cloud.healthcare.fdamystudies.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.google.cloud.healthcare.fdamystudies.service.AuditLogService;

@Component
public class AuditLogScheduledTask {

  private static final Logger LOG = LoggerFactory.getLogger(AuditLogScheduledTask.class);

  @Autowired private AuditLogService auditLogService;

  // 5min fixed delay and 10s initial delay
  @Scheduled(fixedDelay = 300000, initialDelay = 10000)
  public void executeSendAuditLogEventsTask() {
    LOG.debug("--- BEGIN executeSendAuditLogEventsTask() ");
    auditLogService.resendLogAuditEvents();
  }
}
