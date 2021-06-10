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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "instructions")
@NamedQueries({
  @NamedQuery(
      name = "getInstructionStep",
      query = "from InstructionsBo IBO where IBO.id=:id and IBO.active=1"),
})
public class InstructionsBo implements Serializable {

  private static final long serialVersionUID = 1389506581768527442L;

  @Column(name = "active")
  private Boolean active;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "instruction_text", length = 2500)
  private String instructionText;

  @Column(name = "instruction_title", length = 250)
  private String instructionTitle;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Transient private String questionnaireId;

  @Transient private QuestionnairesStepsBo questionnairesStepsBo;

  @Column(name = "status")
  private Boolean status;

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

  public String getId() {
    return id;
  }

  public String getInstructionText() {
    return instructionText;
  }

  public String getInstructionTitle() {
    return instructionTitle;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getQuestionnaireId() {
    return questionnaireId;
  }

  public QuestionnairesStepsBo getQuestionnairesStepsBo() {
    return questionnairesStepsBo;
  }

  public Boolean getStatus() {
    return status;
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

  public void setId(String id) {
    this.id = id;
  }

  public void setInstructionText(String instructionText) {
    this.instructionText = instructionText;
  }

  public void setInstructionTitle(String instructionTitle) {
    this.instructionTitle = instructionTitle;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setQuestionnaireId(String questionnaireId) {
    this.questionnaireId = questionnaireId;
  }

  public void setQuestionnairesStepsBo(QuestionnairesStepsBo questionnairesStepsBo) {
    this.questionnairesStepsBo = questionnairesStepsBo;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setType(String type) {
    this.type = type;
  }
}
