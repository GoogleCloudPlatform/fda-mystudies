/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mail {

    private static final Logger logger = LogManager.getLogger(Mail.class);

    private String toemail;
    private String subject;
    private String messageBody;
    // Fallback hostname if we are authenticating.
    private static final String SMTP_HOSTNAME = "smtp.gmail.com";
    // Fallback hostname if we are not authenticating.
    private static final String SMTP_RELAY_HOSTNAME = "smtp-relay.gmail.com";
    private static final String SMTP_PORT = "465";
    private String smtp_Hostname = "";
    private String smtp_portvalue = "";
    static String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private String sslFactory = "";
    private String fromEmailAddress = "";
    private String fromEmailPass = "";
    private String fromEmailName = "";
    private String ccEmail;
    private String bccEmail;
    private String attachmentPath;
    // Domain that we send in the EHLO request if we are not authenticating
    // with the SMTP server.
    @Setter
    private String fromDomain = "";
    // If set to true, we will not authenticate with the SMTP service and
    // rather rely on the SMTP service's configured IP whitelist. If false we
    // will authenticate with the provided fromEmailAddress and fromEmailPass.
    @Setter
    private Boolean useIpWhitelist = false;

    public boolean sendemail() throws Exception {
        logger.warn(" sendemail() ==== starts");
        boolean sentMail = false;
        try {
            final String username = this.getFromEmailAddress();
            final String password = this.getFromEmailPass();
            Properties props = makeProperties(useIpWhitelist);
            Session session = useIpWhitelist ? makeSession(props) :
                    makeSession(props, username, password);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            if (StringUtils.isNotBlank(this.getToemail())) {
                if (this.getToemail().indexOf(",") != -1) {
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
            Transport transport = session.getTransport();
            transport.send(message);
            logger.debug("sendemail()==== ends");
            sentMail = true;
        } catch (MessagingException e) {
            logger.error("sendemail() MessagingException- error", e);
        } catch (Exception e) {
            logger.error("ERROR:  sendemail() - error( ) " + e + " : ");
        }
        logger.info("Mail.sendemail() :: Ends");
        return sentMail;
    }

    public boolean sendMailWithAttachment() throws Exception {
        logger.debug("sendemail()==== start");
        boolean sentMail = false;
        try {
            final String username = this.getFromEmailAddress();
            final String password = this.getFromEmailPass();
            Properties props = makeProperties(useIpWhitelist);
            Session session = useIpWhitelist ? makeSession(props) :
                    makeSession(props, username, password);
            Message message = new MimeMessage(session);
            if (StringUtils.isNotBlank(this.getToemail())) {
                if (this.getToemail().indexOf(",") != -1) {
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
            // message.setText("Check attachment in Mail");
            // message.setContent(messageBody, "text/html");
            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();
            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
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
        } catch (MessagingException e) {
            logger.error("ERROR:  sendemail() - " + e + " : ");
            sentMail = false;
        } catch (Exception e) {
            logger.error("ERROR:  sendemail() - " + e + " : ");
        }
        logger.info("Mail.sendemail() :: Ends");
        return sentMail;
    }

    // Constructs a Propterties either relying on IP Whitelist on the SMTP
    // service or on authentication with email and password.
    private Properties makeProperties(Boolean useIpWhitelist) {
        Properties props = new Properties();
        props.put("mail.smtp.host", this.getSmtp_Hostname());
        props.put("mail.smtp.port", this.getSmtp_portvalue());
        props.put("mail.smtp.socketFactory.class", this.getSslFactory());
        props.put("mail.smtp.socketFactory.port", this.getSmtp_portvalue());
        if (useIpWhitelist) {
            props.put("mail.smtp.auth", "false");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.localhost", fromDomain);
        } else {
            props.put("mail.smtp.auth", "true");
        }
        return props;
    }

    private Session makeSession(Properties props) {
        return Session.getInstance(props, null);
    }

    private Session makeSession(Properties props, String username,
                                String password) {
        return Session.getInstance(
                props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    public String getToemail() {
        return toemail;
    }

    public void setToemail(String toemail) {
        this.toemail = toemail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSmtp_Hostname() {
        if (this.smtp_Hostname.equals("")) {
            if (useIpWhitelist) {
                return Mail.SMTP_RELAY_HOSTNAME;
            } else {
                return Mail.SMTP_HOSTNAME;
            }
        } else {
            return this.smtp_Hostname;
        }
    }

    public void setSmtp_Hostname(String smtp_Hostname) {
        this.smtp_Hostname = smtp_Hostname;
    }

    public String getSmtp_portvalue() {
        String portvalue = "";
        if (this.smtp_portvalue.equals("")) {
            portvalue = Mail.SMTP_PORT;
        } else {
            portvalue = this.smtp_portvalue;
        }

        return portvalue;
    }

    public void setSmtp_portvalue(String smtp_portvalue) {
        this.smtp_portvalue = smtp_portvalue;
    }

    public static String getSSL_FACTORY() {
        return SSL_FACTORY;
    }

    public static void setSSL_FACTORY(String sSL_FACTORY) {
        SSL_FACTORY = sSL_FACTORY;
    }

    public String getSslFactory() {

        String sslfactoryvalue = "";
        if (this.sslFactory.equals("")) {
            sslfactoryvalue = Mail.SSL_FACTORY;
        } else {
            sslfactoryvalue = this.sslFactory;
        }

        return sslfactoryvalue;
    }

    public void setSslFactory(String sslFactory) {
        this.sslFactory = sslFactory;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getFromEmailPass() {
        return fromEmailPass;
    }

    public void setFromEmailPass(String fromEmailPassword) {
        this.fromEmailPass = fromEmailPassword;
    }

    public static String getSmtpPort() {
        return SMTP_PORT;
    }

    public String getFromEmailName() {
        return fromEmailName;
    }

    public void setFromEmailName(String fromEmailName) {
        this.fromEmailName = fromEmailName;
    }

    public String getCcEmail() {
        return ccEmail;
    }

    public void setCcEmail(String ccEmail) {
        this.ccEmail = ccEmail;
    }

    public String getBccEmail() {
        return bccEmail;
    }

    public void setBccEmail(String bccEmail) {
        this.bccEmail = bccEmail;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }
}
