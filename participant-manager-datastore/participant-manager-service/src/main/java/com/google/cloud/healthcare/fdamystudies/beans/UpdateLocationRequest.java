/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringExclude;

@Setter
@Getter
@NoArgsConstructor
public class UpdateLocationRequest {

  @Size(max = 255)
  private String name;

  @Size(max = 255)
  private String description;

  @ToStringExclude private String userId;

  private String locationId;

  private Integer status;
}
