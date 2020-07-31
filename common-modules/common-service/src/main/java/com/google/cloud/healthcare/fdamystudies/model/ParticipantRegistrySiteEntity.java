/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Setter
@Getter
@Entity
@Table(
    name = "participant_registry_site",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"email", "study_info_id"},
          name = "uk_participant_registry_site_email_study")
    })
public class ParticipantRegistrySiteEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false, length = ColumnConstraints.ID_LENGTH)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id", insertable = true, updatable = false)
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id", insertable = true, updatable = true)
  private StudyEntity study;

  @ToString.Exclude
  @Column(name = "email", length = ColumnConstraints.LARGE_LENGTH)
  private String email;

  @ToString.Exclude
  @Column(name = "name", length = ColumnConstraints.MEDIUM_LENGTH)
  private String name;

  @Column(
      name = "invitation_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp invitationDate;

  @Column(name = "invitation_count", columnDefinition = "BIGINT DEFAULT 0")
  private Long invitationCount;

  @Column(
      name = "disabled_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp disabledDate;

  @Column(
      name = "enrollment_token_expiry",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp enrollmentTokenExpiry;

  @ToString.Exclude
  @Column(name = "onboarding_status", length = ColumnConstraints.TINY_LENGTH)
  private String onboardingStatus;

  @Column(name = "enrollment_token", unique = true, length = ColumnConstraints.SMALL_LENGTH)
  private String enrollmentToken;

  @Column(
      name = "created_on",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "created_by", length = ColumnConstraints.LARGE_LENGTH)
  private String createdBy;

  @Column(name = "modified_by", length = ColumnConstraints.LARGE_LENGTH)
  private String modifiedBy;

  @Column(
      name = "modified_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modified;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "participantRegistrySite")
  private List<ParticipantStudyEntity> participantStudies = new ArrayList<>();

  public void addParticipantStudiesEntity(ParticipantStudyEntity participantStudiesEntity) {
    participantStudies.add(participantStudiesEntity);
    participantStudiesEntity.setParticipantRegistrySite(this);
  }
}
