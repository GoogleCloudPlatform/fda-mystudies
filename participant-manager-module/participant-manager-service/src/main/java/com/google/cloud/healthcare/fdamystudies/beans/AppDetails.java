/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppDetails {
  private String id;

  private String customId;

  private String name;

  private Long invitedCount;

  private Long enrolledCount;

  private Double enrollmentPercentage;

  private Long totalStudiesCount;

  private Long appUsersCount;

  private Integer appPermission;
}
