/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;

public final class PlaceholderReplacer {

  private PlaceholderReplacer() {}

  private static XLogger logger = XLoggerFactory.getXLogger(PlaceholderReplacer.class.getName());

  public static String replaceNamedPlaceholders(
      String textWithNamedPlaceholders, Map<String, String> values) {
    PropertyPlaceholderHelper helper;
    if (StringUtils.contains(textWithNamedPlaceholders, "${")
        && StringUtils.contains(textWithNamedPlaceholders, '}')) {
      helper = new PropertyPlaceholderHelper("${", "}");
    } else {
      helper = new PropertyPlaceholderHelper("@", "@");
    }

    PlaceholderResolver placeholderResolver =
        placeholderName -> {
          String result = values.get(placeholderName);
          if (StringUtils.isEmpty(result)) {
            logger.error(
                String.format(
                    "missing value for placeholder: '%s' in '%s'",
                    placeholderName, textWithNamedPlaceholders));
          }
          return result;
        };

    return helper.replacePlaceholders(textWithNamedPlaceholders, placeholderResolver);
  }
}
