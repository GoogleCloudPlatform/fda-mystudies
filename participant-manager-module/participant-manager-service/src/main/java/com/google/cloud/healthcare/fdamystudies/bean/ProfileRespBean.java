/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileRespBean {
  private String firstName = "";
  private String lastName = "";
  private String email = "";
  private Integer userId = 0;
  private Integer manageLocations;
  private Boolean superAdmin = false;
}
