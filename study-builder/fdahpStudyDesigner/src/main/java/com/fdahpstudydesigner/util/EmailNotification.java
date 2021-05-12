/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailNotification {

  private static XLogger logger = XLoggerFactory.getXLogger(EmailNotification.class.getName());
  @Autowired Mail mail;

  public boolean sendEmailNotification(
      String subjectProprtyName,
      String content,
      String toMail,
      List<String> ccMailList,
      List<String> bccMailList) {

    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    boolean sentMail = false;
    try {
      if (toMail != null) {
        toMail = toMail.trim();
        mail.setToemail(toMail.toLowerCase());
      }
      mail.setFromEmailAddress(propMap.get("from.email.address"));
      mail.setFromEmailPassword(propMap.get("from.email.password"));
      mail.setSmtpHostname(propMap.get("smtp.hostname"));
      mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
      mail.setSslFactory(propMap.get("sslfactory.value"));
      mail.setUseIpWhitelist(Boolean.parseBoolean(propMap.get("from.email.use_ip_whitelist")));
      mail.setFromEmailDomain(propMap.get("from.email.domain"));
      mail.setCcEmail(StringUtils.join(ccMailList, ','));
      mail.setBccEmail(StringUtils.join(bccMailList, ','));
      mail.setSubject(propMap.get(subjectProprtyName));
      mail.setMessageBody(content);
      sentMail = mail.sendemail();
    } catch (Exception e) {
      logger.error("EmailNotification.sendEmailNotification() :: ERROR ", e);
    }
    logger.exit("EmailNotification - Ends: sendLinkToEmail() - returning  a List value" + " : ");
    return sentMail;
  }

  public boolean sendEmailNotificationToMany(
      String subjectProprtyName,
      String content,
      List<String> toMailList,
      List<String> ccMailList,
      List<String> bccMailList) {
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    logger.entry(
        "EmailNotification - Starts: sendEmailNotificationToMany() - Input arg are ServletContext ");
    boolean sentMail = false;
    List<String> toMailListNew = new ArrayList<>();
    try {
      if ((toMailList != null) && !toMailList.isEmpty()) {
        for (String mailId : toMailList) {
          mailId = mailId.trim();
          toMailListNew.add(mailId.toLowerCase());
        }
        mail.setToemail(StringUtils.join(toMailListNew, ','));
      }
      mail.setFromEmailAddress(propMap.get("from.email.address"));
      mail.setFromEmailPassword(propMap.get("from.email.password"));
      mail.setSmtpHostname(propMap.get("smtp.hostname"));
      mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
      mail.setSslFactory(propMap.get("sslfactory.value"));
      mail.setUseIpWhitelist(Boolean.parseBoolean(propMap.get("from.email.use_ip_whitelist")));
      mail.setFromEmailDomain(propMap.get("from.email.domain"));
      mail.setCcEmail(StringUtils.join(ccMailList, ','));
      mail.setBccEmail(StringUtils.join(bccMailList, ','));
      mail.setSubject(propMap.get(subjectProprtyName));
      mail.setMessageBody(content);
      mail.sendemail();
      sentMail = true;
    } catch (Exception e) {
      sentMail = false;
      logger.error("EmailNotification.sendEmailNotificationToMany() :: ERROR ", e);
    }
    logger.exit(
        "EmailNotification - Ends: sendEmailNotificationToMany() - returning  a List value"
            + " : ");
    return sentMail;
  }

  public boolean sendMailWithAttachment(
      String subjectProprtyName,
      String content,
      String toMail,
      List<String> ccMailList,
      List<String> bccMailList,
      String attachmentPath) {
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    boolean sentMail = false;
    try {
      if (toMail != null) {
        toMail = toMail.trim();
        mail.setToemail(toMail.toLowerCase());
      }
      mail.setFromEmailAddress(propMap.get("from.email.address"));
      mail.setFromEmailPassword(propMap.get("from.email.password"));
      mail.setSmtpHostname(propMap.get("smtp.hostname"));
      mail.setSmtpPortvalue(propMap.get("smtp.portvalue"));
      mail.setSslFactory(propMap.get("sslfactory.value"));
      mail.setUseIpWhitelist(Boolean.parseBoolean(propMap.get("from.email.use_ip_whitelist")));
      mail.setFromEmailDomain(propMap.get("from.email.domain"));
      mail.setCcEmail(StringUtils.join(ccMailList, ','));
      mail.setBccEmail(StringUtils.join(bccMailList, ','));
      mail.setSubject(propMap.get(subjectProprtyName));
      mail.setMessageBody(content);
      mail.setAttachmentPath(attachmentPath);
      mail.sendMailWithAttachment();
      sentMail = true;
    } catch (Exception e) {
      logger.error("EmailNotification.sendEmailNotification() :: ERROR ", e);
    }
    logger.exit("EmailNotification - Ends: sendLinkToEmail() - returning  a List value" + " : ");
    return sentMail;
  }
}
