/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.hphc.mystudies.bean;

public class SettingsBean {

  private boolean enrolling = false;

  private String platform = "";

  public boolean isEnrolling() {
    return enrolling;
  }

  public void setEnrolling(boolean enrolling) {
    this.enrolling = enrolling;
  }

  public String getPlatform() {
    return platform;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }
}
