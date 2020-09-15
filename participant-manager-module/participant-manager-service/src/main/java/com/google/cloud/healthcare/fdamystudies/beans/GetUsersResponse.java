/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GetUsersResponse extends BaseResponse {

  private List<User> users = new ArrayList<>();

  public GetUsersResponse(MessageCode messageCode, List<User> users) {
    super(messageCode);
    this.users.addAll(users);
  }
}
