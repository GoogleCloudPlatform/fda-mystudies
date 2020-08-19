/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequest implements Serializable {

  private static final long serialVersionUID = 1L;

  @ToString.Exclude
  @NotBlank
  @Size(max = 255)
  private String firstName;

  @ToString.Exclude
  @NotBlank
  @Size(max = 255)
  private String lastName;

  private String userId;
}
