/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@RequiredArgsConstructor
@JsonSerialize(using = MessageCode.MessageCodeSerializer.class)
public enum MessageCode {
  ADD_SITE_SUCCESS(HttpStatus.CREATED, "MSG_0001", "Site added successfully"),

  ADD_LOCATION_SUCCESS(HttpStatus.CREATED, "MSG_0002", "New location added successfully"),

  GET_APPS_SUCCESS(HttpStatus.OK, "MSG_0003", "Apps fetched successfully"),

  GET_STUDIES_SUCCESS(HttpStatus.OK, "MSG_0004", "Study details fetched successfully"),

  DECOMMISSION_SITE_SUCCESS(HttpStatus.OK, "MSG_0005", "Site decommissioned successfully"),

  RECOMMISSION_SITE_SUCCESS(HttpStatus.OK, "MSG_0006", "Site activated successfully"),

  LOCATION_UPDATE_SUCCESS(HttpStatus.OK, "MSG_0007", "Location details updated successfully"),

  GET_PARTICIPANT_REGISTRY_SUCCESS(
      HttpStatus.OK, "MSG_0008", "Participant registry fetched successfully"),

  ADD_PARTICIPANT_SUCCESS(HttpStatus.CREATED, "MSG_0009", "Email added successfully"),

  GET_APPS_DETAILS_SUCCESS(HttpStatus.OK, "MSG_0010", "App details fetched successfully"),

  TARGET_ENROLLMENT_UPDATE_SUCCESS(
      HttpStatus.OK, "MSG_0011", "Target enrollment updated successfully"),

  GET_APP_PARTICIPANTS_SUCCESS(HttpStatus.OK, "MSG_0012", "App participants fetched successfully"),

  ADD_NEW_USER_SUCCESS(HttpStatus.CREATED, "MSG_0013", "New user added successfully"),

  UPDATE_USER_SUCCESS(HttpStatus.OK, "MSG_0014", "User details updated successfully"),

  GET_USER_PROFILE_SUCCESS(HttpStatus.OK, "MSG_0015", "User profile fetched successfully"),

  GET_USER_PROFILE_WITH_SECURITY_CODE_SUCCESS(
      HttpStatus.OK, "MSG_0016", "User profile with security code fetched successfully"),

  GET_PARTICIPANT_DETAILS_SUCCESS(
      HttpStatus.OK, "MSG_0017", "Participant details fetched successfully"),

  PARTICIPANTS_INVITED_SUCCESS(
      HttpStatus.OK, "MSG_0018", "Invitation to particiapant sent successfully"),

  PASSWORD_RESET_SUCCESS(HttpStatus.OK, "MSG_0019", "Your password has been reset successfully"),

  EMAIL_ACCEPTED_BY_MAIL_SERVER(
      HttpStatus.ACCEPTED, "MSG_0020", "Email accepted by receiving mail server"),

  IMPORT_PARTICIPANT_SUCCESS(HttpStatus.OK, "MSG_0021", "Email list imported successfully"),

  UPDATE_STATUS_SUCCESS(HttpStatus.OK, "MSG_0022", "Status updated successfully"),

  GET_SITES_SUCCESS(HttpStatus.OK, "MSG_0023", "Sites fetched successfully"),

  GET_CONSENT_DOCUMENT_SUCCESS(HttpStatus.OK, "MSG_0024", "Consent document fetched successfully"),

  UPDATE_USER_DETAILS_SUCCESS(HttpStatus.OK, "MSG_0025", "User details updated successfully"),

  SET_UP_ACCOUNT_SUCCESS(HttpStatus.CREATED, "MSG_0026", "New account added successfully"),

  GET_ADMIN_DETAILS_SUCCESS(HttpStatus.OK, "MSG_0027", "Admin details fetched successfully"),

  DEACTIVATE_USER_SUCCESS(HttpStatus.OK, "MSG_0028", "User deactivated successfully"),

  REACTIVATE_USER_SUCCESS(HttpStatus.OK, "MSG_0029", "User activated successfully"),

  DECOMMISSION_SUCCESS(HttpStatus.OK, "MSG_0030", "Location decommisioned successfully"),

  REACTIVE_SUCCESS(HttpStatus.OK, "MSG_0031", "Location activated successfully"),

  GET_LOCATION_SUCCESS(HttpStatus.OK, "MSG_0032", "Location fetched successfully"),

  GET_LOCATION_FOR_SITE_SUCCESS(
      HttpStatus.OK, "MSG_0033", "Locations for site fetched successfully"),

  PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "MSG_0034", "Profile updated successfully"),

  CHANGE_PASSWORD_SUCCESS(HttpStatus.OK, "MSG_0035", "Your password has been changed successfully"),

  GET_USERS_SUCCESS(HttpStatus.OK, "MSG_0036", "User details fetched successfully"),

  INVITATION_SENT_SUCCESSFULLY(
      HttpStatus.CREATED, "MSG_0037", "Invitation to user resent successfully");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;

  static class MessageCodeSerializer extends StdSerializer<MessageCode> {

    private static final long serialVersionUID = 1L;

    public MessageCodeSerializer() {
      super(MessageCode.class);
    }

    @Override
    public void serialize(
        MessageCode msgCode, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
        throws IOException {
      jsonGenerator.writeStartObject();
      jsonGenerator.writeNumberField("status", msgCode.getHttpStatus().value());
      jsonGenerator.writeStringField("code", msgCode.getCode());
      jsonGenerator.writeStringField("message", msgCode.getMessage());
      jsonGenerator.writeEndObject();
    }
  }
}
