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

public class UserDetailsResponseBean {

  private ErrorBean error = new ErrorBean();

  private ProfileRespBean profileRespBean;

  public ErrorBean getError() {
    return error;
  }

  public void setError(ErrorBean error) {
    this.error = error;
  }

  public ProfileRespBean getProfileRespBean() {
    return profileRespBean;
  }

  public void setProfileRespBean(ProfileRespBean profileRespBean) {
    this.profileRespBean = profileRespBean;
  }
}
