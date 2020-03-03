/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;

public class ParticipantDetailsBean {
  private Integer participantRegistrySiteid;

  private Integer siteId;

  private String customLocationId;

  private String locationName;

  private String customStudyId;

  private String studyName;

  private String customAppId;

  private String appName;

  private String onboardringStatus;

  private String email;

  private String invitationDate;

  private List<Enrollments> enrollments = new ArrayList<>();

  private List<ConsentHistory> consentHistory = new ArrayList<>();

  private ErrorBean error = new ErrorBean();

  public String getLocationName() {
    return locationName;
  }

  public void setLocationName(String locationName) {
    this.locationName = locationName;
  }

  public String getStudyName() {
    return studyName;
  }

  public void setStudyName(String studyName) {
    this.studyName = studyName;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getCustomAppId() {
    return customAppId;
  }

  public void setCustomAppId(String customAppId) {
    this.customAppId = customAppId;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public String getCustomLocationId() {
    return customLocationId;
  }

  public void setCustomLocationId(String customLocationId) {
    this.customLocationId = customLocationId;
  }

  public List<Enrollments> getEnrollments() {
    return enrollments;
  }

  public void setEnrollments(List<Enrollments> enrollments) {
    this.enrollments = enrollments;
  }

  public List<ConsentHistory> getConsentHistory() {
    return consentHistory;
  }

  public void setConsentHistory(List<ConsentHistory> consentHistory) {
    this.consentHistory = consentHistory;
  }

  public String getOnboardringStatus() {
    return onboardringStatus;
  }

  public void setOnboardringStatus(String onboardringStatus) {
    this.onboardringStatus = onboardringStatus;
  }

  public Integer getSiteId() {
    return siteId;
  }

  public void setSiteId(Integer siteId) {
    this.siteId = siteId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getInvitationDate() {
    return invitationDate;
  }

  public void setInvitationDate(String invitationDate) {
    this.invitationDate = invitationDate;
  }

  public ErrorBean getError() {
    return error;
  }

  public void setError(ErrorBean error) {
    this.error = error;
  }

  public Integer getParticipantRegistrySiteid() {
    return participantRegistrySiteid;
  }

  public void setParticipantRegistrySiteid(Integer participantRegistrySiteid) {
    this.participantRegistrySiteid = participantRegistrySiteid;
  }
}
