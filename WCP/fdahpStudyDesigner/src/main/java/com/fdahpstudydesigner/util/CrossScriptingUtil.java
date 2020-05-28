/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import java.lang.reflect.Field;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

public class CrossScriptingUtil {

  private CrossScriptingUtil() {
    super();
  }

  @SuppressWarnings("rawtypes")
  public static void replaceAll(Object obj, String... excludeProps) {
    Class klass = obj.getClass();
    while (klass != null) {
      for (Field field : klass.getDeclaredFields()) {
        if (field.getType().isAssignableFrom(String.class)
            && !ArrayUtils.contains(excludeProps, field.getName())) {
          field.setAccessible(true);
          String value = (String) ReflectionUtils.getField(field, obj);
          if (StringUtils.isNotEmpty(value)) {
            value = value.replaceAll("eval\\((.*)\\)", "");
            value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
            value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
            value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");
            value = value.replaceAll("&lt;", "<");
            value = value.replaceAll("&gt;", ">");
            value = RegExUtils.removeAll(value, "<script[\\d\\D]*?>[\\d\\D]*?</script>");
            value = RegExUtils.removeAll(value, "<script>.*$");
            ReflectionUtils.setField(field, obj, value);
          }
          field.setAccessible(false);
        } else if (field.getType().isAssignableFrom(String[].class)
            && !ArrayUtils.contains(excludeProps, field.getName())) {

          field.setAccessible(true);
          String[] stringArray = (String[]) ReflectionUtils.getField(field, obj);
          if (null != stringArray) {
            String[] temporaryArr = new String[stringArray.length];
            for (int i = 0; i < stringArray.length; i++) {
              String value = stringArray[i];
              if (StringUtils.isNotEmpty(value)) {
                value = value.replaceAll("eval\\((.*)\\)", "");
                value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
                value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
                value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", "");
                value = value.replaceAll("&lt;", "<");
                value = value.replaceAll("&gt;", ">");
                value = RegExUtils.removeAll(value, "<script[\\d\\D]*?>[\\d\\D]*?</script>");
                value = RegExUtils.removeAll(value, "<script>.*$");
              }
              temporaryArr[i] = value;
            }
            ReflectionUtils.setField(field, obj, temporaryArr);
            field.setAccessible(false);
          }
        }
      }
      klass = klass.getSuperclass();
    }
  }
}
