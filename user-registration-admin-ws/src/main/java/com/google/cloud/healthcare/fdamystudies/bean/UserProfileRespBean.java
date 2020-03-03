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

public class UserProfileRespBean {
  private ProfileRespBean profile = new ProfileRespBean();

  public ProfileRespBean getProfile() {
    return profile;
  }

  public void setProfile(ProfileRespBean profile) {
    this.profile = profile;
  }
}
