/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.responsedatastore.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "participant_info")
public class ParticipantInfoEntity implements Serializable {

  private static final long serialVersionUID = -8669517487080184697L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "participant_identifier", unique = true, length = SMALL_LENGTH)
  private String participantIdentifier = AppConstants.EMPTY_STR;

  @Column(name = "token_identifier", length = SMALL_LENGTH)
  private String tokenIdentifier = AppConstants.EMPTY_STR;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Column(name = "study_id", length = XS_LENGTH)
  private String studyId;
}
