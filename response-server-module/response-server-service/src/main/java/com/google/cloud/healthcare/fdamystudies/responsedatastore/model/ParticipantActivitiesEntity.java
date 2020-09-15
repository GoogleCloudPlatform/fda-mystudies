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

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "participant_activities")
public class ParticipantActivitiesEntity implements Serializable {

  private static final long serialVersionUID = 1005603353927628403L;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "participant_id", nullable = false, length = SMALL_LENGTH)
  private String participantId;

  @Column(name = "study_id", nullable = false, length = XS_LENGTH)
  private String studyId;

  @Column(name = "activity_id", nullable = false, length = SMALL_LENGTH)
  private String activityId;

  @Column(name = "bookmark", nullable = false, columnDefinition = "TINYINT(1) default 0")
  private Boolean bookmark;

  @Column(name = "activity_version", nullable = false, length = XS_LENGTH)
  private String activityVersion;

  @Column(name = "activity_state", nullable = false, length = LARGE_LENGTH)
  private String activityState;

  @Column(name = "activity_run_id", nullable = false, length = XS_LENGTH)
  private String activityRunId;

  @Column(name = "total")
  private Integer total;

  @Column(name = "completed")
  private Integer completed;

  @Column(name = "missed")
  private Integer missed;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;
}
