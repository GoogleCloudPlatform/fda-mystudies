/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.CLOUD_STORAGE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_CONSENT_DATASTORE;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum ConsentManagementEnum implements AuditLogEvent {
  INFORMED_CONSENT_PROVIDED_FOR_STUDY(
      MOBILE_APPS,
      PARTICIPANT_CONSENT_DATASTORE,
      null,
      "Informed consent provided by app user for the study (consent version: ${consent_version}, "
          + "data-sharing consent: ${data_sharing_consent}).",
      "INFORMED_CONSENT_PROVIDED_FOR_STUDY"),

  READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT(
      MOBILE_APPS,
      CLOUD_STORAGE,
      PARTICIPANT_CONSENT_DATASTORE,
      "Participant's consent document (${file_name}) retrieved.",
      "READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT"),

  READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT(
      MOBILE_APPS,
      CLOUD_STORAGE,
      PARTICIPANT_CONSENT_DATASTORE,
      "Attempt to retrieve participant's consent document failed.",
      "READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT"),

  SIGNED_CONSENT_DOCUMENT_SAVED(
      MOBILE_APPS,
      CLOUD_STORAGE,
      PARTICIPANT_CONSENT_DATASTORE,
      "Consent document (${file_name}) for consent version ${consent_version}"
          + "saved in directory ${directory_name}.",
      "SIGNED_CONSENT_DOCUMENT_SAVED"),

  SIGNED_CONSENT_DOCUMENT_SAVE_FAILED(
      MOBILE_APPS,
      CLOUD_STORAGE,
      PARTICIPANT_CONSENT_DATASTORE,
      "Consent document (${file_name}) for consent version ${consent_version}"
          + "could not be saved in directory ${directory_name}.",
      "SIGNED_CONSENT_DOCUMENT_SAVE_FAILED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private ConsentManagementEnum(
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
