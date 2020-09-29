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
  STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE"),

  STUDY_ACTIVE_TASK_MARKED_COMPLETE(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Active task marked completed (activity ID - ${activetask_id}).",
      "STUDY_ACTIVE_TASK_MARKED_COMPLETE"),

  STUDY_ACTIVE_TASK_SAVED_OR_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Active task saved/updated (activity ID - ${activetask_id}).",
      "STUDY_ACTIVE_TASK_SAVED_OR_UPDATED"),

  STUDY_ACTIVE_TASK_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Active task deleted (activity ID - ${activetask_id}).",
      "STUDY_ACTIVE_TASK_DELETED"),

  STUDY_NEW_NOTIFICATION_CREATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "New notification created (notification ID - ${notification_id}).",
      "STUDY_NEW_NOTIFICATION_CREATED"),

  STUDY_NOTIFICATION_SAVED_OR_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Notification saved/updated (notification ID - ${notification_id}).",
      "STUDY_NOTIFICATION_SAVED_OR_UPDATED"),

  STUDY_NOTIFICATION_MARKED_COMPLETE(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Notification marked done/complete (notification ID - ${notification_id}).",
      "STUDY_NOTIFICATION_MARKED_COMPLETE"),

  APP_LEVEL_NOTIFICATION_LIST_VIEWED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "APP_LEVEL_NOTIFICATION_LIST_VIEWED"),

  APP_LEVEL_NOTIFICATION_CREATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "App-level notification created. Notification ID : '${notification_id}'",
      "APP_LEVEL_NOTIFICATION_CREATED"),

  APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "App-level notification replicated for resend, by user. Existing notification ID : '${old_notification_id}', new notification ID : '${new_notification_id}'.",
      "APP_LEVEL_NOTIFICATION_REPLICATED_FOR_RESEND"),

  USER_ACCOUNT_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account details updated in My Account section.",
      "USER_ACCOUNT_UPDATED"),

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

  PASSWORD_HELP_REQUESTED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_HELP_REQUESTED"),

  STUDY_QUESTIONNAIRE_SAVED_OR_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Questionnaire saved/updated (activity ID - ${questionnaire_id}).",
      "STUDY_QUESTIONNAIRE_SAVED_OR_UPDATED"),

  STUDY_QUESTIONNAIRE_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Questionnaire deleted (activity ID - ${questionnaire_id}).",
      "STUDY_QUESTIONNAIRE_DELETED"),

  STUDY_QUESTION_STEP_IN_FORM_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Question step in form deleted (activity ID - ${questionnaire_id}, form ID - ${form_id}, step ID - ${step_id}).",
      "STUDY_QUESTION_STEP_IN_FORM_DELETED"),

  STUDY_NEW_QUESTIONNAIRE_CREATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "New questionnaire created (activity ID - ${questionnaire_id}).",
      "STUDY_NEW_QUESTIONNAIRE_CREATED"),

  STUDY_FORM_STEP_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Form step deleted (activity ID - ${questionnaire_id}, step ID - ${step_id}).",
      "STUDY_FORM_STEP_DELETED"),

  STUDY_INSTRUCTION_STEP_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Instruction step deleted (activity ID - ${questionnaire_id}, step ID - ${step_id}).",
      "STUDY_INSTRUCTION_STEP_DELETED"),

  STUDY_QUESTION_STEP_DELETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Question step deleted (activity ID - ${questionnaire_id}, step ID - ${step_id}).",
      "STUDY_QUESTION_STEP_DELETED"),

  STUDY_CONSENT_SECTIONS_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_CONSENT_SECTIONS_MARKED_COMPLETE"),

  STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE"),

  STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE"),

  STUDY_RESOURCE_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_RESOURCE_SECTION_MARKED_COMPLETE"),

  NEW_STUDY_CREATION_INITIATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "NEW_STUDY_CREATION_INITIATED"),

  LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED"),

  STUDY_VIEWED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_VIEWED"),

  STUDY_ACCESSED_IN_EDIT_MODE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_ACCESSED_IN_EDIT_MODE"),

  STUDY_LAUNCHED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_LAUNCHED"),

  STUDY_PUBLISHED_AS_UPCOMING_STUDY(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_PUBLISHED_AS_UPCOMING_STUDY"),

  UPDATES_PUBLISHED_TO_STUDY(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "UPDATES_PUBLISHED_TO_STUDY"),

  STUDY_PAUSED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_PAUSED"),

  STUDY_RESUMED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_RESUMED"),

  STUDY_SETTINGS_SAVED_OR_UPDATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_SETTINGS_SAVED_OR_UPDATED"),

  USER_ACCOUNT_RE_ACTIVATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "User account activated (user ID - ${edited_user_id}).",
      "USER_ACCOUNT_RE_ACTIVATED"),

  USER_RECORD_DEACTIVATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "User account deactivated (user ID - ${edited_user_id}).",
      "USER_RECORD_DEACTIVATED");

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
