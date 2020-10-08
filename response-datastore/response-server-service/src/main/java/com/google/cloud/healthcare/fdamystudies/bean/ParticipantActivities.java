/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantActivities {

  private Integer id = 0;
  private String participantId;
  private String customStudyId;
  private String activityId;
  private Integer activityCompleteId = 0;
  private String activityType;
  private Boolean bookmark = false;
  private String status;
  private String activityVersion;
  private String activityState;
  private String activityRunId;

  private Integer total = 0;
  private Integer completed = 0;
  private Integer missed = 0;
}
