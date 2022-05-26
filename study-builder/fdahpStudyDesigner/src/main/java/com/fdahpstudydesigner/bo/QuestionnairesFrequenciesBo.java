/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "questionnaires_frequencies")
public class QuestionnairesFrequenciesBo implements Serializable {

  private static final long serialVersionUID = -1673441133422366930L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "questionnaires_id")
  private String questionnairesId;

  @Column(name = "frequency_date")
  private String frequencyDate;

  @Column(name = "frequency_time")
  private String frequencyTime;

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

  @Column(name = "sequence_number")
  private Integer sequenceNumber;

  public String getFrequencyDate() {
    return frequencyDate;
  }

  public String getFrequencyTime() {
    return frequencyTime;
  }

  public String getId() {
    return id;
  }

  public Boolean getIsLaunchStudy() {
    return isLaunchStudy;
  }

  public Boolean getIsStudyLifeTime() {
    return isStudyLifeTime;
  }

  public String getQuestionnairesId() {
    return questionnairesId;
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

  public void setQuestionnairesId(String questionnairesId) {
    this.questionnairesId = questionnairesId;
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

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
}
