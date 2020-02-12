package com.harvard.studyappmodule.custom.activetask;

import java.io.Serializable;

public class TappingResultFormat implements Serializable {
  private String duration;
  private double value;

  public String getDuration() {
    return duration;
  }

  public void setDuration(String duration) {
    this.duration = duration;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
