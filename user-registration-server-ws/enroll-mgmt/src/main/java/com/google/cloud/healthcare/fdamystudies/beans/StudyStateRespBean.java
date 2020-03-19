package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StudyStateRespBean {
  private Integer code = 0;
  private String message = "";
}
