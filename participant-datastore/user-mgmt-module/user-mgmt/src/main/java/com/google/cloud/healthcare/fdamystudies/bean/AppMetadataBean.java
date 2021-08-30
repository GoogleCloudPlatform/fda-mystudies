/*
 * Copyright 2020-2021 Google LLC
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
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String contactEmail;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String feedBackEmail;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String appSupportEmail;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String fromEmail;

  @NotBlank private String appTermsUrl;
  @NotBlank private String appPrivacyUrl;
  @NotBlank private String appStoreUrl;
  @NotBlank private String playStoreUrl;
  @NotBlank private String appWebsite;

  @NotBlank private String oraganizationName;

  @NotBlank private String androidBundleId;
  @NotBlank private String androidServerKey;
  @NotBlank private String iosBundleId;
  @NotBlank private String iosServerKey;

  @NotBlank private String iosXCodeAppVersion;
  @NotBlank private String iosAppBuildVersion;
  private Integer iosForceUpgrade;
  @NotBlank private String androidAppBuildVersion;
  private Integer androidForceUpdrade;

  @NotBlank private String appStatus;
}
