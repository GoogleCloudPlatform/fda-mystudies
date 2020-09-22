/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

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
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Transient;

@ToString
@Getter
@Setter
@Entity
@Table(
    name = "ur_admin_user",
    indexes = {
      @Index(name = "email_index", columnList = "email"),
      @Index(name = "ur_admin_auth_id_index", columnList = "ur_admin_auth_id"),
      @Index(name = "security_code_index", columnList = "security_code")
    })
public class UserRegAdminEntity implements Serializable {

  private static final long serialVersionUID = 8686769972691178223L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(nullable = false, unique = true, length = EMAIL_LENGTH)
  private String email;

  @ToString.Exclude
  @Column(name = "ur_admin_auth_id", unique = true, length = LARGE_LENGTH)
  private String urAdminAuthId;

  @ToString.Exclude
  @Column(name = "first_name", length = MEDIUM_LENGTH)
  private String firstName;

  @ToString.Exclude
  @Column(name = "last_name", length = MEDIUM_LENGTH)
  private String lastName;

  @ToString.Exclude
  @Column(name = "phone_number", length = XS_LENGTH)
  private String phoneNumber;

  @Column(name = "email_changed")
  private Boolean emailChanged;

  private Integer status;

  @Column(name = "super_admin")
  private boolean superAdmin;

  @Column(name = "location_permission")
  private Integer locationPermission;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Column(name = "security_code_expire_date", columnDefinition = "TIMESTAMP")
  private Timestamp securityCodeExpireDate;

  @ToString.Exclude
  @Column(name = "security_code", length = SMALL_LENGTH)
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
