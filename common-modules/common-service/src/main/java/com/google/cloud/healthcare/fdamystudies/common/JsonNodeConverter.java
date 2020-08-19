/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class JsonNodeConverter implements AttributeConverter<JsonNode, String> {

  private XLogger logger = XLoggerFactory.getXLogger(JsonNodeConverter.class.getName());

  @Override
  public String convertToDatabaseColumn(JsonNode jsonNode) {
    if (jsonNode == null) {
      logger.warn("jsonNode input is null, returning null");
      return null;
    }

    return jsonNode.toString();
  }

  @Override
  public JsonNode convertToEntityAttribute(String jsonNodeString) {
    if (StringUtils.isEmpty(jsonNodeString)) {
      logger.warn("jsonNodeString input is empty, returning null");
      return null;
    }

    if (StringUtils.startsWith(jsonNodeString, "\"")
        && StringUtils.endsWith(jsonNodeString, "\"")) {
      jsonNodeString = jsonNodeString.substring(1, jsonNodeString.length() - 1);
    }

    jsonNodeString = StringEscapeUtils.unescapeJson(jsonNodeString);

    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readTree(jsonNodeString);
    } catch (JsonProcessingException e) {
      logger.error("error parsing jsonNodeString", e);
    }
    return null;
  }
}
