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
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "active_task_frequencies")
@NamedQuery(name = "ActiveTaskFrequencyBo.findAll", query = "SELECT a FROM ActiveTaskFrequencyBo a")
public class ActiveTaskFrequencyBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "active_task_id")
  private String activeTaskId;

  @Column(name = "frequency_date")
  private String frequencyDate;

  @Column(name = "frequency_time")
  private String frequencyTime;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "is_launch_study")
  private Boolean isLaunchStudy = false;

  @Column(name = "is_study_life_time")
  private Boolean isStudyLifeTime = false;

  @Column(name = "x_days_sign", length = 1)
  private boolean xDaysSign = false;

  @Column(name = "y_days_sign", length = 1)
  private boolean yDaysSign = false;

  @Column(name = "time_period_from_days")
  private Integer timePeriodFromDays;

  @Column(name = "time_period_to_days")
  private Integer timePeriodToDays;

  public ActiveTaskFrequencyBo() {}

  public String getActiveTaskId() {
    return activeTaskId;
  }

  public String getFrequencyDate() {
    return this.frequencyDate;
  }

  public String getFrequencyTime() {
    return this.frequencyTime;
  }

  public String getId() {
    return this.id;
  }

  public Boolean getIsLaunchStudy() {
    return isLaunchStudy;
  }

  public Boolean getIsStudyLifeTime() {
    return isStudyLifeTime;
  }

  public void setActiveTaskId(String string) {
    this.activeTaskId = string;
  }

  public void setFrequencyDate(String frequencyDate) {
    this.frequencyDate = frequencyDate;
  }

  public void setFrequencyTime(String frequencyTime) {
    this.frequencyTime = frequencyTime;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIsLaunchStudy(Boolean isLaunchStudy) {
    this.isLaunchStudy = isLaunchStudy;
  }

  public void setIsStudyLifeTime(Boolean isStudyLifeTime) {
    this.isStudyLifeTime = isStudyLifeTime;
  }

  public boolean isxDaysSign() {
    return xDaysSign;
  }

  public void setxDaysSign(boolean xDaysSign) {
    this.xDaysSign = xDaysSign;
  }

  public boolean isyDaysSign() {
    return yDaysSign;
  }

  public void setyDaysSign(boolean yDaysSign) {
    this.yDaysSign = yDaysSign;
  }

  public Integer getTimePeriodFromDays() {
    return timePeriodFromDays;
  }

  public void setTimePeriodFromDays(Integer timePeriodFromDays) {
    this.timePeriodFromDays = timePeriodFromDays;
  }

  public Integer getTimePeriodToDays() {
    return timePeriodToDays;
  }

  public void setTimePeriodToDays(Integer timePeriodToDays) {
    this.timePeriodToDays = timePeriodToDays;
  }
}
