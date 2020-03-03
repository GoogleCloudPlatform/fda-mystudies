/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;

public class SavedActivityResponse {
  private String participantId;
  private ActivityMetadataBean metadata;
  private String createdTimestamp;
  private String siteId;
  private String studyVersion;
  private String type;
  private String tokenIdentifier;
  List<Object> results = new ArrayList<Object>();

  public String getParticipantId() {
    return participantId;
  }

  public void setParticipantId(String participantId) {
    this.participantId = participantId;
  }

  public ActivityMetadataBean getMetadata() {
    return metadata;
  }

  public void setMetadata(ActivityMetadataBean metadata) {
    this.metadata = metadata;
  }

  public String getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(String createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public String getSiteId() {
    return siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public String getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(String studyVersion) {
    this.studyVersion = studyVersion;
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

  public List<Object> getResults() {
    return results;
  }

  public void setResults(List<Object> results) {
    this.results = results;
  }

}

