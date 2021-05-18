/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_MANAGER;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_USER_DATASTORE;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum ParticipantManagerEvent implements AuditLogEvent {
  USER_ACCOUNT_ACTIVATED(
      PARTICIPANT_MANAGER, PARTICIPANT_USER_DATASTORE, null, null, "USER_ACCOUNT_ACTIVATED"),

  USER_ACCOUNT_ACTIVATION_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      null,
      "USER_ACCOUNT_ACTIVATION_FAILED"),

  ADMIN_DEACTIVATED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Admin user account deactivated (user ID - ${edited_user_id}).",
      "ADMIN_DEACTIVATED"),

  ADMIN_REACTIVATED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Admin user account re-activated (user ID - ${edited_user_id}).",
      "ADMIN_REACTIVATED"),

  ADMIN_DELETED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Admin user record deleted  (user ID - ${new_user_id})",
      "ADMIN_DELETED"),

  RESEND_INVITATION(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation resent to the admin (user ID - ${new_user_id}",
      "RESEND_INVITATION"),

  ADMIN_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      null,
      "ADMIN_ACCOUNT_ACTIVATION_FAILED_DUE_TO_EXPIRED_INVITATION"),

  SITE_ADDED_FOR_STUDY(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Site added to study (site ID- ${site_id}).",
      "SITE_ADDED_FOR_STUDY"),

  PARTICIPANT_EMAIL_ADDED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participant email added to site (site ID- ${site_id}).",
      "PARTICIPANT_EMAIL_ADDED"),

  PARTICIPANTS_EMAIL_LIST_IMPORTED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participants email list imported for site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORTED"),

  PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participants email list import failed for site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORT_FAILED"),

  PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "1 or more emails in list failed to get imported to site (site ID- ${site_id}).",
      "PARTICIPANTS_EMAIL_LIST_IMPORT_PARTIAL_FAILED"),

  SITE_DECOMMISSIONED_FOR_STUDY(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Site decommissioned for study (site ID- ${site_id}).",
      "SITE_DECOMMISSIONED_FOR_STUDY"),

  SITE_ACTIVATED_FOR_STUDY(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Site activated for study (site ID- ${site_id}).",
      "SITE_ACTIVATED_FOR_STUDY"),

  PARTICIPANT_INVITATION_EMAIL_RESENT(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation email re-sent to 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_EMAIL_RESENT"),

  PARTICIPANT_INVITATION_EMAIL_RESEND_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Resend of invitation email failed for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_EMAIL_RESEND_FAILED"),

  PARTICIPANT_INVITATION_DISABLED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation disabled for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_DISABLED"),

  CONSENT_DOCUMENT_DOWNLOADED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Participant consent document downloaded (site ID- ${site_id}, participant ID- ${participant_id}, consent version - ${consent_version}).",
      "CONSENT_DOCUMENT_DOWNLOADED"),

  INVITATION_EMAIL_SENT(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation email sent to 1 or more participants (site ID- ${site_id}).",
      "INVITATION_EMAIL_SENT"),

  INVITATION_EMAIL_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation email failed for 1 or more participant emails (site ID- ${site_id}).",
      "INVITATION_EMAIL_FAILED"),

  PARTICIPANT_INVITATION_ENABLED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Invitation enabled for 1 or more participants (site ID- ${site_id}).",
      "PARTICIPANT_INVITATION_ENABLED"),

  ENROLLMENT_TARGET_UPDATED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Enrolment target updated for site (site ID- ${site_id}).",
      "ENROLLMENT_TARGET_UPDATED"),

  NEW_LOCATION_ADDED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "New location added (location ID- ${location_id}).",
      "NEW_LOCATION_ADDED"),

  LOCATION_EDITED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Location details edited (location ID - ${location_id}).",
      "LOCATION_EDITED"),

  LOCATION_DECOMMISSIONED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Location decommissioned (location ID - ${location_id})",
      "LOCATION_DECOMMISSIONED"),

  LOCATION_ACTIVATED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Location activated (location ID- ${location_id}).",
      "LOCATION_ACTIVATED"),

  NEW_ADMIN_ADDED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "New admin user created (user ID - ${new_user_id}, access level - ${new_user_access_level}).",
      "NEW_ADMIN_ADDED"),

  NEW_ADMIN_INVITATION_EMAIL_SENT(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Account setup invitation email sent to admin (user ID -${new_user_id}).",
      "NEW_ADMIN_INVITATION_EMAIL_SENT"),

  NEW_ADMIN_INVITATION_EMAIL_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Account setup invitation email could not be sent to admin (user ID -${new_user_id}).",
      "NEW_ADMIN_INVITATION_EMAIL_FAILED"),

  ADMIN_USER_RECORD_UPDATED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Admin user record updated (user id - ${edited_user_id}, access level - ${edited_user_access_level}).",
      "ADMIN_USER_RECORD_UPDATED"),

  ACCOUNT_UPDATE_EMAIL_SENT(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Account update email sent to admin user (user id- ${edited_user_id}).",
      "ACCOUNT_UPDATE_EMAIL_SENT"),

  ACCOUNT_UPDATE_EMAIL_FAILED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Account update email could not be sent to admin user (user id- ${edited_user_id}).",
      "ACCOUNT_UPDATE_EMAIL_FAILED"),

  ACCOUNT_UPDATE_BY_ADMIN(
      PARTICIPANT_MANAGER, PARTICIPANT_USER_DATASTORE, null, null, "ACCOUNT_UPDATE_BY_ADMIN"),

  SITE_PARTICIPANT_REGISTRY_VIEWED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Site participant registry viewed by the admin(site ID- ${site_id})",
      "SITE_PARTICIPANT_REGISTRY_VIEWED"),

  STUDY_PARTICIPANT_REGISTRY_VIEWED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      null,
      "STUDY_PARTICIPANT_REGISTRY_VIEWED"),

  APP_PARTICIPANT_REGISTRY_VIEWED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
      null,
      null,
      "APP_PARTICIPANT_REGISTRY_VIEWED"),

  USER_REGISTRY_VIEWED(
      PARTICIPANT_MANAGER,
      PARTICIPANT_USER_DATASTORE,
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
