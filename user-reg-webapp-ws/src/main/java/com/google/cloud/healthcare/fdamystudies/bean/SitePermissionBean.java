package com.google.cloud.healthcare.fdamystudies.bean;

import java.time.LocalDateTime;

public class SitePermissionBean {
  private Integer id;
  private Integer edit;
  private LocalDateTime created;
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getEdit() {
    return edit;
  }

  public void setEdit(Integer edit) {
    this.edit = edit;
  }

  public LocalDateTime getCreated() {
    return created;
  }

  public void setCreated(LocalDateTime created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }
}
