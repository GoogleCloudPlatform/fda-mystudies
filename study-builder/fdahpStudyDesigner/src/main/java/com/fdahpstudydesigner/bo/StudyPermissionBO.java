/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "study_permission")
@NamedQueries({
  @NamedQuery(
      name = "getStudyPermissionById",
      query = " from StudyPermissionBO where studyId=:studyId and userId=:userId"),
})
public class StudyPermissionBO implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "delFlag")
  private Integer delFlag;

  @Column(name = "project_lead")
  private Integer projectLead;

  @Column(name = "study_id")
  private String studyId;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String studyPermissionId;

  @Transient private String userFullName;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "view_permission", length = 1)
  private boolean viewPermission;

  public Integer getDelFlag() {
    return delFlag;
  }

  public Integer getProjectLead() {
    return projectLead;
  }

  public String getStudyId() {
    return studyId;
  }

  public String getStudyPermissionId() {
    return studyPermissionId;
  }

  public String getUserFullName() {
    return userFullName;
  }

  public String getUserId() {
    return userId;
  }

  public boolean isViewPermission() {
    return viewPermission;
  }

  public void setDelFlag(Integer delFlag) {
    this.delFlag = delFlag;
  }

  public void setProjectLead(Integer projectLead) {
    this.projectLead = projectLead;
  }

  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }

  public void setStudyPermissionId(String studyPermissionId) {
    this.studyPermissionId = studyPermissionId;
  }

  public void setUserFullName(String userFullName) {
    this.userFullName = userFullName;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setViewPermission(boolean viewPermission) {
    this.viewPermission = viewPermission;
  }
}
