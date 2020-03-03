package com.google.cloud.healthcare.fdamystudies.util;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;

/** @author Aswini */
public class EmailNotification {

  private static final Logger logger = LogManager.getLogger(EmailNotification.class);

  @Autowired private ApplicationPropertyConfiguration appConfig;

  /**
   * @param subjectProprtyName
   * @param content
   * @param toMail
   * @param ccMailList
   * @param bccMailList
   * @return boolean
   * @throws Exception
   */
  public boolean sendEmailNotification(
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
      mail.setFromEmailPass(appConfig.getFromEmailPassword());
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
      logger.error("EmailNotification.sendEmailNotification() :: ERROR ", e);
    }
    logger.info("EmailNotification - Ends: sendLinkToEmail() ");
    return sentMail;
  }
}
