/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Setter
@Getter
@Entity
@Table(name = "study_consent")
public class StudyConsentEntity implements Serializable {

  private static final long serialVersionUID = 6218229749598633153L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_info_id")
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_details_id")
  private UserDetailsEntity userDetails;

  @Column(length = SMALL_LENGTH)
  private String status;

  @Column(length = SMALL_LENGTH)
  private String version;

  @Type(type = "text")
  private String pdf;

  @Column(length = LARGE_LENGTH)
  private String pdfPath;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "participant_study_id")
  private ParticipantStudyEntity participantStudy;

  // represents whether pdf content is stored in db=0 or gcp=1
  @Column(nullable = false)
  private int pdfStorage;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "data_sharing_status", length = SMALL_LENGTH)
  private String sharing;

  @Column(name = "consent_date")
  private Timestamp consentDate;
}
