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
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "comprehension_test_question")
@NamedQueries({
  @NamedQuery(
      name = "comprehensionQuestionByStudyId",
      query =
          " from ComprehensionTestQuestionDto CTDTO"
              + " where CTDTO.studyId =:studyId and CTDTO.status=true and CTDTO.active=true"
              + " ORDER BY CTDTO.sequenceNo"),
})
public class ComprehensionTestQuestionDto implements Serializable {

  private static final long serialVersionUID = 6056127394260427728L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "question_text")
  private String questionText;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "structure_of_correct_ans")
  private Boolean structureOfCorrectAns = false;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "study_version")
  private Integer studyVersion = 1;

  @Column(name = "status")
  private Boolean status = false;

  @Column(name = "active")
  private Boolean active = false;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getQuestionText() {
    return questionText;
  }

  public void setQuestionText(String questionText) {
    this.questionText = questionText;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public Boolean getStructureOfCorrectAns() {
    return structureOfCorrectAns;
  }

  public void setStructureOfCorrectAns(Boolean structureOfCorrectAns) {
    this.structureOfCorrectAns = structureOfCorrectAns;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public Integer getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(Integer studyVersion) {
    this.studyVersion = studyVersion;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }
}
