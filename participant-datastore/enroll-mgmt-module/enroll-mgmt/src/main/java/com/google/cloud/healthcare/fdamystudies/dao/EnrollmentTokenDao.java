/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public interface EnrollmentTokenDao {

  public StudyEntity getStudyDetails(String shortName);

  public boolean hasParticipant(String shortName, @NotNull String tokenValue);

  public boolean isValidStudyToken(@NotNull String token, String shortName, @NotNull String email);

  public boolean enrollmentTokenRequired(String shortName);

  public EnrollmentResponseBean enrollParticipant(
      String shortName,
      @Nullable String tokenValue,
      UserDetailsEntity userDetailsEntity,
      boolean isTokenRequired,
      String participantId);
}
