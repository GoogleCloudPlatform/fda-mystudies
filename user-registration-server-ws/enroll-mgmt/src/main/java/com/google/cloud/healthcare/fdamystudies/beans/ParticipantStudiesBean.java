package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ParticipantStudiesBean {
  private String appToken;
  private String siteid;
  private String hashedToken;
}
