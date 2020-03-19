package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class EnrollmentBodyProvider {
  private String tokenIdentifier;
  private String customStudyId;
}
