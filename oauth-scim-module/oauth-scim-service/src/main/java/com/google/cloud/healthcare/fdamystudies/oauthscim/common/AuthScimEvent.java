/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.SCIM_AUTH_SERVER;

import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum AuthScimEvent implements AuditLogEvent {
  PASSWORD_RESET_SUCCESS(
      PARTICIPANT_DATASTORE,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Password reset successful.",
      "PASSWORD_RESET_SUCCESS"),

  PASSWORD_RESET_FAILED(
      PARTICIPANT_DATASTORE,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Password reset failed.",
      "PASSWORD_RESET_FAILED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private AuthScimEvent(
      PlatformComponent source,
      PlatformComponent destination,
      PlatformComponent resourceServer,
      String description,
      String eventCode) {
    this.source = Optional.ofNullable(source);
    this.destination = destination;
    this.resourceServer = Optional.ofNullable(resourceServer);
    this.description = description;
    this.eventCode = eventCode;
  }
}
