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
