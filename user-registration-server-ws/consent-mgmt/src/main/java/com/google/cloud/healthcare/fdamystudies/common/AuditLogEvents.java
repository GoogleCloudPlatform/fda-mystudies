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
public enum AuditLogEvents {
  VALIDATION_TOKEN_SUCCESS(
      false,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.APP_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Validation of tokens successful",
      "Client/app-specific and user-specific tokens sent to Auth Server for validation and found valid by Auth Server, for app user with User ID ${user_id}.",
      "VALIDATION_TOKEN_SUCCESS",
      false),

  VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_INVALID_CLIENT_CREDENTIALS(
      true,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.APP_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Validation of tokens unsuccessful: Client Credentials invalid",
      "Client/app-specific and user-specific tokens sent to Auth Server for validation and client/app-specific credentials found invalid by Auth Server, for app user with User ID ${user_id}.",
      "VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_INVALID_CLIENT_CREDENTIALS",
      false),

  VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_INVALID_ACCESS_TOKEN(
      true,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.APP_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Validation of tokens unsuccessful: Access Token invalid",
      "Client/app-specific and user-specific tokens sent to Auth Server for validation and Access Token found invalid by Auth Server, for app user with User ID ${user_id}.",
      "VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_INVALID_ACCESS_TOKEN",
      false),

  VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_EXPIRED_ACCESS_TOKEN(
      false,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.APP_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Validation of tokens unsuccessful: Access Token expired.",
      "Client/app-specific and user-specific tokens sent to Auth Server for validation and Access Token found expired by Auth Server, for app user with User ID ${user_id}.",
      "VALIDATION_TOKEN_UNSUCCESSFUL_DUE_TO_EXPIRED_ACCESS_TOKEN",
      false),

  CLIENT_CREDENTIAL_VALIDATION_SUCCESS(
      false,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.SYSTEM_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Client credentials validation success",
      "Client credentials sent to Auth server for validation and found valid by Auth Server, for ${resource_requesting_entity_system_id}.",
      "CLIENT_CREDENTIAL_VALIDATION_SUCCESS",
      false),

  INVALID_CLIENT_CREDENTIALS(
      true,
      Constants.NA,
      Constants.FMSGCPARDTST,
      Constants.FMSGCPARDTST,
      Constants.SYSTEM_LEVEL_ACCESS_IN_AUTH_SERVER,
      Constants.NA,
      "Invalid client credentials",
      "Client credentials sent to Auth server for validation and found invalid by Auth Server,  for ${resource_requesting_entity_system_id}.",
      "INVALID_CLIENT_CREDENTIALS",
      false),

  INFORMED_CONSENT_PROVIDED_FOR_STUDY(
      false,
      Constants.APP_USER,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Informed Consent provided for study",
      "App user provided informed consent for the study and Consent information saved for the participant is as follows: Consent Version: ${consent_version}, Primary Consent: Provided, Data-Sharing Consent: ${provided_or_not_provided_or_not_applicable}",
      "INFORMED_CONSENT_PROVIDED_FOR_STUDY",
      false),

  ENROLMENT_INTO_STUDY_SUCCESSFUL(
      false,
      Constants.PARTICIPANT,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Enrolment into study successful",
      "App user was enrolled into study successfully as a Participant. Enrolment Status updated to ${enrolment_status}, Participant ID: ${participant_id}",
      "ENROLMENT_INTO_STUDY_SUCCESSFUL",
      false),

  ENROLMENT_INTO_STUDY_FAILED(
      true,
      Constants.APP_USER,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Enrolment into study failed",
      "App user could not be enrolled into study as a Participant. ",
      "ENROLMENT_INTO_STUDY_FAILED",
      false),

  SIGNED_CONSENT_DOCUMENT_SAVED(
      false,
      Constants.PARTICIPANT,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Signed Consent Document saved ",
      "Consent Document e-signed by the participant, saved successfully into Cloud Storage. Consent Document file name ${file_name}, Enrolment Status: ${enrolment_status},  Consent Version: ${consent_version}, Primary Consent: Provided, Data-Sharing Consent: ${provided_or_not_provided_or_not_applicable}. ",
      "SIGNED_CONSENT_DOCUMENT_SAVED",
      false),

  SIGNED_CONSENT_DOCUMENT_FAILED(
      true,
      Constants.PARTICIPANT,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Signed Consent Document could not be saved ",
      "Consent Document e-signed by the participant, could not be saved  into Cloud Storage. Consent Document file name ${file_name}, Enrolment Status: ${enrolment_status},  Consent Version: ${consent_version}, Primary Consent: Provided, Data-Sharing Consent: ${provided_or_not_provided_or_not_applicable}",
      "SIGNED_CONSENT_DOCUMENT_FAILED",
      false),

  READ_OPERATION_SUCCESSFUL_FOR_SIGNED_CONSENT_DOCUMENT(
      false,
      Constants.PARTICIPANT,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Read operation successful for signed consent document",
      "Participant's signed consent document with file name ${file_name} (as stored in Cloud Storage) read by Mobile App.  (Web Service name: ${web_service_name})",
      "READ_OPERATION_SUCCESSFUL_FOR_SIGNED_CONSENT_DOCUMENT",
      false),

  READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT(
      true,
      Constants.PARTICIPANT,
      Constants.FMSGCPARDTST,
      Constants.FMSGCMOBAPP,
      Constants.STUDY_LEVEL,
      Constants.NA,
      "Read operation failed for signed consent document",
      "Participant's signed consent document with file name ${file_name} (as stored in Cloud Storage) could not be read by Mobile App.  (Web Service name: ${web_service_name})",
      "READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT",
      false);

  private final String eventName;
  private final boolean alert;
  private final String systemId;
  private final String accessLevel;
  private final String clientId;
  private final String clientAccessLevel;
  private final String resourceServer;
  private final String eventDetail;

  private final String description;

  private final boolean rollback;

  private AuditLogEvents(
      boolean alert,
      String accessLevel,
      String systemId,
      String clientId,
      String clientAccessLevel,
      String resourceServer,
      String eventDetail,
      String description,
      String eventName,
      boolean rollback) {
    this.alert = alert;
    this.accessLevel = accessLevel;
    this.systemId = systemId;
    this.clientId = clientId;
    this.clientAccessLevel = clientAccessLevel;
    this.resourceServer = resourceServer;
    this.description = description;
    this.eventDetail = eventDetail;
    this.eventName = eventName;
    this.rollback = rollback;
  }

  private static class Constants {
    private static final String NA = "NA";

    private static final String PARTICIPANT = "Participant";

    private static final String APP_LEVEL_ACCESS_IN_AUTH_SERVER = "App-level access in Auth Server";

    private static final String STUDY_LEVEL = "Study-level";

    private static final String APP_USER = "App User";

    private static final String SYSTEM_LEVEL_ACCESS_IN_AUTH_SERVER =
        "System-level access in Auth Server";

    private static final String FMSGCMOBAPP = "FMSGCMOBAPP";

    private static final String FMSGCPARDTST = "FMSGCPARDTST";
  }
}
