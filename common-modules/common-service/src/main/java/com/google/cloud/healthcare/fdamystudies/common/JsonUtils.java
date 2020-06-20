package com.google.cloud.healthcare.fdamystudies.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.service.BaseServiceImpl;

public final class JsonUtils {

  private static final XLogger LOGGER = XLoggerFactory.getXLogger(BaseServiceImpl.class.getName());

  private JsonUtils() {}

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }

  public static ObjectNode getObjectNode() {
    return getObjectMapper().createObjectNode();
  }

  public static ArrayNode getArrayNode() {
    return getObjectMapper().createArrayNode();
  }

  public static String getTextValue(JsonNode node, String fieldname) {
    return node.hasNonNull(fieldname) ? node.get(fieldname).textValue() : StringUtils.EMPTY;
  }

  public static ObjectNode toJson(String value) throws JsonProcessingException {
    LOGGER.entry(String.format("deserialize JSON string into ObjectNode object, value=%s", value));
    // remove first and last quotes if the value starts & ends with quotes
    if (StringUtils.startsWith(value, "\"")) {
      value = value.substring(1, value.length() - 1);
    }

    value = StringEscapeUtils.unescapeJava(value);
    LOGGER.exit(value);
    return getObjectMapper().readValue(value, ObjectNode.class);
  }

  public static void copyAll(JsonNode source, ObjectNode dest, String... excludeFields) {
    dest.setAll((ObjectNode) source);
    for (String fieldname : excludeFields) {
      dest.remove(fieldname);
    }
  }

  public static void addTextFields(
      JsonNode src, MultiValueMap<String, String> dest, String... fieldNames) {
    for (String fieldName : fieldNames) {
      if (src.hasNonNull(fieldName)) {
        dest.add(fieldName, getTextValue(src, fieldName));
      }
    }
  }

  public static void addTextFields(JsonNode src, ObjectNode dest, String... fieldNames) {
    for (String fieldName : fieldNames) {
      if (src.hasNonNull(fieldName)) {
        dest.put(fieldName, getTextValue(src, fieldName));
      }
    }
  }
}
