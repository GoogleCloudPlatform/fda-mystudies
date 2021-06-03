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

package com.fdahpstudydesigner.bean;

import java.util.Map;

public class QuestionnaireStepBean {

  private String destinationStep;
  private String destinationText;
  Map<Integer, QuestionnaireStepBean> fromMap;
  private String lineChart;
  private String questionInstructionId;
  private Integer responseType;
  private String responseTypeText;
  private Integer sequenceNo;
  private String statData;
  private Boolean status;
  private String stepId;
  private String stepType;
  private String title;
  private Boolean useAnchorDate;

  public String getDestinationStep() {
    return destinationStep;
  }

  public void setDestinationStep(String destinationStep) {
    this.destinationStep = destinationStep;
  }

  public String getDestinationText() {
    return destinationText;
  }

  public void setDestinationText(String destinationText) {
    this.destinationText = destinationText;
  }

  public Map<Integer, QuestionnaireStepBean> getFromMap() {
    return fromMap;
  }

  public void setFromMap(Map<Integer, QuestionnaireStepBean> fromMap) {
    this.fromMap = fromMap;
  }

  public String getLineChart() {
    return lineChart;
  }

  public void setLineChart(String lineChart) {
    this.lineChart = lineChart;
  }

  public String getQuestionInstructionId() {
    return questionInstructionId;
  }

  public void setQuestionInstructionId(String questionInstructionId) {
    this.questionInstructionId = questionInstructionId;
  }

  public Integer getResponseType() {
    return responseType;
  }

  public void setResponseType(Integer responseType) {
    this.responseType = responseType;
  }

  public String getResponseTypeText() {
    return responseTypeText;
  }

  public void setResponseTypeText(String responseTypeText) {
    this.responseTypeText = responseTypeText;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public String getStatData() {
    return statData;
  }

  public void setStatData(String statData) {
    this.statData = statData;
  }

  public Boolean getStatus() {
    return status;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public String getStepId() {
    return stepId;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  public String getStepType() {
    return stepType;
  }

  public void setStepType(String stepType) {
    this.stepType = stepType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getUseAnchorDate() {
    return useAnchorDate;
  }

  public void setUseAnchorDate(Boolean useAnchorDate) {
    this.useAnchorDate = useAnchorDate;
  }
}
