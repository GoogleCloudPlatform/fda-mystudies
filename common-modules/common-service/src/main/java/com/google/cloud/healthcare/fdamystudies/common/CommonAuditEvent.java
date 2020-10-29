/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.SCIM_AUTH_SERVER;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum CommonAuditEvent implements AuditLogEvent {
  INVALID_CLIENT_ID_OR_SECRET(null, SCIM_AUTH_SERVER, null, null, "INVALID_CLIENT_ID_OR_SECRET"),

  ACCESS_TOKEN_INVALID_OR_EXPIRED(
      null, SCIM_AUTH_SERVER, null, null, "ACCESS_TOKEN_INVALID_OR_EXPIRED"),

  INVALID_GRANT_OR_INVALID_REFRESH_TOKEN(
      null, SCIM_AUTH_SERVER, null, null, "INVALID_GRANT_OR_INVALID_REFRESH_TOKEN"),

  RESOURCE_ACCESS_FAILED(
      null,
      null,
      null,
      "${uri_path} request failed with status ${status_code}",
      "RESOURCE_ACCESS_FAILED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private CommonAuditEvent(
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
