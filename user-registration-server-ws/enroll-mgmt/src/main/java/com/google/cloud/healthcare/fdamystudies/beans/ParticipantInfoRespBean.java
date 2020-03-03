package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantInfoRespBean {
  private String sharing;
  private String enrollment;
  private String withdrawl;
  private String message;
}
