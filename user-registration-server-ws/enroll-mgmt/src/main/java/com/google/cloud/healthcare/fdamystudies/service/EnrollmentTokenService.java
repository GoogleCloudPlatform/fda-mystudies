/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public interface EnrollmentTokenService {

  public boolean studyExists(@NotNull String studyId);

  public boolean hasParticipant(@NotNull String studyId, @NotNull String tokenValue);

  public boolean isEnrollmentTokenValid(
      @NotNull String token, @NotNull String studyId, @NotNull String userId);

  public boolean enrollmentTokenRequired(@NotNull String studyId);

  public EnrollmentResponseBean enrollParticipant(
      @NotNull String studyId, @Nullable String tokenValue, String userId)
      throws SystemException, InvalidRequestException, UnAuthorizedRequestException;
}
