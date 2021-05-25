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
package com.hphc.mystudies.dto;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "active_task_steps")
public class ActiveTaskStepsDto implements Serializable {

  private static final long serialVersionUID = 8677367389857232011L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "step_id", updatable = false, nullable = false)
  private String stepId;

  @Column(name = "active_task_id")
  private String activeTaskId;

  @Column(name = "active_task_stepscol")
  private String activeTaskStepscol;

  @Column(name = "sd_live_form_id")
  private String sdLiveFormId;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "study_version")
  private Integer studyVersion = 1;

  public String getActiveTaskId() {
    return activeTaskId;
  }

  public void setActiveTaskId(String activeTaskId) {
    this.activeTaskId = activeTaskId;
  }

  public String getActiveTaskStepscol() {
    return activeTaskStepscol;
  }

  public void setActiveTaskStepscol(String activeTaskStepscol) {
    this.activeTaskStepscol = activeTaskStepscol;
  }

  public String getSdLiveFormId() {
    return sdLiveFormId;
  }

  public void setSdLiveFormId(String sdLiveFormId) {
    this.sdLiveFormId = sdLiveFormId;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public String getStepId() {
    return stepId;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  public Integer getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(Integer studyVersion) {
    this.studyVersion = studyVersion;
  }
}
