/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "active_task_custom_frequencies")
@NamedQuery(
    name = "ActiveTaskCustomScheduleBo.findAll",
    query = "SELECT a FROM ActiveTaskCustomScheduleBo a")
public class ActiveTaskCustomScheduleBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "active_task_id")
  private Integer activeTaskId;

  @Column(name = "frequency_end_date")
  private String frequencyEndDate;

  @Column(name = "frequency_start_date")
  private String frequencyStartDate;

  @Column(name = "frequency_start_time")
  private String frequencyStartTime;

  @Column(name = "frequency_end_time")
  private String frequencyEndTime;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

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

  public Integer getActiveTaskId() {
    return this.activeTaskId;
  }

  public String getFrequencyEndDate() {
    return this.frequencyEndDate;
  }

  public String getFrequencyStartDate() {
    return this.frequencyStartDate;
  }

  public Integer getId() {
    return this.id;
  }

  public boolean isUsed() {
    return used;
  }

  public void setActiveTaskId(Integer activeTaskId) {
    this.activeTaskId = activeTaskId;
  }

  public void setFrequencyEndDate(String frequencyEndDate) {
    this.frequencyEndDate = frequencyEndDate;
  }

  public void setFrequencyStartDate(String frequencyStartDate) {
    this.frequencyStartDate = frequencyStartDate;
  }

  public void setId(Integer id) {
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
