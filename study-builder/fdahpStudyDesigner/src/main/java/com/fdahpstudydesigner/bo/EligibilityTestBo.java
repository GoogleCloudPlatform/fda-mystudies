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
import org.hibernate.annotations.Type;

@Entity
@Table(name = "eligibility_test")
@NamedQueries({
  @NamedQuery(
      name = "EligibilityTestBo.findAll",
      query =
          "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true ORDER BY ETB.sequenceNo"),
  @NamedQuery(
      name = "EligibilityTestBo.findById",
      query =
          "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.id=:eligibilityTestId ORDER BY ETB.sequenceNo"),
  @NamedQuery(
      name = "EligibilityTestBo.findByEligibilityId",
      query =
          "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.eligibilityId=:eligibilityId ORDER BY ETB.sequenceNo"),
  @NamedQuery(
      name = "EligibilityTestBo.findByEligibilityIdAndSequenceNo",
      query =
          "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.active = true AND ETB.eligibilityId=:eligibilityId AND ETB.sequenceNo =:sequenceNo"),
  @NamedQuery(
      name = "EligibilityTestBo.deleteById",
      query = "UPDATE EligibilityTestBo SET active = false WHERE id=:eligibilityTestId "),
  @NamedQuery(
      name = "EligibilityTestBo.validateShortTitle",
      query =
          "SELECT ETB FROM EligibilityTestBo ETB WHERE ETB.shortTitle =:shortTitle AND ETB.id !=:eligibilityTestId AND ETB.eligibilityId =:eligibilityId ")
})
public class EligibilityTestBo implements Serializable {

  private static final long serialVersionUID = -6517033483482921515L;

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  @Column(name = "active")
  private Boolean active = true;

  @Column(name = "eligibility_id")
  private String eligibilityId;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "question")
  private String question;

  @Column(name = "response_format")
  private String responseFormat;

  @Column(name = "response_no_option")
  private Boolean responseNoOption;

  @Column(name = "response_yes_option")
  private Boolean responseYesOption;

  @Column(name = "sequence_no")
  private Integer sequenceNo;

  @Column(name = "short_title")
  private String shortTitle;

  @Column(name = "status")
  private Boolean status = false;

  @Transient private String type;

  @Column(name = "is_used")
  @Type(type = "yes_no")
  private boolean used = false;

  public Boolean getActive() {
    return active;
  }

  public String getEligibilityId() {
    return eligibilityId;
  }

  public String getId() {
    return id;
  }

  public String getQuestion() {
    return question;
  }

  public String getResponseFormat() {
    return responseFormat;
  }

  public Boolean getResponseNoOption() {
    return responseNoOption;
  }

  public Boolean getResponseYesOption() {
    return responseYesOption;
  }

  public Integer getSequenceNo() {
    return sequenceNo;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public Boolean getStatus() {
    return status;
  }

  public String getType() {
    return type;
  }

  public boolean isUsed() {
    return used;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public void setEligibilityId(String eligibilityId) {
    this.eligibilityId = eligibilityId;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public void setResponseFormat(String responseFormat) {
    this.responseFormat = responseFormat;
  }

  public void setResponseNoOption(Boolean responseNoOption) {
    this.responseNoOption = responseNoOption;
  }

  public void setResponseYesOption(Boolean responseYesOption) {
    this.responseYesOption = responseYesOption;
  }

  public void setSequenceNo(Integer sequenceNo) {
    this.sequenceNo = sequenceNo;
  }

  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setStatus(Boolean status) {
    this.status = status;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setUsed(boolean used) {
    this.used = used;
  }
}
