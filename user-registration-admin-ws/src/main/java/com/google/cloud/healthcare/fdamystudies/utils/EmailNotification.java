/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.utils;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;

public class EmailNotification {

  private static final Logger logger = LogManager.getLogger(EmailNotification.class);

  private static ApplicationConfiguratation appConfig = null;

  public EmailNotification(ApplicationConfiguratation appConfig) {
    setAppConfig(appConfig);
  }

  public static ApplicationConfiguratation getAppConfig() {
    return appConfig;
  }

  public static void setAppConfig(ApplicationConfiguratation appConfig) {
    EmailNotification.appConfig = appConfig;
  }

  /**
   * @param subjectProprtyName
   * @param content
   * @param toMail
   * @param ccMailList
   * @param bccMailList
   * @return boolean
   * @throws Exception
   */
  public static boolean sendEmailNotification(
      String subject,
      String content,
      String toMail,
      List<String> ccMailList,
      List<String> bccMailList)
      throws Exception {
    logger.info(
        "EmailNotification - sendEmailNotification() ,Email = "
            + toMail
            + ", Subject = "
            + subject
            + ", contents ="
            + content);
    boolean sentMail = false;
    try {
      Mail mail = new Mail();
      if (toMail != null) {
        toMail = toMail.trim();
        mail.setToemail(toMail.toLowerCase());
      }
      mail.setFromEmailAddress(appConfig.getFromEmailAddress());
      mail.setFromEmailPass(appConfig.getFromEmailPasswod());
      mail.setSmtp_Hostname(appConfig.getSmtpHostName());
      mail.setSmtp_portvalue(appConfig.getSmtpPortValue());
      mail.setSslFactory(appConfig.getSslFactoryValue());
      mail.setCcEmail(StringUtils.join(ccMailList, ','));
      mail.setBccEmail(StringUtils.join(bccMailList, ','));
      mail.setSubject(subject);
      mail.setMessageBody(content);
      mail.sendemail();
      sentMail = true;
    } catch (Exception e) {
      sentMail = false;
      logger.error("EmailNotification.sendEmailNotification() :: ERROR ", e);
      throw new Exception(
          "Exception in EmailNotification.sendEmailNotification() " + e.getMessage(), e);
    }
    logger.info("EmailNotification - Ends: sendLinkToEmail() ");
    return sentMail;
  }
}
