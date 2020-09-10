/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserRequest {

  @ToString.Exclude
  @NotBlank
  @Size(max = 320)
  @Email
  private String email;

  @NotBlank
  @Size(max = 200)
  @ToString.Exclude
  private String firstName;

  @NotBlank
  @Size(max = 200)
  @ToString.Exclude
  private String lastName;

  @Min(0)
  @Max(1)
  private Integer manageLocations;

  @NotNull private boolean superAdmin;

  private List<UserAppPermissionRequest> apps;

  private String id;

  private String superAdminUserId;
}
