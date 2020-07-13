/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.Getter;

@Getter
public enum ParticipantManagerEvent implements AuditLogEvent {
  ADD_NEW_LOCATION_SUCCESS(
      false,
      Constants.ACCESS_LEVEL,
      "",
      Constants.FMSGCPARMGRWEBAPP,
      Constants.USER_ACCESS_LEVEL,
      "",
      "New location added successful",
      "New location added by user successful.(location Name- ${location})",
      "ADD_NEW_LOCATION_SUCCESS",
      true),

  ADD_NEW_LOCATION_FAILURE(
      false,
      Constants.ACCESS_LEVEL,
      "",
      Constants.FMSGCPARMGRWEBAPP,
      Constants.USER_ACCESS_LEVEL,
      "",
      "New location  added failure",
      "New location added by user failed.(location Name- ${location})",
      "ADD_NEW_LOCATION_FAILURE",
      true);

  private final String eventName;
  private final boolean alert;
  private final String systemId;
  private final String accessLevel;
  private final String clientId;
  private final String clientAccessLevel;
  private final String resourceServer;
  private final String eventDetail;
  private final String description;
  private final boolean fallback;

  private ParticipantManagerEvent(
      boolean alert,
      String accessLevel,
      String systemId,
      String clientId,
      String clientAccessLevel,
      String resourceServer,
      String eventDetail,
      String description,
      String eventName,
      boolean fallback) {
    this.alert = alert;
    this.accessLevel = accessLevel;
    this.systemId = systemId;
    this.clientId = clientId;
    this.clientAccessLevel = clientAccessLevel;
    this.resourceServer = resourceServer;
    this.description = description;
    this.eventDetail = eventDetail;
    this.eventName = eventName;
    this.fallback = fallback;
  }

  private static class Constants {

    private static final String ACCESS_LEVEL = "Access Level";
    private static final String FMSGCPARMGRWEBAPP = "FMSGCPARMGRWEBAPP";
    private static final String USER_ACCESS_LEVEL = "User Access Level";
  }
}
