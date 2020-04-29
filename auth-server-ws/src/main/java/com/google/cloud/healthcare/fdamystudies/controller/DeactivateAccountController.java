/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import com.google.cloud.healthcare.fdamystudies.service.AuditLogService;
import com.google.cloud.healthcare.fdamystudies.service.UserDetailsService;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@RestController
public class DeactivateAccountController {

  private static final Logger logger = LoggerFactory.getLogger(DeactivateAccountController.class);

  @Autowired private UserDetailsService userDetailsService;

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired private AuditLogService auditLogService;

  @GetMapping("/healthCheck")
  public ResponseEntity<?> healthCheck() {
    return ResponseEntity.ok("Auth Server Up and Running");
  }

  @PostMapping(value = "/deactivate")
  public ResponseEntity<?> deactivateAccount(
      @RequestHeader("userId") String userId, @Context HttpServletResponse response) {
    logger.info("DeactivateAccountController deactivate() - Starts ");
    ResponseBean responseBean = null;
    Integer value = null;
    try {
      DaoUserBO userInfo = userDetailsService.loadUserByUserId(userId);
      if (userInfo != null) {
        responseBean = userDetailsService.deactivateAcct(userInfo);
        if (responseBean.getMessage().equalsIgnoreCase(AppConstants.SUCCESS)) {
          value = 1;
          auditLogService.createAuditLog(
              "",
              AppConstants.AUDIT_EVENT_DELETE_USER_NAME,
              String.format(AppConstants.AUDIT_EVENT_DELETE_USER_DESC, userId),
              AppConstants.AUDIT_LOG_PARTICIPANT_DATASTORE_CLIENT_ID,
              "",
              "",
              "");
        } else {
          value = 3; // status update failed
          auditLogService.createAuditLog(
              "",
              AppConstants.AUDIT_EVENT_DELETE_USER_FAILURE_NAME,
              String.format(AppConstants.AUDIT_EVENT_DELETE_USER_FAILURE_DESC, userId),
              AppConstants.AUDIT_LOG_PARTICIPANT_DATASTORE_CLIENT_ID,
              "",
              "",
              "");
        }
      }
    } catch (Exception e) {
      auditLogService.createAuditLog(
          "",
          AppConstants.AUDIT_EVENT_DELETE_USER_FAILURE_NAME,
          String.format(AppConstants.AUDIT_EVENT_DELETE_USER_FAILURE_DESC, userId),
          AppConstants.AUDIT_LOG_PARTICIPANT_DATASTORE_CLIENT_ID,
          "",
          "",
          "");
      logger.error("DeactivateAccountController deactivate() - error ", e);
    }
    logger.info("DeactivateAccountController deactivate() - Ends ");
    return ResponseEntity.ok(value);
  }
}
