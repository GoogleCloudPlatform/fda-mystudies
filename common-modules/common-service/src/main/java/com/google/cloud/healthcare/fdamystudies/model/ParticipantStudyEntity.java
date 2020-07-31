/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.beans.Transient;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
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
    name = "participant_study_info",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"participant_id", "site_id"},
          name = "uk_participant_study_info_participant_site")
    })
public class ParticipantStudyEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false, length = ColumnConstraints.ID_LENGTH)
  private String id;

  @Column(name = "participant_id", length = ColumnConstraints.XS_LENGTH)
  private String participantId;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id")
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "participant_registry_site_id")
  private ParticipantRegistrySiteEntity participantRegistrySite;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id")
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id")
  private UserDetailsEntity userDetails;

  @Column(name = "consent_status")
  private Boolean consentStatus = false;

  @Column(name = "status", length = ColumnConstraints.SMALL_LENGTH)
  private String status;

  @Column(name = "bookmark")
  private Boolean bookmark;

  @Column(name = "eligibility")
  private Boolean eligibility;

  @Column(
      name = "enrolled_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp enrolledDate;

  @Column(name = "sharing")
  @Type(type = "text")
  private String sharing;

  @Column(name = "completion")
  private Integer completion;

  @Column(name = "adherence")
  private Integer adherence;

  @Column(
      name = "withdrawal_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp withdrawalDate;

  @Transient
  public String getUserDetailsId() {
    return userDetails != null ? userDetails.getId() : StringUtils.EMPTY;
  }
}
