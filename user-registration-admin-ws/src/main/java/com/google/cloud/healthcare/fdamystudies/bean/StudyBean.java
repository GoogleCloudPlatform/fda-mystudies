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

public class StudyBean {
  private Integer id;
  private String customId;
  private String name;
  private Long totalSitesCount = 0L;
  private List<SiteBean> sites = new ArrayList<>();
  private String type;
  private Integer appInfoId;
  private String appId;
  private Long invited = 0L;
  private Long enrolled = 0L;
  private Double enrollmentPercentage = 0d;
  private Integer studyPermission = 0;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getCustomId() {
    return customId;
  }

  public void setCustomId(String customId) {
    this.customId = customId;
  }

  public List<SiteBean> getSites() {
    return sites;
  }

  public void setSites(List<SiteBean> sites) {
    this.sites = sites;
  }

  public Long getTotalSitesCount() {
    return totalSitesCount;
  }

  public void setTotalSitesCount(Long totalSitesCount) {
    this.totalSitesCount = totalSitesCount;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Integer getAppInfoId() {
    return appInfoId;
  }

  public void setAppInfoId(Integer appInfoId) {
    this.appInfoId = appInfoId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public Long getInvitedCount() {
    return invited;
  }

  public void setInvitedCount(Long invitedCount) {
    this.invited = invitedCount;
  }

  public Long getEnrolledCount() {
    return enrolled;
  }

  public void setEnrolledCount(Long enrolledCount) {
    this.enrolled = enrolledCount;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getEnrollmentPercentage() {
    return enrollmentPercentage;
  }

  public void setEnrollmentPercentage(Double enrollmentPercentage) {
    this.enrollmentPercentage = enrollmentPercentage;
  }

  public Integer getStudyPermission() {
    return studyPermission;
  }

  public void setStudyPermission(Integer studyPermission) {
    this.studyPermission = studyPermission;
  }
}
