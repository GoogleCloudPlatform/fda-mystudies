/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserProfileResponse extends BaseResponse {
  private String firstName;
  private String lastName;
  private String email;
  private String userId;
  private Integer manageLocations;
  private boolean superAdmin;

  public UserProfileResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
