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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;

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
          columnNames = {"user_details_id", "study_info_id"},
          name = "participant_study_info_user_details_id_study_info_id__uidx")
    })
public class ParticipantStudyEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "participant_id", unique = true, length = XS_LENGTH)
  private String participantId;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id")
  @Index(name = "participant_study_info_study_info_idx")
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "participant_registry_site_id")
  @Index(name = "participant_study_info_participant_registry_site_idx")
  private ParticipantRegistrySiteEntity participantRegistrySite;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id")
  @Index(name = "participant_study_info_site_idx")
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id")
  @Index(name = "participant_study_info_user_details_idx")
  private UserDetailsEntity userDetails;

  @Column(name = "consent_status")
  private Boolean consentStatus = false;

  @Column(length = SMALL_LENGTH)
  private String status;

  private Boolean bookmark;

  private Boolean eligibility;

  @Column(name = "enrolled_time")
  @CreationTimestamp
  private Timestamp enrolledDate;

  @Type(type = "java.lang.Boolean")
  private Boolean sharing;

  private Integer completion;

  private Integer adherence;

  @Column(name = "withdrawal_time")
  @CreationTimestamp
  private Timestamp withdrawalDate;

  @Transient
  public String getUserDetailsId() {
    return userDetails != null ? userDetails.getId() : StringUtils.EMPTY;
  }
}
