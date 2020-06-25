/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
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
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "sites_permissions")
public class SitePermission {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "ur_admin_user_id", insertable = false, updatable = false)
  private UserRegAdminUser urAdminUser;

  @ManyToOne
  @JoinColumn(name = "study_id", insertable = true, updatable = true)
  private StudyInfoBO studyInfo;

  @ManyToOne
  @JoinColumn(name = "site_id", insertable = true, updatable = true)
  private SiteBo siteBo;

  @ManyToOne
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  private AppInfoDetailsBO appInfo;

  @Column(name = "edit", columnDefinition = "TINYINT(1) default 0")
  private Integer edit = 0;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private LocalDateTime created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy = 0;

  public SitePermission() {
    super();
  }

  public SitePermission(
      UserRegAdminUser urAdminUser,
      StudyInfoBO studyInfo,
      SiteBo siteBo,
      AppInfoDetailsBO appInfo,
      Integer edit,
      Integer createdBy) {
    super();
    this.urAdminUser = urAdminUser;
    this.studyInfo = studyInfo;
    this.siteBo = siteBo;
    this.appInfo = appInfo;
    this.edit = edit;
    this.createdBy = createdBy;
  }
}
