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
@Table(name = "eligibility")
@NamedQueries({
  @NamedQuery(name = "getEligibiltyById", query = " From EligibilityBo EBO WHERE EBO.id =:id"),
  @NamedQuery(
      name = "getEligibiltyByStudyId",
      query = " From EligibilityBo EBO WHERE EBO.studyId =:studyId")
})
public class EligibilityBo implements Serializable {

  private static final long serialVersionUID = -8985485973006714523L;

  @Transient private String actionType;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "eligibility_mechanism")
  private Integer eligibilityMechanism = 1;

  @Column(name = "failure_outcome_text")
  private String failureOutcomeText;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "instructional_text")
  private String instructionalText;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "study_id")
  private String studyId;

  public String getActionType() {
    return actionType;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public Integer getEligibilityMechanism() {
    return eligibilityMechanism;
  }

  public String getFailureOutcomeText() {
    return failureOutcomeText;
  }

  public String getId() {
    return id;
  }

  public String getInstructionalText() {
    return instructionalText;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setEligibilityMechanism(Integer eligibilityMechanism) {
    this.eligibilityMechanism = eligibilityMechanism;
  }

  public void setFailureOutcomeText(String failureOutcomeText) {
    this.failureOutcomeText = failureOutcomeText;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setInstructionalText(String instructionalText) {
    this.instructionalText = instructionalText;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }
}
