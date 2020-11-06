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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;

@Setter
@Getter
@NoArgsConstructor
public class LocationDetails {

  private String customId;

  private String name;

  private String description;

  @ToStringExclude private String userId;

  private String locationId;

  private Integer status;

  private Integer studiesCount = 0;

  private List<String> studyNames = new ArrayList<>();

  public LocationDetails(String customId, String name, String description) {
    this.customId = customId;
    this.name = name;
    this.description = description;
  }
}
