/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

public class StudyListBean {

  private String category;
  private String createdFirstName;
  private String createdLastName;
  private String createdOn;
  private String customStudyId;
  private boolean flag = false;
  private String id;
  private String liveStudyId;
  private String name;
  private String projectLeadName;
  private String researchSponsor;
  private String status;
  private boolean viewPermission;
  private String appId;

  public StudyListBean(String id, String customStudyId, String name, boolean viewPermission) {
    super();
    this.id = id;
    this.customStudyId = customStudyId;
    this.name = name;
    this.viewPermission = viewPermission;
  }

  public StudyListBean(
      String id,
      String customStudyId,
      String name,
      String category,
      String researchSponsor,
      String createdFirstName,
      String createdLastName,
      boolean viewPermission,
      String status,
      String createdOn,
      String appid) {
    super();
    this.id = id;
    this.customStudyId = customStudyId;
    this.name = name;
    this.category = category;
    this.researchSponsor = researchSponsor;
    this.createdFirstName = createdFirstName;
    this.createdLastName = createdLastName;
    this.viewPermission = viewPermission;
    this.status = status;
    this.createdOn = createdOn;
    this.appId = appid;
  }

  public StudyListBean(
      String id,
      String customStudyId,
      String name,
      String category,
      String researchSponsor,
      String createdFirstName,
      String createdLastName,
      String status,
      String createdOn,
      String appid) {
    super();
    this.id = id;
    this.customStudyId = customStudyId;
    this.name = name;
    this.category = category;
    this.researchSponsor = researchSponsor;
    this.createdFirstName = createdFirstName;
    this.createdLastName = createdLastName;
    this.status = status;
    this.createdOn = createdOn;
    this.appId = appid;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getCreatedFirstName() {
    return createdFirstName;
  }

  public void setCreatedFirstName(String createdFirstName) {
    this.createdFirstName = createdFirstName;
  }

  public String getCreatedLastName() {
    return createdLastName;
  }

  public void setCreatedLastName(String createdLastName) {
    this.createdLastName = createdLastName;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getCustomStudyId() {
    return customStudyId;
  }

  public void setCustomStudyId(String customStudyId) {
    this.customStudyId = customStudyId;
  }

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getLiveStudyId() {
    return liveStudyId;
  }

  public void setLiveStudyId(String liveStudyId) {
    this.liveStudyId = liveStudyId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProjectLeadName() {
    return projectLeadName;
  }

  public void setProjectLeadName(String projectLeadName) {
    this.projectLeadName = projectLeadName;
  }

  public String getResearchSponsor() {
    return researchSponsor;
  }

  public void setResearchSponsor(String researchSponsor) {
    this.researchSponsor = researchSponsor;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public boolean isViewPermission() {
    return viewPermission;
  }

  public void setViewPermission(boolean viewPermission) {
    this.viewPermission = viewPermission;
  }
}
