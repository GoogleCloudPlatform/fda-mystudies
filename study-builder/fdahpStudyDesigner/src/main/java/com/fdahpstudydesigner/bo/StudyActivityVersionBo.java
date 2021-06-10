/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "study_activity_version")
public class StudyActivityVersionBo implements Serializable {

  private static final long serialVersionUID = 8912773395341094340L;

  @Column(name = "activity_id")
  private Integer activityId;

  @Column(name = "activity_type")
  private String activityType;

  @Column(name = "activity_version")
  private Float activityVersion;

  @Column(name = "custom_study_id")
  private String customStudyId;

  @Column(name = "short_title")
  private String shortTitle;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "study_activity_id", updatable = false, nullable = false)
  private String studyActivityId;

  @Column(name = "study_version")
  private Float studyVersion;

  public Integer getActivityId() {
    return activityId;
  }

  public String getActivityType() {
    return activityType;
  }

  public Float getActivityVersion() {
    return activityVersion;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public String getStudyActivityId() {
    return studyActivityId;
  }

  public Float getStudyVersion() {
    return studyVersion;
  }

  public void setActivityId(Integer activityId) {
    this.activityId = activityId;
  }

  public void setActivityType(String activityType) {
    this.activityType = activityType;
  }

  public void setActivityVersion(Float activityVersion) {
    this.activityVersion = activityVersion;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setStudyActivityId(String studyActivityId) {
    this.studyActivityId = studyActivityId;
  }

  public void setStudyVersion(Float studyVersion) {
    this.studyVersion = studyVersion;
  }
}
