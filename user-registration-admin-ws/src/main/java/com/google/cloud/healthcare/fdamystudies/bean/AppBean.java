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

public class AppBean {
  private Integer id = 0;
  private String customId = "";
  private String name = "";
  private Long invitedCount = 0L;
  private Long enrolledCount = 0L;
  private Double enrollmentPercentage = 0d;
  private Long totalStudiesCount = 0L;
  private Long appUsersCount = 0L;
  private Integer appPermission = 0;

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

  public Long getInvitedCount() {
    return invitedCount;
  }

  public void setInvitedCount(Long invitedCount) {
    this.invitedCount = invitedCount;
  }

  public Long getEnrolledCount() {
    return enrolledCount;
  }

  public void setEnrolledCount(Long enrolledCount) {
    this.enrolledCount = enrolledCount;
  }

  public Double getEnrollmentPercentage() {
    return enrollmentPercentage;
  }

  public void setEnrollmentPercentage(Double enrollmentPercentage) {
    this.enrollmentPercentage = enrollmentPercentage;
  }

  public Long getTotalStudiesCount() {
    return totalStudiesCount;
  }

  public void setTotalStudiesCount(Long totalStudiesCount) {
    this.totalStudiesCount = totalStudiesCount;
  }

  public Long getAppUsersCount() {
    return appUsersCount;
  }

  public void setAppUsersCount(Long appUsersCount) {
    this.appUsersCount = appUsersCount;
  }

  public Integer getAppPermission() {
    return appPermission;
  }

  public void setAppPermission(Integer appPermission) {
    this.appPermission = appPermission;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
