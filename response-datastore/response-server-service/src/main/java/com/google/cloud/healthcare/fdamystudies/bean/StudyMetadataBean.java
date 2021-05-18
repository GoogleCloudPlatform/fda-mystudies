/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudyMetadataBean implements Serializable {

  private static final long serialVersionUID = 1L;
  private String studyId;
  private String studyTitle;
  private String studyVersion;
  private String studyType;
  private String studyStatus;
  private String studyCategory;
  private String studyTagline;
  private String studySponsor;
  private String studyEnrolling;
  private String appId;
  private String appName;
  private String appDescription;
  private String logoImageUrl;
  private String contactEmail;
}
