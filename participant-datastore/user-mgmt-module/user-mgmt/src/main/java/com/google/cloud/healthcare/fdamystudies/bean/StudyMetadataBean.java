/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.LARGE_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.MEDIUM_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.TINY_LENGTH;
import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.XS_LENGTH;
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
@AllArgsConstructor
@ToString
public class StudyMetadataBean implements Serializable {

  private static final long serialVersionUID = 1L;
  @NotBlank private String studyId;

  @Size(max = SMALL_LENGTH)
  private String studyTitle;

  @NotBlank private String studyVersion;

  @Size(max = XS_LENGTH)
  private String studyType;

  @Size(max = XS_LENGTH)
  private String studyStatus;

  @Size(max = SMALL_LENGTH)
  private String studyCategory;

  @Size(max = MEDIUM_LENGTH)
  private String studyTagline;

  @Size(max = MEDIUM_LENGTH)
  private String studySponsor;

  @Size(max = TINY_LENGTH)
  private String studyEnrolling;

  @NotBlank private String appId;

  @Size(max = SMALL_LENGTH)
  @NotBlank
  private String appName;

  private String appDescription;

  @Size(max = LARGE_LENGTH)
  private String logoImageUrl;

  @ToString.Exclude
  @NotBlank
  @Size(max = EMAIL_LENGTH)
  @Email
  private String contactEmail;
}
