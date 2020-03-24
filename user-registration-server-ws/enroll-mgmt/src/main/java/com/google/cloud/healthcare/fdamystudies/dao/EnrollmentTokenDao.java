/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

public interface EnrollmentTokenDao {

  public boolean studyExists(@NotNull String shortName);

  public boolean hasParticipant(@NotNull String shortName, @NotNull String tokenValue);

  public boolean isValidStudyToken(@NotNull String token, @NotNull String shortName);

  public boolean enrollmentTokenRequired(@NotNull String shortName);

  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName,
      @Nullable String tokenValue,
      UserDetailsBO userDetailsBO,
      boolean isTokenRequired,
      String participantId);
}
