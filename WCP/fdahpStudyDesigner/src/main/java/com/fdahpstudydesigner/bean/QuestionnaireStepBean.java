package com.fdahpstudydesigner.bean;

import java.util.Map;

public class QuestionnaireStepBean {

  private Integer destinationStep;
  private String destinationText;
  Map<Integer, QuestionnaireStepBean> fromMap;
  private String lineChart;
  private Integer questionInstructionId;
  private Integer responseType;
  private String responseTypeText;
  private Integer sequenceNo;
  private String statData;
  private Boolean status;
  private Integer stepId;
  private String stepType;
  private String title;
  private Boolean useAnchorDate;

  public Integer getDestinationStep() {
    return destinationStep;
  }

  public void setDestinationStep(Integer destinationStep) {
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

  public Integer getQuestionInstructionId() {
    return questionInstructionId;
  }

  public void setQuestionInstructionId(Integer questionInstructionId) {
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

  public Integer getStepId() {
    return stepId;
  }

  public void setStepId(Integer stepId) {
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
