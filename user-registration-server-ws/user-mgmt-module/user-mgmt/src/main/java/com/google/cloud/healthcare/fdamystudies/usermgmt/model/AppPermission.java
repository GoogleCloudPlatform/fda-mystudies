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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Index;

@ToString
@Setter
@Getter
@Entity
@Table(name = "app_permissions")
public class AppPermission implements Serializable {

  private static final long serialVersionUID = 8610289975376774137L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "ur_admin_user_id", insertable = true, updatable = true)
  @Index(name = "app_permissions_ur_admin_user_idx")
  private UserRegAdminUser urAdminUser;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id", insertable = true, updatable = true)
  @Index(name = "app_permissions_app_info_idx")
  private AppInfoDetailsBO appInfo;

  @Column(name = "edit", columnDefinition = "TINYINT(1) default 0")
  private Integer edit = 0;

  @Column(name = "created", columnDefinition = "TIMESTAMP")
  private Date created;

  @Column(name = "created_by", columnDefinition = "INT(20) default 0")
  private Integer createdBy;
}
