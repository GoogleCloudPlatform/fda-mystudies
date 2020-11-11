/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StudyDetails {
  private String id;

  private String customId;

  private String name;

  private Long sitesCount;

  private List<SiteDetails> sites = new ArrayList<>();

  private String type;

  private String appInfoId;

  private String appId;

  private String appName;

  private Long invited;

  private Long enrolled;

  private Double enrollmentPercentage;

  private Integer studyPermission;

  private String logoImageUrl;

  private String studyStatus;
}
