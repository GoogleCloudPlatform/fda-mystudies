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

public class SiteBean {
  private Integer id;
  private String name;
  private Long invited = 0L;
  private Long enrolled = 0L;
  private Double enrollmentPercentage = 0d;
  private Integer edit = 0;
  private Integer status;

  public SiteBean() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getInvitedCount() {
    return invited;
  }

  public void setInvitedCount(Long long1) {
    this.invited = long1;
  }

  public Long getEnrolledCount() {
    return enrolled;
  }

  public void setEnrolledCount(Long enrolledCount) {
    this.enrolled = enrolledCount;
  }

  public Integer getEdit() {
    return edit;
  }

  public void setEdit(Integer edit) {
    this.edit = edit;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public Double getEnrollmentPercentage() {
    return enrollmentPercentage;
  }

  public void setEnrollmentPercentage(Double enrollmentPercentage) {
    this.enrollmentPercentage = enrollmentPercentage;
  }
}
