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
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "comprehension_test_response")
public class ComprehensionTestResponseBo implements Serializable {

  private static final long serialVersionUID = 7739882770594873383L;

  @Column(name = "comprehension_test_question_id")
  private String comprehensionTestQuestionId;

  @Column(name = "correct_answer")
  private Boolean correctAnswer;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "response_option")
  private String responseOption;

  @Column(name = "sequence_number")
  private Integer sequenceNumber;

  public String getComprehensionTestQuestionId() {
    return comprehensionTestQuestionId;
  }

  public Boolean getCorrectAnswer() {
    return correctAnswer;
  }

  public String getId() {
    return id;
  }

  public String getResponseOption() {
    return responseOption;
  }

  public void setComprehensionTestQuestionId(String comprehensionTestQuestionId) {
    this.comprehensionTestQuestionId = comprehensionTestQuestionId;
  }

  public void setCorrectAnswer(Boolean correctAnswer) {
    this.correctAnswer = correctAnswer;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setResponseOption(String responseOption) {
    this.responseOption = responseOption;
  }

  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  public void setSequenceNumber(Integer sequenceNumber) {
    this.sequenceNumber = sequenceNumber;
  }
}
