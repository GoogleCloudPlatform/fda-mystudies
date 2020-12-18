/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
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

  public static ArrayNode createArrayNode() {
    return getObjectMapper().createArrayNode();
  }

  public static String readJsonFile(String filepath) throws IOException {
    return getObjectMapper()
        .readValue(JsonUtils.class.getResourceAsStream(filepath), JsonNode.class)
        .toString();
  }

  public static String getTextValue(JsonNode node, String fieldname) {
    return node.hasNonNull(fieldname) ? node.get(fieldname).textValue() : StringUtils.EMPTY;
  }

  public static String asJsonString(Object obj) throws IOException {
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
