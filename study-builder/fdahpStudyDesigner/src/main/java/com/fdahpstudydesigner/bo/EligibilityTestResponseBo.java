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
@Table(name = "eligibility_test_response")
public class EligibilityTestResponseBo implements Serializable {

  private static final long serialVersionUID = -6967340852884815498L;

  @Column(name = "destination_question")
  private String destinationQuestion;

  @Column(name = "eligibility_test_id")
  private String eligibilityTestId;

  @Column(name = "pass_fail")
  private String passFail;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "response_id", updatable = false, nullable = false)
  private String responseId;

  @Column(name = "response_option")
  private String responseOption;

  public String getDestinationQuestion() {
    return destinationQuestion;
  }

  public String getEligibilityTestId() {
    return eligibilityTestId;
  }

  public String getPassFail() {
    return passFail;
  }

  public String getResponseId() {
    return responseId;
  }

  public String getResponseOption() {
    return responseOption;
  }

  public void setDestinationQuestion(String destinationQuestion) {
    this.destinationQuestion = destinationQuestion;
  }

  public void setEligibilityTestId(String eligibilityTestId) {
    this.eligibilityTestId = eligibilityTestId;
  }

  public void setPassFail(String passFail) {
    this.passFail = passFail;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public void setResponseOption(String responseOption) {
    this.responseOption = responseOption;
  }
}
