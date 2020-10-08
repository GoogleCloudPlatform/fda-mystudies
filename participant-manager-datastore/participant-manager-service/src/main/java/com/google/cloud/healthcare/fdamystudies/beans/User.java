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
import lombok.ToString;

@Getter
@Setter
@ToString
public class User {

  private String id;

  private String email;

  private String firstName;

  private String lastName;

  private boolean superAdmin;

  private Integer manageLocations;

  private String status;

  private List<UserAppDetails> apps = new ArrayList<>();
}
