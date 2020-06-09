/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantActivityBean {
  private String activityId = AppConstants.EMPTY_STR;
  private Boolean bookmarked = false;
  private String activityState = AppConstants.EMPTY_STR;
  private String activityRunId = AppConstants.EMPTY_STR;
  private String activityVersion = AppConstants.EMPTY_STR;
  private ActivityRunBean activityRun;
}
