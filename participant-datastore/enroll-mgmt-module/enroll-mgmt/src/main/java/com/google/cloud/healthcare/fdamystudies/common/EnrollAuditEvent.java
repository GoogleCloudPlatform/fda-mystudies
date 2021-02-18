/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_ENROLL_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.RESPONSE_DATASTORE;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum EnrollAuditEvent implements AuditLogEvent {
  USER_FOUND_ELIGIBLE_FOR_STUDY(
      MOBILE_APPS, PARTICIPANT_ENROLL_DATASTORE, null, null, "USER_FOUND_ELIGIBLE_FOR_STUDY"),

  ENROLLMENT_TOKEN_FOUND_INVALID(
      MOBILE_APPS, PARTICIPANT_ENROLL_DATASTORE, null, null, "ENROLLMENT_TOKEN_FOUND_INVALID"),

  USER_FOUND_INELIGIBLE_FOR_STUDY(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "App user found in-eligible for study.",
      "USER_FOUND_INELIGIBLE_FOR_STUDY"),

  PARTICIPANT_ID_RECEIVED(
      PARTICIPANT_ENROLL_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant ID received after study consent captured.",
      "PARTICIPANT_ID_RECEIVED"),

  PARTICIPANT_ID_NOT_RECEIVED(
      PARTICIPANT_ENROLL_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant ID not received after study consent capture from app user",
      "PARTICIPANT_ID_NOT_RECEIVED"),

  STUDY_ENROLLMENT_FAILED(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "App user could not be enrolled into the study.",
      "STUDY_ENROLLMENT_FAILED"),

  STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "Study State '${study_state_value}' saved or updated for participant.",
      "STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT"),

  STUDY_STATE_SAVE_OR_UPDATE_FAILED(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "Study State '${study_state_value}' failed to get saved/updated for participant.",
      "STUDY_STATE_SAVE_OR_UPDATE_FAILED"),

  WITHDRAWAL_FROM_STUDY_SUCCEEDED(
      MOBILE_APPS, PARTICIPANT_ENROLL_DATASTORE, null, null, "WITHDRAWAL_FROM_STUDY_SUCCEEDED"),

  WITHDRAWAL_FROM_STUDY_FAILED(
      MOBILE_APPS, PARTICIPANT_ENROLL_DATASTORE, null, null, "WITHDRAWAL_FROM_STUDY_FAILED"),

  READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS(
      RESPONSE_DATASTORE,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "Participant's enrollment status '${enrollment_status}' read.",
      "READ_OPERATION_SUCCEEDED_FOR_ENROLLMENT_STATUS"),

  READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS(
      RESPONSE_DATASTORE,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "Attempt to read participant's enrollment status failed.",
      "READ_OPERATION_FAILED_FOR_ENROLLMENT_STATUS"),

  READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "App user's study participation status for 1 or more studies read.",
      "READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO"),

  READ_OPERATION_FAILED_FOR_STUDY_INFO(
      MOBILE_APPS,
      PARTICIPANT_ENROLL_DATASTORE,
      null,
      "Attempt to read app user's study participation status for 1 or more studies, failed.",
      "READ_OPERATION_FAILED_FOR_STUDY_INFO"),

  USER_ENROLLED_INTO_STUDY(
      MOBILE_APPS, PARTICIPANT_ENROLL_DATASTORE, null, null, "USER_ENROLLED_INTO_STUDY");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private EnrollAuditEvent(
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
