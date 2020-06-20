/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.validator;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getArrayNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getObjectNode;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.getTextValue;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public final class AuditLogEventValidator {

  private static final String ERROR_DESCRIPTION = "error_description";

  private static final String ERROR_TYPE = "error_type";

  private static final String FILED_NAME = "fieldName";

  private static final String ERRORS = "errors";

  private static final String STATUS = "status";

  private static final String USER_ID = "user_id";

  private Schema schema;

  @PostConstruct
  public void loadSchema() {
    JSONObject jsonSchema =
        new JSONObject(
            new JSONTokener(
                AuditLogEventValidator.class.getResourceAsStream("/audit-log-event-schema.json")));
    this.schema = SchemaLoader.load(jsonSchema);
  }

  public JsonNode validateJson(JsonNode eventParams) {
    try {
      JSONObject jsonSubject = new JSONObject(eventParams.toString());
      schema.validate(jsonSubject);
    } catch (ValidationException e) {
      List<String> messages = e.getAllMessages();
      ArrayNode errors = getArrayNode();
      for (String msg : messages) {
        ObjectNode err = getObjectNode();
        err.put(FILED_NAME, extractFieldName(msg));
        err.put(ERROR_DESCRIPTION, msg);
        errors.add(err);
      }

      return buildValidationResponse(eventParams, errors);
    }

    return null;
  }

  private JsonNode buildValidationResponse(JsonNode eventParams, ArrayNode errors) {
    StringBuilder errorDescription = new StringBuilder("Audit log event validation failed");
    if (eventParams.hasNonNull(USER_ID)) {
      errorDescription.append(" for user_id=").append(getTextValue(eventParams, USER_ID));
    }
    ObjectNode result = getObjectNode();
    result.set(ERRORS, errors);
    result.put(ERROR_TYPE, HttpStatus.BAD_REQUEST.getReasonPhrase());
    result.put(STATUS, HttpStatus.BAD_REQUEST.value());
    result.put(ERROR_DESCRIPTION, errorDescription.toString());
    return result;
  }

  private String extractFieldName(String msg) {
    String fieldname;
    if (StringUtils.contains(msg, '[') && StringUtils.contains(msg, ']')) {
      fieldname = msg.substring(msg.indexOf('[') + 1, msg.indexOf(']'));
    } else if (StringUtils.contains(msg, '/') && StringUtils.contains(msg, ':')) {
      fieldname = msg.substring(msg.lastIndexOf('/') + 1, msg.indexOf(':'));
    } else {
      fieldname = msg.substring(0, msg.indexOf(':'));
    }
    return fieldname;
  }
}
