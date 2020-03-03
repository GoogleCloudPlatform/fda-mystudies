/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "mail_messages")
public class MailMessages implements Serializable {

  /** */
  private static final long serialVersionUID = 1226444459997456414L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "mail_messageid")
  private Integer mailMessageId;

  @Column(name = "email_id")
  private String emailId;

  @Column(name = "user_name")
  private String userName;

  @Column(name = "email_title", columnDefinition = "TEXT")
  private String emailTitle;

  @Column(name = "email_body", columnDefinition = "TEXT")
  private String emailBody;

  @Column(name = "created_time", columnDefinition = "DATETIME")
  private String createdTime;

  @Column(name = "is_email_sent", columnDefinition = "TINYINT(1) default 0")
  private int isEmailSent = 0;

  @Column(name = "sent_Datetime", columnDefinition = "DATETIME")
  private String sentDateTime;

  @Column(name = "cc_email", columnDefinition = "TEXT")
  private String ccEmail;

  @Column(name = "bcc_email", columnDefinition = "TEXT")
  private String bccEmail;

  @Column(name = "user_type")
  private String userType;

  @Column(name = "notification_type")
  private String notificationType = "";

  @Column(name = "search_id")
  private String searchId = "";

  public MailMessages() {}

  public MailMessages(
      String emailId,
      String userName,
      String emailTitle,
      String emailBody,
      String createtime,
      String userType,
      String notificationType) {
    this.emailId = emailId;
    this.userName = userName;
    this.emailTitle = emailTitle;
    this.emailBody = emailBody;
    this.createdTime = createtime;
    this.userType = userType;
    this.notificationType = notificationType;
  }

  public MailMessages(
      String emailId,
      String userName,
      String emailTitle,
      String emailBody,
      String createtime,
      String ccEmail,
      String bccEmail,
      String userType,
      String notificationType) {
    this.emailId = emailId;
    this.userName = userName;
    this.emailTitle = emailTitle;
    this.emailBody = emailBody;
    this.createdTime = createtime;
    this.ccEmail = ccEmail;
    this.bccEmail = bccEmail;
    this.userType = userType;
    this.notificationType = notificationType;
  }

  public MailMessages(
      String emailId,
      String userName,
      String emailTitle,
      String emailBody,
      String createdTime,
      String userType,
      String notificationType,
      String searchId) {
    super();
    this.emailId = emailId;
    this.userName = userName;
    this.emailTitle = emailTitle;
    this.emailBody = emailBody;
    this.createdTime = createdTime;
    this.userType = userType;
    this.notificationType = notificationType;
    this.searchId = searchId;
  }

  public Integer getMailMessageId() {
    return mailMessageId;
  }

  public void setMailMessageId(Integer mailMessageId) {
    this.mailMessageId = mailMessageId;
  }

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmailTitle() {
    return emailTitle;
  }

  public void setEmailTitle(String emailTitle) {
    this.emailTitle = emailTitle;
  }

  public String getEmailBody() {
    return emailBody;
  }

  public void setEmailBody(String emailBody) {
    this.emailBody = emailBody;
  }

  public String getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(String createdTime) {
    this.createdTime = createdTime;
  }

  public int getIsEmailSent() {
    return isEmailSent;
  }

  public void setIsEmailSent(int isEmailSent) {
    this.isEmailSent = isEmailSent;
  }

  public String getSentDateTime() {
    return sentDateTime;
  }

  public void setSentDateTime(String sentDateTime) {
    this.sentDateTime = sentDateTime;
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

  public String getUserType() {
    return userType;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType(String notificationType) {
    this.notificationType = notificationType;
  }

  public String getSearchId() {
    return searchId;
  }

  public void setSearchId(String searchId) {
    this.searchId = searchId;
  }
}
