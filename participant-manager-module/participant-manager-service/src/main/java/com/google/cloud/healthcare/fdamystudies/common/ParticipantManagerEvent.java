/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_DATASTORE;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum ParticipantManagerEvent implements AuditLogEvent {
  USER_ACCOUNT_ACTIVATED(null, PARTICIPANT_DATASTORE, null, null, "USER_ACCOUNT_ACTIVATED"),

  USER_ACCOUNT_ACTIVATION_FAILED(
      null, PARTICIPANT_DATASTORE, null, null, "USER_ACCOUNT_ACTIVATION_FAILED"),

  USER_DEACTIVATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "User account deactivated (user ID - ${edited_user_id}).",
      "USER_DEACTIVATED"),

  USER_ACTIVATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "User account activated (user ID - ${edited_user_id}).",
      "USER_ACTIVATED"),

  USER_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION(
      null,
      PARTICIPANT_DATASTORE,
      null,
      null,
      "USER_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION"),

  SITE_ADDED_FOR_STUDY(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Site added to study (site ID- ${site_id}).",
      "SITE_ADDED_FOR_STUDY"),

  PARTICIPANT_EMAIL_ADDED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Participant email added to site (site ID- ${site_id}).",
      "PARTICIPANT_EMAIL_ADDED"),

  PARTICIPANTS_EMAIL_LIST_IMPORTED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Participants email list imported for site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORTED"),

  PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Participants email list import failed for site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED"),

  PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "1 or more emails in list failed to get imported to site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED"),

  SITE_DECOMMISSIONED_FOR_STUDY(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Site decommissioned for study (site ID- ${site_id}).",
      "SITE_DECOMMISSIONED_FOR_STUDY"),

  SITE_ACTIVATED_FOR_STUDY(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Site activated for study (site ID- ${site_id}).",
      "SITE_ACTIVATED_FOR_STUDY"),

  PARTICIPANT_INVITATION_EMAIL_RESENT(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Invitation email re-sent to 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_EMAIL_RESENT"),

  PARTICIPANT_INVITATION_EMAIL_RESEND_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Resend of invitation email failed for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_EMAIL_RESEND_FAILED"),

  PARTICIPANT_INVITATION_DISABLED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Invitation disabled for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_DISABLED"),

  CONSENT_DOCUMENT_DOWNLOADED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Participant consent document downloaded (site ID- ${site_id}, participant ID- ${participant_id}, consent version - ${consent_version}).",
      "CONSENT_DOCUMENT_DOWNLOADED"),

  INVITATION_EMAIL_SENT(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Invitation email sent to 1 or more participants (site ID- ${site_id}).",
      "INVITATION_EMAIL_SENT"),

  INVITATION_EMAIL_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Invitation email failed for 1 or more participant emails (site ID- ${site_id}).",
      "INVITATION_EMAIL_FAILED"),

  PARTICIPANT_INVITATION_ENABLED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Invitation enabled for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_ENABLED"),

  ENROLLMENT_TARGET_UPDATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Enrolment target updated for site (site ID- ${site_id}).",
      "ENROLLMENT_TARGET_UPDATED"),

  NEW_LOCATION_ADDED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "New location added (location ID- ${location_id}).",
      "NEW_LOCATION_ADDED"),

  LOCATION_EDITED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Location details edited (location ID - ${location_id}).",
      "LOCATION_EDITED"),

  LOCATION_DECOMMISSIONED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Location decommissioned (location ID - ${location_id})",
      "LOCATION_DECOMMISSIONED"),

  LOCATION_ACTIVATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Location activated (location ID- ${location_id}).",
      "LOCATION_ACTIVATED"),

  NEW_USER_CREATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "New user created (user ID - ${new_user_id}, access level - ${new_user_access_level}).",
      "NEW_USER_CREATED"),

  NEW_USER_INVITATION_EMAIL_SENT(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Account setup invitation email sent to user (user ID -${new_user_id})",
      "NEW_USER_INVITATION_EMAIL_SENT"),

  NEW_USER_INVITATION_EMAIL_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Account setup invitation email could not be sent to user (user ID -${new_user_id}).",
      "NEW_USER_INVITATION_EMAIL_FAILED"),

  USER_RECORD_UPDATED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "User record updated (user id - ${edited_user_id}, access level - ${edited_user_access_level}).",
      "USER_RECORD_UPDATED"),

  ACCOUNT_UPDATE_EMAIL_SENT(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Account update email sent to user (user id- ${edited_user_id}).",
      "ACCOUNT_UPDATE_EMAIL_SENT"),

  ACCOUNT_UPDATE_EMAIL_FAILED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Account update email could not be sent to user (user id- ${edited_user_id}).",
      "ACCOUNT_UPDATE_EMAIL_FAILED"),

  ACCOUNT_UPDATE_BY_USER(null, PARTICIPANT_DATASTORE, null, null, "ACCOUNT_UPDATE_BY_USER"),

  SITE_PARTICIPANT_REGISTRY_VIEWED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Site participant registry viewed by the user.(site ID- ${site_id})",
      "SITE_PARTICIPANT_REGISTRY_VIEWED"),

  STUDY_PARTICIPANT_REGISTRY_VIEWED(
      null, PARTICIPANT_DATASTORE, null, null, "STUDY_PARTICIPANT_REGISTRY_VIEWED"),

  APP_PARTICIPANT_REGISTRY_VIEWED(
      null, PARTICIPANT_DATASTORE, null, null, "APP_PARTICIPANT_REGISTRY_VIEWED"),

  USER_REGISTRY_VIEWED(
      null,
      PARTICIPANT_DATASTORE,
      null,
      "Participant Manager user registry viewed.",
      "USER_REGISTRY_VIEWED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private ParticipantManagerEvent(
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
