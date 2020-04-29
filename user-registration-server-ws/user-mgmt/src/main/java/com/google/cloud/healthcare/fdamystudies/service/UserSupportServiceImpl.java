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
import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import com.google.cloud.healthcare.fdamystudies.util.MyStudiesUserRegUtil;

@Service
public class UserSupportServiceImpl implements UserSupportService {

  private static final Logger logger = LoggerFactory.getLogger(UserSupportServiceImpl.class);

  @Autowired ApplicationPropertyConfiguration appConfig;

  @Autowired EmailNotification emailNotification;

  @Override
  public boolean feedback(String subject, String body) {
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
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - feedback() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - feedback() :: Ends");
    return isEmailSent;
  }

  @Override
  public boolean contactUsDetails(String subject, String body, String firstName, String email) {
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
    } catch (Exception e) {
      logger.error("UserManagementProfileServiceImpl - contactUsDetails() - error() ", e);
    }
    logger.info("UserManagementProfileServiceImpl - contactUsDetails() :: Ends");
    return isEmailSent;
  }
}
