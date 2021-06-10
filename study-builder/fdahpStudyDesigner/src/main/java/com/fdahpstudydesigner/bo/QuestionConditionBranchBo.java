/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "question_condtion_branching")
@NamedQueries({
  @NamedQuery(
      name = "getQuestionConditionBranchList",
      query =
          "from QuestionConditionBranchBo QCBO where QCBO.questionId=:questionId order by QCBO.sequenceNo ASC"),
})
public class QuestionConditionBranchBo implements Serializable {

  private static final long serialVersionUID = 8189512029031610252L;

  @Column(name = "active")
  private Boolean active;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "condition_id", updatable = false, nullable = false)
  private String conditionId;

  @Column(name = "input_type")
  private String inputType;

  @Column(name = "input_type_value")
  private String inputTypeValue;

  @Column(name = "parent_sequence_no")
  private Integer parentSequenceNo;

  @Transient private List<QuestionConditionBranchBo> questionConditionBranchBos;

  @Column(name = "question_id")
  private String questionId;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  public Boolean getActive() {
    return active;
  }

  public String getConditionId() {
    return conditionId;
  }

  public String getInputType() {
    return inputType;
  }

  public String getInputTypeValue() {
    return inputTypeValue;
  }

  public Integer getParentSequenceNo() {
    return parentSequenceNo;
  }

  public List<QuestionConditionBranchBo> getQuestionConditionBranchBos() {
    return questionConditionBranchBos;
  }

  public String getQuestionId() {
    return questionId;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setConditionId(String conditionId) {
    this.conditionId = conditionId;
  }

  public void setInputType(String inputType) {
    this.inputType = inputType;
  }

  public void setInputTypeValue(String inputTypeValue) {
    this.inputTypeValue = inputTypeValue;
  }

  public void setParentSequenceNo(Integer parentSequenceNo) {
    this.parentSequenceNo = parentSequenceNo;
  }

  public void setQuestionConditionBranchBos(
      List<QuestionConditionBranchBo> questionConditionBranchBos) {
    this.questionConditionBranchBos = questionConditionBranchBos;
  }

  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }
}
