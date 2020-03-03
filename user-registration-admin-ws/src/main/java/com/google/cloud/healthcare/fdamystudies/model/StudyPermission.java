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

import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "study_permissions")
public class StudyPermission {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = false, updatable = false)
  private AppInfoDetailsBO appInfo;

  @ManyToOne
  @JoinColumn(name = "study_id", insertable = false, updatable = false)
  private StudyInfoBO studyInfo;

  @ManyToOne
  @JoinColumn(name = "ur_admin_user_id", insertable = false, updatable = false)
  private UserRegAdminUser urAdminUser;

  @Column(name = "edit")
  private Integer edit;

  @Column(name = "created")
  private Date created;

  @Column(name = "created_by")
  private Integer createdBy;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public AppInfoDetailsBO getAppInfo() {
    return appInfo;
  }

  public void setAppInfo(AppInfoDetailsBO appInfo) {
    this.appInfo = appInfo;
  }

  public StudyInfoBO getStudyInfo() {
    return studyInfo;
  }

  public void setStudyInfo(StudyInfoBO studyInfo) {
    this.studyInfo = studyInfo;
  }

  public UserRegAdminUser getUrAdminUser() {
    return urAdminUser;
  }

  public void setUrAdminUser(UserRegAdminUser urAdminUser) {
    this.urAdminUser = urAdminUser;
  }

  public Integer getEdit() {
    return edit;
  }

  public void setEdit(Integer edit) {
    this.edit = edit;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public Integer getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(Integer createdBy) {
    this.createdBy = createdBy;
  }
}
