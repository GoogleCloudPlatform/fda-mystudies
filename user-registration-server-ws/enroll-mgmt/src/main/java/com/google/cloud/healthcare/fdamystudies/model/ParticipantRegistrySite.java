/*
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "participant_registry_site")
public class ParticipantRegistrySite implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "site_id", insertable = false, updatable = false)
  private SiteBo sites;

  /*  @ManyToOne
  @JoinColumn(name = "participant_study_info_id", insertable = false, updatable = false)
  private ParticipantStudiesBO participantStudies;*/

  @Column(name = "email", columnDefinition = "VARCHAR(255)")
  private String email = "";

  @Column(name = "name", columnDefinition = "VARCHAR(255)")
  private String name = "";

  @Column(name = "invitation_date", columnDefinition = "TIMESTAMP")
  private Date invitationDate;

  @Column(name = "enrollment_token_expiry", columnDefinition = "TIMESTAMP")
  private Date enrollmentTokenExpiry;

  @Column(name = "onboarding_status", columnDefinition = "CHAR(8)")
  private String onboardingStatus = "";

  @Column(name = "enrollment_token", columnDefinition = "VARCHAR(50)")
  private String enrollmentToken = "";

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private Date created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public SiteBo getSites() {
    return sites;
  }

  public void setSites(SiteBo sites) {
    this.sites = sites;
  }

  /*  public ParticipantStudiesBO getParticipantStudies() {
    return participantStudies;
  }

  public void setParticipantStudies(ParticipantStudiesBO participantStudies) {
    this.participantStudies = participantStudies;
  }*/

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getInvitationDate() {
    return invitationDate;
  }

  public void setInvitationDate(Date invitationDate) {
    this.invitationDate = invitationDate;
  }

  public Date getEnrollmentTokenExpiry() {
    return enrollmentTokenExpiry;
  }

  public void setEnrollmentTokenExpiry(Date enrollmentTokenExpiry) {
    this.enrollmentTokenExpiry = enrollmentTokenExpiry;
  }

  public String getOnboardingStatus() {
    return onboardingStatus;
  }

  public void setOnboardingStatus(String onboardingStatus) {
    this.onboardingStatus = onboardingStatus;
  }

  public String getEnrollmentToken() {
    return enrollmentToken;
  }

  public void setEnrollmentToken(String enrollmentToken) {
    this.enrollmentToken = enrollmentToken;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }
}
