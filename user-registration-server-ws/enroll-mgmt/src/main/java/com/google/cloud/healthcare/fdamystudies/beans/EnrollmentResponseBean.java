package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EnrollmentResponseBean {
  private String appToken;
  private Integer siteId;
  private String hashedToken;
  private Integer code;
  private String message;
}
