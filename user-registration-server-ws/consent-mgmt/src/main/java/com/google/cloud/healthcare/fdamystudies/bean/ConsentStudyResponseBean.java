/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ConsentStudyResponseBean {
  private String message;
  private ConsentResponseBean consent = new ConsentResponseBean();
  private String sharing;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ConsentResponseBean getConsent() {
    return consent;
  }

  public void setConsent(ConsentResponseBean consent) {
    this.consent = consent;
  }

  public String getSharing() {
    return sharing;
  }

  public void setSharing(String sharing) {
    this.sharing = sharing;
  }
}
