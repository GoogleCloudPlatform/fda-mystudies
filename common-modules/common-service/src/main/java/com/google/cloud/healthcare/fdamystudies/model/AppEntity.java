/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.APP_STUDY_ID_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

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
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@Entity
@Table(name = "app_info")
public class AppEntity implements Serializable {

  private static final long serialVersionUID = 4985607753888575491L;

  @ToString.Exclude
  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @ToString.Exclude
  @Column(name = "custom_app_id", nullable = false, unique = true, length = APP_STUDY_ID_LENGTH)
  private String appId;

  @ToString.Exclude
  @NotNull
  @Column(name = "app_name", length = SMALL_LENGTH)
  private String appName;

  @Column(name = "app_description")
  @Type(type = "text")
  private String appDescription;

  @ToString.Exclude
  @Column(name = "ios_bundle_id", length = SMALL_LENGTH)
  private String iosBundleId;

  @ToString.Exclude
  @Column(name = "android_bundle_id", length = SMALL_LENGTH)
  private String androidBundleId;

  @ToString.Exclude
  @Column(name = "ios_authorization_token")
  @Type(type = "text")
  private String iosAuthorizationToken;

  @ToString.Exclude
  @Column(name = "ios_key_id", length = SMALL_LENGTH)
  private String iosKeyId;

  @ToString.Exclude
  @Column(name = "ios_team_id", length = SMALL_LENGTH)
  private String iosTeamId;

  @ToString.Exclude
  @Column(name = "android_server_key", length = LARGE_LENGTH)
  private String androidServerKey;

  @ToString.Exclude
  @Column(name = "ios_server_key", length = LARGE_LENGTH)
  private String iosServerKey;

  @ToString.Exclude
  @Column(name = "from_email_id", length = LARGE_LENGTH)
  private String formEmailId;

  @ToString.Exclude
  @Column(name = "from_email_password", length = LARGE_LENGTH)
  private String fromEmailPassword;

  @ToString.Exclude
  @Column(name = "reg_email_sub", length = LARGE_LENGTH)
  private String regEmailSub;

  @ToString.Exclude
  @Column(name = "reg_email_body")
  @Type(type = "text")
  private String regEmailBody;

  @ToString.Exclude
  @Column(name = "forgot_email_sub", length = LARGE_LENGTH)
  private String forgotEmailSub;

  @Column(name = "forgot_email_body")
  @Type(type = "text")
  private String forgotEmailBody;

  @Column(name = "method_handler")
  private Integer methodHandler;

  @Column(name = "created_by", length = LARGE_LENGTH)
  private String createdBy;

  @Column(name = "modified_by", length = LARGE_LENGTH)
  private String modifiedBy;

  @Column(name = "updated_time")
  @UpdateTimestamp
  private Timestamp modified;

  @Column(name = "created_time")
  @CreationTimestamp
  private Timestamp created;

  @ToString.Exclude
  @Column(name = "contact_us_to_email", length = EMAIL_LENGTH)
  private String contactUsToEmail;

  @ToString.Exclude
  @Column(name = "feedback_to_email", length = EMAIL_LENGTH)
  private String feedBackToEmail;

  @ToString.Exclude
  @Column(name = "app_support_email_address", length = EMAIL_LENGTH)
  private String appSupportEmailAddress;

  @Column(name = "app_platform")
  private String appPlatform;

  @Column(name = "app_store_url")
  private String appStoreUrl;

  @Column(name = "app_privacy_url")
  private String appPrivacyUrl;

  @Column(name = "play_store_url")
  private String playStoreUrl;

  @Column(name = "app_terms_url")
  private String appTermsUrl;

  @Column(name = "organization_name")
  private String organizationName;

  @Column(name = "ios_latest_xcode_app_version")
  private String iosXCodeAppVersion;

  @Column(name = "ios_latest_app_build_version")
  private String iosAppBuildVersion;

  @Column(name = "ios_force_upgrade")
  private Integer iosForceUpgrade;

  @Column(name = "android_latest_app_version")
  private String androidAppBuildVersion;

  @Column(name = "android_force_upgrade")
  private Integer androidForceUpdrade;

  @Column(name = "type")
  private String type;

  @Column(name = "app_website")
  private String appWebsite;

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "app",
      orphanRemoval = true)
  private List<AppPermissionEntity> appPermissions = new ArrayList<>();

  public void addAppPermissionEntity(AppPermissionEntity appPermission) {
    appPermissions.add(appPermission);
    appPermission.setApp(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "app",
      orphanRemoval = true)
  private List<SitePermissionEntity> sitePermissions = new ArrayList<>();

  public void addSitePermissionEntity(SitePermissionEntity sitePermission) {
    sitePermissions.add(sitePermission);
    sitePermission.setApp(this);
  }

  @OneToMany(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      mappedBy = "app",
      orphanRemoval = true)
  private List<StudyPermissionEntity> studyPermissions = new ArrayList<>();

  public void addStudyPermissionEntity(StudyPermissionEntity studyPermission) {
    studyPermissions.add(studyPermission);
    studyPermission.setApp(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
  private List<UserDetailsEntity> userDetails = new ArrayList<>();

  public void addUserDetailsEntity(UserDetailsEntity userDetail) {
    userDetails.add(userDetail);
    userDetail.setApp(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
  private List<StudyEntity> studies = new ArrayList<>();

  public void addStudyEntity(StudyEntity study) {
    studies.add(study);
    study.setApp(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
  private List<AuthInfoEntity> apps = new ArrayList<>();

  public void addAuthInfoEntity(AuthInfoEntity authInfoEntity) {
    apps.add(authInfoEntity);
    authInfoEntity.setApp(this);
  }

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "app")
  private List<UserAppDetailsEntity> userAppDetails = new ArrayList<>();

  public void addUserAppDeatailsEntity(UserAppDetailsEntity userAppDetailsEntity) {
    userAppDetails.add(userAppDetailsEntity);
    userAppDetailsEntity.setApp(this);
  }
}
