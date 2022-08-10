/*
 * Copyright 2020-2021 Google LLC
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
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;
import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.ErrorCodeException;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserSupportServiceImpl implements UserSupportService {

  private XLogger logger = XLoggerFactory.getXLogger(UserSupportServiceImpl.class.getName());

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  @Autowired private EmailService emailService;

  @Autowired private AppRepository appRepository;

  @Override
  @Transactional()
  public EmailResponse feedback(
      FeedbackReqBean feedbackRequest, AuditLogEventRequest auditRequest) {
    logger.entry("Begin feedback()");
    String feedbackSubject = appConfig.getFeedbackMailSubject() + feedbackRequest.getSubject();
    String feedbackBody = appConfig.getFeedbackMailBody();

    Optional<AppEntity> optApp = appRepository.findByAppId(feedbackRequest.getAppId());
    if (!optApp.isPresent()) {
      throw new ErrorCodeException(
          com.google.cloud.healthcare.fdamystudies.common.ErrorCode.APP_NOT_FOUND);
    }
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("body", feedbackRequest.getBody());
    templateArgs.put("orgName", optApp.get().getOrganizationName());
    templateArgs.put("appName", feedbackRequest.getAppName());
    String fromEmail =
        (optApp.get().getFromEmailId() != null)
            ? optApp.get().getFromEmailId()
            : appConfig.getFromEmail();

    EmailRequest emailRequest =
        new EmailRequest(
            fromEmail,
            new String[] {optApp.get().getFeedBackToEmail()},
            null,
            null,
            feedbackSubject,
            feedbackBody,
            templateArgs);
    EmailResponse emailResponse = emailService.sendMimeMail(emailRequest);

    Map<String, String> map =
        Collections.singletonMap(
            "feedback_destination_email_address", optApp.get().getFeedBackToEmail());

    AuditLogEvent auditEvent =
        MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())
            ? FEEDBACK_CONTENT_EMAILED
            : FEEDBACK_CONTENT_EMAIL_FAILED;

    userMgmntAuditHelper.logEvent(auditEvent, auditRequest, map);

    logger.exit("feedback() :: Ends");
    return emailResponse;
  }

  @Transactional()
  @Override
  public EmailResponse contactUsDetails(
      ContactUsReqBean contactUsRequest, AuditLogEventRequest auditRequest) throws Exception {
    logger.entry("Begin contactUsDetails()");
    String contactUsSubject = appConfig.getContactusMailSubject() + contactUsRequest.getSubject();
    String contactUsContent = appConfig.getContactusMailBody();
    Optional<AppEntity> optApp = appRepository.findByAppId(contactUsRequest.getAppId());
    if (!optApp.isPresent()) {
      throw new ErrorCodeException(
          com.google.cloud.healthcare.fdamystudies.common.ErrorCode.APP_NOT_FOUND);
    }
    Map<String, String> templateArgs = new HashMap<>();
    templateArgs.put("firstName", contactUsRequest.getFirstName());
    templateArgs.put("email", contactUsRequest.getEmail());
    templateArgs.put("subject", contactUsRequest.getSubject());
    templateArgs.put("body", contactUsRequest.getBody());
    templateArgs.put("orgName", optApp.get().getOrganizationName());
    templateArgs.put("appName", contactUsRequest.getAppName());
    String fromEmail =
        (optApp.get().getFromEmailId() != null)
            ? optApp.get().getFromEmailId()
            : appConfig.getFromEmail();
    EmailRequest emailRequest =
        new EmailRequest(
            fromEmail,
            new String[] {optApp.get().getContactUsToEmail()},
            null,
            null,
            contactUsSubject,
            contactUsContent,
            templateArgs);
    EmailResponse emailResponse = emailService.sendMimeMail(emailRequest);

    Map<String, String> map =
        Collections.singletonMap(
            "contactus_destination_email_address", optApp.get().getContactUsToEmail());

    AuditLogEvent auditEvent =
        MessageCode.EMAIL_ACCEPTED_BY_MAIL_SERVER.getMessage().equals(emailResponse.getMessage())
            ? CONTACT_US_CONTENT_EMAILED
            : CONTACT_US_CONTENT_EMAIL_FAILED;
    userMgmntAuditHelper.logEvent(auditEvent, auditRequest, map);

    return emailResponse;
  }
}
