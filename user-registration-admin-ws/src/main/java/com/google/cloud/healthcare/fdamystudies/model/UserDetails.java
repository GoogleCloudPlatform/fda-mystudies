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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.ToString;

@ToString
@Entity
@Table(name = "user_details")
public class UserDetails {
  @Id
  @Column(name = "user_details_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer userDetailsId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "_ts")
  private Date ts;

  @Column(name = "email")
  private String email;

  @Column(name = "use_pass_code", columnDefinition = "TINYINT(1)")
  private Boolean usePassCode = false;

  @Column(name = "touch_id", columnDefinition = "TINYINT(1)")
  private Boolean touchId = false;

  @Column(name = "local_notification_flag", columnDefinition = "TINYINT(1)")
  private Boolean localNotificationFlag = false;

  @Column(name = "remote_notification_flag", columnDefinition = "TINYINT(1)")
  private Boolean remoteNotificationFlag = false;

  @Column(name = "status")
  private Integer status;

  @Column(name = "password")
  private String password;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = false, updatable = false)
  private AppInfoDetailsBO appInfo;

  @Column(name = "temp_password", columnDefinition = "TINYINT(1)")
  private Boolean tempPassword = false;

  @Column(name = "locale")
  private String locale;

  @Column(name = "reset_password")
  private String resetPassword;

  @Column(name = "verification_date")
  private Date verificationDate;

  @Column(name = "temp_password_date")
  private Date tempPasswordDate;

  @Column(name = "password_updated_date")
  private Date passwordUpdatedDate;

  @Column(name = "reminder_lead_time")
  private String reminderLeadTime;

  @Column(name = "security_token")
  private String securityToken;

  @Column(name = "email_code")
  private String emailCode;

  @Column(name = "user_id")
  private String userId;

  public Integer getUserDetailsId() {
    return userDetailsId;
  }

  public void setUserDetailsId(Integer userDetailsId) {
    this.userDetailsId = userDetailsId;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Date getTs() {
    return ts;
  }

  public void setTs(Date ts) {
    this.ts = ts;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean getUsePassCode() {
    return usePassCode;
  }

  public void setUsePassCode(Boolean usePassCode) {
    this.usePassCode = usePassCode;
  }

  public Boolean getTouchId() {
    return touchId;
  }

  public void setTouchId(Boolean touchId) {
    this.touchId = touchId;
  }

  public Boolean getLocalNotificationFlag() {
    return localNotificationFlag;
  }

  public void setLocalNotificationFlag(Boolean localNotificationFlag) {
    this.localNotificationFlag = localNotificationFlag;
  }

  public Boolean getRemoteNotificationFlag() {
    return remoteNotificationFlag;
  }

  public void setRemoteNotificationFlag(Boolean remoteNotificationFlag) {
    this.remoteNotificationFlag = remoteNotificationFlag;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public AppInfoDetailsBO getAppInfo() {
    return appInfo;
  }

  public void setAppInfo(AppInfoDetailsBO appInfo) {
    this.appInfo = appInfo;
  }

  public Boolean getTempPassword() {
    return tempPassword;
  }

  public void setTempPassword(Boolean tempPassword) {
    this.tempPassword = tempPassword;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getResetPassword() {
    return resetPassword;
  }

  public void setResetPassword(String resetPassword) {
    this.resetPassword = resetPassword;
  }

  public Date getVerificationDate() {
    return verificationDate;
  }

  public void setVerificationDate(Date verificationDate) {
    this.verificationDate = verificationDate;
  }

  public Date getTempPasswordDate() {
    return tempPasswordDate;
  }

  public void setTempPasswordDate(Date tempPasswordDate) {
    this.tempPasswordDate = tempPasswordDate;
  }

  public Date getPasswordUpdatedDate() {
    return passwordUpdatedDate;
  }

  public void setPasswordUpdatedDate(Date passwordUpdatedDate) {
    this.passwordUpdatedDate = passwordUpdatedDate;
  }

  public String getReminderLeadTime() {
    return reminderLeadTime;
  }

  public void setReminderLeadTime(String reminderLeadTime) {
    this.reminderLeadTime = reminderLeadTime;
  }

  public String getSecurityToken() {
    return securityToken;
  }

  public void setSecurityToken(String securityToken) {
    this.securityToken = securityToken;
  }

  public String getEmailCode() {
    return emailCode;
  }

  public void setEmailCode(String emailCode) {
    this.emailCode = emailCode;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
