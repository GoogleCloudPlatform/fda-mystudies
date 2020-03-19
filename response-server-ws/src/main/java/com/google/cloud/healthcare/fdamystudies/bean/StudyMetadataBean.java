/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.io.Serializable;

public class StudyMetadataBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private String studyId;
  private String studyTitle;
  private String studyVersion;
  private String studyType;
  private String studyStatus;
  private String studyCategory;
  private String studyTagline;
  private String studySponsor;
  private String studyEnrolling;
  private String appId;
  private String appName;
  private String appDescription;
  private String orgId;

  public String getStudyId() {
    return studyId;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public String getStudyTitle() {
    return studyTitle;
  }

  public void setStudyTitle(String studyTitle) {
    this.studyTitle = studyTitle;
  }

  public String getStudyVersion() {
    return studyVersion;
  }

  public void setStudyVersion(String studyVersion) {
    this.studyVersion = studyVersion;
  }

  public String getStudyType() {
    return studyType;
  }

  public void setStudyType(String studyType) {
    this.studyType = studyType;
  }

  public String getStudyStatus() {
    return studyStatus;
  }

  public void setStudyStatus(String studyStatus) {
    this.studyStatus = studyStatus;
  }

  public String getStudyCategory() {
    return studyCategory;
  }

  public void setStudyCategory(String studyCategory) {
    this.studyCategory = studyCategory;
  }

  public String getStudyTagline() {
    return studyTagline;
  }

  public void setStudyTagline(String studyTagline) {
    this.studyTagline = studyTagline;
  }

  public String getStudySponsor() {
    return studySponsor;
  }

  public void setStudySponsor(String studySponsor) {
    this.studySponsor = studySponsor;
  }

  public String getStudyEnrolling() {
    return studyEnrolling;
  }

  public void setStudyEnrolling(String studyEnrolling) {
    this.studyEnrolling = studyEnrolling;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getAppDescription() {
    return appDescription;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }
}
