package com.harvard.studyappmodule.consent.model;

import io.realm.RealmObject;

public class Review extends RealmObject {
  private String reasonForConsent;

  private String reviewHTML;

  private String signatureTitle;

  public String getReasonForConsent() {
    return reasonForConsent;
  }

  public void setReasonForConsent(String reasonForConsent) {
    this.reasonForConsent = reasonForConsent;
  }

  public String getReviewHTML() {
    return reviewHTML;
  }

  public void setReviewHTML(String reviewHTML) {
    this.reviewHTML = reviewHTML;
  }

  public String getSignatureTitle() {
    return signatureTitle;
  }

  public void setSignatureTitle(String signatureTitle) {
    this.signatureTitle = signatureTitle;
  }
}
