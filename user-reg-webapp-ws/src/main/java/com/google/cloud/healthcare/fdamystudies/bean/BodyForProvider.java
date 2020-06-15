/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

public class BodyForProvider {

  private String userId;
  private String accessToken;
  private ChangePasswordBean changePasswordBean;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public ChangePasswordBean getChangePasswordBean() {
    return changePasswordBean;
  }

  public void setChangePasswordBean(ChangePasswordBean changePasswordBean) {
    this.changePasswordBean = changePasswordBean;
  }
}
