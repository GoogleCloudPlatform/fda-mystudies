/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class EnrollmentTokenIdentifierBean {

  private String tokenIdentifier;
  private String customStudyId;

  public String getTokenIdentifier() {
    return tokenIdentifier;
  }

  public void setTokenIdentifier(String tokenIdentifier) {
    this.tokenIdentifier = tokenIdentifier;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }
}
