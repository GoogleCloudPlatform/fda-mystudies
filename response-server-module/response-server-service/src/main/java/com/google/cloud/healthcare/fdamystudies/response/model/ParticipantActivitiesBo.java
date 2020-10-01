/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.response.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "participant_activities",
    indexes = {
      @Index(
          name = "participant_activities_participant_identifier_idx",
          columnList = "participant_identifier"),
      @Index(name = "participant_activities_study_id_idx", columnList = "study_id")
    })
public class ParticipantActivitiesBo implements Serializable {

  private static final long serialVersionUID = 1005603353927628403L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;

  @Column(name = "participant_identifier")
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

  @Column(name = "activity_start_date")
  private LocalDateTime activityStartDate;

  @Column(name = "activity_end_date")
  private LocalDateTime activityEndDate;

  @Column(name = "anchordate_version")
  private String anchorDateVersion;

  @Column(name = "anchordate_created_date")
  private LocalDateTime anchorDateCreatRappledDate;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;
}
