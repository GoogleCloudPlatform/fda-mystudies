/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "participant_registry_site",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"email", "study_info_id"},
          name = "participant_registry_site_email_study_info_id_uidx")
    })
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
  @JoinColumn(name = "study_info_id", insertable = true, updatable = true)
  private StudyInfoBO studyInfo;

  @Column(name = "email", columnDefinition = "VARCHAR(255)")
  private String email = "";

  @Column(name = "name", columnDefinition = "VARCHAR(255)")
  private String name = "";

  @Column(name = "invitation_date", columnDefinition = "TIMESTAMP")
  private Date invitationDate;

  @Column(name = "invitation_count", nullable = false, columnDefinition = "bigint(20) default 0")
  private Long invitationCount = 0L;

  @Column(name = "disabled_date", columnDefinition = "TIMESTAMP default NULL")
  private Date disabledDate;

  @Column(name = "enrollment_token_expiry", columnDefinition = "TIMESTAMP")
  private Date enrollmentTokenExpiry;

  @Column(name = "onboarding_status", columnDefinition = "CHAR(1)")
  private String onboardingStatus = "";

  @Column(name = "enrollment_token", unique = true, columnDefinition = "VARCHAR(50)")
  private String enrollmentToken = "";

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private Date created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;
}
