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

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class Mail {

  private static XLogger logger = XLoggerFactory.getXLogger(Mail.class.getName());

  // Fallback hostname if we are authenticating.
  private static final String SMTP_HOSTNAME = "smtp.gmail.com";
  // Fallback hostname if we are not authenticating.
  private static final String SMTP_RELAY_HOSTNAME = "smtp-relay.gmail.com";
  private static final String SMTP_PORT = "465";
  private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  private String attachmentPath;
  private String bccEmail;
  private String ccEmail;
  private String fromEmailAddress = "";
  private String fromEmailName = "";
  private String fromEmailPassword;
  private String messageBody;
  private String smtpHostname = "";
  private String smtpPortvalue = "";

  private String sslFactory = "";

  private String subject;

  private String toemail;

  // Domain that we send in the EHLO request if we are not authenticating
  // with the SMTP server.
  private String fromEmailDomain = "";
  // If set to true, we will not authenticate with the SMTP service and
  // rather rely on the SMTP service's configured IP whitelist. If false we
  // will authenticate with the provided fromEmailAddress and fromEmailPass.
  private Boolean useIpWhitelist = false;

  public String getAttachmentPath() {
    return attachmentPath;
  }

  public String getBccEmail() {
    return bccEmail;
  }

  public String getCcEmail() {
    return ccEmail;
  }

  public String getFromEmailAddress() {
    return fromEmailAddress;
  }

  public String getFromEmailName() {
    return fromEmailName;
  }

  public String getFromEmailPassword() {
    return fromEmailPassword;
  }

  public String getMessageBody() {
    return messageBody;
  }

  public String getSmtpHostname() {
    if ("".equals(this.smtpHostname)) {
      if (useIpWhitelist) {
        return Mail.SMTP_RELAY_HOSTNAME;
      } else {
        return Mail.SMTP_HOSTNAME;
      }
    } else {
      return this.smtpHostname;
    }
  }

  public String getSmtpPortvalue() {
    String portvalue;
    if (("").equals(this.smtpPortvalue)) {
      portvalue = Mail.SMTP_PORT;
    } else {
      portvalue = this.smtpPortvalue;
    }

    return portvalue;
  }

  public String getSslFactory() {
    String sslfactoryvalue;
    if (("").equals(this.sslFactory)) {
      sslfactoryvalue = Mail.SSL_FACTORY;
    } else {
      sslfactoryvalue = this.sslFactory;
    }

    return sslfactoryvalue;
  }

  public String getSubject() {
    return subject;
  }

  public String getToemail() {
    return toemail;
  }

  public boolean sendemail() {
    logger.entry("begin sendemail()");
    boolean sentMail = false;
    try {
      final String username = this.getFromEmailAddress();
      final String password = this.getFromEmailPassword();
      Properties props = makeProperties(useIpWhitelist);
      Session session =
          useIpWhitelist ? makeSession(props) : makeSession(props, username, password);

      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(username));
      if (StringUtils.isNotBlank(this.getToemail())) {
        if (this.getToemail().indexOf(',') != -1) {
          message.setRecipients(
              Message.RecipientType.BCC, InternetAddress.parse(this.getToemail()));
        } else {
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.getToemail()));
        }
      }
      if (StringUtils.isNotBlank(this.getCcEmail())) {
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(this.getCcEmail()));
      }
      if (StringUtils.isNotBlank(this.getBccEmail())) {
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(this.getBccEmail()));
      }
      message.setSubject(this.subject);
      message.setContent(this.getMessageBody(), "text/html");
      Transport.send(message);
      sentMail = true;
    } catch (SendFailedException se) {
      logger.error("ERROR: sendemail() - ", se);
      sentMail = false;
    } catch (MailAuthenticationException mae) {
      logger.error("ERROR: sendemail() - ", mae);
      sentMail = false;
    } catch (MessagingException me) {
      logger.error("ERROR: sendemail() - ", me);
      sentMail = false;
    } catch (Exception e) {
      logger.error("ERROR: sendemail() - ", e);
      sentMail = false;
    }
    logger.exit("sendemail() :: Ends");
    return sentMail;
  }

  public boolean sendMailWithAttachment() {
    logger.entry("begin sendMailWithAttachment()");
    boolean sentMail = false;
    BodyPart messageBodyPart = null;
    Multipart multipart = null;

    try {
      final String username = this.getFromEmailAddress();
      final String password = this.getFromEmailPassword();
      Properties props = makeProperties(useIpWhitelist);
      Session session =
          useIpWhitelist ? makeSession(props) : makeSession(props, username, password);

      Message message = new MimeMessage(session);
      if (StringUtils.isNotBlank(this.getToemail())) {
        if (this.getToemail().indexOf(',') != -1) {
          message.setRecipients(
              Message.RecipientType.BCC, InternetAddress.parse(this.getToemail()));
        } else {
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.getToemail()));
        }
      }
      if (StringUtils.isNotBlank(this.getCcEmail())) {
        message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(this.getCcEmail()));
      }
      if (StringUtils.isNotBlank(this.getBccEmail())) {
        message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(this.getBccEmail()));
      }
      message.setSubject(this.subject);
      message.setFrom(new InternetAddress(username));

      // Create the message part
      messageBodyPart = new MimeBodyPart();
      // Create a multipart message
      multipart = new MimeMultipart();

      // String filename = "D:\\temp\\TestLinks.pdf"; // D:\temp\noteb.txt
      DataSource source = new FileDataSource(this.getAttachmentPath());
      messageBodyPart.setDataHandler(new DataHandler(source));
      messageBodyPart.setFileName(source.getName());
      messageBodyPart.setHeader("Content-Transfer-Encoding", "base64");
      messageBodyPart.setHeader("Content-Type", source.getContentType());
      // Send the complete message parts
      multipart.addBodyPart(messageBodyPart);

      messageBodyPart = new MimeBodyPart();
      messageBodyPart.setText(messageBody);
      messageBodyPart.setHeader("MIME-Version", "1.0");
      messageBodyPart.setHeader("Content-Type", messageBodyPart.getContentType());
      multipart.addBodyPart(messageBodyPart);

      message.setContent(multipart);
      Transport.send(message);
      sentMail = true;
    } catch (Exception e) {
      logger.error("ERROR:  sendemail() - ", e);
    }
    logger.exit("sendMailWithAttachment() :: Ends");
    return sentMail;
  }

  // Constructs a Propterties either relying on IP Whitelist on the SMTP
  // service or on authentication with email and password.
  private Properties makeProperties(Boolean useIpWhitelist) {
    Properties props = new Properties();
    props.put("mail.smtp.host", this.getSmtpHostname());
    props.put("mail.smtp.port", this.getSmtpPortvalue());
    props.put("mail.smtp.starttls.enable", "true");

    if (useIpWhitelist) {
      props.put("mail.smtp.auth", "false");
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.smtp.localhost", fromEmailDomain);
    } else {
      props.put("mail.smtp.auth", "true");
    }
    return props;
  }

  private Session makeSession(Properties props) {
    return Session.getInstance(props, null);
  }

  private Session makeSession(Properties props, final String username, final String password) {
    return Session.getInstance(
        props,
        new javax.mail.Authenticator() {
          @Override
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });
  }

  public void setAttachmentPath(String attachmentPath) {
    this.attachmentPath = attachmentPath;
  }

  public void setBccEmail(String bccEmail) {
    this.bccEmail = bccEmail;
  }

  public void setCcEmail(String ccEmail) {
    this.ccEmail = ccEmail;
  }

  public void setFromEmailAddress(String fromEmailAddress) {
    this.fromEmailAddress = fromEmailAddress;
  }

  public void setFromEmailName(String fromEmailName) {
    this.fromEmailName = fromEmailName;
  }

  public void setFromEmailPassword(String fromEmailPassword) {
    this.fromEmailPassword = fromEmailPassword;
  }

  public void setMessageBody(String messageBody) {
    this.messageBody = messageBody;
  }

  public void setSmtpHostname(String smtpHostname) {
    this.smtpHostname = smtpHostname;
  }

  public void setSmtpPortvalue(String smtpPortvalue) {
    this.smtpPortvalue = smtpPortvalue;
  }

  public void setSslFactory(String sslFactory) {
    this.sslFactory = sslFactory;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setToemail(String toemail) {
    this.toemail = toemail;
  }

  public void setUseIpWhitelist(Boolean useIpWhitelist) {
    this.useIpWhitelist = useIpWhitelist;
  }

  public void setFromEmailDomain(String fromEmailDomain) {
    this.fromEmailDomain = fromEmailDomain;
  }
}
