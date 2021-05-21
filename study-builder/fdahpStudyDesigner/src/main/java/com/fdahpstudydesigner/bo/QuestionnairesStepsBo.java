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

package com.fdahpstudydesigner.bo;

import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import java.io.Serializable;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
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
@Table(name = "questionnaires_steps")
@NamedQueries({
  @NamedQuery(
      name = "getQuestionnaireStepSequenceNo",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnairesId and QSBO.active=1 order by QSBO.sequenceNo DESC"),
  @NamedQuery(
      name = "getQuestionnaireStep",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:instructionFormId and QSBO.stepType=:stepType and QSBO.active=1"),
  @NamedQuery(
      name = "getQuestionnaireStepList",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId and QSBO.active=1 order by QSBO.sequenceNo"),
  @NamedQuery(
      name = "checkQuestionnaireStepShortTitle",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId and QSBO.stepShortTitle=:shortTitle"),
  @NamedQuery(
      name = "getForwardQuestionnaireSteps",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnairesId and QSBO.sequenceNo >:sequenceNo and QSBO.active=1 order by QSBO.sequenceNo ASC"),
  @NamedQuery(
      name = "getQuestionnaireStepsByType",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnairesId and QSBO.stepType=:stepType and QSBO.active=1"),
  @NamedQuery(
      name = "getQuestionnaireStepsByquestionnairesId",
      query =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (:questionnairesIds)  and QSBO.active=1"),
})
public class QuestionnairesStepsBo implements Serializable {

  private static final long serialVersionUID = -7908951701723989954L;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "destination_step")
  private String destinationStep;

  @Transient private SortedMap<Integer, QuestionnaireStepBean> formQuestionMap = new TreeMap<>();

  @Column(name = "instruction_form_id")
  private String instructionFormId;

  @Transient private Integer isShorTitleDuplicate = 0;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Transient private List<QuestionConditionBranchBo> questionConditionBranchBoList;

  @Column(name = "questionnaires_id")
  private String questionnairesId;

  @Transient private QuestionReponseTypeBo questionReponseTypeBo;

  @Transient private List<QuestionResponseSubTypeBo> questionResponseSubTypeList;

  @Transient private QuestionsBo questionsBo;

  @Column(name = "repeatable")
  private String repeatable = "No";

  @Column(name = "repeatable_text")
  private String repeatableText;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "skiappable")
  private String skiappable;

  @Column(name = "status")
  private Boolean status;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "step_id", updatable = false, nullable = false)
  private String stepId;

  @Column(name = "step_short_title")
  private String stepShortTitle;

  @Column(name = "step_type")
  private String stepType;

  @Transient private String type;

  public Boolean getActive() {
    return active;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public String getDestinationStep() {
    return destinationStep;
  }

  public SortedMap<Integer, QuestionnaireStepBean> getFormQuestionMap() {
    return formQuestionMap;
  }

  public String getInstructionFormId() {
    return instructionFormId;
  }

  public Integer getIsShorTitleDuplicate() {
    return isShorTitleDuplicate;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public List<QuestionConditionBranchBo> getQuestionConditionBranchBoList() {
    return questionConditionBranchBoList;
  }

  public String getQuestionnairesId() {
    return questionnairesId;
  }

  public QuestionReponseTypeBo getQuestionReponseTypeBo() {
    return questionReponseTypeBo;
  }

  public List<QuestionResponseSubTypeBo> getQuestionResponseSubTypeList() {
    return questionResponseSubTypeList;
  }

  public QuestionsBo getQuestionsBo() {
    return questionsBo;
  }

  public String getRepeatable() {
    return repeatable;
  }

  public String getRepeatableText() {
    return repeatableText;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public String getSkiappable() {
    return skiappable;
  }

  public Boolean getStatus() {
    return status;
  }

  public String getStepId() {
    return stepId;
  }

  public String getStepShortTitle() {
    return stepShortTitle;
  }

  public String getStepType() {
    return stepType;
  }

  public String getType() {
    return type;
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

  public void setDestinationStep(String desId) {
    this.destinationStep = desId;
  }

  public void setFormQuestionMap(SortedMap<Integer, QuestionnaireStepBean> formQuestionMap) {
    this.formQuestionMap = formQuestionMap;
  }

  public void setInstructionFormId(String instructionFormId) {
    this.instructionFormId = instructionFormId;
  }

  public void setIsShorTitleDuplicate(Integer isShorTitleDuplicate) {
    this.isShorTitleDuplicate = isShorTitleDuplicate;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setQuestionConditionBranchBoList(
      List<QuestionConditionBranchBo> questionConditionBranchBoList) {
    this.questionConditionBranchBoList = questionConditionBranchBoList;
  }

  public void setQuestionnairesId(String questionnairesId) {
    this.questionnairesId = questionnairesId;
  }

  public void setQuestionReponseTypeBo(QuestionReponseTypeBo questionReponseTypeBo) {
    this.questionReponseTypeBo = questionReponseTypeBo;
  }

  public void setQuestionResponseSubTypeList(
      List<QuestionResponseSubTypeBo> questionResponseSubTypeList) {
    this.questionResponseSubTypeList = questionResponseSubTypeList;
  }

  public void setQuestionsBo(QuestionsBo questionsBo) {
    this.questionsBo = questionsBo;
  }

  public void setRepeatable(String repeatable) {
    this.repeatable = repeatable;
  }

  public void setRepeatableText(String repeatableText) {
    this.repeatableText = repeatableText;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public void setSkiappable(String skiappable) {
    this.skiappable = skiappable;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  public void setStepShortTitle(String stepShortTitle) {
    this.stepShortTitle = stepShortTitle;
  }

  public void setStepType(String stepType) {
    this.stepType = stepType;
  }

  public void setType(String type) {
    this.type = type;
  }
}
