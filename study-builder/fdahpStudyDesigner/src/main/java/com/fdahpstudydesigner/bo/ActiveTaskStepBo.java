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
@Table(name = "active_task_steps")
@NamedQuery(name = "ActiveTaskStepBo.findAll", query = "SELECT a FROM ActiveTaskStepBo a")
public class ActiveTaskStepBo implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "active_task_id")
  private String activetaskId;

  @Column(name = "active_task_stepscol")
  private String activeTaskStepscol;

  @Column(name = "sd_live_form_id")
  private String sdLiveFormId;

  @Column(name = "sequence_no")
  private int sequenceNo;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "step_id", updatable = false, nullable = false)
  private String stepId;

  public ActiveTaskStepBo() {}

  public String getActivetaskId() {
    return activetaskId;
  }

  public String getActiveTaskStepscol() {
    return this.activeTaskStepscol;
  }

  public String getSdLiveFormId() {
    return this.sdLiveFormId;
  }

  public int getSequenceNo() {
    return this.sequenceNo;
  }

  public String getStepId() {
    return this.stepId;
  }

  public void setActivetaskId(String activetaskId) {
    this.activetaskId = activetaskId;
  }

  public void setActiveTaskStepscol(String activeTaskStepscol) {
    this.activeTaskStepscol = activeTaskStepscol;
  }

  public void setSdLiveFormId(String sdLiveFormId) {
    this.sdLiveFormId = sdLiveFormId;
  }

  public void setSequenceNo(int sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }
}
