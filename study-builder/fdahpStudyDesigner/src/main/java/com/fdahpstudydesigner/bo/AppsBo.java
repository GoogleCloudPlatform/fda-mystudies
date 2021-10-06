/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "apps")
@NamedQueries({
  @NamedQuery(name = "AppsBo.getAppsById", query = " From AppsBo ABO WHERE ABO.id =:id"),
  @NamedQuery(
      name = "getApps",
      query =
          " From AppsBo WHERE appStatus IN ('Active','Deactivated') and live=0 order by createdOn"),
  @NamedQuery(
      name = "getAppByLatestVersion",
      query = " From AppsBo WHERE customAppId =:customAppId order by version DESC"),
  @NamedQuery(
      name = "updateAppVersion",
      query = "UPDATE AppsBo SET live=2 WHERE customAppId =:customAppId and live=1"),
  @NamedQuery(
      name = "AppsBo.getAppByCustomAppId",
      query = " From AppsBo WHERE customAppId =:customAppId"),
  @NamedQuery(
      name = "AppsBo.getAppByAppId",
      query = " From AppsBo WHERE customAppId =:customAppId and live=0")
})
public class AppsBo implements Serializable {

  private static final long serialVersionUID = 2147840266295837728L;

  @Transient private String buttonText;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_on")
  private String createdOn;

  @Column(name = "custom_app_id")
  private String customAppId;

  @Column(name = "name")
  private String name;

  @Column(name = "type")
  private String type;

  @Column(name = "app_platform")
  private String appPlatform;

  @Column(name = "from_email_address")
  private String fromEmailAddress;

  @Column(name = "feedback_email_address")
  private String feedbackEmailAddress;

  @Column(name = "contact_us_address")
  private String contactEmailAddress;

  @Column(name = "app_support_email_address")
  private String appSupportEmailAddress;

  @Column(name = "app_privacy_url")
  private String appPrivacyUrl;

  @Column(name = "app_terms_url")
  private String appTermsUrl;

  @Column(name = "app_website_url")
  private String appWebsiteUrl;

  @Column(name = "organization_name")
  private String organizationName;

  @Column(name = "app_store_url")
  private String appStoreUrl;

  @Column(name = "play_store_url")
  private String playStoreUrl;

  @ToString.Exclude
  @Column(name = "ios_bundle_id")
  private String iosBundleId;

  @ToString.Exclude
  @Column(name = "android_bundle_id")
  private String androidBundleId;

  @ToString.Exclude
  @Column(name = "android_server_key")
  private String androidServerKey;

  @ToString.Exclude
  @Column(name = "ios_server_key")
  private String iosServerKey;

  @Column(name = "ios_latest_xcode_app_version")
  private String iosXCodeAppVersion;

  @Column(name = "ios_latest_app_build_version")
  private String iosAppBuildVersion;

  @Transient private Integer iosForceUpgrade;

  @Transient private String androidAppBuildVersion;

