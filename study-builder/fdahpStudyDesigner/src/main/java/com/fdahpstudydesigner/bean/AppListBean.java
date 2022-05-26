/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.fdahpstudydesigner.bean;

import java.math.BigInteger;

public class AppListBean {
  private String customAppId;
  private String id;
  private String name;
  private String appStatus;
  private String type;
  private String createdOn;
  private boolean flag = false;
  private boolean createFlag = false;
  private boolean viewPermission;
  private String liveAppId;
  private BigInteger studiesCount;
  private BigInteger studyPermissionCount;

  public AppListBean(
      String id, String customAppId, String name, String appStatus, String type, String createdOn) {
    super();
    this.id = id;
    this.customAppId = customAppId;
    this.name = name;
    this.appStatus = appStatus;
    this.type = type;
    this.createdOn = createdOn;
  }

  public AppListBean(
      String id,
      String customAppId,
      String name,
      String appStatus,
      String type,
      String createdOn,
      boolean viewPermission) {
    super();
    this.id = id;
    this.customAppId = customAppId;
    this.name = name;
    this.appStatus = appStatus;
    this.type = type;
    this.createdOn = createdOn;
    this.viewPermission = viewPermission;
  }

  public AppListBean(
      String id,
      String customAppId,
      String name,
      String appStatus,
      String type,
      String createdOn,
      boolean viewPermission,
      boolean flag,
      String liveAppId,
      BigInteger studiesCount) {
    super();
    this.id = id;
    this.customAppId = customAppId;
    this.name = name;
    this.appStatus = appStatus;
    this.type = type;
    this.createdOn = createdOn;
    this.viewPermission = viewPermission;
    this.liveAppId = liveAppId;
    this.flag = flag;
    this.studiesCount = studiesCount;
  }

  public String getCustomAppId() {
    return customAppId;
  }

  public void setCustomAppId(String customAppId) {
    this.customAppId = customAppId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAppStatus() {
    return appStatus;
  }

  public void setAppStatus(String appStatus) {
    this.appStatus = appStatus;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public boolean isFlag() {
    return flag;
  }

  public void setFlag(boolean flag) {
    this.flag = flag;
  }

  public boolean isViewPermission() {
    return viewPermission;
  }

  public void setViewPermission(boolean viewPermission) {
    this.viewPermission = viewPermission;
  }

  public BigInteger getStudiesCount() {
    return studiesCount;
  }

  public void setStudiesCount(BigInteger studiesCount) {
    this.studiesCount = studiesCount;
  }

  public String getLiveAppId() {
    return liveAppId;
  }

  public void setLiveAppId(String liveAppId) {
    this.liveAppId = liveAppId;
  }

  public boolean isCreateFlag() {
    return createFlag;
  }

  public void setCreateFlag(boolean createFlag) {
    this.createFlag = createFlag;
  }

  public BigInteger getStudyPermissionCount() {
    return studyPermissionCount;
  }

  public void setStudyPermissionCount(BigInteger studyPermissionCount) {
    this.studyPermissionCount = studyPermissionCount;
  }
}
