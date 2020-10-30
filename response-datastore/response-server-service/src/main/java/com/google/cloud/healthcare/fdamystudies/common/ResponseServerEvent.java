/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_USER_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.RESPONSE_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.STUDY_BUILDER;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseServerEvent implements AuditLogEvent {
  PARTICIPANT_ID_GENERATED(
      PARTICIPANT_USER_DATASTORE, RESPONSE_DATASTORE, null, null, "PARTICIPANT_ID_GENERATED"),

  PARTICIPANT_ID_GENERATION_FAILED(
      PARTICIPANT_USER_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant ID could not be generated for app user.",
      "PARTICIPANT_ID_GENERATION_FAILED"),

  PARTICIPANT_ID_INVALID(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Participant ID found invalid in submitted response.",
      "PARTICIPANT_ID_INVALID"),

  STUDY_METADATA_RECEIVED(
      STUDY_BUILDER,
      RESPONSE_DATASTORE,
      null,
      "Study metadata received.",
      "STUDY_METADATA_RECEIVED"),

  ACTIVITY_RESPONSE_RECEIVED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Activity response received from participant for activity type '${activity_type}',"
          + " activity ID '${activity_id}', activity version: '${activity_version}' "
          + "and run ID '${run_id}'.",
      "ACTIVITY_RESPONSE_RECEIVED"),

  ACTIVITY_RESPONSE_RECEIPT_FAILED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Receipt of activity response from participant failed,"
          + " for activity type '${questionnaire_or_active_task}', activity ID '${activity_id}',"
          + " activity version: '${version}' and run ID '${run_id}'.",
      "ACTIVITY_RESPONSE_RECEIPT_FAILED"),

  ACTIVTY_METADATA_RETRIEVED(
      RESPONSE_DATASTORE,
      STUDY_BUILDER,
      null,
      "Activity metadata retrieved for activity type '${activity_type}',"
          + " activity ID '${activity_id}' and activity version '${activity_version}'.",
      "ACTIVTY_METADATA_RETRIEVED"),

  ACTIVTY_METADATA_RETRIEVAL_FAILED(
      RESPONSE_DATASTORE,
      STUDY_BUILDER,
      null,
      "Activity metadata could not be retrieved for activity type '${activity_type}',"
          + " activity ID '${activity_id}' and activity version '${activity_version}'.",
      "ACTIVTY_METADATA_RETRIEVAL_FAILED"),

  ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity metadata conjoined with response data for activity type '${activity_type}',"
          + " activity ID '${activity_id}', activity version '${activity_version}'"
          + " and run ID '${run_id}'.",
      "ACTIVITY_METADATA_CONJOINED_WITH_RESPONSE_DATA"),

  ACTIVITY_METADATA_CONJOINING_WITH_RESPONSE_DATA_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Conjoining of activity metadata with response data, failed for "
          + "activity type '${activity_type}', activity ID '${activity_id}',"
          + " activity version '${activity_version}' and run ID '${run_id}'.",
      "ACTIVITY_METADATA_CONJOINING_WITH_RESPONSE_DATA_FAILED"),

  DATA_SHARING_CONSENT_VALUE_RETRIEVED(
      RESPONSE_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Latest data-sharing consent value ('${datasharing_consent_value}')"
          + " for participant retrieved.",
      "DATA_SHARING_CONSENT_VALUE_RETRIEVED"),

  DATA_SHARING_CONSENT_VALUE_RETRIEVAL_FAILED(
      RESPONSE_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Latest data-sharing consent value for participant could not be retrieved.",
      "DATA_SHARING_CONSENT_VALUE_RETRIEVAL_FAILED"),

  DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Latest data-sharing consent value of participant successfully conjoined with response data"
          + " for activity type '${activity_type}',activity ID '${activity_id}',"
          + " activity version '${activity_version}' and run ID '${run_id}'.",
      "DATA_SHARING_CONSENT_VALUE_CONJOINED_WITH_ACTIVITY_RESPONSE_DATA"),

  WITHDRAWAL_INFORMATION_RETRIEVED(
      RESPONSE_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participant's withdrawal information (withdrawn status: '${withdrawn_status}')"
          + " successfully retrieved, after receipt of activity response from participant.",
      "WITHDRAWAL_INFORMATION_RETRIEVED"),

  WITHDRAWAL_INFORMATION_RETREIVAL_FAILED(
      RESPONSE_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participant's withdrawal information could not be retrieved, after receipt of "
          + "activity response from participant.",
      "WITHDRAWAL_INFORMATION_RETREIVAL_FAILED"),

  ACTIVITY_RESPONSE_NOT_SAVED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Participant's activity response could not be saved. Response Metadata: "
          + "activity type '${activity_type}', activity ID '${activity_id}',"
          + " activity version '${activity_version}', "
          + "timestamp of response submission: '${submission_timestamp}'.",
      "ACTIVITY_RESPONSE_NOT_SAVED"),

  ACTIVITY_RESPONSE_SAVED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity response saved for activity type '${activity_type}', "
          + "activity ID '${activity_id}', activity version '${activity_version}'"
          + " and run ID '${run_id}', after retrieval and conjoining of required metadata.",
      "ACTIVITY_RESPONSE_SAVED"),

  ACTIVITY_RESPONSE_DATA_PROCESSING_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity response save operation failed for activity type '${activity_type}',"
          + " activity ID '${activity_id}', activity version '${activity_version}' and"
          + " run ID '${run_id}', after attempts to retrieve and conjoin required metadata.",
      "ACTIVITY_RESPONSE_DATA_PROCESSING_FAILED"),

  WITHDRAWAL_INFORMATION_UPDATED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Withdrawal information of participant successfully updated into all participant activity"
          + " responses.",
      "WITHDRAWAL_INFORMATION_UPDATED"),

  WITHDRAWAL_INFORMATION_UPDATE_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Withdrawal information of participant failed to get updated into 1 or more participant"
          + " activity responses.",
      "WITHDRAWAL_INFORMATION_UPDATE_FAILED"),

  PARTICIPANT_RESPONSE_DATA_DELETED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "All response data belonging to participant deleted.",
      "PARTICIPANT_RESPONSE_DATA_DELETED"),

  PARTICIPANT_RESPONSE_DATA_DELETION_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "1 or more of the participant's response datasets failed to get deleted.",
      "PARTICIPANT_RESPONSE_DATA_DELETION_FAILED"),

  ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity state '${activity_state}' for activity ID '${activity_id}' was saved "
          + "or updated for participant, after response submission. "
          + "Activity version: '${activity_version}' , run ID: '${run_id}'.",
      "ACTIVITY_STATE_SAVED_OR_UPDATED_AFTER_RESPONSE_SUBMISSION"),

  PARTICIPANT_ACTIVITY_DATA_DELETED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "All activity-state data for participant deleted.",
      "PARTICIPANT_ACTIVITY_DATA_DELETED"),

  ACTIVITY_DATA_DELETION_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity-state data of the participant, failed to get deleted.",
      "ACTIVITY_DATA_DELETION_FAILED"),

  PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE(
      PARTICIPANT_USER_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Information about participant's withdrawal from study received"
          + " as withdrawal timestamp '${withdrawal_timetamp}' and "
          + "data-retention setting '${dataretention_setting}'.",
      "PARTICIPANT_WITHDRAWAL_INTIMATION_FROM_PARTICIPANT_DATASTORE"),

  ACTIVITY_STATE_SAVED_OR_UPDATED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity state '${activity_state}' for activity ID '${activity_id}' was saved"
          + " or updated for participant. Activity version: '${activity_version}' , "
          + "run ID: '${run_id}'.",
      "ACTIVITY_STATE_SAVED_OR_UPDATED"),

  ACTIVITY_STATE_SAVE_OR_UPDATE_FAILED(
      RESPONSE_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Activity state '${activity_state}' for activity ID '${activity_id}' could not be saved"
          + " or updated for participant. Activity version: '${activity_version}' , "
          + "run ID: '${run_id}'.",
      "ACTIVITY_STATE_SAVE_OR_UPDATE_FAILED"),

  READ_OPERATION_FOR_ACTIVITY_STATE_INFO_SUCCEEDED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Participant's activity state information read for 1 or more activities.",
      "READ_OPERATION_FOR_ACTIVITY_STATE_INFO_SUCCEEDED"),

  READ_OPERATION_FOR_ACTIVITY_STATE_INFO_FAILED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Participant's activity state information could not be read for 1 or more activities.",
      "READ_OPERATION_FOR_ACTIVITY_STATE_INFO_FAILED"),

  READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Participant's response data read for 1 or more runs of 1 or more activities.",
      "READ_OPERATION_FOR_RESPONSE_DATA_SUCCEEDED"),

  READ_OPERATION_FOR_RESPONSE_DATA_FAILED(
      MOBILE_APPS,
      RESPONSE_DATASTORE,
      null,
      "Participant's response data could not be read for 1 or more runs of 1 or more activities.",
      "READ_OPERATION_FOR_RESPONSE_DATA_FAILED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private ResponseServerEvent(
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
