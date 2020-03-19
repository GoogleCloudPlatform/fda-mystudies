package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ConsentStatusBean {

  private String studyId;
  private Boolean eligibility;
  private ConsentReqBean consent;
  private String sharing;

  public Boolean getEligibility() {
    return eligibility;
  }

  public void setEligibility(Boolean eligibility) {
    this.eligibility = eligibility;
  }

  public ConsentReqBean getConsent() {
    return consent;
  }

  public void setConsent(ConsentReqBean consent) {
    this.consent = consent;
  }

  public String getSharing() {
    return sharing;
  }

  public void setSharing(String sharing) {
    this.sharing = sharing;
  }
}
