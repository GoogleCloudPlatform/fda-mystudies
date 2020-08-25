/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import com.google.cloud.healthcare.fdamystudies.common.Permission;
import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sites_permissions")
@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class SitePermissionEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinColumn(name = "ur_admin_user_id")
  @Index(name = "sites_permissions_ur_admin_user_idx")
  private UserRegAdminEntity urAdminUser;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinColumn(name = "study_id")
  @Index(name = "sites_permissions_study_idx")
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinColumn(name = "site_id")
  @Index(name = "sites_permissions_site_idx")
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @JoinColumn(name = "app_info_id")
  @Index(name = "sites_permissions_app_info_idx")
  private AppEntity app;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "edit")
  private Permission canEdit;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;
}
