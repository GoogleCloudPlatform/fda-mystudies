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