  @Transient private Integer androidForceUpgrade;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "app_launched_date")
  private String appLaunchDate;

  @Column(name = "has_app_draft")
  private Integer hasAppDraft = 0;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "apps_status")
  private String appStatus;

  @Column(name = "ios_app_distributed")
  private Boolean iosAppDistributed;

  @Column(name = "is_app_published")
  private Boolean isAppPublished;

  @Column(name = "android_app_distributed")
  private Boolean androidAppDistributed;

  @Column(name = "version")
  private Float version = 0f;

  @Transient AppSequenceBo appSequenceBo = new AppSequenceBo();

  @Transient VersionInfoBO versionInfoBO = new VersionInfoBO();

  @Transient private AppsBo liveAppsBo = null;

  @Transient private String userId;

  @Transient private boolean viewPermission = true;

  public String getButtonText() {
    return buttonText;
  }

  public void setButtonText(String buttonText) {
    this.buttonText = buttonText;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getCustomAppId() {
    return customAppId;
  }

  public void setCustomAppId(String customAppId) {
    this.customAppId = customAppId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getAppPlatform() {
    return appPlatform;
  }

  public void setAppPlatform(String appPlatform) {
    this.appPlatform = appPlatform;
  }

  public String getFromEmailAddress() {
    return fromEmailAddress;
  }

  public void setFromEmailAddress(String fromEmailAddress) {
    this.fromEmailAddress = fromEmailAddress;
  }

  public String getFeedbackEmailAddress() {
    return feedbackEmailAddress;
  }

  public void setFeedbackEmailAddress(String feedbackEmailAddress) {
    this.feedbackEmailAddress = feedbackEmailAddress;
  }

  public String getContactEmailAddress() {
    return contactEmailAddress;
  }

  public void setContactEmailAddress(String contactEmailAddress) {
    this.contactEmailAddress = contactEmailAddress;
  }

  public String getAppSupportEmailAddress() {
    return appSupportEmailAddress;
  }

  public void setAppSupportEmailAddress(String appSupportEmailAddress) {
    this.appSupportEmailAddress = appSupportEmailAddress;
  }

  public String getAppPrivacyUrl() {
    return appPrivacyUrl;
  }

  public void setAppPrivacyUrl(String appPrivacyUrl) {
    this.appPrivacyUrl = appPrivacyUrl;
  }

  public String getAppTermsUrl() {
    return appTermsUrl;
  }

  public void setAppTermsUrl(String appTermsUrl) {
    this.appTermsUrl = appTermsUrl;
  }

  public String getOrganizationName() {
    return organizationName;
  }

  public void setOrganizationName(String organizationName) {
    this.organizationName = organizationName;
  }

  public String getAppStoreUrl() {
    return appStoreUrl;
  }

  public void setAppStoreUrl(String appStoreUrl) {
    this.appStoreUrl = appStoreUrl;
  }

  public String getPlayStoreUrl() {
    return playStoreUrl;
  }

  public void setPlayStoreUrl(String playStoreUrl) {
    this.playStoreUrl = playStoreUrl;
  }

  public String getIosBundleId() {
    return iosBundleId;
  }

  public void setIosBundleId(String iosBundleId) {
    this.iosBundleId = iosBundleId;
  }

  public String getAndroidBundleId() {
    return androidBundleId;
  }

  public void setAndroidBundleId(String androidBundleId) {
    this.androidBundleId = androidBundleId;
  }

  public String getAndroidServerKey() {
    return androidServerKey;
  }

  public void setAndroidServerKey(String androidServerKey) {
    this.androidServerKey = androidServerKey;
  }

  public String getIosServerKey() {
    return iosServerKey;
  }

  public void setIosServerKey(String iosServerKey) {
    this.iosServerKey = iosServerKey;
  }

  public String getIosXCodeAppVersion() {
    return iosXCodeAppVersion;
  }

  public void setIosXCodeAppVersion(String iosXCodeAppVersion) {
    this.iosXCodeAppVersion = iosXCodeAppVersion;
  }

  public String getIosAppBuildVersion() {
    return iosAppBuildVersion;
  }

  public void setIosAppBuildVersion(String iosAppBuildVersion) {
    this.iosAppBuildVersion = iosAppBuildVersion;
  }

  public Integer getIosForceUpgrade() {
    return iosForceUpgrade;
  }

  public void setIosForceUpgrade(Integer iosForceUpgrade) {
    this.iosForceUpgrade = iosForceUpgrade;
  }

  public String getAndroidAppBuildVersion() {
    return androidAppBuildVersion;
  }

  public void setAndroidAppBuildVersion(String androidAppBuildVersion) {
    this.androidAppBuildVersion = androidAppBuildVersion;
  }

  public Integer getAndroidForceUpgrade() {
    return androidForceUpgrade;
  }

  public void setAndroidForceUpgrade(Integer androidForceUpgrade) {
    this.androidForceUpgrade = androidForceUpgrade;
  }

  public Integer getLive() {
    return live;
  }

  public void setLive(Integer live) {
    this.live = live;
  }

  public String getAppLaunchDate() {
    return appLaunchDate;
  }

  public void setAppLaunchDate(String appLaunchDate) {
    this.appLaunchDate = appLaunchDate;
  }

  public Integer getHasAppDraft() {
    return hasAppDraft;
  }

  public void setHasAppDraft(Integer hasAppDraft) {
    this.hasAppDraft = hasAppDraft;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public String getAppStatus() {
    return appStatus;
  }

  public void setAppStatus(String appStatus) {
    this.appStatus = appStatus;
  }

  public Boolean getIosAppDistributed() {
    return iosAppDistributed;
  }

  public void setIosAppDistributed(Boolean iosAppDistributed) {
    this.iosAppDistributed = iosAppDistributed;
  }

  public Boolean getIsAppPublished() {
    return isAppPublished;
  }

  public void setIsAppPublished(Boolean isAppPublished) {
    this.isAppPublished = isAppPublished;
  }

  public Boolean getAndroidAppDistributed() {
    return androidAppDistributed;
  }

  public void setAndroidAppDistributed(Boolean androidAppDistributed) {
    this.androidAppDistributed = androidAppDistributed;
  }

  public Float getVersion() {
    return version;
  }

  public void setVersion(Float version) {
    this.version = version;
  }

  public AppSequenceBo getAppSequenceBo() {
    return appSequenceBo;
  }

  public void setAppSequenceBo(AppSequenceBo appSequenceBo) {
    this.appSequenceBo = appSequenceBo;
  }

  public AppsBo getLiveAppsBo() {
    return liveAppsBo;
  }

  public void setLiveAppsBo(AppsBo liveAppsBo) {
    this.liveAppsBo = liveAppsBo;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getAppWebsiteUrl() {
    return appWebsiteUrl;
  }

  public void setAppWebsiteUrl(String appWebsiteUrl) {
    this.appWebsiteUrl = appWebsiteUrl;
  }

  public boolean isViewPermission() {
    return viewPermission;
  }

  public void setViewPermission(boolean viewPermission) {
    this.viewPermission = viewPermission;
  }

  public VersionInfoBO getVersionInfoBO() {
    return versionInfoBO;
  }

  public void setVersionInfoBO(VersionInfoBO versionInfoBO) {
    this.versionInfoBO = versionInfoBO;
  }
}
