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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.TINY_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;

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
          name = "participant_registry_site_email_study_info_id_uidx")
    })
public class ParticipantRegistrySiteEntity implements Serializable {
  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id", updatable = false)
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id")
  private StudyEntity study;

  @ToString.Exclude
  @Column(length = LARGE_LENGTH)
  private String email;

  @ToString.Exclude
  @Column(length = MEDIUM_LENGTH)
  private String name;

  @Column(name = "invitation_time")
  @CreationTimestamp
  private Timestamp invitationDate;

  @Column(name = "invitation_count", columnDefinition = "BIGINT DEFAULT 0")
  private Long invitationCount;

  @Column(name = "disabled_time")
  private Timestamp disabledDate;

  @Column(name = "enrollment_token_expiry")
  private Timestamp enrollmentTokenExpiry;

  @ToString.Exclude
  @Column(name = "onboarding_status", length = TINY_LENGTH)
  private String onboardingStatus;

  @Column(name = "enrollment_token", unique = true, length = XS_LENGTH)
  private String enrollmentToken;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Column(name = "modified_by", length = LARGE_LENGTH)
  private String modifiedBy;

  @Column(name = "updated_time")
  @UpdateTimestamp
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
