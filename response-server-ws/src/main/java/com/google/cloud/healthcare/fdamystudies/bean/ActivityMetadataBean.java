/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.bean;

/**
 * Provides activity metadata information like activity identifier, name of activity, version of
 * activity etc
 * 
 * This
 *
 */
public class ActivityMetadataBean {

  private String studyId = "";
  private String studyVersion = "";
  private String activityId = "";
  private String activityRunId = "";
  private String name = "";
  private String version = "";
  private String lastModified = "";
  private String startDate = "";
  private String endDate = "";

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public String getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(String studyVersion) {
    this.studyVersion = studyVersion;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getLastModified() {
    return lastModified;
  }

  public void setLastModified(String lastModified) {
    this.lastModified = lastModified;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getActivityRunId() {
    return activityRunId;
  }

  public void setActivityRunId(String activityRunId) {
    this.activityRunId = activityRunId;
  }

}
