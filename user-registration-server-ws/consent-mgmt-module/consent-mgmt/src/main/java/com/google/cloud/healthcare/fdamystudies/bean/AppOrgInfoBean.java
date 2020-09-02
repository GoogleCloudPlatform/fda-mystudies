/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

public class AppOrgInfoBean {
  private String appInfoId;
  private String orgInfoId;

  public String getAppInfoId() {
    return appInfoId;
  }

  public void setAppInfoId(String appInfoId) {
    this.appInfoId = appInfoId;
  }

  public String getOrgInfoId() {
    return orgInfoId;
  }

  public void setOrgInfoId(String orgInfoId) {
    this.orgInfoId = orgInfoId;
  }
}
