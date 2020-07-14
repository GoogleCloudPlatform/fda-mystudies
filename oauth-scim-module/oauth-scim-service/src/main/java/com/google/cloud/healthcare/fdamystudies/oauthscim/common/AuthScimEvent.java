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
      Constant.APP_USER,
      Constant.SYSTEM_ID,
      Constant.CLIENT_ID,
      Constant.APP_LEVEL,
      Constant.PARTICIPANT_DATASTORE,
      "Password reset success",
      "Password reset for User ID ${user_id} was successful.",
      "PASSWORD_RESET_SUCCESS",
      true),

  PASSWORD_RESET_FAILED(
      false,
      Constant.APP_USER,
      Constant.SYSTEM_ID,
      Constant.CLIENT_ID,
      Constant.APP_LEVEL,
      Constant.PARTICIPANT_DATASTORE,
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
      Constant accessLevel,
      Constant systemId,
      Constant clientId,
      Constant clientAccessLevel,
      Constant resourceServer,
      String eventDetail,
      String description,
      String eventName,
      boolean fallback) {
    this.alert = alert;
    this.accessLevel = accessLevel.value;
    this.systemId = systemId.value;
    this.clientId = clientId.value;
    this.clientAccessLevel = clientAccessLevel.value;
    this.resourceServer = resourceServer.value;
    this.description = description;
    this.eventDetail = eventDetail;
    this.eventName = eventName;
    this.fallback = fallback;
  }

  private enum Constant {
    APP_LEVEL("App-level"),
    APP_USER("App User"),
    PARTICIPANT_DATASTORE("Participant Datastore"),
    SYSTEM_ID("FMSGCAUTHSVR"),
    CLIENT_ID("FMSGCMOBAPP");

    private final String value;

    private Constant(String value) {
      this.value = value;
    }
  }
}
