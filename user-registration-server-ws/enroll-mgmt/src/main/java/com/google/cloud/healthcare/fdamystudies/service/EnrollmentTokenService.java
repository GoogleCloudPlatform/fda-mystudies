/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;

public interface EnrollmentTokenService {

  public boolean studyExists(@NotNull String shortName);

  public boolean hasParticipant(@NotNull String shortName, @NotNull String tokenValue);

  public boolean isValidStudyToken(@NotNull String token, @NotNull String shortName);

  public boolean enrollmentTokenRequired(@NotNull String shortName);

  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName, @Nullable String tokenValue, String userId)
      throws SystemException, InvalidRequestException, UnAuthorizedRequestException;
}
