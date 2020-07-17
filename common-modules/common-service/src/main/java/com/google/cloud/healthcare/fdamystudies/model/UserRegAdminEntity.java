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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Entity
@Table(name = "ur_admin_user")
@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class UserRegAdminEntity implements Serializable {

  private static final long serialVersionUID = 8686769972691178223L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "email")
  private String email;

  @Column(name = "ur_admin_auth_id")
  private String urAdminAuthId;

  @ToString.Exclude
  @Column(name = "first_name")
  private String firstName;

  @ToString.Exclude
  @Column(name = "last_name")
  private String lastName;

  @ToString.Exclude
  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "email_changed")
  private Boolean emailChanged;

  @ToString.Exclude
  @Column(name = "status")
  private Integer status;

  @ToString.Exclude
  @Column(name = "super_admin")
  private Boolean superAdmin;

  @Column(name = "manage_locations")
  private Integer manageLocations;

  @Column(
      name = "created_time",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp createdTime;

  @Column(name = "created_by")
  private String createdBy;

  @Column(
      name = "security_code_expire_date",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp securityCodeExpireDate;

  @Column(name = "security_code")
  private String securityCode;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "urAdminUser")
  private List<SitePermissionEntity> sitePermissions = new ArrayList<>();

  public void addSitePermissionEntity(SitePermissionEntity sitePermission) {
    sitePermissions.add(sitePermission);
    sitePermission.setUrAdminUser(this);
  }
}
