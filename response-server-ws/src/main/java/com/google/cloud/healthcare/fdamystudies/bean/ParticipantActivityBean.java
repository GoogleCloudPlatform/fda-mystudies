/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class ParticipantActivityBean {
  private String activityId = "";
  private Boolean bookmarked = false;
  private String activityState = "";
  private String activityRunId = "";
  private String activityVersion = "";
  private ActivityRunBean activityRun;

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public Boolean getBookmarked() {
    return bookmarked;
  }

  public void setBookmarked(Boolean bookmarked) {
    this.bookmarked = bookmarked;
  }

  public String getActivityVersion() {
    return activityVersion;
  }

  public void setActivityVersion(String activityVersion) {
    this.activityVersion = activityVersion;
  }

  public String getActivityState() {
    return activityState;
  }

  public void setActivityState(String activityState) {
    this.activityState = activityState;
  }

  public String getActivityRunId() {
    return activityRunId;
  }

  public void setActivityRunId(String activityRunId) {
    this.activityRunId = activityRunId;
  }

  public ActivityRunBean getActivityRun() {
    return activityRun;
  }

  public void setActivityRun(ActivityRunBean activityRun) {
    this.activityRun = activityRun;
  }
}
