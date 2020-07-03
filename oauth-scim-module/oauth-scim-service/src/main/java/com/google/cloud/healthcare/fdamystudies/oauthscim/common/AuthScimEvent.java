/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.common;

import com.google.cloud.healthcare.fdamystudies.common.AuditLogEvent;
import lombok.Getter;

@Getter
public enum AuthScimEvent implements AuditLogEvent {
  PASSWORD_RESET_SUCCESS(
      false,
      Constants.APP_USER,
      Constants.FMSGCAUTHSVR,
      Constants.FMSGCMOBAPP,
      Constants.APP_LEVEL,
      Constants.PARTICIPANT_DATASTORE,
      "Password reset success",
      "Password reset for User ID ${user_id} was successful.",
      "PASSWORD_RESET_SUCCESS",
      true),

  PASSWORD_RESET_FAILED(
      false,
      Constants.APP_USER,
      Constants.FMSGCAUTHSVR,
      Constants.FMSGCMOBAPP,
      Constants.APP_LEVEL,
      Constants.PARTICIPANT_DATASTORE,
      "Password reset failure",
      "Password reset for User ID ${user_id}, failed.",
      "PASSWORD_RESET_FAILED",
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

  private AuthScimEvent(
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
    private static final String APP_LEVEL = "App-level";

    private static final String FMSGCAUTHSVR = "FMSGCAUTHSVR";

    private static final String APP_USER = "App User";

    private static final String PARTICIPANT_DATASTORE = "Participant Datastore";

    private static final String FMSGCMOBAPP = "FMSGCMOBAPP";
  }
}
