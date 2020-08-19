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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Setter
@Getter
@Entity
@Table(name = "participant_registry_site")
public class ParticipantRegistrySiteEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id", insertable = true, updatable = false)
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id", insertable = true, updatable = true)
  private StudyEntity study;

  @ToString.Exclude
  @Column(name = "email")
  private String email;

  @ToString.Exclude
  @Column(name = "name")
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
  @Column(name = "onboarding_status")
  private String onboardingStatus;

  @Column(name = "enrollment_token")
  private String enrollmentToken;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "created_by")
  private String createdBy;

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
