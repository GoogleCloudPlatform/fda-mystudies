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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@Table(name = "app_info")
@ConditionalOnProperty(
    value = "participant.manager.entities.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class AppEntity implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(
      name = "app_info_id",
      updatable = false,
      nullable = false,
      length = ColumnConstraints.ID_LENGTH)
  private String id;

  @ToString.Exclude
  @Column(
      name = "custom_app_id",
      nullable = false,
      unique = true,
      length = ColumnConstraints.XS_LENGTH)
  private String appId;

  @ToString.Exclude
  @Column(name = "app_name", length = ColumnConstraints.SMALL_LENGTH)
  private String appName;

  @Column(name = "app_description")
  @Type(type = "text")
  private String appDescription;

  @ToString.Exclude
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "org_info_id")
  private OrgInfoEntity orgInfo;

  @ToString.Exclude
  @Column(name = "ios_bundle_id", length = ColumnConstraints.SMALL_LENGTH)
  private String iosBundleId;

  @ToString.Exclude
  @Column(name = "android_bundle_id", length = ColumnConstraints.SMALL_LENGTH)
  private String androidBundleId;

  @ToString.Exclude
  @Column(name = "ios_certificate")
  @Type(type = "text")
  private String iosCertificate;

  @ToString.Exclude
  @Column(name = "ios_certificate_password", length = ColumnConstraints.SMALL_LENGTH)
  private String iosCertificatePassword;

  @ToString.Exclude
  @Column(name = "android_server_key", length = ColumnConstraints.LARGE_LENGTH)
  private String androidServerKey;

  @ToString.Exclude
  @Column(name = "from_email_id", length = ColumnConstraints.LARGE_LENGTH)
  private String formEmailId;

  @ToString.Exclude
  @Column(name = "from_email_password", length = ColumnConstraints.LARGE_LENGTH)
  private String fromEmailPassword;

  @ToString.Exclude
  @Column(name = "reg_email_sub", length = ColumnConstraints.LARGE_LENGTH)
  private String regEmailSub;

  @ToString.Exclude
  @Column(name = "reg_email_body")
  @Type(type = "text")
  private String regEmailBody;

  @ToString.Exclude
  @Column(name = "forgot_email_sub", length = ColumnConstraints.LARGE_LENGTH)
  private String forgotEmailSub;

  @Column(name = "forgot_email_body")
  @Type(type = "text")
  private String forgotEmailBody;

  @Column(name = "method_handler", length = ColumnConstraints.TINY_LENGTH)
  private Integer methodHandler;

  @Column(name = "created_by", length = ColumnConstraints.LARGE_LENGTH)
  private String createdBy;

  @Column(name = "modified_by", length = ColumnConstraints.LARGE_LENGTH)
  private String modifiedBy;

  @Column(
      name = "modified_date",
      insertable = false,
      updatable = true,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp modified;

  @Column(
      name = "created_on",
      insertable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private Timestamp created;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "appInfo",
      orphanRemoval = true)
  private List<AppPermissionEntity> appPermissions = new ArrayList<>();

  public void addAppPermissionEntity(AppPermissionEntity appPermission) {
    appPermissions.add(appPermission);
    appPermission.setAppInfo(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "appInfo",
      orphanRemoval = true)
  private List<SitePermissionEntity> sitePermissions = new ArrayList<>();

  public void addSitePermissionEntity(SitePermissionEntity sitePermission) {
    sitePermissions.add(sitePermission);
    sitePermission.setAppInfo(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "appInfo",
      orphanRemoval = true)
  private List<StudyPermissionEntity> studyPermissions = new ArrayList<>();

  public void addStudyPermissionEntity(StudyPermissionEntity studyPermission) {
    studyPermissions.add(studyPermission);
    studyPermission.setAppInfo(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "appInfo")
  private List<UserDetailsEntity> userDetails = new ArrayList<>();

  public void addUserDetailsEntity(UserDetailsEntity userDetail) {
    userDetails.add(userDetail);
    userDetail.setAppInfo(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "appInfo")
  private List<StudyEntity> studies = new ArrayList<>();

  public void addStudyEntity(StudyEntity study) {
    studies.add(study);
    study.setAppInfo(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "appInfo")
  private List<AuthInfoEntity> apps = new ArrayList<>();

  public void addAuthInfoEntity(AuthInfoEntity authInfoEntity) {
    apps.add(authInfoEntity);
    authInfoEntity.setAppInfo(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "appInfo")
  private List<UserAppDetailsEntity> userAppDetails = new ArrayList<>();

  public void addUserAppDeatailsEntity(UserAppDetailsEntity userAppDetailsEntity) {
    userAppDetails.add(userAppDetailsEntity);
    userAppDetailsEntity.setAppInfo(this);
  }
}
