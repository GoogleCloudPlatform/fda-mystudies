/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivityResponseDataStructureBean {
  private String resultType = AppConstants.EMPTY_STR;
  private String startTime = AppConstants.EMPTY_STR;
  private String endTime = AppConstants.EMPTY_STR;
  private List<QuestionnaireActivityStepsBean> results = new ArrayList<>();
}
