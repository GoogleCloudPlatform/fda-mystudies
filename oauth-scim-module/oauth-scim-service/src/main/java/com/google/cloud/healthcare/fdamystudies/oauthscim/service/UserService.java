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
import com.google.cloud.healthcare.fdamystudies.beans.AuthenticationResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ChangePasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ResetPasswordResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import java.util.Optional;

public interface UserService {

  public UserResponse createUser(UserRequest userRequest);

  public ResetPasswordResponse resetPassword(
      ResetPasswordRequest resetPasswordRequest, AuditLogEventRequest auditRequest)
      throws JsonProcessingException;

  public ChangePasswordResponse changePassword(ChangePasswordRequest userRequest)
      throws JsonProcessingException;

  public Optional<UserEntity> findUserByTempRegId(String tempRegId);

  public UpdateEmailStatusResponse updateEmailStatusAndTempRegId(
      UpdateEmailStatusRequest userRequest) throws JsonProcessingException;

  public AuthenticationResponse authenticate(UserRequest user) throws JsonProcessingException;

  public void resetTempRegId(String userId);

  public void removeExpiredTempRegIds();

  public UserResponse logout(String userId) throws JsonProcessingException;

  public UserResponse revokeAndReplaceRefreshToken(String userId, String refreshToken)
      throws JsonProcessingException;

  public void deleteUserAccount(String userId);
}
