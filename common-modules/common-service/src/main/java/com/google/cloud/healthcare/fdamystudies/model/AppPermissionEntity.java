/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;

import com.google.cloud.healthcare.fdamystudies.common.Permission;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@ToString
@Setter
@Getter
@Entity
@Table(
    name = "app_permissions",
    indexes = {
      @Index(name = "ur_admin_user_id_index", columnList = "ur_admin_user_id"),
      @Index(name = "app_info_id_index", columnList = "app_info_id")
    })
public class AppPermissionEntity implements Serializable {

  private static final long serialVersionUID = 8610289975376774137L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "ur_admin_user_id")
  private UserRegAdminEntity urAdminUser;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id")
  private AppEntity app;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "edit")
  private Permission edit;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Transient
  public String getAppId() {
    return app == null ? StringUtils.EMPTY : app.getId();
  }
}
