/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.response.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
// @Entity
// @Table(name = "participant_activities")
public class ParticipantActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;

  @Column(name = "_ts")
  private String _ts;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "participant_id", insertable = false, updatable = false)
  private ParticipantBo participantInfo;

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
}
