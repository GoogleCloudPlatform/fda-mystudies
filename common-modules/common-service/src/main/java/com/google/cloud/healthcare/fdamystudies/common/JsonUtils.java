package com.google.cloud.healthcare.fdamystudies.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.MultiValueMap;

public final class JsonUtils {

  private JsonUtils() {}

  public static ObjectMapper getObjectMapper() {
    return new ObjectMapper();
  }

  public static ObjectNode getObjectNode() {
    return getObjectMapper().createObjectNode();
  }

  public static String readJsonFile(String filepath)
      throws JsonParseException, JsonMappingException, IOException {
    return getObjectMapper()
        .readValue(JsonUtils.class.getResourceAsStream(filepath), JsonNode.class)
        .toString();
  }

  public static String getTextValue(JsonNode node, String fieldname) {
    return node.hasNonNull(fieldname) ? node.get(fieldname).textValue() : StringUtils.EMPTY;
  }

  public static String asJsonString(Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }

  public static void addTextFields(
      JsonNode src, MultiValueMap<String, String> dest, String... fieldNames) {
    for (String fieldName : fieldNames) {
      if (src.hasNonNull(fieldName)) {
        dest.add(fieldName, getTextValue(src, fieldName));
      }
    }
  }
}
