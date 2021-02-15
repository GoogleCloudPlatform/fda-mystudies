/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

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

@Setter
@Getter
@Entity
@Table(
    name = "participant_study_info",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"user_details_id", "study_info_id", "site_id"},
          name = "participant_study_info_user_details_id_study_info_id__uidx")
    },
    indexes = {
      @Index(name = "participant_study_info_site_id_status_idx", columnList = "site_id,status")
    })
public class ParticipantStudyEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "participant_id", unique = true, length = SMALL_LENGTH)
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

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(length = SMALL_LENGTH)
  private String status;

  private Boolean bookmark;

  private Boolean eligibility;

  @Column(name = "enrolled_timestamp")
  private Timestamp enrolledDate;

  @Column(name = "data_sharing_status", length = SMALL_LENGTH)
  private String sharing;

  private Integer completion;

  private Integer adherence;

  @Column(name = "withdrawal_timestamp")
  private Timestamp withdrawalDate;

  @Transient
  public String getUserDetailsId() {
    return userDetails != null ? userDetails.getId() : StringUtils.EMPTY;
  }

  @Transient
  public String getStudyId() {
    return study != null ? study.getCustomId() : StringUtils.EMPTY;
  }
}
