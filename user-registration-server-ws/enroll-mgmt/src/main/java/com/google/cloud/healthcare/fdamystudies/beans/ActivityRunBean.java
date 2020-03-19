package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivityRunBean {
  private Integer total;
  private Integer completed;
  private Integer missed;
}
