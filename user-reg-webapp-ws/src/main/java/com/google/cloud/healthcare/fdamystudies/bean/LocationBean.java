/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.LinkedList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class LocationBean {

  private Integer id;

  private String customId = "";

  private String name = "";

  private String description = "";

  private String status = "";

  private Integer studiesCount = 0;

  private List<String> studies = new LinkedList<>();

  private ErrorBean errorBean;

  private SuccessBean successBean;

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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getStudiesCount() {
    return studiesCount;
  }

  public void setStudiesCount(Integer studiesCount) {
    this.studiesCount = studiesCount;
  }

  public List<String> getStudies() {
    return studies;
  }

  public void setStudies(List<String> studies) {
    this.studies = studies;
  }

  public ErrorBean getErrorBean() {
    return errorBean;
  }

  public void setErrorBean(ErrorBean errorBean) {
    this.errorBean = errorBean;
  }

  public SuccessBean getSuccessBean() {
    return successBean;
  }

  public void setSuccessBean(SuccessBean successBean) {
    this.successBean = successBean;
  }

  public LocationBean(String customId, String name, String description) {
    super();
    this.customId = customId;
    this.name = name;
    this.description = description;
  }

  public LocationBean(String customId, String status) {
    super();
    this.customId = customId;
    this.status = status;
  }
}
