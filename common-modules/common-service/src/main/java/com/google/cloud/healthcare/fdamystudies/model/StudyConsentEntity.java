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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

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
@Table(name = "study_consent")
public class StudyConsentEntity implements Serializable {

  private static final long serialVersionUID = 6218229749598633153L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "study_consent_id", updatable = false, nullable = false)
  private String id;

  @Column(name = "study_info_id")
  private String studyInfoId;

  @Column(name = "user_details_id")
  private String userId;

  @Column(name = "status")
  private String status;

  @Column(name = "version")
  private String version;

  @Column(name = "pdf")
  private String pdf;

  @Column(name = "pdfpath")
  private String pdfPath;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "participant_study_id", nullable = false, updatable = false)
  private ParticipantStudyEntity participantStudy;

  // represents whether pdf content is stored in db=0 or gcp=1
  @Column(name = "pdfStorage", nullable = false)
  private int pdfStorage;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;
}
