/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

public class StudyActivityMetadataRequestBean {
  private String studyId = null;
  private String activityId = null;
  private String activityVersion = null;

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getActivityVersion() {
    return activityVersion;
  }

  public void setActivityVersion(String activityVersion) {
    this.activityVersion = activityVersion;
  }
}
