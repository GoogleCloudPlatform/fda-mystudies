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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Provides questionnaire activity steps details i.e. destinations {@link DestinationBean}, activity
 * steps information {@link QuestionnaireActivityStepsBean}
 */
@Setter
@Getter
@ToString
public class QuestionnaireActivityStepsBean {

  private String type = AppConstants.EMPTY_STR;
  private String resultType = AppConstants.EMPTY_STR;
  private String key = AppConstants.EMPTY_STR;
  private String title = AppConstants.EMPTY_STR;
  private String text = AppConstants.EMPTY_STR;
  private Boolean skippable = null;
  private Boolean skipped = null;
  private String groupName = AppConstants.EMPTY_STR;
  private Boolean repeatable = null;
  private String repeatableText = AppConstants.EMPTY_STR;
  private List<DestinationBean> destinations = new ArrayList<>();
  private String healthDataKey = AppConstants.EMPTY_STR;
  private Map<String, Object> format = new HashMap<>();
  private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
  private List<String> options = new ArrayList<>();
  private String startTime = AppConstants.EMPTY_STR;
  private String endTime = AppConstants.EMPTY_STR;
  private Object value = AppConstants.EMPTY_STR;

  private ActivityValueGroupBean actvityValueGroup = new ActivityValueGroupBean();
}
