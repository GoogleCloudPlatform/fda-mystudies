/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.


package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import com.google.cloud.healthcare.fdamystudies.common.UserAccessLevel;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.SCIM_AUTH_SERVER;

@Getter
@AllArgsConstructor
public enum AuthScimEvent implements AuditLogEvent {
  PASSWORD_RESET_SUCCESS(
      PARTICIPANT_DATASTORE,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Password reset successful.",
      UserAccessLevel.APP_STUDY_ADMIN,
      "PASSWORD_RESET_SUCCESS"),

  PASSWORD_RESET_FAILED(
      PARTICIPANT_DATASTORE,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Password reset failed.",
      UserAccessLevel.APP_STUDY_ADMIN,
      "PASSWORD_RESET_FAILED");

  private final PlatformComponent source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final UserAccessLevel userAccessLevel;
  private final String eventCode;
}
*/
