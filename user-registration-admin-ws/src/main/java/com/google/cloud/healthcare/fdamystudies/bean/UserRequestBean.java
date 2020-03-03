/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.io.Serializable;

public class UserRequestBean implements Serializable {

  private static final long serialVersionUID = 1L;

  private String email = "";
  private String confirmPswd = "";
  private String currentPswd = "";
  private String newPswd = "";
  private String userId = "";
  UserProfileUpdateBean userInfo;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getConfirmPswd() {
    return confirmPswd;
  }

  public void setConfirmPswd(String confirmPswd) {
    this.confirmPswd = confirmPswd;
  }

  public String getCurrentPswd() {
    return currentPswd;
  }

  public void setCurrentPswd(String currentPswd) {
    this.currentPswd = currentPswd;
  }

  public String getNewPswd() {
    return newPswd;
  }

  public void setNewPswd(String newPswd) {
    this.newPswd = newPswd;
  }

  public UserProfileUpdateBean getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserProfileUpdateBean userInfo) {
    this.userInfo = userInfo;
  }
}
