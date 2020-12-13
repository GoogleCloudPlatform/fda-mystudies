/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "participant_enrollment_history")
public class ParticipantEnrollmentHistoryEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinColumn(name = "site_id", updatable = false)
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinColumn(name = "study_info_id")
  private StudyEntity study;

  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
  @JoinColumn(name = "app_info_id")
  private AppEntity app;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id")
  private UserDetailsEntity userDetails;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "participant_registry_site_id")
  private ParticipantRegistrySiteEntity participantRegistrySite;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(length = SMALL_LENGTH)
  private String status;
}
