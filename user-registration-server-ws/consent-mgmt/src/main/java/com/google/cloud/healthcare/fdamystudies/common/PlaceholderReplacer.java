package com.google.cloud.healthcare.fdamystudies.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

public final class PlaceholderReplacer {

  private PlaceholderReplacer() {}

  private static final Logger LOG = LoggerFactory.getLogger(PlaceholderReplacer.class);

  public static String replaceNamedPlaceholders(
      final JsonNode params, final String textWithNamedPlaceholders) {
    PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper("${", "}");

    // flatten the json node
    ObjectNode aleParams = params.deepCopy();
    if (aleParams.has(AppConstants.PLACE_HOLDERS)) {
      ObjectNode placeHolders = (ObjectNode) aleParams.remove(AppConstants.PLACE_HOLDERS);
      aleParams.setAll(placeHolders);
    }

    PlaceholderResolver placeholderResolver =
        placeholderName -> {
          JsonNode valueNode = aleParams.findValue(placeholderName);
          String result = valueNode == null ? "" : aleParams.findValue(placeholderName).asText();
          if (StringUtils.isEmpty(result) && LOG.isErrorEnabled()) {
            LOG.error(
                String.format(
                    "Missing value for placeholder: '%s' in '%s'",
                    placeholderName, textWithNamedPlaceholders));
          }
          return result;
        };
    return helper.replacePlaceholders(textWithNamedPlaceholders, placeholderResolver);
  }
}
