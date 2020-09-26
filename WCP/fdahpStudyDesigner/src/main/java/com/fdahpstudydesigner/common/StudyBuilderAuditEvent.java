/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

import static com.fdahpstudydesigner.common.PlatformComponent.STUDY_BUILDER;
import static com.fdahpstudydesigner.common.PlatformComponent.STUDY_DATASTORE;
import lombok.Getter;

@Getter
public enum StudyBuilderAuditEvent {
  USER_SIGNOUT_SUCCEEDED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "USER_SIGNOUT_SUCCEEDED"),

  USER_SIGNOUT_FAILED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "USER_SIGNOUT_FAILED"),

  PASSWORD_CHANGE_SUCCEEDED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_CHANGE_SUCCEEDED"),

  PASSWORD_CHANGE_FAILED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_CHANGE_FAILED"),

  PASSWORD_HELP_EMAIL_SENT(
      STUDY_DATASTORE, STUDY_DATASTORE, null, null, "PASSWORD_HELP_EMAIL_SENT"),

  PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT(
      STUDY_DATASTORE, STUDY_DATASTORE, null, null, "PASSWORD_RESET_EMAIL_SENT_FOR_LOCKED_ACCOUNT"),

  NEW_USER_ACCOUNT_ACTIVATION_FAILED_INVALID_ACCESS_CODE(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account activation failed for new user due to invalid access code (user ID - ${new_user_id}).",
      "NEW_USER_ACCOUNT_ACTIVATION_FAILED_INVALID_ACCESS_CODE"),

  ACCOUNT_DETAILS_VIEWED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account details viewed in My Account section.",
      "ACCOUNT_DETAILS_VIEWED"),

  USER_RECORD_VIEWED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "User record for user ID '${viewed_user_id}' viewed.",
      "USER_RECORD_VIEWED"),

  NEW_USER_INVITATION_RESENT(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account setup invitation email re-sent to user (user ID - ${new_user_id}).",
      "NEW_USER_INVITATION_RESENT"),

  PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Password change enforced for all users by signed-in user.",
      "PASSWORD_CHANGE_ENFORCED_FOR_ALL_USERS"),

  USER_RECORD_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "User record updated (user id - ${edited_user_id}, access level - ${edited_user_access_level}).",
      "USER_RECORD_UPDATED"),

  NEW_USER_CREATION_FAILED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "NEW_USER_CREATION_FAILED"),

  NEW_USER_CREATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "New user created (user ID - ${new_user_id}, access level - ${new_user_access_level}).",
      "NEW_USER_CREATED"),

  NEW_USER_INVITATION_EMAIL_SENT(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account setup invitation email sent to user (user ID -${new_user_id}).",
      "NEW_USER_INVITATION_EMAIL_SENT"),

  PASSWORD_HELP_EMAIL_FAILED(
      STUDY_DATASTORE, STUDY_DATASTORE, null, null, "PASSWORD_HELP_EMAIL_FAILED"),

  NEW_USER_ACCOUNT_ACTIVATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "NEW_USER_ACCOUNT_ACTIVATED"),

  PASSWORD_RESET_SUCCEEDED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_RESET_SUCCEEDED"),

  NEW_USER_ACCOUNT_ACTIVATION_FAILED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account activation failed for new user (user ID - ${new_user_id}, access level - ${new_user_access_level}).",
      "NEW_USER_ACCOUNT_ACTIVATION_FAILED"),

  PASSWORD_RESET_FAILED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_RESET_FAILED"),

  PASSWORD_CHANGE_ENFORCED_FOR_USER(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Password change enforced for user (user id - ${edited_user_id}).",
      "PASSWORD_CHANGE_ENFORCED_FOR_USER"),

  USER_ACCOUNT_UPDATED_FAILED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Attempt to update account details failed for user in My Account section.",
      "USER_ACCOUNT_UPDATED_FAILED"),

  PASSWORD_ENFORCEMENT_EMAIL_SENT(
      STUDY_DATASTORE,
      STUDY_DATASTORE,
      null,
      "Password change enforcement email sent to user ID- '${edited_user_id}'.",
      "PASSWORD_ENFORCEMENT_EMAIL_SENT"),

  PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED(
      STUDY_DATASTORE,
      STUDY_DATASTORE,
      null,
      "Password change enforcement email failed to be sent to User ID '${edited_user_id}'.",
      "PASSWORD_CHANGE_ENFORCEMENT_EMAIL_FAILED"),

  PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_SENT(
      STUDY_DATASTORE,
      STUDY_DATASTORE,
      null,
      "Password change enforcement email sent to all users barring superadmin(s).",
      "PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_SENT"),

  NEW_USER_INVITATION_EMAIL_FAILED(
      STUDY_DATASTORE,
      STUDY_DATASTORE,
      null,
      "Invitation email failed to be sent to new user (user ID - ${new_user_id}).",
      "NEW_USER_INVITATION_EMAIL_FAILED"),

  PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_FAILED(
      STUDY_DATASTORE,
      STUDY_DATASTORE,
      null,
      "Password change enforcement email failed to 1 or more users (barring superadmins).",
      "PASSWORD_CHANGE_ENFORCEMENT_FOR_ALL_USERS_EMAIL_FAILED"),

  PASSWORD_HELP_REQUESTED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_HELP_REQUESTED");

  private final PlatformComponent source;
  private final PlatformComponent destination;
  private final PlatformComponent resourceServer;
  private final String description;
  private final String eventCode;

  StudyBuilderAuditEvent(
      PlatformComponent source,
      PlatformComponent destination,
      PlatformComponent resourceServer,
      String description,
      String eventCode) {
    this.source = source;
    this.destination = destination;
    this.resourceServer = resourceServer;
    this.description = description;
    this.eventCode = eventCode;
  }
}
