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
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

@Entity
@Table(name = "form_mapping")
@NamedQueries({
  @NamedQuery(
      name = "getFormMappingBO",
      query = "from FormMappingBo FMBO where FMBO.questionId=:questionId"),
  @NamedQuery(
      name = "updateFromQuestionSequenceNo",
      query =
          "update FormMappingBo f set f.sequenceNo=:newOrderNumber where f.id=:id and f.active=1"),
  @NamedQuery(
      name = "getFromByIdAndSequenceNo",
      query =
          "From FormMappingBo FMBO where FMBO.formId=:formId and FMBO.sequenceNo=:oldOrderNumber and FMBO.active=1"),
  @NamedQuery(
      name = "deleteFormQuestion",
      query =
          "delete from FormMappingBo FMBO where FMBO.formId=:formId and FMBO.questionId=:questionId"),
  @NamedQuery(
      name = "getFormQuestion",
      query = "from FormMappingBo FMBO where FMBO.formId=:formId and FMBO.questionId=:questionId"),
  @NamedQuery(
      name = "getFormByFormId",
      query = "from FormMappingBo FMBO where FMBO.formId=:formId order by id desc"),
})
public class FormMappingBo implements Serializable {

  private static final long serialVersionUID = -1590511768535969365L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "form_id")
  private String formId;

  @Column(name = "question_id")
  private String questionId;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "active")
  private Boolean active = true;

  public Boolean getActive() {
    return active;
  }

  public String getFormId() {
    return formId;
  }

  public String getId() {
    return id;
  }

  public String getQuestionId() {
    return questionId;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setFormId(String formId) {
    this.formId = formId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setQuestionId(String questionId) {
    this.questionId = questionId;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }
}
