package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdateEligibilityConsentBean {
  private Integer code;
  private String message;
  private String consentDocumentFileName;
}
