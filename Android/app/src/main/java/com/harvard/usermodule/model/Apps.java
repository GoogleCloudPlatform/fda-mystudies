
package com.harvard.usermodule.model;

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
}
