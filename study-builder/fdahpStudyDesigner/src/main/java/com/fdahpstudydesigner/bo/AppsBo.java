/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Setter
@Getter
@Entity
@Table(name = "apps")
@NamedQueries({
  @NamedQuery(
      name = "AppsBo.getAppsById",
      query = " From AppsBo ABO WHERE ABO.id =:id order by version DESC LIMIT 1"),
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

  @Column(name = "ios_force_upgrade")
  private Integer iosForceUpgrade;

  @Column(name = "android_latest_app_version")
  private String androidAppBuildVersion;

  @Column(name = "android_force_upgrade")
  private Integer androidForceUpdrade;

  @Column(name = "is_live")
  private Integer live = 0;

  @Column(name = "app_launched_date")
  private String appLaunchDate;

  @Column(name = "has_app_draft")
  private Integer hasAppDraft = 0;

  @Column(name = "has_app_settings_draft")
  private Integer hasAppSettingDraft = 0;

  @Column(name = "has_app_properties_draft")
  private Integer hasAppPropertiesDraft = 0;

  @Column(name = "has_app_developer_config_draft")
  private Integer hasAppDevConfigDraft = 0;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_on")
  private String modifiedOn;

  @Column(name = "apps_status")
  private String appsStatus;

  @Column(name = "ios_app_distributed")
  private Integer iosAppDistributed;

  @Column(name = "android_app_distributed")
  private Integer androidAppDistributed;

  @Column(name = "version")
  private Float version = 0f;

  @Transient AppSequenceBo appSequenceBo = new AppSequenceBo();

  @Transient private AppsBo liveAppsBo = null;
}
