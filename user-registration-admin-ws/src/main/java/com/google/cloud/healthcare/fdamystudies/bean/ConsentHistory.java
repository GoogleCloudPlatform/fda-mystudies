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

public class ConsentHistory {
  private String consentVersion;

  private String consentedDate;

  private String consentDocumentPath;

  private String dataSharingPermissions;

  public String getConsentVersion() {
    return consentVersion;
  }

  public void setConsentVersion(String consentVersion) {
    this.consentVersion = consentVersion;
  }

  public String getConsentedDate() {
    return consentedDate;
  }

  public void setConsentedDate(String consentedDate) {
    this.consentedDate = consentedDate;
  }

  public String getConsentDocumentPath() {
    return consentDocumentPath;
  }

  public void setConsentDocumentPath(String consentDocumentPath) {
    this.consentDocumentPath = consentDocumentPath;
  }

  public String getDataSharingPermissions() {
    return dataSharingPermissions;
  }

  public void setDataSharingPermissions(String dataSharingPermissions) {
    this.dataSharingPermissions = dataSharingPermissions;
  }
}
