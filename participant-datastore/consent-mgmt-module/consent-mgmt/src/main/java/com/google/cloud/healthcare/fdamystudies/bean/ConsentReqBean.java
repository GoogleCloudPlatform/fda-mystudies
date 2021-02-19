/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ConsentReqBean {
  @Size(max = SMALL_LENGTH)
  @NotBlank
  private String version;

  @Size(max = SMALL_LENGTH)
  @NotBlank
  private String status;

  @NotBlank private String pdf;
}
