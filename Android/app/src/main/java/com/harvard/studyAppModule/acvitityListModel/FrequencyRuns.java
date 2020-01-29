package com.harvard.studyappmodule.acvititylistmodel;

import io.realm.RealmObject;

public class FrequencyRuns extends RealmObject {
  private String startTime;

  private String endTime;

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
}
