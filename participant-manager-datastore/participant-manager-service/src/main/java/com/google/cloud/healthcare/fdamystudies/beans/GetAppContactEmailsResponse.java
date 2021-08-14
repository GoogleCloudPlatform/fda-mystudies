package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetAppContactEmailsResponse {
  private String contactUsEmail;
  private String feedbackEmail;
}
