/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

@Entity
@Table(name = "participant_info")
public class ParticipantBo implements Serializable {

  private static final long serialVersionUID = -8669517487080184697L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(name = "participant_identifier", unique = true)
  private String participantIdentifier = AppConstants.EMPTY_STR;

  @Column(name = "token_identifier")
  private String tokenIdentifier = AppConstants.EMPTY_STR;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "study_id")
  private String studyId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getParticipantIdentifier() {
    return participantIdentifier;
  }

  public void setParticipantIdentifier(String participantIdentifier) {
    this.participantIdentifier = participantIdentifier;
  }

  public String getTokenIdentifier() {
    return tokenIdentifier;
  }

  public void setTokenIdentifier(String tokenIdentifier) {
    this.tokenIdentifier = tokenIdentifier;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }
}
