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
  ADD_SITE_SUCCESS(HttpStatus.CREATED, "MSG-0001", "Site added successfully"),

  ADD_LOCATION_SUCCESS(HttpStatus.CREATED, "MSG-0002", "New location added successfully"),

  GET_APPS_SUCCESS(HttpStatus.OK, "MSG-0003", "Get apps successfully"),

  GET_STUDIES_SUCCESS(HttpStatus.OK, "MSG-0004", "Get studies successfully"),

  DECOMMISSION_SUCCESS(HttpStatus.OK, "MSG-0003", "Decommission successfully"),

  REACTIVE_SUCCESS(HttpStatus.OK, "MSG-0004", "Reactivate successfully"),

  LOCATION_UPDATE_SUCCESS(HttpStatus.OK, "MSG-0004", "Location updated successfully"),

  GET_PARTICIPANT_REGISTRY_SUCCESS(
      HttpStatus.OK, "MSG-0008", "Get participant registry successfully"),

  ADD_PARTICIPANT_SUCCESS(HttpStatus.CREATED, "MSG-0016", "Participant added successfully"),

  GET_APPS_DETAILS_SUCCESS(HttpStatus.OK, "MSG-0018", "Get App Details successfully"),

  DECOMMISSION_SITE_SUCCESS(HttpStatus.OK, "MSG-0014", "Site Decommissioned successfully"),

  RECOMMISSION_SITE_SUCCESS(HttpStatus.OK, "MSG-0015", "Site Recommissioned successfully"),

  GET_LOCATION_SUCCESS(HttpStatus.OK, "MSG-0008", "Get locations successfully"),

  GET_LOCATION_FOR_SITE_SUCCESS(HttpStatus.OK, "MSG-0009", "Get locations for site successfully"),

  GET_APP_PARTICIPANTS_SUCCESS(HttpStatus.OK, "MSG-0005", "get App Participants successfully"),

  ADD_NEW_USER_SUCCESS(HttpStatus.CREATED, "MSG-0020", "New user added successfully"),

  UPDATE_USER_SUCCESS(HttpStatus.OK, "MSG-0021", "User updated successfully"),

  GET_USER_PROFILE_SUCCESS(HttpStatus.OK, "MSG-0022", "Get user profile successfully"),

  GET_USER_PROFILE_WITH_SECURITY_CODE_SUCCESS(
      HttpStatus.OK, "MSG-0023", "Get user profile with security code successfully"),

  GET_PARTICIPANT_DETAILS_SUCCESS(
      HttpStatus.OK, "MSG-0019", "Get participant details successfully"),

  PARTICIPANTS_INVITED_SUCCESS(HttpStatus.OK, "MSG-0016", "Participants are invited"),

  PASSWORD_RESET_SUCCESS(HttpStatus.OK, "MSG-0001", "Your password has been reset successfully!"),

  CHANGE_PASSWORD_SUCCESS(
      HttpStatus.OK, "MSG-0002", "Your password has been changed successfully!"),

  EMAIL_ACCEPTED_BY_MAIL_SERVER(
      HttpStatus.ACCEPTED, "MSG-0022", "The email is accepted by the receiving mail server."),

  IMPORT_PARTICIPANT_SUCCESS(HttpStatus.OK, "MSG-0026", "Participants imported successfully"),

  UPDATE_STATUS_SUCCESS(HttpStatus.OK, "MSG-0028", "Status updated successfully"),

  GET_SITES_SUCCESS(HttpStatus.OK, "MSG-0018", "Get sites successfully"),

  PROFILE_UPDATE_SUCCESS(HttpStatus.OK, "MSG-0011", "Profile updated successfully"),

  TARGET_ENROLLMENT_UPDATE_SUCCESS(
      HttpStatus.OK, "MSG-0030", "Target enrollment updated successfully"),

  GET_CONSENT_DOCUMENT_SUCCESS(HttpStatus.OK, "MSG-0023", "Get consent document successfully"),

  UPDATE_USER_DETAILS_SUCCESS(HttpStatus.OK, "MSG-0004", "User details successfully updated."),

  SET_UP_ACCOUNT_SUCCESS(HttpStatus.CREATED, "MSG-0005", "New account added successfully"),

  GET_ADMIN_DETAILS_SUCCESS(HttpStatus.OK, "MSG-0029", "Admin details fetched successfully"),

  DEACTIVATE_USER_SUCCESS(HttpStatus.OK, "MSG-0032", "User deactivated successfully"),

  GET_USERS_SUCCESS(HttpStatus.OK, "MSG-0033", "All users fetched successfully"),

  REACTIVATE_USER_SUCCESS(HttpStatus.OK, "MSG-0032", "User reactivated successfully");

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
