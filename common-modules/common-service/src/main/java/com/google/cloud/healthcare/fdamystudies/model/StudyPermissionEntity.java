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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@ToString
@Entity
@Table(
    name = "study_permissions",
    indexes = {
      @Index(name = "ur_admin_user_id_index", columnList = "ur_admin_user_id"),
      @Index(name = "app_info_id_index", columnList = "app_info_id"),
      @Index(name = "study_id_index", columnList = "study_id")
    })
public class StudyPermissionEntity implements Serializable {

  private static final long serialVersionUID = -9223143734827095684L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id")
  private AppEntity app;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_id")
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "ur_admin_user_id")
  private UserRegAdminEntity urAdminUser;

  @Enumerated(EnumType.ORDINAL)
  private Permission edit;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;
}
