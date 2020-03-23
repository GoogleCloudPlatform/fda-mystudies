/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class AppOrgInfoBean {
  private int appInfoId;
  private int orgInfoId;

  public int getAppInfoId() {
    return appInfoId;
  }

  public void setAppInfoId(int appInfoId) {
    this.appInfoId = appInfoId;
  }

  public int getOrgInfoId() {
    return orgInfoId;
  }

  public void setOrgInfoId(int orgInfoId) {
    this.orgInfoId = orgInfoId;
  }
}
