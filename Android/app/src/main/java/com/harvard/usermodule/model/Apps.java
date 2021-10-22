
package com.harvard.usermodule.model;

import com.harvard.utils.version.Version;
import com.harvard.utils.version.VersionModel;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Apps extends RealmObject {
  private int status;
  private String message;
  private String code;
  private String appName;
  @PrimaryKey
  private String appId;
  private String fromEmail;
  private String contactUsEmail;
  private String supportEmail;
  private String termsUrl;
  private String privacyPolicyUrl;
  private String appWebsite;
  private VersionModel version;

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getFromEmail() {
    return fromEmail;
  }

  public void setFromEmail(String fromEmail) {
    this.fromEmail = fromEmail;
  }

  public String getContactUsEmail() {
    return contactUsEmail;
  }

  public void setContactUsEmail(String contactUsEmail) {
    this.contactUsEmail = contactUsEmail;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getTermsUrl() {
    return termsUrl;
  }

  public void setTermsUrl(String termsUrl) {
    this.termsUrl = termsUrl;
  }

  public String getPrivacyPolicyUrl() {
    return privacyPolicyUrl;
  }

  public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
    this.privacyPolicyUrl = privacyPolicyUrl;
  }

  public String getAppWebsite() {
    return appWebsite;
  }

  public void setAppWebsite(String appWebsite) {
    this.appWebsite = appWebsite;
  }

  public VersionModel getVersion() {
    return version;
  }

  public void setVersion(VersionModel version) {
    this.version = version;
  }

  public String getSupportEmail() {
    return supportEmail;
  }

  public void setSupportEmail(String supportEmail) {
    this.supportEmail = supportEmail;
  }
}
