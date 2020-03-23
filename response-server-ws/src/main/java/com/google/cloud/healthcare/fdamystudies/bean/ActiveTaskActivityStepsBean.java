/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Provides active task steps information. For example, activity type, format details, title of
 * activity, destinations metadata {@link DestinationBean} and steps details {@link
 * QuestionnaireActivityStepsBean}
 */
@Setter
@Getter
@ToString
public class ActiveTaskActivityStepsBean {

  private String type = AppConstants.EMPTY_STR;
  private String resultType = AppConstants.EMPTY_STR;
  private String key = AppConstants.EMPTY_STR;
  private String text = AppConstants.EMPTY_STR;
  private String[] options = new String[0];
  private Object format = new Object();
  private String title = AppConstants.EMPTY_STR;
  private Boolean skippable = false;
  private String groupName = AppConstants.EMPTY_STR;
  private Boolean repeatable = false;
  private String repeatableText = AppConstants.EMPTY_STR;
  private List<DestinationBean> destinations = new ArrayList<>();
  private String healthDataKey = AppConstants.EMPTY_STR;
  private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String[] getOptions() {
    return options;
  }

  public void setOptions(String[] options) {
    this.options = options;
  }

  public Object getFormat() {
    return format;
  }

  public void setFormat(Object format) {
    this.format = format;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Boolean getSkippable() {
    return skippable;
  }

  public void setSkippable(Boolean skippable) {
    this.skippable = skippable;
  }

  public String getGroupName() {
    return groupName;
  }

  public void setGroupName(String groupName) {
    this.groupName = groupName;
  }

  public Boolean getRepeatable() {
    return repeatable;
  }

  public void setRepeatable(Boolean repeatable) {
    this.repeatable = repeatable;
  }

  public String getRepeatableText() {
    return repeatableText;
  }

  public void setRepeatableText(String repeatableText) {
    this.repeatableText = repeatableText;
  }

  public List<DestinationBean> getDestinations() {
    return destinations;
  }

  public void setDestinations(List<DestinationBean> destinations) {
    this.destinations = destinations;
  }

  public String getHealthDataKey() {
    return healthDataKey;
  }

  public void setHealthDataKey(String healthDataKey) {
    this.healthDataKey = healthDataKey;
  }

  public List<QuestionnaireActivityStepsBean> getSteps() {
    return steps;
  }

  public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
    this.steps = steps;
  }
}
