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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LocationRequest {

  @Size(max = 15)
  @NotBlank
  private String customId;

  @Size(max = 255)
  @NotBlank
  private String name;

  @Size(max = 255)
  @NotBlank
  private String description;

  public LocationRequest(String customId, String name, String description) {
    this.customId = customId;
    this.name = name;
    this.description = description;
  }
}
