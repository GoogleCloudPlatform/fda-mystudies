/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

public class AppSessionBean {

  private String permission;
  private Integer sessionAppCount;
  private String appId;

  public String getPermission() {
    return permission;
  }

  public void setPermission(String permission) {
    this.permission = permission;
  }

  public Integer getSessionAppCount() {
    return sessionAppCount;
  }

  public void setSessionAppCount(Integer sessionAppCount) {
    this.sessionAppCount = sessionAppCount;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }
}
