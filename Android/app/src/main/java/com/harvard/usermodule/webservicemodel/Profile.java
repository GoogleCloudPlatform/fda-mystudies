package com.harvard.usermodule.webservicemodel;

import io.realm.RealmObject;

public class Profile extends RealmObject {
  private String emailId;
  private String verificationTime;
  private String firstName;
  private String lastName;

  public String getEmailId() {
    return emailId;
  }

  public void setEmailId(String emailId) {
    this.emailId = emailId;
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

  public String getVerificationTime() {
    return verificationTime;
  }

  public void setVerificationTime(String verificationTime) {
    this.verificationTime = verificationTime;
  }
}
