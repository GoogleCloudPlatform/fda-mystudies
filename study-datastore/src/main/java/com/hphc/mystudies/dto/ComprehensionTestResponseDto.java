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
@Table(name = "comprehension_test_response")
@NamedQueries({
  @NamedQuery(
      name = "comprehensionQuestionResponseByCTID",
      query =
          " from ComprehensionTestResponseDto CTRDTO"
              + " where CTRDTO.comprehensionTestQuestionId =:comprehensionTestQuestionId"
              + " ORDER BY CTRDTO.sequenceNumber"),
})
public class ComprehensionTestResponseDto implements Serializable {

  private static final long serialVersionUID = -8004751419746704475L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "comprehension_test_question_id")
  private String comprehensionTestQuestionId;

  @Column(name = "response_option")
  private String responseOption;

  @Column(name = "correct_answer")
  private Boolean correctAnswer = false;

  @Column(name = "study_version")
  private Integer studyVersion = 1;

  @Column(name = "sequence_number")
  private Integer sequenceNumber;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getComprehensionTestQuestionId() {
    return comprehensionTestQuestionId;
  }

  public void setComprehensionTestQuestionId(String comprehensionTestQuestionId) {
    this.comprehensionTestQuestionId = comprehensionTestQuestionId;
  }

  public String getResponseOption() {
    return responseOption;
  }

  public void setResponseOption(String responseOption) {
    this.responseOption = responseOption;
  }

  public Boolean getCorrectAnswer() {
    return correctAnswer;
  }

  public void setCorrectAnswer(Boolean correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public Integer getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(Integer studyVersion) {
    this.studyVersion = studyVersion;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
}
