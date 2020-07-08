/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivitiesRequestBean {

  private String customStudyId;

  private String activityId;

  private Integer activityCompleteId;

  private String activityType;

  private Boolean bookmark = false;

  private String status;

  private String activityVersion;

  private String activityState;

  private String activityRunId;

  private Integer total;

  private Integer completed;

  private Integer missed;

  private LocalDateTime activityStartDate;

  private LocalDateTime activityEndDate;

  private String anchorDateVersion;

  private String anchorDateCreatRappledDate;
}
