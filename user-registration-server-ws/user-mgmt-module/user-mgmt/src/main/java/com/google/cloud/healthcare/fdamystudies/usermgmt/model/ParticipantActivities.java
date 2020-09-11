/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.usermgmt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "participant_activities")
public class ParticipantActivities {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;

  @Column(name = "_ts")
  private String ts;

  @Column(name = "participant_id")
  private String participantId;

  @Column(name = "study_id")
  private String studyId;

  @Column(name = "activity_id")
  private String activityId;

  @Column(name = "activity_complete_id")
  private Integer activityCompleteId;

  @Column(name = "activity_type")
  private String activityType;

  @Column(name = "bookmark", columnDefinition = "TINYINT(1)")
  private Boolean bookmark = false;

  @Column(name = "status")
  private String status;

  @Column(name = "activity_version")
  private String activityVersion;

  @Column(name = "activity_state")
  private String activityState;

  @Column(name = "activity_run_id")
  private String activityRunId;

  @Column(name = "total")
  private Integer total;

  @Column(name = "completed")
  private Integer completed;

  @Column(name = "missed")
  private Integer missed;

  @Column(name = "application_id")
  private String applicationId;

  @Column(name = "activity_start_date")
  private String activityStartDate;

  @Column(name = "activity_end_date")
  private String activityEndDate;

  @Column(name = "anchordate_version")
  private String anchorDateVersion;

  @Column(name = "anchordate_created_date")
  private String anchorDateCreatedDate;

  @Column(name = "last_modified_date")
  private String lastModifiedDate;

  @Column(name = "user_id")
  private String userId;
}
