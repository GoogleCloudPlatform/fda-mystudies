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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
@Entity
@Table(name = "app_info")
public class AppInfoDetailsBO implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "app_info_id")
  private int appInfoId;

  @Column(name = "custom_app_id")
  private String appId;

  @Column(name = "app_name", columnDefinition = "VARCHAR(255)")
  private String appName;

  @Column(name = "org_info_id")
  private String orgInfoId;

  @Column(name = "ios_bundle_id")
  private String iosBundleId;

  @Column(name = "android_bundle_id")
  private String androidBundleId;

  @Column(name = "ios_certificate")
  private String iosCertificate;

  @Column(name = "ios_certificate_password")
  private String iosCertificatePassword;

  @Column(name = "android_server_key")
  private String androidServerKey;

  @Column(name = "from_email_id")
  private String formEmailId;

  @Column(name = "from_email_password")
  private String fromEmailPassword;

  @Column(name = "reg_email_sub")
  private String regEmailSub;

  @Column(name = "reg_email_body")
  private String regEmailBody;

  @Column(name = "forgot_email_sub")
  private String forgotEmailSub;

  @Column(name = "forgot_email_body")
  private String forgotEmailBody;

  @Column(name = "method_handler", columnDefinition = "TINYINT(1)")
  private Integer methodHandler;

  @Column(name = "created_on")
  private Date createdOn;

  public int getAppInfoId() {
    return appInfoId;
  }

  public void setAppInfoId(int appInfoId) {
    this.appInfoId = appInfoId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getOrgInfoId() {
    return orgInfoId;
  }

  public void setOrgInfoId(String orgInfoId) {
    this.orgInfoId = orgInfoId;
  }

  public String getIosBundleId() {
    return iosBundleId;
  }

  public void setIosBundleId(String iosBundleId) {
    this.iosBundleId = iosBundleId;
  }

  public String getAndroidBundleId() {
    return androidBundleId;
  }

  public void setAndroidBundleId(String androidBundleId) {
    this.androidBundleId = androidBundleId;
  }

  public String getIosCertificate() {
    return iosCertificate;
  }

  public void setIosCertificate(String iosCertificate) {
    this.iosCertificate = iosCertificate;
  }

  public String getIosCertificatePassword() {
    return iosCertificatePassword;
  }

  public void setIosCertificatePassword(String iosCertificatePassword) {
    this.iosCertificatePassword = iosCertificatePassword;
  }

  public String getAndroidServerKey() {
    return androidServerKey;
  }

  public void setAndroidServerKey(String androidServerKey) {
    this.androidServerKey = androidServerKey;
  }

  public String getFormEmailId() {
    return formEmailId;
  }

  public void setFormEmailId(String formEmailId) {
    this.formEmailId = formEmailId;
  }

  public String getFromEmailPassword() {
    return fromEmailPassword;
  }

  public void setFromEmailPassword(String fromEmailPassword) {
    this.fromEmailPassword = fromEmailPassword;
  }

  public String getRegEmailSub() {
    return regEmailSub;
  }

  public void setRegEmailSub(String regEmailSub) {
    this.regEmailSub = regEmailSub;
  }

  public String getRegEmailBody() {
    return regEmailBody;
  }

  public void setRegEmailBody(String regEmailBody) {
    this.regEmailBody = regEmailBody;
  }

  public String getForgotEmailSub() {
    return forgotEmailSub;
  }

  public void setForgotEmailSub(String forgotEmailSub) {
    this.forgotEmailSub = forgotEmailSub;
  }

  public String getForgotEmailBody() {
    return forgotEmailBody;
  }

  public void setForgotEmailBody(String forgotEmailBody) {
    this.forgotEmailBody = forgotEmailBody;
  }

  public Integer isMethodHandler() {
    return methodHandler;
  }

  public void setMethodHandler(Integer methodHandler) {
    this.methodHandler = methodHandler;
  }

  public Date getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(Date createdOn) {
    this.createdOn = createdOn;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }
}
