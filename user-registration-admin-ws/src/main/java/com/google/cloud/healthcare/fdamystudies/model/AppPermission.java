/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.ToString;

@ToString
@Entity
@Table(name = "app_permissions")
public class AppPermission {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "ur_admin_user_id", insertable = false, updatable = false)
  private UserRegAdminUser urAdminUser;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = false, updatable = false)
  private AppInfoDetailsBO appInfo;

  @Column(name = "edit", columnDefinition = "TINYINT(1)")
  private Integer edit;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UserRegAdminUser getUrAdminUser() {
    return urAdminUser;
  }

  public void setUrAdminUser(UserRegAdminUser urAdminUser) {
    this.urAdminUser = urAdminUser;
  }

  public AppInfoDetailsBO getAppInfo() {
    return appInfo;
  }

  public void setAppInfo(AppInfoDetailsBO appInfo) {
    this.appInfo = appInfo;
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
