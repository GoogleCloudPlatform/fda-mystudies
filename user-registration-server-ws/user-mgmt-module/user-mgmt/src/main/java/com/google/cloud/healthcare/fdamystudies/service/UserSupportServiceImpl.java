/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.CONTACT_US_CONTENT_EMAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.CONTACT_US_CONTENT_EMAIL_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.FEEDBACK_CONTENT_EMAILED;
import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.FEEDBACK_CONTENT_EMAIL_FAILED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSupportServiceImpl implements UserSupportService {

  private static final Logger logger = LoggerFactory.getLogger(UserSupportServiceImpl.class);

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired EmailNotification emailNotification;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @Override
  @Transactional()
  public boolean feedback(String subject, String body, AuditLogEventRequest auditRequest) {
    logger.info("UserManagementProfileServiceImpl - feedback() :: Starts");
    boolean isEmailSent = false;
    try {
      String feedbackSubject = appConfig.getFeedbackMailSubject() + subject;
      String feedbackBody = appConfig.getFeedbackMailBody();
      Map<String, String> emailMap = new HashMap<String, String>();
      emailMap.put("$body", body);
      String dynamicContent = MyStudiesUserRegUtil.generateEmailContent(feedbackBody, emailMap);
      isEmailSent =
          emailNotification.sendEmailNotification(
              feedbackSubject, dynamicContent, appConfig.getFeedbackToEmail(), null, null);

      Map<String, String> map =
          Collections.singletonMap(
              "feedback_destination_email_address", appConfig.getFeedbackToEmail());

      AuditLogEvent auditEvent =
          isEmailSent ? FEEDBACK_CONTENT_EMAILED : FEEDBACK_CONTENT_EMAIL_FAILED;
      userMgmntAuditHelper.logEvent(auditEvent, auditRequest, map);

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - feedback() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - feedback() :: Ends");
    return isEmailSent;
  }

  @Override
  @Transactional()
  public boolean contactUsDetails(
      String subject,
      String body,
      String firstName,
      String email,
      AuditLogEventRequest auditRequest) {
    logger.info("AppMetaDataOrchestration - contactUsDetails() :: Starts");
    boolean isEmailSent = false;
    try {
      String contactUsSubject = appConfig.getContactusMailSubject() + subject;
      String contactUsContent = appConfig.getContactusMailBody();
      Map<String, String> emailMap = new HashMap<String, String>();
      emailMap.put("$firstName", firstName);
      emailMap.put("$email", email);
      emailMap.put("$subject", subject);
      emailMap.put("$body", body);
      String dynamicContent = MyStudiesUserRegUtil.generateEmailContent(contactUsContent, emailMap);
      isEmailSent =
          emailNotification.sendEmailNotification(
              contactUsSubject, dynamicContent, appConfig.getContactusToEmail(), null, null);

      Map<String, String> map =
          Collections.singletonMap(
              "contactus_destination_email_address", appConfig.getContactusToEmail());

      AuditLogEvent auditEvent =
          isEmailSent ? CONTACT_US_CONTENT_EMAILED : CONTACT_US_CONTENT_EMAIL_FAILED;
      userMgmntAuditHelper.logEvent(auditEvent, auditRequest, map);

    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - contactUsDetails() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - contactUsDetails() :: Ends");
    return isEmailSent;
  }
}
