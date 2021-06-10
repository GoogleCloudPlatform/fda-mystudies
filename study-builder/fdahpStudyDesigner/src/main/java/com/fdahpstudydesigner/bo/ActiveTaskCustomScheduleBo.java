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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "active_task_custom_frequencies")
@NamedQuery(
    name = "ActiveTaskCustomScheduleBo.findAll",
    query = "SELECT a FROM ActiveTaskCustomScheduleBo a")
public class ActiveTaskCustomScheduleBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "active_task_id")
  private String activeTaskId;

  @Column(name = "frequency_end_date")
  private String frequencyEndDate;

  @Column(name = "frequency_start_date")
  private String frequencyStartDate;

  @Column(name = "frequency_start_time")
  private String frequencyStartTime;

  @Column(name = "frequency_end_time")
  private String frequencyEndTime;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "is_used")
  @Type(type = "yes_no")
  private boolean used = false;

  @Column(name = "x_days_sign", length = 1)
  private boolean xDaysSign = false;

  @Column(name = "y_days_sign", length = 1)
  private boolean yDaysSign = false;

  @Column(name = "time_period_from_days")
  private Integer timePeriodFromDays;

  @Column(name = "time_period_to_days")
  private Integer timePeriodToDays;

  public ActiveTaskCustomScheduleBo() {
    // Do nothing
  }

  public String getActiveTaskId() {
    return this.activeTaskId;
  }

  public String getFrequencyEndDate() {
    return this.frequencyEndDate;
  }

  public String getFrequencyStartDate() {
    return this.frequencyStartDate;
  }

  public String getId() {
    return this.id;
  }

  public boolean isUsed() {
    return used;
  }

  public void setActiveTaskId(String activeTaskId) {
    this.activeTaskId = activeTaskId;
  }

  public void setFrequencyEndDate(String frequencyEndDate) {
    this.frequencyEndDate = frequencyEndDate;
  }

  public void setFrequencyStartDate(String frequencyStartDate) {
    this.frequencyStartDate = frequencyStartDate;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setUsed(boolean used) {
    this.used = used;
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

  public String getFrequencyStartTime() {
    return frequencyStartTime;
  }

  public void setFrequencyStartTime(String frequencyStartTime) {
    this.frequencyStartTime = frequencyStartTime;
  }

  public String getFrequencyEndTime() {
    return frequencyEndTime;
  }

  public void setFrequencyEndTime(String frequencyEndTime) {
    this.frequencyEndTime = frequencyEndTime;
  }
}
