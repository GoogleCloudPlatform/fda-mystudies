package com.google.cloud.healthcare.fdamystudies.service;

import javax.validation.constraints.NotNull;
import org.springframework.lang.Nullable;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentResponseBean;

public interface EnrollmentTokenService {

  public boolean studyExists(@NotNull String shortName);

  public boolean hasParticipant(@NotNull String shortName, @NotNull String tokenValue);

  public boolean isValidStudyToken(@NotNull String token, @NotNull String shortName);

  public boolean enrollmentTokenRequired(@NotNull String shortName);

  public EnrollmentResponseBean enrollParticipant(
      @NotNull String shortName, @Nullable String tokenValue, String userId);
}
