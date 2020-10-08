/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * Provides activity metadata information like activity identifier, name of activity, version of
 * activity etc
 *
 */

@Setter
@Getter
@ToString
public class ActivityMetadataBean {

  private String studyId = AppConstants.EMPTY_STR;
  private String studyVersion = AppConstants.EMPTY_STR;
  private String activityId = AppConstants.EMPTY_STR;
  private String activityRunId = AppConstants.EMPTY_STR;
  private String activityType = AppConstants.EMPTY_STR;
  private String name = AppConstants.EMPTY_STR;
  private String version = AppConstants.EMPTY_STR;
  private String lastModified = AppConstants.EMPTY_STR;
  private String startDate = AppConstants.EMPTY_STR;
  private String endDate = AppConstants.EMPTY_STR;
}
