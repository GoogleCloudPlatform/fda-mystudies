/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AdminUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;

public interface ManageUserService {

  public AdminUserResponse createUser(UserRequest user);

  public AdminUserResponse updateUser(UserRequest user, String loggedInAdminUserId);
}
