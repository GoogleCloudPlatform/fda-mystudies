/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.RESPONSE_DATASTORE;

@Getter
@AllArgsConstructor
public enum EnrollAuditEvent implements AuditLogEvent {
  USER_FOUND_ELGIBLE_FOR_STUDY(
      MOBILE_APPS, PARTICIPANT_DATASTORE, null, null, "USER_FOUND_ELGIBLE_FOR_STUDY"),

  ENROLMENT_TOKEN_FOUND_INVALID(
      MOBILE_APPS, PARTICIPANT_DATASTORE, null, null, "ENROLMENT_TOKEN_FOUND_INVALID"),

  USER_FOUND_INELGIBLE_FOR_STUDY(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "App user found in-eligible for study.",
      "USER_FOUND_INELGIBLE_FOR_STUDY"),

  PARTICIPANT_ID_RECEIVED(
      PARTICIPANT_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant ID received after study consent capturer.",
      "PARTICIPANT_ID_RECEIVED"),

  PARTICIPANT_ID_NOT_RECEIVED(
      PARTICIPANT_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant ID not received after study consent capture from app user",
      "PARTICIPANT_ID_NOT_RECEIVED"),

  STUDY_ENROLLMENT_FAILED(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "App user could not be enrolled into the study.",
      "STUDY_ENROLMENT__FAILED"),

  STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "Study State '${study_state_value}' saved or updated for participant.",
      "STUDY_STATE_SAVED_OR_UPDATED_FOR_PARTICIPANT"),

  STUDY_STATE_SAVE_OR_UPDATE_FAILED(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "Study State '${study_state_value}' failed to get saved/updated for participant.",
      "STUDY_STATE_SAVE_OR_UPDATE_FAILED"),

  WITHDRAWAL_FROM_STUDY_SUCCEEDED(
      MOBILE_APPS, PARTICIPANT_DATASTORE, null, null, "WITHDRAWAL_FROM_STUDY_SUCCEEDED"),

  WITHDRAWAL_FROM_STUDY_FAILED(
      MOBILE_APPS, PARTICIPANT_DATASTORE, null, null, "WITHDRAWAL_FROM_STUDY_FAILED"),

  READ_OPERATION_SUCCEEDED_FOR_ENROLMENT_STATUS(
      RESPONSE_DATASTORE,
      PARTICIPANT_DATASTORE,
      null,
      "Participant's Enrolment Status '${enrolment_Status}' read.",
      "READ_OPERATION_SUCCEEDED_FOR_ENROLMENT_STATUS"),

  READ_OPERATION_FAILED_FOR_ENROLMENT_STATUS(
      RESPONSE_DATASTORE,
      PARTICIPANT_DATASTORE,
      null,
      "Attempt to read participant's Enrolment Status '${enrolment_Status}' failed.",
      "READ_OPERATION_FAILED_FOR_ENROLMENT_STATUS"),

  READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "App user's study participation status for 1 or more studies read.",
      "READ_OPERATION_SUCCEEDED_FOR_STUDY_INFO"),

  READ_OPEARATION_FAILED_FOR_STUDY_INFO(
      MOBILE_APPS,
      PARTICIPANT_DATASTORE,
      null,
      "Attempt to read app user's study participation status for 1 or more studies, failed.",
      "READ_OPEARATION_FAILED_FOR_STUDY_INFO");

  private final PlatformComponent source;
  private final PlatformComponent destination;
  private final PlatformComponent resourceServer;
  private final String description;
  private final String eventCode;
}
