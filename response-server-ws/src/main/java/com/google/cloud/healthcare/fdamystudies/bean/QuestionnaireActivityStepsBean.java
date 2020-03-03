/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides questionnaire activity steps details i.e. destinations {@link DestinationBean}, activity
 * steps information {@link QuestionnaireActivityStepsBean}
 * 
 */
public class QuestionnaireActivityStepsBean {

  private String type = "";
  private String resultType = "";
  private String key = "";
  private String title = "";
  private String text = "";
  private Boolean skippable = null;
  private Boolean skipped = null;
  private String groupName = "";
  private Boolean repeatable = null;
  private String repeatableText = "";
  private List<DestinationBean> destinations = new ArrayList<>();
  private String healthDataKey = "";
  private Map<String, Object> format = new HashMap<>();
  private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
  private List<String> options = new ArrayList<String>();
  private String startTime = "";
  private String endTime = "";
  private Object value = "";

  private ActivityValueGroupBean actvityValueGroup = new ActivityValueGroupBean();

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Boolean getSkippable() {
    return skippable;
  }

  public void setSkippable(Boolean skippable) {
    this.skippable = skippable;
  }

  public Boolean getSkipped() {
    return skipped;
  }

  public void setSkipped(Boolean skipped) {
    this.skipped = skipped;
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

  public Map<String, Object> getFormat() {
    return format;
  }

  public void setFormat(Map<String, Object> format) {
    this.format = format;
  }

  public List<QuestionnaireActivityStepsBean> getSteps() {
    return steps;
  }

  public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
    this.steps = steps;
  }

  public List<String> getOptions() {
    return options;
  }

  public void setOptions(List<String> options) {
    this.options = options;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public ActivityValueGroupBean getActvityValueGroup() {
    return actvityValueGroup;
  }

  public void setActvityValueGroup(ActivityValueGroupBean actvityValueGroup) {
    this.actvityValueGroup = actvityValueGroup;
  }
}
