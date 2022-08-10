/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.bean;

public class StudyDetailsBean {
  private String studyId = "",
      studyTitle = "",
      studyVersion = "",
      studyType = "",
      studyStatus = "",
      studyEnrolling = "",
      appId = "",
      appName = "",
      appDescription = "",
      logoImageUrl = "",
      contactEmail = "";

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public String getStudyId() {
    return studyId;
  }

  public String getStudyTitle() {
    return studyTitle;
  }

  public String getStudyVersion() {
    return studyVersion;
  }

  public String getStudyType() {
    return studyType;
  }

  public String getStudyStatus() {
    return studyStatus;
  }

  public String getStudyEnrolling() {
    return studyEnrolling;
  }

  public String getAppId() {
    return appId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setStudyTitle(String studyTitle) {
    this.studyTitle = studyTitle;
  }

  public void setStudyVersion(String studyVersion) {
    this.studyVersion = studyVersion;
  }

  public void setStudyType(String studyType) {
    this.studyType = studyType;
  }

  public void setStudyStatus(String studyStatus) {
    this.studyStatus = studyStatus;
  }

  public void setStudyEnrolling(String studyEnrolling) {
    this.studyEnrolling = studyEnrolling;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppName() {
    return appName;
  }

  public String getAppDescription() {
    return appDescription;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public String getLogoImageUrl() {
    return logoImageUrl;
  }

  public void setLogoImageUrl(String logoImageUrl) {
    this.logoImageUrl = logoImageUrl;
  }
}
