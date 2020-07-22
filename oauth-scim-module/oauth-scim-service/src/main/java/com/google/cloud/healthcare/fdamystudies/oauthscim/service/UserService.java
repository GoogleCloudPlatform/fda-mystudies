/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;

public interface UserService {

  public UserResponse createUser(UserRequest userRequest);

  public ResetPasswordResponse resetPassword(ResetPasswordRequest resetPasswordRequest)

  public ChangePasswordResponse changePassword(ChangePasswordRequest userRequest)
      throws JsonProcessingException;
}
