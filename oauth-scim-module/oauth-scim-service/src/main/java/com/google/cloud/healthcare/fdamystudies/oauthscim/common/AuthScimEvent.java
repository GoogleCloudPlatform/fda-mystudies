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
  SIGNIN_SUCCEEDED(null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "SIGNIN_SUCCEEDED"),

  SIGNIN_FAILED(null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "SIGNIN_FAILED"),

  SIGNIN_FAILED_UNREGISTERED_USER(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Sign-in failure due to unregistered username.",
      "SIGNIN_FAILED_UNREGISTERED_USER"),

  SIGNIN_FAILED_INVALID_PASSWORD(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "SIGNIN_FAILED_INVALID_PASSWORD"),

  SIGNIN_FAILED_EXPIRED_PASSWORD(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "SIGNIN_FAILED_EXPIRED_PASSWORD"),

  PASSWORD_HELP_REQUESTED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_HELP_REQUESTED"),

  PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      null,
      "PASSWORD_HELP_REQUESTED_FOR_UNREGISTERED_USERNAME"),

  PASSWORD_HELP_EMAIL_SENT(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_HELP_EMAIL_SENT"),

  PASSWORD_HELP_EMAIL_FAILED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_HELP_EMAIL_FAILED"),

  SIGNIN_WITH_TEMPORARY_PASSWORD_SUCCEEDED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "User signed in with temporary password.",
      "SIGNIN_WITH_TEMPORARY_PASSWORD_SUCCEEDED"),

  SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Sign-in with temporary password failed.",
      "SIGNIN_WITH_TEMPORARY_PASSWORD_FAILED"),

  SIGNIN_FAILED_INVALID_TEMPORARY_PASSWORD(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Sign-in failure due to invalid temporary password.",
      "SIGNIN_FAILED_INVALID_TEMPORARY_PASSWORD"),

  SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Sign-in failure due to expired temporary password.",
      "SIGNIN_FAILED_EXPIRED_TEMPORARY_PASSWORD"),

  PASSWORD_RESET_SUCCEEDED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_RESET_SUCCEEDED"),

  PASSWORD_RESET_FAILED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_RESET_FAILED"),

  ACCOUNT_LOCKED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "User account locked for ${lock_time} due to ${failed_attempts} consecutive failed sign-in attempts.",
      "ACCOUNT_LOCKED"),

  PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      null,
      "PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT"),

  PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      null,
      "PASSWORD_RESET_EMAIL_FAILED_FOR_LOCKED_ACCOUNT"),

  PASSWORD_CHANGE_SUCCEEDED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_CHANGE_SUCCEEDED"),

  PASSWORD_CHANGE_FAILED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "PASSWORD_CHANGE_FAILED"),

  USER_SIGNOUT_SUCCEEDED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "USER_SIGNOUT_SUCCEEDED"),

  USER_SIGNOUT_FAILED(null, SCIM_AUTH_SERVER, PARTICIPANT_DATASTORE, null, "USER_SIGNOUT_FAILED"),

  ACCESS_TOKEN_INVALID_OR_EXPIRED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "Access token found invalid or expired for user ID ${user_id}.",
      "ACCESS_TOKEN_INVALID_OR_EXPIRED"),

  NEW_ACCESS_TOKEN_GENERATED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_DATASTORE,
      "New access token generated for user with grant type ${grant_type}.",
      "NEW_ACCESS_TOKEN_GENERATED");

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
