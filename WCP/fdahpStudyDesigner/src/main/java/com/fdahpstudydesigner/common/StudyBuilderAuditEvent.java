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

  PASSWORD_CHANGE_SUCCEEDED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_CHANGE_SUCCEEDED"),

  PASSWORD_CHANGE_FAILED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "PASSWORD_CHANGE_FAILED"),

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

  ACCOUNT_DETAILS_VIEWED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Account details viewed in My Account section.",
      "ACCOUNT_DETAILS_VIEWED"),

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

  STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED"),

  STUDY_SETTINGS_MARKED_COMPLETE(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Settings section marked complete with enrollment setting '${enrollment_setting}', re-join setting '${rejoin_setting}' and data-retention setting '${dataretention_setting}'. ",
      "STUDY_SETTINGS_MARKED_COMPLETE"),

  STUDY_DEACTIVATED(STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_DEACTIVATED"),

  STUDY_SAVED_IN_DRAFT_STATE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_SAVED_IN_DRAFT_STATE"),

  STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_BASIC_INFO_SECTION_MARKED_COMPLETE"),

  STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_BASIC_INFO_SECTION_SAVED_OR_UPDATED"),

  STUDY_NEW_RESOURCE_CREATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "New Resource created (resource ID - ${resource_id}).",
      "STUDY_NEW_RESOURCE_CREATED"),

  STUDY_RESOURCE_SAVED_OR_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Resource saved/updated (resource ID - ${resource_id}).",
      "STUDY_RESOURCE_SAVED_OR_UPDATED"),

  STUDY_RESOURCE_MARKED_COMPLETED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Resource marked done/complete (resource ID - ${resource_id}).",
      "STUDY_RESOURCE_MARKED_COMPLETED"),

  STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED"),

  STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE"),

  STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED(
      STUDY_BUILDER, STUDY_DATASTORE, null, null, "STUDY_REVIEW_AND_E_CONSENT_SAVED_OR_UPDATED"),

  STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      "Review and E-consent section marked complete (data-sharing consent setting: '${datasharing_consent_setting}', consent document version: '${consent_document_version}')",
      "STUDY_REVIEW_AND_E_CONSENT_MARKED_COMPLETE"),

  STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED(
      STUDY_BUILDER,
      STUDY_DATASTORE,
      null,
      null,
      "STUDY_COMPREHENSION_TEST_SECTION_SAVED_OR_UPDATED");

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
