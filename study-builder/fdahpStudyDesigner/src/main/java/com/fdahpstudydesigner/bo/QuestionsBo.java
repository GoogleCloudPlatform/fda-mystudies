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
@Table(name = "questions")
@NamedQueries({
  @NamedQuery(
      name = "getQuestionStep",
      query = "from QuestionsBo QBO where QBO.id=:stepId and QBO.active=1"),
  @NamedQuery(
      name = "deletQuestion",
      query = "delete from QuestionsBo QBO where QBO.id=:questionId"),
  @NamedQuery(
      name = "getQuestionByFormId",
      query = "from QuestionsBo QBO where QBO.id=:formId and QBO.active=1"),
})
public class QuestionsBo implements Serializable {

  private static final long serialVersionUID = 7281155550929426893L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "short_title")
  private String shortTitle;

  @Column(name = "question")
  private String question;

  @Column(name = "description")
  private String description;

  @Column(name = "response_type")
  private Integer responseType;

  @Column(name = "skippable")
  private String skippable;

  @Column(name = "add_line_chart")
  private String addLineChart = "No";

  @Column(name = "line_chart_timerange")
  private String lineChartTimeRange;

  @Column(name = "allow_rollback_chart")
  private String allowRollbackChart;

  @Column(name = "chart_title")
  private String chartTitle;

  @Column(name = "use_stastic_data")
  private String useStasticData = "No";

  @Column(name = "stat_short_name")
  private String statShortName;

  @Column(name = "stat_display_name")
  private String statDisplayName;

  @Column(name = "stat_diaplay_units")
  private String statDisplayUnits;

  @Column(name = "stat_type")
  private String statType;

  @Column(name = "stat_formula")
  private String statFormula;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "status")
  private Boolean status;

  @Column(name = "use_anchor_date")
  private Boolean useAnchorDate = false;

  @Column(name = "allow_healthkit")
  private String allowHealthKit = "No";

  @Column(name = "healthkit_datatype")
  private String healthkitDatatype;

  @Column(name = "anchor_date_id")
  private String anchorDateId;

  @Transient private String type;

  @Transient private String stepType;

  @Transient private String questionnaireId;

  @Transient private String fromId;

  @Transient QuestionnairesStepsBo questionnairesStepsBo;

  @Transient QuestionReponseTypeBo questionReponseTypeBo;

  @Transient private List<QuestionResponseSubTypeBo> questionResponseSubTypeList;

  @Transient private Integer isShorTitleDuplicate = 0;

  @Transient private Integer isStatShortNameDuplicate = 0;

  @Transient private String anchorDateName = "";

  @Transient private String customStudyId = "";

  public Boolean getActive() {
    return active;
  }

  public String getAddLineChart() {
    return addLineChart;
  }

  public String getAllowHealthKit() {
    return allowHealthKit;
  }

  public String getAllowRollbackChart() {
    return allowRollbackChart;
  }

  public String getChartTitle() {
    return chartTitle;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public String getDescription() {
    return description;
  }

  public String getFromId() {
    return fromId;
  }

  public String getHealthkitDatatype() {
    return healthkitDatatype;
  }

  public String getId() {
    return id;
  }

  public Integer getIsShorTitleDuplicate() {
    return isShorTitleDuplicate;
  }

  public Integer getIsStatShortNameDuplicate() {
    return isStatShortNameDuplicate;
  }

  public String getLineChartTimeRange() {
    return lineChartTimeRange;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getQuestion() {
    return question;
  }

  public String getQuestionnaireId() {
    return questionnaireId;
  }

  public QuestionnairesStepsBo getQuestionnairesStepsBo() {
    return questionnairesStepsBo;
  }

  public QuestionReponseTypeBo getQuestionReponseTypeBo() {
    return questionReponseTypeBo;
  }

  public List<QuestionResponseSubTypeBo> getQuestionResponseSubTypeList() {
    return questionResponseSubTypeList;
  }

  public Integer getResponseType() {
    return responseType;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public String getSkippable() {
    return skippable;
  }

  public String getStatDisplayName() {
    return statDisplayName;
  }

  public String getStatDisplayUnits() {
    return statDisplayUnits;
  }

  public String getStatFormula() {
    return statFormula;
  }

  public String getStatShortName() {
    return statShortName;
  }

  public String getStatType() {
    return statType;
  }

  public Boolean getStatus() {
    return status;
  }

  public String getStepType() {
    return stepType;
  }

  public String getType() {
    return type;
  }

  public Boolean getUseAnchorDate() {
    return useAnchorDate;
  }

  public String getUseStasticData() {
    return useStasticData;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setAddLineChart(String addLineChart) {
    this.addLineChart = addLineChart;
  }

  public void setAllowHealthKit(String allowHealthKit) {
    this.allowHealthKit = allowHealthKit;
  }

  public void setAllowRollbackChart(String allowRollbackChart) {
    this.allowRollbackChart = allowRollbackChart;
  }

  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFromId(String fromId) {
    this.fromId = fromId;
  }

  public void setHealthkitDatatype(String healthkitDatatype) {
    this.healthkitDatatype = healthkitDatatype;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setIsShorTitleDuplicate(Integer isShorTitleDuplicate) {
    this.isShorTitleDuplicate = isShorTitleDuplicate;
  }

  public void setIsStatShortNameDuplicate(Integer isStatShortNameDuplicate) {
    this.isStatShortNameDuplicate = isStatShortNameDuplicate;
  }

  public void setLineChartTimeRange(String lineChartTimeRange) {
    this.lineChartTimeRange = lineChartTimeRange;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public void setQuestionnaireId(String questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public void setQuestionnairesStepsBo(QuestionnairesStepsBo questionnairesStepsBo) {
    this.questionnairesStepsBo = questionnairesStepsBo;
  }

  public void setQuestionReponseTypeBo(QuestionReponseTypeBo questionReponseTypeBo) {
    this.questionReponseTypeBo = questionReponseTypeBo;
  }

  public void setQuestionResponseSubTypeList(
      List<QuestionResponseSubTypeBo> questionResponseSubTypeList) {
    this.questionResponseSubTypeList = questionResponseSubTypeList;
  }

  public void setResponseType(Integer responseType) {
    this.responseType = responseType;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setSkippable(String skippable) {
    this.skippable = skippable;
  }

  public void setStatDisplayName(String statDisplayName) {
    this.statDisplayName = statDisplayName;
  }

  public void setStatDisplayUnits(String statDisplayUnits) {
    this.statDisplayUnits = statDisplayUnits;
  }

  public void setStatFormula(String statFormula) {
    this.statFormula = statFormula;
  }

  public void setStatShortName(String statShortName) {
    this.statShortName = statShortName;
  }

  public void setStatType(String statType) {
    this.statType = statType;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setStepType(String stepType) {
    this.stepType = stepType;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setUseAnchorDate(Boolean useAnchorDate) {
    this.useAnchorDate = useAnchorDate;
  }

  public void setUseStasticData(String useStasticData) {
    this.useStasticData = useStasticData;
  }

  public String getAnchorDateId() {
    return anchorDateId;
  }

  public void setAnchorDateId(String anchorDateId) {
    this.anchorDateId = anchorDateId;
  }

  public String getAnchorDateName() {
    return anchorDateName;
  }

  public void setAnchorDateName(String anchorDateName) {
    this.anchorDateName = anchorDateName;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }
}
