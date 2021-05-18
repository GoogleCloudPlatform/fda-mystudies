/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.MOBILE_APPS;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.NATIVE_PUSH_NOTIFICATION_SERVER;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.PARTICIPANT_USER_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.RESPONSE_DATASTORE;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.SCIM_AUTH_SERVER;
import static com.google.cloud.healthcare.fdamystudies.common.PlatformComponent.STUDY_BUILDER;

import java.util.Optional;
import lombok.Getter;

@Getter
public enum UserMgmntEvent implements AuditLogEvent {
  ACCOUNT_REGISTRATION_REQUEST_RECEIVED(
      MOBILE_APPS, PARTICIPANT_USER_DATASTORE, null, null, "ACCOUNT_REGISTRATION_REQUEST_RECEIVED"),

  REGISTRATION_SUCCEEDED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_USER_DATASTORE,
      "New user registration succeeded.",
      "REGISTRATION_SUCCEEDED"),

  REGISTRATION_FAILED(
      null,
      SCIM_AUTH_SERVER,
      PARTICIPANT_USER_DATASTORE,
      "New user registration failed.",
      "REGISTRATION_FAILED"),

  USER_REGISTRATION_ATTEMPT_FAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Account registration request could not be processed.",
      "USER_REGISTRATION_ATTEMPT_FAILED"),

  USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Request for new account registration was denied as the username is already registered.",
      "USER_REGISTRATION_ATTEMPT_FAILED_EXISTING_USERNAME"),

  VERIFICATION_EMAIL_SENT(
      PARTICIPANT_USER_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Verification email with code sent to user for account activation.",
      "VERIFICATION_EMAIL_SENT"),

  VERIFICATION_EMAIL_FAILED(
      PARTICIPANT_USER_DATASTORE,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Verification email (for account activation) could not be sent to user.",
      "VERIFICATION_EMAIL_FAILED"),

  USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      null,
      "USER_EMAIL_VERIFIED_FOR_ACCOUNT_ACTIVATION"),

  USER_ACCOUNT_ACTIVATED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_USER_DATASTORE, null, "USER_ACCOUNT_ACTIVATED"),

  ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Email verification step for account activation failed.",
      "ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED"),

  ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Email verification for account activation failed due to invalid code.",
      "ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_WRONG_CODE"),

  ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Email verification for account activation failed due to expired code.",
      "ACCOUNT_ACTIVATION_USER_EMAIL_VERIFICATION_FAILED_EXPIRED_CODE"),

  ACCOUNT_ACTIVATION_FAILED(
      null, SCIM_AUTH_SERVER, PARTICIPANT_USER_DATASTORE, null, "ACCOUNT_ACTIVATION_FAILED"),

  VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Request received for resend of verification email.",
      "VERIFICATION_EMAIL_RESEND_REQUEST_RECEIVED"),

  USER_DELETED(
      MOBILE_APPS,
      SCIM_AUTH_SERVER,
      PARTICIPANT_USER_DATASTORE,
      "User record deactivated on Participant Datastore and deleted from Auth Server.",
      "USER_DELETED"),

  USER_DELETION_FAILED(
      MOBILE_APPS,
      SCIM_AUTH_SERVER,
      PARTICIPANT_USER_DATASTORE,
      "User record deactivation from Participant Datastore and deletion from Auth Server failed. ",
      "USER_DELETION_FAILED"),

  USER_PROFILE_UPDATED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Profile/Preferences updated by user.",
      "USER_PROFILE_UPDATED"),

  USER_PROFILE_UPDATE_FAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Profile/Preferences update by app user failed.",
      "USER_PROFILE_UPDATE_FAILED"),

  WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE(
      PARTICIPANT_USER_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Response Datastore informed about participant's study withdrawal.",
      "WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE"),

  WITHDRAWAL_INTIMATION_TO_RESPONSE_DATASTORE_FAILED(
      PARTICIPANT_USER_DATASTORE,
      RESPONSE_DATASTORE,
      null,
      "Communication failed to Response Datastore about participant's study withdrawal information.",
      "WITHDRAWAL_INTIMATION_TO_RESPONSE_DATASTORE_FAILED"),

  STUDY_METADATA_RECEIVED(
      STUDY_BUILDER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "App/Study metadata received.",
      "STUDY_METADATA_RECEIVED"),

  NOTIFICATION_METADATA_RECEIVED(
      STUDY_BUILDER,
      PARTICIPANT_USER_DATASTORE,
      null,
      "App/Study notifications metadata received.",
      "NOTIFICATION_METADATA_RECEIVED"),

  PUSH_NOTIFICATION_SENT(
      PARTICIPANT_USER_DATASTORE,
      NATIVE_PUSH_NOTIFICATION_SERVER,
      null,
      "Push Notifications successfully sent to native platforms' notifications cloud services for delivery to app users/participants.",
      "PUSH_NOTIFICATION_SENT"),

  PUSH_NOTIFICATION_FAILED(
      PARTICIPANT_USER_DATASTORE,
      NATIVE_PUSH_NOTIFICATION_SERVER,
      null,
      "Push Notifications failed to be sent to native platforms' notifications cloud service for delivery to app users/participants.",
      "PUSH_NOTIFICATION_FAILED"),

  READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "App user's profile information retrieved.",
      "READ_OPERATION_SUCCEEDED_FOR_USER_PROFILE"),

  READ_OPERATION_FAILED_FOR_USER_PROFILE(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Attempt to retrieve app user's profile information failed.",
      "READ_OPERATION_FAILED_FOR_USER_PROFILE"),

  FEEDBACK_CONTENT_EMAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Content submitted by app user via 'Feedback' form in mobile app, emailed to ${feedback_destination_email_address}.",
      "FEEDBACK_CONTENT_EMAILED"),

  FEEDBACK_CONTENT_EMAIL_FAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Content submitted by app user via 'Feedback' form in mobile app, could not be emailed to ${feedback_destination_email_address}.",
      "FEEDBACK_CONTENT_EMAIL_FAILED"),

  CONTACT_US_CONTENT_EMAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Content submitted by app user via 'Contact Us' form in mobile app, emailed to ${contactus_destination_email_address}.",
      "CONTACT_US_CONTENT_EMAILED"),

  CONTACT_US_CONTENT_EMAIL_FAILED(
      MOBILE_APPS,
      PARTICIPANT_USER_DATASTORE,
      null,
      "Content submitted by app user via 'Contact Us' form in mobile app, could not be emailed to ${contactus_destination_email_address}.",
      "CONTACT_US_CONTENT_EMAIL_FAILED");

  private final Optional<PlatformComponent> source;
  private final PlatformComponent destination;
  private final Optional<PlatformComponent> resourceServer;
  private final String description;
  private final String eventCode;

  private UserMgmntEvent(
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
