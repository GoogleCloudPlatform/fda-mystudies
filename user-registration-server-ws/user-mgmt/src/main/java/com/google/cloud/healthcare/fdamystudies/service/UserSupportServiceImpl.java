/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exceptions.OrchestrationException;
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Service
public class UserSupportServiceImpl implements UserSupportService {

  private static final Logger logger = LoggerFactory.getLogger(UserSupportServiceImpl.class);

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired EmailNotification emailNotification;

  @Override
  public Integer feedback(String subject, String body) throws OrchestrationException {
    logger.info("UserManagementProfileServiceImpl - feedback() :: Starts");
    Map<String, String> emailMap = new HashMap<String, String>();
    String feedbackSubject = "";
    String dynamicContent = "";
    String feedbackBody = "";
    boolean isSent = false;
    int isEmailSent = 0;
    try {
      feedbackSubject = appConfig.getFeedbackMailSuject() + "" + subject;
      feedbackBody = appConfig.getFeedbackMailBody();
      emailMap.put("$body", body);
      dynamicContent = MyStudiesUserRegUtil.generateEmailContent(feedbackBody, emailMap);
      isSent =
          emailNotification.sendEmailNotification(
              feedbackSubject, dynamicContent, appConfig.getFeedabckToEmail(), null, null);
      if (!isSent) {
        isEmailSent = 1;
      } else {
        isEmailSent = 2;
      }
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - feedback() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - feedback() :: Ends");
    return isEmailSent;
  }

  @Override
  public Integer contactUsDetails(String subject, String body, String firstName, String email)
      throws OrchestrationException {
    logger.info("AppMetaDataOrchestration - contactUsDetails() :: Starts");
    Map<String, String> emailMap = new HashMap<String, String>();
    String contactUsSubject = "";
    String contactUsContent = "";
    String dynamicContent = "";
    boolean isSent = false;
    int isEmailSent = 0;
    try {
      contactUsSubject = appConfig.getContactusMailSubject() + "" + subject;
      contactUsContent = appConfig.getContactusMailBody();
      emailMap.put("$firstName", firstName);
      emailMap.put("$email", email);
      emailMap.put("$subject", subject);
      emailMap.put("$body", body);
      dynamicContent = MyStudiesUserRegUtil.generateEmailContent(contactUsContent, emailMap);
      System.out.println("Final Email Content:" + dynamicContent);
      isSent =
          emailNotification.sendEmailNotification(
              contactUsSubject, dynamicContent, appConfig.getContactusToEmail(), null, null);
      if (!isSent) {
        isEmailSent = 1;
      } else {
        isEmailSent = 2;
      }
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - contactUsDetails() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - contactUsDetails() :: Ends");
    return isEmailSent;
  }
}
