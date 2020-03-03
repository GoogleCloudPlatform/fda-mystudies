package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivitiesBean {
  private String activityId = "";
  private String studyId = "";
  private String activityVersion = "";
  private String status = "";
  private Boolean bookmarked;
  private String activityRunId = "";
  private String activityState;
  private ActivityRunBean activityRun;
}
