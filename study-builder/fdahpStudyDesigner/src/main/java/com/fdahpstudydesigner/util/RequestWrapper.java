/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class RequestWrapper extends HttpServletRequestWrapper {

  private static XLogger logger = XLoggerFactory.getXLogger(RequestWrapper.class.getName());

  public RequestWrapper(HttpServletRequest servletRequest) {
    super(servletRequest);
  }

  private String cleanXSS(String value) {
    // You'll need to remove the spaces from the html entities below
    logger.entry("begin cleanXSS()");
    if (value == null) {
      logger.exit("cleanXSS() ends");
      return null;
    }
    String filteredValue =
        value
            .replaceAll("eval\\((.*)\\)", "")
            .replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"")
            .replaceAll("(?i)<script.*?>.*?<script.*?>", "")
            .replaceAll("(?i)<script.*?>.*?</script.*?>", "")
            .replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", "");
    /* to skip the coverted html content from truncating */
    logger.exit("cleanXSS() ends");
    return filteredValue;
  }

  @Override
  public String getHeader(String name) {
    logger.entry("begin getHeader()");
    String value = super.getHeader(name);
    if (value == null) {
      return value;
    }
    logger.exit("getHeader() ends");
    return this.cleanXSS(value);
  }

  @Override
  public String getParameter(String parameter) {
    logger.entry("begin getParameter()");
    String value = super.getParameter(parameter);
    if (value == null) {
      return value;
    }
    logger.exit("getParameter() ends");
    return this.cleanXSS(value);
  }

  @Override
  public String[] getParameterValues(String parameter) {
    logger.entry("begin getParameterValues()");
    String[] values = super.getParameterValues(parameter);
    if (values == null) {
      return values;
    }
    int count = values.length;
    String[] encodedValues = new String[count];
    for (int i = 0; i < count; i++) {
      encodedValues[i] = this.cleanXSS(values[i]);
    }
    logger.exit("getParameterValues() ends");
    return encodedValues;
  }
}
