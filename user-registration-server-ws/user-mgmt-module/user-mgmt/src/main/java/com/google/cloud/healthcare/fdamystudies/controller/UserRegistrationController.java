/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.service.UserRegistrationService;
import javax.servlet.http.HttpServletRequest;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.ACCOUNT_REGISTRATION_REQUEST_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_CREATED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_CREATION_FAILED_ON_PARTICIPANT_DATA_STORE;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_NOT_CREATED_AFTER_REGISTRATION_FAILED_IN_AUTH_SERVER;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_REGISTRATION_ATTEMPT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.VERIFICATION_EMAIL_SENT;

import com.google.cloud.healthcare.fdamystudies.bean.UserRegistrationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.AuthRegistrationResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeleteAccountInfoResponseBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDao;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsService;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.util.AppConstants;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import com.google.cloud.healthcare.fdamystudies.util.UserDomainWhitelist;
import com.google.cloud.healthcare.fdamystudies.util.UserManagementUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRegistrationController {

  private XLogger logger = XLoggerFactory.getXLogger(UserRegistrationController.class.getName());

  @Autowired private UserRegistrationService userRegistrationService;

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private UserMgmntAuditHelper userMgmntAuditHelper;

  @Value("${email.code.expire_time}")
  private long expireTime;

  @GetMapping("/healthCheck")
  public ResponseEntity<?> healthCheck() {
    return ResponseEntity.ok("Up and Running");
  }

  @PostMapping("/register")
  public ResponseEntity<UserRegistrationResponse> registerUser(
      @Valid @RequestBody UserRegistrationForm user,
      @RequestHeader("appId") String appId,
      @RequestHeader("orgId") String orgId,
      HttpServletRequest request) {
    logger.entry(String.format(BEGIN_REQUEST_LOG, request.getRequestURI()));

    user.setAppId(appId);
    user.setOrgId(orgId);

    UserRegistrationResponse userRegistrationResponse = userRegistrationService.register(user);

    logger.exit("User registration successful");
    return ResponseEntity.status(HttpStatus.CREATED).body(userRegistrationResponse);
  }
}
