/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteRequest {

  @NotBlank
  @Size(max = 64)
  private String studyId;

  @NotBlank
  @Size(max = 64)
  private String locationId;

  private String userId;

  public SiteRequest(String studyId, String locationId) {
    this.studyId = studyId;
    this.locationId = locationId;
  }
}
