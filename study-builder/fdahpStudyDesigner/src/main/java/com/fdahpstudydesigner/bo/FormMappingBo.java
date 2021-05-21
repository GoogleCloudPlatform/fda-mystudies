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
