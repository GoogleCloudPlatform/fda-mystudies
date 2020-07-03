/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateUserResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;

public interface UserService {

  public UserResponse createUser(UserRequest userRequest);

  public UpdateUserResponse updateUser(UpdateUserRequest userRequest)
      throws JsonProcessingException;

  public UpdateUserResponse resetPassword(
      UpdateUserRequest userRequest, AuditLogEventRequest aleRequest)
      throws JsonProcessingException;
}
