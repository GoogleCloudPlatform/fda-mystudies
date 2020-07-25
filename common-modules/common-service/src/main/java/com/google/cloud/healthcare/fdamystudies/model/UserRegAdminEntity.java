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
import java.util.Optional;
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
import org.springframework.data.annotation.Transient;
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
  @Column(name = "ur_admin_user_id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "email", length = 320)
  private String email;

  @ToString.Exclude
  @Column(name = "ur_admin_auth_id", length = 255)
  private String urAdminAuthId;

  @ToString.Exclude
  @Column(name = "first_name", length = 100)
  private String firstName;

  @ToString.Exclude
  @Column(name = "last_name", length = 100)
  private String lastName;

  @ToString.Exclude
  @Column(name = "phone_number", length = 20)
  private String phoneNumber;

  @Column(name = "email_changed")
  private Boolean emailChanged;

  @Column(name = "status", length = 1)
  private Integer status;

  @Column(name = "super_admin", length = 1)
  private boolean superAdmin;

  @Column(name = "edit_permission", length = 1)
  private Integer editPermission;

  @Column(
      name = "created",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "created_by", length = 64)
  private String createdBy;

  @Column(name = "security_code_expire_date", columnDefinition = "TIMESTAMP")
  private Timestamp securityCodeExpireDate;

  @ToString.Exclude
  @Column(name = "security_code", length = 64)
  private String securityCode;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "urAdminUser")
  private List<AppPermissionEntity> appPermissions = new ArrayList<>();

  public void addAppPermissionEntity(AppPermissionEntity appPermission) {
    appPermissions.add(appPermission);
    appPermission.setUrAdminUser(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "urAdminUser")
  private List<StudyPermissionEntity> studyPermissions = new ArrayList<>();

  public void addStudyPermissionEntity(StudyPermissionEntity studyPermission) {
    studyPermissions.add(studyPermission);
    studyPermission.setUrAdminUser(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "urAdminUser")
  private List<SitePermissionEntity> sitePermissions = new ArrayList<>();

  public void addSitePermissionEntity(SitePermissionEntity sitePermission) {
    sitePermissions.add(sitePermission);
    sitePermission.setUrAdminUser(this);
  }

  @Transient
  public boolean isActive() {
    return Optional.ofNullable(status).orElse(0) == 1;
  }
}
