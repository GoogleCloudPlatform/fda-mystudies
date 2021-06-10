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
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "comprehension_test_question")
public class ComprehensionTestQuestionBo implements Serializable {

  private static final long serialVersionUID = -4092393873968937668L;

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "question_text")
  private String questionText;

  @Transient private List<ComprehensionTestResponseBo> responseList;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "structure_of_correct_ans")
  private Boolean structureOfCorrectAns = true;

  @Column(name = "study_id")
  private String studyId;

  public Boolean getActive() {
    return active;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public String getId() {
    return id;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getQuestionText() {
    return questionText;
  }

  public List<ComprehensionTestResponseBo> getResponseList() {
    return responseList;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public Boolean getStatus() {
    return status;
  }

  public Boolean getStructureOfCorrectAns() {
    return structureOfCorrectAns;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public void setResponseList(List<ComprehensionTestResponseBo> responseList) {
    this.responseList = responseList;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setStructureOfCorrectAns(Boolean structureOfCorrectAns) {
    this.structureOfCorrectAns = structureOfCorrectAns;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }
}
