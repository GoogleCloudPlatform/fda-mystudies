/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
  private boolean viewPermission;
  private String liveAppId;
  private BigInteger studiesCount;

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
}
