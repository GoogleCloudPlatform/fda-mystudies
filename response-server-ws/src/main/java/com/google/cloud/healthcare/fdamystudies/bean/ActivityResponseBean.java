/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class ActivityResponseBean {
  private String orgId = "";
  private String applicationId = "";
  private String participantId;
  private String tokenIdentifier;
  private String siteId;
  private Boolean sharingConsent;
  private Boolean withdrawalStatus;
  private String type;
  private ActivityMetadataBean metadata = new ActivityMetadataBean();
  private ActivityResponseDataStructureBean data = new ActivityResponseDataStructureBean();
  private String createdTimestamp;

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public ActivityMetadataBean getMetadata() {
    return metadata;
  }

  public void setMetadata(ActivityMetadataBean metadata) {
    this.metadata = metadata;
  }

  public String getParticipantId() {
    return participantId;
  }

  public void setParticipantId(String participantId) {
    this.participantId = participantId;
  }

  public String getSiteId() {
    return siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public Boolean getSharingConsent() {
    return sharingConsent;
  }

  public void setSharingConsent(Boolean sharingConsent) {
    this.sharingConsent = sharingConsent;
  }

  public ActivityResponseDataStructureBean getData() {
    return data;
  }

  public void setData(ActivityResponseDataStructureBean data) {
    this.data = data;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getTokenIdentifier() {
    return tokenIdentifier;
  }

  public void setTokenIdentifier(String tokenIdentifier) {
    this.tokenIdentifier = tokenIdentifier;
  }

  public String getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(String createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public Boolean getWithdrawalStatus() {
    return withdrawalStatus;
  }

  public void setWithdrawalStatus(Boolean withdrawalStatus) {
    this.withdrawalStatus = withdrawalStatus;
  }
}
