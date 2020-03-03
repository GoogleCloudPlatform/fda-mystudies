/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
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
import lombok.ToString;

@ToString
@Entity
@Table(name = "participant_registry_site")
public class ParticipantRegistrySite implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "site_id", insertable = true, updatable = false)
  private SiteBo sites;

  @ManyToOne
  @JoinColumn(name = "study_info_id", insertable = true, updatable = false)
  private StudyInfoBO studyInfo;

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

  @Column(name = "invitation_count", nullable = false, columnDefinition = "bigint(20) default 0")
  private Long invitationCount = 0L;

  @Column(name = "disabled_date", columnDefinition = "TIMESTAMP default NULL")
  private Date disabledDate;

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

  public StudyInfoBO getStudyInfo() {
    return studyInfo;
  }

  public void setStudyInfo(StudyInfoBO studyInfo) {
    this.studyInfo = studyInfo;
  }

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

  public ParticipantStudiesBO getParticipantStudies() {
    return null;
  }

  public Long getInvitationCount() {
    return invitationCount;
  }

  public void setInvitationCount(Long invitationCount) {
    this.invitationCount = invitationCount;
  }

  /** @return the disabledDate */
  public Date getDisabledDate() {
    return disabledDate;
  }

  /** @param disabledDate the disabledDate to set */
  public void setDisabledDate(Date disabledDate) {
    this.disabledDate = disabledDate;
  }
}
