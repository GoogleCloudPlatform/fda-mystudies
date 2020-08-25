/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.usermgmt.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Entity
@Table(name = "study_permissions")
public class StudyPermission implements Serializable {

  private static final long serialVersionUID = -9223143734827095684L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  @Index(name = "study_permissions_app_info_idx")
  private AppInfoDetailsBO appInfo;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_id", insertable = true, updatable = true)
  @Index(name = "study_permissions_study_idx")
  private StudyInfoBO studyInfo;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "ur_admin_user_id", insertable = true, updatable = true)
  @Index(name = "study_permissions_ur_admin_user_idx")
  private UserRegAdminUser urAdminUser;

  @Column(name = "edit", columnDefinition = "TINYINT(1) default 0")
  private Integer edit = 0;

  @Column(name = "created")
  private Date created;

  @Column(name = "created_by")
  private Integer createdBy;
}
