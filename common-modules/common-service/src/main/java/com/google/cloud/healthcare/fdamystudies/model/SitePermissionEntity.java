/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "ur_admin_user_id", insertable = true, updatable = true)
  private UserRegAdminEntity urAdminUser;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "study_id", insertable = true, updatable = true)
  private StudyEntity study;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "site_id")
  private SiteEntity site;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "app_info_id")
  private AppEntity appInfo;

  @Column(name = "edit", length = 100)
  private Integer canEdit;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @Column(name = "created_by", length = 64)
  private String createdBy;
}
