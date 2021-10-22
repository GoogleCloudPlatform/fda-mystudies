/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.EMAIL_LENGTH;

import java.io.Serializable;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class AppMetadataBean implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotBlank private String appId;

  @Size(max = SMALL_LENGTH)
  @NotBlank
  private String appName;

  //  private String appDescription;

  private String appType;

  private String appPlatform;

  @ToString.Exclude
  @Size(max = EMAIL_LENGTH)
  @Email
  private String contactEmail;

  @ToString.Exclude
  @Size(max = EMAIL_LENGTH)
  @Email
  private String feedBackEmail;

  @ToString.Exclude
  @Size(max = EMAIL_LENGTH)
  @Email
  private String appSupportEmail;

  @ToString.Exclude
  @Size(max = EMAIL_LENGTH)
  @Email
  private String fromEmail;

  private String appTermsUrl;
  private String appPrivacyUrl;
  private String appStoreUrl;
  private String playStoreUrl;
  private String appWebSiteUrl;

  private String organizationName;

  private String androidBundleId;
  private String androidServerKey;
  private String iosBundleId;
  private String iosServerKey;

  private String appStatus;

  private String iosVersion;
  private Boolean iosForceUpgrade;
  private String androidVersion;
  private Boolean androidForceUpgrade;
}
